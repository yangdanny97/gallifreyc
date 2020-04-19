package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import polyglot.ast.Node;

public class GallifreyNewExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return node().typeCheck(tc);
    }
    
}
