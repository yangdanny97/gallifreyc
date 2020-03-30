package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.ext.jl5.parse.FlagAnnotations;
import polyglot.ext.jl7.ast.JL7NodeFactory;
import polyglot.types.Flags;
import polyglot.types.Package;
import polyglot.types.Type;
import polyglot.types.Qualifier;
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
    
    SharedRef SharedRef(Position pos, RestrictionId restriction);
    
    RefQualifiedTypeNode RefQualifiedTypeNode(Position pos, RefQualification refQualification, TypeNode t);
    
    RestrictionId RestrictionId(Position pos, Id rv, Id restriction, boolean wildcard);
    
    Transition Transition(Position pos, Expr expr, RestrictionId newRestriction);
    
    Move Move(Position pos, Expr expr);
    
    MatchRestriction MatchRestriction(Position pos, Expr expr, List<MatchBranch> branches);
    
    MatchBranch MatchBranch(Position pos, LocalDecl pattern, Stmt stmt);
    
    // Restrictions
    RestrictionDecl RestrictionDecl(Position pos, Id id, Id for_id, RestrictionBody body);
    
    RestrictionBody RestrictionBody(Position Pos, List<Node> members);
    
    AllowsStmt AllowsStmt(Position pos, Id id);
    
    AllowsStmt AllowsStmt(Position pos, Id id, Id contingent_id);
    
    MethodDecl MethodDecl(Position pos, FlagAnnotations flags, TypeNode returnType,
                          Id name, List<Formal> formals, List<TypeNode> throwTypes,
                          Block body, boolean isTest);
    
    RestrictionUnionDecl RestrictionUnionDecl(Position pos, Id name, List<Id> restrictions);
}
