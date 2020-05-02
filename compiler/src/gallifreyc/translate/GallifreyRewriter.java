package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyLang;
import gallifreyc.types.GallifreyTypeSystem;

import java.util.*;

// move a-normalization to earlier pass, translations for transition and match
public class GallifreyRewriter extends ExtensionRewriter implements GRewriter {
    final String VALUE = "VALUE";
    final String RES = "RESTRICTION";
    final String TEMP = "TEMP";

    @Override
    public GallifreyLang lang() {
        return (GallifreyLang) super.lang();
    }

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    // wrap unique/shared refs with .value, AFTER rewriting
    private Node wrapExpr(Expr e) {
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        RefQualification q;
        try {
            q = ext.gallifreyType.qualification();
        } catch (Exception ex) {
            System.out.println(e.toString());
            throw ex;
        }
        if (q instanceof SharedRef || q instanceof UniqueRef) {
            Expr new_e = qq().parseExpr("(%E)." + VALUE, e);
            return new_e;
        }
        return e;
    }

    private Node rewriteExpr(Node n) throws SemanticException {
        NodeFactory nf = nodeFactory();

        // unwrap Moves
        if (n instanceof Move) {
            // move(a) ---> ((a.TEMP = a.value) == (a.value = null)) ? a.TEMP : a.TEMP
            Move m = (Move) n;
            Position p = n.position();
            Expr e = m.expr();

            // HACK: re-wrap unique exprs inside of Moves
            if (e instanceof Field) {
                Field f = (Field) e;
                if (f.name().toString().equals(VALUE)) {
                    e = (Expr) f.target();
                }
            }

            Field tempField = nf.Field(p, e, nf.Id(p, TEMP));
            Field tempField2 = nf.Field(p, e, nf.Id(p, TEMP));
            Field tempField3 = nf.Field(p, e, nf.Id(p, TEMP));
            Field valueField = nf.Field(p, e, nf.Id(p, VALUE));
            Field valueField2 = nf.Field(p, e, nf.Id(p, VALUE));

            FieldAssign fa1 = nf.FieldAssign(p, tempField, Assign.ASSIGN, valueField);
            FieldAssign fa2 = nf.FieldAssign(p, valueField2, Assign.ASSIGN, nf.NullLit(p));

            Expr cond = nf.Binary(p, fa1, Binary.EQ, fa2);
            Expr conditional = nf.Conditional(p, cond, tempField2, tempField3);

            return conditional;
        }
        return n;
    }

    private Node rewriteStmt(Node n) throws SemanticException {
        if (n instanceof LocalDecl) {
            // rewrite RHS of decls
            LocalDecl l = (LocalDecl) n;
            Expr rhs = l.init();
            RefQualification q = GallifreyExprExt.ext(rhs).gallifreyType().qualification();
            if (q instanceof SharedRef) {
                SharedRef s = (SharedRef) q;
                RestrictionId rid = s.restriction();
                Expr restriction = nf.StringLit(n.position(), rid.toString());
                Expr new_rhs = qq().parseExpr("new Shared(%E, %E)", rhs, restriction);
                l = l.type(nf.TypeNodeFromQualifiedName(l.position(), "Shared<" + l.type().type().toString() + ">"));
                l = l.init(new_rhs);
                return l;
            }
            if (q instanceof UniqueRef) {
                Expr new_rhs = qq().parseExpr("new Unique(%E)", rhs);
                l = l.type(nf.TypeNodeFromQualifiedName(l.position(), "Unique<" + l.type().type().toString() + ">"));
                l = l.init(new_rhs);
                return l;
            }
            return l;
        }
        // translate Transition to java
        if (n instanceof Transition) {
            Transition t = (Transition) n;
            Position p = t.position();
            FieldAssign fa = nf.FieldAssign(p, nf.Field(p, t.expr(), nf.Id(p, RES)), Assign.ASSIGN,
                    nf.StringLit(p, t.restriction().toString()));
            return nf.Eval(p, fa);
        }
        // translate MatchRestriction to java
        if (n instanceof MatchRestriction) {
            MatchRestriction m = (MatchRestriction) n;
            Expr e = m.expr();
            Position p = m.position();
            If currentif = null;
            List<MatchBranch> branches = new ArrayList<>(m.branches());
            Collections.reverse(branches);

            for (MatchBranch b : branches) {
                LocalDecl d = b.pattern();
                RefQualifiedTypeNode t = (RefQualifiedTypeNode) d.type();
                RestrictionId restriction = ((SharedRef) t.qualification()).restriction();

                Expr field = nf.Field(p, e, nf.Id(p, RES));
                Expr cond = nf.Binary(p, field, Binary.EQ, nf.StringLit(p, restriction.restriction().toString()));

                Block block = nf.Block(p, d.init(e), b.stmt());

                if (currentif != null) {
                    If i = nf.If(p, cond, block, currentif);
                    currentif = i;
                } else {
                    If i = nf.If(p, cond, block);
                    currentif = i;
                }
            }
            return currentif;
        }
        return n;
    }

    public Node rewrite(Node n) throws SemanticException {
        if (n instanceof Expr) {
            Expr e = (Expr) rewriteExpr(n);
            return wrapExpr(e);
        }

        if (n instanceof Stmt && !(n instanceof Block)) {
            Stmt s = (Stmt) rewriteStmt(n);
            return s;
        }

        // add Unique and Shared decls
        if (n instanceof SourceFile) {
            NodeFactory nf = nodeFactory();
            SourceFile sf = (SourceFile) n;
            Import unique = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Unique");
            Import shared = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Shared");
            List<Import> imports = new ArrayList<>(sf.imports());
            imports.add(0, unique);
            imports.add(0, shared);
            return sf.imports(imports);
        }

        return n;
    }

    public NodeVisitor rewriteEnter(Node n) throws SemanticException {
        return n.extRewriteEnter(this);
    }
}