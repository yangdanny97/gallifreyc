package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class MoveRef extends Node_c implements RefQualification {
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
}
