package gallifreyc.extension;

import polyglot.ast.Instanceof;
import polyglot.util.SerialVersionUID;

public class GallifreyInstanceofExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public Instanceof node() {
    	return (Instanceof) super.node();
    }
}
