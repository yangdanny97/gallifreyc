package gallifreyc.ast;

import java.util.LinkedList;
import java.util.List;

import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
import gallifreyc.visit.RefQualificationAdder;
import gallifreyc.visit.SharedTypeWrapper;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassBody_c;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Id_c;
import polyglot.ast.Node;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Copy;
import polyglot.util.ListUtil;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyCanonicalTypeNodeExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public CanonicalTypeNode node() {
        return (CanonicalTypeNode) super.node();
    }

    /**
     * Suppose we are given a class as follows:
     * class Foo {
     *   Foo(<args>)
     *   
     *   void func1(<args>) {
     *     ...
     *   }
     *   
     *   void func2(<args>) {
     *     ...
     *   }
     * }
     * 
     * TODO: Not sure what to do about constructors
     * Generate wrapper classes that represents the shared version of this:
     * class FrontendFoo {
     *   Key name;
     *   void func1(<args>) {
     *     s = serialize(<args>);
     *     Andidote.send(name, <func1Id>, s); 
     *   }
     *   
     *   void func2(<args>) {
     *     s = serialize(<args>);
     *     Andidote.send(name, <func2Id>, s); 
     *   }
     * }
     * 
     * class BackendFoo {
     *   private Foo foo;
     *   void invoke(int methodId, byte[] args) {
     *     switch(methodId) {
     *       case <func1Id>:
     *         foo.func1(unpack(args));
     *         return;
     *       case <func2Id>:
     *         foo.func2(unpack(args));
     *         return;
     *       default:
     *         throw Exception(...);
     *     }
     *   }
     * }
     */
    @Override
    public Node wrapSharedType(SharedTypeWrapper v) {
//        // Make sure v.sourceFile is not null
//        if (v.sourceFile() == null) {
//            System.out.println("(wrapSharedType) v.sourceFile() is null");
//            return super.wrapSharedType(v);
//        }
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
        if (!(refQualifiedType.base() instanceof ReferenceType)) {
            return super.wrapSharedType(v);
        }
        
        System.out.printf("-------------------------------------------\n");
        System.out.printf("Running `wrapSharedType` compiler pass on RefQualifiedType %s\n", refQualifiedType);
        System.out.printf("Class of type is %s\n", refQualifiedType.getClass().getName());
        
        ReferenceType base = (ReferenceType) refQualifiedType.base();
        if (v.typesVisited().contains(base)) {
            return super.wrapSharedType(v);
        }
        v.typesVisited().add(base);

        
        // Create the body of the class
        ClassBody wrappedClassBody =
                v.nodeFactory().ClassBody(Position.COMPILER_GENERATED,
                                          new LinkedList<ClassMember>());

        System.out.printf("Base type name is %s\n", base.toString());
        
        // Create the class
        ClassDecl wrappedClassDecl =
                v.nodeFactory().ClassDecl(Position.COMPILER_GENERATED,
                                          Flags.NONE,
                                          v.nodeFactory().Id(Position.COMPILER_GENERATED,
                                                             "Shared" + base.toString().replace('.', '_')),
                                          null, // superclass
                                          new LinkedList<TypeNode>(),
                                          wrappedClassBody);
//        List<TopLevelDecl> newDecls = ListUtil.copy(v.sourceFile().decls(), false);
//        newDecls.add(wrappedClassDecl);
//        System.out.printf("v.source_file hashCode is %d\n", v.sourceFile().hashCode());
//        v.sourceFile().decls(newDecls);
//        System.out.printf("v.source_file hashCode is %d\n", v.sourceFile().hashCode());
//        
//        wrappedClassDecl.buildTypes()
//        ParsedClassType ct = v.typeSystem().createClassType();
//        ct
//        System.out.printf("ct %s\n", ct);
//        ct.kind(ClassType.TOP_LEVEL);
//        wrappedClassDecl = wrappedClassDecl.type(ct);
//        System.out.printf("wrappedClassDecl type is %s\n", wrappedClassDecl.type());
        v.decls.add(wrappedClassDecl);
//        for (TopLevelDecl d : v.decls) {
//            System.out.printf("Found decl %s\n", d);
//        }
        System.out.printf("-------------------------------------------\n");
        
        // TODO: Make sure to take into account which functions are actually "allow"ed
        // by the Restriction and only add those to the wrapper class.
        
        // TODO: Will have to create another compiler pass that replaces all uses of
        // a variable whose type is this shared reference with the wrapper class.
        
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
