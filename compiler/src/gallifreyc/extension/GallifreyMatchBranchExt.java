package gallifreyc.extension;

import gallifreyc.ast.MatchBranch;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

public class GallifreyMatchBranchExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyMatchBranchExt() {
    }

    @Override
    public MatchBranch node() {
        return (MatchBranch) super.node();
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        // set init to null
        MatchBranch b = node().pattern(node().pattern().init(tb.nodeFactory().NullLit(Position.COMPILER_GENERATED)));
        return superLang().buildTypesEnter(b, tb);
    }
}
