package gallifreyc.extension;

import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.types.GallifreyConstructorInstance;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.NewOps;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyNewExt extends GallifreyExprExt implements NewOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public New node() {
        return (New) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        New node = (New) superLang().typeCheck(this.node(), tc);
        GallifreyConstructorInstance ci = (GallifreyConstructorInstance) node.constructorInstance();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        this.gallifreyType = ts.checkArgs(ci, node().arguments());
        return node;
    }

    @Override
    public void printArgs(CodeWriter w, PrettyPrinter tr) {
        superLang().printArgs(node(), w, tr);
    }

    @Override
    public TypeNode findQualifiedTypeNode(AmbiguityRemover ar, ClassType outer, TypeNode objectType)
            throws SemanticException {
        return superLang().findQualifiedTypeNode(node(), ar, outer, objectType);
    }

    @Override
    public Expr findQualifier(AmbiguityRemover ar, ClassType ct) throws SemanticException {
        return superLang().findQualifier(node(), ar, ct);
    }

    @Override
    public void typeCheckFlags(TypeChecker tc) throws SemanticException {
        superLang().typeCheckFlags(node(), tc);
    }

    @Override
    public void typeCheckNested(TypeChecker tc) throws SemanticException {
        superLang().typeCheckNested(node(), tc);
    }

    @Override
    public void printQualifier(CodeWriter w, PrettyPrinter tr) {
        superLang().printQualifier(node(), w, tr);
    }

    @Override
    public void printShortObjectType(CodeWriter w, PrettyPrinter tr) {
        superLang().printShortObjectType(node(), w, tr);
    }

    @Override
    public void printBody(CodeWriter w, PrettyPrinter tr) {
        superLang().printBody(node(), w, tr);
    }

    @Override
    public ClassType findEnclosingClass(Context c, ClassType ct) {
        return superLang().findEnclosingClass(node(), c, ct);
    }
}
