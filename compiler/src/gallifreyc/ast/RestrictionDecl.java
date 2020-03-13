package gallifreyc.ast;

import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TopLevelDecl;
import polyglot.types.Flags;

public interface RestrictionDecl extends Term, TopLevelDecl {
    Id id();
    Id for_id();
    RestrictionBody body();
}
