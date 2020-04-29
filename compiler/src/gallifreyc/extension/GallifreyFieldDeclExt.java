package gallifreyc.extension;

import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.translate.GRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.FieldDecl;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5FieldDeclExt;
import polyglot.translate.ExtensionRewriter;

public class GallifreyFieldDeclExt extends JL5FieldDeclExt implements GallifreyOps {
    private static final long serialVersionUID = SerialVersionUID.generate(); 
    
    @Override
    public FieldDecl node() {
    	return (FieldDecl) super.node();
    }
    
    @Override 
    public Node extRewrite(ExtensionRewriter rw) throws SemanticException {
        GRewriter crw = (GRewriter) rw;
        return crw.rewrite(node);
    }
    
    @Override 
    public NodeVisitor extRewriteEnter(ExtensionRewriter rw) throws SemanticException {
        GRewriter crw = (GRewriter) rw;
        return crw.rewriteEnter(node);
    }
    
    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
    	FieldDecl node = node();
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();

        ParsedClassType ct = tb.currentClass();

        if (ct == null) {
            return node();
        }

        Flags f = node.flags();

        if (ct.flags().isInterface()) {
            f = f.Public().Static().Final();
        }

        FieldDecl n = node;

        if (node.init() != null) {
            Flags iflags = f.isStatic() ? Flags.STATIC : Flags.NONE;
            InitializerInstance ii =
                    ts.initializerInstance(node.init().position(), ct, iflags);
            n = n.initializerInstance(ii);
        }

        // qualification
        TypeNode t = node.type();
        if (!(t instanceof RefQualifiedTypeNode)) {
        	throw new SemanticException("cannot declare unqualified field");
        }
        RefQualification q = ((RefQualifiedTypeNode) t).qualification();
        FieldInstance fi =
                ts.fieldInstance(node.position(),
                                 ct,
                                 f,
                                 ts.unknownType(node.position()),
                                 node.name(),
                                 q);
        ct.addField(fi);

        n = n.flags(f);
        n = n.fieldInstance(fi);
        return n;
    }
}

