package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyType;
import polyglot.ast.NewArray;
import polyglot.ast.Node;

public class GallifreyNewArrayExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public NewArray node() {
        return (NewArray) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        NewArray node = (NewArray) superLang().typeCheck(this.node(), tc);
        if (node.init() != null) {
            GallifreyType initType = GallifreyExprExt.ext(node.init()).gallifreyType;
            this.gallifreyType = new GallifreyType(initType.qualification());
        } else {
            this.gallifreyType = new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
        }
        return node;
    }
}
