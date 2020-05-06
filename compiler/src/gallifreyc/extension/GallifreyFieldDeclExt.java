package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.ast.UnknownRef;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyFieldDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification = new UnknownRef(Position.COMPILER_GENERATED);

    @Override
    public FieldDecl node() {
        return (FieldDecl) super.node();
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeNode t = node().type();
        if (!(t instanceof RefQualifiedTypeNode)) {
            throw new SemanticException("cannot declare unqualified field");
        }
        return super.buildTypesEnter(tb);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        FieldDecl node = (FieldDecl) superLang().buildTypes(node(), tb);
        TypeNode t = node().type();
        RefQualification q;
        if (t instanceof RefQualifiedTypeNode) {
            q = ((RefQualifiedTypeNode) t).qualification();
        } else {
            // for primitives - placeholder
            q = new MoveRef(Position.COMPILER_GENERATED);
        }
        qualification = q;
        GallifreyFieldInstance fi = (GallifreyFieldInstance) node.fieldInstance();
        fi.gallifreyType(new GallifreyType(q));
        return node;
    }
    
    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        FieldDecl node = (FieldDecl) superLang().typeCheck(node(), tc);
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
