package gallifreyc.extension;

import java.util.List;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

public class GallifreyForExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public For node() {
        return (For) super.node();
    }

    @Override
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc.enter(parent, node());
        GallifreyTypeSystem ts = gtc.typeSystem();

        // visit children
        List<ForInit> inits = visitList(node().inits(), gtc);
        Expr cond = visitChild(node().cond(), gtc);
        List<ForUpdate> iters = visitList(node().iters(), gtc);
        ts.region_context();
        ts.push_regionContext();
        Stmt body = visitChild(node().body(), gtc);
        // no need to pop
        Node n = node().inits(inits).cond(cond).iters(iters).body(body);

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
