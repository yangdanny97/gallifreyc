package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import java.util.List;

public class RestrictionBody_c extends Node_c implements RestrictionBody {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected List<Node> members;

    public RestrictionBody_c(Position pos, List<Node> members) {
        super(pos);
        this.members = members;
    }

    @Override
    public String toString() {
        // TODO: Implement this
        return "";
    }


    public List<Node> members() {
        return members;
    }
}
