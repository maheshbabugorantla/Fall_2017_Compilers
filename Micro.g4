grammar Micro;

// fragment is pretty much like an inline function
fragment DIGITS :   [0-9];
fragment CHARS  :   ('a'..'z' | 'A'..'Z');

id : IDENTIFIER;

// Comment and Whitespace need to be skipped
COMMENT         :   '--' .*?('\r\n'|'\n') -> skip; // Doubtful

// Keywords in MICRO Langauge
KEYWORD         :   'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE' | 'IF' | 'ELSE' | 'FI' | 'FOR' | 'ROF'
                    | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT';

// An Identifier Token will begin with a letter, and be followed by any number of letters and numbers
IDENTIFIER      :   CHARS(CHARS | DIGITS)*;

// Pattern for Integers
INTLITERAL      :   DIGITS+; // One or more integers

// Operators
OPERATOR        :   (':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=') ;

WHITESPACE      :   (' ' | '\n' | '\t' | '\r' | '\f' )+ -> skip;

// Pattern String
STRINGLITERAL   :   '"'~["]*'"';

// Pattern for Float-Point Numbers
FLOATLITERAL    :   DIGITS*'.'DIGITS+; // | '.'DIGITS+;