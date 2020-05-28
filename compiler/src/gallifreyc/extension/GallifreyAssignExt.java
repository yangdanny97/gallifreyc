package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

import java.util.ArrayList;
import java.util.Arrays;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UniqueRef;
import gallifreyc.translate.GallifreyRewriter;
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
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        // rewrite RHS of assignments
        GallifreyNodeFactory nf = rw.nodeFactory();
        Assign a = node();
        Expr lhs = a.left();
        Expr rhs = a.right();
        GallifreyType gt = GallifreyExprExt.ext(a).gallifreyType;
        RefQualification q = GallifreyExprExt.ext(lhs).gallifreyType.qualification;

        // shared[R] C x = e ----> R x = new R(e);
        if (q instanceof SharedRef) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            Expr new_rhs = rw.qq().parseExpr("new " + rid.toString() + "_impl(%E)", rhs);
            a = a.right(new_rhs);
        } else if (q instanceof UniqueRef) {
            Expr new_rhs = nf.New(rhs.position(), nf.TypeNodeFromQualifiedName(a.position(), "Unique<>"),
                    new ArrayList<Expr>(Arrays.asList(rhs)));
            a = a.right(new_rhs);
        }
        return a;
    }
}
