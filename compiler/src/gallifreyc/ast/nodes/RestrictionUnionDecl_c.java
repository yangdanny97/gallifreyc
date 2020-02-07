package gallifreyc.ast.nodes;

import polyglot.ast.*;
import polyglot.types.Flags;
import polyglot.util.Position;
import java.util.List;

public class RestrictionUnionDecl_c extends Node_c implements RestrictionUnionDecl {
	protected Id id;
	protected List<Id> restrictions;
	
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
    	return null; //TODO
    }

    public String name() {
    	return id.id();
    }
    
    public Documentable javadoc(Javadoc javadoc) {
    	return null; //TODO
    }

    public Javadoc javadoc() {
    	return null; //TODO
    }
}
