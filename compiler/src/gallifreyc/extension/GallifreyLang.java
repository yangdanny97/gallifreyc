package gallifreyc.extension;

import polyglot.ast.Expr;
import polyglot.ext.jl7.ast.J7Lang;

public interface GallifreyLang extends J7Lang {
    String freshVar();
    int fresh();
    
    public GallifreyExprExt exprExt(Expr n);
}
