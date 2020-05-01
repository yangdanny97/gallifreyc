package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class LocalRef extends Node_c implements RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public LocalRef(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "local";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof LocalRef;
    }
}
