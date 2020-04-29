package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.PostCondition;
import gallifreyc.ast.PreCondition;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;

// extends method declarations to hold an optional pre/post condition, and a flag for whether it's a test method
public class GallifreyMethodDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    public PreCondition pre;
    public PostCondition post;
    // Is this MethodDecl a test method (inside a restriction)
    public boolean isTest;

    PreCondition pre() {
        return pre;
    }
    
    PostCondition post() {
        return post;
    }
    
    boolean isTest() {
        return isTest;
    }
    
    @Override
    public MethodDecl node() {
        return (MethodDecl) super.node();
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
    	MethodDecl md = (MethodDecl) superLang().buildTypes(this.node, tb);
        
        List<GallifreyType> inputTypes = new ArrayList<>();
        
        TypeNode returnType = md.returnType();
        if (!(returnType instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("return type must be ref qualified: " + md.name(), md.position());
        }
        GallifreyType gReturn = new GallifreyType(((RefQualifiedTypeNode) returnType).qualification());
        
        for (Formal f : md.formals()) {
            if (!(f instanceof RefQualifiedTypeNode)) {
            	throw new SemanticException("param types must be ref qualified: " + md.name(), md.position());
            }
            GallifreyType fQ = new GallifreyType(((RefQualifiedTypeNode) f).qualification());
            inputTypes.add(fQ);
        }
        
        GallifreyMethodInstance mi = (GallifreyMethodInstance) md.methodInstance();
        mi.gallifreyInputTypes(inputTypes);
        mi.gallifreyReturnType(gReturn);

        return md;
    }
}
