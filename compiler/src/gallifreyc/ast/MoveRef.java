package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

// variables with this qualification may be used as the RHS in assignments to isolated or local
// mostly unused
public class MoveRef extends RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public MoveRef(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "move";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MoveRef;
    }

    @Override
    public RefQualification copy() {
        return new MoveRef(this.position);
    }

    public boolean isMove() {
        return true;
    }
}
