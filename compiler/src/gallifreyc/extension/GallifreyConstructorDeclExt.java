package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.translate.GRewriter;
import gallifreyc.types.GallifreyConstructorInstance;
import gallifreyc.types.GallifreyProcedureInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDeclOps;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;

public class GallifreyConstructorDeclExt extends GallifreyExt implements GallifreyOps, ProcedureDeclOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

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
    public ConstructorDecl node() {
        return (ConstructorDecl) super.node();
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        ConstructorDecl cd = (ConstructorDecl) superLang().buildTypes(this.node, tb);

        List<GallifreyType> inputTypes = new ArrayList<>();

        for (Formal f : cd.formals()) {
            if (!(f.type() instanceof RefQualifiedTypeNode) && (f.declType() == null || !f.declType().isPrimitive())) {
                throw new SemanticException("param types must be ref qualified: " + f.name(), f.position());
            }
            //TODO check this...
            GallifreyType fQ = (f.declType() == null && f.declType().isPrimitive())
                    ? new GallifreyType(new MoveRef(Position.COMPILER_GENERATED))
                    : new GallifreyType(((RefQualifiedTypeNode) f.type()).qualification());
            inputTypes.add(fQ);
        }

        GallifreyProcedureInstance ci = (GallifreyProcedureInstance) cd.constructorInstance();
        ci = ci.gallifreyInputTypes(inputTypes);

        return cd;
    }

    @Override
    public void prettyPrintHeader(Flags flags, CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintHeader(node(), flags, w, tr);
    }
}