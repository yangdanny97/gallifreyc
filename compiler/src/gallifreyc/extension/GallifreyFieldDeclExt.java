package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyFieldDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate(); 
    
    @Override
    public FieldDecl node() {
    	return (FieldDecl) super.node();
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
    	FieldDecl node = (FieldDecl) superLang().buildTypes(node(), tb);

        TypeNode t = node.type();
        if (!(t instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("cannot declare unqualified field");
        }
        RefQualification q = ((RefQualifiedTypeNode) t).qualification();
        GallifreyFieldInstance fi = (GallifreyFieldInstance) node.fieldInstance();
        fi.gallifreyType(new GallifreyType(q));
        
        return node;
    }
}

