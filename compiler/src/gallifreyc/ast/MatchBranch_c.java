package gallifreyc.ast;

import polyglot.ast.Term_c;
import polyglot.types.Context;
import polyglot.types.LocalInstance_c;
import polyglot.types.SemanticException;
import polyglot.types.VarInstance;
import polyglot.ast.*;
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
    	LocalDecl pattern = visitChild(this.pattern, v);
    	Stmt stmt = visitChild(this.stmt, v);
    	MatchBranch_c mb = (MatchBranch_c) this.copy();
    	mb.pattern = pattern;
    	mb.stmt = stmt;
        return mb;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return this;
    }

	@Override
	public Context enterScope(Context c) {
		VarInstance li = pattern.varInstance();
		c.pushBlock();
		c.addVariable(li);
		return super.enterScope(c);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
    
}
