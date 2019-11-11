package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;

public class GallifreyExt extends Ext_c {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static GallifreyExt ext(Node n) {
        Ext e = n.ext();
        while (e != null && !(e instanceof GallifreyExt)) {
            e = e.ext();
        }
        if (e == null) {
            throw new InternalCompilerError("No Gallifrey extension object for node "
                    + n + " (" + n.getClass() + ")", n.position());
        }
        return (GallifreyExt) e;
    }

    @Override
    public final GallifreyLang lang() {
        return GallifreyLang_c.instance;
    }

    // TODO:  Override operation methods for overridden AST operations.
}
