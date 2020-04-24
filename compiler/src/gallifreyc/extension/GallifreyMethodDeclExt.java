package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.PostCondition;
import gallifreyc.ast.PreCondition;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ext.jl5.ast.JL5Ext;
import polyglot.ext.jl5.ast.JL5FormalExt;
import polyglot.ext.jl5.types.JL5Flags;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeBuilder;

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
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
    	//TODO
    	return node();
    }
}
