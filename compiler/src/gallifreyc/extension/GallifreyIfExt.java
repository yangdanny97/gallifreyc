package gallifreyc.extension;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.types.RegionContext;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

public class GallifreyIfExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public If node() {
        return (If) super.node();
    }

    @Override
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc.enter(parent, node());
        GallifreyTypeSystem ts = gtc.typeSystem();

        // visit children
        Expr cond = visitChild(node().cond(), gtc);
        ts.push_regionContext();
        Stmt consequent = visitChild(node().consequent(), gtc);
        RegionContext after_then = ts.pop_regionContext();
        ts.push_regionContext();
        Stmt alternative = visitChild(node().alternative(), gtc);
        RegionContext after_els = ts.region_context();
        // no need to pop, wlog use els's context
        Node n = node().cond(cond).consequent(consequent).alternative(alternative);

        try {
            Node result = (Node) gtc.leave(parent, node(), n, gtc);
            return result;
        } catch (InternalCompilerError e) {
            if (e.position() == null)
                e.setPosition(n.position());
            throw e;
        }
    }
}
