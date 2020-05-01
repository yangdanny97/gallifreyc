package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyType;
import polyglot.ast.NewArray;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyNewArrayExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate(); 
    
    @Override
    public NewArray node() {
    	return (NewArray) super.node();
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	NewArray node = (NewArray) superLang().typeCheck(this.node(), tc);
    	TypeNode t = node.baseType();
        if (!(t instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("array must have qualification", node.position());
        }
        RefQualification q = ((RefQualifiedTypeNode) t).qualification();
        GallifreyType initType = GallifreyExprExt.ext(node.init()).gallifreyType;
        if (!initType.qualification().equals(q) && !(initType.qualification() instanceof MoveRef)) {
        	throw new SemanticException("qualifications of array "+q+" and initializer "+ initType.qualification +" do not match");
        }
        this.gallifreyType = new GallifreyType(q);
    	return node;
    }
}
