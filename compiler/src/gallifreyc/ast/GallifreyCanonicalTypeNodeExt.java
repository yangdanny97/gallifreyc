package gallifreyc.ast;

import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import gallifreyc.visit.RefQualificationAdder;
import gallifreyc.visit.SharedTypeWrapper;
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
    public Node wrapSharedType(SharedTypeWrapper v) {
        // Make sure this node is a CanonicalTypeNode whose type is a
        // shared reference.
        CanonicalTypeNode n = node();
        Type t = n.type();
        if (!(t instanceof RefQualifiedType)) {
            return super.wrapSharedType(v);
        }
        RefQualifiedType refQualifiedType = (RefQualifiedType) t;
        if (!(refQualifiedType.refQualification() instanceof SharedRef)) {
            return super.wrapSharedType(v);
        }
        
        // TODO: Create a wrapper class if the [base] type of [refQualifiedType]
        // is a reference type that hasn't had a wrapper class created for it already.
        // Probably will implement this by first setting a member in [SharedTypeWrapper]
        // to whatever node/scope (not sure yet) we can add actual classes to,
        // and will have a set of all reference types that have been wrapped so far.
        // TODO: Make sure to take into account which functions are actually "allow"ed
        // by the Restriction and only add those to the wrapper class.
        
        // TODO: Will have to create another compiler pass that replaces all uses of
        // a variable whose type is this shared reference with the wrapper class.
        System.out.printf("-------------------------------------------\n");
        System.out.printf("Running `wrapSharedType` compiler pass on RefQualifiedType %s\n", refQualifiedType);
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
        return super.wrapSharedType(v);
    }
}
