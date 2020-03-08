package gallifreyc.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Ext;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

// I think this is currently unused
public class ConditionedMethodDecl_c extends MethodDecl_c implements ConditionedMethodDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected PreCondition pre;
    protected PostCondition post;

    public ConditionedMethodDecl_c(Position pos, MethodDecl method, PreCondition pre, PostCondition post) {
        super(pos, method.flags(), method.returnType(), method.id(),
              method.formals(), method.throwTypes(), method.body());
        this.pre = pre;
        this.post = post;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public PreCondition pre() {
        return pre;
    }

    @Override
    public PostCondition post() {
        return post;
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        //TODO
    }

    @Override
    public void translate(CodeWriter w, Translator tr) {
        //TODO
    }
    
    @Override
    public Node visitChildren(NodeVisitor v) {
    	//TODO
        return null;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return null;
    }
}
