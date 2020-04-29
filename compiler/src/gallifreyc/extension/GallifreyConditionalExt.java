package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Conditional;
import polyglot.ast.Node;

public class GallifreyConditionalExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();  
    
    @Override
    public Conditional node() {
    	return (Conditional) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	Conditional c = (Conditional) superLang().typeCheck(this.node(), tc);
    	GallifreyType trueType = GallifreyExprExt.ext(c.consequent()).gallifreyType;
    	GallifreyType falseType = GallifreyExprExt.ext(c.alternative()).gallifreyType;
    	if (!trueType.qualification().equals(falseType.qualification())) {
    		throw new SemanticException("branches of ternary must have same qualification", c.position());
    	}
    	this.gallifreyType = new GallifreyType(trueType.qualification());
        return c;
    }
}
