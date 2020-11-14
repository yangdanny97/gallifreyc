package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.translate.ANormalizer;
import gallifreyc.types.GallifreyProcedureInstance;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
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
    public Node aNormalize(ANormalizer rw) throws SemanticException {
        List<Expr> args = new ArrayList<>(node().arguments());
        List<Expr> hoistedArgs = new ArrayList<>();
        for (Expr arg : args) {
            hoistedArgs.add(rw.hoist(arg));
        }
        return node().arguments(hoistedArgs);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        ConstructorCall node = (ConstructorCall) superLang().typeCheck(this.node(), tc);
        GallifreyProcedureInstance ci = (GallifreyProcedureInstance) node.constructorInstance();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        ts.checkArgs(ci, node().arguments());
        return node;
    }
}
