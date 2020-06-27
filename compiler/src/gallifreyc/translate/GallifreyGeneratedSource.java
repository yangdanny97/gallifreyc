package gallifreyc.translate;

import javax.tools.FileObject;

import polyglot.frontend.Source;
import polyglot.frontend.Source_c;

public class GallifreyGeneratedSource extends Source_c implements Source {

    public GallifreyGeneratedSource(FileObject f) {
        super(f, Source.Kind.COMPILER_GENERATED);
    }
}
