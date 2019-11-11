package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.ext.jl7.ast.JL7NodeFactory_c;
import polyglot.util.*;

import java.util.*;

/**
 * NodeFactory for gallifreyc extension.
 */
public class GallifreyNodeFactory_c extends JL7NodeFactory_c implements GallifreyNodeFactory {
    public GallifreyNodeFactory_c(GallifreyLang lang, GallifreyExtFactory extFactory) {
        super(lang, extFactory);
    }

    @Override
    public GallifreyExtFactory extFactory() {
        return (GallifreyExtFactory) super.extFactory();
    }

    // TODO:  Implement factory methods for new AST nodes.
    // TODO:  Override factory methods for overridden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
