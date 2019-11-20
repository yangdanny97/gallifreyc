package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.ext.jl7.ast.JL7NodeFactory;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
import polyglot.util.*;

import java.util.*;

/**
 * NodeFactory for gallifreyc extension.
 */
public interface GallifreyNodeFactory extends JL7NodeFactory {
    // TODO: Declare any factory methods for new AST nodes.
    PreCondition PreCondition(Position pos, Expr e);
    PostCondition PostCondition(Position pos, Expr e);
//    ConditionedMethodDecl ConditionedMethodDecl(Position pos, MethodDecl method, PreCondition pre, PostCondition post);
    MethodDecl MethodDecl(Position pos, MethodDecl method, PreCondition pre, PostCondition post);

}
