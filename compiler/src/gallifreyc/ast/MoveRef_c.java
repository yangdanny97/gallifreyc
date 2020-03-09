package gallifreyc.ast;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class MoveRef_c extends RefQualification_c implements UniqueRef {
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
