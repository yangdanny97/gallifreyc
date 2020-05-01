package gallifreyc.ast;

import polyglot.ast.*;

import java.util.List;

public interface RestrictionUnionDecl extends Node, TopLevelDecl {
    Id id();

    List<Id> restrictions();
}
