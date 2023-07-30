/* parser grammar for IFLog */
parser grammar IFLogParser ;
options { tokenVocab = IFLogLexer ; }

/* =============================================================================== */
/* main entry point */
/* =============================================================================== */

prog : (table | view | composite_rule)* EOF ;

/* =============================================================================== */
/* composite rule */
/* =============================================================================== */

composite_rule :
    name=V_ATOM '(' (vars+=composite_rule_arg (',' vars+=composite_rule_arg)*)? ')' ('->' ret_type=any_ret_type)?
    '{' (composite_rule_global | composite_rule_event | comp_rule)* '}'
    ;
composite_rule_arg : field=V_ATOM (':' type=any_type)? ('=' default_value=any_value)? ;

/* ------------------------------------------------------------------------------- */

composite_rule_global :
    'global' ':'
        vars+=composite_rule_global_el (',' vars+=composite_rule_global_el)*
    ';'
    ;
composite_rule_global_el : name=V_ATOM (':' type=any_type)? ;

/* ------------------------------------------------------------------------------- */

composite_rule_event :
    type=composite_rule_event_type ':' events+=composite_rule_event_el (',' events+=composite_rule_event_el)*
    ';'
    ;
composite_rule_event_type : 'before' | 'after' ;
composite_rule_event_el :
    type=comp_rule_event_type
    '('
        ev_table=V_ATOM
        (
            '::' fields+=V_ATOM
            | '(' fields+=V_ATOM (',' fields+=V_ATOM)* ')'
        )?
    ')'
    ;
comp_rule_event_type : 'ins' | 'upd' | 'del' ;

/* ------------------------------------------------------------------------------- */

comp_rule :
    (name=V_ATOM ':')?
    (   'noaction' body=comp_rule_body
        | head=comp_rule_head (':-' | '<-') 'always'
        | head=comp_rule_head body=comp_rule_body )
    ';'
    ;

/* rule head */
comp_rule_head :
    elems+=comp_rule_head_el (',' elems+=comp_rule_head_el)*
    ;
comp_rule_head_el :
    // can also be empty (but not negated)
    a_comp_rule_call=stat_comp_rule_call
    | a_ret=stat_comp_rule_return
    | a_print=stat_comp_rule_print
    | a_assignment=stat_assignment
    | a_db=stat_comp_rule_db_action
    ;

/* valid actions for rule head */

stat_comp_rule_return : 'ret' '(' ele=stat_comp_rule_return_el ')' ;
stat_comp_rule_return_el : var=V_ATOM | value=any_ret_value ;

stat_comp_rule_print : ('print' | 'printerr' | 'printwarn') '(' ele=stat_arithmetic ')' ;

stat_comp_rule_db_action :
    // insert row or delete specific row(s)
    ('ins' | 'del') '(' ele=stat_comp_rule_call ')'
    // delete all row(s)
    | 'del' '(' a_table=V_ATOM ')'
    // update specific row(s)
    | 'upd' '(' a_table=V_ATOM ':' a_values=table_data_record  '<-' a_cond=table_data_record ')'
    | 'upd' '(' values=stat_comp_rule_call '<-' cond=stat_comp_rule_call ')' ;

// positive comp_rule_call statement for head
stat_comp_rule_call :
    name=V_ATOM (table_data_record | '('')')
    ;

/* rule body */
comp_rule_body :
    (':-' | '<-') b_tags=stat_comp_rule_tags
    | ':-' (b_tags=stat_comp_rule_tags ',')? b_multi_select=stat_comp_rule_multi
    | ':-' (b_tags=stat_comp_rule_tags ',')? b_selects=stat_comp_rule_selects
    | '<-' (b_tags=stat_comp_rule_tags ',')? b_between=stat_comp_rule_between
    | '<-' (b_tags=stat_comp_rule_tags ',')? b_ifs=stat_comp_rule_if
    ;

/* valid elements for rule body */

/* tag block */
stat_comp_rule_tags :
    '(' stat_comp_rule_tags ')'
    | tags+=stat_comp_rule_tags_el (',' tags+=stat_comp_rule_tags_el)*
    ;
stat_comp_rule_tags_el : (negated=SYM_NEGATION)? name=V_ATOM ;

/* if block */
stat_comp_rule_if :
    stat_comp_rule_if any_logical_op stat_comp_rule_if
    | (negated=SYM_NEGATION)? '(' stat_comp_rule_if ')'
    | stat_comp_rule_comparison
    // needed to cover old/new accessors
    | stat_select
    // comp rule call
    // mostly covers the empty case
    // rest get's (falsely) flagged as tableView as part of a select
    | stat_comp_rule_get
    ;
stat_comp_rule_comparison :
    stat_comp_rule_comparison_el any_comparison_op stat_comp_rule_comparison_el
    | stat_in
    ;
stat_comp_rule_comparison_el : stat_arithmetic | stat_comp_rule_get ;
stat_comp_rule_get :
    (negated=SYM_NEGATION)? name=V_ATOM
    (table_data_record | '('')')
    ;

/* select block */
stat_comp_rule_selects :
    '(' stat_comp_rule_selects ')'
    | elems+=stat_select ('|' elems+=stat_select)*
    ;

/* foreach block (only as part of a select) */
stat_comp_rule_multi :
    '(' stat_comp_rule_multi ')'
    | select=stat_select ',' multi=stat_comp_rule_multi_el
    ;
stat_comp_rule_multi_el :
    'multi' '(' vars+=V_ATOM (',' vars+=V_ATOM)* ')'
    ;

/* for loop block */
stat_comp_rule_between :
    'between' '('
    from=stat_comp_rule_between_el ','
    to=stat_comp_rule_between_el ','
    idx=stat_comp_rule_between_el2 ')'
    ;
stat_comp_rule_between_el : var=V_ATOM | value=V_INT ;
stat_comp_rule_between_el2 : var=V_ATOM | value=SYM_WILDCARD_SINGLE ;

/* =============================================================================== */
/* view */
/* =============================================================================== */

view :
    'def' name=V_ATOM '(' view_field (',' view_field)* ')'
    '{' view_rule+ '}'
    ;

/* ------------------------------------------------------------------------------- */

view_field : field=V_ATOM (':' type=any_type)? ;

/*
    permutations:
    - (assignment)* select (select_constraints)? (assignment | case_when)*
*/

view_rule :
    (name=V_ATOM ':')?
        (lhs=view_rule_lhs ',')?
        body=stat_select
        (',' rhs=view_rule_rhs)?
    ';'
    ;
view_rule_lhs :
    '(' view_rule_lhs ')'
    | elems+=stat_assignment (',' elems+=stat_assignment)*
    ;
view_rule_rhs :
    '(' view_rule_rhs ')'
    | elems+=view_rule_ele (',' elems+=view_rule_ele)*
    ;
view_rule_ele : stat_assignment | stat_case_when ;

stat_case_when :
    '(' stat_case_when ')'
    | elems+=stat_case_when_el
        ('|' elems+=stat_case_when_el)*
        ('|' default_ele=stat_assignment)?
    ;
stat_case_when_el :
    '(' stat_case_when_el ')'
    | condition=table_constraint_el ',' action=stat_assignment
    ;

/* ------------------------------------------------------------------------------- */

stat_assignment :
    '(' stat_assignment ')'
    | vars+=V_ATOM '=' vars+=V_ATOM
    | vars+=V_ATOM '=' stat_arithmetic
    | stat_arithmetic '=' vars+=V_ATOM
    ;

/* ------------------------------------------------------------------------------- */

// one select always required
stat_select :
    '(' stat_select ')'
    | tables+=stat_table (',' tables+=stat_table)* (',' constraint=table_constraint_el)?
    ;

stat_table :
    (negated=SYM_NEGATION)? name=stat_table_el table_data_record
    ;
stat_table_el :
    var=V_ATOM | trigger_var=V_TRIGGER
    ;

/* =============================================================================== */
/* table */
/* =============================================================================== */

table :
    'def' name=V_ATOM '(' table_field (',' table_field)* ')'
    '{' (table_pk | table_uks | table_constraint | table_data)* '}'
    ;

/* ------------------------------------------------------------------------------- */

table_field : table_arg | table_arg_extended ;
table_arg : field=V_ATOM ':' type=any_type ('=' default_value=any_value)? ;

/*
    foreign key constraints
*/
table_arg_extended :
    parent=V_ATOM '::' fields+=V_ATOM
        ( 'as' aliases+=table_arg_alias ('(' constraint=V_ATOM ')')? )?
        ( '=' default_values+=table_arg_default_value )?
    | parent=V_ATOM '(' fields+=V_ATOM (',' fields+=V_ATOM)* ')'
        ( 'as' '(' aliases+=table_arg_alias (',' aliases+=table_arg_alias)* ')' ('(' constraint=V_ATOM ')')? )?
        ( '=' '(' default_values+=table_arg_default_value (',' default_values+=table_arg_default_value)* ')' )?
    ;
table_arg_alias : V_ATOM | SYM_WILDCARD_SINGLE ;
table_arg_default_value : any_value | SYM_WILDCARD_SINGLE ;

/* ------------------------------------------------------------------------------- */

/* primary key and unique key constraints */

table_pk : 'pk' ':' table_pk_uk_el ';' ;

table_uks : 'uks' ':' table_pk_uk_el (',' table_pk_uk_el)* ';' ;

table_pk_uk_el :
    ( fields+=V_ATOM | '(' fields+=V_ATOM (',' fields+=V_ATOM)+ ')' )
    ('as' constraint=V_ATOM )?
    ;

/* ------------------------------------------------------------------------------- */

/* table data for inserts */

table_data : 'data' ':' table_data_record (',' table_data_record)* ';' ;
table_data_record : table_record | table_record_extended ;

table_record :
    '('
        values+=table_record_value
        (',' values+=table_record_value)*
        (',' SYM_WILDCARD_MULTI)?
    ')'
    ;
table_record_value : stat_coalesce | stat_arithmetic | SYM_WILDCARD_SINGLE ;

table_record_extended :
    '('
        fields+=V_ATOM ':' values+=table_record_extended_value
        (',' fields+=V_ATOM ':' values+=table_record_extended_value)*
    ')'
    ;
table_record_extended_value : stat_coalesce | stat_arithmetic ;

/* ------------------------------------------------------------------------------- */

/* check constraints */

table_constraint :
    (constraint=V_ATOM ':')?
        body=table_constraint_el
    ';'
    ;
table_constraint_el :
    table_constraint_el any_logical_op table_constraint_el
    | (negated=SYM_NEGATION)? '(' table_constraint_el ')'
    | stat_comparison
    ;

/* ------------------------------------------------------------------------------- */

/* built-in predicates */

// additional built-in preds that evaluate to an atomic value
// that can be used as a value of a rule head action
stat_coalesce : 'coalesce' '(' elems+=stat_coalesce_el (',' elems+=stat_coalesce_el)+ ')' ;
stat_coalesce_el : value=any_non_null_value | var=V_ATOM ;

/* comparison and arithmetic statements */

stat_comparison :
    stat_arithmetic any_comparison_op stat_arithmetic
    | stat_in
    ;

stat_arithmetic :
    stat_arithmetic any_arithmetic_op stat_arithmetic
    | '(' stat_arithmetic ')'
    | stat_arithmetic_el
    ;
stat_arithmetic_el : value=any_value | field=V_ATOM ;

stat_in : field=V_ATOM in_op='in' value_list=v_list ;
v_list : '[' values+=any_value (',' values+=any_value)+ ']' ;

/* =============================================================================== */
/* helpers */
/* =============================================================================== */

/* except void + trigger */
any_type : KW_DATE | KW_AUTO | KW_INT | KW_FLOAT | KW_BOOL | KW_STRING | KW_CHAR | V_ATOM ;
any_ret_type : KW_INT | KW_FLOAT | KW_BOOL | KW_STRING | KW_CHAR | KW_VOID | KW_TRIGGER ;
any_ret_value : V_TRIGGER | V_NULL | V_NEWLINE | V_TABRIGHT | V_CURRENT_TIMESTAMP | V_INT | V_FLOAT | V_BOOL | V_CHAR_STRING_DOUBLE | V_CHAR_STRING_SINGLE ;
any_value : V_NULL | V_NEWLINE | V_TABRIGHT | V_CURRENT_TIMESTAMP | V_INT | V_FLOAT | V_BOOL | V_CHAR_STRING_DOUBLE | V_CHAR_STRING_SINGLE ;
any_non_null_value : V_NEWLINE | V_TABRIGHT | V_CURRENT_TIMESTAMP | V_INT | V_FLOAT | V_BOOL | V_CHAR_STRING_DOUBLE | V_CHAR_STRING_SINGLE ;
any_arithmetic_op : SYM_ADD | SYM_SUB | SYM_MUL | SYM_DIV | SYM_MOD ;
any_comparison_op : SYM_EQ | SYM_LT | SYM_LE | SYM_GT | SYM_GE | SYM_UE ;
any_logical_op : SYM_AND | SYM_OR ;

/* ------------------------------------------------------------------------------- */