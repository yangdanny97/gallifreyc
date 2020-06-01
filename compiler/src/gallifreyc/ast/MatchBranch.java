package gallifreyc.ast;

import polyglot.ast.Term;
import polyglot.ast.Stmt;
import polyglot.ast.Block;
import polyglot.ast.LocalDecl;

public interface MatchBranch extends Term {
    LocalDecl pattern();

    Block body();
    
    MatchBranch pattern(LocalDecl d);
}
