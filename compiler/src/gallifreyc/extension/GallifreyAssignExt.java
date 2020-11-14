package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.translate.GallifreyCodegenRewriter;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyAssignExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Assign node() {
        return (Assign) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Assign a = (Assign) superLang().typeCheck(this.node(), tc);
        GallifreyType lt = GallifreyExprExt.ext(a.left()).gallifreyType;
        GallifreyType rt = GallifreyExprExt.ext(a.right()).gallifreyType;
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();

        if (!ts.checkQualifications(rt, lt)) {
            throw new SemanticException("cannot assign " + rt.qualification + " to " + lt.qualification,
                    node().position());
        }

        this.gallifreyType = new GallifreyType(lt);
        return a;
    }

    @Override
    public Node gallifreyRewrite(GallifreyCodegenRewriter rw) throws SemanticException {
        // rewrite RHS of assignments
        GallifreyNodeFactory nf = rw.nodeFactory();
        GallifreyTypeSystem ts = rw.typeSystem();
        Assign a = node();
        Expr lhs = a.left();
        Expr rhs = a.right();
        GallifreyExprExt.ext(a);
        RefQualification q = GallifreyExprExt.ext(lhs).gallifreyType.qualification;

        // shared[R] C x = e ----> R x = new R(e);
        if (q.isShared()) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            a = a.right(nf.Cast(rhs.position(), nf.TypeNode(Position.COMPILER_GENERATED, rid.getWrapperName()),
                    rw.rewriteRHS(rid, rhs)));
        } else if (rhs != null && rhs.type() != null && lhs.type() != null && (!ts.typeEquals(rhs.type(), lhs.type()))
                && ts.isCastValid(rhs.type(), lhs.type())) {
            a = a.right(nf.Cast(rhs.position(), nf.CanonicalTypeNode(Position.COMPILER_GENERATED, lhs.type()), rhs));
        }
        return a;
    }
}
