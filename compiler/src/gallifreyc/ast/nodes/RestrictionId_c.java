package gallifreyc.ast.nodes;

import polyglot.ast.Id;
import polyglot.ast.Node_c;
import polyglot.util.Position;

public class RestrictionId_c extends Node_c implements RestrictionId {
    private Id rv;
    private Id restriction;
    private boolean wildcard;
    
    public RestrictionId_c(Position pos, Id rv, Id restriction, boolean wildcard) {
        super(pos);
        assert restriction != null;
        this.restriction = restriction;
        this.rv = rv;
        this.wildcard = wildcard;
    }
    
	public Id rv() {
		return rv;
	}
	
	public Id restriction() {
		return restriction;
	}
	
	public boolean wildcardRv() {
		return wildcard;
	}
	public boolean isRvQualified() {
		return wildcard || rv != null;
	}
}
