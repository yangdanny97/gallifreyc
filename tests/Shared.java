package gallifrey;

import java.io.Serializable;
import gallifrey.core.SharedObject;

public interface Shared extends Serializable {
    public SharedObject sharedObj();
} 