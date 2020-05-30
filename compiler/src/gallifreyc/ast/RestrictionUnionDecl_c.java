package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gallifreyc.types.GallifreyTypeSystem;
import gallifreyc.visit.GallifreyTypeBuilder;

public class RestrictionUnionDecl_c extends Node_c implements RestrictionUnionDecl {
    private static final long serialVersionUID = SerialVersionUID.generate();
    protected Id id;
    protected List<Id> restrictions;
    protected Javadoc javadoc;

    public RestrictionUnionDecl_c(Position pos, Id id, List<Id> ids) {
        super(pos);
        this.id = id;
        this.restrictions = ids;
    }

    public Id id() {
        return id;
    }

    public List<Id> restrictions() {
        return restrictions;
    }

    @Override
    public String toString() {
        return "restriction union " + restrictions.toString();
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
        return javadoc;
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        GallifreyTypeSystem ts = (GallifreyTypeSystem) tb.typeSystem();
        List<String> restrictionClasses = new ArrayList<>();
        List<String> variants = new ArrayList<>();

        for (Id r : restrictions) {
            String forClass = ts.getClassNameForRestriction(r.id());
            if (forClass == null) {
                throw new SemanticException("Unknown restriction " + r.id(), this.position());
            }
            // requires all variant restrictions to be for the same class
            if (restrictionClasses.size() > 0 && !forClass.contentEquals(restrictionClasses.get(0))) {
                throw new SemanticException(
                        "Restriction classes in union do not match: " + forClass + ", " + restrictionClasses.get(0),
                        this.position());
            }
            if (ts.isRV(r.id())) {
                throw new SemanticException("Cannot have RV containing other RVs",  this.position());
            }
            restrictionClasses.add(0, forClass);
            variants.add(r.id());
        }
//        ts.addRestrictionMapping(id.id(), restrictionClasses.get(0));
        ts.addRV(id.id(), variants);
        return super.buildTypes(tb);
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        return this;
    }
}
