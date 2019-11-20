package gallifreyc.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class PreCondition_c extends Node_c implements PreCondition {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Expr cond;

    public PreCondition_c(Position pos, Expr e) {
        super(pos);
        this.cond = e;
    }

    @Override
    public String toString() {
        return "requires " + cond.toString();
    }

    @Override
    public Expr cond() {
        return cond;
    }
}
