package gallifreyc.ast;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;

public final class GallifreyExtFactory_c extends GallifreyAbstractExtFactory_c {

    public GallifreyExtFactory_c() {
        super();
    }

    public GallifreyExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    @Override
    protected Ext extNodeImpl() {
        return new GallifreyExt();
    }

    // TODO: Override factory methods for new extension nodes in the current
    // extension.
}
