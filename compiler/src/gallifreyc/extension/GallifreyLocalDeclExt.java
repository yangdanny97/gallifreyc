package gallifreyc.extension;

import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyFieldInstance;
import gallifreyc.types.GallifreyLocalInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

public class GallifreyLocalDeclExt extends GallifreyExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public RefQualification qualification;

    @Override
    public LocalDecl node() {
        return (LocalDecl) super.node();
    }
    
    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeNode t = node().type();
        if (!(t instanceof RefQualifiedTypeNode)) {
            throw new SemanticException("cannot declare unqualified local");
        }
        return super.buildTypesEnter(tb);
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        LocalDecl node = (LocalDecl) superLang().buildTypes(node(), tb);
        TypeNode t = node.type();
        RefQualification q = ((RefQualifiedTypeNode) t).qualification();
        qualification = q;
        
        GallifreyLocalInstance li = (GallifreyLocalInstance) node.localInstance();
        li.gallifreyType(new GallifreyType(q));
        return node;
    }

    public RefQualification qualification() {
        return qualification;
    }
}
