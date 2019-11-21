package gallifreyc.ast;

import polyglot.util.SerialVersionUID;

public class GallifreyMethodDeclExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    protected PreCondition pre;
    protected PostCondition post;
    // Is this MethodDecl a "test"?
    protected boolean isTest;

    PreCondition pre() {
        return pre;
    }
    
    PostCondition post() {
        return post;
    }
    
    boolean isTest() {
        return isTest;
    }
}
