package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class LocalRef extends Node_c implements RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static final String DEFAULT_OWNER = "DEFAULT";

    public String ownerAnnotation = DEFAULT_OWNER;
    public boolean borrow = false;

    public LocalRef(Position pos) {
        super(pos);
    }

    public LocalRef(Position pos, String owner) {
        super(pos);
        ownerAnnotation = owner;
    }
    
    public LocalRef(Position pos, String owner, boolean borrow) {
        super(pos);
        ownerAnnotation = owner;
        this.borrow = borrow;
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
