package gallifreyc.extension;

import java.net.URI;
import java.util.*;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RestrictionDecl;
import gallifreyc.ast.RestrictionUnionDecl;
import gallifreyc.translate.GallifreyGeneratedSource;
import gallifreyc.translate.GallifreyRewriter;
import polyglot.ast.*;
import polyglot.ast.Import.Kind;
import polyglot.ext.jl5.ast.JL5Import;
import polyglot.filemanager.ExtFileObject;
import polyglot.frontend.Source;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

// extra operations for source files
public class GallifreySourceFileExt extends GallifreyExt {
    private static final long serialVersionUID = SerialVersionUID.generate();

    @Override
    public SourceFile node() {
        return (SourceFile) super.node();
    }

    @Override
    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        return superLang().buildTypes(node(), tb);
    }
    
    private Source getSourceFromParent(Source parent, ClassDecl d) {
        List<String> parentURI = Arrays.asList(parent.toString().split("/"));
        List<String> baseURI = new ArrayList<>();
        for (int i = 0; i < parentURI.size() - 1; i++) {
            baseURI.add(parentURI.get(i));
        }
        baseURI.add(d.name() + ".gal");
        String uristring = "file:" + String.join("/", baseURI);
        FileObject fo = new ExtFileObject(URI.create(uristring), JavaFileObject.Kind.OTHER);
        
        return new GallifreyGeneratedSource(fo);
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        GallifreyNodeFactory nf = rw.nodeFactory();
        SourceFile sf = node();
        Position p = Position.COMPILER_GENERATED;

        List<Import> imports = new ArrayList<>(sf.imports());
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.Unique"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.Shared"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.RunAfterTest"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.core.SharedObject"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.core.MergeComparator"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.core.GenericFunction"));
        imports.add(nf.Import(p, Import.SINGLE_TYPE, "java.io.Serializable"));
        imports.add(nf.Import(p, Import.SINGLE_TYPE, "java.util.Arrays"));
        imports.add(nf.Import(p, Import.SINGLE_TYPE, "java.util.ArrayList"));

        List<TopLevelDecl> decls = new ArrayList<TopLevelDecl>();
        for (TopLevelDecl d : sf.decls()) {
            if (d != null) {
                decls.add(d);
            }
        }
        sf = sf.imports(imports).decls(decls);
        
        List<SourceFile> sources = new ArrayList<SourceFile>();
        sources.add(sf);
        
        for (ClassDecl d : rw.generatedClasses) {
            List<TopLevelDecl> generatedDecl = new ArrayList<>();
            generatedDecl.add(d);
            SourceFile generatedSource = nf.SourceFile(p, new ArrayList<>(imports), generatedDecl)
                    .source(getSourceFromParent(sf.source(), d));
            sources.add(generatedSource);
        }

        return nf.SourceCollection(p, sources);
    }

    @Override
    public NodeVisitor typeCheckEnter(TypeChecker tc) throws SemanticException {
        NodeVisitor nv = superLang().typeCheckEnter(node(), tc);
        return nv;
    }

    @Override
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Map<String, Named> declaredTypes = new HashMap<>();
        boolean hasPublic = false;

        for (TopLevelDecl d : node().decls()) {
            if (d instanceof RestrictionDecl) {
                lang().typeCheck((RestrictionDecl) d, tc);
            }
            if (d instanceof RestrictionUnionDecl) {
                lang().typeCheck((RestrictionUnionDecl) d, tc);
            }
        }

        for (TopLevelDecl d : node().decls()) {
            if (d instanceof ClassDecl) {
                String s = d.name();

                if (declaredTypes.containsKey(s)) {
                    throw new SemanticException("Duplicate declaration: \"" + s + "\".", d.position());
                }

                declaredTypes.put(s, ((ClassDecl) d).type());

                if (d.flags().isPublic()) {
                    if (hasPublic) {
                        throw new SemanticException("The source contains more than one public declaration.",
                                d.position());
                    }

                    hasPublic = true;
                }
            }
        }

        TypeSystem ts = tc.typeSystem();
        Map<String, Named> importedTypes = new HashMap<>();
        Map<String, Named> staticImportedTypes = new HashMap<>();

        for (Import i : node().imports()) {
            Kind kind = i.kind();
            if (kind == Import.SINGLE_TYPE) {
                String s = i.name();
                Named named = ts.forName(s);
                String name = named.name();
                importedTypes.put(name, named);
            }
            if (kind != JL5Import.SINGLE_STATIC_MEMBER)
                continue;

            String s = i.name();
            Named named;
            try {
                named = ts.forName(s);
            } catch (SemanticException e) {
                // static import is not a type; further checks unnecessary.
                continue;
            }
            String name = named.name();

            // See JLS 3rd Ed. | 7.5.3.

            // If a compilation unit contains both a single-static-import
            // declaration that imports a type whose simple name is n, and a
            // single-type-import declaration that imports a type whose simple
            // name is n, a compile-time error occurs.
            if (importedTypes.containsKey(name)) {
                Named importedType = importedTypes.get(name);
                throw new SemanticException(
                        name + " is already defined in a single-type import as type " + importedType + ".",
                        i.position());
            } else
                staticImportedTypes.put(name, named);

            // If a single-static-import declaration imports a type whose simple
            // name is n, and the compilation unit also declares a top level
            // type whose simple name is n, a compile-time error occurs.
            if (declaredTypes.containsKey(name)) {
                Named declaredType = declaredTypes.get(name);
                throw new SemanticException("The static import " + s + " conflicts with type " + declaredType
                        + " defined in the same file.", i.position());
            }

        }

        return node();
    }
}
