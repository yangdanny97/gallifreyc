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
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class RestrictionDecl_c extends Term_c implements RestrictionDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();

    protected Id id;
    protected TypeNode forClass;
    protected RestrictionBody body;
    protected Javadoc javadoc;

    public RestrictionDecl_c(Position pos, Id id, TypeNode forClass, RestrictionBody body) {
        super(pos);
        this.id = id;
        this.forClass = forClass;
        this.body = body;
    }

    @Override
    public String toString() {
        return "restriction " + id.toString() + " for " + forClass.toString() + " " + body;
    }

    public Id id() {
        return id;
    }

    public TypeNode forClass() {
        return forClass;
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
    public boolean isDisambiguated() {
        return forClass.type() != null && forClass.type().isCanonical() && super.isDisambiguated();
    }
    
    @Override
    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        this.forClass = (TypeNode) lang().disambiguate(forClass, ar);
        return this;
    }

    @Override
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write("restriction " + id.toString() + " for " + forClass.toString() + " {");
        w.newline();
        body.prettyPrint(w, tr);
        w.newline();
        w.write("}");
        w.newline();
    }

    @Override
    public Term firstChild() {
        return forClass;
    }

    @Override
    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();
        GallifreyTypeSystem gts = (GallifreyTypeSystem) ts;
        gts.addRestrictionMapping(id.id(), forClass.name());
        
        GallifreyTypeBuilder gtb = (GallifreyTypeBuilder) tb;
        gtb.currentRestriction = id.id();
        gtb.currentRestrictionClass = forClass.name();
        
        return super.buildTypesEnter(tb);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        GallifreyTypeChecker gtc = (GallifreyTypeChecker) tc;
        
        this.forClass = (TypeNode) lang().typeCheck(forClass, gtc);
        gtc.currentRestriction = id.id();
        gtc.currentRestrictionClass = forClass.type();
        
        if (!(forClass.type() != null && forClass.type() instanceof ClassType)) {
            throw new SemanticException("Restriction " + id.id() + " for " + forClass.type() + " must be for a valid class",
                    this.position);
        }
        return super.typeCheckEnter(tc);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        body.typeCheck(tc);
        return this;
    }

    @Override
    public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
        v.visitCFG(forClass(), body(), ENTRY);
        v.visitCFG(body(), this, EXIT);
        return succs;
    }
}
