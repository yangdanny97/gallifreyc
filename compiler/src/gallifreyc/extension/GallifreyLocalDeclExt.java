package gallifreyc.extension;

import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyLocalInstance;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;

public class GallifreyLocalDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate(); 
    
    public RefQualification qualification;
    
    @Override
    public LocalDecl node() {
    	return (LocalDecl) super.node();
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        LocalDecl n = (LocalDecl) superLang().buildTypes(this.node, tb);
        TypeNode t = n.type();
        if (!(t instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("declaration must have qualification");
        }
        RefQualification q = ((RefQualifiedTypeNode) t).qualification();
        GallifreyLocalInstance li = (GallifreyLocalInstance) n.localInstance();
        li.gallifreyType().qualification = q;
        qualification = q;
        return n;
    }
    
    public RefQualification qualification() {
    	return qualification;
    }
}
