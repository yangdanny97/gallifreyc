package gallifreyc.extension;

import gallifreyc.types.GallifreyType;
import polyglot.ast.ExprOps;

public interface GallifreyExprOps extends ExprOps {
    public GallifreyType gallifreyType();

    public GallifreyExprExt gallifreyType(GallifreyType t);

    public boolean isMove();

    public boolean isUnique();

    public boolean isShared();

    public boolean isLocal();
}
