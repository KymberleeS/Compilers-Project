lexer grammar Little;
INTLITERAL      : [0-9]+;
FLOATLITERAL    : [0-9]* '.' INTLITERAL;
STRINGLITERAL   : '"' ~('"')+ '"'; //STRING : '"' .*? '"' ; book definition
COMMENT         : '--' .*? '\n' -> skip; //channel(HIDDEN) instead of skip
KEYWORD         : 'PROGRAM' | 'BEGIN' | 'END' | 'FUNCTION' | 'READ' | 'WRITE' | 'IF' | 'ELSE' | 'ENDIF' | 'WHILE' | 'ENDWHILE' | 'CONTINUE' | 'BREAK' | 'RETURN' | 'INT' | 'VOID' | 'STRING' | 'FLOAT';
OPERATOR        : ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>=';
WS              : [ \r\n\t]+ -> skip;
IDENTIFIER      : [a-zA-Z]([a-zA-Z] | [0-9])*;
ERROR		: .;