package gallifreyc.ast;

import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Term;

public interface MergeDecl extends Term {
    public Id method1();

    public MergeDecl method1(Id method1);

    public List<Formal> method1Formals();

    public MergeDecl method1Formals(List<Formal> method1Formals);

    public Id method2();

    public MergeDecl method2(Id method2);

    public List<Formal> method2Formals();

    public MergeDecl method2Formals(List<Formal> method2Formals);

    public Block body();

    public MergeDecl body(Block body);
}
