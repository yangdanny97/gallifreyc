package gallifreyc.ast;

import polyglot.ast.*;

import java.util.List;

// declaration of a restriction union (or restriction variant), 
// containing many sub-restrictions which must all be for the same class
public interface RestrictionUnionDecl extends Node, TopLevelDecl {
    Id id();

    List<Id> restrictions();
}
