package gallifreyc.types;

import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.UnknownRef;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import java.io.Serializable;

// holds the qualification of each expression
public class GallifreyType implements Serializable {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification;

    /* for serialization */
    protected GallifreyType() {
        this.qualification = new UnknownRef(Position.COMPILER_GENERATED);
    }

    public GallifreyType(RefQualification q) {
        this.qualification = q;
    }

    public GallifreyType(GallifreyType t) {
        this.qualification = t.qualification();
    }

    public RefQualification qualification() {
        return qualification;
    }

    public GallifreyType qualification(RefQualification qualification) {
        this.qualification = qualification;
        return this;
    }

    public boolean isMove() {
        return qualification instanceof MoveRef;
    }

    public boolean isIsolated() {
        return qualification.isIsolated();
    }

    public boolean isShared() {
        return qualification.isShared();
    }

    public boolean isLocal() {
        return qualification.isLocal();
    }

    @Override
    public String toString() {
        return "GallifreyType<" + qualification.toString() + ">";
    }
}
