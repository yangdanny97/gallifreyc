package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class AllowsStmt_c extends RestrictionMember_c implements AllowsStmt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id contingent_id;

    public AllowsStmt_c(Position pos, Id id, Id contingent_id) {
        super(pos);
        this.id = id;
        this.contingent_id = contingent_id;
    }

    @Override
    public String toString() {
        // TODO: Implement this
        return "";
    }

    public Id id() {
        return id;
    }

    public Id contingent_id() {
        return contingent_id;
    }
}
