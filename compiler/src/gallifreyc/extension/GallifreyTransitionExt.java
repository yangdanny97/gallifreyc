package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Arrays;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.Transition;
import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
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
        // transition(c, R) ------> c = new R(c.SHARED);
        Assign fa = nf.Assign(p, (Local) t.expr().copy(), Assign.ASSIGN,
                nf.New(p, nf.TypeNodeFromQualifiedName(p, t.restriction().restriction().id()),
                        new ArrayList<Expr>(Arrays.asList(nf.Field(p, (Local) t.expr().copy(), nf.Id(p, rw.SHARED))))));
        return nf.Eval(p, fa);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO revisit this
        Transition node = (Transition) superLang().typeCheck(node(), tc);
        GallifreyExprExt ext = GallifreyExprExt.ext(node.expr());
        GallifreyTypeSystem gts = (GallifreyTypeSystem) tc.typeSystem();
        Type t = node.expr().type();

        RefQualification q = ext.gallifreyType.qualification();

        if (q instanceof SharedRef) {
            throw new SemanticException("Can only transition restrictions for Shared types", node.position());
        }

        String restrictionClass = gts.getClassNameForRestriction(node.restriction().restriction().id());
        if (restrictionClass == null) {
            throw new SemanticException("Unknown Restriction " + node.restriction().restriction().id(),
                    node.position());
        }

        // requires equality between expr type and restriction's "for" type
        if (!gts.typeEquals(t, gts.typeForName(restrictionClass))) {
            throw new SemanticException("Invalid restriction for class " + restrictionClass, node.position());
        }
        return node;
    }

}
