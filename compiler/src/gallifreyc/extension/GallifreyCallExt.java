package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Call;
import polyglot.ast.Node;

public class GallifreyCallExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Call node() {
    	return (Call) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	Call node = (Call) superLang().typeCheck(this.node, tc);
    	GallifreyMethodInstance mi = (GallifreyMethodInstance) node.methodInstance();
    	GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
    	//TODO check args
    	this.gallifreyType = new GallifreyType(mi.gallifreyReturnType().qualification());
        return node;
    }
}
