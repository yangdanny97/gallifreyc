package gallifreyc.extension;

import java.util.LinkedList;
import java.util.List;

import gallifreyc.ast.PostCondition;
import gallifreyc.ast.PreCondition;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.util.SerialVersionUID;

// extends method declarations to hold an optional pre/post condition, and a flag for whether it's a test method
public class GallifreyMethodDeclExt extends GallifreyExt {
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
}
