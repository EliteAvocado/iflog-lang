/* lexer grammar for SQL Target defintion */
lexer grammar SQLTargetLexer ;

KW_COMMENT : 'comment' ;
KW_BASETYPES : 'basetypes' ;
KW_TYPE : 'type' ;
KW_BASE : 'base' ;
KW_COMMENT_LINE : 'line' ;
KW_COMMENT_BLOCK : 'block' ;
KW_TABLE : 'table' ;
KW_VIEW : 'view' ;
KW_COMPOSITE_RULE : 'composite_rule' ;
KW_SYMBOLS : 'symbols' ;
KW_OPERATORS : 'operators' ;
KW_CONSTANTS : 'constants' ;

ASSIGNMENT_SYMBOL : ':' ;
TERMINATOR_SYMBOL : ';' ;
CURLY_OPEN : '{' ;
CURLY_CLOSE : '}' ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
ID : [a-zA-Z_]+ [a-zA-Z0-9_]* ;
WS : [ \t\n\r]+ -> skip ;
STRING : '"' (~'"'|'\\"')* '"' ;
