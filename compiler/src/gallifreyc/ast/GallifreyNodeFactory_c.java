package gallifreyc.ast;

import polyglot.ext.jl5.ast.ParamTypeNode;
import polyglot.ast.*;
import gallifreyc.extension.*;
import polyglot.ext.jl5.parse.FlagAnnotations;
import polyglot.ext.jl7.ast.JL7NodeFactory_c;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for gallifreyc extension.
 */
public class GallifreyNodeFactory_c extends JL7NodeFactory_c implements GallifreyNodeFactory {
    public GallifreyNodeFactory_c(GallifreyLang lang, GallifreyExtFactory extFactory) {
        super(lang, extFactory);
    }

    @Override
    public GallifreyExtFactory extFactory() {
        return (GallifreyExtFactory) super.extFactory();
    }

    @Override
    public PreCondition PreCondition(Position pos, Expr e) {
        PreCondition p = new PreCondition_c(pos, e);
        p = ext(p, extFactory().extPreCondition());
        return p;
    }

    @Override
    public PostCondition PostCondition(Position pos, Expr e) {
        PostCondition_c p = new PostCondition_c(pos, e);
        p = ext(p, extFactory().extPostCondition());
        return p;
    }

    @Override
    public MethodDecl MethodDecl(Position pos, MethodDecl method, PreCondition pre, PostCondition post) {
        GallifreyMethodDeclExt ext = (GallifreyMethodDeclExt) GallifreyExt.ext(method);
        ext.pre = pre;
        ext.post = post;
        ext.isTest = false;
        return method;
    }

    @Override
    public UniqueRef UniqueRef(Position pos) {
        UniqueRef u = new UniqueRef(pos);
        u = ext(u, extFactory().extUniqueRef());
        return u;
    }

    @Override
    public MoveRef MoveRef(Position pos) {
        MoveRef m = new MoveRef(pos);
        m = ext(m, extFactory().extMoveRef());
        return m;
    }

    @Override
    public LocalRef LocalRef(Position pos) {
        LocalRef m = new LocalRef(pos);
        m = ext(m, extFactory().extLocalRef());
        return m;
    }
    
    @Override
    public LocalRef LocalRef(Position pos, String owner) {
        LocalRef m = new LocalRef(pos, owner);
        m = ext(m, extFactory().extLocalRef());
        return m;
    }

    @Override
    public SharedRef SharedRef(Position pos, RestrictionId restriction) {
        SharedRef s = new SharedRef(pos, restriction);
        s = ext(s, extFactory().extSharedRef());
        return s;
    }

    @Override
    public RestrictionId RestrictionId(Position pos, Id rv, Id restriction, boolean wildcard) {
        RestrictionId i = new RestrictionId_c(pos, rv, restriction, wildcard);
        i = ext(i, extFactory().extRestrictionId());
        return i;
    }

    @Override
    public RestrictionDecl RestrictionDecl(Position pos, Id id, Id for_id, RestrictionBody body) {
        RestrictionDecl d = new RestrictionDecl_c(pos, id, for_id, body);
        d = ext(d, extFactory().extRestrictionDecl());
        return d;
    }

    @Override
    public RestrictionBody RestrictionBody(Position pos, List<Node> members) {
        RestrictionBody b = new RestrictionBody_c(pos, members);
        b = ext(b, extFactory().extRestrictionBody());
        return b;
    }

    @Override
    public AllowsStmt AllowsStmt(Position pos, Id id) {
        AllowsStmt s = new AllowsStmt_c(pos, id, null);
        s = ext(s, extFactory().extAllowsStmt());
        return s;
    }

    @Override
    public AllowsStmt AllowsStmt(Position pos, Id id, Id contingent_id) {
        AllowsStmt s = new AllowsStmt_c(pos, id, contingent_id);
        s = ext(s, extFactory().extAllowsStmt());
        return s;
    }

    @Override
    public MethodDecl MethodDecl(Position pos, FlagAnnotations flags, TypeNode returnType, Id name,
            List<Formal> formals, List<TypeNode> throwTypes, Block body, boolean isTest) {
        MethodDecl n = super.MethodDecl(pos, flags.flags(), flags.annotations(), returnType, name, formals, throwTypes,
                body, new LinkedList<ParamTypeNode>(), Javadoc(pos, ""));
        GallifreyMethodDeclExt ext = (GallifreyMethodDeclExt) GallifreyExt.ext(n);
        ext.isTest = isTest;
        return n;
    }

    @Override
    public RefQualifiedTypeNode RefQualifiedTypeNode(Position pos, RefQualification refQualification, TypeNode t) {
        RefQualifiedTypeNode n = new RefQualifiedTypeNode_c(pos, refQualification, t);
        n = ext(n, extFactory().extRefQualifiedTypeNode());
        return n;
    }

    @Override
    public RestrictionUnionDecl RestrictionUnionDecl(Position pos, Id name, List<Id> restrictions) {
        RestrictionUnionDecl d = new RestrictionUnionDecl_c(pos, name, restrictions);
        d = ext(d, extFactory().extRestrictionUnionDecl());
        return d;
    }

    @Override
    public Transition Transition(Position pos, Expr expr, RestrictionId newRestriction) {
        Transition t = new Transition_c(pos, expr, newRestriction);
        t = ext(t, extFactory().extTransition());
        return t;
    }

    @Override
    public MatchRestriction MatchRestriction(Position pos, Expr expr, List<MatchBranch> branches) {
        MatchRestriction m = new MatchRestriction_c(pos, expr, branches);
        m = ext(m, extFactory().extMatchRestriction());
        return m;
    }

    @Override
    public MatchBranch MatchBranch(Position pos, LocalDecl pattern, Stmt stmt) {
        MatchBranch b = new MatchBranch_c(pos, pattern, stmt);
        b = ext(b, extFactory().extMatchBranch());
        return b;
    }

    @Override
    public Move Move(Position pos, Expr expr) {
        Move m = new Move_c(pos, expr);
        m = ext(m, extFactory().extMove());
        return m;
    }
}
