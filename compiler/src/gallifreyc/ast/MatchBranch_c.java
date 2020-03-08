package gallifreyc.ast;

import polyglot.ast.Term_c;
import polyglot.types.SemanticException;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

import java.util.List;

import polyglot.ast.LocalDecl;

public class MatchBranch_c extends Term_c implements MatchBranch {
	private LocalDecl pattern;
	private Stmt stmt;

	public MatchBranch_c(Position pos, LocalDecl pattern, Stmt stmt) {
		super(pos);
		assert pattern.init() == null;
		this.pattern = pattern;
		this.stmt = stmt;
	}
	
	public LocalDecl pattern() {
		return pattern;
	}
	
	public Stmt stmt() {
		return stmt;
	}
	
	@Override
	public String toString() {
		return "| " + pattern.toString() + " -> { " + stmt.toString() + "}"; 
	}

	@Override
	public Term firstChild() {
        if (pattern != null) return pattern;
        return null;
	}

	@Override
	public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
		v.visitCFG(pattern, stmt, ENTRY);
		v.visitCFG(stmt, this, EXIT);
		return succs;
	}
	
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        //TODO
    }

    @Override
    public void translate(CodeWriter w, Translator tr) {
        //TODO
    }
    
    @Override
    public Node visitChildren(NodeVisitor v) {
    	//TODO
        return null;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return null;
    }
	
}
