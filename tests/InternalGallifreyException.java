package gallifrey;

public class InternalGallifreyException extends RuntimeException {
    public InternalGallifreyException() {
        super();
    }
    
    public InternalGallifreyException(String message) {
        super(message);
    }
    
    public InternalGallifreyException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InternalGallifreyException(Throwable cause) {
        super(cause);
    }
}