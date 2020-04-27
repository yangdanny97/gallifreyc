package gallifreyc.extension;

import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyLocalInstance;
import polyglot.ast.LocalDecl;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class GallifreyLocalDeclExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();   
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        LocalDecl n = (LocalDecl) superLang().buildTypes(this.node, tb);
        TypeNode t = n.type();
        if (!(t instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("declaration must have qualification");
        }
        RefQualifiedTypeNode rt = (RefQualifiedTypeNode) t;
        GallifreyLocalInstance li = (GallifreyLocalInstance) n.localInstance();
        li.gallifreyType().qualification = rt.qualification();
        return n;
    }
}
