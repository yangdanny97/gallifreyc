package gallifreyc.types;

import gallifreyc.ast.RefQualification;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.types.*;
import polyglot.util.Position;

public interface GallifreyTypeSystem extends JL7TypeSystem {
    // TODO: declare any new methods needed
    RefQualifiedType refQualifiedTypeOf(Position pos, Type base, RefQualification q);
}
