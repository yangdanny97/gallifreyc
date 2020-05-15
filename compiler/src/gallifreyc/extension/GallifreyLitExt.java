package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyLitExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    {
        gallifreyType = new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Expr node = (Expr) superLang().typeCheck(node(), tc);
        return node;
    }
}
