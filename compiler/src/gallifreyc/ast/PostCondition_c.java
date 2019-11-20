package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class PostCondition_c extends Node_c implements PostCondition {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Expr cond;

    public PostCondition_c(Position pos, Expr e) {
        super(pos);
        this.cond = e;
    }

    @Override
    public String toString() {
        return "ensures " + cond.toString();
    }

    @Override
    public Expr cond() {
        return cond;
    }
}
