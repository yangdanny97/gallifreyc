package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.FieldInstance;
import polyglot.types.FieldInstance_c;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.InitializerInstance_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import java.util.*;

import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.extension.GallifreyExt;
import gallifreyc.extension.GallifreyFieldDeclExt;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;

//move field initializers into an initializer block
public class FieldInitRewriter extends ExtensionRewriter implements GRewriter {

    public FieldInitRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
        super(job, from_ext, to_ext);
    }

    @Override
    public Node rewrite(Node n) throws SemanticException {
        NodeFactory nf = nodeFactory();
        if (n instanceof ClassDecl) {
            ClassDecl c = (ClassDecl) n.copy();
            ClassBody b = (ClassBody) c.body().copy();
            List<ClassMember> members = new ArrayList<>(b.members());
            List<ClassMember> newMembers = new ArrayList<>();
            List<Stmt> hoistedDecls = new ArrayList<>();
            for (ClassMember member : members) {
                if (member instanceof FieldDecl) {
                    FieldDecl f = (FieldDecl) member.copy();
                    GallifreyFieldDeclExt fde = (GallifreyFieldDeclExt) GallifreyExt.ext(f);
                    Position p = f.position();
                    if (f.init() != null) {
                        Field field = nf.Field(p, nf.This(p), nf.Id(p, f.name()));
                        GallifreyFieldInstance fi = (GallifreyFieldInstance) typeSystem().fieldInstance(p, c.type(),
                                Flags.NONE, f.init().type(), f.name());
                        RefQualification q = fde.qualification();
                        fi.gallifreyType(new GallifreyType(q));
                        field = field.fieldInstance(fi);
                        hoistedDecls.add(nf.Eval(p, nf.FieldAssign(p, field, Assign.ASSIGN, f.init())));
                        // remove inits
                        newMembers.add(f.init(null));
                    } else {
                        newMembers.add((ClassMember) f.copy());
                    }
                } else
                    newMembers.add(member);
            }
            Initializer i = nf.Initializer(n.position(), Flags.NONE, nf.Block(n.position(), hoistedDecls));
            InitializerInstance ii = typeSystem().initializerInstance(n.position(), c.type(), Flags.NONE);
            i = i.initializerInstance(ii);
            newMembers.add(i);
            ClassBody newB = b.members(newMembers);
            return c.body(newB);
        }
        return n.copy();
    }

    @Override
    public NodeVisitor rewriteEnter(Node n) throws SemanticException {
        return this;
    }

}
