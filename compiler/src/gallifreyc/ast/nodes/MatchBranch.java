package gallifreyc.ast.nodes;

import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.LocalDecl;

public interface MatchBranch extends Node {
	LocalDecl pattern();
	Stmt stmt();
}
