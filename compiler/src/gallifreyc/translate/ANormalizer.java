package gallifreyc.translate;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyLocalDeclExt;
import gallifreyc.types.GallifreyLocalInstance;
import polyglot.ast.Expr;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class ANormalizer extends GRewriter {
    public List<Stmt> hoisted;

    public ANormalizer(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
        hoisted = new ArrayList<>();
    }

    @Override
    public NodeVisitor enterCall(Node n) throws SemanticException {
        ANormalizer v = (ANormalizer) super.enterCall(n);
        v.hoisted = new ArrayList<>();
        return v;
    }

    @Override
    public Node extRewrite(Node n) throws SemanticException {
        return GallifreyExt.ext(n).aNormalize(this);
    }

    // hoist an expression e and replace it with a fresh temp
    public Expr hoist(Expr e) {
        // variables and literals are safe
        if (e instanceof NamedVariable || e instanceof Lit || e instanceof Special)
            return e;

        // hoist everything else
        GallifreyNodeFactory nf = nodeFactory();
        Position p = e.position();
        String fresh = lang().freshVar();

        // hoisted local decl
        LocalDecl l = nf.LocalDecl(p, Flags.NONE, nf.CanonicalTypeNode(p, e.type()), nf.Id(fresh), e);

        // transfer qualification
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        GallifreyLocalInstance li = (GallifreyLocalInstance) typeSystem().localInstance(e.position(), Flags.NONE,
                e.type(), fresh);
        li = li.gallifreyType(ext.gallifreyType());
        l = l.localInstance(li);

        GallifreyLocalDeclExt lde = (GallifreyLocalDeclExt) GallifreyExt.ext(l);
        lde.qualification = ext.gallifreyType().qualification;

        hoisted.add(l);

        // new local variable to replace original expr
        Local newLocal = nf.Local(fresh);
        newLocal = newLocal.localInstance(li);
        GallifreyExprExt localExt = GallifreyExprExt.ext(newLocal);
        localExt.gallifreyType = ext.gallifreyType();
        return newLocal;
    }

    // replace statements with blocks of hoisted decls (if any) + original statement
    public Stmt addHoistedDecls(Stmt s) {
        if (hoisted.size() > 0) {
            List<Stmt> blockBody = new ArrayList<>(hoisted);
            blockBody.add(s);
            hoisted = new ArrayList<>();
            return nf.Block(s.position(), blockBody);
        }
        return s;
    }
}
