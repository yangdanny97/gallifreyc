package gallifreyc.ast.nodes;

import polyglot.ast.Id;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class SharedRef_c extends RefQualification_c implements SharedRef {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected RestrictionId restriction;

    public SharedRef_c(Position pos, RestrictionId restriction) {
        super(pos);
        this.restriction = restriction;
    }

    @Override
    public String toString() {
        return "shared[" + restriction.toString() + "]";
    }

    @Override
    public RestrictionId restriction() {
        return restriction;
    }
    
    @Override
    public boolean equals(Object other) {
    	if (other instanceof SharedRef) {
    		return restriction.equals(((SharedRef) other).restriction());
    	}
    	return false;
    }
}
