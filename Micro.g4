grammar Micro;

fragment DIGITS : [0-9]; // Reusable fragment

id : IDENTIFIER;

// An Identifier Token will begin with a letter, and be followed by any number of letters and numbers
IDENTIFIER      :   ('a'..'z' | 'A'..'Z')(('a'..'z' | 'A'..'Z') | [0-9])*;

// Pattern for Integers
INTLITERAL      :   DIGITS+; // One or more integers

// Pattern for Float-Point Numbers
FLOATLITERAL    :   DIGITS+'.'DIGITS+ | '.'DIGITS+;

// Pattern String
STRINGLITERAL   :   '"'~["]*'"'; // Doubtful


// Comment and Whitespace need to be skipped
COMMENT         :   '--' .*?('\r\n'|'\n') -> skip; // Doubtful
                    // { $setType(Token.SKIP); }; // Doubtful

WHITESPACE      :   (' ' | '\n' | '\t' | '\r' | '\f' )+ -> skip; // Doubtful
                    // { $setType(Token.SKIP); }; // Doubtful

// Keywords in MICRO Langauge
KEYWORD         :   'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE' | 'IF' | 'ELSE' | 'FI' | 'FOR' | 'ROF'
                    | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT';

// Operators
OPERATOR        :   (':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=') ;
