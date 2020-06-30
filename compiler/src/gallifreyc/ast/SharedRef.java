package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class SharedRef extends RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RestrictionId restriction;

    public SharedRef(Position pos, RestrictionId restriction) {
        super(pos);
        this.restriction = restriction;
    }

    @Override
    public String toString() {
        return "shared[" + restriction.toString() + "]";
    }

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

    @Override
    public RefQualification copy() {
        return new SharedRef(this.position, restriction.copy());
    }

    public boolean isShared() {
        return true;
    }

}