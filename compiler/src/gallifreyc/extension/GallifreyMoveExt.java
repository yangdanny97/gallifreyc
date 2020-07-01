package gallifreyc.extension;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.Move;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyType;
import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

public class GallifreyMoveExt extends GallifreyExprExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public Move node() {
        return (Move) super.node();
    }

    public GallifreyMoveExt() {
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Move node = (Move) superLang().typeCheck(node(), tc);
        GallifreyExprExt ext = GallifreyExprExt.ext(node.expr());
        tc.nodeFactory();
        RefQualification q = ext.gallifreyType.qualification();
        if (q.isUnique()) {
            ext.gallifreyType.qualification = new MoveRef(q.position());
            this.gallifreyType = new GallifreyType(new MoveRef(q.position()));
            return node;
        }
        throw new SemanticException("cannot move non-unique!", node.position());
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        GallifreyNodeFactory nf = rw.nodeFactory();
        // move(a) ---> ((a.TEMP = a.value) == (a.value = null)) ? a.TEMP : a.TEMP
        Move m = node();
        Position p = node().position();
        Expr e = m.expr();
        GallifreyExprExt ext = GallifreyExprExt.ext(e);

        // HACK: re-wrap unique exprs inside of Moves
        if (e instanceof Field) {
            Field f = (Field) e;
            if (f.name().toString().equals(rw.VALUE)) {
                e = (Expr) f.target();
            }
        }

        Field tempField = nf.Field(e, rw.TEMP);
        Field tempField2 = nf.Field(e, rw.TEMP);
        Field tempField3 = nf.Field(e, rw.TEMP);
        Field valueField = nf.Field(e, rw.VALUE);
        Field valueField2 = nf.Field(e, rw.VALUE);

        FieldAssign fa1 = nf.FieldAssign(p, tempField, Assign.ASSIGN, valueField);
        FieldAssign fa2 = nf.FieldAssign(p, valueField2, Assign.ASSIGN, nf.NullLit(p));

        Expr condition = nf.Binary(p, fa1, Binary.EQ, fa2);
        Expr conditional = nf.Conditional(p, condition, tempField2, tempField3);
        GallifreyExprExt condExt = GallifreyExprExt.ext(conditional);
        condExt.gallifreyType(ext.gallifreyType());
        return conditional;
    }
}
