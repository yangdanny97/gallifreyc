# Gallifrey Polyglot Extension

This is the Polyglot extension for Gallifrey.

**To build:** run build.xml

Building the generated extension requires that Polyglot be installed and in your
classpath.  Adjust the classpath to include polyglot.jar, ja-
va_cup.jar, and JFlex.jar or add these jar files to the gallifreyc/lib directory.

**To run:** invoke gallifreyc.Main (see Polyglot documentation for list of arguments/flags)

### Repository Overview:

| Package                | Contents                                                      |
|------------------------|---------------------------------------------------------------|
| `gallifreyc`           | Scheduler                                                     |
| `gallifreyc.ast`       | NodeFactory, new AST nodes                                    |
| `gallifreyc.extension` | ExtFactory, Language dispatcher, exts for existing AST nodes  |
| `gallifreyc.parse`     | Lexer (`.flex`) and Parser (`.ppg` compiles to `.cup`)        |
| `gallifreyc.translate` | Rewriters                                                     |
| `gallifreyc.types`     | TypeSystem and Type object overrides                          |
| `gallifreyc.visit`     | TypeChecker                                                   |


TODOs:
- parsing ambiguity for qualified array types
- expected failing test cases are failing with unexpected messages
- varargs during procedure calls
- default qualifications for stdlib
- typesystem allow method override
- inheritance field qualifications
- handling primitive types (canonical type nodes before typebuilding, no qualifiers needed)
- AnyRef semantics

