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
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.util.Copy;
import polyglot.util.ListUtil;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreySourceFileExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();
    
    @Override
    public SourceFile node() {
        return (SourceFile) super.node();
    }

    @Override
    public Node wrapSharedType(SharedTypeWrapper v) {
//        System.out.printf("In wrapSharedType for SourceFile");
//        System.out.printf("(SourceFile) node() hashcode is %d\n", node().hashCode());
//        System.out.printf("v.decls.size() = %d\n", v.decls.size());
//        for (TopLevelDecl d : v.decls) {
//            System.out.printf("Found decl %s\n", d);
//        }
        List<TopLevelDecl> curr_decls = ListUtil.copy(node().decls(), false);
        curr_decls.addAll(v.decls);
        return node().decls(curr_decls);
//        if (v.sourceFile() != null) {
//            System.out.printf("v.sourceFile() != null in wrapSharedType\n");
//            return v.sourceFile();
//        } else {
//            System.out.println("v.sourceFile() is null");
//        }
//        return super.wrapSharedType(v);
    }
}
