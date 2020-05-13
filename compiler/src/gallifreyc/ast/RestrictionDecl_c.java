package gallifreyc.ast;

import java.util.List;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;
import gallifreyc.visit.GallifreyTypeChecker;
import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class RestrictionDecl_c extends Term_c implements RestrictionDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected Id for_id;
    protected RestrictionBody body;
    protected Javadoc javadoc;

    public RestrictionDecl_c(Position pos, Id id, Id for_id, RestrictionBody body) {
        super(pos);
        this.id = id;
        this.for_id = for_id;
        this.body = body;
    }

    @Override
    public String toString() {
        return "restriction " + id.toString() + " for " + for_id.toString() + " " + body;
    }

    public Id id() {
        return id;
    }

    public Id for_id() {
        return for_id;
    }

    public RestrictionBody body() {
        return body;
    }

    /** From TopLevelDecl */
    public Flags flags() {
        return Flags.NONE;
    }

    public String name() {
        return id.id();
    }

    public Documentable javadoc(Javadoc javadoc) {
        this.javadoc = javadoc;
        return this;
    }

    public Javadoc javadoc() {
        return this.javadoc;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("restriction " + id.toString() + " for " + for_id.toString() + " {");
        w.newline();
        body.prettyPrint(w, tr);
        w.newline();
        w.write("}");
        w.newline();
    }

    @Override
    public Term firstChild() {
        return body;
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();
        GallifreyTypeSystem gts = (GallifreyTypeSystem) ts;
        gts.addRestrictionMapping(id.id(), for_id.id());
        
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        gtb.currentRestriction = id.id();
        gtb.currentRestrictionClass = for_id.id();
        
        return super.buildTypesEnter(tb);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        gtc.currentRestriction = id.id();
        gtc.currentRestrictionClass = for_id.id();
        return super.typeCheckEnter(tc);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        try {
            if (!(ts.typeForName(for_id.id()) instanceof ClassType)) {
                throw new SemanticException("Restriction " + for_id.id() + " must be for a valid class",
                        this.position);
            }
        } catch (SemanticException e) {
            //TODO
            System.out.println(gtc.currentRestrictionClass + "|" + for_id.id());
            System.out.println("exn in R DECL");
        }
        body.typeCheck(tc);
        return this;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(this.body(), this, EXIT);
        return succs;
    }
}
