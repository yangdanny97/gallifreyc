package gallifreyc.extension;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.TypeChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gallifreyc.ast.GallifreyNodeFactory;
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
        GallifreyNodeFactory nf = rw.nodeFactory();
        GallifreyTypeSystem ts = rw.typeSystem();
        WhenStmt node = node();
        Call c = (Call) node.expr();
        Position p = Position.COMPILER_GENERATED;
        
        String testName = "__test__" + c.name();
        List<Expr> args = new ArrayList<>(c.arguments());
        
        // add new RunAfterTest{...} argument
        List<ClassMember> members = new ArrayList<>();
        Block body;
        if (node.body() instanceof Block) {
            body = (Block) node.body();
        } else {
            List<Stmt> blockBody = new ArrayList<>();
            blockBody.add(node.body());
            body = nf.Block(p, blockBody);
        }
        members.add(nf.MethodDecl(p, Flags.NONE, nf.CanonicalTypeNode(p, ts.Void()), nf.Id("run"), 
                new ArrayList<Formal>(), new ArrayList<TypeNode>(), body, nf.Javadoc(p, "")));
        Expr rat = nf.New(p, nf.TypeNode("RunAfterTest"), new ArrayList<Expr>(), nf.ClassBody(p, members));
        
        args.add(rat);
        return nf.Eval(node.position(), nf.Call(c.position(), c.target(), 
                nf.Id(c.id().position(), testName), args));
    }
}
