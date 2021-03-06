package gallifreyc.ast;

import polyglot.ast.Term;
import polyglot.ast.Block;
import polyglot.ast.LocalDecl;

// single branch of match_restriction node
public interface MatchBranch extends Term {
    LocalDecl pattern();

    Block body();

    MatchBranch pattern(LocalDecl d);
}
