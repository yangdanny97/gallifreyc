package gallifreyc.types;

import gallifreyc.ast.RefQualification;

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

}
