package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class RefQualification extends Node_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification(Position pos) {
        super(pos);
    }

    public RefQualification copy() {
        throw new UnsupportedOperationException();
    }

    public boolean isShared() {
        return false;
    }

    public boolean isMove() {
        return false;
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isUnique() {
        return false;
    }

    public boolean isUnknown() {
        return false;
    }

    public boolean isAny() {
        return false;
    }
}
