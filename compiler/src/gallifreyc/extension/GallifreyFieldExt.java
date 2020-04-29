package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import polyglot.ast.Field;
import polyglot.ast.Node;

public class GallifreyFieldExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();   
    
    @Override
    public Field node() {
    	return (Field) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	//TODO
        return node().typeCheck(tc);
    }
}
