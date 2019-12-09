package gallifreyc.ast;

import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import gallifreyc.visit.RefQualificationAdder;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyExprExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public Expr node() {
        return (Expr) super.node();
    }

    @Override
    public Node addRefQualification(RefQualificationAdder v) {
        Expr e = node();
//        System.out.printf("-------------------------------------------\n", e.toString());
//        System.out.printf("Running `addRefQualification` compiler pass on expression %s\n", e.toString());
//        System.out.printf("Class of expr is %s\n", e.getClass().getName());
//        System.out.printf("-------------------------------------------\n", e.toString());
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
