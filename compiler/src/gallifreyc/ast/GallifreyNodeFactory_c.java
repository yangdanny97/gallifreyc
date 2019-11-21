package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.ext.jl5.ast.JL5Ext;
import polyglot.ext.jl5.ast.JL5MethodDeclExt;
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
    public PreCondition PreCondition(Position pos, Expr e) {
        PreCondition p = new PreCondition_c(pos, e);
        p = ext(p, extFactory().extPreCondition());
        return p;
    }

    public PostCondition PostCondition(Position pos, Expr e) {
        PostCondition_c p = new PostCondition_c(pos, e);
        p = ext(p, extFactory().extPostCondition());
        return p;
    }
    
    public MethodDecl MethodDecl(Position pos, MethodDecl method, PreCondition pre, PostCondition post) {
//        GallifreyExt ext = GallifreyExt.ext(method);
//        GallifreyMethodDeclExt ext = (GallifreyMethodDeclExt) GallifreyExt.ext(method);
        GallifreyMethodDeclExt ext = (GallifreyMethodDeclExt) GallifreyExt.ext(method);
        ext.pre = pre;
        ext.post = post;
        return method;
    }
    
    @Override
    public LocalRef LocalRef(Position pos) {
        LocalRef l = new LocalRef_c(pos);
        l = ext(l, extFactory().extLocalRef());
        return l;
    }

    @Override
    public UniqueRef UniqueRef(Position pos) {
        UniqueRef u = new UniqueRef_c(pos);
        u = ext(u, extFactory().extUniqueRef());
        return u;
    }

    @Override
    public SharedRef SharedRef(Position pos, Id restriction) {
        SharedRef s = new SharedRef_c(pos, restriction);
        s = ext(s, extFactory().extSharedRef());
        return s;
    }

    // TODO:  Override factory methods for overridden AST nodes.
    // TODO:  Override factory methods for AST nodes with new extension nodes.
}
