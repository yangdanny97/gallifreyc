package gallifrey;

public class Shared<T> {
	public T VALUE;
	public String RESTRICTION;
	
	public Shared(T VALUE, String RESTRICTION) {
		this.VALUE = VALUE;
		this.RESTRICTION = RESTRICTION;
	}
} 