package gallifreyc.ast;

import polyglot.ast.Node_c;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class RefQualification_c extends Node_c implements RefQualification {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification_c(Position pos) {
        super(pos);
    }
}
