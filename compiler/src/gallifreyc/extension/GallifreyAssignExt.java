package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Assign;
import polyglot.ast.Node;

public class GallifreyAssignExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	Assign a = (Assign) node().typeCheck(tc);
    	GallifreyType lt = lang().exprExt(a.left()).gallifreyType;
    	GallifreyType rt = lang().exprExt(a.right()).gallifreyType;
    	
    	//TODO shared
    	
    	if (rt.qualification instanceof MoveRef) {}
    	else if (lt.qualification instanceof LocalRef && rt.qualification instanceof LocalRef) {}
    	else {
    		throw new SemanticException("cannot assign " + rt.qualification + " to " + lt.qualification, node().position());
    	}
    	
    	this.gallifreyType = new GallifreyType(lt.qualification);
        return a;
    }
}
