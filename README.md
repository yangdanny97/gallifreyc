# Gallifreyc

This is the Polyglot compiler extension for the Gallifrey language.

### Repository Overview:

| Package                | Contents                                                      |
|------------------------|---------------------------------------------------------------|
| gallifreyc             | Pass scheduler                                                |
| gallifreyc.ast         | AST node definition & construction                            |
| gallifreyc.extension   | Language dispatcher, extension objects for existing AST nodes | 
| gallifreyc.parse       | Lexer (.flex) and Parser (.ppg compiles to .cup)              |
| gallifreyc.translate   | AST rewriters                                                 |
| gallifreyc.types       | TypeSystem and Type object overrides                          |
| gallifreyc.visit       | TypeChecker & other visitors                                  |

This extension follows a similar structure as other Polyglot extensions except for a few areas:
- The singleton language dispatcher is used (not present in older Polyglot examples)
- AST nodes and extension objects are in separate packages
- Multiple rewriting passes post-typechecking, potentially generating additional source & class files

### Setup Notes:
- Requires: Java 8, ant build system
- Dependencies in `lib/`: `java_cup.jar`, `jflex.jar`, `polyglot.jar`, `ppg.jar`, `full-runtime.jar`
- The first 3 can be taken from `polyglot`, but the latter dependency requires `gallifrey-antidote`; the directory containing the `gallifreyc` repo and the directory containing the `gallifrey-antidote` repo should be under the same parent directory
- To set up `gallifrey-antidote`: build the project, run `fatjar.sh`, and verify that `full-runtime.jar` was generated in that directory. There is an alias of `full-runtime.jar` inside `gallifreyc/lib`
- Build `gallifreyc` using `ant`
- To execute gallifreyc from the command line:
`java -jar lib/gallifreyc.jar -classpath tests/out:lib/full-runtime.jar <FILE_NAME>` (the sample classpath assumes the test suite has been run already; it may be adjusted but must include `full-runtime.jar`, and compiled versions of `Shared.java`, `Unique.java`, & `RunAfterTest.java` from `tests/`.
- Useful flags: -d (set output directory), -c (don't run javac; emit .java instead of .class), -stdout (dump java AST to stdout)

### Test Suite Notes:
- Requires `pth` (the polyglot test harness) in your classpath. The easiest way to get this is from the polyglot repo; clone it and add `polyglot/bin` to your path.
- Run test suite by executing `test.sh` inside `tests/`
- The test cases check that 1) gallifreyc successfully compiles the code OR throws an expected error and 2) the generated java code can compile
- Output `.java` and `.class` files are written to `tests/out`
- Antidote tests need to be manually run, and require both `gallifrey-antidote` and `antidote` repositories (make sure to `make clean` in both repositories before each run)
- To set up antidote tests: `make shell` to build `antidote`; edit the `Makefile` in `gallifrey-antidote` to include the directory where your `.class` files are (for test cases, `tests/out`), and `make backend`.
- To run the existing antidote tests, execute `test.sh` and then run the appropriate `.class` file inside `test/out`.
- Because the tests use the same output directory, avoid conflicting restriction/class names for test cases.
- `ClassC` is a dummy class that has several dummy methods, it is used in several test cases and can be modified to add methods for new test cases if necessary (don't change any existing methods otherwise tests will break)

### Other notes/caveats/limitations:
- Restrictions should only be written for classes in the same compilation unit, due to the source code for each shared class requiring modifications to be compatible with the Gallifrey runtime
- Restriction-defined test methods cannot be overloaded, cannot overload/shadow methods of the base class, and no two restrictions may define test methods that have the same name
- Restriction-defined test methods should not access members of the class in their bodies for now (pending further testing)
- Should use wrapper classes of primitives for type annotations whenever possible; the behavior of primitives _should_ default to `local`, but there may be bugs so explicitly specifying `local Integer` is probably safer than using `int`

### TODOs:
- Default qualifications for stdlib (field and method instances)
- Check transition runtime behavior matches TS
- Check scoping of restriction-defined test methods
- Ownership typechecking
- Add validation for signatures of merge definitions (currently only the names of the functions are checked)




