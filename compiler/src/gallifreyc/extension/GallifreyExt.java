package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.types.SemanticException;
import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.FieldInitRewriter;
import gallifreyc.translate.GallifreyRewriter;

public class GallifreyExt extends Ext_c implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public static GallifreyOps ext(Node n) {
        Ext e = n.ext();
        while (e != null && !(e instanceof GallifreyOps)) {
            e = e.ext();
        }
        if (e == null) {
            throw new InternalCompilerError("No Gallifrey extension object for node " + n + " (" + n.getClass() + ")",
                    n.position());
        }
        return (GallifreyOps) e;
    }

    @Override
    public final GallifreyLang lang() {
        return GallifreyLang_c.instance;
    }

    @Override
    public Node aNormalize(ANormalizer rw) throws SemanticException {
        if (node() instanceof Stmt && !(node() instanceof Block)) {
            return rw.addHoistedDecls((Stmt) node());
        }
        return node();
    }

    @Override
    public Node rewriteFieldInits(FieldInitRewriter rw) throws SemanticException {
        return node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        return node();
    }
}
