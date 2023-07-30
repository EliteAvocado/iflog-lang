package IFLog.Extractors;

import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Core.Logger;
import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetParser;
import IFLog.Components.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class View extends Base
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
     */
    public View(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                 final Map<String, String> TranslationMap, final List<Table> TableList)
    {
        this(IFLogParser, IFLogLexer, SQLTargetParser, TranslationMap, TableList, null);
    }

    /**
     *
     * Constructor if there's already an existing view list which we want to extend.
     * In any other case we create a new one.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     * @param TableList
     * @param ViewList
     */
    public View(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                 final Map<String, String> TranslationMap, final List<Table> TableList, final List<View> ViewList)
    {
        super(IFLogParser, IFLogLexer, SQLTargetParser);

        m_TranslationMap = TranslationMap;
        m_TableList = TableList;

        if(ViewList == null)
            m_ViewList = new ArrayList<>();
    }

    /**
     *
     * Access function.
     *
     * @return ArrayList
     */
    public List<IFLog.Components.View> Get() { return m_ViewList; }

    /**
     *
     * Main function.
     *
     * (1) Add fields with all properties that are relevant for translation to their respective views.
     * (2) Add current view to list of views.
     *
     */
    @Override
    public void Extract()
    {
        // TODO: add dependency graph and rearrange views if one uses another
        //  also throw an error if there's a cyclic dependency

        for(var viewCtx : m_IFLogParser.prog().view())
        {
            IFLog.Components.View curView = new IFLog.Components.View(viewCtx.name.getText());

            // add fields to view
            for(var field : viewCtx.view_field())
            {
                AddFieldToView(field, curView);
            }

            // add selects
            for(var viewRule : viewCtx.view_rule())
            {
                // can be null
                var selectName = viewRule.name;
                var select = viewRule.stat_select();

                IFLog.Components.View.Select curSelect = new IFLog.Components.View.Select(
                        (selectName != null) ? selectName.getText() : null
                );

                // add lhs
                if(viewRule.lhs != null)
                {
                    AddLhsToSelect(viewRule.lhs, curSelect, curView);
                }

                // add body
                AddBodyToSelect(select, curSelect, curView);

                // add rhs
                if(viewRule.rhs != null)
                {
                    AddRhsToSelect(viewRule.rhs, curSelect, curView);
                }

                // check that all fields in view are set in all selects
                for(var field : curView.m_Fields)
                {
                    boolean found = false;

                    for(var selectVar : curSelect.m_Vars)
                    {
                        if(selectVar.m_Name.equals(field.m_Name))
                        {
                            // check for type equality
                            // but only throw a warning since there might be exceptions like
                            // auto which should be treated the same as an int
                            // or implicit conversions between int and float etc.
                            if(!selectVar.m_Type.equals(field.m_Type))
                            {
                                Logger.LogWarning(
                                        "possible type miss-match for field " + field.m_Name + " of view " + curView.m_Name +
                                        ", expected: " + field.m_Type + ", found (in var): " + selectVar.m_Type
                                );
                            }

                            found = true;
                            break;
                        }
                    }

                    Assert.Equals(
                            found,
                            true,
                            "field " + field.m_Name + " of view " + curView.m_Name + " not set"
                    );
                }

                // add additional constraints based on tables and common vars
                // as well as for every fixed value that gets passed
                AddAdditionalConstraintsToSelect(curSelect, curView);

                // replace all flagged vars, e.g.: {x} in constraint,
                // conditional values and vars + undo flags in rest
                ReplaceVarsInSelect(curSelect, curView);

                // add select to view
                curView.AddSelect(curSelect);
            }

            // check for duplicates before adding
            Assert.Equals(
                    ContainerExists(curView.m_Name),
                    false,
                    "duplicate identifier: there's already a container with the name " + curView.m_Name
            );

            // check that there's at least one select
            Assert.Unequals(
                    curView.m_Selects.size(),
                    0,
                    "no selects for view " + curView.m_Name
            );

            // each select should contain at least one table or view
            for(var select : curView.m_Selects)
            {
                Assert.Unequals(
                        select.m_TablesViews.size(),
                        0,
                        "no table or view for select " + select.m_Name
                );
            }

            m_ViewList.add(curView);
        }

        // always reset lexer/parser at the end when used in function
        ResetIFLogParser();
    }

    /**
     *
     * Add field with all properties that are relevant for translation to the specified view.
     *
     * (1) Extract name, type property for field.
     * (2) Check if the provided type is a valid base type itself.
     * (3) Check for duplicates (based on field name) in the current table.
     * (4) Add field to the specified table.
     *
     * @param field
     * @param curView
     */
    private void AddFieldToView(IFLogParser.View_fieldContext field, IFLog.Components.View curView)
    {
        var fieldName = field.field.getText();
        // can be null
        var fieldType = field.type;

        // check if type is valid
        // this will throw an error if it can't find a basetype
        if(fieldType != null)
        {
            Assert.Check(
                    BasetypeExists(fieldType.getText()),
                    "type " + fieldType.getText() + " doesn't exist in translation target as a base type"
            );
        }

        // check for duplicates
        Assert.Equals(
                curView.GetField(fieldName),
                null,
                "field " + fieldName + " already exists in view " + curView.m_Name
        );

        // finally add the field to the current view
        curView.AddField(
                fieldName,
                (fieldType != null) ? fieldType.getText() : null
        );
    }

    private void AddLhsToSelect(IFLogParser.View_rule_lhsContext lhsCtx, IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        if(lhsCtx.view_rule_lhs() != null)
        {
            AddLhsToSelect(lhsCtx.view_rule_lhs(), curSelect, curView);
        }
        else
        {
            for(var assignmentStat : lhsCtx.elems)
            {
                AssignmentResult ar = ParseAssignmentStatement(assignmentStat, curSelect, curView.m_Name);

                curSelect.AddVar(ar.m_Name, ar.m_Type, ar.m_Value);

                // check if we are setting a view field
                // and if view field doesn't have a type yet, set it
                curView.SetFieldTypeIfNull(ar.m_Name, ar.m_Type);
            }
        }
    }

    private void AddBodyToSelect(IFLogParser.Stat_selectContext selectCtx, IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        // if it's another stat select
        if(selectCtx.stat_select() != null)
        {
            AddBodyToSelect(selectCtx.stat_select(), curSelect, curView);
        }
        else
        {
            int tableIdx = 0;
            int viewIdx = 0;
            for(var tableView : selectCtx.tables)
            {
                boolean isTable = AddTableViewToSelect(tableView, curSelect, curView, tableIdx, viewIdx);
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
                AddConstraintToSelect(selectCtx.constraint, curSelect, curView);
            }
        }
    }

    private boolean AddTableViewToSelect(IFLogParser.Stat_tableContext tableView, IFLog.Components.View.Select curSelect, IFLog.Components.View curView, int tableIdx, int viewIdx)
    {
        Assert.Check(
                tableView.name.trigger_var == null,
                "trigger vars (old/new) not allowed in view definition"
        );

        var tableViewName = tableView.name.var.getText();

        var isTable = (GetTable(tableViewName) != null);
        var isView = (GetView(tableViewName) != null);

        // check if container exists
        // no reason to check for duplicates
        Assert.Equals(
                isTable || isView,
                true,
                "no table or view with the name " + tableViewName
        );

        String tableViewAlias = isTable ? ("t" + tableIdx) : ("v" + viewIdx) ;

        IFLog.Components.View.Select.TableView curTableView = new IFLog.Components.View.Select.TableView(
                tableViewName,
                tableViewAlias,
                tableView.negated != null
        );

        AddTableViewBodyToSelect(tableView.table_data_record(), curSelect, curView, curTableView);

        curSelect.AddTableView(curTableView);

        return isTable;
    }

    private void AddTableViewBodyToSelect(
            IFLogParser.Table_data_recordContext tableViewBody, IFLog.Components.View.Select curSelect,
            IFLog.Components.View curView, IFLog.Components.View.Select.TableView curTableView)
    {
        var table = GetTable(curTableView.m_Name);
        var view = GetView(curTableView.m_Name);

        // if it's a table
        if(table != null)
        {
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
                                    + table.m_Name + " (you might add a multi-wildcard at the end)"
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

                        // check that it's not a coalesce statement
                        Assert.Check(
                                curDataInsertCtx.stat_coalesce() == null,
                                "coalesce statements are not allowed in view definition"
                        );

                        AddVarOrValueToSelect(table, field, curDataInsertCtx.stat_arithmetic(), curTableView, curSelect, curView);
                    }

                    // pointer to current field
                    curFieldIdx++;
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

                    // check that it's not a coalesce statement
                    Assert.Check(
                            curElement.stat_coalesce() == null,
                            "coalesce statements are not allowed in view definition"
                    );

                    AddVarOrValueToSelect(table, field, curElement.stat_arithmetic(), curTableView, curSelect, curView);
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
                                    + view.m_Name + " (you might add a multi-wildcard at the end)"
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

                        // check that it's not a coalesce statement
                        Assert.Check(
                                curDataInsertCtx.stat_coalesce() == null,
                                "coalesce statements are not allowed in view definition"
                        );

                        AddVarOrValueToSelect(view, field, curDataInsertCtx.stat_arithmetic(), curTableView, curSelect, curView);
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

                    // check that it's not a coalesce statement
                    Assert.Check(
                            curElement.stat_coalesce() == null,
                            "coalesce statements are not allowed in view definition"
                    );

                    AddVarOrValueToSelect(view, field, curElement.stat_arithmetic(), curTableView, curSelect, curView);
                }
            }
        }
    }

    private void AddVarOrValueToSelect(
            Table table, Table.Field tableField,
            IFLogParser.Stat_arithmeticContext arithmeticStat, IFLog.Components.View.Select.TableView curTableView,
            IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        AddVarOrValueToSelect(table, null, tableField, null, arithmeticStat, curTableView, curSelect, curView);
    }

    private void AddVarOrValueToSelect(
            IFLog.Components.View view, IFLog.Components.View.Field viewField,
            IFLogParser.Stat_arithmeticContext arithmeticStat, IFLog.Components.View.Select.TableView curTableView,
            IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        AddVarOrValueToSelect(null, view, null, viewField, arithmeticStat, curTableView, curSelect, curView);
    }

    private void AddVarOrValueToSelect(
            Table table, IFLog.Components.View view, Table.Field tableField, IFLog.Components.View.Field viewField,
            IFLogParser.Stat_arithmeticContext arithmeticStat, IFLog.Components.View.Select.TableView curTableView,
            IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
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
        ArithmeticResult ar = ParseArithmeticStatement(arithmeticStat, curSelect, curTableView.m_Name, true);
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

            curSelect.AddVar(varName, varType, varValue);
            // set field type if it's still null
            curView.SetFieldTypeIfNull(varName, varType);
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

    private void AddConstraintToSelect(IFLogParser.Table_constraint_elContext constraint, IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        String bodyAsString = ParseConstraint(constraint, curSelect, curView.m_Name);

        curSelect.AddConstraint(bodyAsString);
    }

    private void AddRhsToSelect(IFLogParser.View_rule_rhsContext rhsCtx, IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        if(rhsCtx.view_rule_rhs() != null)
        {
            AddRhsToSelect(rhsCtx.view_rule_rhs(), curSelect, curView);
        }
        else
        {
            for(var ele : rhsCtx.elems)
            {
                // if conditional statement
                if(ele.stat_case_when() != null)
                {
                    ConditionalResult cr = ParseConditionalStatement(ele.stat_case_when(), curSelect, curView.m_Name);

                    curSelect.AddVar(cr.m_Name, cr.m_Type, cr.m_ConditionalValues);

                    // check if we are setting a view field
                    // and if view field doesn't have a type yet, set it
                    curView.SetFieldTypeIfNull(cr.m_Name, cr.m_Type);
                }
                // if assignment
                else
                {
                    AssignmentResult ar = ParseAssignmentStatement(ele.stat_assignment(), curSelect, curView.m_Name);

                    curSelect.AddVar(ar.m_Name, ar.m_Type, ar.m_Value);

                    // check if we are setting a view field
                    // and if view field doesn't have a type yet, set it
                    curView.SetFieldTypeIfNull(ar.m_Name, ar.m_Type);
                }
            }
        }
    }

    private void AddAdditionalConstraintsToSelect(IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
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
                // get the template
                String lhs = m_TranslationMap.get(
                        Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_FIELD)
                );
                // set the tableview alias
                lhs = lhs.replace(Globals.Translation.View.V_ALIAS, thisTableView.m_Alias);
                // set the field name
                lhs = lhs.replace(Globals.Translation.View.V_NAME, thisField.m_Name);

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
                // if there's a var then it might be a join
                else
                {
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

                                if(!isUnequal)
                                {
                                    isUnequal = otherTableView.m_Negated;
                                }
                                else
                                {
                                    Assert.Check(
                                            !otherTableView.m_Negated,
                                            "only one table can be negated between two tables connected by an logical AND that share a variable in view " +
                                                    curView.m_Name
                                    );
                                }

                                // get the template
                                rhs = m_TranslationMap.get(
                                        Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_FIELD)
                                );
                                // set the tableview alias
                                rhs = rhs.replace(Globals.Translation.View.V_ALIAS, otherTableView.m_Alias);
                                // set the field name
                                rhs = rhs.replace(Globals.Translation.View.V_NAME, otherField.m_Name);

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
                }
            }

            Assert.Check(
                    hasSetVars || !thisTableView.m_Negated,
                    "negation only allowed for selects that have at least one set var or value in view " + curView.m_Name
            );
        }

        if(!newConstraint.isEmpty()) curSelect.AddConstraint(newConstraint.toString());
    }

    private void ReplaceVarsInSelect(IFLog.Components.View.Select curSelect, IFLog.Components.View curView)
    {
        // TODO: skip if already replaced
        //  (currently it still checks multiple times if there are multiple matches for the same var)

        // replace vars in constraint
        if(curSelect.m_Constraint != null)
        {
            String newConstraint = curSelect.m_Constraint;

            newConstraint = ParseStringAndReplaceVars(curSelect, curView.m_Name, newConstraint, true);

            // add new constraint
            curSelect.AddConstraint(newConstraint);
        }

        // replace vars in conditional statements
        for(var conditionalStatVar : curSelect.m_Vars)
        {
            // skip all non-conditional vars
            if(conditionalStatVar.m_ConditionalValues.size() < 1) continue;

            // get the template
            String caseWhenBlock = m_TranslationMap.get(
                    Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_CASE_WHEN)
            );

            StringBuilder caseWhenCases = new StringBuilder();

            for(var conditionalValue : conditionalStatVar.m_ConditionalValues)
            {
                // replace vars in value
                String newValue = conditionalValue.m_Value;
                newValue = ParseStringAndReplaceVars(curSelect, curView.m_Name, newValue, true);
                conditionalValue.m_Value = newValue;

                String caseWhenCaseBlock;

                // replace vars in condition (skip for default values)
                // create a case block
                if(conditionalValue.m_Condition != null)
                {
                    String newCondition = conditionalValue.m_Condition;
                    newCondition = ParseStringAndReplaceVars(curSelect, curView.m_Name, newCondition, true);
                    conditionalValue.m_Condition = newCondition;

                    // get the template
                    caseWhenCaseBlock = m_TranslationMap.get(
                            Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_CASE_WHEN_CASE)
                    );
                    // set the condition
                    caseWhenCaseBlock = caseWhenCaseBlock.replace(Globals.Translation.View.V_CONDITION, newCondition);
                    // set the result
                    caseWhenCaseBlock = caseWhenCaseBlock.replace(Globals.Translation.View.V_RESULT, newValue);
                }
                // create an else block
                else
                {
                    // get the template
                    caseWhenCaseBlock = m_TranslationMap.get(
                            Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_CASE_WHEN_DEFAULT)
                    );
                    // set the result
                    caseWhenCaseBlock = caseWhenCaseBlock.replace(Globals.Translation.View.V_RESULT, newValue);
                }

                // add to cases
                if(!caseWhenCases.isEmpty()) caseWhenCases.append(" ");
                caseWhenCases.append(caseWhenCaseBlock);
            }

            // add translation for conditional value for easier access later
            // and to keep all the replacement of vars inside the extractor class
            conditionalStatVar.m_Value = caseWhenBlock.replace(Globals.Translation.View.V_CASES, caseWhenCases.toString());
        }

        // replace vars in vars (also look in conditionalVars)
        for(var selectVar : curSelect.m_Vars)
        {
            // skip conditional vars
            if(selectVar.m_ConditionalValues.size() > 0) continue;

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
                        "couldn't find matching field for var " + selectVar.m_Name + " in view " + curView.m_Name
                );
            }
            // if value is non-null replace the vars in it
            else
            {
                newValue = ParseStringAndReplaceVars(curSelect, curView.m_Name, newValue, false);
            }

            selectVar.m_Value = newValue;
        }
    }

    private String ParseStringAndReplaceVars(IFLog.Components.View.Select curSelect, String tableViewName, String newStringValue, boolean fields_only)
    {
        Pattern pattern = Pattern.compile("\\{([^}]*)}");
        Matcher matcher = pattern.matcher(newStringValue);

        // extract all the variables
        while (matcher.find())
        {
            String oldVar = matcher.group();
            // remove the flags '{x}' -> 'x'
            oldVar = oldVar.substring(1, oldVar.length() - 1);

            // if var references a field
            String newVar = GetMatchingFieldForVar(curSelect, oldVar);
            // if var references a conditionalStatement
            if(!fields_only && newVar == null)
            {
                for(var conditionalVar : curSelect.m_Vars)
                {
                    if(conditionalVar.m_ConditionalValues.size() > 0 && conditionalVar.m_Name.equals(oldVar))
                    {
                        newVar = conditionalVar.m_Value;
                    }
                }
            }

            // should never happen
            Assert.Unequals(
                    newVar,
                    null,
                    "couldn't find matching field for var " + oldVar + " in view " + tableViewName
            );

            // replace var with new value
            newStringValue = newStringValue.replace(matcher.group(), newVar);
        }

        // new string shouldn't contain any matches
        // important: check before removing the flags
        matcher = pattern.matcher(newStringValue);
        Assert.Check(
                matcher.results().count() == 0,
                "cant resolve one or more variables while replacing in view " + tableViewName +
                        ", list of variables: " + GetMatchResultsAsString(matcher.reset().results().toList())
        );

        // remove flags
        newStringValue = UndoVarFlag(newStringValue);

        return newStringValue;
    }

    private String GetMatchingFieldForVar(IFLog.Components.View.Select curSelect, String selectVar)
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
                            Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_FIELD)
                    );
                    // set the tableview alias
                    ret = ret.replace(Globals.Translation.View.V_ALIAS, tableView.m_Alias);
                    // set the field name
                    ret = ret.replace(Globals.Translation.View.V_NAME, field.m_Name);

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

    private String ParseConstraint(IFLogParser.Table_constraint_elContext body, IFLog.Components.View.Select curSelect, String tableViewName)
    {
        StringBuilder bodyAsString= new StringBuilder();

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
            bodyAsString.append(ParseConstraint(bodyEles.get(0), curSelect, tableViewName));

            // if there's a logical operator parse the second element as well
            // and add its result to the body string
            // also add the operator
            var logicalOp = body.any_logical_op();
            if(logicalOp != null)
            {
                // get translation for logical operator
                bodyAsString.append(" ").append(GetLogicalOperatorTranslation(logicalOp)).append(" ");
                bodyAsString.append(ParseConstraint(bodyEles.get(1), curSelect, tableViewName));
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

                String lhs = ParseArithmeticStatement(arithmeticStats.get(0), curSelect, tableViewName, false).m_Value;
                String rhs = ParseArithmeticStatement(arithmeticStats.get(1), curSelect, tableViewName, false).m_Value;

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

                // check if type of lhs and type of every ele on rhs are valid for operation
                // String lhsBasetype = GetBasetypeForField(fieldName, curTable);
                var selectVar = curSelect.GetVar(fieldName);

                // make sure the variable is set
                Assert.Unequals(selectVar, null, "var on lhs of in-statement not set in " + tableViewName);

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

    private ArithmeticResult ParseArithmeticStatement(IFLogParser.Stat_arithmeticContext stat, IFLog.Components.View.Select curSelect, String tableViewName, boolean is_select)
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
            ArithmeticResult ar = ParseArithmeticStatement(arithmeticStats.get(0), curSelect, tableViewName, isSelect);
            lhs = ar.m_Value;
            is_var = ar.m_IsVar;

            // if there's an arithmetic operator parse the second element as well
            // and add its result to the statement string
            // also add the operator
            if(arithmeticOp != null)
            {
                ar = ParseArithmeticStatement(arithmeticStats.get(1), curSelect, tableViewName, false);
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

                // check if field type known
                var knownVar = curSelect.GetVar(fieldName);
                if(!is_select)
                {
                    Assert.Unequals(
                            knownVar,
                            null,
                            "usage of unknown variables inside arithmetic statement in " + tableViewName
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

    private AssignmentResult ParseAssignmentStatement(IFLogParser.Stat_assignmentContext stat, IFLog.Components.View.Select curSelect, String tableViewName)
    {
        AssignmentResult ret = null;

        if(stat.stat_assignment() != null)
        {
            ret = ParseAssignmentStatement(stat.stat_assignment(), curSelect, tableViewName);
        }
        else
        {
            var selectVars = stat.vars;
            // if assignment between two vars
            if(selectVars.size() > 1)
            {
                var knownVar0 = curSelect.GetVar(selectVars.get(0).getText());
                var knownVar1 = curSelect.GetVar(selectVars.get(1).getText());

                Assert.Check(
                        !(knownVar0 == null && knownVar1 == null),
                        "assignment between two unknown variables in " + tableViewName
                );

                Assert.Check(
                        !(knownVar0 != null && knownVar1 != null),
                        "assignment between two known variables in " + tableViewName
                );

                if(knownVar0 != null)
                {
                    ret = new AssignmentResult(
                            selectVars.get(1).getText(),
                            knownVar0.m_Type,
                            Globals.Translation.Fixed.VarFlag(knownVar0.m_Name)
                    );
                }
                else
                {
                    ret = new AssignmentResult(
                            selectVars.get(0).getText(),
                            knownVar1.m_Type,
                            Globals.Translation.Fixed.VarFlag(knownVar1.m_Name)
                    );
                }
            }
            // if assignment between var and value
            else
            {
                String value = ParseArithmeticStatement(stat.stat_arithmetic(), curSelect, tableViewName, false).m_Value;

                // remove stat type
                String[] valueList = value.split(Globals.Translation.Fixed.SYM_FLAG);
                value = valueList[0];
                // remove placeholders for SYM_FLAG
                value = value.replaceAll(
                        Globals.Translation.Fixed.SYM_FLAG_STR,
                        Globals.Translation.Fixed.SYM_FLAG
                );

                ret = new AssignmentResult(selectVars.get(0).getText(), valueList[1], value);
            }
        }

        return ret;
    }

    private ConditionalResult ParseConditionalStatement(IFLogParser.Stat_case_whenContext stat, IFLog.Components.View.Select curSelect, String tableViewName)
    {
        ConditionalResult ret = null;

        if(stat.stat_case_when() != null)
        {
            ret = ParseConditionalStatement(stat.stat_case_when(), curSelect, tableViewName);
        }
        else
        {
            // if condition and value
            for(var conditionalEle : stat.stat_case_when_el())
            {
                var cr = ParseConditionalElement(conditionalEle, curSelect, tableViewName);

                if(ret != null)
                {
                    // check that name and type are the same in all assignments
                    Assert.Check(
                            cr.m_Name.equals(ret.m_Name) && cr.m_Type.equals(ret.m_Type),
                            "vars have to be the same in all branches of a conditional statement " + tableViewName
                    );
                }
                else
                {
                    ret = new ConditionalResult(cr.m_Name, cr.m_Type);
                }

                ret.AddConditionalValue(cr.m_ConditionalValue);
            }

            // if default case
            if(stat.default_ele != null)
            {
                var ar = ParseAssignmentStatement(stat.default_ele, curSelect, tableViewName);

                // check that name and type are the same in all assignments
                Assert.Check(
                        ar.m_Name.equals(ret.m_Name) && ar.m_Type.equals(ret.m_Type),
                        "vars have to be the same in all branches of a conditional statement " + tableViewName
                );

                ret.AddConditionalValue(new IFLog.Components.View.Select.SelectVar.ConditionalValue(null, ar.m_Value));
            }
        }

        return ret;
    }

    private ConditionalElementResult ParseConditionalElement(IFLogParser.Stat_case_when_elContext conditionalEle, IFLog.Components.View.Select curSelect, String tableViewName)
    {
        ConditionalElementResult ret = null;

        if(conditionalEle.stat_case_when_el() != null)
        {
            ret = ParseConditionalElement(conditionalEle.stat_case_when_el(), curSelect, tableViewName);
        }
        else
        {
            var condition = ParseConstraint(conditionalEle.condition, curSelect, tableViewName);
            var ar = ParseAssignmentStatement(conditionalEle.action, curSelect, tableViewName);
            ret = new ConditionalElementResult(ar.m_Name, ar.m_Type, condition, ar.m_Value);
        }

        return ret;
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

    private static class ConditionalResult
    {
        // name of the variable
        public String m_Name;
        // type of the variable
        public String m_Type;
        public List<IFLog.Components.View.Select.SelectVar.ConditionalValue> m_ConditionalValues;

        public ConditionalResult(String name, String type)
        {
            m_Name = name;
            m_Type = type;
            m_ConditionalValues = new ArrayList<>();
        }

        public void AddConditionalValue(IFLog.Components.View.Select.SelectVar.ConditionalValue conditionalValue)
        {
            m_ConditionalValues.add(conditionalValue);
        }
    }

    private static class ConditionalElementResult
    {
        // name + type should be the same for all conditional values of the same statement
        public String m_Name;
        public String m_Type;
        public IFLog.Components.View.Select.SelectVar.ConditionalValue m_ConditionalValue;

        public ConditionalElementResult(String name, String type, String condition, String value)
        {
            m_Name = name;
            m_Type = type;
            m_ConditionalValue = new IFLog.Components.View.Select.SelectVar.ConditionalValue(condition, value);
        }
    }
}
