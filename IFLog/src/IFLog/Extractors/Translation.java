package IFLog.Extractors;

import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translation extends Base
{
    /**
     *
     * Constructor.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     */
    public Translation(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser)
    {
        this(IFLogParser, IFLogLexer, SQLTargetParser, null);
    }

    /**
     *
     * Constructor if there's already an existing translation map which we want to extend.
     * In any other case we create a new one.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     */
    public Translation(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                       final Map<String, String> TranslationMap)
    {
        super(IFLogParser, IFLogLexer, SQLTargetParser);

        if(TranslationMap == null)
            m_TranslationMap = new HashMap<>();
    }

    /**
     *
     * Access function.
     *
     * @return HashMap
     */
    public Map<String, String> Get() { return m_TranslationMap; }

    /**
     *
     * Main function.
     *
     * (1) Get translation comments (line and/or block comments).
     * (2) Get translation for base types.
     * (3) Get translation for custom types.
     * (4) Get translation for table elements.
     * (5) Get translation for symbols.
     * (6) Get translation for operators.
     * (7) Get translation for constants.
     *
     */
    @Override
    public void Extract()
    {
        // get translation for comments
        // there's only one comment block allowed
        // can include line and/or block comment definition
        var commentBlockCtx = m_SQLTargetParser.prog().comment_block();
        Assert.Equals(
                commentBlockCtx.size(),
                1,
                "exactly one comment block allowed"
        );
        Assert.Equals(
                commentBlockCtx.get(0).comment_ele().size(),
                2,
                "exactly one line and one block comment allowed"
        );
        AddCommentBlockTranslation(commentBlockCtx.get(0));

        ResetSQLTargetParser();

        // get translation for basetypes
        // there's only one basetypes block allowed
        var basetypeBlockCtx = m_SQLTargetParser.prog().basetypes_block();
        Assert.Equals(
                basetypeBlockCtx.size(),
                1,
                "exactly one basetypes block allowed"
        );
        AddBasetypeBlockTranslation(basetypeBlockCtx.get(0));

        ResetSQLTargetParser();

        // get translation for custom types
        // there can be multiple such blocks
        for(var typeBlockCtx : m_SQLTargetParser.prog().type_block())
        {
            AddTypeBlockTranslation(typeBlockCtx);
        }

        ResetSQLTargetParser();

        // get translation for table
        // there's only one table block allowed
        var tableBlockCtx = m_SQLTargetParser.prog().table_block();
        Assert.Equals(
                tableBlockCtx.size(),
                1,
                "exactly one table block allowed"
        );
        AddTableBlockTranslation(tableBlockCtx.get(0));

        ResetSQLTargetParser();

        // get translation for view
        // there's only one view block allowed
        var viewBlockCtx = m_SQLTargetParser.prog().view_block();
        Assert.Equals(
                viewBlockCtx.size(),
                1,
                "exactly one view block allowed"
        );
        AddViewBlockTranslation(viewBlockCtx.get(0));

        ResetSQLTargetParser();

        // get translation for composite rule
        // there's only one composite rule block allowed
        var compositeRuleCtx = m_SQLTargetParser.prog().composite_rule_block();
        Assert.Equals(
                compositeRuleCtx.size(),
                1,
                "exactly one composite rule block allowed"
        );
        AddCompositeRuleBlockTranslation(compositeRuleCtx.get(0));

        ResetSQLTargetParser();

        // get translation for symbols
        // there's only one symbols block allowed
        var symbolsBlockCtx = m_SQLTargetParser.prog().symbols_block();
        Assert.Equals(
                symbolsBlockCtx.size(),
                1,
                "exactly one symbols block allowed"
        );
        AddSymbolsBlockTranslation(symbolsBlockCtx.get(0));

        ResetSQLTargetParser();

        // get translation for operators
        // there's only one operators block allowed
        var operatorsBlockCtx = m_SQLTargetParser.prog().operators_block();
        Assert.Equals(
                operatorsBlockCtx.size(),
                1,
                "exactly one operators block allowed"
        );
        AddOperatorsBlockTranslation(operatorsBlockCtx.get(0));

        ResetSQLTargetParser();

        // get translation for constants
        // there's only one constants block allowed
        var constantsBlockCtx = m_SQLTargetParser.prog().constants_block();
        Assert.Equals(
                constantsBlockCtx.size(),
                1,
                "exactly one constants block allowed"
        );
        AddConstantsBlockTranslation(constantsBlockCtx.get(0));

        ResetSQLTargetParser();
    }

    /**
     *
     * Get translations for line and block comments and add them to the map.
     *
     * (1) Check if line or block comment definition.
     * (2) Add to translation map.
     * (3) Check for duplicates in map.
     *
     * @param commentBlock
     */
    private void AddCommentBlockTranslation(SQLTargetParser.Comment_blockContext commentBlock)
    {
        for(var commentBlockEle : commentBlock.comment_ele())
        {
            var lineComment = commentBlockEle.comment_ele_head().KW_COMMENT_LINE();
            var blockComment = commentBlockEle.comment_ele_head().KW_COMMENT_BLOCK();
            String key = "";

            if(lineComment != null)
                key = Globals.Translation.Comment.LINE;
            else if(blockComment != null)
                key = Globals.Translation.Comment.BLOCK;
            Assert.Unequals(key, "", "not a valid comment");

            String curValue = m_TranslationMap.putIfAbsent(key, TrimValue(commentBlockEle.value.getText()));
            Assert.Equals(
                    curValue,
                    null,
                    "comment type " + key +  " already defined"
            );
        }
    }

    /**
     *
     * Get translations for base types and add them to the map.
     *
     * (1) Add base type to translation map.
     * (2) Check for duplicates in map.
     * (3) Check that all required (fixed) base types have been defined.
     *
     * @param basetypeBlock
     */
    private void AddBasetypeBlockTranslation(SQLTargetParser.Basetypes_blockContext basetypeBlock)
    {
        for(var baseType : basetypeBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.Basetype.Key(baseType.key.getText()),
                    TrimValue(baseType.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "basetype " + baseType.key.getText() + " already defined"
            );
        }

        // check that all the fixed basetypes that are supported by default
        // are properly defined
        for(String fixedBaseType : Globals.Translation.Fixed.BT_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.Basetype.Key(fixedBaseType));
            Assert.Unequals(curValue, null, "definition for basetype " + fixedBaseType + " is missing");
        }
    }

    /**
     *
     * Get translations for custom types and add them to the map.
     *
     * (1) Add custom type to translation map.
     * (2) Check for duplicates in map.
     * (3) Assign mapping to respective base types.
     *
     * @param typeBlock
     */
    private void AddTypeBlockTranslation(SQLTargetParser.Type_blockContext typeBlock)
    {
        for(var type : typeBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.Type.Key(type.key.getText()),
                    TrimValue(type.value.getText())
            );
            Assert.Equals(
                    curValue,
                    null,
                    "type " + type.key.getText() + " already defined"
            );

            // also assign mapping to basetype
            // don't trim the value since it's already an ID
            m_TranslationMap.put(
                    Globals.Translation.Type.BaseKey(type.key.getText()),
                    typeBlock.base_ele().value.getText()
            );
        }
    }


    /**
     *
     * Get translations for table elements and add them to the map.
     *
     * (1) Add table elements to translation map.
     * (2) Check for duplicates in map.
     * (3) Check that all required (fixed) table elements have been defined.
     *
     * @param tableBlock
     */
    private void AddTableBlockTranslation(SQLTargetParser.Table_blockContext tableBlock)
    {
        for(var tableEle : tableBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.Table.Key(tableEle.key.getText()),
                    TrimValue(tableEle.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "table element " + tableEle.key.getText() + " already defined"
            );
        }

        // check that all the fixed elements that are supported by default
        // are properly defined
        for(String fixedTableEle : Globals.Translation.Fixed.TABLE_ELE_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.Table.Key(fixedTableEle));
            Assert.Unequals(
                    curValue,
                    null,
                    "definition for table element " + fixedTableEle + " is missing"
            );
        }
    }

    private void AddViewBlockTranslation(SQLTargetParser.View_blockContext viewBlock)
    {
        for(var viewEle : viewBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.View.Key(viewEle.key.getText()),
                    TrimValue(viewEle.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "view element " + viewEle.key.getText() + " already defined"
            );
        }

        // check that all the fixed elements that are supported by default
        // are properly defined
        for(String fixedViewEle : Globals.Translation.Fixed.VIEW_ELE_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.View.Key(fixedViewEle));
            Assert.Unequals(
                    curValue,
                    null,
                    "definition for view element " + fixedViewEle + " is missing"
            );
        }
    }

    // COMPOSITE_RULE_ELE_ALL
    private void AddCompositeRuleBlockTranslation(SQLTargetParser.Composite_rule_blockContext compositeRuleBlock)
    {
        for(var compositeRuleEle : compositeRuleBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.CompositeRule.Key(compositeRuleEle.key.getText()),
                    TrimValue(compositeRuleEle.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "composite rule element " + compositeRuleEle.key.getText() + " already defined"
            );
        }

        // check that all the fixed elements that are supported by default
        // are properly defined
        for(String fixedViewEle : Globals.Translation.Fixed.COMPOSITE_RULE_ELE_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.CompositeRule.Key(fixedViewEle));
            Assert.Unequals(
                    curValue,
                    null,
                    "definition for composite rule element " + fixedViewEle + " is missing"
            );
        }
    }

    /**
     *
     * Get translations for symbols and add them to the map.
     *
     * (1) Add symbols to translation map.
     * (2) Check for duplicates in map.
     * (3) Check that all required (fixed) symbols have been defined.
     *
     * @param symbolsBlock
     */
    private void AddSymbolsBlockTranslation(SQLTargetParser.Symbols_blockContext symbolsBlock)
    {
        for(var symbol : symbolsBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.Symbol.Key(symbol.key.getText()),
                    TrimValue(symbol.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "symbol " + symbol.key.getText() + " already defined"
            );
        }

        // check that all the fixed symbols that are supported by default
        // are properly defined
        for(String fixedSymbol : Globals.Translation.Fixed.SYM_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.Symbol.Key(fixedSymbol));
            Assert.Unequals(
                    curValue,
                    null,
                    "definition for symbol " + fixedSymbol + " is missing"
            );
        }
    }

    /**
     *
     * Get translations for operators and add them to the map.
     *
     * (1) Add operators to translation map.
     * (2) Check for duplicates in map.
     * (3) Check that all required (fixed) operators have been defined.
     *
     * @param operatorsBlock
     */
    private void AddOperatorsBlockTranslation(SQLTargetParser.Operators_blockContext operatorsBlock)
    {
        for(var operator : operatorsBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.Operator.Key(operator.key.getText()),
                    TrimValue(operator.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "operator " + operator.key.getText() + " already defined"
            );
        }

        // check that all the fixed operators that are supported by default
        // are properly defined
        for(String fixedOperator : Globals.Translation.Fixed.OP_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.Operator.Key(fixedOperator));
            Assert.Unequals(
                    curValue,
                    null,
                    "definition for operator " + fixedOperator + " is missing"
            );
        }
    }

    /**
     *
     * Get translations for constants and add them to the map.
     *
     * (1) Add constants to translation map.
     * (2) Check for duplicates in map.
     * (3) Check that all required (fixed) constants have been defined.
     *
     * @param constantsBlock
     */
    private void AddConstantsBlockTranslation(SQLTargetParser.Constants_blockContext constantsBlock)
    {
        for(var constant : constantsBlock.ele())
        {
            String curValue = m_TranslationMap.putIfAbsent(
                    Globals.Translation.Constant.Key(constant.key.getText()),
                    TrimValue(constant.value.getText())
            );

            Assert.Equals(
                    curValue,
                    null,
                    "constant " + constant.key.getText() + " already defined"
            );
        }

        // check that all the fixed constants that are supported by default
        // are properly defined
        for(String fixedConstant : Globals.Translation.Fixed.CONST_ALL)
        {
            String curValue = m_TranslationMap.get(Globals.Translation.Constant.Key(fixedConstant));
            Assert.Unequals(
                    curValue,
                    null,
                    "definition for constant " + fixedConstant + " is missing"
            );
        }
    }

    /**
     *
     * Simple helper function that removes leading and trailing String delimiters.
     *
     * @param value
     * @return String
     */
    private String TrimValue(String value)
    {
        // remove first and last char: ""
        return value.substring(1, value.length() - 1);
    }
}
