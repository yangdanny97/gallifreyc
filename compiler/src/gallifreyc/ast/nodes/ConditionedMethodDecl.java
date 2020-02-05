package gallifreyc.ast.nodes;

import polyglot.ast.MethodDecl;

public interface ConditionedMethodDecl extends MethodDecl {
    PreCondition pre();
    PostCondition post();
}
