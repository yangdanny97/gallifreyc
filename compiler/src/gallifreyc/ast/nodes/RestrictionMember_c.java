package gallifreyc.ast.nodes;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public abstract class RestrictionMember_c extends Node_c implements RestrictionMember {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RestrictionMember_c(Position pos) {
        super(pos);
    }
}
