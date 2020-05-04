package gallifreyc.extension;

import gallifreyc.types.GallifreyProcedureInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Node;
import polyglot.ast.ProcedureCallOps;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.SerialVersionUID;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/* this is the constructor call STATEMENT (this(...) or super(...)) */
public class GallifreyConstructorCallExt extends GallifreyExt implements ProcedureCallOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyConstructorCallExt() {
    }

    @Override
    public ConstructorCall node() {
        return (ConstructorCall) super.node();
    }

    @Override
    public void printArgs(CodeWriter w, PrettyPrinter tr) {
        superLang().printArgs(node(), w, tr);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        ConstructorCall node = (ConstructorCall) superLang().typeCheck(this.node(), tc);
        GallifreyProcedureInstance ci = (GallifreyProcedureInstance) node.constructorInstance();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        ts.checkArgs(ci.gallifreyInputTypes(), node().arguments());
        return node;
    }
}
