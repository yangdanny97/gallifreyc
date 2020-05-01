package gallifreyc.extension;

import polyglot.ast.FieldAssign;
import polyglot.util.SerialVersionUID;

public class GallifreyFieldAssignExt extends GallifreyAssignExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public FieldAssign node() {
    	return (FieldAssign) super.node();
    }
}
