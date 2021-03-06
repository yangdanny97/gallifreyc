package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyEnumConstantExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Expr node = (Expr) superLang().typeCheck(this.node(), tc);
        this.gallifreyType = new GallifreyType(new MoveRef(node.position()));
        return node;
    }
}
