package IFLog.Extractors;

import IFLog.Components.CompositeRule;
import IFLog.Components.Table;
import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetParser;

import java.util.List;
import java.util.Map;

public abstract class Base
{
    /**
     *
     * Constructor.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     */
    public Base(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser)
    {
        this(IFLogParser, IFLogLexer, SQLTargetParser,
                null, null, null, null, null);
    }

    /**
     *
     * Constructor if there are already existing elements that we want to extend.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     * @param CommentList
     * @param TableList
     * @param ViewList
     * @param CompositeRuleList
     */
    public Base(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                final Map<String, String> TranslationMap, final List<IFLog.Components.Comment> CommentList,
                final List<IFLog.Components.Table> TableList, final List<IFLog.Components.View> ViewList,
                final List<IFLog.Components.CompositeRule> CompositeRuleList)
    {
        m_IFLogParser = IFLogParser;
        m_IFLogLexer = IFLogLexer;
        m_SQLTargetParser = SQLTargetParser;

        m_TranslationMap = TranslationMap;
        m_CommentList = CommentList;
        m_TableList = TableList;
        m_ViewList = ViewList;
        m_CompositeRuleList = CompositeRuleList;
    }

    /**
     *
     * Extract function that should be overridden by all child classes.
     *
     */
    public void Extract() {}

    /**
     *
     * Helper function that resets the IFLog Lexer to the beginning.
     *
     */
    protected final void ResetIFLogLexer()
    {
        m_IFLogLexer.reset();
    }
    /**
     *
     * Helper function that resets the IFLog Parser to the beginning.
     *
     */
    protected final void ResetIFLogParser()
    {
        m_IFLogParser.reset();
    }
    /**
     *
     * Helper function that resets the SQLTarget Parser to the beginning.
     *
     */
    protected final void ResetSQLTargetParser()
    {
        m_SQLTargetParser.reset();
    }

    /**
     *
     * Helper function to check if container already exists (based on name).
     *
     * (1) Check in tables.
     * (2) Check in views.
     * (3) Check in composite rules.
     *
     * @param name
     * @return boolean
     */
    protected final boolean ContainerExists(String name)
    {
        boolean ret = false;

        if(m_TableList != null)
        {
            for(var table : m_TableList)
            {
                if(name.equals(table.m_Name))
                {
                    ret = true;
                    break;
                }
            }
        }

        if(!ret && m_ViewList != null)
        {
            for(var view : m_ViewList)
            {
                if(name.equals(view.m_Name))
                {
                    ret = true;
                    break;
                }
            }
        }

        if(!ret && m_CompositeRuleList != null)
        {
            for(var compRule : m_CompositeRuleList)
            {
                if(name.equals(compRule.m_Name))
                {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    protected final IFLog.Components.Table GetTable(String name)
    {
        IFLog.Components.Table ret = null;

        for(var table : m_TableList)
        {
            if(table.m_Name.equals(name))
            {
                ret = table;
                break;
            }
        }

        return ret;
    }

    protected final IFLog.Components.View GetView(String name)
    {
        IFLog.Components.View ret = null;

        for(var view : m_ViewList)
        {
            if(view.m_Name.equals(name))
            {
                ret = view;
                break;
            }
        }

        return ret;
    }

    protected final IFLog.Components.CompositeRule GetCompositeRule(String name)
    {
        IFLog.Components.CompositeRule ret = null;

        for(var compositeRule : m_CompositeRuleList)
        {
            if(compositeRule.m_Name.equals(name))
            {
                ret = compositeRule;
                break;
            }
        }

        return ret;
    }

    /* ======================================================================== */
    /* Type Getters */
    /* ======================================================================== */

    /**
     *
     * Helper function to get base type of derived type.
     *
     * (1) Check if type is a base type.
     * (2) Check if type is a custom type and get it's base type.
     * (3) Return the base type else throw an error.
     *
     * @param type
     * @return String
     */
    protected final String GetBasetype(String type)
    {
        return GetBasetype(type, false);
    }

    protected final String GetBasetype(String type, boolean no_auto)
    {
        // check in base types
        String ret = m_TranslationMap.get(Globals.Translation.Basetype.Key(type));

        // if not found there, check in types
        if(ret == null)
        {
            ret = m_TranslationMap.get(Globals.Translation.Type.BaseKey(type));
            Assert.Unequals(ret, null, "type " + type + " doesn't exist in translation target");
        }
        // if it's already a valid basetype
        else
        {
            ret = type;
        }

        if(no_auto && ret.equals(Globals.Translation.Fixed.BT_AUTO))
        {
            ret = Globals.Translation.Fixed.BT_INT;
        }

        return ret;
    }

    /**
     *
     * Helper function to check if a base type with the name type exists.
     *
     * @param type
     * @return boolean
     */
    protected final boolean BasetypeExists(final String type)
    {
        return (m_TranslationMap.get(Globals.Translation.Basetype.Key(type)) != null);
    }

    /**
     *
     * Helper function to get type for specified field in specified table.
     * Throws an error if there's no such table or the field doesn't exist in the table.
     *
     * @param parent_field
     * @param parent_name
     * @return String
     */
    protected final String GetTypeForField(String parent_field, String parent_name)
    {
        IFLog.Components.Table parent = null;
        String type = null;
        for(var table : m_TableList)
        {
            if(table.m_Name.equals(parent_name))
            {
                parent = table;
                break;
            }
        }

        Assert.Unequals(parent, null, "couldn't find parent table " + parent_name);

        for(var field : parent.m_Fields)
        {
            if(field.m_Name.equals(parent_field))
            {
                type = field.m_Type;
                break;
            }
        }

        Assert.Unequals(type, null, "couldn't find type for parent field " + parent_field);

        return type;
    }

    /**
     *
     * Helper function to get base type for specified field in specified table.
     *
     * @param fieldName
     * @param curTable
     * @return String
     */
    protected final String GetBasetypeForField(String fieldName, Table curTable)
    {
        Table.Field field = curTable.GetField(fieldName);

        Assert.Unequals(
                field,
                null,
                "field " + fieldName + " not found in table " + curTable.m_Name);

        String baseType = null;
        for(var bt : Globals.Translation.Fixed.BT_ALL)
        {
            if(bt.equals(GetBasetype(field.m_Type, false)))
            {
                baseType = bt;
                break;
            }
        }

        Assert.Unequals(
                baseType,
                null,
                "couldn't find matching basetype for field " + field.m_Type
                        + " in table " + curTable.m_Name);

        return baseType;
    }

    /**
     *
     * Helper function to get base type for specified value.
     *
     * @param value
     * @param container
     * @return String
     */
    protected final String GetBasetypeForValue(IFLogParser.Any_ret_valueContext value, String container)
    {
        String baseType = null;
        // if it's a string or char
        if(value.V_CHAR_STRING_DOUBLE() != null || value.V_CHAR_STRING_SINGLE() != null
                || value.V_NEWLINE() != null || value.V_TABRIGHT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_STRING;
        }
        // if it's a bool
        else if(value.V_BOOL() != null)
        {
            baseType = Globals.Translation.Fixed.BT_BOOL;
        }
        // if it's an int
        else if(value.V_INT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_INT;
        }
        // if it's a float
        else if(value.V_FLOAT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_FLOAT;
        }
        // if it's a date
        else if(value.V_CURRENT_TIMESTAMP() != null)
        {
            baseType = Globals.Translation.Fixed.BT_DATE;
        }
        // if it's null (should probably always be a valid value for any type)
        else if(value.V_NULL() != null)
        {
            baseType = Globals.Translation.Fixed.BT_VOID;
        }
        // if it's new or old
        else if(value.V_TRIGGER() != null)
        {
            baseType = Globals.Translation.Fixed.BT_TRIGGER;
        }

        Assert.Unequals(
                baseType,
                null,
                "couldn't find matching basetype for value " + value.getText()
                        + " in container " + container);

        return baseType;
    }

    protected final String GetBasetypeForValue(IFLogParser.Any_valueContext value, String container)
    {
        String baseType = null;
        // if it's a string or char
        if(value.V_CHAR_STRING_DOUBLE() != null || value.V_CHAR_STRING_SINGLE() != null
                || value.V_NEWLINE() != null || value.V_TABRIGHT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_STRING;
        }
        // if it's a bool
        else if(value.V_BOOL() != null)
        {
            baseType = Globals.Translation.Fixed.BT_BOOL;
        }
        // if it's an int
        else if(value.V_INT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_INT;
        }
        // if it's a float
        else if(value.V_FLOAT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_FLOAT;
        }
        // if it's a date
        else if(value.V_CURRENT_TIMESTAMP() != null)
        {
            baseType = Globals.Translation.Fixed.BT_DATE;
        }
        // if it's null (should probably always be a valid value for any type)
        else if(value.V_NULL() != null)
        {
            baseType = Globals.Translation.Fixed.BT_VOID;
        }

        Assert.Unequals(
                baseType,
                null,
                "couldn't find matching basetype for value " + value.getText()
                        + " in container " + container);

        return baseType;
    }

    protected final String GetBasetypeForValue(IFLogParser.Any_non_null_valueContext value, String container)
    {
        String baseType = null;
        // if it's a string or char
        if(value.V_CHAR_STRING_DOUBLE() != null || value.V_CHAR_STRING_SINGLE() != null
                || value.V_NEWLINE() != null || value.V_TABRIGHT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_STRING;
        }
        // if it's a bool
        else if(value.V_BOOL() != null)
        {
            baseType = Globals.Translation.Fixed.BT_BOOL;
        }
        // if it's an int
        else if(value.V_INT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_INT;
        }
        // if it's a float
        else if(value.V_FLOAT() != null)
        {
            baseType = Globals.Translation.Fixed.BT_FLOAT;
        }
        // if it's a date
        else if(value.V_CURRENT_TIMESTAMP() != null)
        {
            baseType = Globals.Translation.Fixed.BT_DATE;
        }

        Assert.Unequals(
                baseType,
                null,
                "couldn't find matching basetype for value " + value.getText()
                        + " in container " + container);

        return baseType;
    }

    /**
     *
     * Helper function to get (final) type of arithmetic statement.
     *
     * @param lhs
     * @param rhs
     * @param arithmeticOp
     * @param container
     * @return String
     */
    protected final String GetTypeOfArithmeticStatement(String lhs, String rhs, IFLogParser.Any_arithmetic_opContext arithmeticOp, String container)
    {
        // +
        // lhs: string/char -> rhs: string/char/date/bool/int/auto/float/void -> res: string
        // rhs: string/char -> lhs: string/char/date/bool/int/auto/float/void -> res: string
        // lhs: int/auto -> rhs: int/auto -> res: int
        // lhs: float -> rhs: float -> res: float

        // -
        // lhs: int/auto -> rhs: int/auto -> res: int
        // lhs: float -> rhs: float -> res: float

        // *
        // lhs: int/auto -> rhs: int/auto -> res: int
        // lhs: float -> rhs: float -> res: float

        // /
        // lhs: int/auto -> rhs: int/auto -> res: int
        // lhs: float -> rhs: float -> res: float

        // %
        // lhs: int/auto -> rhs: int/auto -> res: int
        // lhs: float -> rhs: float -> res: float

        String[] lhsList = lhs.split(Globals.Translation.Fixed.SYM_FLAG);
        String[] rhsList = rhs.split(Globals.Translation.Fixed.SYM_FLAG);

        String lhsValue = lhsList[0];
        String rhsValue = rhsList[0];
        String lhsType = lhsList[1];
        String rhsType = rhsList[1];
        String statType = null;

        // if one is unknown pick the other
        // technically this shouldn't ever occur in practice
        if(lhsType.equals(Globals.Translation.Fixed.BT_UNKNOWN))
        {
            statType = rhsType;
        }
        else if(rhsType.equals(Globals.Translation.Fixed.BT_UNKNOWN))
        {
            statType = lhsType;
        }
        // if string on lhs
        else if(lhsType.equals(Globals.Translation.Fixed.BT_STRING)
                || lhsType.equals(Globals.Translation.Fixed.BT_CHAR))
        {
            Assert.Unequals(
                    arithmeticOp.SYM_ADD(),
                    null,
                    arithmeticOp.getText() + " is not a valid operator for values of type "
                            + lhsType + " in container " + container
            );

            Assert.Check(
                    rhsType.equals(Globals.Translation.Fixed.BT_STRING)
                            || rhsType.equals(Globals.Translation.Fixed.BT_CHAR)
                            || rhsType.equals(Globals.Translation.Fixed.BT_DATE)
                            || rhsType.equals(Globals.Translation.Fixed.BT_BOOL)
                            || rhsType.equals(Globals.Translation.Fixed.BT_INT)
                            || rhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                            || rhsType.equals(Globals.Translation.Fixed.BT_FLOAT)
                            || rhsType.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch for arithmetic values " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );

            statType = Globals.Translation.Fixed.BT_STRING;
        }
        // if string on rhs
        else if(rhsType.equals(Globals.Translation.Fixed.BT_STRING)
                || rhsType.equals(Globals.Translation.Fixed.BT_CHAR))
        {
            Assert.Unequals(
                    arithmeticOp.SYM_ADD(),
                    null,
                    arithmeticOp.getText() + " is not a valid operator for values of type "
                            + lhsType + " container " + container
            );

            Assert.Check(
                    lhsType.equals(Globals.Translation.Fixed.BT_DATE)
                            || lhsType.equals(Globals.Translation.Fixed.BT_BOOL)
                            || lhsType.equals(Globals.Translation.Fixed.BT_INT)
                            || lhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                            || lhsType.equals(Globals.Translation.Fixed.BT_FLOAT)
                            || lhsType.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch for arithmetic values " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );

            statType = Globals.Translation.Fixed.BT_STRING;
        }
        // if lhs or rhs are numbers
        else if(lhsType.equals(Globals.Translation.Fixed.BT_INT)
                || lhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                || lhsType.equals(Globals.Translation.Fixed.BT_FLOAT))
        {
            Assert.Check(
                    arithmeticOp.SYM_ADD() != null
                            || arithmeticOp.SYM_SUB() != null
                            || arithmeticOp.SYM_MUL() != null
                            || arithmeticOp.SYM_DIV() != null
                            || arithmeticOp.SYM_MOD() != null,
                    arithmeticOp.getText() + " is not a valid operator for values of type "
                            + lhsType + " in container " + container
            );

            Assert.Check(
                    rhsType.equals(Globals.Translation.Fixed.BT_INT)
                            || rhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                            || rhsType.equals(Globals.Translation.Fixed.BT_FLOAT),
                    "type mismatch for arithmetic values " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );

            if(lhsType.equals(Globals.Translation.Fixed.BT_FLOAT)
                    || rhsType.equals(Globals.Translation.Fixed.BT_FLOAT))
            {
                statType = Globals.Translation.Fixed.BT_FLOAT;
            }
            else
            {
                statType = Globals.Translation.Fixed.BT_INT;
            }
        }

        Assert.Unequals(
                statType,
                null,
                "couldn't determine type for arithmetic statement in container " + container
        );

        return statType;
    }

    /* ======================================================================== */
    /* Type Checks */
    /* ======================================================================== */

    /**
     *
     * Helper function to check if types of lhs and rhs in a comparison are valid.
     *
     * @param lhs
     * @param rhs
     * @param container
     */
    protected final void ValidateTypesInComparisonStatement(String lhs, String rhs, String container)
    {
        String[] lhsList = lhs.split(Globals.Translation.Fixed.SYM_FLAG);
        String[] rhsList = rhs.split(Globals.Translation.Fixed.SYM_FLAG);

        String lhsValue = lhsList[0];
        String rhsValue = rhsList[0];
        String lhsType = lhsList[1];
        String rhsType = rhsList[1];

        // if lhs or rhs is a string
        if(lhsType.equals(Globals.Translation.Fixed.BT_STRING)
                || lhsType.equals(Globals.Translation.Fixed.BT_CHAR))
        {
            Assert.Check(
                    rhsType.equals(Globals.Translation.Fixed.BT_STRING)
                            || rhsType.equals(Globals.Translation.Fixed.BT_CHAR)
                            || rhsType.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch in comparison between " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );
        }
        // if lhs or rhs is a bool
        else if(lhsType.equals(Globals.Translation.Fixed.BT_BOOL))
        {
            Assert.Check(
                    rhsType.equals(Globals.Translation.Fixed.BT_BOOL)
                            || rhsType.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch in comparison between " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );
        }
        // if lhs or rhs is a number
        else if(lhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                || lhsType.equals(Globals.Translation.Fixed.BT_INT)
                || lhsType.equals(Globals.Translation.Fixed.BT_FLOAT))
        {
            Assert.Check(
                    rhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                            || rhsType.equals(Globals.Translation.Fixed.BT_INT)
                            || rhsType.equals(Globals.Translation.Fixed.BT_FLOAT)
                            || rhsType.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch in comparison between " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );
        }
        // if lhs is null
        else if(lhsType.equals(Globals.Translation.Fixed.BT_VOID))
        {
            Assert.Check(
                    rhsType.equals(Globals.Translation.Fixed.BT_STRING)
                            || rhsType.equals(Globals.Translation.Fixed.BT_CHAR)
                            || rhsType.equals(Globals.Translation.Fixed.BT_AUTO)
                            || rhsType.equals(Globals.Translation.Fixed.BT_INT)
                            || rhsType.equals(Globals.Translation.Fixed.BT_FLOAT),
                    "type mismatch in comparison between " + lhsValue
                            + " and " + rhsValue + " in container " + container
            );
        }
    }

    /**
     *
     * Helper function to check if type of lhs and rhs in a list check (in-operator) are valid.
     *
     * @param lhsBasetype
     * @param rhsBasetype
     * @param rhsValue
     * @param container
     */
    protected final void ValidateTypeInList(String lhsBasetype, String rhsBasetype, String rhsValue, String container)
    {
        // if lhs is a string
        if(lhsBasetype.equals(Globals.Translation.Fixed.BT_STRING)
                || lhsBasetype.equals(Globals.Translation.Fixed.BT_CHAR) )
        {
            Assert.Check(
                    rhsBasetype.equals(Globals.Translation.Fixed.BT_STRING)
                            || rhsBasetype.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch for list value " + rhsValue
                            + " in container " + container
            );
        }
        // if is a bool
        else if(lhsBasetype.equals(Globals.Translation.Fixed.BT_BOOL))
        {
            Assert.Check(
                    rhsBasetype.equals(Globals.Translation.Fixed.BT_BOOL)
                            || rhsBasetype.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch for list value " + rhsValue
                            + " in container " + container
            );
        }
        // if lhs is an int
        else if(lhsBasetype.equals(Globals.Translation.Fixed.BT_AUTO)
                || lhsBasetype.equals(Globals.Translation.Fixed.BT_INT))
        {
            Assert.Check(
                    rhsBasetype.equals(Globals.Translation.Fixed.BT_INT)
                            || rhsBasetype.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch for list value " + rhsValue
                            + " in container " + container
            );
        }
        // if lhs is a float
        else if(lhsBasetype.equals(Globals.Translation.Fixed.BT_FLOAT))
        {
            Assert.Check(
                    rhsBasetype.equals(Globals.Translation.Fixed.BT_FLOAT)
                            || rhsBasetype.equals(Globals.Translation.Fixed.BT_VOID),
                    "type mismatch for list value " + rhsValue
                            + " in container " + container
            );
        }
    }


    /**
     *
     * Helper function to check if type of element and field are valid for a specific table.
     *
     * @param field
     * @param element
     * @param container
     */
    protected final void ValidateTypeInDataInsert(IFLog.Components.Table.Field field, String element, String container)
    {
        String[] elementList = element.split(Globals.Translation.Fixed.SYM_FLAG);

        String fieldName = field.m_Name;
        String fieldType = GetBasetype(field.m_Type, true);
        boolean fieldIsNullable = field.m_Nullable;
        String elementValue = elementList[0];
        String elementType = elementList[1];

        ValidateTypeInDataInsert(fieldName, fieldType, elementValue, elementType, container, fieldIsNullable);
    }

    protected final void ValidateTypeInDataInsert(IFLog.Components.View.Field field, String element, String container)
    {
        String[] elementList = element.split(Globals.Translation.Fixed.SYM_FLAG);

        String fieldName = field.m_Name;
        // should always be a base type anyway
        String fieldType = field.m_Type;
        String elementValue = elementList[0];
        String elementType = elementList[1];

        ValidateTypeInDataInsert(fieldName, fieldType, elementValue, elementType, container, null);
    }

    protected final void ValidateTypeInDataInsert(IFLog.Components.CompositeRule.CompRuleVar compRuleVar, String element, String container)
    {
        String[] elementList = element.split(Globals.Translation.Fixed.SYM_FLAG);

        String fieldName = compRuleVar.m_Name;
        // should always be a base type anyway
        String fieldType = compRuleVar.m_Type;
        String elementValue = elementList[0];
        String elementType = elementList[1];

        ValidateTypeInDataInsert(fieldName, fieldType, elementValue, elementType, container, null);
    }

    private void ValidateTypeInDataInsert(
            String fieldName, String fieldType, String elementValue, String elementType, String container, Boolean fieldIsNullable)
    {
        // if element is unknown skip the check
        if(elementType.equals(Globals.Translation.Fixed.BT_UNKNOWN))
        {
            // do nothing
        }
        // if element is null (should only be checked in table data insert)
        else if(fieldIsNullable != null && elementType.equals(Globals.Translation.Fixed.BT_VOID))
        {
            Assert.Check(
                    fieldIsNullable,
                    "type mismatch in data insert in container "  + container
                            + " field " + fieldName + " is not nullable  "
            );
        }
        // if field is a date
        else if(fieldType.equals(Globals.Translation.Fixed.BT_DATE))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_DATE),
                    "type mismatch in data insert for field " + fieldName
                            + " with value " + elementValue + " in container " + container
            );
        }
        // if field is a string
        else if(fieldType.equals(Globals.Translation.Fixed.BT_STRING)
                || fieldType.equals(Globals.Translation.Fixed.BT_CHAR))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_STRING)
                            || elementType.equals(Globals.Translation.Fixed.BT_CHAR),
                    "type mismatch in data insert for field " + fieldName
                            + " with value " + elementValue + " in container " + container
            );
        }
        // if field is a bool
        else if(fieldType.equals(Globals.Translation.Fixed.BT_BOOL))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_BOOL),
                    "type mismatch in data insert for field " + fieldName
                            + " with value " + elementValue + " in container " + container
            );
        }
        // if field is a number
        else if(fieldType.equals(Globals.Translation.Fixed.BT_INT)
                || fieldType.equals(Globals.Translation.Fixed.BT_FLOAT))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_INT)
                            || elementType.equals(Globals.Translation.Fixed.BT_FLOAT),
                    "type mismatch in data insert for field " + fieldName
                            + " with value " + elementValue + " in container " + container
            );
        }
    }

    /* ======================================================================== */
    /* Translation */
    /* ======================================================================== */

    /**
     *
     * Helper function that translates a logical operator and returns it as a string.
     *
     * @param logicalOp
     * @return String
     */
    protected final String GetLogicalOperatorTranslation(IFLogParser.Any_logical_opContext logicalOp)
    {
        String ret = null;

        if(logicalOp.SYM_AND() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_AND)
            );
        }
        else if(logicalOp.SYM_OR() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_OR)
            );
        }

        Assert.Unequals(
                ret,
                null,
                "no translation found for logical operator " + logicalOp.getText()
        );

        return ret;
    }

    /**
     *
     * Helper function that translates a comparison operator and returns it as a string.
     * Treat null-elements (can be lhs or rhs) as a special case.
     *
     * @param comparisonOp
     * @param nullElement
     * @return String
     */
    protected final String GetComparisonOperatorTranslation(IFLogParser.Any_comparison_opContext comparisonOp, boolean nullElement)
    {
        String ret = null;

        if(comparisonOp.SYM_EQ() != null)
        {
            // special case if lhs or rhs is null
            if(nullElement)
            {
                ret = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_NULL_EQ)
                );
            }
            else
            {
                ret = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_EQ)
                );
            }
        }
        else if(comparisonOp.SYM_UE() != null)
        {
            // special case if lhs or rhs is null
            if(nullElement)
            {
                ret = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_NULL_UE)
                );
            }
            else
            {
                ret = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_UE)
                );
            }
        }
        else if(comparisonOp.SYM_GE() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_GE)
            );
        }
        else if(comparisonOp.SYM_GT() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_GT)
            );
        }
        else if(comparisonOp.SYM_LE() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_LE)
            );
        }
        else if(comparisonOp.SYM_LT() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_LT)
            );
        }

        Assert.Unequals(
                ret,
                null,
                "no translation found for comparison operator " + comparisonOp.getText()
        );

        return ret;
    }

    /**
     *
     * Helper function that translates a list as a whole (including list symbols and operator) and returns it as a string.
     *
     *
     * @param listCtx
     * @return String
     */
    protected final String GetListTranslation(IFLogParser.V_listContext listCtx)
    {
        StringBuilder listBody = new StringBuilder();

        listBody.append(
                m_TranslationMap.get(
                        Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_LIST_LHS)
                )
        );
        int listIdx = 0;
        for(var valueCtx : listCtx.values)
        {
            String value = GetValueTranslation(valueCtx);

            if(listIdx > 0)
            {
                listBody.append(listCtx.SYM_AND().get(0)).append(" ");
            }

            listBody.append(value);

            listIdx++;
        }
        listBody.append(
                m_TranslationMap.get(
                        Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_LIST_RHS)
                )
        );

        return listBody.toString();
    }

    /**
     *
     * Helper function that translates a fixed atomic value and returns it as a string.
     * Also translates relevant symbols (like string delimiters).
     *
     * @param valueCtx
     * @return String
     */
    protected final String GetValueTranslation(IFLogParser.Any_valueContext valueCtx)
    {
        String value = null;

        if(valueCtx.V_CHAR_STRING_SINGLE() != null)
        {
            String stringSymbol = m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_CHAR_STRING_SINGLE)
            );

            value = stringSymbol + valueCtx.getText().substring(1,valueCtx.getText().length() - 1) + stringSymbol;
        }
        else if(valueCtx.V_CHAR_STRING_DOUBLE() != null)
        {
            String stringSymbol = m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_CHAR_STRING_DOUBLE)
            );

            value = stringSymbol + valueCtx.getText().substring(1,valueCtx.getText().length() - 1) + stringSymbol;
        }
        else if(valueCtx.V_NEWLINE() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_NEWLINE)
            );
        }
        else if(valueCtx.V_TABRIGHT() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_TAB_RIGHT)
            );
        }
        else if(valueCtx.V_NULL() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_NULL)
            );
        }
        else if(valueCtx.V_CURRENT_TIMESTAMP() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_CURRENT_TIMESTAMP)
            );
        }
        else if(valueCtx.V_BOOL() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(valueCtx.getText())
            );
        }
        else if(valueCtx.V_INT() != null || valueCtx.V_FLOAT() != null)
        {
            value = valueCtx.getText();
        }

        Assert.Unequals(
                value,
                null,
                "no translation found for value " + valueCtx.getText()
        );

        return value;
    }

    protected final String GetValueTranslation(IFLogParser.Any_ret_valueContext valueCtx)
    {
        String value = null;

        if(valueCtx.V_CHAR_STRING_SINGLE() != null)
        {
            String stringSymbol = m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_CHAR_STRING_SINGLE)
            );

            value = stringSymbol + valueCtx.getText().substring(1,valueCtx.getText().length() - 1) + stringSymbol;
        }
        else if(valueCtx.V_CHAR_STRING_DOUBLE() != null)
        {
            String stringSymbol = m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_CHAR_STRING_DOUBLE)
            );

            value = stringSymbol + valueCtx.getText().substring(1,valueCtx.getText().length() - 1) + stringSymbol;
        }
        else if(valueCtx.V_NEWLINE() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_NEWLINE)
            );
        }
        else if(valueCtx.V_TABRIGHT() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_TAB_RIGHT)
            );
        }
        else if(valueCtx.V_NULL() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_NULL)
            );
        }
        else if(valueCtx.V_CURRENT_TIMESTAMP() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_CURRENT_TIMESTAMP)
            );
        }
        else if(valueCtx.V_BOOL() != null)
        {
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(valueCtx.getText())
            );
        }
        else if(valueCtx.V_INT() != null || valueCtx.V_FLOAT() != null)
        {
            value = valueCtx.getText();
        }
        else if(valueCtx.V_TRIGGER() != null)
        {
            // if it's old
            if(valueCtx.getText().equals(Globals.Translation.Fixed.CONST_OLD))
            {
                value = m_TranslationMap.get(
                        Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_OLD_VALUE)
                );
            }
            // if it's new
            else
            {
                value = m_TranslationMap.get(
                        Globals.Translation.Constant.Key(Globals.Translation.Fixed.CONST_NEW_VALUE)
                );
            }

        }

        Assert.Unequals(
                value,
                null,
                "no translation found for value " + valueCtx.getText()
        );

        return value;
    }

    /**
     *
     * Helper function that translates an arithmetic operator and returns it as a string.
     * Treat string-elements (can be lhs or rhs) as a special case.
     *
     * @param arithmeticOp
     * @param stringElement
     * @return String
     */
    protected final String GetArithmeticOperatorTranslation(IFLogParser.Any_arithmetic_opContext arithmeticOp, boolean stringElement)
    {
        String ret = null;

        if(arithmeticOp.SYM_ADD() != null)
        {
            // special case if lhs or rhs is a string
            // create a concatenation instead
            if(stringElement)
            {
                ret = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_CONCAT)
                );
            }
            else
            {
                ret = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_ADD)
                );
            }
        }
        else if(arithmeticOp.SYM_SUB() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_SUB)
            );
        }
        else if(arithmeticOp.SYM_MUL() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_MUL)
            );
        }
        else if(arithmeticOp.SYM_DIV() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_DIV)
            );
        }
        else if(arithmeticOp.SYM_MOD() != null)
        {
            ret = m_TranslationMap.get(
                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_MOD)
            );
        }

        Assert.Unequals(
                ret,
                null,
                "no translation found for arithmetic operator " + arithmeticOp.getText()
        );

        return ret;
    }

    /* ======================================================================== */
    /* Members */
    /* ======================================================================== */

    protected final IFLogParser m_IFLogParser;
    protected final IFLogLexer m_IFLogLexer;
    protected final SQLTargetParser m_SQLTargetParser;

    // these should all be null by default
    protected Map<String, String>                      m_TranslationMap;
    protected List<IFLog.Components.Comment>           m_CommentList;
    protected List<IFLog.Components.Table>             m_TableList;
    protected List<IFLog.Components.View>              m_ViewList;
    protected List<IFLog.Components.CompositeRule>     m_CompositeRuleList;
}

