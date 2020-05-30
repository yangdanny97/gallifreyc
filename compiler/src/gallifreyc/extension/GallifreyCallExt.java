package gallifreyc.extension;

import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.translate.ANormalizer;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Call;
import polyglot.ast.CallOps;
import polyglot.ast.Expr;
import polyglot.ast.Node;

public class GallifreyCallExt extends GallifreyExprExt implements CallOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Call node() {
        return (Call) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Call node = (Call) superLang().typeCheck(this.node, tc);

        if (node.target() instanceof Expr) {
            GallifreyType receiverType = GallifreyExprExt.ext(node.target()).gallifreyType();
            if (receiverType.qualification() instanceof SharedRef) {
                RestrictionId restriction = ((SharedRef) receiverType.qualification()).restriction();
                Set<String> allowedMethods = ((GallifreyTypeSystem) tc.typeSystem()).getAllowedMethods(restriction);
                if (!allowedMethods.contains(node.name())) {
                    throw new SemanticException(
                            "Cannot call method " + node.name() + " under restriction " + restriction, node.position());
                }
            }
        }

        GallifreyMethodInstance mi = (GallifreyMethodInstance) node.methodInstance();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        GallifreyType returnType = ts.checkArgs(mi, node().arguments());
        if (mi.flags().contains(Flags.STATIC)) {
            this.gallifreyType = returnType;
            // TODO revisit for default stdlib qualifications
        } else if (mi.returnType().isVoid() || mi.returnType().isPrimitive()
                || mi.gallifreyReturnType().qualification() == null) {
            // HACK: fill in qualification if the return qualification isn't present in
            // method instance
            this.gallifreyType = new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
        } else {
            this.gallifreyType = new GallifreyType(mi.gallifreyReturnType());
        }
        return node;
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
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        GallifreyNodeFactory nf = rw.nodeFactory();
        Call c = node();
        if (c.target() instanceof Expr) {
            GallifreyType t = GallifreyExprExt.ext(c.target()).gallifreyType();
            if (t.qualification() instanceof SharedRef) {
                RestrictionId restriction = ((SharedRef) t.qualification()).restriction();
                Expr newTarget = nf.Cast(node().position(),
                        nf.TypeNodeFromQualifiedName(node().position(), 
                                restriction.getInterfaceName()), (Expr) c.target());
                return c.target(newTarget);
            }
        }
        return c;
    }

    @Override
    public void printArgs(CodeWriter w, PrettyPrinter tr) {
        superLang().printArgs(node(), w, tr);
    }

    @Override
    public Type findContainer(TypeSystem ts, MethodInstance mi) {
        return superLang().findContainer(node(), ts, mi);
    }

    @Override
    public ReferenceType findTargetType() throws SemanticException {
        return superLang().findTargetType(node());
    }

    @Override
    public Node typeCheckNullTarget(TypeChecker tc, List<Type> argTypes) throws SemanticException {
        return superLang().typeCheckNullTarget(node(), tc, argTypes);
    }
}
