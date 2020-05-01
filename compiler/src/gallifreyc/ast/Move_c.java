package gallifreyc.ast;

import java.util.List;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.types.GallifreyType;

public class Move_c extends Expr_c implements Move {
    private static final long serialVersionUID = SerialVersionUID.generate();
    Expr expr;

    public Move_c(Position pos, Expr expr) {
        super(pos);
        this.expr = expr;
    }

    public Expr expr() {
        return expr;
    }

    @Override
    public Term firstChild() {
        if (expr != null)
            return expr;
        return null;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(expr, this, EXIT);
        return succs;
    }

    @Override
    public String toString() {
        return "move(" + expr.toString() + ")";
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write(this.toString());
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        Expr expr = visitChild(this.expr, v);
        Move_c n = copyIfNeeded(this);
        n.expr = expr;
        return n;
    }

    @Override
    public Node copy(NodeFactory nf) {
        return ((GallifreyNodeFactory) nf).Move(this.position, (Expr) this.expr.copy(nf));
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        GallifreyExprExt ext = GallifreyExprExt.ext(this.expr);
        tc.nodeFactory();
        RefQualification q = ext.gallifreyType.qualification();
        if (q instanceof UniqueRef) {
            ext.gallifreyType.qualification = new MoveRef(q.position());
            GallifreyExprExt thisExt = GallifreyExprExt.ext(this);
            thisExt.gallifreyType = new GallifreyType(new MoveRef(q.position()));
            return type(this.expr.type());
        }
        throw new SemanticException("cannot move non-unique!", this.position());
    }
}
