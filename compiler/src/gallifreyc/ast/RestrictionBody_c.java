package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

import java.util.Iterator;
import java.util.List;

public class RestrictionBody_c extends Term_c implements RestrictionBody {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected List<Node> members;

    public RestrictionBody_c(Position pos, List<Node> members) {
        super(pos);
        this.members = members;
    }

    @Override
    public String toString() {
        return "{...}";
    }


    public List<Node> members() {
        return members;
    }
    
    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (!members.isEmpty()) {
            w.newline(4);
            w.begin(0);
            Node prev = null;

            for (Iterator<Node> i = members.iterator(); i.hasNext();) {
                Node member = i.next();
                prev = member;
                printBlock(member, w, tr);
                if (i.hasNext()) {
                    w.newline(0);
                }
            }
            w.end();
            w.newline(0);
        }
    }

    @Override
    public void translate(CodeWriter w, Translator tr) {
        //TODO
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        //TODO 
    	return this;
    }

	@Override
	public Term firstChild() {
		//intentional, I believe - same as ClassBody
		return null;
	}

	@Override
	public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
		return succs;
	}
}
