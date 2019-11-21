package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class LocalRef_c extends RefQualification_c implements LocalRef {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public LocalRef_c(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "local";
    }
}
