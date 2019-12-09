package gallifreyc.ast;

import java.util.LinkedList;
import java.util.List;

import gallifreyc.visit.RefQualificationAdder;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
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
    
    @Override
    public MethodDecl node() {
        return (MethodDecl) super.node();
    }
    
    @Override
    public Node addRefQualification(RefQualificationAdder v) {
        // TODO: Iterate through parameters and change their TypeNodes to wrapped in a
        // RefQualifiedTypeNode (if not already)
        MethodDecl m = node();
//        System.out.printf("Found MethodDecl: %s\n", m.toString());
//        MethodInstance mi = m.methodInstance();
//        List<? extends Type> newFormalTypes = new LinkedList<>();
//        for (Type t : mi.formalTypes()) {
////            if (!t instanceof )
//        }
        
        return super.addRefQualification(v);
    }
}
