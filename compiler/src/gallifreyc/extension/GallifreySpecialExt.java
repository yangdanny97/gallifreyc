package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreySpecialExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	//this & super are always local
    	Expr node = (Expr) node();
    	this.gallifreyType = new GallifreyType(new LocalRef(node.position()));
        return superLang().typeCheck(this.node(), tc);
    }
}
