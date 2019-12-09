package gallifreyc.ast;

import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import gallifreyc.visit.RefQualificationAdder;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyCanonicalTypeNodeExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public CanonicalTypeNode node() {
        return (CanonicalTypeNode) super.node();
    }

    @Override
    public Node addRefQualification(RefQualificationAdder v) {
        CanonicalTypeNode n = node();
        Type t = n.type();
        if (!(t instanceof RefQualifiedType)) {
            return super.addRefQualification(v);
        }
        RefQualifiedType refQualifiedType = (RefQualifiedType) t;
        System.out.printf("-------------------------------------------\n");
        System.out.printf("Running `addRefQualification` compiler pass on RefQualifiedType %s\n", refQualifiedType);
        System.out.printf("Class of type is %s\n", refQualifiedType.getClass().getName());
        System.out.printf("-------------------------------------------\n");
//        if (e.type() != null) {
//            System.out.printf("Old type is %s\n", e.type().toString());
//        }
//        if (!(e.type() instanceof RefQualifiedType)) {
//            System.out.println("Changing type to a RefQualifiedType");
//            e.type(new RefQualifiedType_c(v.typeSystem(),
//                                                 e.position(),
//                                                 e.type(),
//                                                 new LocalRef_c(Position.COMPILER_GENERATED)));
//            if (e.type() != null) {
//                System.out.printf("New type is %s\n", e.type().toString());
//                System.out.printf("Class of new type is %s\n", e.type().getClass().getName());
//            }
//        }
        return super.addRefQualification(v);
    }
}
