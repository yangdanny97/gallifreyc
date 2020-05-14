package gallifrey;

import gallifrey.Frontend;
import gallifrey.GenericKey;

import java.util.List;
import java.io.Serializable;


// this is a stubbed shared object purely for test cases
public class SharedObject implements Serializable {
    
    private static final long serialVersionUID = 1;

    Frontend frontend;
    GenericKey key;
    String rmiBackend;
    Object crdt;

    public SharedObject(Object crdt) {
        this.crdt = crdt;
        this.key = new GenericKey();
    }

    public SharedObject(GenericKey key) {
        this.key = key;
    }

    public GenericKey getKey(){
        return key;
    }

    public void void_call(String FunctionName, List<Object> Arguments) {
        System.out.println("void call: " + FunctionName);
    }

    public Object value(){
        return crdt;
    }

    public Object const_call(String FunctionName, List<Object> Arguments) {
        System.out.println("const call: " + FunctionName);
        return null;
    }

}
