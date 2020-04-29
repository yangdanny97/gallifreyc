package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.LocalRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Special;

public class GallifreySpecialExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();  
    
    @Override
    public Special node() {
    	return (Special) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	//this & super are always local
    	Expr node = node();
    	this.gallifreyType = new GallifreyType(new LocalRef(node.position()));
        return superLang().typeCheck(this.node(), tc);
    }
}