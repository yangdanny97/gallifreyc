package gallifreyc.types;

import gallifreyc.ast.LocalRef;
import gallifreyc.ast.MoveRef;
import gallifreyc.ast.RefQualification;
import gallifreyc.ast.SharedRef;
import gallifreyc.ast.UniqueRef;

public class GallifreyType {
	public RefQualification qualification;
    public String capability;
    public String path;

	public GallifreyType(RefQualification q) {
		this.qualification = q;
	}
	
	public RefQualification qualification() {
		return qualification;
	}

	public GallifreyType qualification(RefQualification qualification) {
		this.qualification = qualification;
		return this;
	}

	public String capability() {
		return capability;
	}

	public GallifreyType capability(String capability) {
		this.capability = capability;
		return this;
	}

	public String path() {
		return path;
	}

	public GallifreyType path(String path) {
		this.path = path;
		return this;
	}
	
    public boolean isMove() {
    	return qualification instanceof MoveRef;
    }
    
    public boolean isUnique() {
    	return qualification instanceof UniqueRef;
    }
    
    public boolean isShared() {
    	return qualification instanceof SharedRef;
    }
    
    public boolean isLocal() {
    	return qualification instanceof LocalRef;
    }


}
