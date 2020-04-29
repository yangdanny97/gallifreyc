package gallifreyc.extension;

import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.translate.GRewriter;
import gallifreyc.types.GallifreyLocalInstance;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5LocalDeclExt;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

public class GallifreyLocalDeclExt extends JL5LocalDeclExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate(); 
    
    @Override
    public LocalDecl node() {
    	return (LocalDecl) super.node();
    }
    
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
