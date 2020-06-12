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
        return wildcard || (rv != null);
    }

    @Override
    public String toString() {
        String s = "";
        if (rv != null) {
            s = rv.toString() + "::";
        } else if (wildcard) {
            s = "*::";
        }
        s = s + restriction.toString();
        return s;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof RestrictionId) {
            RestrictionId otherrid = (RestrictionId) other;
            if ((rv == null && otherrid.rv() != null) || (rv != null && otherrid.rv() == null))
                return false;
            if ((rv == null && otherrid.rv() == null) || rv.id().equals(otherrid.rv().id())) {
                return restriction.id().equals(otherrid.restriction().id()) && wildcard == otherrid.wildcardRv();
            }
        }
        return false;
    }

    @Override
    public String getInterfaceName() {
        if (rv != null) {
            return rv.toString() + "_" + restriction.toString();
        }
        return restriction.toString();
    }

    @Override
    public String getWrapperName() {
        if (rv != null) {
            return rv.toString();
        }
        return restriction.toString();
    }

    @Override
    public RestrictionId copy() {
        Id rv = (this.rv != null) ? (Id) this.rv.copy() : null;
        Id restriction = (this.restriction != null) ? (Id) this.restriction.copy() : null;
        return new RestrictionId_c(this.position, rv, restriction, this.wildcard);
    }
}
