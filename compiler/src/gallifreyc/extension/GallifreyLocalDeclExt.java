package gallifreyc.extension;

import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.UnknownRef;
import gallifreyc.types.GallifreyLocalInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class GallifreyLocalDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification = new UnknownRef(Position.COMPILER_GENERATED);

    @Override
    public LocalDecl node() {
        return (LocalDecl) super.node();
    }
    
    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeNode t = node().type();
        if (t instanceof RefQualifiedTypeNode
                || (t instanceof CanonicalTypeNode && ((CanonicalTypeNode) t).type().isPrimitive())) {
            return superLang().buildTypesEnter(node(), tb);
        }
        throw new SemanticException("cannot declare unqualified local", node().position());
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        LocalDecl node = (LocalDecl) superLang().buildTypes(node(), tb);
        TypeNode t = node.type();
        RefQualification q;
        if (t instanceof RefQualifiedTypeNode) {
            q = ((RefQualifiedTypeNode) t).qualification();
        } else {
            // for primitives
            q = new LocalRef(Position.COMPILER_GENERATED);
        }
        qualification = q;
        GallifreyLocalInstance li = (GallifreyLocalInstance) node.localInstance();
        li.gallifreyType(new GallifreyType(q));
        return node;
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        LocalDecl node = (LocalDecl) superLang().typeCheck(node(), tc);
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tc.typeSystem();
        if (node.init() != null) {
            GallifreyType lt = new GallifreyType(qualification);
            GallifreyType rt = GallifreyExprExt.ext(node.init()).gallifreyType();

            if (!ts.checkQualifications(rt, lt)) {
                throw new SemanticException("cannot assign " + rt.qualification + " to " + lt.qualification,
                        node().position());
            }
        }
        return node;
    }

    public RefQualification qualification() {
        return qualification;
    }
}
