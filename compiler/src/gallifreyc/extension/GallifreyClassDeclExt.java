package gallifreyc.extension;

import polyglot.ast.ClassDeclOps;
import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.types.ConstructorInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.SerialVersionUID;
import polyglot.visit.PrettyPrinter;

public class GallifreyClassDeclExt extends GallifreyExt implements ClassDeclOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyClassDeclExt() {
    }

    @Override
    public ClassDecl node() {
        return (ClassDecl) super.node();
    }

    @Override
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintHeader(node(), w, tr);
    }

    @Override
    public void prettyPrintFooter(CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintFooter(node(), w, tr);
    }

    @Override
    public Node addDefaultConstructor(TypeSystem ts, NodeFactory nf, ConstructorInstance defaultConstructorInstance)
            throws SemanticException {
        return superLang().addDefaultConstructor(node(), ts, nf, defaultConstructorInstance);
    }
}
