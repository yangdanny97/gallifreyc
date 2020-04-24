package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;

public class GallifreyBinaryExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();  
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	// assuming results of binary are always const
    	Expr node = (Expr) node().typeCheck(tc);
    	this.gallifreyType = new GallifreyType(new MoveRef(node.position()));
        return node;
    }
}
