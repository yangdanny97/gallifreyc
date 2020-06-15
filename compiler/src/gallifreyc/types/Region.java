package gallifreyc.types;

public class Region {
    public String name;
    public static String DEFAULT_NAME = "default_region";
    
    public Region(String name) {
        this.name = name;
    }
    
    public Region() {
        this.name = Region.DEFAULT_NAME;
    }
}
