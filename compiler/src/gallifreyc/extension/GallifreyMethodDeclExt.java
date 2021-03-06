package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.PostCondition;
import gallifreyc.ast.PreCondition;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.RestrictionId;
import gallifreyc.ast.SharedRef;
import gallifreyc.translate.GallifreyCodegenRewriter;
import gallifreyc.types.GallifreyMethodInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.types.RegionContext;
import gallifreyc.visit.GallifreyTypeBuilder;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.Formal;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDeclOps;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

// extends method declarations to hold an optional pre/post condition, and a flag for whether it's a test method
public class GallifreyMethodDeclExt extends GallifreyExt implements GallifreyOps, ProcedureDeclOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public PreCondition pre;
    public PostCondition post;

    // The following fields only apply for test methods declared inside restrictions
    public boolean isTest;
    protected ClassType currentRestrictionClass = null;
    protected String currentRestriction;

    PreCondition pre() {
        return pre;
    }

    PostCondition post() {
        return post;
    }

    boolean isTest() {
        return isTest;
    }

    @Override
    public MethodDecl node() {
        return (MethodDecl) super.node();
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        GallifreyTypeSystem ts = gtb.typeSystem();
        if (this.isTest) {
            this.currentRestriction = gtb.currentRestriction;
            if (ts.getTestMethod(this.currentRestriction, node().name()) != null) {
                // I don't want to deal with overloading for these
                throw new SemanticException("Cannot declare 2 test methods with same name", node().position());
            }
        }
        return superLang().buildTypesEnter(node(), tb);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        MethodDecl md = (MethodDecl) superLang().buildTypes(node(), tb);
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        GallifreyTypeSystem ts = gtb.typeSystem();

        List<GallifreyType> inputTypes = new ArrayList<>();

        TypeNode rt = md.returnType();
        GallifreyType gReturn;
        if (rt instanceof RefQualifiedTypeNode) {
            gReturn = new GallifreyType(((RefQualifiedTypeNode) rt).qualification());
        } else {
            // primitive return types = return MOVE
            gReturn = new GallifreyType(new MoveRef(Position.COMPILER_GENERATED));
        }

        for (Formal f : md.formals()) {
            TypeNode t = f.type();
            if (t instanceof RefQualifiedTypeNode) {
                GallifreyType fQ = new GallifreyType(((RefQualifiedTypeNode) f.type()).qualification());
                inputTypes.add(fQ);
            } else {
                // primitive param types = take in LOCAL
                GallifreyType fQ = new GallifreyType(new LocalRef(Position.COMPILER_GENERATED));
                inputTypes.add(fQ);
            }
        }

        GallifreyMethodInstance mi = (GallifreyMethodInstance) md.methodInstance();
        mi = mi.gallifreyInputTypes(inputTypes);
        mi = mi.gallifreyReturnType(gReturn);
        mi = (GallifreyMethodInstance) mi.name(node().name());
        md = md.methodInstance(mi);

        if (isTest) {
            ts.addTestMethod(this.currentRestriction, mi, md);
        }
        return md;
    }

    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        assert (gtc.typeSystem().region_context().isEmpty());
        if (isTest) {
            this.currentRestrictionClass = gtc.currentRestrictionClass;
        }
        return superLang().typeCheckEnter(node(), tc);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        GallifreyTypeSystem ts = ((GallifreyTypeChecker) tc).typeSystem();
        ts.region_context(new RegionContext());
        MethodDecl node = node();
        // no need to ensure test methods return booleans; test method headers have no
        // place to declare return type
        if (isTest) {
            if (this.currentRestrictionClass.methodsNamed(node().name()).size() > 0) {
                throw new SemanticException("Cannot declare test method with same name as existing method",
                        node().position());
            }
            node = node.methodInstance(node.methodInstance().container(this.currentRestrictionClass));
        }
        return superLang().typeCheck(node, tc);
    }

    @Override
    public Node gallifreyRewrite(GallifreyCodegenRewriter rw) throws SemanticException {
        GallifreyMethodInstance mi = (GallifreyMethodInstance) node().methodInstance();
        GallifreyNodeFactory nf = rw.nodeFactory();
        RefQualification q = mi.gallifreyReturnType().qualification;
        if (q.isShared()) {
            SharedRef s = (SharedRef) q;
            RestrictionId rid = s.restriction();
            return node().returnType(rw.getFormalTypeNode(rid));
        }
        return node();
    }

    @Override
    public void prettyPrintHeader(Flags flags, CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintHeader(node(), flags, w, tr);
    }

    @Override
    public Context enterScope(Context c) {
        if (this.isTest) {
            return c.pushClass(null, this.currentRestrictionClass);
        }
        return superLang().enterScope(node(), c);
    }
}
