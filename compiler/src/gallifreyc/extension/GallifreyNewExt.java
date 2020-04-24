package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;

public class GallifreyNewExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	New node = (New) node().typeCheck(tc);
    	//TODO check arguments, just like function calls
    	this.gallifreyType = new GallifreyType(new MoveRef(node.position()));
        return node;
    }
}
