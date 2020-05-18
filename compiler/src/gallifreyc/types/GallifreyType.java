package gallifreyc.types;

import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UniqueRef;
import gallifreyc.ast.UnknownRef;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import java.io.Serializable;

public class GallifreyType implements Serializable {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    public static final String DEFAULT_CAPABILITY = "DEFAULT";

    public RefQualification qualification;
    public String capability = DEFAULT_CAPABILITY;
    public String path = "";

    /* for serialization */
    protected GallifreyType() {
        this.qualification = new UnknownRef(Position.COMPILER_GENERATED);
    }

    public GallifreyType(RefQualification q) {
        this.qualification = q;
    }
    
    public GallifreyType(GallifreyType t) {
        this.qualification = t.qualification();
        this.capability = t.capability();
        this.path = t.path();
    }

    public RefQualification qualification() {
        return qualification;
    }

    public GallifreyType qualification(RefQualification qualification) {
        this.qualification = qualification;
        return this;
    }

    public String capability() {
        return capability;
    }

    public GallifreyType capability(String capability) {
        this.capability = capability;
        return this;
    }

    public String path() {
        return path;
    }

    public GallifreyType path(String path) {
        this.path = path;
        return this;
    }

    public boolean isMove() {
        return qualification instanceof MoveRef;
    }

    public boolean isUnique() {
        return qualification instanceof UniqueRef;
    }

    public boolean isShared() {
        return qualification instanceof SharedRef;
    }

    public boolean isLocal() {
        return qualification instanceof LocalRef;
    }

    @Override
    public String toString() {
        return "GallifreyType<" + qualification.toString() + ">";
    }
}
