package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Cast;
import polyglot.ast.Node;

public class GallifreyCastExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Cast node() {
        return (Cast) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Cast node = (Cast) superLang().typeCheck(this.node(), tc);
        if (node.castType() instanceof RefQualifiedTypeNode) {
            throw new SemanticException("cannot cast to different qualification", node.position());
        }
        this.gallifreyType = new GallifreyType(GallifreyExprExt.ext(node.expr()).gallifreyType);
        return node;
    }
}
