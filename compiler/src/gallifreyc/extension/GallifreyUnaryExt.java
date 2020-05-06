package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Node;
import polyglot.ast.Unary;

public class GallifreyUnaryExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Unary node() {
        return (Unary) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Unary node = (Unary) superLang().typeCheck(this.node(), tc);
        GallifreyExprExt exprExt = GallifreyExprExt.ext(node.expr());
        this.gallifreyType = new GallifreyType(exprExt.gallifreyType().qualification);
        return node;
    }
}
