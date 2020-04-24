package gallifreyc.extension;

import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyLocalInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Local;
import polyglot.ast.Node;

public class GallifreyLocalExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();    
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
    	Local node = (Local) node();
        Context c = tc.context();
        GallifreyLocalInstance li = (GallifreyLocalInstance) c.findLocal(node.name());
        this.gallifreyType = new GallifreyType(li.gallifreyType().qualification);
        return node().typeCheck(tc);
    }
}
