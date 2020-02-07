package gallifreyc.ast.nodes;

import polyglot.ast.*;

import java.util.List;

public interface RestrictionUnionDecl extends Node, TopLevelDecl {
	Id id();
	List<Id> restrictions();
}
