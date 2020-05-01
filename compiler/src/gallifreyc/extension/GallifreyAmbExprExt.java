package gallifreyc.extension;

import polyglot.ast.AmbExpr;
import polyglot.util.SerialVersionUID;

public class GallifreyAmbExprExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public AmbExpr node() {
        return (AmbExpr) super.node();
    }
}
