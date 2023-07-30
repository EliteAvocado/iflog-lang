package IFLog.Extractors;

import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Core.Logger;
import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetParser;
import IFLog.Components.Table;
import IFLog.Components.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompositeRule extends Base
{
    /**
     *
     * Constructor.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     * @param TableList
     * @param ViewList
     */
    public CompositeRule(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                         final Map<String, String> TranslationMap, final List<IFLog.Components.Table> TableList,
                         final List<View> ViewList)
    {
        this(IFLogParser, IFLogLexer, SQLTargetParser, TranslationMap, TableList, ViewList, null);
    }

    /**
     *
     * Constructor if there's already an existing composite rule list which we want to extend.
     * In any other case we create a new one.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     * @param TableList
     * @param ViewList
     */
    public CompositeRule(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                         final Map<String, String> TranslationMap, final List<Table> TableList,
                         final List<View> ViewList, final List<CompositeRule> CompositeRuleList)
    {
        super(IFLogParser, IFLogLexer, SQLTargetParser);

        m_TranslationMap = TranslationMap;
        m_TableList = TableList;
        m_ViewList = ViewList;

        if(CompositeRuleList == null)
            m_CompositeRuleList = new ArrayList<>();
    }

    /**
     *
     * Access function.
     *
     * @return ArrayList
     */
    public List<IFLog.Components.CompositeRule> Get() { return m_CompositeRuleList; }


    /**
     *
     * Main function.
     *
     */
    @Override
    public void Extract()
    {
        // TODO: add dependency graph and rearrange composite rules if one uses another
        //  also throw an error if there's a cyclic dependency

        for(var compositeRuleCtx : m_IFLogParser.prog().composite_rule())
        {
            // check if return type is valid
            if(compositeRuleCtx.ret_type != null)
            {
                Assert.Check(
                        BasetypeExists(compositeRuleCtx.ret_type.getText()),
                        "type " + compositeRuleCtx.ret_type.getText() +
                                " doesn't exist in translation target as a base type"
                );
            }

            // add name and return type (if known)
            IFLog.Components.CompositeRule curCompositeRule = new IFLog.Components.CompositeRule(
                    compositeRuleCtx.name.getText(),
                    (compositeRuleCtx.ret_type != null) ? compositeRuleCtx.ret_type.getText() : null
            );

            // add compRuleVars to composite rule
            for(var field : compositeRuleCtx.composite_rule_arg())
            {
                AddCompRuleVarToCompositeRule(field, curCompositeRule);
            }

            // add global comp vars to composite rule
            if(compositeRuleCtx.composite_rule_global().size() > 0)
            {
                Assert.Equals(
                        compositeRuleCtx.composite_rule_global().size(),
                        1,
                        "there can be at most one global definition"
                );

                for(var globalVar : compositeRuleCtx.composite_rule_global().get(0).vars)
                {
                    AddGlobalVarToCompositeRule(globalVar, curCompositeRule);
                }

                // init for later
                curCompositeRule.m_AllVars.addAll(curCompositeRule.m_GlobalVars);
            }

            // add triggers to composite rule
            if(compositeRuleCtx.composite_rule_event().size() > 0)
            {
                Assert.Check(
                        compositeRuleCtx.composite_rule_event().size() <= 2,
                        "there can be at most two event definitions (before and after)"
                );

                if(compositeRuleCtx.composite_rule_event().size() > 1)
                {
                    Assert.Unequals(
                            compositeRuleCtx.composite_rule_event().get(0).type.getText(),
                            compositeRuleCtx.composite_rule_event().get(1).type.getText(),
                            "there can be at most one of each event definition (before and after)"
                    );
                }

                for(var compositeRuleEvent : compositeRuleCtx.composite_rule_event())
                {
                    AddTriggerToCompositeRule(compositeRuleEvent, curCompositeRule);
                }

                // set return type to trigger if it should be derived
                if(curCompositeRule.m_ReturnType == null)
                {
                    curCompositeRule.m_ReturnType = Globals.Translation.Fixed.BT_TRIGGER;
                }
                // else check that it's set to trigger
                else
                {
                    Assert.Equals(
                            curCompositeRule.m_ReturnType,
                            Globals.Translation.Fixed.BT_TRIGGER,
                            "trigger events defined for composite rule of non-trigger type in " +
                                    curCompositeRule.m_Name
                    );
                }
            }

            boolean hasReturnStat = false;
            int compRuleIdx = 0;
            for(var compRule : compositeRuleCtx.comp_rule())
            {
                String localVarPrefix = "lvar" + compRuleIdx + "_";
                String curCompRuleBlockName = (compRule.name != null) ? compRule.name.getText() : null;

                IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock = new IFLog.Components.CompositeRule.CompRuleBlock(
                        curCompRuleBlockName,
                        null
                );

                IFLog.Components.CompositeRule.CompRuleBlock parentCompRuleBlock = null;

                // body
                if(compRule.body != null)
                {
                    // there are tags
                    if(compRule.body.b_tags != null)
                    {
                        CompRuleTagResult cr = ParseCompRuleTags(compRule.body.b_tags, curCompositeRule);

                        // catch tags that are defined in the wrong order
                        // or are missing a tag along the path
                        Assert.Check(
                                cr != null,
                                "couldn't process tags " + compRule.body.b_tags.getText() +
                                        " in composite rule " + curCompositeRule.m_Name +
                                        " check that all tags are defined in the proper order and none are missing along the path"
                        );

                        parentCompRuleBlock = cr.m_ParentCompRuleBlock;
                        curCompRuleBlock.m_AlternativeTo = cr.m_AlternativeTo;
                    }

                    // init compRuleBlock
                    // if there's a parentBlock
                    if(parentCompRuleBlock != null)
                    {
                        curCompRuleBlock.AddMultipleGlobalVars(parentCompRuleBlock.m_GlobalVars);
                    }
                    // if it's at root level
                    else
                    {
                        curCompRuleBlock.AddMultipleGlobalVars(curCompositeRule.m_GlobalVars);
                    }

                    // it's one or more selects
                    if(compRule.body.b_selects != null)
                    {
                        AddSelectsToCompRuleBlock(compRule.body.b_selects, curCompRuleBlock, curCompositeRule, localVarPrefix);
                    }
                    // it's a foreach-loop
                    else if(compRule.body.b_multi_select != null)
                    {
                        AddForEachLoopToCompRuleBlock(compRule.body.b_multi_select, curCompRuleBlock, curCompositeRule, localVarPrefix);
                    }
                    // it's a for-loop
                    else if(compRule.body.b_between != null)
                    {
                        AddForLoopToCompRuleBlock(compRule.body.b_between, curCompRuleBlock, curCompositeRule, localVarPrefix);
                    }
                    // it's one or more if-statements
                    else if(compRule.body.b_ifs != null)
                    {
                        AddIfCondToCompRuleBlock(compRule.body.b_ifs, curCompRuleBlock, curCompositeRule, localVarPrefix);
                    }
                }
                // if there's no body then init with root level
                else
                {
                    curCompRuleBlock.AddMultipleGlobalVars(curCompositeRule.m_GlobalVars);
                }

                // head (create new compRUleBlock for each ele in head)
                if(compRule.head != null)
                {
                    // used to assign unique name to print vars inside rule head (if there are multiple)
                    int printIdx = 0;
                    for(var headEle : compRule.head.elems)
                    {
                        // if it's an assignment
                        if(headEle.a_assignment != null)
                        {
                            AddAssignmentToCompRuleBlock(headEle.a_assignment, curCompRuleBlock, curCompositeRule, localVarPrefix);
                        }
                        // if it's a print statement
                        else if(headEle.a_print != null)
                        {
                            AddPrintToCompRuleBlock(headEle.a_print, curCompRuleBlock, curCompositeRule, localVarPrefix, printIdx);

                            // no longer action-free
                            curCompositeRule.UnsetActionFree();

                            // keep track of number of prints (more specifically their vars)
                            // inside the current rule head
                            printIdx++;
                        }
                        // if it's a composite rule call
                        else if(headEle.a_comp_rule_call != null)
                        {
                            AddCompositeRuleCallToCompRuleBlock(headEle.a_comp_rule_call, curCompRuleBlock, curCompositeRule, localVarPrefix);

                            // no longer action-free
                            curCompositeRule.UnsetActionFree();
                        }
                        // if it's a database action (insert, delete or update)
                        else if(headEle.a_db != null)
                        {
                            AddDatabaseActionToCompRuleBlock(headEle.a_db, curCompRuleBlock, curCompositeRule, localVarPrefix);

                            // no longer action-free
                            curCompositeRule.UnsetActionFree();
                        }
                        // if it's a return statement
                        else if(headEle.a_ret != null)
                        {
                            Assert.Check(
                                    curCompRuleBlock.m_ReturnValue == null,
                                    "comp rule in composite rule " + curCompositeRule.m_Name +
                                            " already has a return value (only one allowed per comp rule head)"
                            );

                            AddReturnToCompRuleBlock(headEle.a_ret, curCompRuleBlock, curCompositeRule, localVarPrefix);

                            hasReturnStat = true;
                        }
                    }
                }

                // if there's a parentBlock
                if(parentCompRuleBlock != null)
                {
                    parentCompRuleBlock.AddCompRuleBlock(curCompRuleBlock);
                    // add global vars changes to parent (set in current block)
                    // add new global vars
                    parentCompRuleBlock.ApplyGlobalVarChanges(curCompRuleBlock.m_GlobalVars);
                }
                // if it's at root level
                else
                {
                    curCompositeRule.AddCompRuleBlock(curCompRuleBlock);
                    // add global vars changes to parent (set in current block)
                    // add new global vars
                    curCompositeRule.ApplyGlobalVarChanges(curCompRuleBlock.m_GlobalVars);
                }

                // keep track of all compRuleTags
                if(curCompRuleBlock.m_Name != null)
                {
                    // duplicate check
                    Assert.Equals(
                            curCompositeRule.m_CompRuleTags.contains(curCompRuleBlock.m_Name),
                            false,
                            "comp rule with tag " + curCompRuleBlock.m_Name +
                                    " already exists in composite rule " + curCompositeRule.m_Name
                    );

                    curCompositeRule.AddCompRuleTag(curCompRuleBlock.m_Name);
                }

                // keep track of all global and local vars in one place
                // easier for later translation
                curCompositeRule.ApplyAllVarChanges(curCompRuleBlock.m_GlobalVars);

                compRuleIdx++;
            }

            // check for duplicates before adding
            Assert.Equals(
                    ContainerExists(curCompositeRule.m_Name),
                    false,
                    "duplicate identifier: there's already a container with the name " + curCompositeRule.m_Name
            );

            // if return type should be derived and there's no return statement in any compRule
            // it's should probably be void
            if(curCompositeRule.m_ReturnType == null && !hasReturnStat)
            {
                curCompositeRule.m_ReturnType = Globals.Translation.Fixed.BT_VOID;
            }

            m_CompositeRuleList.add(curCompositeRule);
        }

        // always reset lexer/parser at the end when used in function
        ResetIFLogParser();
    }

    private void AddCompRuleVarToCompositeRule(IFLogParser.Composite_rule_argContext compRuleVar, IFLog.Components.CompositeRule curCompositeRule)
    {
        var compRuleVarName = compRuleVar.field.getText();
        // can be null
        var compRuleVarType = compRuleVar.type;
        // can be null
        var compRuleVarDefaultValue = compRuleVar.default_value;

        // check if type is valid
        // this will throw an error if it can't find a base type
        if(compRuleVarType != null)
        {
            Assert.Check(
                    BasetypeExists(compRuleVarType.getText()),
                    "type " + compRuleVarType.getText() + " doesn't exist in translation target as a base type"
            );

            // check if type and default value match
            if(compRuleVarDefaultValue != null)
            {
                var compRuleVarDefaultValueBaseType = GetBasetypeForValue(compRuleVarDefaultValue, curCompositeRule.m_Name);

                // check if valid default value for type of compRuleVar
                // only throw a warning since there might be implicit conversions in ifl-target
                if(!compRuleVarType.getText().equals(compRuleVarDefaultValueBaseType))
                {
                    Logger.LogWarning(
                            compRuleVarDefaultValue.getText() + " may not be valid default value for compRuleVar " +
                                    compRuleVarName + " in composite rule " + curCompositeRule.m_Name +
                                    ", expected: " + compRuleVarType.getText() +
                                    ", found (as default value): " + compRuleVarDefaultValueBaseType
                    );
                }
            }
        }

        // check for duplicates
        Assert.Equals(
                curCompositeRule.GetCompRuleVar(compRuleVarName),
                null,
                "compRuleVar " + compRuleVarName + " already exists in composite rule " + curCompositeRule.m_Name
        );

        // finally add the field to the current composite rule
        curCompositeRule.AddCompRuleVar(
                compRuleVarName,
                (compRuleVarType != null) ? compRuleVarType.getText() : null,
                (compRuleVarDefaultValue != null) ? GetValueTranslation(compRuleVarDefaultValue) : null
        );
    }

    private void AddGlobalVarToCompositeRule(IFLogParser.Composite_rule_global_elContext globalVar, IFLog.Components.CompositeRule curCompositeRule)
    {
        var globalVarName = globalVar.name.getText();
        // can be null
        var globalVarType = globalVar.type;

        // check if type is valid
        // this will throw an error if it can't find a base type
        if(globalVarType != null)
        {
            Assert.Check(
                    BasetypeExists(globalVarType.getText()),
                    "type " + globalVarType.getText() +
                            " doesn't exist in translation target as a base type"
            );
        }

        // check for duplicates
        Assert.Equals(
                curCompositeRule.GetGlobalVar(globalVarName),
                null,
                "globalVar " + globalVarName + " already exists in composite rule " + curCompositeRule.m_Name
        );

        // finally add the field to the current composite rule
        curCompositeRule.AddGlobalVar(
                globalVarName,
                (globalVarType != null) ? globalVarType.getText() : null
        );
    }

    private void AddTriggerToCompositeRule(IFLogParser.Composite_rule_eventContext compositeRuleEvent, IFLog.Components.CompositeRule curCompositeRule)
    {
        int triggerIdx = 0;

        // add events (basically creates a trigger for each event)
        for(var curCompositeRuleEvent : compositeRuleEvent.composite_rule_event_el())
        {
            String triggerName = "t" + triggerIdx + "_" + curCompositeRule.m_Name;
            int eventType;

            IFLog.Components.CompositeRule.Trigger.Table curTable = new IFLog.Components.CompositeRule.Trigger.Table(
                    curCompositeRuleEvent.ev_table.getText()
            );

            var curTableRef = GetTable(curTable.m_Name);

            // ensure that table exists
            Assert.Check(
                    GetTable(curTable.m_Name) != null,
                    "event on table " + curTable.m_Name + " which doesn't exist"
            );

            // if update event
            if(curCompositeRuleEvent.type.KW_UPDATE() != null)
            {
                for(var field : curCompositeRuleEvent.fields)
                {
                    // ensure that field exists in table
                    Assert.Check(
                            curTableRef.GetField(field.getText()) != null,
                            "field " + field.getText() + " doesn't exist in table " + curTable.m_Name
                    );

                    curTable.AddField(field.getText());
                }

                eventType = IFLog.Components.CompositeRule.Trigger.TYPE_UPD_EVENT;
            }
            // if insert or delete event
            else
            {
                Assert.Equals(
                        curCompositeRuleEvent.fields,
                        0,
                        "events on fields are only allowed for update events"
                );

                if(curCompositeRuleEvent.type.KW_INSERT() != null)
                {
                    eventType = IFLog.Components.CompositeRule.Trigger.TYPE_INS_EVENT;
                }
                else
                {
                    eventType = IFLog.Components.CompositeRule.Trigger.TYPE_DEL_EVENT;
                }
            }

            curCompositeRule.AddTrigger(
                    triggerName,
                    curTable,
                    compositeRuleEvent.type.KW_BEFORE() != null,
                    eventType
            );

            triggerIdx++;
        }
    }

    private void AddReturnToCompRuleBlock(
            IFLogParser.Stat_comp_rule_returnContext returnCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {

        String returnType = curCompositeRule.m_ReturnType;
        String elementValue;
        String elementType;
        // if it's a var
        if(returnCtx.ele.var != null)
        {
            var knownVar = GetKnownVar(null, returnCtx.ele.var.getText(), curCompRuleBlock, curCompositeRule, localVarPrefix, true);

            Assert.Check(
                    knownVar != null,
                    "usage of unknown/unset var in return of composite rule " + curCompositeRule.m_Name
            );

            elementType = knownVar.m_Type;
            elementValue = knownVar.m_Name;
        }
        // if it's a value
        else
        {
            elementType = GetBasetypeForValue(returnCtx.ele.value, curCompositeRule.m_Name);
            elementValue = GetValueTranslation(returnCtx.ele.value);
        }

        if(returnType != null)
        {
            // don't allow return for returnType void
            Assert.Check(
                    !returnType.equals(Globals.Translation.Fixed.BT_VOID),
                    "return in composite rules with return type void not allowed in  composite rule "  +
                            curCompositeRule.m_Name
            );
        }

        // if return type should be derived, set it to the first non-void type
        if(returnType == null && !elementType.equals(Globals.Translation.Fixed.BT_VOID))
        {
            curCompositeRule.m_ReturnType = elementType;
        }
        // if element type is void (in other words null)
        else if(elementType.equals(Globals.Translation.Fixed.BT_VOID))
        {
            // do nothing
        }
        // if return type is trigger
        else if(returnType.equals(Globals.Translation.Fixed.BT_TRIGGER))
        {
            // if it's old
            if(returnCtx.ele.value.getText().equals(Globals.Translation.Fixed.CONST_OLD))
            {
                Assert.Check(
                        curCompositeRule.OldAllowed(),
                        "usage of " + Globals.Translation.Fixed.CONST_OLD +
                                " not allowed inside composite rule " + curCompositeRule.m_Name
                );
            }
            // if it's new
            else
            {
                Assert.Check(
                        curCompositeRule.NewAllowed(),
                        "usage of " + Globals.Translation.Fixed.CONST_NEW +
                                " not allowed inside composite rule " + curCompositeRule.m_Name
                );
            }
        }
        // if return type is a date
        else if(returnType.equals(Globals.Translation.Fixed.BT_DATE))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_DATE),
                    "type mismatch in return in composite rule " + curCompositeRule.m_Name +
                            ", expected: " + returnType + ", found: " + elementType
            );
        }
        // if return type is a string (assume implicit conversion)
        else if(returnType.equals(Globals.Translation.Fixed.BT_STRING)
                || returnType.equals(Globals.Translation.Fixed.BT_CHAR))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_STRING)
                            || elementType.equals(Globals.Translation.Fixed.BT_CHAR),
                    "type mismatch in return in composite rule " + curCompositeRule.m_Name +
                            ", expected: " + returnType + ", found: " + elementType
            );

            if(!returnType.equals(elementType))
            {
                Logger.LogWarning(
                        "implicit conversion in return in composite rule " + curCompositeRule.m_Name +
                        " between " + returnType + " and " + elementType
                );
            }
        }
        // if return type is a bool
        else if(returnType.equals(Globals.Translation.Fixed.BT_BOOL))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_BOOL),
                    "type mismatch in return in composite rule " + curCompositeRule.m_Name +
                            ", expected: " + returnType + ", found: " + elementType
            );
        }
        // if return type is a number (assume implicit conversion)
        else if(returnType.equals(Globals.Translation.Fixed.BT_INT)
                || returnType.equals(Globals.Translation.Fixed.BT_FLOAT))
        {
            Assert.Check(
                    elementType.equals(Globals.Translation.Fixed.BT_INT)
                            || elementType.equals(Globals.Translation.Fixed.BT_FLOAT),
                    "type mismatch in return in composite rule " + curCompositeRule.m_Name +
                            ", expected: " + returnType + ", found: " + elementType
            );

            if(!returnType.equals(elementType))
            {
                Logger.LogWarning(
                        "implicit conversion in return in composite rule " + curCompositeRule.m_Name +
                                " between " + returnType + " and " + elementType
                );
            }
        }

        curCompRuleBlock.AddReturn(elementValue);
    }

    private void AddDatabaseActionToCompRuleBlock(
            IFLogParser.Stat_comp_rule_db_actionContext databaseActionCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        // if database action is an insert
        if(databaseActionCtx.KW_INSERT() != null)
        {
            var tableName = databaseActionCtx.ele.name.getText();

            var curSelect = ParseDatabaseActionBody(
                    tableName, databaseActionCtx.ele.table_data_record(), curCompRuleBlock,
                    curCompositeRule, localVarPrefix, true, true
            );

            IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Insert curInsert =
                    new IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Insert(tableName);

            for(var field : curSelect.m_TablesViews.get(0).m_Fields)
            {
                String value = (field.m_Var != null) ? field.m_Var : field.m_Value;
                curInsert.AddFieldValuePair(field.m_Name, value);
            }

            curCompRuleBlock.AddDatabaseAction(curInsert);
        }
        // if database action is an update
        else if(databaseActionCtx.KW_UPDATE() != null)
        {
            String tableName;
            IFLog.Components.CompositeRule.CompRuleBlock.Select selectValues;
            IFLog.Components.CompositeRule.CompRuleBlock.Select selectCond;

            // handle different syntax
            if(databaseActionCtx.a_table != null)
            {
                tableName = databaseActionCtx.a_table.getText();

                selectValues = ParseDatabaseActionBody(
                        tableName, databaseActionCtx.a_values, curCompRuleBlock,
                        curCompositeRule, localVarPrefix, true, false
                );

                selectCond = ParseDatabaseActionBody(
                        tableName, databaseActionCtx.a_cond, curCompRuleBlock,
                        curCompositeRule, localVarPrefix, true, false
                );
            }
            else
            {
                tableName = databaseActionCtx.values.name.getText();
                var tableNameCond = databaseActionCtx.cond.name.getText();

                Assert.Equals(
                        tableName,
                        tableNameCond,
                        "value and condition table for update action have to be the same in composite rule " +
                                curCompositeRule.m_Name
                );

                selectValues = ParseDatabaseActionBody(
                        tableName, databaseActionCtx.values.table_data_record(), curCompRuleBlock,
                        curCompositeRule, localVarPrefix, true, false
                );

                selectCond = ParseDatabaseActionBody(
                        tableName, databaseActionCtx.cond.table_data_record(), curCompRuleBlock,
                        curCompositeRule, localVarPrefix, true, false
                );
            }

            IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Update curUpdate =
                    new IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Update(
                            tableName, selectCond.m_Constraint
                    );

            for(var field : selectValues.m_TablesViews.get(0).m_Fields)
            {
                String value = (field.m_Var != null) ? field.m_Var : field.m_Value;
                curUpdate.AddFieldValuePair(field.m_Name, value);
            }

            curCompRuleBlock.AddDatabaseAction(curUpdate);
        }
        // if database action is a delete
        else
        {
            // it's a delete for the whole table
            if(databaseActionCtx.a_table != null)
            {
                var tableName = databaseActionCtx.a_table.getText();

                Assert.Equals(
                        GetTable(tableName) != null,
                        true,
                        "no table with the name " + tableName
                );

                curCompRuleBlock.AddDatabaseAction(
                        new IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Delete(tableName, null)
                );
            }
            // it's on a specific set of rows
            else
            {
                var tableName = databaseActionCtx.ele.name.getText();

                var curSelect = ParseDatabaseActionBody(
                        tableName, databaseActionCtx.ele.table_data_record(), curCompRuleBlock,
                        curCompositeRule, localVarPrefix, true, false
                );

                curCompRuleBlock.AddDatabaseAction(
                        new IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Delete(tableName, curSelect.m_Constraint)
                );
            }
        }
    }

    private IFLog.Components.CompositeRule.CompRuleBlock.Select ParseDatabaseActionBody(
            String tableName, IFLogParser.Table_data_recordContext tableRecord, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement, boolean isInsert)
    {
        IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect = new IFLog.Components.CompositeRule.CompRuleBlock.Select();

        // check if container exists
        // no reason to check for duplicates
        Assert.Equals(
                GetTable(tableName) != null,
                true,
                "no table with the name " + tableName
        );

        IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView = new IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView(
                tableName,
                "t",
                false,
                IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_NORMAL
        );

        AddTableViewBodyToSelect(
                tableRecord, curSelect, curCompRuleBlock,
                curCompositeRule, curTableView, localVarPrefix, isHeadElement, isInsert
        );

        curSelect.AddTableView(curTableView);

        // there are no unset vars allowed
        Assert.Equals(
                curSelect.m_Vars.size(),
                0,
                "no unset vars in select as part of if condition allowed in composite rule " + curCompositeRule.m_Name
        );

        // add additional constraints based on tables and common vars
        // as well as for every fixed value that gets passed
        AddAdditionalConstraintsToSelect(curSelect, curCompositeRule, true);

        // replace all flagged vars, e.g.: {x} in constraint,
        // conditional values and vars + undo flags in rest
        ReplaceVarsInSelect(curSelect, curCompRuleBlock, curCompositeRule);

        return curSelect;
    }

    private void AddCompositeRuleCallToCompRuleBlock(
            IFLogParser.Stat_comp_rule_callContext compRuleCallCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        String compRuleCall = ParseCompositeRuleCall(
                compRuleCallCtx.name.getText(), compRuleCallCtx, curCompRuleBlock, curCompositeRule, localVarPrefix
        );

        // finally add it to the compRuleBlock
        curCompRuleBlock.AddCompRuleCall(compRuleCall);
    }

    private void AddPrintToCompRuleBlock(
            IFLogParser.Stat_comp_rule_printContext printStatCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, int printIdx)
    {
        // parse print value as an arithmetic statement
        // and create a new local var
        var ar = ParsePrintStatement(printStatCtx, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, localVarPrefix, printIdx, true);

        // replace all flagged vars, e.g.: {x} in constraint,
        // conditional values and vars + undo flags in rest
        ar.m_Value = ParseStringAndReplaceVars(
                null, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, ar.m_Value
        );
        // create an assignment between newly created var and print value
        curCompRuleBlock.AddAssignment(ar.m_Name, ar.m_Type, ar.m_Value);

        // set new var it (since it got assigned the print value)
        curCompRuleBlock.SetGlobalVar(ar.m_Name);

        // get translation for print
        String printStat = null;
        // if statement is a simple notice/info
        if(printStatCtx.KW_PRINT() != null)
        {
            printStat = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(
                            Globals.Translation.Fixed.COMPOSITE_RULE_PRINT
                    )
            );
        }
        // if statement is an error/exception
        else if(printStatCtx.KW_PRINTERR() != null)
        {
            printStat = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(
                            Globals.Translation.Fixed.COMPOSITE_RULE_PRINTERR
                    )
            );
        }
        // if statement is a warning
        else if(printStatCtx.KW_PRINTWARN() != null)
        {
            printStat = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(
                            Globals.Translation.Fixed.COMPOSITE_RULE_PRINTWARN
                    )
            );
        }

        // access newly created var (since it's used in print statement)
        curCompRuleBlock.AccessGlobalVar(ar.m_Name);

        printStat = printStat.replace(Globals.Translation.CompositeRule.V_VAR, ar.m_Name);

        // finally add it to the compRuleBlock
        curCompRuleBlock.AddPrintStat(printStat);
    }

    private void AddAssignmentToCompRuleBlock(
            IFLogParser.Stat_assignmentContext assignmentStat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        var ar = ParseAssignmentStatement(assignmentStat, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, localVarPrefix, true);

        // replace all flagged vars, e.g.: {x} in constraint,
        // conditional values and vars + undo flags in rest
        String value = ParseStringAndReplaceVars(
                null, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, ar.m_Value
        );

        curCompRuleBlock.AddAssignment(ar.m_Name, ar.m_Type, value);
    }

    private CompRuleTagResult ParseCompRuleTags(IFLogParser.Stat_comp_rule_tagsContext compRuleTags, IFLog.Components.CompositeRule curCompositeRule)
    {
        CompRuleTagResult ret = null;

        if(compRuleTags.stat_comp_rule_tags() != null)
        {
            ret = ParseCompRuleTags(compRuleTags.stat_comp_rule_tags(), curCompositeRule);
        }
        else
        {
            // build map for easier processing
            Map<String, Boolean> tagMap = new HashMap<>();
            boolean negatedFound = false;
            for(var tag : compRuleTags.tags)
            {
                // check that there's at most one negated tag
                if(tag.negated != null)
                {
                    Assert.Check(
                            !negatedFound,
                            "only one negated tag allowed but more found in composite rule " + curCompositeRule.m_Name
                    );

                    negatedFound = true;
                }

                // check that tag even exists as a rule
                Assert.Equals(
                        curCompositeRule.m_CompRuleTags.contains(tag.name.getText()),
                        true,
                        "couldn't process tag " + tag.name.getText() +
                                " in composite rule " + curCompositeRule.m_Name +
                                " check that all tags are properly defined as rules"
                );

                // add to map
                boolean found = tagMap.putIfAbsent(tag.name.getText(), tag.negated != null) != null;

                // check if tag is already part of map
                Assert.Check(
                        !found,
                        "duplicate tag " + tag.name +  " found in rule in composite rule " + curCompositeRule.m_Name
                );
            }

            ret = ParseCompRuleBlocks(curCompositeRule.m_CompRuleBlocks, curCompositeRule, tagMap);
        }

        return ret;
    }

    private CompRuleTagResult ParseCompRuleBlocks(
            List<IFLog.Components.CompositeRule.CompRuleBlock> compRuleBlocks, IFLog.Components.CompositeRule curCompositeRule,
            Map<String, Boolean> tagMap)
    {
        CompRuleTagResult ret = null;

        // check if there are rules left that we can check
        Assert.Unequals(
                compRuleBlocks.size(),
                0,
                "can't process remaining tags in composite rule " + curCompositeRule.m_Name +
                        " (no rules left to process)"
        );

        for(var compRuleBlock : compRuleBlocks)
        {
            Boolean isNegated = tagMap.remove(compRuleBlock.m_Name);

            // if it's an unknown tag
            if(isNegated == null) continue;

            if(!isNegated)
            {
                // has to be an if or foreach or for
                Assert.Check(
                        (compRuleBlock.m_IfCondition != null) ||
                                (compRuleBlock.m_ForEachLoop != null) ||
                                (compRuleBlock.m_ForLoop != null),
                        "positive tags are required to be an if-condition, foreach-loop or for-loop"
                );

                if(tagMap.size() > 0)
                {
                    ret = ParseCompRuleBlocks(compRuleBlock.m_CompRuleBlocks, curCompositeRule, tagMap);

                    // cases like: r0, !r1
                    if(ret.m_ParentCompRuleBlock == null)
                    {
                        ret.m_ParentCompRuleBlock = compRuleBlock;
                    }
                }
                else
                {
                    // cases like: r0 or r0, r1
                    ret = new CompRuleTagResult(compRuleBlock, null);
                }
            }
            else
            {
                // only if for alternative tags
                Assert.Check(
                        (compRuleBlock.m_IfCondition != null),
                        "negated tags are required to be an if-condition"
                );

                // check if there are tags left to process
                Assert.Equals(
                        tagMap.size(),
                        0,
                        "can't process remaining tags in composite rule " + curCompositeRule.m_Name +
                                " (no rules left to process)"
                );

                // cases like: !r0
                ret = new CompRuleTagResult(null, compRuleBlock.m_Name);
            }
        }

        return ret;
    }

    private void AddIfCondToCompRuleBlock(
            IFLogParser.Stat_comp_rule_ifContext ifCondCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        String ifCond = ParseIfCond(ifCondCtx, curCompRuleBlock, curCompositeRule, localVarPrefix, false);


        // replace all flagged vars, e.g.: {x} in constraint,
        // conditional values and vars + undo flags in rest
        ifCond = ParseStringAndReplaceVars(
                null, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, ifCond
        );

        curCompRuleBlock.AddIfCondition(ifCond);
    }

    private String ParseIfCond(
            IFLogParser.Stat_comp_rule_ifContext ifCondCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement)
    {
        StringBuilder bodyAsString= new StringBuilder();
        String compositeRuleName = curCompositeRule.m_Name;

        var negated = ifCondCtx.negated;

        var lRound = ifCondCtx.SYM_LROUND();
        var rRound = ifCondCtx.SYM_RROUND();

        // add not operator
        if(negated != null)
        {
            // get translation for negation
            bodyAsString.append(
                    m_TranslationMap.get(
                            Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_NEGATION)
                    )
            );
        }

        // add left bracket
        if(lRound != null)
        {
            bodyAsString.append(lRound.getText());
        }

        // if the body is another body element in itself
        var bodyEles = ifCondCtx.stat_comp_rule_if();
        if(bodyEles.size() > 0)
        {
            // parse any additional body element recursively
            // and add their result to the body string
            bodyAsString.append(ParseIfCond(bodyEles.get(0), curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement));

            // if there's a logical operator parse the second element as well
            // and add its result to the body string
            // also add the operator
            var logicalOp = ifCondCtx.any_logical_op();
            if(logicalOp != null)
            {
                // get translation for logical operator
                bodyAsString.append(" ").append(GetLogicalOperatorTranslation(logicalOp)).append(" ");
                bodyAsString.append(ParseIfCond(bodyEles.get(1), curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement));
            }
        }
        // if the body (or current element) is a comparison statement
        else if(ifCondCtx.stat_comp_rule_comparison() != null)
        {
            var comparisonOp = ifCondCtx.stat_comp_rule_comparison().any_comparison_op();

            // if it's a comparison between two elements
            // parse lhs and rhs of it and add their results
            // together with the logical operator to the body string
            var comparisonStats = ifCondCtx.stat_comp_rule_comparison().stat_comp_rule_comparison_el() ;
            if(comparisonStats.size() > 0)
            {
                String lhs = null;
                String rhs = null;

                // if lhs is an arithmetic statement
                if(comparisonStats.get(0).stat_arithmetic() != null)
                {
                    lhs = ParseArithmeticStatement(
                            comparisonStats.get(0).stat_arithmetic(), null, curCompRuleBlock,
                            curCompositeRule, compositeRuleName, localVarPrefix, false, isHeadElement).m_Value;
                }
                // if lhs is a composite rule get
                else
                {
                    var compositeRuleGet = comparisonStats.get(0).stat_comp_rule_get();

                    lhs = ParseCompositeRuleGet(
                            compositeRuleGet.name.getText(), compositeRuleGet, curCompRuleBlock,
                            curCompositeRule, localVarPrefix, true
                    );
                }

                // if rhs is an arithmetic statement
                if(comparisonStats.get(1).stat_arithmetic() != null)
                {
                    rhs = ParseArithmeticStatement(
                            comparisonStats.get(1).stat_arithmetic(), null, curCompRuleBlock,
                            curCompositeRule, compositeRuleName, localVarPrefix, false, isHeadElement).m_Value;
                }
                // if rhs is a composite rule get
                else
                {
                    var compositeRuleGet = comparisonStats.get(1).stat_comp_rule_get();

                    rhs = ParseCompositeRuleGet(
                            compositeRuleGet.name.getText(), compositeRuleGet, curCompRuleBlock,
                            curCompositeRule, localVarPrefix, true
                    );
                }

                // check that the types are comparable
                ValidateTypesInComparisonStatement(lhs, rhs, compositeRuleName);

                String[] lhsList = lhs.split(Globals.Translation.Fixed.SYM_FLAG);
                String[] rhsList = rhs.split(Globals.Translation.Fixed.SYM_FLAG);

                // get translation for comparison operator
                // pass types to check if one of them is null (void)
                String comparisonOpStr = GetComparisonOperatorTranslation(
                        comparisonOp,
                        lhsList[1].equals(Globals.Translation.Fixed.BT_VOID)
                                || rhsList[1].equals(Globals.Translation.Fixed.BT_VOID)
                );

                // remove types
                // if lhs is null swap them
                if(lhsList[1].equals(Globals.Translation.Fixed.BT_VOID))
                {
                    rhs = lhsList[0];
                    lhs = rhsList[0];
                }
                else
                {
                    lhs = lhsList[0];
                    rhs = rhsList[0];
                }

                // remove placeholders for SYM_FLAG
                lhs = lhs.replaceAll(
                        Globals.Translation.Fixed.SYM_FLAG_STR,
                        Globals.Translation.Fixed.SYM_FLAG
                );
                rhs = rhs.replaceAll(
                        Globals.Translation.Fixed.SYM_FLAG_STR,
                        Globals.Translation.Fixed.SYM_FLAG
                );

                bodyAsString.append(lhs);
                bodyAsString.append(" ").append(comparisonOpStr).append(" ");
                bodyAsString.append(rhs);

            }
            // if it is an in_statement consisting of both a field
            // and a list of values
            // add them together with the in_operator to the body string
            else
            {
                var inStat = ifCondCtx.stat_comp_rule_comparison().stat_in();
                // technically this is the var name and not the field
                String fieldName = inStat.field.getText();

                // check if type of lhs and type of every ele on rhs are valid for operation
                var selectVar = GetKnownVar(null, fieldName, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement);

                // make sure the variable is set
                Assert.Unequals(selectVar, null, "var on lhs of in-statement not set in " + compositeRuleName);

                for(var value : inStat.value_list.values)
                {
                    ValidateTypeInList(
                            selectVar.m_Type,
                            GetBasetypeForValue(value, compositeRuleName),
                            value.getText(),
                            compositeRuleName
                    );
                }

                // get translation for in comparison operator
                String inOp = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_IN)
                );

                // flag it for later
                bodyAsString.append(Globals.Translation.Fixed.VarFlag(fieldName));
                bodyAsString.append(" ").append(inOp).append(" ");
                // get translation for list
                bodyAsString.append(GetListTranslation(inStat.value_list));
            }
        }
        // if the body (or current element) is a select
        // double check here: could also be a comp rule call
        else if(ifCondCtx.stat_select() != null)
        {
            String selectStr = ParseIfConditionSelect(
                    ifCondCtx.stat_select(), curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
            );

            bodyAsString.append(selectStr);
        }
        // if the body (or current element) is a composite rule call
        // (basically covers comp rule call with no args)
        else
        {
            String compositeRuleGetStr = ParseCompositeRuleGet(
                    ifCondCtx.stat_comp_rule_get().name.getText(),
                    ifCondCtx.stat_comp_rule_get(),
                    curCompRuleBlock, curCompositeRule, localVarPrefix, false
            );

            bodyAsString.append(compositeRuleGetStr);
        }

        // add right bracket
        if(rRound != null)
        {
            bodyAsString.append(rRound.getText());
        }

        return bodyAsString.toString();
    }

    private String ParseIfConditionSelect(
            IFLogParser.Stat_selectContext selectCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement)
    {
        StringBuilder bodyAsString = new StringBuilder();

        // if it's another stat select
        if(selectCtx.stat_select() != null)
        {
            var lRound = selectCtx.SYM_LROUND();
            var rRound = selectCtx.SYM_RROUND();

            // append left bracket
            if(lRound != null)
            {
                bodyAsString.append(lRound.getText());
            }

            bodyAsString.append(
                    ParseIfConditionSelect(selectCtx.stat_select(), curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement)
            );

            // append right bracket
            if(rRound != null)
            {
                bodyAsString.append(rRound.getText());
            }
        }
        else
        {
            IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect = new IFLog.Components.CompositeRule.CompRuleBlock.Select();

            int tableIdx = 0;
            int viewIdx = 0;
            for(var tableView : selectCtx.tables)
            {
                String containerName = tableView.name.getText();

                // if comp rule call got wrongly flagged as part of select
                if(GetCompositeRule(containerName) != null)
                {
                    // if there's already something in the string add an AND
                    if(!bodyAsString.isEmpty())
                    {
                        bodyAsString.append(
                                m_TranslationMap.get(
                                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_AND)
                                )
                        );
                        bodyAsString.append(" ");
                    }

                    bodyAsString.append(
                            ParseCompositeRuleGet(
                                    containerName, tableView, curCompRuleBlock,
                                    curCompositeRule, localVarPrefix)
                    );
                }
                else
                {
                    boolean isTable = AddTableViewToSelect(
                            tableView, curSelect, curCompRuleBlock,
                            curCompositeRule, tableIdx, viewIdx, localVarPrefix, isHeadElement
                    );

                    // if tableView is a table
                    if(isTable)
                    {
                        tableIdx++;
                    }
                    // if tableView is a view
                    else
                    {
                        viewIdx++;
                    }
                }
            }

            if (selectCtx.constraint != null)
            {
                AddConstraintToSelect(
                        selectCtx.constraint, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                );
            }

            // there are no unset vars allowed
            Assert.Equals(
                    curSelect.m_Vars.size(),
                    0,
                    "no unset vars in select as part of if condition allowed in composite rule " + curCompositeRule.m_Name
            );

            // add additional constraints based on tables and common vars
            // as well as for every fixed value that gets passed
            AddAdditionalConstraintsToSelect(curSelect, curCompositeRule, false);

            // replace all flagged vars, e.g.: {x} in constraint,
            // conditional values and vars + undo flags in rest
            ReplaceVarsInSelect(curSelect, curCompRuleBlock, curCompositeRule);

            // translate select and add it to bodystring
            StringBuilder tableViewBlock = new StringBuilder();
            for(var tableView : curSelect.m_TablesViews)
            {
                // skip all tables that are flagged as old/new
                if(tableView.m_Type != IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_NORMAL) continue;

                String selectTableView = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_TABLEVIEW)
                );

                // set name
                selectTableView = selectTableView.replace(Globals.Translation.CompositeRule.V_NAME, tableView.m_Name);

                // set alias
                selectTableView = selectTableView.replace(Globals.Translation.CompositeRule.V_ALIAS, tableView.m_Alias);

                if(!tableViewBlock.isEmpty())
                {
                    tableViewBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                }

                tableViewBlock.append(selectTableView);
            }

            // there has to be at least one table/view that is not OLD/NEW in select
            String ifCondSelect = null;
            if(!tableViewBlock.isEmpty())
            {
                StringBuilder selectBody = new StringBuilder();

                // get select all translation template
                String selectAll = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_ALL)
                );

                // add to select body
                selectBody.append(selectAll);

                // get select from translation template
                String selectFrom = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_FROM)
                );

                // set table views in from clause
                selectFrom = selectFrom.replace(Globals.Translation.CompositeRule.V_TABLEVIEWS, tableViewBlock);

                // add to select body
                selectBody.append(" ").append(selectFrom);

                if(curSelect.m_Constraint != null)
                {
                    // get select where translation template
                    String selectWhere = m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_WHERE)
                    );

                    // set condition in where  clause
                    selectWhere = selectWhere.replace(Globals.Translation.CompositeRule.V_CONDITION, curSelect.m_Constraint);

                    // add to select body
                    selectBody.append(" ").append(selectWhere);
                }

                // get if condition translation template
                ifCondSelect = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_IF_COND_SELECT)
                );

                // set select body
                ifCondSelect = ifCondSelect.replace(Globals.Translation.CompositeRule.V_SELECT_ALL, selectBody);
            }
            // if there's only a constraint
            // can occur if a composite rule call gets wrongly flagged
            else if(curSelect.m_Constraint != null)
            {
                ifCondSelect = curSelect.m_Constraint;
            }

            // possible that ifCondSelect is null if the whole statement was wrongly flagged
            if(ifCondSelect != null)
            {
                // if there's already something in the string add an AND
                if(!bodyAsString.isEmpty())
                {
                    bodyAsString.append(
                            m_TranslationMap.get(
                                    Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_AND)
                            )
                    );
                    bodyAsString.append(" ");
                }

                bodyAsString.append(ifCondSelect);
            }
        }

        return bodyAsString.toString();
    }

    private String ParseCompositeRuleCall(
            String compositeRuleCallName, IFLogParser.Stat_comp_rule_callContext compRuleCallCtx,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix)
    {
        return ParseCompositeRuleCallBody(
                compositeRuleCallName, compRuleCallCtx.table_data_record(), curCompRuleBlock, curCompositeRule,
                localVarPrefix, false, true, false
        );
    }

    private String ParseCompositeRuleGet(
            String compositeRuleGetName, IFLogParser.Stat_comp_rule_getContext compRuleGetCtx,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean is_comparison)
    {
        return ParseCompositeRuleCallBody(
                compositeRuleGetName, compRuleGetCtx.table_data_record(), curCompRuleBlock, curCompositeRule,
                localVarPrefix, is_comparison, false, compRuleGetCtx.negated != null
        );
    }

    private String ParseCompositeRuleGet(
            String compositeRuleGetName, IFLogParser.Stat_tableContext compRuleGetCtx,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix)
    {
        return ParseCompositeRuleCallBody(
                compositeRuleGetName, compRuleGetCtx.table_data_record(), curCompRuleBlock, curCompositeRule,
                localVarPrefix, false, false, compRuleGetCtx.negated != null
        );
    }

    private String ParseCompositeRuleCallBody(
            String compositeRuleGetName, IFLogParser.Table_data_recordContext compositeRuleCallBody,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean is_comparison, boolean is_head_element, boolean is_negated)
    {
        var compositeRule = GetCompositeRule(compositeRuleGetName);

        // check if rule exists
        Assert.Check(
                compositeRule != null,
                "couldn't find composite rule " + compositeRuleGetName +
                        " while parsing composite rule " + curCompositeRule.m_Name
        );

        String statType = null;
        if(!is_head_element)
        {
            // check if composite rule call is action free
            Assert.Check(
                    compositeRule.m_IsActionFree,
                    "only action-free composite rule calls in rule body allowed in " + curCompositeRule.m_Name
            );

            // if composite rule is part of a comparison statement
            if(is_comparison)
            {
                // only allow non-bool/-void type for composite rules which are part of a comparison statement
                Assert.Check(
                        !compositeRule.m_ReturnType.equals(Globals.Translation.Fixed.BT_VOID) &&
                                !compositeRule.m_ReturnType.equals(Globals.Translation.Fixed.BT_BOOL),
                        "bool and void not allowed for return type of composite rules which are part of a comparison statement" +
                                " in composite rule " + curCompositeRule.m_Name
                );

                statType = compositeRule.m_ReturnType;
            }
            // if composite rule is not part of a comparison statement (standalone)
            else
            {
                Assert.Check(
                                compositeRule.m_ReturnType.equals(Globals.Translation.Fixed.BT_BOOL),
                        "only bool for return type of composite rules allowed which are not part of a comparison statement" +
                                " in composite rule " + curCompositeRule.m_Name
                );
            }
        }

        // skip vars if it's a composite rule without vars
        String compositeRuleCallBodyStr = "";
        if(compositeRuleCallBody != null)
        {
            // parse body into string
            compositeRuleCallBodyStr = ParseCompositeRuleCallVars(
                    compositeRuleCallBody, compositeRule, curCompRuleBlock, curCompositeRule, localVarPrefix, is_head_element
            );
        }

        // get if condition translation template
        String ifCondGetTemplate = null;
        if(is_head_element)
        {
            ifCondGetTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_CALL)
            );
        }
        else
        {
            if(is_negated)
            {
                ifCondGetTemplate = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_GET_NEGATED)
                );
            }
            else
            {
                ifCondGetTemplate = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_GET)
                );
            }
        }

        // set the name
        ifCondGetTemplate = ifCondGetTemplate.replace(Globals.Translation.CompositeRule.V_NAME, compositeRule.m_Name);

        // set vars
        ifCondGetTemplate = ifCondGetTemplate.replace(Globals.Translation.CompositeRule.V_VARS, compositeRuleCallBodyStr);

        if(!is_head_element && is_comparison)
        {
            // add flag for statement type
            ifCondGetTemplate += Globals.Translation.Fixed.Flag(statType);
        }

        return ifCondGetTemplate;
    }

    private String ParseCompositeRuleCallVars(
            IFLogParser.Table_data_recordContext compositeRuleCallBody, IFLog.Components.CompositeRule compositeRule,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        StringBuilder bodyAsString = new StringBuilder();

        // simple helper to keep track of which comp rule vars
        // have default values
        // in other words the ones that are allowed to have wildcards as a value
        Map<String, Boolean> compRuleVarMap = new HashMap<>();
        for(var compRuleVar : compositeRule.m_CompRuleVars)
        {
            compRuleVarMap.put(
                    compRuleVar.m_Name,
                    compRuleVar.m_DefaultValue != null
            );
        }

        // if record only contains values (identification by position)
        if(compositeRuleCallBody.table_record() != null)
        {
            // check that number of values matches number of comp rule vars
            Assert.Check(
                    compositeRuleCallBody.table_record().values.size() <= compositeRule.m_CompRuleVars.size(),
                    "too many values for composite rule call " + compositeRule.m_Name + " in composite rule " + curCompositeRule.m_Name
            );
            // or contains a multi-wildcard
            if(compositeRuleCallBody.table_record().values.size() < compositeRule.m_CompRuleVars.size())
            {
                // multi-wildcard needs to be defined at the end
                var multiWildcard = compositeRuleCallBody.table_record().SYM_WILDCARD_MULTI();
                Assert.Unequals(
                        multiWildcard, null,
                        "not enough values for composite rule get " + compositeRule.m_Name + " in composite rule "
                                + curCompositeRule.m_Name + " (you might add a multi-wildcard at the end)"
                );
            }

            int curCompRuleVarIdx = 0;
            for(var curDataInsertCtx : compositeRuleCallBody.table_record().values)
            {
                // if it's not a wildcard
                if(curDataInsertCtx.SYM_WILDCARD_SINGLE() == null)
                {
                    // retrieve the comp rule var from the referenced composite rule
                    IFLog.Components.CompositeRule.CompRuleVar compRuleVar = compositeRule.m_CompRuleVars.get(curCompRuleVarIdx);
                    String compRuleVarAsStr = null;

                    // if it's an arithmetic statement
                    if(curDataInsertCtx.stat_arithmetic() != null)
                    {
                        // parse var or value
                        compRuleVarAsStr = ParseVarOrValue(
                                compositeRule, compRuleVar, curDataInsertCtx.stat_arithmetic(),
                                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                        );
                    }
                    // if it's a coalesce statement
                    else if(curDataInsertCtx.stat_coalesce() != null)
                    {
                        Assert.Check(
                                isHeadElement,
                                "coalesce statements are only allowed in rule heads"
                        );

                        compRuleVarAsStr = ParseAndTranslateCoalesceStatement(
                                compositeRule, compRuleVar, curDataInsertCtx.stat_coalesce(),
                                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                        );
                    }

                    // append it to the body
                    if(!bodyAsString.isEmpty())
                    {
                        bodyAsString.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                    }
                    bodyAsString.append(compRuleVarAsStr);

                    // keep track of all comp rule vars
                    compRuleVarMap.remove(compRuleVar.m_Name);
                }

                // pointer to current field
                curCompRuleVarIdx++;
            }

            // there should only be comp rule vars left that contain a default value
            Assert.Check(
                    !compRuleVarMap.containsValue(false),
                    "comp rule var for composite rule call is missing a value in composite rule " + curCompositeRule.m_Name
                            + "\n" + compRuleVarMap
            );
        }
        // if record contains fields and associated values (identification by name)
        else
        {
            Assert.Check(
                    compositeRuleCallBody.table_record_extended().fields.size() <= compositeRule.m_CompRuleVars.size(),
                    "too many values for composite rule call in composite rule " + compositeRule.m_Name
            );

            for(int i = 0; i < compositeRuleCallBody.table_record_extended().fields.size(); ++i)
            {
                var curElement = compositeRuleCallBody.table_record_extended().values.get(i);

                // retrieve the field name and make sure it matches
                // with a field in the referenced composite rule
                String fieldName = compositeRuleCallBody.table_record_extended().fields.get(i).getText();
                IFLog.Components.CompositeRule.CompRuleVar compRuleVar = compositeRule.GetCompRuleVar(fieldName);

                // check if field is part of table
                Assert.Unequals(
                        compRuleVar,
                        null,
                        "compRuleVar " + fieldName + " doesn't exist in composite rule " + compositeRule.m_Name
                );

                String compRuleVarAsStr = null;

                // if it's an arithmetic statement
                if(curElement.stat_arithmetic() != null)
                {
                    // parse var or value
                    compRuleVarAsStr = ParseVarOrValue(
                            compositeRule, compRuleVar, curElement.stat_arithmetic(),
                            curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                    );
                }
                else if(curElement.stat_coalesce() != null)
                {
                    Assert.Check(
                            isHeadElement,
                            "coalesce statements are only allowed in rule heads"
                    );

                    compRuleVarAsStr = ParseAndTranslateCoalesceStatement(
                            compositeRule, compRuleVar, curElement.stat_coalesce(),
                            curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                    );
                }

                // append it to the body
                if(!bodyAsString.isEmpty())
                {
                    bodyAsString.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                }
                bodyAsString.append(compRuleVarAsStr);

                // keep track of all comp rule vars
                compRuleVarMap.remove(compRuleVar.m_Name);
            }

            // there should only be comp rule vars left that contain a default value
            Assert.Check(
                    !compRuleVarMap.containsValue(false),
                    "comp rule var for composite rule call is missing a value in composite rule " + curCompositeRule.m_Name
                            + "\n" + compRuleVarMap
            );
        }

        return bodyAsString.toString();
    }

    private String ParseVarOrValue(
            IFLog.Components.CompositeRule compositeRule, IFLog.Components.CompositeRule.CompRuleVar compRuleVar,
            IFLogParser.Stat_arithmeticContext arithmeticStat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement)
    {
        // parse the statement for the value
        ArithmeticResult ar = ParseArithmeticStatement(
                arithmeticStat, null, curCompRuleBlock,
                curCompositeRule, curCompositeRule.m_Name, localVarPrefix, true, isHeadElement
        );
        String value = ar.m_Value;

        // check that the value is valid for field type
        ValidateTypeInDataInsert(compRuleVar, value, compositeRule.m_Name);

        // remove type
        String[] valueList = value.split(Globals.Translation.Fixed.SYM_FLAG);
        value = valueList[0];
        // remove placeholders for SYM_FLAG
        value = value.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG_STR,
                Globals.Translation.Fixed.SYM_FLAG
        );

        // if var is of type unknown
        Assert.Unequals(
                valueList[1],
                Globals.Translation.Fixed.BT_UNKNOWN,
                "usage of unset/unknown var " + value + " in composite rule " + curCompositeRule.m_Name
        );

        if(ar.m_IsVar)
        {
            // remove brackets if it's a variable
            value = value.replaceAll(
                    "\\" + Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT,
                    ""
            );
            value = value.replaceAll(
                    Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT,
                    ""
            );
        }

        return value;
    }

    private void AddForLoopToCompRuleBlock(
            IFLogParser.Stat_comp_rule_betweenContext forLoopCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar fromVar = null;
        IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar toVar = null;
        IFLog.Components.CompositeRule.GlobalLocalVar idxVar = null;
        Integer fromValue = null;
        Integer toValue = null;

        // if from is a value
        if(forLoopCtx.from.value != null)
        {
            fromValue = Integer.parseInt(forLoopCtx.from.value.getText());
        }
        // if from is a var
        else
        {
            // check if it's a known local or global var
            fromVar = GetKnownVar(
                    null, forLoopCtx.from.var.getText(), curCompRuleBlock, curCompositeRule,
                    localVarPrefix, false
            );

            Assert.Check(
                    fromVar != null,
                    "couldn't find forLoop from var " + forLoopCtx.from.var.getText() +
                            " in composite rule " + curCompositeRule.m_Name
            );
        }

        // if to is a value
        if(forLoopCtx.to.value != null)
        {
            toValue = Integer.parseInt(forLoopCtx.to.value.getText());

            if(fromValue != null)
            {
                Assert.Check(
                        toValue >= fromValue,
                        "to value in forLoop has to be >= from value in composite rule " + curCompositeRule.m_Name
                );
            }
        }
        // if to is a var
        else
        {
            // check if it's a known local or global var
            toVar = GetKnownVar(
                    null, forLoopCtx.to.var.getText(), curCompRuleBlock, curCompositeRule,
                    localVarPrefix, false
            );

            Assert.Check(
                    toVar != null,
                    "couldn't find forLoop to var " + forLoopCtx.to.var.getText() +
                            " in composite rule " + curCompositeRule.m_Name
            );
        }

        String idxVarName;
        // if idx is a wildcard
        if(forLoopCtx.idx.value != null)
        {
            idxVarName = localVarPrefix + "i";

            idxVar = curCompRuleBlock.GetGlobalVar(idxVarName);

            // duplicate check
            Assert.Check(
                    idxVar == null,
                    "couldn't create local forLoop idx var " + idxVarName +
                            " in composite rule " + curCompositeRule.m_Name +
                            " (if the name was generated via a wildcard assign a value by hand)"
            );
        }
        // if idx is a var
        else
        {
            idxVarName = forLoopCtx.idx.var.getText();

            // check if there's already a global var with the name
            idxVar = curCompRuleBlock.GetGlobalVar(idxVarName);

            if(idxVar == null)
            {
                // check if there's already a local var with the name
                idxVarName = localVarPrefix + forLoopCtx.idx.var.getText();
                idxVar = curCompRuleBlock.GetGlobalVar(idxVarName);
            }

            Assert.Check(
                    idxVar == null || !idxVar.m_IsSet,
                    "idx var " + idxVarName +
                            " in composite rule " + curCompositeRule.m_Name +
                            " already set"
            );
        }

        // if it's an unknown variable
        if(idxVar == null)
        {
            // create new local/global var
            curCompRuleBlock.AddGlobalVar(
                    new IFLog.Components.CompositeRule.GlobalLocalVar(
                            idxVarName, Globals.Translation.Fixed.BT_INT, true, true
                    )
            );
        }
        // if it's a known variable
        else
        {
            // set local/global var if known and type if it should be derived
            curCompRuleBlock.SetGlobalVar(idxVar.m_Name, Globals.Translation.Fixed.BT_INT);
        }

        curCompRuleBlock.AddForLoop(
                (fromVar != null) ? fromVar.m_Name : fromValue.toString(),
                (toVar != null) ? toVar.m_Name : toValue.toString(),
                (idxVar != null) ? idxVar.m_Name : idxVarName
        );
    }

    private void AddForEachLoopToCompRuleBlock(
            IFLogParser.Stat_comp_rule_multiContext forEachLoopCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        if(forEachLoopCtx.stat_comp_rule_multi() != null)
        {
            AddForEachLoopToCompRuleBlock(forEachLoopCtx.stat_comp_rule_multi(), curCompRuleBlock, curCompositeRule, localVarPrefix);
        }
        else
        {
            IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect =
                    new IFLog.Components.CompositeRule.CompRuleBlock.Select();

            AddBodyToSelect(forEachLoopCtx.select, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, false);

            // there needs to be at least one unset var else we don't derive any new values
            // and essentially have an if that's flagged as a select
            Assert.Unequals(
                    curSelect.m_Vars.size(),
                    0,
                    "no unset vars in select of composite rule " + curCompositeRule.m_Name
            );

            // add additional constraints based on tables and common vars
            // as well as for every fixed value that gets passed
            AddAdditionalConstraintsToSelect(curSelect, curCompositeRule, false);

            // replace all flagged vars, e.g.: {x} in constraint,
            // conditional values and vars + undo flags in rest
            ReplaceVarsInSelect(curSelect, curCompRuleBlock, curCompositeRule);

            IFLog.Components.CompositeRule.CompRuleBlock.ForEachLoop curForEachLoop =
                    new IFLog.Components.CompositeRule.CompRuleBlock.ForEachLoop(curSelect);

            // check if all vars defined in multi get set in select
            // and add them to the forEachLoop
            for(var forEachVar : forEachLoopCtx.multi.vars)
            {
                // check if it's a global var
                String varName = forEachVar.getText();
                boolean found = curSelect.GetVar(varName) != null;

                // check if it's a local var
                if(!found)
                {
                    varName = localVarPrefix + varName;
                    found = curSelect.GetVar(varName) != null;
                }

                Assert.Check(
                        found,
                        "couldn't find forEachLoop var " + forEachVar.getText() +
                                " in select of composite rule " + curCompositeRule.m_Name
                );

                // duplicate check
                Assert.Check(
                        curForEachLoop.GetVar(varName) == null,
                        "forEachLoop var " + varName +
                                " already defined in foreach loop of composite rule " + curCompositeRule.m_Name
                );

                curForEachLoop.AddVar(varName);
            }

            curCompRuleBlock.AddForEachLoop(curForEachLoop);
        }
    }

    private void AddSelectsToCompRuleBlock(
            IFLogParser.Stat_comp_rule_selectsContext selectsCtx, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix)
    {
        if(selectsCtx.stat_comp_rule_selects() != null)
        {
            AddSelectsToCompRuleBlock(selectsCtx.stat_comp_rule_selects(), curCompRuleBlock, curCompositeRule, localVarPrefix);
        }
        else
        {
            for(var selectCtx : selectsCtx.elems)
            {
                IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect = new IFLog.Components.CompositeRule.CompRuleBlock.Select();

                AddBodyToSelect(selectCtx, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, false);

                // there needs to be at least one unset var else we don't derive any new values
                // and essentially have an if that's flagged as a select
                Assert.Unequals(
                        curSelect.m_Vars.size(),
                        0,
                        "no unset vars in select of composite rule " + curCompositeRule.m_Name
                );

                // add additional constraints based on tables and common vars
                // as well as for every fixed value that gets passed
                AddAdditionalConstraintsToSelect(curSelect, curCompositeRule, false);

                // replace all flagged vars, e.g.: {x} in constraint,
                // conditional values and vars + undo flags in rest
                ReplaceVarsInSelect(curSelect, curCompRuleBlock, curCompositeRule);

                curCompRuleBlock.AddSelect(curSelect);
            }
        }
    }

    private void AddBodyToSelect(
            IFLogParser.Stat_selectContext selectCtx, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        // if it's another stat select
        if(selectCtx.stat_select() != null)
        {
            AddBodyToSelect(
                    selectCtx.stat_select(), curSelect, curCompRuleBlock,
                    curCompositeRule, localVarPrefix, isHeadElement
            );
        }
        else
        {
            int tableIdx = 0;
            int viewIdx = 0;
            for(var tableView : selectCtx.tables)
            {
                boolean isTable = AddTableViewToSelect(
                        tableView, curSelect, curCompRuleBlock, curCompositeRule,
                        tableIdx, viewIdx, localVarPrefix, isHeadElement
                );
                // if tableView is a table
                if(isTable)
                {
                    tableIdx++;
                }
                // if tableView is a view
                else
                {
                    viewIdx++;
                }
            }

            if(selectCtx.constraint != null)
            {
                AddConstraintToSelect(selectCtx.constraint, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement);
            }
        }
    }

    private boolean AddTableViewToSelect(
            IFLogParser.Stat_tableContext tableView, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule compositeRule,
            int tableIdx, int viewIdx, String localVarPrefix, boolean isHeadElement)
    {
        boolean isTable;
        String tableViewName;
        String tableViewAlias;
        int tableViewType;

        if(tableView.name.trigger_var != null)
        {
            if(tableView.name.trigger_var.getText().equals(Globals.Translation.Fixed.CONST_OLD))
            {
                Assert.Check(
                        compositeRule.OldAllowed(),
                        "usage of " + Globals.Translation.Fixed.CONST_OLD +
                                " not allowed inside composite rule " + compositeRule.m_Name
                );

                tableViewType = IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_OLD;
            }
            else
            {
                Assert.Check(
                        compositeRule.NewAllowed(),
                        "usage of " + Globals.Translation.Fixed.CONST_NEW +
                                " not allowed inside composite rule " + compositeRule.m_Name
                );

                tableViewType = IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_NEW;
            }

            Assert.Check(
                    compositeRule.m_Triggers.size() > 0,
                    "usage of " + Globals.Translation.Fixed.CONST_OLD + "/" + Globals.Translation.Fixed.CONST_NEW +
                    " but no events defined using before/after inside composite rule " + compositeRule.m_Name
            );

            tableViewAlias = GetValueTranslation(tableView.name);
            tableViewName = compositeRule.m_Triggers.get(0).m_Table.m_Name;

            // check that the usage of old/new fits each table definition (if there are multiple)
            // in other words run AddTableViewBodyToSelect() for each additional table but discard all results
            if(compositeRule.m_Triggers.size() > 1)
            {
                for(int i = 1; i < compositeRule.m_Triggers.size(); i++)
                {
                    String tempTableName = compositeRule.m_Triggers.get(i).m_Table.m_Name;

                    // make copies of all relevant objects to ensure that there aren't any hidden side effects
                    IFLog.Components.CompositeRule tempCompositeRule =
                            new IFLog.Components.CompositeRule(compositeRule);
                    IFLog.Components.CompositeRule.CompRuleBlock tempCompRuleBlock =
                            new IFLog.Components.CompositeRule.CompRuleBlock(curCompRuleBlock);
                    IFLog.Components.CompositeRule.CompRuleBlock.Select tempSelect =
                            new IFLog.Components.CompositeRule.CompRuleBlock.Select(curSelect);

                    IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView tempTable =
                            new IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView(
                                    tempTableName,
                                    tableViewAlias,
                                    tableView.negated != null,
                                    tableViewType
                    );

                    AddTableViewBodyToSelect(
                            tableView.table_data_record(), tempSelect, tempCompRuleBlock,
                            tempCompositeRule, tempTable, localVarPrefix, isHeadElement, false
                    );
                }
            }

            // we only allow events on tables
            isTable = true;
        }
        else
        {
            tableViewName = tableView.name.var.getText();
            tableViewType = IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_NORMAL;

            isTable = (GetTable(tableViewName) != null);
            var isView = (GetView(tableViewName) != null);

            // check if container exists
            // no reason to check for duplicates
            Assert.Equals(
                    isTable || isView,
                    true,
                    "no table or view with the name " + tableViewName
            );

            tableViewAlias = isTable ? ("t" + tableIdx) : ("v" + viewIdx) ;
        }

        IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView =
                new IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView(
                        tableViewName,
                        tableViewAlias,
                        tableView.negated != null,
                        tableViewType
        );

        AddTableViewBodyToSelect(
                tableView.table_data_record(), curSelect, curCompRuleBlock,
                compositeRule, curTableView, localVarPrefix, isHeadElement, false
        );

        curSelect.AddTableView(curTableView);

        return isTable;
    }

    private void AddTableViewBodyToSelect(
            IFLogParser.Table_data_recordContext tableViewBody, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView, String localVarPrefix, boolean isHeadElement,
            boolean isInsert)
    {
        var table = GetTable(curTableView.m_Name);
        var view = GetView(curTableView.m_Name);

        // if it's a table
        if(table != null)
        {
            // simple helper to keep track of which fields
            // have default values or are flagged as nullable
            // or are of type auto
            // in other words the ones that are allowed to have wildcards as a value
            Map<String, Boolean> fieldMap = new HashMap<>();
            for(var field : table.m_Fields)
            {
                fieldMap.put(
                        field.m_Name,
                        field.m_DefaultValue != null || field.m_Nullable
                                || GetBasetype(field.m_Type, false).equals(Globals.Translation.Fixed.BT_AUTO));
            }

            // if record only contains values (identification by position)
            if(tableViewBody.table_record() != null)
            {
                // check that number of values matches number of fields
                Assert.Check(
                        tableViewBody.table_record().values.size() <= table.m_Fields.size(),
                        "too many values for select in table " + table.m_Name
                );
                // or contains a multi-wildcard
                if(tableViewBody.table_record().values.size() < table.m_Fields.size())
                {
                    // multi-wildcard needs to be defined at the end
                    var multiWildcard = tableViewBody.table_record().SYM_WILDCARD_MULTI();
                    Assert.Unequals(
                            multiWildcard, null,
                            "not enough values for select in table "
                                    + table.m_Name + " in composite rule " + curCompositeRule.m_Name +
                                    " (you might add a multi-wildcard at the end)"
                    );
                }

                int curFieldIdx = 0;
                for(var curDataInsertCtx : tableViewBody.table_record().values)
                {
                    // if it's not a wildcard
                    if(curDataInsertCtx.SYM_WILDCARD_SINGLE() == null)
                    {
                        // retrieve the field from the current table
                        IFLog.Components.Table.Field field = table.m_Fields.get(curFieldIdx);

                        // if it's an arithmetic statement
                        if(curDataInsertCtx.stat_arithmetic() != null)
                        {
                            AddVarOrValueToSelect(
                                    table, field, curDataInsertCtx.stat_arithmetic(), curTableView,
                                    curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                            );
                        }
                        // if it's a coalesce statement
                        else if(curDataInsertCtx.stat_coalesce() != null)
                        {
                            Assert.Check(
                                    isHeadElement,
                                    "coalesce statements are only allowed in rule heads"
                            );

                            AddCoalesceToSelect(
                                    table, field, curDataInsertCtx.stat_coalesce(), curTableView, curSelect,
                                    curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                            );
                        }

                        // keep track of all fields
                        fieldMap.remove(field.m_Name);
                    }

                    // pointer to current field
                    curFieldIdx++;
                }

                // basically treat select in head like an insert
                // since it's only ever used in combination with a database action
                if(isHeadElement && isInsert)
                {
                    // there should only be fields left that contain a default value or are flagged as nullable
                    Assert.Check(
                            !fieldMap.containsValue(false),
                            "field for data insert is missing a value in table " + table.m_Name
                                    + "\n" + fieldMap
                    );
                }
            }
            // if record contains fields and associated values (identification by name)
            else
            {
                Assert.Check(
                        tableViewBody.table_record_extended().fields.size() <= table.m_Fields.size(),
                        "too many values for select in table " + table.m_Name
                );

                for(int i = 0; i < tableViewBody.table_record_extended().fields.size(); ++i)
                {
                    var curElement = tableViewBody.table_record_extended().values.get(i);

                    // retrieve the field name and make sure it matches
                    // with a field in the current table
                    String fieldName = tableViewBody.table_record_extended().fields.get(i).getText();
                    IFLog.Components.Table.Field field = table.GetField(fieldName);

                    // check if field is part of table
                    Assert.Unequals(
                            field,
                            null,
                            "field " + fieldName + " doesn't exist in table " + table.m_Name
                    );

                    // if it's an arithmetic statement
                    if(curElement.stat_arithmetic() != null)
                    {
                        AddVarOrValueToSelect(
                                table, field, curElement.stat_arithmetic(), curTableView, curSelect,
                                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                        );
                    }
                    // if it's a coalesce statement
                    else if(curElement.stat_coalesce() != null)
                    {
                        Assert.Check(
                                isHeadElement,
                                "coalesce statements are only allowed in rule heads"
                        );

                        AddCoalesceToSelect(
                                table, field, curElement.stat_coalesce(), curTableView, curSelect,
                                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                        );
                    }

                    // keep track of all fields
                    fieldMap.remove(fieldName);
                }

                // basically treat select in head like an insert
                // since it's only ever used in combination with a database action
                if(isHeadElement && isInsert)
                {
                    // there should only be fields left that contain a default value or are flagged as nullable
                    Assert.Check(
                            !fieldMap.containsValue(false),
                            "field for data insert is missing a value in table " + table.m_Name
                                    + "\n" + fieldMap
                    );
                }
            }
        }
        // if it's a view
        else
        {
            // if record only contains values (identification by position)
            if(tableViewBody.table_record() != null)
            {
                // check that number of values matches number of fields
                Assert.Check(
                        tableViewBody.table_record().values.size() <= view.m_Fields.size(),
                        "too many values for select in view " + view.m_Name
                );
                // or contains a multi-wildcard
                if(tableViewBody.table_record().values.size() < view.m_Fields.size())
                {
                    // multi-wildcard needs to be defined at the end
                    var multiWildcard = tableViewBody.table_record().SYM_WILDCARD_MULTI();
                    Assert.Unequals(
                            multiWildcard, null,
                            "not enough values for select in view "
                                    + view.m_Name + " in composite rule " + curCompositeRule.m_Name +
                                    " (you might add a multi-wildcard at the end)"
                    );
                }

                int curFieldIdx = 0;
                for(var curDataInsertCtx : tableViewBody.table_record().values)
                {
                    // if it's not a wildcard
                    if(curDataInsertCtx.SYM_WILDCARD_SINGLE() == null)
                    {
                        // retrieve the field from the current table
                        IFLog.Components.View.Field field = view.m_Fields.get(curFieldIdx);

                        // if it's an arithmetic statement
                        if(curDataInsertCtx.stat_arithmetic() != null)
                        {
                            AddVarOrValueToSelect(
                                    view, field, curDataInsertCtx.stat_arithmetic(), curTableView,
                                    curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                            );
                        }
                        // if it's a coalesce statement
                        // this should never happen since we don't allow actions on views
                        else if(curDataInsertCtx.stat_coalesce() != null)
                        {
                            Assert.Check(
                                    isHeadElement,
                                    "coalesce statements are only allowed in rule heads"
                            );
                        }
                    }

                    // pointer to current field
                    curFieldIdx++;
                }
            }
            // if record contains fields and associated values (identification by name)
            else
            {
                Assert.Check(
                        tableViewBody.table_record_extended().fields.size() <= view.m_Fields.size(),
                        "too many values for select in view " + view.m_Name
                );

                for(int i = 0; i < tableViewBody.table_record_extended().fields.size(); ++i)
                {
                    var curElement = tableViewBody.table_record_extended().values.get(i);

                    // retrieve the field name and make sure it matches
                    // with a field in the current table
                    String fieldName = tableViewBody.table_record_extended().fields.get(i).getText();
                    IFLog.Components.View.Field field = view.GetField(fieldName);

                    // check if field is part of table
                    Assert.Unequals(
                            field,
                            null,
                            "field " + fieldName + " doesn't exist in view " + view.m_Name
                    );

                    // if it's an arithmetic statement
                    if(curElement.stat_arithmetic() != null)
                    {
                        AddVarOrValueToSelect(
                                view, field, curElement.stat_arithmetic(), curTableView,
                                curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                        );
                    }
                    // if it's a coalesce statement
                    // this should never happen since we don't allow actions on views
                    else if(curElement.stat_coalesce() != null)
                    {
                        Assert.Check(
                                isHeadElement,
                                "coalesce statements are only allowed in rule heads"
                        );
                    }
                }
            }
        }
    }

    private void AddCoalesceToSelect(
            Table table, Table.Field tableField, IFLogParser.Stat_coalesceContext coalesceStat,
            IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        String value = ParseAndTranslateCoalesceStatement(
                table, tableField, coalesceStat,
                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
        );

        curTableView.AddField(
                tableField.m_Name,
                null,
                value
        );
    }

    private String ParseAndTranslateCoalesceStatement(
            Table table, Table.Field tableField,
            IFLogParser.Stat_coalesceContext coalesceStat,IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement)
    {
        return ParseAndTranslateCoalesceStatement(
                table, tableField, null, null, coalesceStat,
                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
        );
    }

    private String ParseAndTranslateCoalesceStatement(
            IFLog.Components.CompositeRule compositeRule, IFLog.Components.CompositeRule.CompRuleVar compRuleVar,
            IFLogParser.Stat_coalesceContext coalesceStat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement)
    {
        return ParseAndTranslateCoalesceStatement(
                null, null, compositeRule, compRuleVar, coalesceStat,
                curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
        );
    }

    private String ParseAndTranslateCoalesceStatement(
            Table table, Table.Field tableField,
            IFLog.Components.CompositeRule compositeRule, IFLog.Components.CompositeRule.CompRuleVar compRuleVar,
            IFLogParser.Stat_coalesceContext coalesceStat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String localVarPrefix, boolean isHeadElement)
    {
        String value = ParseCoalesceStatement(
                coalesceStat, curCompRuleBlock, curCompositeRule,
                curCompositeRule.m_Name, localVarPrefix, isHeadElement
        );

        // make sure that the types match
        if(table != null && tableField != null)
        {
            ValidateTypeInDataInsert(tableField, value, table.m_Name);
        }
        else if(compositeRule != null && compRuleVar != null)
        {
            ValidateTypeInDataInsert(compRuleVar, value, compositeRule.m_Name);
        }

        // remove type
        String[] valueList = value.split(Globals.Translation.Fixed.SYM_FLAG);
        value = valueList[0];
        // remove placeholders for SYM_FLAG
        value = value.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG_STR,
                Globals.Translation.Fixed.SYM_FLAG
        );

        String statAsString = m_TranslationMap.get(
                Globals.Translation.CompositeRule.Key(
                        Globals.Translation.Fixed.COMPOSITE_RULE_COALESCE
                )
        );
        statAsString = statAsString.replace(Globals.Translation.CompositeRule.V_VARS, value);

        return statAsString;
    }

    private void AddVarOrValueToSelect(
            Table table, Table.Field tableField, IFLogParser.Stat_arithmeticContext arithmeticStat,
            IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        AddVarOrValueToSelect(
                table, null, tableField, null,
                arithmeticStat, curTableView, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
        );
    }

    private void AddVarOrValueToSelect(
            View view, View.Field viewField, IFLogParser.Stat_arithmeticContext arithmeticStat,
            IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        AddVarOrValueToSelect(
                null, view, null, viewField,
                arithmeticStat, curTableView, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
        );
    }

    private void AddVarOrValueToSelect(
            Table table, View view, Table.Field tableField, View.Field viewField, IFLogParser.Stat_arithmeticContext arithmeticStat,
            IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView curTableView, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        Assert.Check(
                (table != null && tableField != null) || (view != null && viewField != null),
                "table or view data has to be non-null"
        );
        Assert.Check(
                (table == null && tableField == null) || (view == null && viewField == null),
                "only one of table or view is allowed to be non-null"
        );

        // parse the statement for the value
        ArithmeticResult ar = ParseArithmeticStatement(
                arithmeticStat, curSelect, curCompRuleBlock,
                curCompositeRule, curTableView.m_Name, localVarPrefix, true, isHeadElement
        );
        String value = ar.m_Value;

        String fieldName;
        String fieldType;
        // check that the value is valid for field type
        if(table != null && tableField != null)
        {
            ValidateTypeInDataInsert(tableField, value, table.m_Name);
            fieldName = tableField.m_Name;
            fieldType = tableField.m_Type;
        }
        else
        {
            ValidateTypeInDataInsert(viewField, value, view.m_Name);
            fieldName = viewField.m_Name;
            fieldType = viewField.m_Type;
        }

        // remove type
        String[] valueList = value.split(Globals.Translation.Fixed.SYM_FLAG);
        value = valueList[0];
        // remove placeholders for SYM_FLAG
        value = value.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG_STR,
                Globals.Translation.Fixed.SYM_FLAG
        );

        // if var is of type unknown
        // we got a new variable
        // type is same as field
        // value is basically alias for table as prefix + field name
        if(valueList[1].equals(Globals.Translation.Fixed.BT_UNKNOWN))
        {
            String varName = value;
            String varType = GetBasetype(fieldType, true);
            // set this later (cleaner and allows easier checks later)
            String varValue = null;

            // check if it's a global var and if so then set it if it's unset
            var globalVar = curCompRuleBlock.GetGlobalVar(varName);
            if(globalVar != null)
            {
                Assert.Check(
                        !globalVar.m_IsSet,
                        "global var " + globalVar.m_Name + " already set in " + curCompositeRule.m_Name
                );

                // if the type should be derived
                curCompRuleBlock.SetGlobalVar(varName, varType);
            }
            // it's a local var
            else
            {
                // add to compRuleBlock mapping
                varName = localVarPrefix + varName;
                value = localVarPrefix + value;

                curCompRuleBlock.AddGlobalVar(new IFLog.Components.CompositeRule.GlobalLocalVar(varName, varType, true, false));
            }

            curSelect.AddVar(varName, varType, varValue);
        }

        if(ar.m_IsVar)
        {
            // remove brackets if it's a variable
            value = value.replaceAll(
                    "\\" + Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT,
                    ""
            );
            value = value.replaceAll(
                    Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT,
                    ""
            );

            curTableView.AddField(
                    fieldName,
                    value,
                    null
            );
        }
        else
        {
            curTableView.AddField(
                    fieldName,
                    null,
                    value
            );
        }
    }

    private void AddConstraintToSelect(
            IFLogParser.Table_constraint_elContext constraint, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        String bodyAsString = ParseConstraint(constraint, curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement);

        curSelect.AddConstraint(bodyAsString);
    }

    private void AddAdditionalConstraintsToSelect(
            IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect, IFLog.Components.CompositeRule curCompositeRule,
            boolean no_alias)
    {
        StringBuilder newConstraint = new StringBuilder();
        // init with current constraint
        if(curSelect.m_Constraint != null) newConstraint.append(curSelect.m_Constraint);

        for(int i = 0; i < curSelect.m_TablesViews.size(); ++i)
        {
            var thisTableView = curSelect.m_TablesViews.get(i);
            boolean isUnequal = thisTableView.m_Negated;
            boolean hasSetVars = false;

            for(var thisField : curSelect.m_TablesViews.get(i).m_Fields)
            {
                String lhs;

                if(no_alias)
                {
                    lhs = thisField.m_Name;
                }
                else
                {
                    // get the template
                    lhs = m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_FIELD)
                    );
                    // set the tableview alias
                    lhs = lhs.replace(Globals.Translation.CompositeRule.V_ALIAS, thisTableView.m_Alias);
                    // set the field name
                    lhs = lhs.replace(Globals.Translation.CompositeRule.V_NAME, thisField.m_Name);
                }

                String rhs;
                String compOp =
                        (isUnequal) ?
                                m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_UE)) :
                                m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_EQ));

                // if there's a value then it's an additional equal/unequal-condition
                if(thisField.m_Value != null)
                {
                    hasSetVars = true;

                    rhs = thisField.m_Value;

                    // only add AND if there's already something in the constraint
                    if(!newConstraint.isEmpty())
                    {
                        newConstraint.append(" ").append(
                                m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_AND))).append(" ");
                    }

                    newConstraint.append(lhs).append(" ").append(compOp).append(" ").append(rhs);
                }
                // if there's a var then it might be a join or a var reference
                else
                {
                    boolean isJoin = false;

                    for(int j = 0; j < curSelect.m_TablesViews.size(); ++j)
                    {
                        // skip current element
                        if(j == i) continue;

                        var otherTableView = curSelect.m_TablesViews.get(j);

                        for(var otherField : curSelect.m_TablesViews.get(j).m_Fields)
                        {
                            // if two fields in two different tables reference the same variable
                            // then we want to add a join condition to the constraint
                            if(thisField.m_Var.equals(otherField.m_Var))
                            {
                                // always set for later check
                                hasSetVars = true;

                                // don't add any duplicates to constraint
                                if(j < i) continue;

                                // if var is used in at least another tableView
                                // and has an associated field then it is a join
                                isJoin = true;

                                if(!isUnequal)
                                {
                                    isUnequal = otherTableView.m_Negated;
                                }
                                else
                                {
                                    Assert.Check(
                                            !otherTableView.m_Negated,
                                            "only one table can be negated between two tables connected by an logical AND that share a variable in composite rule " +
                                                    curCompositeRule.m_Name
                                    );
                                }

                                if(no_alias)
                                {
                                    rhs = otherField.m_Name;
                                }
                                else
                                {
                                    // get the template
                                    rhs = m_TranslationMap.get(
                                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_FIELD)
                                    );
                                    // set the tableview alias
                                    rhs = rhs.replace(Globals.Translation.CompositeRule.V_ALIAS, otherTableView.m_Alias);
                                    // set the field name
                                    rhs = rhs.replace(Globals.Translation.CompositeRule.V_NAME, otherField.m_Name);
                                }

                                compOp =
                                        (isUnequal) ?
                                                m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_UE)) :
                                                m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_EQ));

                                // only add AND if there's already something in the constraint
                                if(!newConstraint.isEmpty())
                                {
                                    newConstraint.append(" ").append(
                                            m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_AND))).append(" ");
                                }

                                newConstraint.append(lhs).append(" ").append(compOp).append(" ").append(rhs);
                            }
                        }
                    }

                    // if it's simply a var reference (var has to be set beforehand in another select)
                    // skip vars that are part of vars section of select (to avoid redundant self-check)
                    if(!isJoin && curSelect.GetVar(thisField.m_Var) == null)
                    {
                        // always set for later check
                        hasSetVars = true;

                        rhs = thisField.m_Var;

                        // only add AND if there's already something in the constraint
                        if(!newConstraint.isEmpty())
                        {
                            newConstraint.append(" ").append(
                                    m_TranslationMap.get(Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_AND))).append(" ");
                        }

                        newConstraint.append(lhs).append(" ").append(compOp).append(" ").append(rhs);
                    }
                }
            }

            Assert.Check(
                    hasSetVars || !thisTableView.m_Negated,
                    "negation only allowed for selects that have at least one set var or value in composite rule " +
                            curCompositeRule.m_Name
            );
        }

        if(!newConstraint.isEmpty()) curSelect.AddConstraint(newConstraint.toString());
    }

    private void ReplaceVarsInSelect(
            IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule)
    {
        // TODO: skip if already replaced
        //  (currently it still checks multiple times if there are multiple matches for the same var)

        // replace vars in constraint
        if(curSelect.m_Constraint != null)
        {
            String newConstraint = curSelect.m_Constraint;

            newConstraint = ParseStringAndReplaceVars(
                    curSelect, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, newConstraint
            );

            // add new constraint
            curSelect.AddConstraint(newConstraint);
        }

        // replace vars in vars
        for(var selectVar : curSelect.m_Vars)
        {
            String newValue = selectVar.m_Value;
            // if value is null then assign the field of the respective table
            // that the var is supposed to reference
            if(newValue == null)
            {
                newValue = GetMatchingFieldForVar(curSelect, selectVar.m_Name);

                // should never happen
                Assert.Unequals(
                        newValue,
                        null,
                        "couldn't find matching field for var " + selectVar.m_Name + " in composite rule " + curCompositeRule.m_Name
                );
            }
            // if value is non-null replace the vars in it
            else
            {
                newValue = ParseStringAndReplaceVars(
                        curSelect, curCompRuleBlock, curCompositeRule, curCompositeRule.m_Name, newValue
                );
            }

            selectVar.m_Value = newValue;
        }
    }

    private String ParseStringAndReplaceVars(
            IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String tableViewName, String newStringValue)
    {
        Pattern pattern = Pattern.compile("\\{([^}]*)}");
        Matcher matcher = pattern.matcher(newStringValue);

        // extract all the variables
        while (matcher.find())
        {
            String oldVar = matcher.group();
            // remove the flags '{x}' -> 'x'
            oldVar = oldVar.substring(1, oldVar.length() - 1);

            String newVar = null;
            // if var references a field (skip if checking composite rule calls)
            if(curSelect != null)
            {
                newVar = GetMatchingFieldForVar(curSelect, oldVar);
            }

            // if var references a global var or compRule var
            if(newVar == null)
            {
                // check if it's a compRule var
                var compRuleVar = curCompositeRule.GetCompRuleVar(oldVar);

                if(compRuleVar == null)
                {
                    // check if it's a global var which is set
                    var globalVar = curCompRuleBlock.GetGlobalVar(oldVar);

                    if(globalVar != null && globalVar.m_IsSet)
                    {
                        newVar = globalVar.m_Name;
                    }
                }
                else
                {
                    newVar = compRuleVar.m_Name;
                }
            }

            // should never happen
            Assert.Unequals(
                    newVar,
                    null,
                    "couldn't find matching field for var " + oldVar + " in composite rule " + tableViewName
            );

            // replace var with new value
            newStringValue = newStringValue.replace(matcher.group(), newVar);
        }

        // new string shouldn't contain any matches
        // important: check before removing the flags
        matcher = pattern.matcher(newStringValue);
        Assert.Check(
                matcher.results().count() == 0,
                "cant resolve one or more variables while replacing in composite rule " + tableViewName +
                        ", list of variables: " + GetMatchResultsAsString(matcher.reset().results().toList())
        );

        // remove flags
        newStringValue = UndoVarFlag(newStringValue);

        return newStringValue;
    }

    private String GetMatchingFieldForVar(IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect, String selectVar)
    {
        String ret = null;

        boolean found = false;
        // find the field of the first matching tableView
        for(var tableView : curSelect.m_TablesViews)
        {
            if(found) break;

            for(var field : tableView.m_Fields)
            {
                if(field.m_Var != null && field.m_Var.equals(selectVar))
                {
                    // get the template
                    ret = m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.VIEW_SELECT_FIELD)
                    );
                    // set the tableview alias
                    ret = ret.replace(Globals.Translation.CompositeRule.V_ALIAS, tableView.m_Alias);
                    // set the field name
                    ret = ret.replace(Globals.Translation.CompositeRule.V_NAME, field.m_Name);

                    found = true;
                    break;
                }
            }
        }

        return ret;
    }

    private String GetMatchResultsAsString(List<MatchResult> match_results)
    {
        StringBuilder ret = new StringBuilder();

        for(var matchResult : match_results)
        {
            ret.append(matchResult.group()).append(" ");
        }

        return ret.toString();
    }

    private String UndoVarFlag(String value)
    {
        String ret = value;

        // make sure there are no strings using SYM_VAR_FLAG_LEFT or SYM_VAR_FLAG_RIGHT
        ret = ret.replaceAll(
                Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT_STR,
                Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT
        );
        ret = ret.replaceAll(
                Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT_STR,
                Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT
        );

        return ret;
    }

    private String ParseConstraint(
            IFLogParser.Table_constraint_elContext body, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        StringBuilder bodyAsString= new StringBuilder();
        String tableViewName = curCompositeRule.m_Name;

        var negated = body.negated;

        var lRound = body.SYM_LROUND();
        var rRound = body.SYM_RROUND();

        // add not operator
        if(negated != null)
        {
            // get translation for negation
            bodyAsString.append(
                    m_TranslationMap.get(
                            Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_NEGATION)
                    )
            );
        }

        // add left bracket
        if(lRound != null)
        {
            bodyAsString.append(lRound.getText());
        }

        // if the body is another body element in itself
        var bodyEles = body.table_constraint_el();
        if(bodyEles.size() > 0)
        {
            // parse any additional body element recursively
            // and add their result to the body string
            bodyAsString.append(ParseConstraint(bodyEles.get(0), curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement));

            // if there's a logical operator parse the second element as well
            // and add its result to the body string
            // also add the operator
            var logicalOp = body.any_logical_op();
            if(logicalOp != null)
            {
                // get translation for logical operator
                bodyAsString.append(" ").append(GetLogicalOperatorTranslation(logicalOp)).append(" ");
                bodyAsString.append(ParseConstraint(bodyEles.get(1), curSelect, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement));
            }
        }
        // if the body (or current element) is a comparison statement
        else
        {
            // if it is an arithmetic statement
            // parse lhs and rhs of it and add their results
            // together with the logical operator to the body string
            var arithmeticStats = body.stat_comparison().stat_arithmetic();
            if(arithmeticStats.size() > 0)
            {
                var comparisonOp = body.stat_comparison().any_comparison_op();

                String lhs = ParseArithmeticStatement(
                        arithmeticStats.get(0), curSelect, curCompRuleBlock, curCompositeRule,
                        curCompositeRule.m_Name, localVarPrefix, false, isHeadElement).m_Value;
                String rhs = ParseArithmeticStatement(
                        arithmeticStats.get(1), curSelect, curCompRuleBlock, curCompositeRule,
                        curCompositeRule.m_Name, localVarPrefix, false, isHeadElement).m_Value;

                // check that the types are comparable
                ValidateTypesInComparisonStatement(lhs, rhs, tableViewName);

                String[] lhsList = lhs.split(Globals.Translation.Fixed.SYM_FLAG);
                String[] rhsList = rhs.split(Globals.Translation.Fixed.SYM_FLAG);

                // get translation for comparison operator
                // pass types to check if one of them is null (void)
                String comparisonOpStr = GetComparisonOperatorTranslation(
                        comparisonOp,
                        lhsList[1].equals(Globals.Translation.Fixed.BT_VOID)
                                || rhsList[1].equals(Globals.Translation.Fixed.BT_VOID)
                );

                // remove types
                // if lhs is null swap them
                if(lhsList[1].equals(Globals.Translation.Fixed.BT_VOID))
                {
                    rhs = lhsList[0];
                    lhs = rhsList[0];
                }
                else
                {
                    lhs = lhsList[0];
                    rhs = rhsList[0];
                }

                // remove placeholders for SYM_FLAG
                lhs = lhs.replaceAll(
                        Globals.Translation.Fixed.SYM_FLAG_STR,
                        Globals.Translation.Fixed.SYM_FLAG
                );
                rhs = rhs.replaceAll(
                        Globals.Translation.Fixed.SYM_FLAG_STR,
                        Globals.Translation.Fixed.SYM_FLAG
                );

                bodyAsString.append(lhs);
                bodyAsString.append(" ").append(comparisonOpStr).append(" ");
                bodyAsString.append(rhs);
            }
            // if it is an in_statement consisting of both a field
            // and a list of values
            // add them together with the in_operator to the body string
            else
            {
                var inStat = body.stat_comparison().stat_in();
                // technically this is the var name and not the field
                String fieldName = inStat.field.getText();

                // check if it's in select
                var selectVar = curSelect.GetVar(localVarPrefix + fieldName);

                // check if it's a local or global var
                selectVar = GetKnownVar(selectVar, fieldName, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement);

                // make sure the variable is set
                Assert.Unequals(selectVar, null, "var on lhs of in-statement not set in " + tableViewName);

                // check if type of lhs and type of every ele on rhs are valid for operation
                for(var value : inStat.value_list.values)
                {
                    ValidateTypeInList(
                            selectVar.m_Type,
                            GetBasetypeForValue(value, tableViewName),
                            value.getText(),
                            tableViewName
                    );
                }

                // get translation for in comparison operator
                String inOp = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_IN)
                );

                // flag it for later
                bodyAsString.append(Globals.Translation.Fixed.VarFlag(fieldName));
                bodyAsString.append(" ").append(inOp).append(" ");
                // get translation for list
                bodyAsString.append(GetListTranslation(inStat.value_list));
            }
        }

        // add right bracket
        if(rRound != null)
        {
            bodyAsString.append(rRound.getText());
        }

        return bodyAsString.toString();
    }

    private ArithmeticResult ParseArithmeticStatement(
            IFLogParser.Stat_arithmeticContext stat, IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String tableViewName, String localVarPrefix, boolean is_select, boolean isHeadElement)
    {
        StringBuilder statAsString = new StringBuilder();
        boolean is_var = false;

        var lRound = stat.SYM_LROUND();
        var rRound = stat.SYM_RROUND();

        String lhs = "";
        String rhs = "";
        String arithmeticOpStr = "";
        String statType = "";

        // if the statement is another arithmetic statement
        var arithmeticStats = stat.stat_arithmetic();
        if(arithmeticStats.size() > 0)
        {
            boolean isSelect = is_select;
            var arithmeticOp = stat.any_arithmetic_op();
            // if a variable stands alone it's an assignment
            // in any other case it has to be a set variable (known type)
            if(arithmeticOp != null) isSelect = false;

            // parse any additional statements recursively
            // and add their result to the statement string
            ArithmeticResult ar = ParseArithmeticStatement(
                    arithmeticStats.get(0), curSelect, curCompRuleBlock,
                    curCompositeRule, tableViewName, localVarPrefix, isSelect, isHeadElement
            );
            lhs = ar.m_Value;
            is_var = ar.m_IsVar;

            // if there's an arithmetic operator parse the second element as well
            // and add its result to the statement string
            // also add the operator
            if(arithmeticOp != null)
            {
                ar = ParseArithmeticStatement(
                        arithmeticStats.get(1), curSelect, curCompRuleBlock,
                        curCompositeRule, tableViewName, localVarPrefix, false, isHeadElement
                );
                rhs = ar.m_Value;
                is_var = ar.m_IsVar;

                // (1) check if valid op + lhs/rhs type + get res type for whole stat
                // (2) replace concat for string/char type
                // (3) remove types from lhs and rhs
                // (4) add lhs and rhs to statement string
                // (5) add res type to statement string
                statType = GetTypeOfArithmeticStatement(lhs, rhs, arithmeticOp, tableViewName);

                arithmeticOpStr = " " + GetArithmeticOperatorTranslation(arithmeticOp, statType.equals(Globals.Translation.Fixed.BT_STRING)) + " ";

                // remove old types
                lhs = lhs.split(Globals.Translation.Fixed.SYM_FLAG)[0];
                rhs = rhs.split(Globals.Translation.Fixed.SYM_FLAG)[0];
            }
            else
            {
                String[] lhsList = lhs.split(Globals.Translation.Fixed.SYM_FLAG);
                lhs = lhsList[0];
                statType = lhsList[1];
            }
        }
        // if it's an arithmetic element
        // simply add it to the statement string
        else
        {
            var arithmeticStatEle = stat.stat_arithmetic_el();

            // if it's a field
            if(arithmeticStatEle.field != null)
            {
                String fieldName = arithmeticStatEle.field.getText();
                lhs = fieldName;
                is_var = true;

                // check if field type known in select
                // skip if used in ifCondition
                IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar knownVar = null;
                if(curSelect != null)
                {
                    knownVar = curSelect.GetVar(localVarPrefix + fieldName);
                }

                // check if it's a local or global var
                knownVar = GetKnownVar(knownVar, fieldName, curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement);

                if(!is_select)
                {
                    Assert.Unequals(
                            knownVar,
                            null,
                            "usage of unknown variable " + fieldName + " inside arithmetic statement in " + tableViewName
                    );
                }

                if(knownVar != null)
                {
                    statType = knownVar.m_Type;

                    // flag the known variables so that we can later replace them in the translation
                    lhs = Globals.Translation.Fixed.VarFlag(knownVar.m_Name);
                }
                else
                {
                    statType = Globals.Translation.Fixed.BT_UNKNOWN;
                }
            }
            // if it's a value
            else
            {
                var valueCtx = arithmeticStatEle.value;

                lhs = GetValueTranslation(valueCtx);
                statType = GetBasetypeForValue(valueCtx, tableViewName);

                // make sure there are no strings using SYM_VAR_FLAG_LEFT or SYM_VAR_FLAG_RIGHT
                // has to be undone later
                lhs = lhs.replaceAll(
                        "\\" + Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT,
                        Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT_STR
                );
                lhs = lhs.replaceAll(
                        Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT,
                        Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT_STR
                );
                rhs = rhs.replaceAll(
                        "\\" + Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT,
                        Globals.Translation.Fixed.SYM_VAR_FLAG_LEFT_STR
                );
                rhs = rhs.replaceAll(
                        Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT,
                        Globals.Translation.Fixed.SYM_VAR_FLAG_RIGHT_STR
                );
            }
        }

        // make sure there are no strings using SYM_FLAG
        lhs = lhs.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG,
                Globals.Translation.Fixed.SYM_FLAG_STR
        );
        rhs = rhs.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG,
                Globals.Translation.Fixed.SYM_FLAG_STR
        );

        // add left bracket
        if(lRound != null)
        {
            statAsString.append(lRound.getText());
        }

        // add statement
        statAsString.append(lhs);
        statAsString.append(arithmeticOpStr);
        statAsString.append(rhs);

        // add right bracket
        if(rRound != null)
        {
            statAsString.append(rRound.getText());
        }

        // add flag for statement type
        statAsString.append(Globals.Translation.Fixed.Flag(statType));

        return new ArithmeticResult(statAsString.toString(), is_var);
    }

    private String ParseCoalesceStatement(
            IFLogParser.Stat_coalesceContext stat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String tableViewName, String localVarPrefix, boolean isHeadElement)
    {
        StringBuilder bodyAsString = new StringBuilder();

        String statType = null;
        for(var ele : stat.elems)
        {
            String eleType = null;
            String eleValue = null;

            // if element is a variable
            if(ele.var != null)
            {
                // get local or global var with name
                var knownVar = GetKnownVar(
                        null, ele.var.getText(), curCompRuleBlock, curCompositeRule, localVarPrefix, isHeadElement
                );

                // unknown/unset vars not allowed
                Assert.Check(
                        knownVar != null,
                        "unknown (null valued) variables in coalesce statements not allowed in composite rule " + tableViewName
                );

                eleType = knownVar.m_Type;
                eleValue = ele.var.getText();
            }
            // if element is a value
            else
            {
                eleType = GetBasetypeForValue(ele.value, tableViewName);
                eleValue = ele.value.getText();
            }

            if(statType != null)
            {
                // make sure all elements are of the same type
                Assert.Equals(
                        statType,
                        eleType,
                        "only one type for all coalesce elements allowed in composite rule " + tableViewName
                );
            }
            else
            {
                statType = eleType;
            }

            if(!bodyAsString.isEmpty())
            {
                bodyAsString.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }

            bodyAsString.append(eleValue);
        }

        // make sure there are no strings using SYM_FLAG
        String coalesceStat = bodyAsString.toString().replaceAll(
                Globals.Translation.Fixed.SYM_FLAG,
                Globals.Translation.Fixed.SYM_FLAG_STR
        );

        coalesceStat += Globals.Translation.Fixed.Flag(statType);

        return coalesceStat;
    }

    private AssignmentResult ParsePrintStatement(
            IFLogParser.Stat_comp_rule_printContext printStat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String tableViewName, String localVarPrefix, int printIdx, boolean isHeadElement )
    {
        String varName = localVarPrefix + "print_" + printIdx;

        // parse as an arithmetic statement
        String value = ParseArithmeticStatement(
                printStat.stat_arithmetic(), null, curCompRuleBlock, curCompositeRule,
                tableViewName, localVarPrefix, false, isHeadElement).m_Value;

        // remove stat type
        String[] valueList = value.split(Globals.Translation.Fixed.SYM_FLAG);
        value = valueList[0];
        // remove placeholders for SYM_FLAG
        value = value.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG_STR,
                Globals.Translation.Fixed.SYM_FLAG
        );

        // check that print value is of type string
        // but only throw warning since there might be implicit conversions that are wanted
        if(!Globals.Translation.Fixed.BT_STRING.equals(valueList[1]))
        {
            Logger.LogWarning("print value is not of type string in composite rule " + curCompositeRule.m_Name);
        }

        // create a new local var
        curCompRuleBlock.AddGlobalVar(
                new IFLog.Components.CompositeRule.GlobalLocalVar(varName, valueList[1], true, false)
        );

        return new AssignmentResult(varName, valueList[1], value);
    }

    private AssignmentResult ParseAssignmentStatement(
            IFLogParser.Stat_assignmentContext stat, IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock,
            IFLog.Components.CompositeRule curCompositeRule, String tableViewName, String localVarPrefix, boolean isHeadElement)
    {
        AssignmentResult ret = null;

        if(stat.stat_assignment() != null)
        {
            ret = ParseAssignmentStatement(stat.stat_assignment(), curCompRuleBlock, curCompositeRule, tableViewName, localVarPrefix, isHeadElement);
        }
        else
        {
            var selectVars = stat.vars;
            String selectVarName0 = selectVars.get(0).getText();

            // if assignment between two vars
            if(selectVars.size() > 1)
            {
                String selectVarName1 = selectVars.get(1).getText();

                var selectVar0 = GetKnownVar(
                        null, selectVarName0, curCompRuleBlock, curCompositeRule,
                        localVarPrefix, isHeadElement
                );
                var selectVar1 = GetKnownVar(
                        null, selectVarName1, curCompRuleBlock, curCompositeRule,
                        localVarPrefix, isHeadElement
                );

                Assert.Check(
                        !(selectVar0 == null && selectVar1 == null),
                        "assignment between two unknown variables in " + tableViewName
                );
                Assert.Check(
                        !(selectVar0 != null && selectVar1 != null),
                        "assignment between two known variables in " + tableViewName
                );

                if(selectVar0 != null)
                {
                    ret = new AssignmentResult(
                            selectVarName1,
                            selectVar0.m_Type,
                            Globals.Translation.Fixed.VarFlag(selectVar0.m_Name)
                    );
                }
                else if(selectVar1 != null)
                {
                    ret = new AssignmentResult(
                            selectVarName0,
                            selectVar1.m_Type,
                            Globals.Translation.Fixed.VarFlag(selectVar1.m_Name)
                    );
                }
            }
            // if assignment between var and value
            else
            {
                String value = ParseArithmeticStatement(
                        stat.stat_arithmetic(), null, curCompRuleBlock, curCompositeRule,
                        tableViewName, localVarPrefix, false, isHeadElement).m_Value;

                // remove stat type
                String[] valueList = value.split(Globals.Translation.Fixed.SYM_FLAG);
                value = valueList[0];
                // remove placeholders for SYM_FLAG
                value = value.replaceAll(
                        Globals.Translation.Fixed.SYM_FLAG_STR,
                        Globals.Translation.Fixed.SYM_FLAG
                );

                ret = new AssignmentResult(selectVarName0, valueList[1], value);
            }
        }

        String varName;
        // could also be that the "unknown" variable is a global var that hasn't been set yet
        var globalVar = curCompRuleBlock.GetGlobalVar(ret.m_Name);
        if(globalVar != null)
        {
            varName = globalVar.m_Name;
            curCompRuleBlock.SetGlobalVar(varName, ret.m_Type);
        }
        // else we create a new local var with the localVar prefix
        else
        {
            varName = localVarPrefix + ret.m_Name;
            curCompRuleBlock.AddGlobalVar(
                    new IFLog.Components.CompositeRule.GlobalLocalVar(varName, ret.m_Type, true, false)
            );
        }

        // apply any changes to the return value
        ret.m_Name = varName;

        return ret;
    }

    private IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar GetKnownVar(
            IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar selectVar, String selectVarName,
            IFLog.Components.CompositeRule.CompRuleBlock curCompRuleBlock, IFLog.Components.CompositeRule curCompositeRule,
            String localVarPrefix, boolean isHeadElement)
    {
        if(selectVar == null)
        {
            // check if it's a compRule var
            var compRuleVar = curCompositeRule.GetCompRuleVar(selectVarName);

            if(compRuleVar == null)
            {
                // check if it's a global or local var which is set in the current compRuleBlock
                IFLog.Components.CompositeRule.GlobalLocalVar globalVar;
                IFLog.Components.CompositeRule.GlobalLocalVar localVar;

                // if it's a head element set the wasAccessed flag
                if(isHeadElement)
                {
                    globalVar = curCompRuleBlock.AccessGlobalVar(selectVarName);
                    localVar = curCompRuleBlock.AccessGlobalVar(localVarPrefix + selectVarName);
                }
                else
                {
                    globalVar = curCompRuleBlock.GetGlobalVar(selectVarName);
                    localVar = curCompRuleBlock.GetGlobalVar(localVarPrefix + selectVarName);
                }

                if(globalVar != null && globalVar.m_IsSet)
                {
                    selectVar = new IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar(
                            globalVar.m_Name, globalVar.m_Type, null
                    );
                }
                else if(localVar != null && localVar.m_IsSet)
                {
                    selectVar = new IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar(
                            localVar.m_Name, localVar.m_Type, null
                    );
                }
            }
            else
            {
                selectVar = new IFLog.Components.CompositeRule.CompRuleBlock.Select.SelectVar(
                        compRuleVar.m_Name, compRuleVar.m_Type, null
                );
            }
        }

        return selectVar;
    }

    protected final String GetValueTranslation(IFLogParser.Stat_table_elContext valueCtx)
    {
        String value = null;

        if(valueCtx.V_TRIGGER() != null)
        {
            // it's either old or new
            value = m_TranslationMap.get(
                    Globals.Translation.Constant.Key(valueCtx.V_TRIGGER().getText())
            );
        }

        Assert.Unequals(
                value,
                null,
                "no translation found for value " + valueCtx.getText()
        );

        return value;
    }

    private static class CompRuleTagResult
    {
        public IFLog.Components.CompositeRule.CompRuleBlock m_ParentCompRuleBlock;
        public String m_AlternativeTo;

        public CompRuleTagResult(IFLog.Components.CompositeRule.CompRuleBlock parentCompRuleBlock, String alternativeTo)
        {
            m_ParentCompRuleBlock = parentCompRuleBlock;
            m_AlternativeTo = alternativeTo;
        }
    }

    private static class ArithmeticResult
    {
        public String m_Value;
        public boolean m_IsVar;

        public ArithmeticResult(String value, boolean is_var)
        {
            m_Value = value;
            m_IsVar = is_var;
        }
    }

    private static class AssignmentResult
    {
        public String m_Name;
        public String m_Type;
        public String m_Value;

        public AssignmentResult(String name, String type, String value)
        {
            m_Name = name;
            m_Type = type;
            m_Value = value;
        }
    }
}
