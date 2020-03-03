package gallifreyc.ast.nodes;

import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

//TODO this may not be necessary anymore
public class LocalRef_c extends RefQualification_c implements LocalRef {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public LocalRef_c(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "local";
    }
    
    @Override
    public boolean equals(Object other) {
    	return other instanceof LocalRef;
    }
    
}
