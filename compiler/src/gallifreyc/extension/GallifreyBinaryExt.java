package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyBinaryExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();  
    
    @Override
    public Binary node() {
    	return (Binary) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	Expr node = (Expr) superLang().typeCheck(this.node(), tc);
    	this.gallifreyType = new GallifreyType(new LocalRef(node.position()));
        return node;
    }
}
