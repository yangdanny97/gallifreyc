package gallifreyc.extension;

import polyglot.ext.jl7.ast.J7Lang;

/* Language dispatcher for Gallifrey - determines which ext object/node to delegate passes/operations to  */
public interface GallifreyLang extends J7Lang {
    String freshVar();

    int fresh();
}
