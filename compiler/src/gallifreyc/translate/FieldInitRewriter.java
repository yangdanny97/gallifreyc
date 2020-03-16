package gallifreyc.translate;

import polyglot.ast.*;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.translate.ExtensionRewriter;
import polyglot.types.Flags;
import polyglot.types.InitializerInstance;
import polyglot.types.InitializerInstance_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import java.util.*;

// hoist field inits to constructor
public class FieldInitRewriter extends ExtensionRewriter implements GRewriter {

	public FieldInitRewriter(Job job, ExtensionInfo from_ext, ExtensionInfo to_ext) {
		super(job, from_ext, to_ext);
	}

	@Override
	public Node rewrite(Node n) throws SemanticException {
		NodeFactory nf = nodeFactory();
		// remove initializers from field inits and add them to initializer block
		if (n instanceof ClassDecl) {
			ClassDecl c = (ClassDecl) n.copy();
			ClassBody b = (ClassBody) c.body().copy();
			List<ClassMember> members = new ArrayList<>(b.members());
			List<ClassMember> newMembers = new ArrayList<>();
			List<Stmt> hoistedDecls = new ArrayList<>();
			for (ClassMember member : members) {
				if (member instanceof FieldDecl) {
					FieldDecl f = (FieldDecl) member.copy();
					Position p = f.position();
					if (f.init() != null) {
						hoistedDecls.add(
								nf.Eval(p, nf.FieldAssign(p, nf.Field(p, nf.This(p), nf.Id(p, f.name())), 
						    			Assign.ASSIGN, f.init()))
						);
						newMembers.add(f);
					} else {
						newMembers.add((ClassMember) f.copy());
					}
				} else newMembers.add(member);
			}
			Initializer i = nf.Initializer(n.position(), Flags.NONE, nf.Block(n.position(), hoistedDecls));
			InitializerInstance ii = new InitializerInstance_c(from_ext.typeSystem(), n.position(), c.type(), Flags.NONE);
			i = i.initializerInstance(ii);
			newMembers.add(i);
			ClassBody newB = b.members(newMembers);
			return c.body(newB);
		}
		return n.copy();
	}

	@Override
	public NodeVisitor rewriteEnter(Node n) throws SemanticException {
		return this;
	}

}
