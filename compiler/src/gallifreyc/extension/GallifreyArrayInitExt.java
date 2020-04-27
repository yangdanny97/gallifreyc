package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

import java.util.List;

import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.types.GallifreyType;
import polyglot.ast.ArrayInit;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyArrayInitExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        ArrayInit node = (ArrayInit) superLang().typeCheck(this.node(), tc);
        List<Expr> elements = node.elements();
        //init expressions need to be all the same OR moves
        RefQualification acc = null;
        for (Expr e : elements) {
        	RefQualification eq = lang().exprExt(e).gallifreyType.qualification();
        	if (acc == null) {
        		acc = eq;
        	} else if (acc instanceof MoveRef) {
        		acc = eq;
        	} else if (eq instanceof MoveRef) {}
        	else if (!acc.equals(eq)) {
        		throw new SemanticException("qualifications in array initialization must all match",e.position());
        	}
        }
        if (acc == null) acc = new MoveRef(node.position());
        this.gallifreyType = new GallifreyType(acc);
        return node;
    }
}
