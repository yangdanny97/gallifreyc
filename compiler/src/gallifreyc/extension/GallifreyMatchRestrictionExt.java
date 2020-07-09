package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.MatchBranch;
import gallifreyc.ast.MatchRestriction;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Catch;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class GallifreyMatchRestrictionExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyMatchRestrictionExt() {
    }

    @Override
    public MatchRestriction node() {
        return (MatchRestriction) super.node();
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        return superLang().buildTypesEnter(node(), tb);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        return superLang().buildTypes(node(), tb);
    }

    @Override
    public Node aNormalize(ANormalizer rw) throws SemanticException {
        Stmt m = node().expr(rw.hoist(node().expr()));
        Stmt s = rw.addHoistedDecls(m);
        return s;
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        MatchRestriction m = node();
        GallifreyNodeFactory nf = rw.nodeFactory();
        
        // e is guaranteed to be a variable
        Expr e = m.expr();
        Position p_ = Position.COMPILER_GENERATED;
        List<Stmt> matchStmts = new ArrayList<>();
        
        // match lock resource
        List<LocalDecl> resources  = new ArrayList<>();
        resources.add(nf.LocalDecl(p_, Flags.NONE, nf.TypeNode("MatchLocked"), nf.Id("ml"), 
                rw.qq().parseExpr("%E.sharedObj().get_current_restriction_lock(%E.holder.getClass().getName())", 
                        e.copy(), e.copy())
                ));

        // reconstruct the holder based on the match lock restriction
        Expr restriction_name = nf.Call(p_, nf.Local("ml"), nf.Id("get_restriction_name"), new ArrayList<Expr>());
        matchStmts.add(nf.LocalDecl(p_, Flags.NONE, nf.TypeNode("String"), nf.Id("rname"), restriction_name));
        Expr classLoader = nf.Call(p_, nf.Call(p_, (Expr) e.copy(), nf.Id("getClass"), new ArrayList<Expr>()), 
                nf.Id("getClassLoader"), new ArrayList<Expr>());
        List<Expr> forNameArgs = new ArrayList<Expr>();
        forNameArgs.add(nf.Local("rname"));
        forNameArgs.add(nf.BooleanLit(p_, true));
        forNameArgs.add(classLoader);
        Expr getClass = nf.Call(p_, nf.TypeNode("Class"), nf.Id("forName"), forNameArgs);
        matchStmts.add(nf.LocalDecl(p_, Flags.NONE, nf.TypeNode("Class"), nf.Id("cls"), getClass));

        Expr constructor = nf.Call(p_, nf.Local("cls"), nf.Id("getConstructor"),
                rw.qq().parseExpr("SharedObject.class"));
        Expr newInstance = nf.Call(p_, constructor, nf.Id("newInstance"),
                rw.qq().parseExpr("new Object[] {%E.sharedObj()}", 
                        nf.Field((Expr) e.copy(), rw.HOLDER)));
        String rvName = ((SharedRef) ((GallifreyLocalDeclExt) GallifreyExt.ext(m.branches().get(0).pattern()))
                .qualification()).restriction().rv().id();
        Stmt assign = nf.Eval(p_, nf.Assign(p_, nf.Field((Expr) e.copy(), rw.HOLDER), Assign.ASSIGN,
                nf.Cast(p_, nf.TypeNode(rvName + "_holder"), newInstance)));
        matchStmts.add(assign);
        
        // increment lock
        matchStmts.add(nf.Eval(p_, nf.Assign(p_, nf.Field(p_, (Expr) e.copy(), nf.Id(p_, rw.LOCK)), Assign.ADD_ASSIGN,
                nf.IntLit(p_, IntLit.INT, 1))));

        If currentif = null;
        List<MatchBranch> branches = new ArrayList<>(m.branches());
        Collections.reverse(branches);

        // fold over branches to build nested If
        for (MatchBranch b : branches) {
            // shared[RV::restriction]
            Position p = b.position();
            LocalDecl d = b.pattern();
            GallifreyLocalDeclExt dExt = (GallifreyLocalDeclExt) GallifreyExt.ext(d);
            RestrictionId rid = ((SharedRef) dExt.qualification()).restriction();
            String rv = rid.rv().id();
            String restriction = rid.restriction().id();
            // local decl is RV_R_impl type
            d = d.type(nf.TypeNode(rv + "_" + restriction + "_impl"));
            // add final flag
            d = d.flags(d.flags().Final());

            // x.holder instanceof RV_restriction
            Expr cond = nf.Instanceof(p, nf.Field(p, (Expr) e.copy(), nf.Id(rw.HOLDER)),
                    nf.TypeNode(rv + "_" + restriction));

            List<Stmt> blockStmts = new ArrayList<>();
            List<Expr> args = new ArrayList<>();
            args.add(e);
            blockStmts.add(d.init(nf.New(p, nf.TypeNode(rv + "_" + restriction + "_impl"), args)));
            blockStmts.addAll(b.body().statements());

            // d.holder = null;
            blockStmts.add(nf.Eval(p, nf.Assign(p, nf.Field(p, nf.Local(p, (Id) d.id().copy()), nf.Id(rw.HOLDER)),
                    Assign.ASSIGN, nf.NullLit(p))));
            Block block = nf.Block(p, blockStmts);

            if (currentif != null) {
                currentif = nf.If(p, cond, block, currentif);
            } else {
                currentif = nf.If(p, cond, block);
            }
        }
        matchStmts.add(currentif);
        // decrement lock
        matchStmts.add(nf.Eval(p_, nf.Assign(p_, nf.Field(p_, (Expr) e.copy(), nf.Id(p_, rw.LOCK)), Assign.SUB_ASSIGN,
                nf.IntLit(p_, IntLit.INT, 1))));
        
        List<Catch> catchBlocks = new ArrayList<>();
        catchBlocks.add(nf.Catch(p_, nf.Formal("Exception", "e"), nf.Block(p_, new ArrayList<Stmt>())));
         
        return nf.TryWithResources(p_, resources, nf.Block(p_, matchStmts), 
                catchBlocks, nf.Block(p_, new ArrayList<Stmt>()));
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        MatchRestriction node = (MatchRestriction) superLang().typeCheck(node(), tc);

        GallifreyTypeSystem gts = (GallifreyTypeSystem) tc.typeSystem();
        GallifreyExprExt ext = GallifreyExprExt.ext(node.expr());
        RefQualification q = ext.gallifreyType.qualification();

        if (!(q.isShared())) {
            throw new SemanticException("Can only match restrictions for Shared types", node.position());
        }
        String thisRV = ((SharedRef) q).restriction().restriction().id();
        if (!gts.isRV(thisRV)) {
            throw new SemanticException("Can only match on RVs", node.position());
        }
        for (MatchBranch b : node.branches()) {
            LocalDecl ld = b.pattern();
            GallifreyLocalDeclExt localExt = (GallifreyLocalDeclExt) GallifreyExt.ext(ld);
            RefQualification localQ = localExt.qualification;
            if (!(q.isShared())) {
                throw new SemanticException("Pattern in match branch must be Shared type", ld.position());
            }
            SharedRef sharedQ = (SharedRef) localQ;
            RestrictionId rid = sharedQ.restriction();
            if (!rid.isRvQualified()) {
                throw new SemanticException("Match branch restriction must be qualified", ld.position());
            }
            if (!rid.wildcardRv() && !rid.rv().id().equals(thisRV)) {
                throw new SemanticException("Match branch restriction qualification (" + rid.rv()
                        + ") does not match current restriction (" + thisRV + ")", ld.position());
            }
            if (!rid.wildcardRv()) {
                String variant = rid.restriction().id();
                if (!gts.getRestrictionsForRV(thisRV).contains(variant)) {
                    throw new SemanticException("Variant " + variant + " is not part of matched RV " + thisRV,
                            ld.position());
                }
            } else {
                // fill in RV for wild cards
                GallifreyNodeFactory nf = (GallifreyNodeFactory) tc.nodeFactory();
                sharedQ.restriction = nf.RestrictionId(Position.COMPILER_GENERATED,
                        nf.Id(Position.COMPILER_GENERATED, thisRV), rid.restriction(), false);
            }
        }
        return node;
    }

}
