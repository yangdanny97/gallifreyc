package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

/* used for compiler-generated nodes where the qualification doesn't matter */
public class UnknownRef extends RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public UnknownRef(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "UNKNOWN-QUALIFICATION";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnknownRef;
    }

    @Override
    public RefQualification copy() {
        return new UnknownRef(this.position);
    }

    public boolean isUnknown() {
        return true;
    }
}
