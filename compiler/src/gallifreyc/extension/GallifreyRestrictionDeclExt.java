package gallifreyc.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RestrictionDecl;
import gallifreyc.translate.GallifreyRewriter;
import gallifreyc.types.GallifreyTypeSystem;
import polyglot.ast.Assign;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.AnnotationElem;
import polyglot.ext.jl5.ast.ParamTypeNode;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

public class GallifreyRestrictionDeclExt extends GallifreyExt {

    private static final long serialVersionUID = SerialVersionUID.generate();

    public GallifreyRestrictionDeclExt() {
    }

    @Override
    public RestrictionDecl node() {
        return (RestrictionDecl) super.node();
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        rw.genRestrictionInterface(node());
        return rw.genRestrictionImplClass(node());
    }

}
