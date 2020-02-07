lexer grammar DecaLexer;

options {
   language=Java;
   // Tell ANTLR to make the generated lexer class extend the
   // the named class, which is where any supporting code and
   // variables will be placed.
   superClass = AbstractDecaLexer;
}

@members {
}

// Deca lexer rules.

//things to skip
WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {
              skip(); // avoid producing a token
          }
    ;

COMMENT : (('/*' .*? '*/') | ('//' ~('\n')* ('\n' | EOF) )) {skip(); } ;


//SPECIAL CHARACTERS
OBRACE : '{' ;
CBRACE : '}' ;
OPARENT : '(' ;
CPARENT : ')' ;
SEMI : ';' ;
LT : '<';
GT : '>';
EQUALS : '=';
PLUS : '+';
MINUS : '-';
TIMES : '*';
SLASH : '/';
PERCENT : '%';
DOT : '.';
COMMA : ',';
EXCLAM : '!';
EQEQ : '==';
NEQ : '!=';
LEQ : '<=';
GEQ : '>=';
AND : '&&';
OR : '||';

// reserved words
ASM : 'asm' ;
INSTANCEOF : 'instanceof';
PRINT : 'print' ;
PRINTLN : 'println' ;
TRUE : 'true';
FALSE : 'false' ;
CLASS : 'class' ;
NEW : 'new';
EXTENDS : 'extends';
PRINTLNX : 'printlnx';
WHILE : 'while';
NULL : 'null';
PRINTX : 'printx';
ELSE : 'else';
READINT : 'readInt';
PROTECTED : 'protected';
READFLOAT : 'readFloat';
RETURN : 'return';
IF : 'if';
THIS : 'this';

//identifiers
fragment LETTER : 'a' .. 'z' | 'A' .. 'Z';
fragment DIGIT : '0' .. '9';
IDENT : (LETTER | '$' | '_') (LETTER  | DIGIT | '$' | '_')*;

//Integer litterals
fragment POSITIVE_DIGIT : '1' .. '9';
INT : '0' | POSITIVE_DIGIT DIGIT*;

//float litterals
fragment NUM : DIGIT+;
fragment SIGN : '+' | '-' |/* epsilon */;
fragment EXP : ('E' | 'e') SIGN NUM;
fragment DEC : NUM '.' NUM;
fragment FLOATDEC : (DEC | DEC EXP) ('F' | 'f' |/* epsilon */);
fragment DIGITHEX : ('0' .. '9') | ('A' .. 'F') | 'a' .. 'f';
fragment NUMHEX : DIGITHEX+;
fragment FLOATHEX : ('0x' | '0X') NUMHEX '.' NUMHEX ('P' | 'p') SIGN NUM ('F' | 'f' |/* epsilon */);
FLOAT : FLOATDEC | FLOATHEX;


//strings
fragment STRING_CAR : ~('\\' | '"' | '\n') ;
STRING : '"' (STRING_CAR | '\\"' | '\\\\')*  '"' ;
MULTI_LINE_STRING : '"' (STRING_CAR | '\n' | '\\"' | '\\\\')*  '"';

//include
fragment FILENAME : (LETTER | DIGIT | '.' | '-' | '_')+ ;
INCLUDE : '#include' (' ')* '"' FILENAME '"' {
    String name = getText();
    doInclude(name);
};

