package gallifreyc.ast;

import polyglot.ast.MethodDecl;

public interface ConditionedMethodDecl extends MethodDecl {
    PreCondition pre();
    PostCondition post();
}
