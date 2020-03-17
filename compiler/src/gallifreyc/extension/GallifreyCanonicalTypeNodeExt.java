package gallifreyc.extension;

import java.util.LinkedList;
import java.util.List;

import gallifreyc.ast.SharedRef;
import gallifreyc.types.RefQualifiedType;
import gallifreyc.types.RefQualifiedType_c;
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
        return super.wrapSharedType(v);
    }
}
