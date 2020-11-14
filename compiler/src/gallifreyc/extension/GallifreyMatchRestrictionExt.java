package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gallifreyc.ast.*;
import gallifreyc.translate.*;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.*;
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
    public Node gallifreyRewrite(GallifreyCodegenRewriter rw) throws SemanticException {
        MatchRestriction m = node();
        GallifreyNodeFactory nf = rw.nodeFactory();

        // e is guaranteed to be a variable b/c of a-normalization pass
        Local e = (Local) m.expr();
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        Position p = Position.COMPILER_GENERATED;
        List<Stmt> stmts = new ArrayList<>();
        // increment lock
        stmts.add(
                nf.Eval(nf.Assign(nf.Field((Expr) e.copy(), rw.LOCK), Assign.ADD_ASSIGN, nf.IntLit(p, IntLit.INT, 1))));

        // make temp to hold ptr for lock decrementing
        String temp = rw.lang().freshVar();
        LocalDecl tempDecl = nf.LocalDecl(p, Flags.NONE, nf.CanonicalTypeNode(p, e.type()), nf.Id(temp),
                (Expr) e.copy());
        tempDecl = tempDecl.localInstance(((Local) e).localInstance());
        GallifreyLocalDeclExt tempExt = (GallifreyLocalDeclExt) GallifreyExt.ext(tempDecl);
        tempExt.qualification = ext.gallifreyType.qualification;
        tempDecl = (LocalDecl) tempExt.gallifreyRewrite(rw);
        stmts.add(tempDecl);

        List<Stmt> matchStmts = new ArrayList<>();

        // match lock resource
        List<LocalDecl> resources = new ArrayList<>();
        resources.add(nf.LocalDecl("MatchLocked", "ml", rw.qq().parseExpr(
                "%E.sharedObj().get_current_restriction_lock(%E.holder.getClass().getName())", e.copy(), e.copy())));

        // reconstruct the holder based on the match lock restriction
        List<Stmt> innerTryStmts = new ArrayList<>();
        Expr restriction_name = rw.qq().parseExpr("ml.get_restriction_name()");
        matchStmts.add(nf.LocalDecl("String", "rname", restriction_name));
        Expr classLoader = rw.qq().parseExpr("%E.getClass().getClassLoader()", (Expr) e.copy());
        List<Expr> forNameArgs = new ArrayList<Expr>();
        forNameArgs.add(nf.Local("rname"));
        forNameArgs.add(nf.BooleanLit(p, true));
        forNameArgs.add(classLoader);
        Expr getClass = nf.Call(nf.TypeNode("Class"), "forName", forNameArgs);
        innerTryStmts.add(nf.LocalDecl(p, Flags.NONE, nf.TypeNode("Class<?>"), nf.Id("cls"), getClass));

        Expr constructor = rw.qq().parseExpr("cls.getConstructor(SharedObject.class)");
        Expr newInstance = nf.Call(constructor, "newInstance",
                rw.qq().parseExpr("new Object[] {%E.sharedObj()}", nf.Field((Expr) e.copy(), rw.HOLDER)));
        String rvName = ((SharedRef) ((GallifreyLocalDeclExt) GallifreyExt.ext(m.branches().get(0).pattern()))
                .qualification()).restriction().rv().id();
        Stmt assign = nf.Eval(nf.Assign(nf.Field((Expr) e.copy(), rw.HOLDER), Assign.ASSIGN,
                nf.Cast(nf.TypeNode(rvName + "_holder"), newInstance)));
        innerTryStmts.add(assign);

        List<Catch> catches = new ArrayList<>();
        String[] exnNames = new String[] { "InstantiationException", "IllegalAccessException", "ClassNotFoundException",
                "NoSuchMethodException", "InvocationTargetException" };
        for (int i = 0; i < exnNames.length; i++) {
            List<Expr> args = new ArrayList<>();
            args.add(nf.Local("e"));
            catches.add(nf.Catch(p, nf.Formal(exnNames[i], "e"),
                    nf.Block(nf.Throw(p, nf.New(p, nf.TypeNode("InternalGallifreyException"), args)))));
        }

        matchStmts.add(nf.Try(p, nf.Block(innerTryStmts), catches));

        If currentif = null;
        List<MatchBranch> branches = new ArrayList<>(m.branches());
        Collections.reverse(branches);

        // fold over branches to build nested If
        for (MatchBranch b : branches) {
            // shared[RV::restriction]
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
            Expr cond = nf.Instanceof(p, nf.Field((Expr) e.copy(), rw.HOLDER), nf.TypeNode(rv + "_" + restriction));

            List<Stmt> blockStmts = new ArrayList<>();
            List<Expr> args = new ArrayList<>();
            args.add(e);
            blockStmts.add(d.init(nf.New(p, nf.TypeNode(rv + "_" + restriction + "_impl"), args)));

            List<Stmt> finallyBlock = new ArrayList<>();
            // decrement lock, null out holder
            finallyBlock.add(nf.Eval(nf.Assign(nf.Field(nf.Local(d.name()), rw.HOLDER), Assign.ASSIGN, nf.NullLit(p))));
            blockStmts.add(nf.Try(p, nf.Block(b.body().statements()), new ArrayList<Catch>(), nf.Block(finallyBlock)));

            Block block = nf.Block(blockStmts);

            if (currentif != null) {
                currentif = nf.If(p, cond, block, currentif);
            } else {
                currentif = nf.If(p, cond, block);
            }
        }
        matchStmts.add(currentif);

        List<Stmt> finallyBlock = new ArrayList<>();
        // decrement lock
        finallyBlock.add(
                nf.Eval(nf.Assign(nf.Field(nf.Local(temp), rw.LOCK), Assign.SUB_ASSIGN, nf.IntLit(p, IntLit.INT, 1))));

        stmts.add(nf.TryWithResources(p, resources, nf.Block(matchStmts), new ArrayList<Catch>(),
                nf.Block(finallyBlock)));

        return nf.Block(stmts);
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
