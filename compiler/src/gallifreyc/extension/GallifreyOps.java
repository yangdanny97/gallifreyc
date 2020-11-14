package gallifreyc.extension;

import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.FieldInitRewriter;
import gallifreyc.translate.GallifreyCodegenRewriter;
import polyglot.ast.Node;
import polyglot.ast.NodeOps;
import polyglot.types.SemanticException;

/**
 * Define a collection of operations that any Gallifrey node must implement.
 * This interface allows extensions both to override and reuse functionality in
 * GallifreyExt (i think).
 */
public interface GallifreyOps extends NodeOps {
    public Node aNormalize(ANormalizer rw) throws SemanticException;

    public Node rewriteFieldInits(FieldInitRewriter rw) throws SemanticException;

    public Node gallifreyRewrite(GallifreyCodegenRewriter rw) throws SemanticException;
}