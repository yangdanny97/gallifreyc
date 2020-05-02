package gallifreyc.extension;

import polyglot.util.SerialVersionUID;
import polyglot.ast.LocalAssign;

public class GallifreyLocalAssignExt extends GallifreyAssignExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public LocalAssign node() {
        return (LocalAssign) super.node();
    }
}
