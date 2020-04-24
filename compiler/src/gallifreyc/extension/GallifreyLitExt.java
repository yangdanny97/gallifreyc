package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyLitExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	Expr node = (Expr) node();
    	this.gallifreyType = new GallifreyType(new MoveRef(node.position()));
        return node().typeCheck(tc);
    }
}
