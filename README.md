# Gallifrey Polyglot Extension

This is the Polyglot extension for Gallifrey.

**To build:** this project is built with Ant, run `build.xml`

Building the generated extension requires that Polyglot be installed and in your
classpath.  Adjust the classpath to include polyglot.jar, java_cup.jar, and JFlex.jar or add these jar files to the gallifreyc/lib directory.

**To run:** invoke gallifreyc.Main (see Polyglot documentation for list of arguments/flags)

### Repository Overview:

| Package                | Contents                                                      |
|------------------------|---------------------------------------------------------------|
| gallifreyc             | Scheduler                                                     |
| gallifreyc.ast         | NodeFactory, new AST nodes                                    |
| gallifreyc.extension   | ExtFactory, GallifreyLang dispatcher, exts for existing AST nodes  |
| gallifreyc.parse       | Lexer (.flex) and Parser (.ppg compiles to .cup)              |
| gallifreyc.translate   | Rewriters                                                     |
| gallifreyc.types       | TypeSystem and Type object overrides                          |
| gallifreyc.visit       | TypeChecker                                                   |

This extension follows a similar structure as other Polyglot extensions except for a few areas:
- the singleton language dispatcher is used (not present in older polyglot examples)
- AST nodes and extension objects are in separate packages
- multiple rewriting passes post-typechecking, right before codegen

### Setup Notes: (this will become instructions later)
- need to be using Java 8, with the ant build system
- `lib/` should have `java_cup.jar`, `jflex.jar`, `polyglot.jar`, `ppg.jar`, `full-runtime.jar`
- to run the test suite, you must have `pth` (the polyglot test harness) in your classpath. the easiest way to get this is from the polyglot repo; clone it and add `polyglot/bin` to your path
- the test suite requires `gallifrey-antidote` to be built; the directory containing this repo and the directory containing the `gallifrey-antidote` repo should be under the same parent directory
- run `fatjar.sh` inside `gallifrey-antidote` and verify that `full-runtime.jar` was generated in that directory. There is an alias of `full-runtime.jar` inside `gallifreyc/lib`
- built using `ant`
- run test suite by executing `test.sh` inside `tests/`
- to run from the command line:
`java -jar lib/gallifreyc.jar -classpath tests/out:lib/full-runtime.jar <FILE_NAME>`
- useful flags: -d (set output directory), -c (don't run javac; emit .java instead of .class), -stdout (dump java AST to stdout)

### Testing Notes:
- the test cases check that 1) gallifreyc successfully compiles the code OR throws an expected error and 2) the generated java code can compile
- output `.java` and `.class` files are in `tests/out`
- antidote tests need to be manually run (make sure to `make clean` in `gallifrey-antidote` and `antidote`
- to set up antidote tests: `make shell` in `antidote`; edit the makefile in `gallifrey-antidote` to include the directory where your `.class` files are (for test cases, `tests/out`), and `make backend`; then run the appropriate `.class` file

TODOs:
- default qualifications for stdlib (field and method instances)
- check transition runtime behavior matches TS
- ownership typechecking






