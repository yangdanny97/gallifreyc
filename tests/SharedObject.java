package gallifrey;

import gallifrey.Frontend;
import gallifrey.GenericKey;
import java.util.List;


// this is a stubbed shared object purely for test cases
public class SharedObject {

    Frontend frontend;
    GenericKey key;
    String rmiBackend;
    Object crdt;

    public SharedObject(Frontend frontend, Object crdt, String backend) {
        this.crdt = crdt;
        this.frontend = frontend;
        this.key = new GenericKey();
    }

    public SharedObject(Frontend frontend, GenericKey key) {
        this.frontend = frontend;
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
