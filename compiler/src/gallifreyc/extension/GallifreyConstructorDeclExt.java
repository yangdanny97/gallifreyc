package gallifreyc.extension;

import java.util.ArrayList;
import java.util.List;

import gallifreyc.ast.LocalRef;
import gallifreyc.ast.RefQualifiedTypeNode;
import gallifreyc.types.GallifreyProcedureInstance;
import gallifreyc.types.GallifreyType;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDeclOps;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;

public class GallifreyConstructorDeclExt extends GallifreyExt implements GallifreyOps, ProcedureDeclOps {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public ConstructorDecl node() {
        return (ConstructorDecl) super.node();
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        ConstructorDecl cd = (ConstructorDecl) superLang().buildTypes(this.node, tb);

        List<GallifreyType> inputTypes = new ArrayList<>();

        for (Formal f : cd.formals()) {
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

        GallifreyProcedureInstance ci = (GallifreyProcedureInstance) cd.constructorInstance();
        ci = ci.gallifreyInputTypes(inputTypes);

        return cd;
    }

    @Override
    public void prettyPrintHeader(Flags flags, CodeWriter w, PrettyPrinter tr) {
        superLang().prettyPrintHeader(node(), flags, w, tr);
    }
}