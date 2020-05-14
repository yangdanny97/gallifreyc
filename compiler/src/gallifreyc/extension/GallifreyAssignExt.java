package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.Node;

public class GallifreyAssignExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Assign node() {
        return (Assign) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Assign a = (Assign) superLang().typeCheck(this.node(), tc);
        GallifreyType lt = GallifreyExprExt.ext(a.left()).gallifreyType;
        GallifreyType rt = GallifreyExprExt.ext(a.right()).gallifreyType;
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();

        if (!ts.checkQualifications(rt, lt)) {
            throw new SemanticException("cannot assign " + rt.qualification + " to " + lt.qualification,
                    node().position());
        }

        this.gallifreyType = new GallifreyType(lt);
        return a;
    }
}
