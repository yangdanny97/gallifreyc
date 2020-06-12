package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class UniqueRef extends Node_c implements RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public UniqueRef(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "unique";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UniqueRef;
    }

    @Override
    public RefQualification copy() {
        return new UniqueRef(this.position);
    }
}