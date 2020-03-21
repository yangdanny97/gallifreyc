package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.ast.Node_c;

public class MoveRef_c extends Node_c implements MoveRef {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public MoveRef_c(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "move";
    }
    
    @Override
    public boolean equals(Object other) {
    	return other instanceof MoveRef;
    }
}
