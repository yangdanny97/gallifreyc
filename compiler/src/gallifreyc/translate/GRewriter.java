package gallifreyc.translate;

import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.visit.NodeVisitor;

public interface GRewriter {
    public Node rewrite(Node n) throws SemanticException;

    public NodeVisitor rewriteEnter(Node n) throws SemanticException;
}
