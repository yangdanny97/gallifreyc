package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.translate.GRewriter;
import gallifreyc.types.GallifreyConstructorInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

public class GallifreyConstructorDeclExt extends GallifreyExt implements GallifreyOps {
	private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override 
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        GRewriter crw = (GRewriter) rw;
        return crw.rewrite(node);
    }
    
    @Override 
    public NodeVisitor extRewriteEnter(ExtensionRewriter rw) throws SemanticException {
        GRewriter crw = (GRewriter) rw;
        return crw.rewriteEnter(node);
    }
	
    @Override
    public ConstructorDecl node() {
    	return (ConstructorDecl) super.node();
    }
	
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
    	ConstructorDecl cd = (ConstructorDecl) superLang().buildTypes(this.node, tb);
        
        List<GallifreyType> inputTypes = new ArrayList<>();
        
        for (Formal f : cd.formals()) {
            if (!(f instanceof RefQualifiedTypeNode)) {
            	throw new SemanticException("param types must be ref qualified: " + cd.name(), cd.position());
            }
            GallifreyType fQ = new GallifreyType(((RefQualifiedTypeNode) f).qualification());
            inputTypes.add(fQ);
        }
        
        GallifreyConstructorInstance ci = (GallifreyConstructorInstance) cd.constructorInstance();
        ci.gallifreyInputTypes(inputTypes);

        return cd;
    }
}