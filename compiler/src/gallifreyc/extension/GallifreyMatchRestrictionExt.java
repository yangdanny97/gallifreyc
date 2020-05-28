package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.MatchBranch;
import gallifreyc.ast.MatchRestriction;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
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
    public Node aNormalize(ANormalizer rw) throws SemanticException {
        Stmt m = node().expr(rw.hoist(node().expr()));
        Stmt s = rw.addHoistedDecls(m);
        return s;
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        //TODO revisit this
        MatchRestriction m = node();
        GallifreyNodeFactory nf = rw.nodeFactory();
        Expr e = m.expr();
        Position p = m.position();
        If currentif = null;
        List<MatchBranch> branches = new ArrayList<>(m.branches());
        Collections.reverse(branches);

        for (MatchBranch b : branches) {
            LocalDecl d = b.pattern();
            RefQualifiedTypeNode t = (RefQualifiedTypeNode) d.type();
            RestrictionId restriction = ((SharedRef) t.qualification()).restriction();

            Expr field = nf.Field(p, e, nf.Id(p, rw.RES));
            Expr cond = nf.Binary(p, field, Binary.EQ, nf.StringLit(p, restriction.restriction().toString()));

            Block block = nf.Block(p, d.init(e), b.stmt());

            if (currentif != null) {
                If i = nf.If(p, cond, block, currentif);
                currentif = i;
            } else {
                If i = nf.If(p, cond, block);
                currentif = i;
            }
        }
        return currentif;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO revisit this
        MatchRestriction node = (MatchRestriction) superLang().typeCheck(node(), tc);

        GallifreyTypeSystem gts = (GallifreyTypeSystem) tc.typeSystem();
        GallifreyExprExt ext = GallifreyExprExt.ext(node.expr());
        RefQualification q = ext.gallifreyType.qualification();

        if (q instanceof SharedRef) {
            throw new SemanticException("Can only match restrictions for Shared types", node.position());
        }
        String thisRV = ((SharedRef) q).restriction().restriction().id();
        if (!gts.isRV(thisRV)) {
            throw new SemanticException("Can only match on union restrictions", node.position());
        }
        for (MatchBranch b : node.branches()) {
            LocalDecl ld = b.pattern();
            TypeNode ldt = ld.type();
            if (!(ldt instanceof RefQualifiedTypeNode)
                    || !(((RefQualifiedTypeNode) ldt).qualification() instanceof SharedRef)) {
                throw new SemanticException("Pattern in match branch must be shared type", b.position());
            }
            RefQualifiedTypeNode ldrt = (RefQualifiedTypeNode) ldt;
            RestrictionId rid = ((SharedRef) ldrt.qualification()).restriction();
            if (!rid.isRvQualified()) {
                throw new SemanticException("Match branch restriction must be qualified", b.position());
            }
            if (!rid.wildcardRv() && rid.rv().id() != thisRV) {
                throw new SemanticException("Match branch restriction qualification does not match current restriction",
                        b.position());
            }
            String variant = rid.restriction().id();
            if (!gts.getRestrictionsForRV(thisRV).contains(variant)) {
                throw new SemanticException("Variant is not part of matched union restriction", b.position());
            }
        }
        return node;
    }

}
