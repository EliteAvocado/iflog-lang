/* parser grammar for SQL Target defintion */
parser grammar SQLTargetParser ;
options { tokenVocab = SQLTargetLexer ; }

prog :
    ( comment_block
    | basetypes_block | type_block
    | table_block
    | view_block
    | composite_rule_block
    | symbols_block | operators_block | constants_block )*
    EOF ;

comment_block : KW_COMMENT '{' comment_ele+ '}' ;
comment_ele_head : KW_COMMENT_LINE | KW_COMMENT_BLOCK ;
comment_ele : key=comment_ele_head ':' value=STRING ';' ;

basetypes_block  : KW_BASETYPES '{' ele+ '}' ;

type_block  : KW_TYPE '{' base_ele ele+ '}' ;
base_ele : key=KW_BASE ':' value=ID ';' ;

table_block : KW_TABLE '{' ele+ '}' ;

view_block : KW_VIEW '{' ele+ '}' ;

composite_rule_block : KW_COMPOSITE_RULE '{' ele+ '}' ;

symbols_block : KW_SYMBOLS '{' ele+ '}' ;
operators_block : KW_OPERATORS '{' ele+ '}' ;
constants_block : KW_CONSTANTS '{' ele+ '}' ;

/* func_def : "CREATE FUNCTION <name>(<arg_list>) ;" ; */
ele : key=ID ':' value=STRING ';' ;
