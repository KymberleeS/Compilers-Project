/*
an IDENTIFIER token will begin with a letter, and be followed by any number of letters and numbers.  
IDENTIFIERS are case sensitive. 

INTLITERAL: integer number 
            ex) 0, 123, 678
FLOATLITERAL: floating point number available in two different format
                yyyy.xxxxxx or .xxxxxxx
            ex) 3.141592 , .1414 , .0001 , 456.98

STRINGLITERAL: any sequence of characters except '"' 
            between '"' and '"' 
            ex) "Hello world!" , "***********" , "this is a string"

COMMENT:
      Starts with "--" and lasts till the end of line
      ex) -- this is a comment
      ex) -- any thing after the "--" is ignored 


Keywords

PROGRAM,BEGIN,END,FUNCTION,READ,WRITE,
IF,ELSE,ENDIF,WHILE,ENDWHILE,CONTINUE,BREAK,
RETURN,INT,VOID,STRING,FLOAT

Operators
:= + - * / = != < > ( ) ; , <= >=
 */
grammar Little;

IDENTIFIER      : [a-zA-Z]([a-zA-Z] | [0-9])* ;
INTLITERAL      : [0-9]+;
FLOATLITERAL    : [0-9]* '.' INTLITERAL;
STRINGLITERAL   : '"' ~('"')+ '"'; //STRING : '"' .*? '"' ; book definition
COMMENT         : '--' .*? '\n' -> channel(HIDDEN); //channel(HIDDEN) instead of skip
KEYWORD         : 'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE' | 'IF' | 'ELSE' | 'ENDIF' | 'WHILE' | 'ENDWHILE' | 'CONTINUE' | 'BREAK' | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT';
OPERATOR        : ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=';