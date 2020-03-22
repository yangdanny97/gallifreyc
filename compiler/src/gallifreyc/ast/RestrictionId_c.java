package gallifreyc.ast;

import polyglot.ast.Id;
import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class RestrictionId_c extends Node_c implements RestrictionId {
	private static final long serialVersionUID = SerialVersionUID.generate();
	// rv::restriction OR wildcard::restriction OR restriction
    private Id rv;
    private Id restriction;
    private boolean wildcard;
    
    public RestrictionId_c(Position pos, Id rv, Id restriction, boolean wildcard) {
        super(pos);
        assert restriction != null;
        assert (wildcard && rv == null) || (!wildcard);
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
	
	@Override
	public String toString() {
		String s = "";
		if (rv != null) {
			s = rv.toString();
		} else if (wildcard) {
			s = "*";
		}
		s = (isRvQualified()) ? s + "::" : s;
		s = s + restriction.toString();
		return s;
	}
	
    @Override
    public boolean equals(Object other) {
    	if (other instanceof RestrictionId) {
    		RestrictionId otherrid = (RestrictionId) other;
    		return rv.id().equals(otherrid.rv().id()) && 
    				restriction.id().equals(otherrid.restriction().id()) && 
    				wildcard == otherrid.wildcardRv();
    	}
    	return false;
    }
}
