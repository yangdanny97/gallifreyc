package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import java.util.*;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RefQualification;
import gallifreyc.extension.GallifreyExprExt;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;

//move field initializers into an initializer block
public class FieldInitRewriter extends GRewriter_c implements GRewriter {

    public FieldInitRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }
    
    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        return super.leaveCall(old, n, v);
    }

    @Override
    public Node rewrite(Node n) throws SemanticException {
        GallifreyNodeFactory nf = nodeFactory();
        
        if (n instanceof ClassDecl && !((ClassDecl) n).flags().isInterface()) {
            ClassDecl c = (ClassDecl) n.copy();
            ClassBody b = (ClassBody) c.body().copy();
            List<ClassMember> members = new ArrayList<>(b.members());
            List<ClassMember> newMembers = new ArrayList<>();
            List<Stmt> hoistedDecls = new ArrayList<>();
            for (ClassMember member : members) {
                if (member instanceof FieldDecl) {
                    FieldDecl f = (FieldDecl) member;
                    RefQualification q = ((GallifreyFieldDeclExt) GallifreyExt.ext(f)).qualification();
                    Position p = f.position();
                    // only hoist field decls with inits
                    if (f.init() != null) {
                        // new assignment LHS
                        Field field = nf.Field(p, nf.This(p), nf.Id(p, f.name()));
                        GallifreyExprExt thisExt = GallifreyExprExt.ext(field);
                        thisExt.gallifreyType(new GallifreyType(q));
                        GallifreyFieldInstance fi = (GallifreyFieldInstance) typeSystem().fieldInstance(p, c.type(),
                                Flags.NONE, f.init().type(), f.name());
                        fi.gallifreyType(new GallifreyType(q));
                        field = field.fieldInstance(fi);
                        // add assignment
                        FieldAssign fa = nf.FieldAssign(p, field, Assign.ASSIGN, f.init());
                        GallifreyExprExt faExt = GallifreyExprExt.ext(fa);
                        faExt.gallifreyType(new GallifreyType(q));
                        hoistedDecls.add(nf.Eval(p, fa));
                        // remove inits
                        member = f.init(null);
                        GallifreyFieldDeclExt ext = (GallifreyFieldDeclExt) GallifreyExt.ext(member);
                        ext.qualification = q;
                    }
                }
                newMembers.add(member);
            }
            if (hoistedDecls.size() > 0) {
                Initializer i = nf.Initializer(n.position(), Flags.NONE, nf.Block(n.position(), hoistedDecls));
                InitializerInstance ii = typeSystem().initializerInstance(n.position(), c.type(), Flags.NONE);
                i = i.initializerInstance(ii);
                newMembers.add(i);
                ClassBody newB = b.members(newMembers);
                return c.body(newB);
            }
            return c;
        }
        return n;
    }

    @Override
    public NodeVisitor rewriteEnter(Node n) throws SemanticException {
        return this;
    }

}
