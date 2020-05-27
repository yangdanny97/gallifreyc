package gallifreyc.extension;

import java.util.*;

import gallifreyc.ast.GallifreyNodeFactory;
import gallifreyc.ast.RestrictionDecl;
import gallifreyc.translate.GallifreyRewriter;
import polyglot.ast.*;
import polyglot.ast.Import.Kind;
import polyglot.ext.jl5.ast.JL5Import;
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
        SourceFile n = node();
        List<TopLevelDecl> cd = new ArrayList<>();
        List<RestrictionDecl> rd = new ArrayList<>();
        for (TopLevelDecl d : n.decls()) {
            if (d instanceof ClassDecl)
                cd.add(d);
            if (d instanceof RestrictionDecl)
                rd.add((RestrictionDecl) d);
        }
        n = n.decls(cd);
        n = (SourceFile) superLang().buildTypes(n, tb);

        List<TopLevelDecl> newDecls = new ArrayList<>();
        List<RestrictionDecl> newRDecls = new ArrayList<>();

        for (RestrictionDecl r : rd) {
            RestrictionDecl newRDecl = (RestrictionDecl) lang().buildTypes(r, tb);
            newRDecls.add(newRDecl);
        }
        newDecls.addAll(n.decls());
        newDecls.addAll(newRDecls);
        return n.decls(newDecls);
    }

    @Override
    public Node gallifreyRewrite(GallifreyRewriter rw) throws SemanticException {
        GallifreyNodeFactory nf = rw.nodeFactory();
        SourceFile sf = node();
        Position p = Position.COMPILER_GENERATED;

        List<Import> imports = new ArrayList<>(sf.imports());
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.Unique"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.Shared"));
        imports.add(0, nf.Import(p, Import.SINGLE_TYPE, "gallifrey.core.SharedObject"));
        imports.add(nf.Import(p, Import.SINGLE_TYPE, "java.io.Serializable"));
        imports.add(nf.Import(p, Import.SINGLE_TYPE, "java.util.Arrays"));
        imports.add(nf.Import(p, Import.SINGLE_TYPE, "java.util.ArrayList"));

        return sf.imports(imports);
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
