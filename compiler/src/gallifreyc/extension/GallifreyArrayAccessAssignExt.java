package gallifreyc.extension;

import polyglot.ast.ArrayAccessAssign;
import polyglot.util.SerialVersionUID;

public class GallifreyArrayAccessAssignExt extends GallifreyAssignExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public ArrayAccessAssign node() {
        return (ArrayAccessAssign) super.node();
    }
}
