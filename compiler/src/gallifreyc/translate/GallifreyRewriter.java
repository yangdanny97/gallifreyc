package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import gallifreyc.ast.*;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.extension.GallifreyFormalExt;
import gallifreyc.extension.GallifreyLang;
import gallifreyc.extension.GallifreyLocalDeclExt;
import java.util.*;

// move a-normalization to earlier pass, translations for transition and match
public class GallifreyRewriter extends GRewriter_c implements GRewriter {
    final String VALUE = "VALUE";
    final String RES = "RESTRICTION";
    final String TEMP = "TEMP";

    @Override
    public GallifreyLang lang() {
        return (GallifreyLang) super.lang();
    }

    @Override
    public GallifreyNodeFactory nodeFactory() {
        return (GallifreyNodeFactory) super.nodeFactory();
    }

    public GallifreyRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public TypeNode typeToJava(Type t, Position pos) {
        return super.typeToJava(t, pos);
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        return super.leaveCall(old, n, v);
    }

    // wrap unique/shared refs with .value, AFTER rewriting
    private Node wrapExpr(Expr e) {
        GallifreyExprExt ext = GallifreyExprExt.ext(e);
        RefQualification q;
        q = ext.gallifreyType.qualification();
        if (q instanceof SharedRef || q instanceof UniqueRef) {
            Expr new_e = qq().parseExpr("(%E)." + VALUE, e);
            return new_e;
        }
        return e;
    }

    private Node rewriteExpr(Node n) throws SemanticException {
        GallifreyNodeFactory nf = nodeFactory();

        // unwrap Moves
        if (n instanceof Move) {
            // move(a) ---> ((a.TEMP = a.value) == (a.value = null)) ? a.TEMP : a.TEMP
            Move m = (Move) n;
            Position p = n.position();
            Expr e = m.expr();
            GallifreyExprExt ext = GallifreyExprExt.ext(e);

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

            Expr condition = nf.Binary(p, fa1, Binary.EQ, fa2);
            Expr conditional = nf.Conditional(p, condition, tempField2, tempField3);
            GallifreyExprExt condExt = GallifreyExprExt.ext(conditional);
            condExt.gallifreyType(ext.gallifreyType());
            return conditional;
        }
        return n;
    }

    private Node rewriteStmt(Node n) throws SemanticException {
        if (n instanceof LocalDecl) {
            // rewrite RHS of decls
            LocalDecl l = (LocalDecl) n;
            GallifreyLocalDeclExt lde = (GallifreyLocalDeclExt) GallifreyExt.ext(l);
            Expr rhs = l.init();
            RefQualification q = lde.qualification();
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

        if (n instanceof Formal) {
            Formal f = (Formal) n;
            GallifreyFormalExt ext = (GallifreyFormalExt) GallifreyExt.ext(n);
            RefQualification q = ext.qualification;
            if (q instanceof SharedRef) {
                f = f.type(
                        nf.TypeNodeFromQualifiedName(f.position(), "Shared<" + f.type().type().toString() + ">"));
            }
            if (q instanceof UniqueRef) {
                f = f.type(
                        nf.TypeNodeFromQualifiedName(f.position(), "Unique<" + f.type().type().toString() + ">"));
            }
            return f;
        }
        
        if (n instanceof FieldDecl) {
            FieldDecl f = (FieldDecl) n;
            GallifreyFieldDeclExt fde = (GallifreyFieldDeclExt) GallifreyExt.ext(f);
            RefQualification q = fde.qualification();
            if (q instanceof SharedRef) {
                return f.type(nf.TypeNodeFromQualifiedName(f.position(), "Shared<" + f.type().type().toString() + ">"));
            }
            if (q instanceof UniqueRef) {
                return f.type(nf.TypeNodeFromQualifiedName(f.position(), "Unique<" + f.type().type().toString() + ">"));
            }
        }

        // add Unique and Shared decls
        if (n instanceof SourceFile) {
            NodeFactory nf = nodeFactory();
            SourceFile sf = (SourceFile) n;

            Import unique = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Unique");
            Import shared = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.SharedObject");
            Import frontend = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.Frontend");
            Import key = nf.Import(n.position(), Import.SINGLE_TYPE, "gallifrey.GenericKey");
            List<Import> imports = new ArrayList<>(sf.imports());
            imports.add(0, frontend);
            imports.add(0, key);
            imports.add(0, unique);
            imports.add(0, shared);
            
            // remove restriction decls
            List<TopLevelDecl> classDecls = new ArrayList<>();
            List<RestrictionDecl> restrictionDecls = new ArrayList<>();
            for (TopLevelDecl d : sf.decls()) {
                if (d instanceof ClassDecl) {
                    classDecls.add(d);
                } else {
                    restrictionDecls.add((RestrictionDecl) d);
                }
            }
            
            for (RestrictionDecl rd : restrictionDecls) {
                //TODO
            }
            
            return sf.imports(imports).decls(classDecls);
        }

        return n;
    }

    public NodeVisitor rewriteEnter(Node n) throws SemanticException {
        return n.extRewriteEnter(this);
    }
}