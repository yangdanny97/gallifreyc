package gallifreyc.extension;

import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;

// override default extension methods
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

    @Override
    protected Ext extMethodDeclImpl() {
        return new GallifreyMethodDeclExt();
    }

    @Override
    protected Ext extExprImpl() {
        return new GallifreyExprExt();
    }
    
    @Override
    protected Ext extSourceFileImpl() {
        return new GallifreySourceFileExt();
    }
}
