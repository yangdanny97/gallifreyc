package gallifreyc.extension;

import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;

public class GallifreyFieldDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification;

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
        TypeNode t = node.type();
        RefQualification q = ((RefQualifiedTypeNode) t).qualification();
        qualification = q;
        
        GallifreyFieldInstance fi = (GallifreyFieldInstance) node.fieldInstance();
        fi.gallifreyType(new GallifreyType(q));
        return node;
    }

    public RefQualification qualification() {
        return qualification;
    }
}
