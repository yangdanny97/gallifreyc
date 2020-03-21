package gallifreyc.extension;

import polyglot.ast.Node;
import polyglot.ast.NodeOps;

/**
 * Define a collection of operations that any Gallifrey node must
 * implement.  This interface allows extensions both to override and reuse
 * functionality in GallifreyExt (i think).
 */
public interface GallifreyOps extends NodeOps { }