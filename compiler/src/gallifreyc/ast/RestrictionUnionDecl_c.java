package gallifreyc.ast;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.util.Position;
import polyglot.util.SerialVersionUID;

import java.util.List;

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
}
