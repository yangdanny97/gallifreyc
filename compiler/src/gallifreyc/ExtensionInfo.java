package gallifreyc;

import polyglot.lex.Lexer;
import gallifreyc.parse.Lexer_c;
import gallifreyc.parse.Grm;
import gallifreyc.ast.*;
import gallifreyc.types.*;
import polyglot.ast.*;
import polyglot.ext.jl5.ast.JL5ExtFactory_c;
import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.ext.jl7.ast.JL7ExtFactory_c;
import polyglot.frontend.*;
import polyglot.main.*;
import polyglot.types.*;
import polyglot.util.*;

import java.io.*;
import java.util.Set;

/**
 * Extension information for gallifreyc extension.
 */
public class ExtensionInfo extends JL7ExtensionInfo {
    static {
        // force Topics to load
        @SuppressWarnings("unused")
        Topics t = new Topics();
    }

    @Override
    public String defaultFileExtension() {
        return "gal";
    }

    @Override
    public String compilerName() {
        return "gallifreycc";
    }

    @Override
    public Parser parser(Reader reader, Source source, ErrorQueue eq) {
        Lexer lexer = new Lexer_c(reader, source, eq);
        Grm grm = new Grm(lexer, ts, nf, eq);
        return new CupParser(grm, source, eq);
    }

    @Override
    public Set<String> keywords() {
	return new Lexer_c(null).keywords();
    }

    @Override
    protected NodeFactory createNodeFactory() {
        return new GallifreyNodeFactory_c(GallifreyLang_c.instance,
                                          new GallifreyExtFactory_c(
                                              new JL7ExtFactory_c(
                                                  new JL5ExtFactory_c())));
    }

    @Override
    protected TypeSystem createTypeSystem() {
        return new GallifreyTypeSystem_c();
    }
    
    @Override
    public Scheduler createScheduler() {
        return new GallifreyScheduler(this);
    }

}
