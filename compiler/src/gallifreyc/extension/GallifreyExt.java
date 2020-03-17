package gallifreyc.extension;

import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.*;
import polyglot.util.Copy;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.translate.ExtensionRewriter;
import gallifreyc.translate.GRewriter;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.*;
import gallifreyc.ast.*;
import java.util.*;

public class GallifreyExt extends Ext_c implements GallifreyOps {
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
    
    @Override 
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        GRewriter crw = (GRewriter) rw;
        return crw.rewrite(node);
    }
    
    @Override 
    public NodeVisitor extRewriteEnter(ExtensionRewriter rw) throws SemanticException {
        GRewriter crw = (GRewriter) rw;
        return crw.rewriteEnter(node);
    }
    
    @Override
    public SharedTypeWrapper wrapSharedTypeEnter(SharedTypeWrapper v) {
        return v;
    }

    @Override
    public Node wrapSharedType(SharedTypeWrapper v) {
        return node();
    }
}
