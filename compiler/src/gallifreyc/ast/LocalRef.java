package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

// default qualification for variables, may be freely assigned to other locals
public class LocalRef extends RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static final String DEFAULT_OWNER = "DEFAULT";

    public String ownerAnnotation = DEFAULT_OWNER;

    public LocalRef(Position pos) {
        super(pos);
    }

    public LocalRef(Position pos, String owner) {
        super(pos);
        ownerAnnotation = owner;
    }

    @Override
    public String toString() {
        return "local";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LocalRef) {
            LocalRef l = (LocalRef) other;
            return this.ownerAnnotation.equals(l.ownerAnnotation);
        }
        return false;
    }

    @Override
    public RefQualification copy() {
        return new LocalRef(this.position);
    }

    public boolean isLocal() {
        return true;
    }
}
