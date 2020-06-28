package gallifreyc.extension;

import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import gallifreyc.ast.WhenStmt;
import gallifreyc.translate.GallifreyRewriter;

public class GallifreyWhenStmtExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    // NO a-normalization of condition expr
    
    @Override
    public WhenStmt node() {
        return (WhenStmt) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        // TODO Auto-generated method stub
        return super.gallifreyRewrite(rw);
    }
}
