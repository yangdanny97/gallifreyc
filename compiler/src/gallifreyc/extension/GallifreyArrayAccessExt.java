package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyType;
import polyglot.ast.ArrayAccess;
import polyglot.ast.Node;

public class GallifreyArrayAccessExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public ArrayAccess node() {
        return (ArrayAccess) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        // array contents and container have same qualification
        ArrayAccess aa = (ArrayAccess) superLang().typeCheck(this.node(), tc);
        GallifreyType arrayType = GallifreyExprExt.ext(aa.array()).gallifreyType;
        this.gallifreyType = new GallifreyType(arrayType.qualification());
        return aa;
    }
}
