package gallifrey;

import java.io.Serializable;
import gallifrey.frontend.SharedObject;

public abstract class Shared implements Serializable {
    public abstract SharedObject sharedObj();
} 