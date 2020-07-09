package gallifreyc.extension;

import java.util.ArrayList;
import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.Transition;
import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class GallifreyTransitionExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Transition node() {
        return (Transition) super.node();
    }

    public GallifreyTransitionExt() {
    }

    @Override
    public Node aNormalize(ANormalizer rw) throws SemanticException {
        Transition t = node().expr(rw.hoist(node().expr()));
        Stmt s = rw.addHoistedDecls(t);
        return s;
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        Transition t = node();
        GallifreyNodeFactory nf = rw.nodeFactory();
        Position p = t.position();
        // transition(c, R) ------> c.transition(R_impl.class)
        ArrayList<Expr> args = new ArrayList<>();
        args.add(rw.qq().parseExpr(t.restriction().restriction().id() + "_impl.class"));
        return nf.Eval(nf.Call((Local) t.expr().copy(), "transition", args));
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        Transition node = (Transition) superLang().buildTypes(node(), tb);
        return node;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Transition node = (Transition) superLang().typeCheck(node(), tc);
        if (!(node.expr() instanceof Local)) {
            throw new SemanticException("Transition is only supported for variables");
        }
        GallifreyExprExt ext = GallifreyExprExt.ext(node.expr());
        GallifreyTypeSystem gts = (GallifreyTypeSystem) tc.typeSystem();
        Type t = node.expr().type();

        RefQualification q = ext.gallifreyType.qualification();

        if (!(q.isShared())) {
            throw new SemanticException("Can only transition restrictions for Shared types", node.position());
        }

        String restrictionClass = gts.getClassNameForRestriction(node.restriction().restriction().id());
        if (restrictionClass == null) {
            throw new SemanticException("Unknown Restriction " + node.restriction().restriction().id(),
                    node.position());
        }
        // check RV's match
        RestrictionId exprId = ((SharedRef) q).restriction;
        RestrictionId transitionId = node.restriction();
        // expr must have either RV or RV::R qualification
        if (exprId.rv() == null && !gts.isRV(exprId.restriction().id())) {
            throw new SemanticException("Cannot transition " + exprId + " to " + transitionId, node().position());
        }
        if (transitionId.rv() == null) {
            throw new SemanticException("Cannot transition to unqualified restriction " + transitionId,
                    node().position());
        }
        if (exprId.rv() == null) { // transition(RV, RV::R)
            if (!exprId.restriction().id().equals(transitionId.rv().id())) {
                throw new SemanticException("Cannot transition " + exprId + " to " + transitionId, node().position());
            }
        } else { // transition(RV::R, RV::R)
            if (!exprId.restriction().id().equals(transitionId.rv().id())) {
                throw new SemanticException("Cannot transition " + exprId + " to " + transitionId, node().position());
            }
        }

        // requires equality between expr type and restriction's "for" type
        if (!gts.typeEquals(t, gts.typeForName(restrictionClass))) {
            throw new SemanticException("Invalid restriction for class " + restrictionClass, node.position());
        }
        return node;
    }

}
