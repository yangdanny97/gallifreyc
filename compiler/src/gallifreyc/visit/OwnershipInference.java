package gallifreyc.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class OwnershipInference  extends ContextVisitor{
	public class UnionFind<T> {
		public class Node {
			public T value;
			public Node parent;
			public int rank;
			public int size;
			
			public Node() {}
		}
		
		public Set<Node> sets;
		
		public UnionFind() {
			sets = new HashSet<>();
		}
		
		public boolean contains(Node x) {
			return false;
		}
		
		public Node makeSet(T value) {
			Node x = new Node();
			x.value = value;
			x.parent = x;
			x.rank = 0;
			x.size = 1;
			sets.add(x);
			return x;
		}
		
		public Node find(Node x) {
			while (!(x.parent.equals(x))) {
				x.parent = x.parent.parent;
				x = x.parent;
			}
			return x;
		}
		
		public void union(Node x, Node y) {
			Node xRoot = find(x);
			Node yRoot = find(y);
			if (xRoot == yRoot) return;
			if (xRoot.rank < yRoot.rank) {
				Node temp = xRoot;
				xRoot = yRoot;
				yRoot = temp;
			}
			yRoot.parent = xRoot;
			if (xRoot.rank == yRoot.rank) {
				xRoot.rank++;
			}
			
			sets.remove(yRoot);
		}
	}

	
    public OwnershipInference(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
}
