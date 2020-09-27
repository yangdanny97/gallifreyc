package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.ext.jl5.parse.FlagAnnotations;
import polyglot.ext.jl7.ast.JL7NodeFactory;
import polyglot.util.*;
import java.util.*;

/**
 * NodeFactory for gallifreyc extension.
 */
public interface GallifreyNodeFactory extends JL7NodeFactory {

    PreCondition PreCondition(Position pos, Expr e);

    PostCondition PostCondition(Position pos, Expr e);

    MethodDecl MethodDecl(Position pos, MethodDecl method, PreCondition pre, PostCondition post);

    UniqueRef UniqueRef(Position pos);

    MoveRef MoveRef(Position pos);

    LocalRef LocalRef(Position pos);

    LocalRef LocalRef(Position pos, String owner);

    LocalRef LocalRef(Position pos, String owner, boolean borrow);

    SharedRef SharedRef(Position pos, RestrictionId restriction);

    RefQualifiedTypeNode RefQualifiedTypeNode(Position pos, RefQualification refQualification, TypeNode t);

    RestrictionId RestrictionId(Position pos, Id rv, Id restriction, boolean wildcard);

    Transition Transition(Position pos, Expr expr, RestrictionId newRestriction);

    WhenStmt WhenStmt(Position pos, Expr expr, Stmt body);

    MatchRestriction MatchRestriction(Position pos, Expr expr, List<MatchBranch> branches);

    MatchBranch MatchBranch(Position pos, LocalDecl pattern, Block body);

    // Restrictions
    RestrictionDecl RestrictionDecl(Position pos, Id id, TypeNode for_id, RestrictionBody body);

    RestrictionBody RestrictionBody(Position Pos, List<ClassMember> members);

    AllowsStmt AllowsStmt(Position pos, Id id);

    AllowsStmt AllowsStmt(Position pos, Id id, Id contingent_id);

    AllowsStmt AllowsStmt(Position pos, Id id, Id contingent_id, boolean testOnly);

    AllowsStmt AllowsStmt(Position pos, Id id, boolean testOnly);

    MethodDecl MethodDecl(Position pos, FlagAnnotations flags, TypeNode returnType, Id name, List<Formal> formals,
            List<TypeNode> throwTypes, Block body, boolean isTest);

    RestrictionUnionDecl RestrictionUnionDecl(Position pos, Id name, List<Id> restrictions);

    MergeDecl MergeDecl(Position pos, Id method1, List<Formal> method1Formals, Id method2, List<Formal> method2Formals,
            Block body);

    // utils for compiler generated nodes to reduce verbosity
    Local Local(String name);

    Formal Formal(String type, String name);

    Field Field(Receiver object, String name);

    Id Id(String name);

    TypeNode TypeNode(String name);

    TypeNode TypeNode(Position pos, String qualifiedName);

    Block Block(List<Stmt> statements);

    Block Block(Stmt... statements);

    Call Call(Receiver target, String name, List<Expr> args);

    Call Call(Receiver target, String name, Expr... args);

    Call Call(String name, List<Expr> args);

    Call Call(String name, Expr... args);

    Cast Cast(TypeNode typenode, Expr expr);

    Special This();

    Assign Assign(Expr left, Assign.Operator op, Expr right);

    Eval Eval(Expr expr);
    
    LocalDecl LocalDecl(String type, String name, Expr init);
}
