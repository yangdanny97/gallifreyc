package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

import java.util.Set;

import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.WhenStmt;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;

public class GallifreyWhenStmtExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    // NO a-normalization of condition expr

    @Override
    public WhenStmt node() {
        return (WhenStmt) super.node();
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        WhenStmt node = node();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        if (!ts.typeEquals(node.expr().type(), ts.Boolean())) {
            throw new SemanticException("Expected boolean condition", node.expr().position());
        }
        if (!(node.expr() instanceof Call)) {
            throw new SemanticException("Expected method call", node.expr().position());
        }
        Call c = (Call) node.expr();
        Receiver r = c.target();
        if (r == null || !(r instanceof Expr)) {
            throw new SemanticException("Expected shared object", r.position());
        }
        GallifreyExprExt ext = GallifreyExprExt.ext(r);
        if (!ext.gallifreyType().isShared()) {
            throw new SemanticException("Expected shared object", r.position());
        }
        RestrictionId restriction = ((SharedRef) ext.gallifreyType().qualification).restriction();
        Set<String> allowedMethods = ts.getAllowedTestMethods(restriction);
        if (!(allowedMethods.contains(c.name()) || ts.getTestMethod(restriction, c.name()) != null)) {
            throw new SemanticException("Cannot use " + c.name() + " under restriction " + restriction,
                    node.expr().position());
        }
        return superLang().typeCheck(node, tc);
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        throw new InternalCompilerError("unimplemented");
        // TODO
    }
}
