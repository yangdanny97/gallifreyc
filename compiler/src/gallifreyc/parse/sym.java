
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20150326
//----------------------------------------------------

package gallifreyc.parse;

/** CUP generated class containing symbol constants. */
public class sym {
  /* terminals */
  public static final int THROW = 54;
  public static final int CONTINGENT = 114;
  public static final int INTEGER_LITERAL = 94;
  public static final int STATIC = 28;
  public static final int MINUS = 62;
  public static final int COMP = 63;
  public static final int WHEN = 127;
  public static final int MULT = 15;
  public static final int INTERFACE = 42;
  public static final int SEMICOLON = 14;
  public static final int BYTE = 3;
  public static final int BREAK = 51;
  public static final int LTEQ = 72;
  public static final int LOCAL = 117;
  public static final int ELSE = 44;
  public static final int ANDEQ = 91;
  public static final int PLUSEQ = 86;
  public static final int IF = 43;
  public static final int LONG_LITERAL_BD = 97;
  public static final int ENUM = 110;
  public static final int OROR = 81;
  public static final int DOT = 13;
  public static final int LPAREN = 20;
  public static final int LONG_LITERAL = 96;
  public static final int MATCH_RESTRICTION = 118;
  public static final int WHERE = 128;
  public static final int CASE = 46;
  public static final int AT = 111;
  public static final int RSHIFTEQ = 89;
  public static final int ELLIPSIS = 109;
  public static final int ENSURES = 116;
  public static final int LBRACK = 10;
  public static final int PUBLIC = 25;
  public static final int THROWS = 39;
  public static final int XOR = 78;
  public static final int DIVEQ = 84;
  public static final int LBRACE = 17;
  public static final int GOTO = 106;
  public static final int LSHIFTEQ = 88;
  public static final int MERGE = 119;
  public static final int CHAR = 7;
  public static final int PLUSPLUS = 59;
  public static final int IMPORT = 24;
  public static final int DELETE = 115;
  public static final int CATCH = 56;
  public static final int DOUBLE = 9;
  public static final int PROTECTED = 26;
  public static final int LONG = 6;
  public static final int COMMA = 16;
  public static final int INTEGER_LITERAL_BD = 95;
  public static final int MODEQ = 85;
  public static final int PRIVATE = 27;
  public static final int CONTINUE = 52;
  public static final int DIV = 65;
  public static final int TRANSITION = 125;
  public static final int ALLOWS = 112;
  public static final int GTEQ = 73;
  public static final int EQEQ = 75;
  public static final int THREAD = 124;
  public static final int VOLATILE = 34;
  public static final int EXTENDS = 36;
  public static final int NEW = 58;
  public static final int INSTANCEOF = 74;
  public static final int LT = 70;
  public static final int CLASS = 35;
  public static final int DO = 48;
  public static final int FINALLY = 57;
  public static final int PACKAGE = 23;
  public static final int CONST = 105;
  public static final int REQUIRES = 120;
  public static final int TRY = 55;
  public static final int SYNCHRONIZED = 32;
  public static final int CHARACTER_LITERAL = 101;
  public static final int FOR = 50;
  public static final int UNIQUE = 126;
  public static final int MINUSMINUS = 60;
  public static final int FINAL = 30;
  public static final int RPAREN = 21;
  public static final int EQ = 19;
  public static final int BOOLEAN = 2;
  public static final int RBRACK = 11;
  public static final int NOT = 64;
  public static final int RBRACE = 18;
  public static final int TEST = 123;
  public static final int AND = 77;
  public static final int MINUSEQ = 87;
  public static final int THIS = 40;
  public static final int SWITCH = 45;
  public static final int VOID = 38;
  public static final int TRANSIENT = 33;
  public static final int NULL_LITERAL = 103;
  public static final int PLUS = 61;
  public static final int FLOAT = 8;
  public static final int NATIVE = 31;
  public static final int error = 1;
  public static final int ABSTRACT = 29;
  public static final int STRICTFP = 107;
  public static final int INT = 5;
  public static final int QUESTION = 82;
  public static final int URSHIFTEQ = 90;
  public static final int RETURN = 53;
  public static final int SHARED = 122;
  public static final int BOOLEAN_LITERAL = 100;
  public static final int XOREQ = 92;
  public static final int IDENTIFIER = 12;
  public static final int EOF = 0;
  public static final int BRANCH = 113;
  public static final int SUPER = 41;
  public static final int MOD = 66;
  public static final int OR = 79;
  public static final int DOUBLE_LITERAL = 98;
  public static final int JAVADOC = 104;
  public static final int ASSERT = 108;
  public static final int COLON = 22;
  public static final int IMPLEMENTS = 37;
  public static final int MULTEQ = 83;
  public static final int OREQ = 93;
  public static final int RESTRICTION = 121;
  public static final int GT = 71;
  public static final int WITH = 129;
  public static final int SHORT = 4;
  public static final int NOTEQ = 76;
  public static final int RSHIFT = 68;
  public static final int LSHIFT = 67;
  public static final int FLOAT_LITERAL = 99;
  public static final int ANDAND = 80;
  public static final int URSHIFT = 69;
  public static final int WHILE = 49;
  public static final int STRING_LITERAL = 102;
  public static final int DEFAULT = 47;
  public static final String[] terminalNames = new String[] {
  "EOF",
  "error",
  "BOOLEAN",
  "BYTE",
  "SHORT",
  "INT",
  "LONG",
  "CHAR",
  "FLOAT",
  "DOUBLE",
  "LBRACK",
  "RBRACK",
  "IDENTIFIER",
  "DOT",
  "SEMICOLON",
  "MULT",
  "COMMA",
  "LBRACE",
  "RBRACE",
  "EQ",
  "LPAREN",
  "RPAREN",
  "COLON",
  "PACKAGE",
  "IMPORT",
  "PUBLIC",
  "PROTECTED",
  "PRIVATE",
  "STATIC",
  "ABSTRACT",
  "FINAL",
  "NATIVE",
  "SYNCHRONIZED",
  "TRANSIENT",
  "VOLATILE",
  "CLASS",
  "EXTENDS",
  "IMPLEMENTS",
  "VOID",
  "THROWS",
  "THIS",
  "SUPER",
  "INTERFACE",
  "IF",
  "ELSE",
  "SWITCH",
  "CASE",
  "DEFAULT",
  "DO",
  "WHILE",
  "FOR",
  "BREAK",
  "CONTINUE",
  "RETURN",
  "THROW",
  "TRY",
  "CATCH",
  "FINALLY",
  "NEW",
  "PLUSPLUS",
  "MINUSMINUS",
  "PLUS",
  "MINUS",
  "COMP",
  "NOT",
  "DIV",
  "MOD",
  "LSHIFT",
  "RSHIFT",
  "URSHIFT",
  "LT",
  "GT",
  "LTEQ",
  "GTEQ",
  "INSTANCEOF",
  "EQEQ",
  "NOTEQ",
  "AND",
  "XOR",
  "OR",
  "ANDAND",
  "OROR",
  "QUESTION",
  "MULTEQ",
  "DIVEQ",
  "MODEQ",
  "PLUSEQ",
  "MINUSEQ",
  "LSHIFTEQ",
  "RSHIFTEQ",
  "URSHIFTEQ",
  "ANDEQ",
  "XOREQ",
  "OREQ",
  "INTEGER_LITERAL",
  "INTEGER_LITERAL_BD",
  "LONG_LITERAL",
  "LONG_LITERAL_BD",
  "DOUBLE_LITERAL",
  "FLOAT_LITERAL",
  "BOOLEAN_LITERAL",
  "CHARACTER_LITERAL",
  "STRING_LITERAL",
  "NULL_LITERAL",
  "JAVADOC",
  "CONST",
  "GOTO",
  "STRICTFP",
  "ASSERT",
  "ELLIPSIS",
  "ENUM",
  "AT",
  "ALLOWS",
  "BRANCH",
  "CONTINGENT",
  "DELETE",
  "ENSURES",
  "LOCAL",
  "MATCH_RESTRICTION",
  "MERGE",
  "REQUIRES",
  "RESTRICTION",
  "SHARED",
  "TEST",
  "THREAD",
  "TRANSITION",
  "UNIQUE",
  "WHEN",
  "WHERE",
  "WITH"
  };
}

