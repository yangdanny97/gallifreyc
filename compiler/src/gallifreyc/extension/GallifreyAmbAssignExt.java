package gallifreyc.extension;

import polyglot.ast.AmbAssign;
import polyglot.util.SerialVersionUID;

public class GallifreyAmbAssignExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();  
    
    @Override
    public AmbAssign node() {
    	return (AmbAssign) super.node();
    }
}
