package gallifreyc.extension;

import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Switch;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

public class GallifreySwitchExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Switch node() {
        return (Switch) super.node();
    }

    @Override
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc.enter(parent, node());
        // visit children
        Expr expr = visitChild(node().expr(), gtc);
        throw new UnsupportedOperationException("no switch for now");
        /*
         * ts.regionMapEnter(); //Switch statements are actually pretty complicated
         * here, because of fallthrough. I would prefer not to support them at all.
         * List<SwitchElement> elements = visitList(node().elements(), gtc);
         * ts.regionMapLeave(); Node n = node().expr(expr).elements(elements);
         * 
         * try { Node result = (Node) gtc.leave(parent, node(), n, gtc); return result;
         * } catch (InternalCompilerError e) { if (e.position() == null)
         * e.setPosition(n.position()); throw e; }
         */
    }
}
