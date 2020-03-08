package gallifreyc.ast;

import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyExprExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public Expr node() {
        return (Expr) super.node();
    }
}
