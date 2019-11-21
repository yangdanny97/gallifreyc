package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class RestrictionDecl_c extends Node_c implements RestrictionDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id for_id;
    protected RestrictionBody body;

    public RestrictionDecl_c(Position pos, Id id, Id for_id, RestrictionBody body) {
        super(pos);
        this.id = id;
        this.for_id = for_id;
        this.body = body;
    }

    @Override
    public String toString() {
        // TODO: Implement this
        return "";
    }


    public Id id() {
        return id;
    }

    public Id for_id() {
        return for_id;
    }

    public RestrictionBody body() {
        return body;
    }
}
