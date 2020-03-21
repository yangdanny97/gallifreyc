package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.ext.jl7.ast.J7Lang;

public interface GallifreyLang extends J7Lang {
    String freshVar();
    int fresh();
}
