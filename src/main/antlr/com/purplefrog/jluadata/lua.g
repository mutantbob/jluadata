grammar lua;

options {
output = AST;
}

@parser::header {
package com.purplefrog.jluadata;
}

@lexer::header {
package com.purplefrog.jluadata;
}

dictionary
  : assignment^*
  ;

assignment
  : Identifier '='^ exp
  ;

exp
  : String
  | Number
  | tableconstructor
  | 'nil'
  | 'false'
  | 'true'
  ;

tableconstructor : '{'^ (fieldlist)? '}'!;

fieldlist : field (fieldsep! field)* (fieldsep!)?;

field : '['! exp ']'! '='^ exp | Identifier '='^ exp | exp^;

fieldsep : ',' | ';';


Identifier
  :  ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*
  ;

Number
  : ( '-'? UnsignedNumber )
  ;

UnsignedNumber
  : INT
  | FLOAT
  | EXP
  | HEX
  ;

INT	: ('0'..'9')+;

FLOAT 	:INT '.' INT ;

EXP	: (INT| FLOAT) ('E'|'e') ('-')? INT;

HEX	:'0x' ('0'..'9'| 'a'..'f')+ ;

String:
  NORMALSTRING
  ;

NORMALSTRING
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;


fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

Space
  :  (' ' | '\t' | '\r' | '\n') {skip();}
  ;


COMMENT
    :   '--[[' ( options {greedy=false;} : . )* ']]' {skip();}
    ;

LINE_COMMENT
    : '--' ~('\n'|'\r')* '\r'? '\n' {skip();}
    ;

