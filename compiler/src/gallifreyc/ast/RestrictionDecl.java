package gallifreyc.ast;

import polyglot.ast.Id;
import polyglot.ast.Term;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;

public interface RestrictionDecl extends Term, TopLevelDecl {
    Id id();

    TypeNode forClass();

    RestrictionBody body();
}
