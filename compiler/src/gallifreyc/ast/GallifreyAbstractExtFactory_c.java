package gallifreyc.ast;

import polyglot.ast.AbstractExtFactory_c;
import polyglot.ast.Ext;
import polyglot.ast.ExtFactory;

public abstract class GallifreyAbstractExtFactory_c extends AbstractExtFactory_c
        implements GallifreyExtFactory {

    public GallifreyAbstractExtFactory_c() {
        super();
    }

    public GallifreyAbstractExtFactory_c(ExtFactory nextExtFactory) {
        super(nextExtFactory);
    }

    // TODO: Implement factory methods for new extension nodes in future
    // extensions.  This entails calling the factory method for extension's
    // AST superclass.
}
