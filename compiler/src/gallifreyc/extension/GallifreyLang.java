package gallifreyc.extension;

import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang;

public interface GallifreyLang extends J7Lang {    
    SharedTypeWrapper wrapSharedTypeEnter(Node n, SharedTypeWrapper v);
    Node wrapSharedType(Node n, SharedTypeWrapper v);
    String freshVar();
    int fresh();
}
