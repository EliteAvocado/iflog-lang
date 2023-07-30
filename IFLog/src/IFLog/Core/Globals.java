package IFLog.Core;

/**
 * define globals here
 */

public final class Globals
{
    public static boolean DEBUG = true;
    public static String LOG_FILE = null ;
    public static boolean KEEP_LOG = false ;

    public static final class Translation
    {
        // all constants should inherit this
        public abstract static class Base
        {
            protected static String CreateKey(final String outer_key, final String inner_key)
            {
                return outer_key + KEY_DELIM + inner_key;
            }

            protected static String CreateValue(final String value)
            {
                return DELIM_OPEN + value + DELIM_CLOSE;
            }

            private static final String DELIM_OPEN = "<@";
            private static final String DELIM_CLOSE = ">";
            // for easier access
            private static final String KEY_DELIM = ".";
        }

        public static class Fixed
        {
            public static final String BT_UNKNOWN = "unknown";

            // basetypes
            public static final String BT_DATE = "date";
            public static final String BT_AUTO = "auto";
            public static final String BT_INT = "int";
            public static final String BT_FLOAT = "float";
            public static final String BT_BOOL = "bool";
            public static final String BT_STRING = "string";
            public static final String BT_CHAR = "char";
            public static final String BT_TRIGGER = "trigger";
            public static final String BT_VOID = "void";
            public static final String[] BT_ALL = {
                    BT_DATE, BT_AUTO, BT_INT, BT_FLOAT, BT_BOOL, BT_STRING,
                    BT_CHAR, BT_TRIGGER, BT_VOID
            };

            // type flags
            public static final String SYM_FLAG = "@";
            public static final String SYM_FLAG_STR = "IFL_STR_AT";
            public static String Flag(final String flag)
            {
                return SYM_FLAG + flag;
            }

            // var flags
            public static final String SYM_VAR_FLAG_LEFT = "{";
            public static final String SYM_VAR_FLAG_RIGHT = "}";
            public static final String SYM_VAR_FLAG_LEFT_STR = "IFL_STR_LEFT_BRACKET";
            public static final String SYM_VAR_FLAG_RIGHT_STR = "IFL_STR_RIGHT_BRACKET";
            public static String VarFlag(final String iflVar)
            {
                return SYM_VAR_FLAG_LEFT + iflVar + SYM_VAR_FLAG_RIGHT;
            }

            // helpers
            public static final String SEP_SEQUENCE = ",";
            public static final String SEP_NEWLINE = "\n";

            // table
            public static final String TABLE_CREATE = "create";
            public static final String TABLE_FIELD = "field";
            public static final String TABLE_FIELD_DEFAULT = "field_default";
            public static final String TABLE_FIELD_NULLABLE = "field_nullable";
            public static final String TABLE_FIELD_NOT_NULLABLE = "field_not_nullable";
            public static final String TABLE_PK = "primary_key_constraint";
            public static final String TABLE_UK = "unique_key_constraint";
            public static final String TABLE_FK = "foreign_key_constraint";
            public static final String TABLE_CHECK = "check_constraint";
            public static final String TABLE_DATA_INSERT = "insert";
            public static final String[] TABLE_ELE_ALL = {
                    TABLE_CREATE, TABLE_FIELD, TABLE_FIELD_DEFAULT, TABLE_FIELD_NULLABLE,
                    TABLE_FIELD_NOT_NULLABLE, TABLE_PK, TABLE_UK,  TABLE_FK, TABLE_CHECK,
                    TABLE_DATA_INSERT
            };

            // view
            public static final String VIEW_CREATE = "create";
            public static final String VIEW_SELECT = "select";
            public static final String VIEW_SELECT_FROM = "select_from";
            public static final String VIEW_SELECT_WHERE = "select_where";
            public static final String VIEW_SELECT_TABLEVIEW = "select_tableview";
            public static final String VIEW_SELECT_VAR = "select_var";
            public static final String VIEW_SELECT_FIELD = "select_field";
            public static final String VIEW_SELECT_CASE_WHEN = "select_case_when";
            public static final String VIEW_SELECT_CASE_WHEN_CASE = "select_case_when_case";
            public static final String VIEW_SELECT_CASE_WHEN_DEFAULT = "select_case_when_default";
            public static final String VIEW_UNION_ALL = "union_all";
            public static final String[] VIEW_ELE_ALL = {
                    VIEW_CREATE, VIEW_SELECT, VIEW_SELECT_FROM, VIEW_SELECT_WHERE, VIEW_SELECT_TABLEVIEW,
                    VIEW_SELECT_VAR, VIEW_SELECT_FIELD, VIEW_SELECT_CASE_WHEN, VIEW_SELECT_CASE_WHEN_CASE,
                    VIEW_SELECT_CASE_WHEN_DEFAULT, VIEW_UNION_ALL
            };

            // composite rule



            public static final String COMPOSITE_RULE_CREATE = "create";
            public static final String COMPOSITE_RULE_COMP_RULE_VAR = "comp_rule_var";
            public static final String COMPOSITE_RULE_COMP_RULE_VAR_DEFAULT = "comp_rule_var_default";
            public static final String COMPOSITE_RULE_GLOBAL_VAR = "global_var";

            public static final String COMPOSITE_RULE_CREATE_TRIGGER = "create_trigger";
            public static final String COMPOSITE_RULE_TRIGGER_TYPE_BEFORE = "trigger_type_before";
            public static final String COMPOSITE_RULE_TRIGGER_TYPE_AFTER = "trigger_type_after";
            public static final String COMPOSITE_RULE_TRIGGER_TYPE_INSERT = "trigger_type_insert";
            public static final String COMPOSITE_RULE_TRIGGER_TYPE_UPDATE = "trigger_type_update";
            public static final String COMPOSITE_RULE_TRIGGER_TYPE_DELETE = "trigger_type_delete";
            public static final String COMPOSITE_RULE_TRIGGER_FIELDS = "trigger_fields";
            public static final String COMPOSITE_RULE_TRIGGER_TABLE = "trigger_table";

            public static final String COMPOSITE_RULE_IF_COND = "if_cond";
            public static final String COMPOSITE_RULE_ELIF_COND = "elif_cond";
            public static final String COMPOSITE_RULE_ELSE_COND = "else_cond";
            public static final String COMPOSITE_RULE_ENDIF_COND = "endif_cond";

            public static final String COMPOSITE_RULE_DB_INSERT = "db_insert";
            public static final String COMPOSITE_RULE_DB_UPDATE = "db_update";
            public static final String COMPOSITE_RULE_DB_UPDATE_SINGLE = "db_update_single";
            public static final String COMPOSITE_RULE_DB_DELETE = "db_delete";
            public static final String COMPOSITE_RULE_DB_DELETE_TABLE = "db_delete_table";

            public static final String COMPOSITE_RULE_FOR_LOOP = "for_loop";
            public static final String COMPOSITE_RULE_FOR_EACH_LOOP = "for_each_loop";

            public static final String COMPOSITE_RULE_RETURN = "return";

            public static final String COMPOSITE_RULE_ASSIGNMENT = "assignment";


            public static final String COMPOSITE_RULE_GET = "composite_rule_get";
            public static final String COMPOSITE_RULE_GET_NEGATED = "composite_rule_get_negated";

            public static final String COMPOSITE_RULE_CALL = "composite_rule_call";

            public static final String COMPOSITE_RULE_IF_COND_SELECT = "if_cond_select";
            public static final String COMPOSITE_RULE_SELECT_ALL = "select_all";
            public static final String COMPOSITE_RULE_SELECT_INTO = "select_into";
            public static final String COMPOSITE_RULE_SELECT = "select";
            public static final String COMPOSITE_RULE_SELECT_FROM = "select_from";
            public static final String COMPOSITE_RULE_SELECT_WHERE = "select_where";

            public static final String COMPOSITE_RULE_SELECT_TABLEVIEW = "select_tableview";

            public static final String COMPOSITE_RULE_SELECT_VAR = "select_var";
            public static final String COMPOSITE_RULE_SELECT_FIELD = "select_field";

            public static final String COMPOSITE_RULE_PRINT = "print";
            public static final String COMPOSITE_RULE_PRINTERR = "printerr";
            public static final String COMPOSITE_RULE_PRINTWARN = "printwarn";

            public static final String COMPOSITE_RULE_COALESCE = "coalesce";

            public static final String[] COMPOSITE_RULE_ELE_ALL = {
                    COMPOSITE_RULE_CREATE, COMPOSITE_RULE_COMP_RULE_VAR, COMPOSITE_RULE_COMP_RULE_VAR_DEFAULT,
                    COMPOSITE_RULE_GLOBAL_VAR, COMPOSITE_RULE_CREATE_TRIGGER, COMPOSITE_RULE_TRIGGER_TYPE_BEFORE,
                    COMPOSITE_RULE_TRIGGER_TYPE_AFTER, COMPOSITE_RULE_TRIGGER_TYPE_INSERT, COMPOSITE_RULE_TRIGGER_TYPE_UPDATE,
                    COMPOSITE_RULE_TRIGGER_TYPE_DELETE, COMPOSITE_RULE_TRIGGER_FIELDS, COMPOSITE_RULE_TRIGGER_TABLE,
                    COMPOSITE_RULE_IF_COND, COMPOSITE_RULE_ELIF_COND, COMPOSITE_RULE_ELSE_COND, COMPOSITE_RULE_ENDIF_COND,
                    COMPOSITE_RULE_DB_INSERT, COMPOSITE_RULE_DB_UPDATE, COMPOSITE_RULE_DB_UPDATE_SINGLE, COMPOSITE_RULE_DB_DELETE, COMPOSITE_RULE_DB_DELETE_TABLE,
                    COMPOSITE_RULE_FOR_LOOP, COMPOSITE_RULE_FOR_EACH_LOOP, COMPOSITE_RULE_RETURN, COMPOSITE_RULE_ASSIGNMENT, COMPOSITE_RULE_GET,
                    COMPOSITE_RULE_GET_NEGATED, COMPOSITE_RULE_CALL, COMPOSITE_RULE_IF_COND_SELECT, COMPOSITE_RULE_SELECT_ALL,
                    COMPOSITE_RULE_SELECT_INTO, COMPOSITE_RULE_SELECT, COMPOSITE_RULE_SELECT_FROM, COMPOSITE_RULE_SELECT_WHERE,
                    COMPOSITE_RULE_SELECT_TABLEVIEW, COMPOSITE_RULE_SELECT_VAR, COMPOSITE_RULE_SELECT_FIELD, COMPOSITE_RULE_PRINT,
                    COMPOSITE_RULE_PRINTERR, COMPOSITE_RULE_PRINTWARN, COMPOSITE_RULE_COALESCE
            };

            // symbols
            public static final String SYM_TERMINAL = "terminal";
            public static final String SYM_CHAR_STRING_DOUBLE = "char_string_double";
            public static final String SYM_CHAR_STRING_SINGLE = "char_string_single";
            public static final String SYM_LIST_LHS = "list_lhs";
            public static final String SYM_LIST_RHS = "list_rhs";
            public static final String[] SYM_ALL = {
                    SYM_TERMINAL, SYM_CHAR_STRING_DOUBLE, SYM_CHAR_STRING_SINGLE,
                    SYM_LIST_LHS, SYM_LIST_RHS
            };

            // operators
            public static final String OP_AND = "and";
            public static final String OP_OR = "or";
            public static final String OP_NEGATION = "negation";
            public static final String OP_EQ = "equals";
            public static final String OP_UE = "unequals";
            public static final String OP_LT = "less_than";
            public static final String OP_LE = "less_equals";
            public static final String OP_GT = "greater_than";
            public static final String OP_GE = "greater_equals";
            public static final String OP_NULL_EQ = "null_equals";
            public static final String OP_NULL_UE = "null_unequals";
            public static final String OP_IN = "in";
            public static final String OP_ASSIGNMENT = "assignment";
            public static final String OP_ADD = "add";
            public static final String OP_SUB = "sub";
            public static final String OP_MUL = "mul";
            public static final String OP_DIV = "div";
            public static final String OP_MOD = "mod";
            public static final String OP_CONCAT = "concat";
            public static final String[] OP_ALL = {
                    OP_AND, OP_OR, OP_NEGATION, OP_EQ, OP_UE, OP_LT, OP_LE, OP_GT, OP_GE,
                    OP_NULL_EQ, OP_NULL_UE, OP_IN, OP_ASSIGNMENT, OP_ADD, OP_SUB, OP_MUL, OP_DIV,
                    OP_MOD, OP_CONCAT
            };

            // constants
            public static final String CONST_TRUE = "true";
            public static final String CONST_FALSE = "false";
            public static final String CONST_OLD = "old";
            public static final String CONST_NEW = "new";
            public static final String CONST_OLD_VALUE = "old_value";
            public static final String CONST_NEW_VALUE = "new_value";
            public static final String CONST_NULL = "null";
            public static final String CONST_CURRENT_TIMESTAMP = "current_timestamp";
            public static final String CONST_NEWLINE = "newline";
            public static final String CONST_TAB_RIGHT = "tab_right";
            public static final String[] CONST_ALL = {
                    CONST_TRUE, CONST_FALSE, CONST_OLD, CONST_NEW, CONST_OLD_VALUE, CONST_NEW_VALUE,
                    CONST_NULL, CONST_CURRENT_TIMESTAMP, CONST_NEWLINE, CONST_TAB_RIGHT
            };
        }

        public static class Comment extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "comment";
            public static final String LINE = CreateKey(OUTER_KEY, "line");
            public static final String BLOCK = CreateKey(OUTER_KEY, "block");
            public static final String TEXT = CreateValue("text");

        }

        public static class Basetype extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "basetype";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }
        }

        public static class Type extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "type";
            private static final String OUTER_KEY_BASE = "base";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }
            public static String BaseKey(final String inner_key)
            {
                return CreateKey(CreateKey(OUTER_KEY, inner_key), OUTER_KEY_BASE);
            }
        }

        public static class Table extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "table";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }

            // values
            public static final String V_NAME = CreateValue("name");
            public static final String V_BODY = CreateValue("body");
            public static final String V_FIELDS = CreateValue("fields");
            public static final String V_TYPE = CreateValue("type");
            public static final String V_DEFAULT = CreateValue("default");
            public static final String V_NULLABLE = CreateValue("nullable");
            public static final String V_PARENT_TABLE = CreateValue("parent_table");
            public static final String V_PARENT_FIELDS = CreateValue("parent_fields");
            public static final String V_VALUES = CreateValue("values");
        }

        public static class View extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "view";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }

            // values
            public static final String V_NAME = CreateValue("name");
            public static final String V_FIELDS = CreateValue("fields");
            public static final String V_BODY = CreateValue("body");
            public static final String V_VARS = CreateValue("vars");
            public static final String V_TABLEVIEWS = CreateValue("tableviews");
            public static final String V_CONDITION = CreateValue("condition");
            public static final String V_ALIAS = CreateValue("alias");
            public static final String V_CASES = CreateValue("cases");
            public static final String V_RESULT = CreateValue("result");
        }

        public static class CompositeRule extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "composite_rule";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }

            // values
            public static final String V_NAME = CreateValue("name");
            public static final String V_COMP_RULE_VARS = CreateValue("comp_rule_vars");
            public static final String V_TYPE = CreateValue("type");
            public static final String V_GLOBAL_VARS = CreateValue("global_vars");
            public static final String V_BODY = CreateValue("body");
            public static final String V_DEFAULT = CreateValue("default");
            public static final String V_FIELD = CreateValue("field");
            public static final String V_FIELDS = CreateValue("fields");
            public static final String V_TABLE = CreateValue("table");
            public static final String V_COMPOSITE_RULE = CreateValue("composite_rule");
            public static final String V_CONDITION = CreateValue("condition");
            public static final String V_VALUES = CreateValue("values");
            public static final String V_IDX_VAR = CreateValue("idx_var");
            public static final String V_FROM_VAR = CreateValue("from_var");
            public static final String V_TO_VAR = CreateValue("to_var");
            public static final String V_VARS = CreateValue("vars");
            public static final String V_SELECT = CreateValue("select");
            public static final String V_VALUE = CreateValue("value");
            public static final String V_VAR = CreateValue("var");
            public static final String V_SELECT_ALL = CreateValue("select_all");
            public static final String V_TABLEVIEWS = CreateValue("tableviews");
            public static final String V_ALIAS = CreateValue("alias");
        }

        public static class Symbol extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "symbol";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }

        }

        public static class Operator extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "operator";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }

        }

        public static class Constant extends Base
        {
            // should not be accessed directly
            private static final String OUTER_KEY = "constant";
            public static String Key(final String inner_key)
            {
                return CreateKey(OUTER_KEY, inner_key);
            }

        }
    }

    // so we don't accidentally instantiate it
    private Globals() {}
}
