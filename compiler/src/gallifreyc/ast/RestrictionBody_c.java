package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestrictionBody_c extends Term_c implements RestrictionBody {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected List<ClassMember> members;

    public RestrictionBody_c(Position pos, List<ClassMember> members) {
        super(pos);
        this.members = members;
    }

    @Override
    public String toString() {
        return "{...}";
    }

    public List<ClassMember> restrictionMembers() {
        return members;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (!members.isEmpty()) {
            w.newline(4);
            w.begin(0);
            for (Iterator<ClassMember> i = members.iterator(); i.hasNext();) {
                Node member = i.next();
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
    public Term firstChild() {
//        for (Node member : members) {
//            if (member instanceof Term) {
//                return (Term) member;
//            }
//        }

        /* intentionally, I believe - same as ClassBody */
        return null;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
//        List<Term> terms = new ArrayList<Term>();
//        for (Node member : members) {
//            if (member instanceof Term) {
//                terms.add((Term) member);
//            }
//        }
//        v.visitCFGList(terms, this, EXIT);
        return succs;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        this.members = visitList(this.members, v);
        return this;
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        return tb.enterAnonClass();
    }

    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        return this;
    }

    @Override
    public List<ClassMember> members() {
        return this.members;
    }

    @Override
    public ClassBody members(List<ClassMember> members) {
        this.members = new ArrayList<>();
        this.members.addAll(members);
        return this;
    }

    @Override
    public ClassBody addMember(ClassMember member) {
        members.add(member);
        return this;
    }
}
