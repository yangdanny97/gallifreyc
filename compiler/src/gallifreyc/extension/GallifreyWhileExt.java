package gallifreyc.extension;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.While;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

public class GallifreyWhileExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public While node() {
        return (While) super.node();
    }
    
    @Override
    public Node typeCheckOverride(Node parent, TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc.enter(parent, node());
        GallifreyTypeSystem ts = gtc.typeSystem();
        
        // visit children
        Expr cond = visitChild(node().cond(), gtc);
        ts.regionMapEnter();
        Stmt body = visitChild(node().body(), gtc);
        ts.regionMapLeave();
        Node n = node().cond(cond).body(body);

        try {
            Node result = (Node) gtc.leave(parent, node(), n, gtc);
            return result;
        }
        catch (InternalCompilerError e) {
            if (e.position() == null) e.setPosition(n.position());
            throw e;
        }
    }  
}
