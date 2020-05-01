package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyLocalInstance;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyFormalExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate(); 
    
    @Override
    public Formal node() {
    	return (Formal) super.node();
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        Formal n = (Formal) superLang().buildTypes(this.node, tb);
        TypeNode t = n.type();
        if (!(t instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("declaration must have qualification", n.position());
        }
        RefQualifiedTypeNode rt = (RefQualifiedTypeNode) t;
        GallifreyLocalInstance li = (GallifreyLocalInstance) n.localInstance();
        li.gallifreyType().qualification = rt.qualification();
        return n;
    }
}

