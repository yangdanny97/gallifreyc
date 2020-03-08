package gallifreyc.ast;

import polyglot.ast.Node;
import polyglot.ast.Id;

public interface RestrictionId extends Node { 
	public Id rv();
	public Id restriction();
	public boolean wildcardRv();
	public boolean isRvQualified();
}

