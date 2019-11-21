package gallifreyc.ast;

import polyglot.ast.Id;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class SharedRef_c extends RefQualification_c implements SharedRef {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id restriction;

    public SharedRef_c(Position pos, Id restriction) {
        super(pos);
        this.restriction = restriction;
    }

    @Override
    public String toString() {
        return "shared[" + restriction.toString() + "]";
    }

    @Override
    public Id restriction() {
        return restriction;
    }
}
