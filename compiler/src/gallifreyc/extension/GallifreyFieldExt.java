package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.SharedRef;
import gallifreyc.translate.ANormalizer;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Node;

public class GallifreyFieldExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Field node() {
        return (Field) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Field f = (Field) superLang().typeCheck(node(), tc);
        GallifreyFieldInstance fi = (GallifreyFieldInstance) f.fieldInstance();
        RefQualification q = fi.gallifreyType().qualification();
        // HACK: fill in a Field Instance qualification for things like java.lang
        if (q == null) {
            q = new MoveRef(Position.COMPILER_GENERATED);
        }

        if (f.target() instanceof Expr) {
            RefQualification targetQ = GallifreyExprExt.ext(f.target()).gallifreyType().qualification();
            if (targetQ instanceof SharedRef) {
                throw new SemanticException("cannot access a field of a shared object", node().position());
            }
        }
        this.gallifreyType = new GallifreyType(q);
        return f;
    }

    @Override
    public Node aNormalize(ANormalizer rw) throws SemanticException {
        if (node().target() instanceof Expr) {
            return node().target(rw.hoist((Expr) (node().target())));
        }
        return node();
    }

}
