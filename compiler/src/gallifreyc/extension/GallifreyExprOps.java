package gallifreyc.extension;

import gallifreyc.types.GallifreyType;

public interface GallifreyExprOps {
    public GallifreyType gallifreyType();

    public GallifreyExprExt gallifreyType(GallifreyType t);

    public boolean isMove();

    public boolean isUnique();

    public boolean isShared();

    public boolean isLocal();
}
