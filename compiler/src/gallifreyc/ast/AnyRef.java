package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class AnyRef extends RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public AnyRef(Position pos) {
        super(pos);
    }

    @Override
    public String toString() {
        return "any";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AnyRef;
    }

    public RefQualification copy() {
        return new AnyRef(this.position);
    }
    
    public boolean isAny() {
        return true;
    }
}
