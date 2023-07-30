package IFLog.Translators;

import IFLog.Core.Globals;
import IFLog.Core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompositeRule extends Base
{
    /**
     *
     * Constructor.
     *
     * @param TranslationMap
     * @param CompositeRuleList
     */
    public CompositeRule(final Map<String, String> TranslationMap, final List<IFLog.Components.CompositeRule> CompositeRuleList)
    {
        super(TranslationMap);

        m_CompositeRuleList = CompositeRuleList;
        m_CompositeRules = new ArrayList<>();
        m_Triggers = new ArrayList<>();
    }

    /**
     *
     * Main function.
     *
     */
    @Override
    public void Translate()
    {
        for(var curCompositeRule : m_CompositeRuleList)
        {
            String compRuleVars = GetCompRuleVarsTranslation(curCompositeRule);
            String globalVars = GetGlobalVarsTranslation(curCompositeRule);
            StringBuilder compositeRuleBody = new StringBuilder();

            for(var curCompRule : curCompositeRule.m_CompRuleBlocks)
            {
                compositeRuleBody.append(Globals.Translation.Fixed.SEP_NEWLINE);
                compositeRuleBody.append(GetCompRuleTranslation(curCompRule, curCompositeRule));
                compositeRuleBody.append(Globals.Translation.Fixed.SEP_NEWLINE);
            }

            // get the template
            String curCompositeRuleBlock = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_CREATE)
            );

            // set the name
            curCompositeRuleBlock = curCompositeRuleBlock.replace(
                    Globals.Translation.CompositeRule.V_NAME,
                    curCompositeRule.m_Name
            );
            // set the comp rule vars
            curCompositeRuleBlock = curCompositeRuleBlock.replace(
                    Globals.Translation.CompositeRule.V_COMP_RULE_VARS,
                    compRuleVars
            );
            // set the return type
            curCompositeRuleBlock = curCompositeRuleBlock.replace(
                    Globals.Translation.CompositeRule.V_TYPE,
                    GetTypeTranslation(curCompositeRule.m_ReturnType, true)
            );
            // set the global vars
            curCompositeRuleBlock = curCompositeRuleBlock.replace(
                    Globals.Translation.CompositeRule.V_GLOBAL_VARS,
                    globalVars
            );
            // set the body
            curCompositeRuleBlock = curCompositeRuleBlock.replace(
                    Globals.Translation.CompositeRule.V_BODY,
                    compositeRuleBody
            );

            // add terminal symbol
            curCompositeRuleBlock += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            // add it
            m_CompositeRules.add(curCompositeRuleBlock);

            for(var curTrigger : curCompositeRule.m_Triggers)
            {
                m_Triggers.add(GetTriggerTranslation(curCompositeRule.m_Name, curTrigger));
            }
        }
    }

    private String GetTriggerTranslation(String compositeRuleName, IFLog.Components.CompositeRule.Trigger curTrigger)
    {
        // get trigger table template
        String triggerTableTemplate = m_TranslationMap.get(
                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_TABLE)
        );
        // set the table
        triggerTableTemplate = triggerTableTemplate.replace(
                Globals.Translation.CompositeRule.V_TABLE,
                curTrigger.m_Table.m_Name
        );

        // get trigger table + fields
        String triggerFieldsTemplate = "";
        if(curTrigger.m_Table.m_Fields.size() > 0)
        {
            StringBuilder fieldsBlock = new StringBuilder();
            for(var field : curTrigger.m_Table.m_Fields)
            {
                if(!fieldsBlock.isEmpty())
                {
                    fieldsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                }

                fieldsBlock.append(field);
            }

            // get trigger fields template
            triggerFieldsTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_FIELDS)
            );
            // set the table
            triggerFieldsTemplate = triggerFieldsTemplate.replace(
                    Globals.Translation.CompositeRule.V_FIELDS,
                    fieldsBlock
            );
        }

        // handle trigger type (before/after)
        StringBuilder triggerType = new StringBuilder();
        // if before trigger
        if(curTrigger.m_IsBefore)
        {
            triggerType.append(
                    m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_TYPE_BEFORE)
                    )
            );
        }
        // if after trigger
        else
        {
            triggerType.append(
                    m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_TYPE_AFTER)
                    )
            );
        }

        // handle trigger type (insert/update/delete)
        // if insert event
        if(curTrigger.m_Type == IFLog.Components.CompositeRule.Trigger.TYPE_INS_EVENT)
        {
            triggerType.append(
                    m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_TYPE_INSERT)
                    )
            );
        }
        // if delete event
        else if(curTrigger.m_Type == IFLog.Components.CompositeRule.Trigger.TYPE_DEL_EVENT)
        {
            triggerType.append(
                    m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_TYPE_DELETE)
                    )
            );
        }
        // if update event
        else if(curTrigger.m_Type == IFLog.Components.CompositeRule.Trigger.TYPE_UPD_EVENT)
        {
            triggerType.append(
                    m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_TRIGGER_TYPE_UPDATE)
                    )
            );
        }

        // handle trigger translation
        // get the template
        String triggerTemplate = m_TranslationMap.get(
                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_CREATE_TRIGGER)
        );
        // set the name
        triggerTemplate = triggerTemplate.replace(
                Globals.Translation.CompositeRule.V_NAME,
                curTrigger.m_Name
        );
        // set the type
        triggerTemplate = triggerTemplate.replace(
                Globals.Translation.CompositeRule.V_TYPE,
                triggerType
        );
        // set the table
        triggerTemplate = triggerTemplate.replace(
                Globals.Translation.CompositeRule.V_TABLE,
                triggerTableTemplate
        );
        // set the fields
        triggerTemplate = triggerTemplate.replace(
                Globals.Translation.CompositeRule.V_FIELDS,
                triggerFieldsTemplate
        );
        // set the composite rule
        triggerTemplate = triggerTemplate.replace(
                Globals.Translation.CompositeRule.V_COMPOSITE_RULE,
                compositeRuleName
        );

        // add terminal symbol
        triggerTemplate += m_TranslationMap.get(Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL));

        return triggerTemplate;
    }

    private String GetCompRuleVarsTranslation(IFLog.Components.CompositeRule curCompositeRule)
    {
        StringBuilder compRuleVarsBlock = new StringBuilder();

        for(var compRuleVar : curCompositeRule.m_CompRuleVars)
        {
            boolean hasDefault = compRuleVar.m_DefaultValue != null;

            if(!compRuleVarsBlock.isEmpty())
            {
                compRuleVarsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }

            // get the template
            String curCompRuleVar;
            if(hasDefault)
            {
                curCompRuleVar = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_COMP_RULE_VAR_DEFAULT)
                );
            }
            else
            {
                curCompRuleVar = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_COMP_RULE_VAR)
                );
            }

            // set the name
            curCompRuleVar = curCompRuleVar.replace(
                    Globals.Translation.CompositeRule.V_NAME,
                    compRuleVar.m_Name
            );
            // set the type
            curCompRuleVar = curCompRuleVar.replace(
                    Globals.Translation.CompositeRule.V_TYPE,
                    GetTypeTranslation(compRuleVar.m_Type, true)
            );
            // set the default value
            if(hasDefault)
            {
                curCompRuleVar = curCompRuleVar.replace(
                        Globals.Translation.CompositeRule.V_DEFAULT,
                        compRuleVar.m_DefaultValue
                );
            }

            compRuleVarsBlock.append(curCompRuleVar);
        }

        return compRuleVarsBlock.toString();
    }

    private String GetGlobalVarsTranslation(IFLog.Components.CompositeRule curCompositeRule)
    {
        StringBuilder globalVarsBlock = new StringBuilder();

        for(var globalVar : curCompositeRule.m_AllVars)
        {
            if(!globalVarsBlock.isEmpty())
            {
                globalVarsBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
            }

            // get the template
            String curGlobalVar = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_GLOBAL_VAR)
            );

            // set the name
            curGlobalVar = curGlobalVar.replace(
                    Globals.Translation.CompositeRule.V_NAME,
                    globalVar.m_Name
            );
            // set the type
            curGlobalVar = curGlobalVar.replace(
                    Globals.Translation.CompositeRule.V_TYPE,
                    GetTypeTranslation(globalVar.m_Type, true)
            );

            // add terminal symbol
            curGlobalVar += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            globalVarsBlock.append(curGlobalVar);
        }

        return globalVarsBlock.toString();
    }

    private String GetCompRuleTranslation(IFLog.Components.CompositeRule.CompRuleBlock curCompRule, IFLog.Components.CompositeRule curCompositeRule)
    {
        StringBuilder curCompRuleBlock = new StringBuilder();

        String compRuleOuterBlock = null;
        StringBuilder compRuleInnerBlock = new StringBuilder();

        // translate the body

        // it's an else
        if(curCompRule.m_IsElseBegin)
        {
            // get the template
            compRuleOuterBlock = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_ELSE_COND)
            );
        }

        // it's an if or elif
        if(curCompRule.m_IfCondition != null)
        {
            // it's an elif
            if(curCompRule.m_AlternativeTo != null)
            {
                // get the template
                compRuleOuterBlock = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_ELIF_COND)
                );
            }
            else
            {
                // get the template
                compRuleOuterBlock = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_IF_COND)
                );
            }

            // set the condition
            compRuleOuterBlock = compRuleOuterBlock.replace(
                    Globals.Translation.CompositeRule.V_CONDITION,
                    curCompRule.m_IfCondition
            );
        }
        // it's a for loop
        else if(curCompRule.m_ForLoop != null)
        {
            // get the template
            String forLoopTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_FOR_LOOP)
            );

            // set loop index var
            forLoopTemplate = forLoopTemplate.replace(
                    Globals.Translation.CompositeRule.V_IDX_VAR,
                    curCompRule.m_ForLoop.m_IdxVar
            );
            // set from var/value
            forLoopTemplate = forLoopTemplate.replace(
                    Globals.Translation.CompositeRule.V_FROM_VAR,
                    curCompRule.m_ForLoop.m_FromVarValue
            );
            // set to var/value
            forLoopTemplate = forLoopTemplate.replace(
                    Globals.Translation.CompositeRule.V_TO_VAR,
                    curCompRule.m_ForLoop.m_ToVarValue
            );

            // add terminal symbol
            forLoopTemplate += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            // if the for loop is inside an else
            if(compRuleOuterBlock != null)
            {
                compRuleOuterBlock = compRuleOuterBlock.replace(
                        Globals.Translation.CompositeRule.V_BODY,
                        forLoopTemplate
                );
            }
            // if the for loop is the most outer block
            else
            {
                compRuleOuterBlock = forLoopTemplate;
            }
        }
        // it's a for each loop
        else if(curCompRule.m_ForEachLoop != null)
        {
            // build up vars block
            StringBuilder forEachVarsBlock = new StringBuilder();
            for(var forEachVar : curCompRule.m_ForEachLoop.m_Vars)
            {
                if(!forEachVarsBlock.isEmpty())
                {
                    forEachVarsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                }

                forEachVarsBlock.append(forEachVar);
            }

            // build up select block
            String forEachSelectBlock = GetSelectTranslation(curCompRule.m_ForEachLoop.m_Select, curCompRule.m_ForEachLoop.m_Vars);

            // get the template
            String forEachLoopTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_FOR_EACH_LOOP)
            );

            // set vars block
            forEachLoopTemplate = forEachLoopTemplate.replace(
                    Globals.Translation.CompositeRule.V_VARS,
                    forEachVarsBlock
            );
            // set select block
            forEachLoopTemplate = forEachLoopTemplate.replace(
                    Globals.Translation.CompositeRule.V_SELECT,
                    forEachSelectBlock
            );

            // add terminal symbol
            forEachLoopTemplate += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            // if the for each loop is inside an else
            if(compRuleOuterBlock != null)
            {
                compRuleOuterBlock = compRuleOuterBlock.replace(
                        Globals.Translation.CompositeRule.V_BODY,
                        forEachLoopTemplate
                );
            }
            // if the for each loop is the most outer block
            else
            {
                compRuleOuterBlock = forEachLoopTemplate;
            }
        }
        else if(curCompRule.m_Selects.size() > 0)
        {
            for(var curSelect : curCompRule.m_Selects)
            {
                if(!compRuleInnerBlock.isEmpty())
                {
                    compRuleInnerBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                }

                compRuleInnerBlock.append(GetSelectIntoTranslation(curSelect, curCompositeRule));
            }
        }
        // in any other case it's a body-less rule

        // translate the head

        for(var orderedAction : curCompRule.m_OrderedActions)
        {
            if(!compRuleInnerBlock.isEmpty())
            {
                compRuleInnerBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
            }
            
            // it's an assignment
            if(orderedAction.m_Assignment != null)
            {
                compRuleInnerBlock.append(
                        GetAssignmentTranslation(orderedAction.m_Assignment.m_Name, orderedAction.m_Assignment.m_Value)
                );
            }
            // it's a print statement
            else if(orderedAction.m_PrintStat != null)
            {
                compRuleInnerBlock.append(orderedAction.m_PrintStat);

                // add terminal symbol
                compRuleInnerBlock.append(
                        m_TranslationMap.get(Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL))
                );
            }
            // it's a composite rule call
            else if(orderedAction.m_CompRuleCall != null)
            {
                compRuleInnerBlock.append(orderedAction.m_CompRuleCall);

                // add terminal symbol
                compRuleInnerBlock.append(
                        m_TranslationMap.get(Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL))
                );
            }
            // it's a database action
            else if(orderedAction.m_DatabaseAction != null)
            {
                // it's an insert
                if(orderedAction.m_DatabaseAction
                        instanceof IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Insert insertAction)
                {
                    StringBuilder fieldsBlock = new StringBuilder();
                    StringBuilder valuesBlock = new StringBuilder();

                    for(var fieldValuePair : insertAction.m_FieldValuePairs)
                    {
                        if(!fieldsBlock.isEmpty())
                        {
                            fieldsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                            valuesBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                        }

                        fieldsBlock.append(fieldValuePair.m_Field);
                        valuesBlock.append(fieldValuePair.m_Value);
                    }

                    // get the template
                    String insertTemplate = m_TranslationMap.get(
                            Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_DB_INSERT)
                    );

                    // set name
                    insertTemplate = insertTemplate.replace(
                            Globals.Translation.CompositeRule.V_NAME,
                            insertAction.m_Table
                    );
                    // set fields block
                    insertTemplate = insertTemplate.replace(
                            Globals.Translation.CompositeRule.V_FIELDS,
                            fieldsBlock
                    );
                    // set values block
                    insertTemplate = insertTemplate.replace(
                            Globals.Translation.CompositeRule.V_VALUES,
                            valuesBlock
                    );

                    compRuleInnerBlock.append(insertTemplate);
                }
                // it's an delete
                else if(orderedAction.m_DatabaseAction
                        instanceof IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Delete deleteAction)
                {
                    String deleteTemplate;

                    // it's on a specific set of rows
                    if(deleteAction.m_Constraint != null)
                    {
                        // get the template
                        deleteTemplate = m_TranslationMap.get(
                                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_DB_DELETE)
                        );

                        // set condition
                        deleteTemplate = deleteTemplate.replace(
                                Globals.Translation.CompositeRule.V_CONDITION,
                                deleteAction.m_Constraint
                        );
                    }
                    // it's on the whole table
                    else
                    {
                        // get the template
                        deleteTemplate = m_TranslationMap.get(
                                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_DB_DELETE_TABLE)
                        );
                    }

                    // set name
                    deleteTemplate = deleteTemplate.replace(
                            Globals.Translation.CompositeRule.V_NAME,
                            deleteAction.m_Table
                    );

                    compRuleInnerBlock.append(deleteTemplate);
                }
                // it's an update
                else if(orderedAction.m_DatabaseAction
                        instanceof IFLog.Components.CompositeRule.CompRuleBlock.OrderedAction.DatabaseAction.Update updateAction)
                {
                    StringBuilder fieldsBlock = new StringBuilder();
                    StringBuilder valuesBlock = new StringBuilder();

                    for(var fieldValuePair : updateAction.m_FieldValuePairs)
                    {
                        if(!fieldsBlock.isEmpty())
                        {
                            fieldsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                            valuesBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                        }

                        fieldsBlock.append(fieldValuePair.m_Field);
                        valuesBlock.append(fieldValuePair.m_Value);
                    }

                    String updateTemplate = null;
                    if(updateAction.m_FieldValuePairs.size() > 1)
                    {
                        // get the template
                        updateTemplate = m_TranslationMap.get(
                                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_DB_UPDATE)
                        );
                        // set fields block
                        updateTemplate = updateTemplate.replace(
                                Globals.Translation.CompositeRule.V_FIELDS,
                                fieldsBlock
                        );
                        // set values block
                        updateTemplate = updateTemplate.replace(
                                Globals.Translation.CompositeRule.V_VALUES,
                                valuesBlock
                        );
                    }
                    else
                    {
                        // get the template
                        updateTemplate = m_TranslationMap.get(
                                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_DB_UPDATE_SINGLE)
                        );
                        // set field block
                        updateTemplate = updateTemplate.replace(
                                Globals.Translation.CompositeRule.V_FIELD,
                                fieldsBlock
                        );
                        // set value block
                        updateTemplate = updateTemplate.replace(
                                Globals.Translation.CompositeRule.V_VALUE,
                                valuesBlock
                        );
                    }

                    // set name
                    updateTemplate = updateTemplate.replace(
                            Globals.Translation.CompositeRule.V_NAME,
                            updateAction.m_Table
                    );
                    // set condition
                    updateTemplate = updateTemplate.replace(
                            Globals.Translation.CompositeRule.V_CONDITION,
                            updateAction.m_Constraint
                    );

                    compRuleInnerBlock.append(updateTemplate);
                }

                // add terminal symbol
                compRuleInnerBlock.append(
                        m_TranslationMap.get(Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL))
                );
            }
        }

        if(curCompRule.m_ReturnValue != null)
        {
            if(!compRuleInnerBlock.isEmpty())
            {
                compRuleInnerBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
            }

            // get the template
            String returnTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_RETURN)
            );

            // set the value
            returnTemplate = returnTemplate.replace(
                    Globals.Translation.CompositeRule.V_VALUE,
                    curCompRule.m_ReturnValue
            );

            // add it
            compRuleInnerBlock.append(returnTemplate);

            // add terminal symbol
            compRuleInnerBlock.append(
                    m_TranslationMap.get(Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL))
            );
        }

        if(!curCompRuleBlock.isEmpty())
        {
            curCompRuleBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
            curCompRuleBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
        }

        if(compRuleOuterBlock != null)
        {
            // translate any nested compRuleBlocks
            for(var nestedCompRule : curCompRule.m_CompRuleBlocks)
            {
                if(!compRuleInnerBlock.isEmpty())
                {
                    compRuleInnerBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                    compRuleInnerBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                }

                // add any nested compRuleBlocks to the inner block translation
                compRuleInnerBlock.append(
                        GetCompRuleTranslation(nestedCompRule, curCompositeRule)
                );
            }

            // add inner to outer block
            compRuleOuterBlock = compRuleOuterBlock.replace(
                    Globals.Translation.CompositeRule.V_BODY,
                    compRuleInnerBlock
            );

            // add it
            curCompRuleBlock.append(compRuleOuterBlock);
        }
        else
        {
            // add it
            curCompRuleBlock.append(compRuleInnerBlock);
        }

        // if it's the last block of an if-elif-else
        if(curCompRule.m_IsIfEnd)
        {
            curCompRuleBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);

            // get the template
            String endIfCondTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_ENDIF_COND)
            );

            // add terminal symbol
            endIfCondTemplate += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            // add it
            curCompRuleBlock.append(endIfCondTemplate);
        }

        return curCompRuleBlock.toString();
    }

    private String GetSelectIntoTranslation(IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect, IFLog.Components.CompositeRule curCompositeRule)
    {
        StringBuilder ret = new StringBuilder();

        // create assignments for all old/new vars
        StringBuilder assignmentsBlock = new StringBuilder();

        // everything else should be in here
        StringBuilder selectBlock = new StringBuilder();
        StringBuilder selectHeadVarsBlock = new StringBuilder();
        StringBuilder selectHeadFieldsBlock = new StringBuilder();
        StringBuilder selectFromBlock = new StringBuilder();

        List<String> addedSelectVars = new ArrayList<>();

        for(var curTableView : curSelect.m_TablesViews)        {

            // if it's an old/new table
            if(curTableView.m_Type != IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_NORMAL)
            {
                // make an assignment for each var
                for(var field : curTableView.m_Fields)
                {
                    var selectVar = curSelect.GetVar(field.m_Var);

                    // skip comp rule vars, selectVars that were already added
                    // and vars that are not part of all global and local vars
                    if(selectVar == null ||
                            addedSelectVars.contains(selectVar.m_Name) ||
                            curCompositeRule.GetAllVar(field.m_Var) == null)
                        continue;

                    if(!assignmentsBlock.isEmpty())
                    {
                        assignmentsBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                    }

                    assignmentsBlock.append(
                            GetAssignmentTranslation(selectVar.m_Name, selectVar.m_Value)
                    );

                    // keep track of all added selectVars
                    addedSelectVars.add(selectVar.m_Name);
                }
            }
            // if it's a normal table
            else
            {
                // handle the select head block
                for(var field : curTableView.m_Fields)
                {
                    var selectVar = curSelect.GetVar(field.m_Var);

                    // skip comp rule vars, selectVars that were already added
                    // and vars that are not part of all global and local vars
                    if(selectVar == null ||
                            addedSelectVars.contains(selectVar.m_Name) ||
                            curCompositeRule.GetAllVar(field.m_Var) == null)
                        continue;

                    if(!selectHeadVarsBlock.isEmpty())
                    {
                        selectHeadVarsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                        selectHeadFieldsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                    }

                    selectHeadVarsBlock.append(selectVar.m_Name);
                    selectHeadFieldsBlock.append(selectVar.m_Value);

                    // keep track of all added selectVars
                    addedSelectVars.add(selectVar.m_Name);
                }

                // handle the select from block
                // get the tableView template
                String selectTableViewTemplate = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_TABLEVIEW)
                );
                // set the name
                selectTableViewTemplate = selectTableViewTemplate.replace(
                        Globals.Translation.CompositeRule.V_NAME,
                        curTableView.m_Name
                );
                // set the alias
                selectTableViewTemplate = selectTableViewTemplate.replace(
                        Globals.Translation.CompositeRule.V_ALIAS,
                        curTableView.m_Alias
                );

                // add to the list of tables and views
                if(!selectFromBlock.isEmpty())
                {
                    selectFromBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
                }
                selectFromBlock.append(selectTableViewTemplate);
            }
        }

        // if there are no tables in from block then we only have assignments
        if(!selectFromBlock.isEmpty())
        {
            // handle the select head block translation
            // get the template
            String selectHeadTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_INTO)
            );
            // set the vars
            selectHeadTemplate = selectHeadTemplate.replace(
                    Globals.Translation.CompositeRule.V_VARS,
                    selectHeadVarsBlock
            );
            // set the fields
            selectHeadTemplate = selectHeadTemplate.replace(
                    Globals.Translation.CompositeRule.V_FIELDS,
                    selectHeadFieldsBlock
            );
            // add it to the select block
            selectBlock.append(selectHeadTemplate);

            // handle the select from block translation
            // get the template
            String selectFromTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_FROM)
            );
            // set the table and views
            selectFromTemplate = selectFromTemplate.replace(
                    Globals.Translation.CompositeRule.V_TABLEVIEWS,
                    selectFromBlock
            );
            // add it to the select block
            selectBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
            selectBlock.append(selectFromTemplate);

            // handle the select where block translation
            if(curSelect.m_Constraint != null)
            {
                // get the template
                String selectWhereTemplate = m_TranslationMap.get(
                        Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_WHERE)
                );
                // set the condition
                selectWhereTemplate = selectWhereTemplate.replace(
                        Globals.Translation.CompositeRule.V_CONDITION,
                        curSelect.m_Constraint
                );
                // add it to the select block
                selectBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                selectBlock.append(selectWhereTemplate);
            }

            // add terminal symbol
            selectBlock.append(
                    m_TranslationMap.get(Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL))
            );
        }

        // add assignment block to ret
        if(!assignmentsBlock.isEmpty())
        {
            ret.append(assignmentsBlock);
        }

        // add select block to ret
        if(!selectBlock.isEmpty())
        {
            if(!ret.isEmpty())
            {
                ret.append(Globals.Translation.Fixed.SEP_NEWLINE);
            }

            ret.append(selectBlock);
        }

        return ret.toString();
    }

    private String GetSelectTranslation(IFLog.Components.CompositeRule.CompRuleBlock.Select curSelect, List<String> forEachLoopVars)
    {
        StringBuilder selectBlock = new StringBuilder();
        StringBuilder selectHeadFieldsBlock = new StringBuilder();
        StringBuilder selectFromBlock = new StringBuilder();

        // handle the select head block
        // loop over vars to preserve the order
        // allows to freely order them in multi()
        for(var forEachLoopVar : forEachLoopVars)
        {
            if(!selectHeadFieldsBlock.isEmpty())
            {
                selectHeadFieldsBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }

            selectHeadFieldsBlock.append(
                    curSelect.GetVar(forEachLoopVar).m_Value
            );
        }

        // handle the select from block
        for(var curTableView : curSelect.m_TablesViews)
        {
            // skip old/new tables
            if(curTableView.m_Type != IFLog.Components.CompositeRule.CompRuleBlock.Select.TableView.TYPE_NORMAL) continue;

            // get the tableView template
            String selectTableViewTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_TABLEVIEW)
            );
            // set the name
            selectTableViewTemplate = selectTableViewTemplate.replace(
                    Globals.Translation.CompositeRule.V_NAME,
                    curTableView.m_Name
            );
            // set the alias
            selectTableViewTemplate = selectTableViewTemplate.replace(
                    Globals.Translation.CompositeRule.V_ALIAS,
                    curTableView.m_Alias
            );

            // add to the list of tables and views
            if(!selectFromBlock.isEmpty())
            {
                selectFromBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }
            selectFromBlock.append(selectTableViewTemplate);
        }

        // handle the select head block translation
        // get the template
        String selectHeadTemplate = m_TranslationMap.get(
                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT)
        );
        // set the fields
        selectHeadTemplate = selectHeadTemplate.replace(
                Globals.Translation.CompositeRule.V_FIELDS,
                selectHeadFieldsBlock
        );
        // add it to the select block
        selectBlock.append(selectHeadTemplate);

        // handle the select from block translation
        // get the template
        String selectFromTemplate = m_TranslationMap.get(
                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_FROM)
        );
        // set the table and views
        selectFromTemplate = selectFromTemplate.replace(
                Globals.Translation.CompositeRule.V_TABLEVIEWS,
                selectFromBlock
        );
        // add it to the select block
        selectBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
        selectBlock.append(selectFromTemplate);

        // handle the select where block translation
        if(curSelect.m_Constraint != null)
        {
            // get the template
            String selectWhereTemplate = m_TranslationMap.get(
                    Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_SELECT_WHERE)
            );
            // set the condition
            selectWhereTemplate = selectWhereTemplate.replace(
                    Globals.Translation.CompositeRule.V_CONDITION,
                    curSelect.m_Constraint
            );
            // add it to the select block
            selectBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
            selectBlock.append(selectWhereTemplate);
        }

        return selectBlock.toString();
    }

    private String GetAssignmentTranslation(String assignmentVar, String assignmentValue)
    {
        // get the template
        String assignmentTemplate = m_TranslationMap.get(
                Globals.Translation.CompositeRule.Key(Globals.Translation.Fixed.COMPOSITE_RULE_ASSIGNMENT)
        );

        // set var
        assignmentTemplate = assignmentTemplate.replace(
                Globals.Translation.CompositeRule.V_VAR,
                assignmentVar
        );
        // set value
        assignmentTemplate = assignmentTemplate.replace(
                Globals.Translation.CompositeRule.V_VALUE,
                assignmentValue
        );

        // add terminal symbol
        assignmentTemplate += m_TranslationMap.get(
                Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
        );

        return assignmentTemplate;
    }

    public List<String> m_CompositeRules;
    public List<String> m_Triggers;
}
