package gallifreyc.ast.nodes;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class UniqueRef_c extends RefQualification_c implements UniqueRef {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public UniqueRef_c(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "unique";
    }
    
    @Override
    public boolean equals(Object other) {
    	return other instanceof UniqueRef;
    }
}
