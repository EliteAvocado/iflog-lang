/* lexer grammar for IFLog */
lexer grammar IFLogLexer ;
channels { LINE_COMMENT, BLOCK_COMMENT }

/* keywords */

/* fixed basetypes that need to be supported as is */

KW_DATE : 'date' ;
KW_AUTO : 'auto' ;
KW_INT : 'int' ;
KW_FLOAT : 'float' ;
KW_BOOL : 'bool' ;
KW_STRING : 'string' ;
KW_CHAR : 'char' ;
KW_TRIGGER : 'trigger' ;
KW_VOID : 'void' ;

/* constants */

KW_IN : 'in' ;
KW_AS : 'as' ;

/* table preds */

KW_DEF : 'def' ;

KW_PK : 'pk' ;
KW_UKS : 'uks' ;
KW_DATA : 'data' ;

/* composite rule preds */

KW_BEFORE : 'before' ;
KW_AFTER : 'after' ;
KW_GLOBAL : 'global' ;

/* head/body preds */
KW_INSERT : 'ins' ;
KW_UPDATE : 'upd' ;
KW_DELETE : 'del' ;

KW_COALESCE : 'coalesce' ;

/* head preds (actions) */
KW_RETURN : 'ret' ;
KW_PRINT : 'print' ;
KW_PRINTERR : 'printerr' ;
KW_PRINTWARN : 'printwarn' ;
KW_NOACTION : 'noaction' ;

/* body preds */
KW_MULTIPLE : 'multi' ;
KW_BETWEEN : 'between' ;
KW_ALWAYS : 'always' ;

/* symbols */

SYM_AND : ',' ;
SYM_OR : '|' ;
SYM_TERMINAL : ';' ;
SYM_NEGATION : '!' ;

SYM_ASSIGNMENT : ':' ;
SYM_ACCESS : '::' ;

SYM_DERIVE_RHS : ':-' ;
SYM_CHECK_RHS : '<-' ;
SYM_RETURN : '->' ;

SYM_LROUND : '(' ;
SYM_RROUND : ')' ;
SYM_LCURLY : '{' ;
SYM_RCURLY : '}' ;
SYM_LBLOCK : '[' ;
SYM_RBLOCK : ']' ;

SYM_EQ : '=' ;
SYM_LT : '<' ;
SYM_GT : '>' ;
SYM_LE : '<=' ;
SYM_GE : '>=' ;
SYM_UE : '!=' ;

SYM_WILDCARD_SINGLE : '_' ;
SYM_WILDCARD_MULTI : '..' ;

SYM_ADD : '+' ;
SYM_SUB : '-' ;
SYM_MUL : '*' ;
SYM_DIV : '/' ;
SYM_MOD : '%' ;

/* type values */

V_INT : [-]? [0-9]+ ;
V_FLOAT : [-]? [0-9]+ '.' [0-9]+ ;
V_BOOL : 'true' | 'false' ;
V_CHAR_STRING_DOUBLE : '"' (~'"'|'\\"')* '"'  ;
V_CHAR_STRING_SINGLE : '\'' (~'\''|'\\\'')* '\''  ;

V_TRIGGER : 'old' | 'new' ;
V_NULL : 'null' ;

V_NEWLINE : 'nl';
V_TABRIGHT : 'tr';
V_CURRENT_TIMESTAMP : 'now';

V_ATOM : [a-zA-Z_]+ [a-zA-Z0-9_]* ;

/* misc */

LCOMMENT : '//' ~('\r' | '\n')* -> channel(LINE_COMMENT) ;
BCOMMENT : '/*' .*? '*/' -> channel(BLOCK_COMMENT) ;
WS : [ \t\n\r]+ -> skip ;
