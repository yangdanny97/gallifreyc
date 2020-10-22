package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

// formerly unique
public class IsolatedRef extends RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public IsolatedRef(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "isolated";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof IsolatedRef;
    }

    @Override
    public RefQualification copy() {
        return new IsolatedRef(this.position);
    }

    public boolean isIsolated() {
        return true;
    }
}