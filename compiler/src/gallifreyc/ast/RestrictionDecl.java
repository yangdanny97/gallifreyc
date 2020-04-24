package gallifreyc.ast;

import polyglot.ast.Id;
import polyglot.ast.Term;
import polyglot.ast.TopLevelDecl;

public interface RestrictionDecl extends Term, TopLevelDecl {
    Id id();
    Id for_id();
    RestrictionBody body();
}
