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
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.*;
import gallifreyc.ast.UniqueRef;
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
    public SharedTypeWrapper wrapSharedTypeEnter(SharedTypeWrapper v) {
////        System.out.printf("Calling wrapSharedTypeEnter on this='%s' whose class is '%s'\n", node(), node().getClass().getName());
////        System.out.printf("v's hashcode = %d\n", v.hashCode());
////        System.out.printf("v.sourceFile() == null is %b\n", v.sourceFile() == null);
//        if (node() instanceof SourceFile) {
////            System.out.printf("Calling wrapSharedTypeEnter on this='%s' whose class is '%s'\n", node(), node().getClass().getName());
//            return v.sourceFile((SourceFile) node());
//        }
        return v;
    }

    @Override
    public Node wrapSharedType(SharedTypeWrapper v) {
//        System.out.printf("Calling wrapSharedType on this='%s' whose class is '%s'\n", node(), node().getClass().getName());
//        if (node() instanceof ClassDecl) {
//            System.out.printf("Calling wrapSharedType on this='%s' whose type is '%s' (class: %s)\n",
//                              node(),
//                              ((ClassDecl) node()).type(),
//                              ((ClassDecl) node()).type().getClass().getName());
//            System.out.printf("Visiting node %s with type %s");
//        }
//        System.out.printf("v's hashcode = %d\n", v.hashCode());
//        if (v.sourceFile() != null) {
//            System.out.printf("v.sourceFile().decls().size() = %d\n", v.sourceFile().decls().size());
//        } else {
//            System.out.printf("v.sourceFile() == null is %b\n", v.sourceFile() == null);
//        }
        return node();
    }
    
    @Override 
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        GallifreyRewriter crw = (GallifreyRewriter) rw;
        return crw.rewrite(node);
    }
    
    @Override 
    public NodeVisitor extRewriteEnter(ExtensionRewriter rw) throws SemanticException {
        GallifreyRewriter crw = (GallifreyRewriter) rw;
        return crw.rewriteEnter(node);
    }
}
