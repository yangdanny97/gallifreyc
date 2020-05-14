# Gallifrey Polyglot Extension

This is the Polyglot extension for Gallifrey.

**To build:** run build.xml

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
- a custom ExtRewriter interface GRewriter to allow multiple rewriting passes

TODOs:
- debug test cases
- default qualifications for stdlib (field and method instances)
- fix translations for transition/match, typechecking for transition
- typing hierarchy and usage of union restrictions vs restriction names
- test methods currently break

