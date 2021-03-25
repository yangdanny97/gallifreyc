package gallifrey;

import java.io.Serializable;
import gallifrey.client.SharedObject;

public interface Shared extends Serializable {
    public SharedObject sharedObj();
} 
