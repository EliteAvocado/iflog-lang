package IFLog.Components;

import java.util.ArrayList;
import java.util.List;

public class CompositeRule
{
    public String m_Name;
    // can be null if unknown (derive later)
    public String m_ReturnType;
    public boolean m_IsActionFree;
    public List<CompRuleVar> m_CompRuleVars;
    public List<GlobalLocalVar> m_GlobalVars;
    // local vars need to be tracked but assigned a unique name
    // like l0, l1 etc.
    public List<GlobalLocalVar> m_AllVars;
    public List<Trigger> m_Triggers;
    // connected by inclusive or
    public List<CompRuleBlock> m_CompRuleBlocks;
    public List<String> m_CompRuleTags;

    public CompositeRule(CompositeRule compositeRule)
    {
        m_Name = compositeRule.m_Name;
        m_ReturnType = compositeRule.m_ReturnType;
        m_IsActionFree = compositeRule.m_IsActionFree;
        m_CompRuleVars = compositeRule.m_CompRuleVars;
        m_GlobalVars = compositeRule.m_GlobalVars;
        m_AllVars = compositeRule.m_AllVars;
        m_Triggers = compositeRule.m_Triggers;
        m_CompRuleBlocks = compositeRule.m_CompRuleBlocks;
        m_CompRuleTags = compositeRule.m_CompRuleTags;
    }

    public CompositeRule(String name, String returnType)
    {
        m_Name = name;
        m_ReturnType = returnType;
        m_IsActionFree = true;
        m_CompRuleVars = new ArrayList<>();
        m_GlobalVars = new ArrayList<>();
        m_AllVars = new ArrayList<>();
        m_Triggers = new ArrayList<>();
        m_CompRuleBlocks = new ArrayList<>();
        m_CompRuleTags = new ArrayList<>();
    }

    public void UnsetActionFree()
    {
        m_IsActionFree = false;
    }

    public void AddCompRuleTag(String name)
    {
        m_CompRuleTags.add(name);
    }

    public void AddCompRuleVar(String name, String type, String default_value)
    {
        m_CompRuleVars.add(new CompRuleVar(name, type, default_value));
    }

    public CompRuleVar GetCompRuleVar(String name)
    {
        CompRuleVar ret = null;
        for(var compRuleVar : m_CompRuleVars)
        {
            if(compRuleVar.m_Name.equals(name))
            {
                ret = compRuleVar;
                break;
            }
        }

        return ret;
    }

    public void AddGlobalVar(String name, String type)
    {
        m_GlobalVars.add(new GlobalLocalVar(name, type));
    }

    public GlobalLocalVar GetGlobalVar(String name)
    {
        GlobalLocalVar ret = null;
        for(var globalVar : m_GlobalVars)
        {
            if(globalVar.m_Name.equals(name))
            {
                ret = globalVar;
                break;
            }
        }

        return ret;
    }

    public GlobalLocalVar GetAllVar(String name)
    {
        GlobalLocalVar ret = null;

        for(var globalLocalVar : m_AllVars)
        {
            if(globalLocalVar.m_Name.equals(name))
            {
                ret = globalLocalVar;
                break;
            }
        }

        return ret;
    }

    public void ApplyAllVarChanges(List<GlobalLocalVar> globalVars)
    {
        for(var globalVar : globalVars)
        {
            // only apply changes if var was accessed (in head) and is set (implied through access)
            if(!globalVar.m_IsSet || !globalVar.m_WasAccessed) continue;

            boolean found = false;

            for(var thisGlobalVar : m_AllVars)
            {
                // if it's a known global var
                if(globalVar.m_Name.equals(thisGlobalVar.m_Name))
                {
                    thisGlobalVar.m_Type = globalVar.m_Type;
                    thisGlobalVar.m_IsSet = true;
                    thisGlobalVar.m_WasAccessed = true;

                    found = true;
                    break;
                }
            }

            // if it's a new global or local var
            if(!found)
            {
                m_AllVars.add(globalVar);
            }
        }
    }

    public void AddTrigger(String name, Trigger.Table table, boolean is_before, int type)
    {
        m_Triggers.add(new Trigger(name, table, is_before, type));
    }

    // old/new allowed for update events
    // new allowed for insert events (no old)
    // old allowed for delete events (no new)
    public boolean OldAllowed()
    {
        boolean ret = true;

        for(var trigger : m_Triggers)
        {
            // old not allowed if there's at least one insert event
            if(trigger.m_Type == Trigger.TYPE_INS_EVENT)
            {
                ret = false;
                break;
            }
        }

        return ret;
    }

    public boolean NewAllowed()
    {
        boolean ret = true;

        for(var trigger : m_Triggers)
        {
            // new not allowed if there's at least one delete event
            if(trigger.m_Type == Trigger.TYPE_DEL_EVENT)
            {
                ret = false;
                break;
            }
        }

        return ret;
    }

    public void ApplyGlobalVarChanges(List<GlobalLocalVar> globalVars)
    {
        for(var globalVar : globalVars)
        {
            boolean found = false;

            for(var thisGlobalVar : m_GlobalVars)
            {
                // if it's a known global var
                if(globalVar.m_Name.equals(thisGlobalVar.m_Name))
                {
                    // only apply changes if it's set
                    if(globalVar.m_IsSet)
                    {
                        thisGlobalVar.m_Type = globalVar.m_Type;
                        thisGlobalVar.m_IsSet = true;
                    }
                    found = true;
                    break;
                }
            }

            // if it's a new global var
            if(!found)
            {
                m_GlobalVars.add(globalVar);
            }
        }
    }

    public void AddCompRuleBlock(CompRuleBlock compRuleBlock)
    {
        boolean found = false;
        // default for first if and last elif or else
        if(compRuleBlock.m_AlternativeTo != null || compRuleBlock.m_IfCondition != null)
        {
            compRuleBlock.m_IsIfEnd = true;
        }

        // if it's a nested block add it at a specific index
        if(compRuleBlock.m_AlternativeTo != null)
        {
            // if it's an else block or an if/elif block
            boolean thisIsElse = compRuleBlock.m_IfCondition == null;
            // default for else
            if(thisIsElse)
            {
                compRuleBlock.m_IsElseBegin = true;
            }

            for(int i = m_CompRuleBlocks.size() - 1; i >= 0; i--)
            {
                var otherCompRuleBlock = m_CompRuleBlocks.get(i);

                boolean ifFound = compRuleBlock.m_AlternativeTo.equals(otherCompRuleBlock.m_Name);
                boolean elifOrElseFound = compRuleBlock.m_AlternativeTo.equals(otherCompRuleBlock.m_AlternativeTo);

                if(ifFound || elifOrElseFound)
                {
                    int insertIdx = i + 1;
                    boolean otherIsElse = otherCompRuleBlock.m_IfCondition == null;

                    // else should always be after last if, elif or else
                    // if last is an if or elif then set elseBegin to true
                    // else it's false
                    // also ifEnd should always be set to true
                    if(thisIsElse)
                    {
                        // we got a new if end
                        otherCompRuleBlock.m_IsIfEnd = false;

                        // if last element in this if-elif-else cond is an else
                        if(otherIsElse)
                        {
                            // we already got a beginning for else
                            compRuleBlock.m_IsElseBegin = false;
                        }
                    }
                    // elif should always be after if or elif
                    // if last is an else then simply continue until if/elif is found
                    // and don't set ifEnd to true
                    // else ifEnd should always be set to true
                    else
                    {
                        // skip over any else
                        if(otherIsElse)
                        {
                            compRuleBlock.m_IsIfEnd = false;
                            continue;
                        }

                        // we got a new if end
                        if(compRuleBlock.m_IsIfEnd)
                        {
                            otherCompRuleBlock.m_IsIfEnd = false;
                        }
                    }

                    // it's already the last element
                    if(insertIdx >= m_CompRuleBlocks.size())
                    {
                        m_CompRuleBlocks.add(compRuleBlock);
                    }
                    else
                    {
                        m_CompRuleBlocks.add(insertIdx, compRuleBlock);
                    }

                    found = true;
                    break;
                }
            }
        }

        // if it's a normal block or we didn't find an if/else branch
        // just add it to the end
        if(!found)
        {
            m_CompRuleBlocks.add(compRuleBlock);
        }
    }

    // basically always set
    public static class CompRuleVar
    {
        public String m_Name;
        public String m_Type;
        public String m_DefaultValue;

        public CompRuleVar(String name, String type)
        {
            this(name, type, null);
        }

        public CompRuleVar(String name, String type, String default_value)
        {
            m_Name = name;
            m_Type = type;
            m_DefaultValue = default_value;
        }
    }

    public static class GlobalLocalVar
    {
        public String m_Name;
        public String m_Type;
        public boolean m_IsSet;
        // used for something like for-loop index
        public boolean m_IgnoreAccess;
        public boolean m_WasAccessed;

        public GlobalLocalVar(GlobalLocalVar globalLocalVar)
        {
            this(globalLocalVar.m_Name, globalLocalVar.m_Type, globalLocalVar.m_IsSet, globalLocalVar.m_IgnoreAccess);
            m_WasAccessed = globalLocalVar.m_WasAccessed;
        }

        public GlobalLocalVar(String name, String type)
        {
            this(name, type, false, false);
        }

        public GlobalLocalVar(String name, String type, boolean is_set, boolean ignore_access)
        {
            m_Name = name;
            m_Type = type;
            m_IsSet = is_set;
            m_IgnoreAccess = ignore_access;
            m_WasAccessed = false;
        }
    }

    public static class Trigger
    {
        final public static int TYPE_UPD_EVENT = 0;
        final public static int TYPE_INS_EVENT = 1;
        final public static int TYPE_DEL_EVENT = 2;

        public String m_Name;
        public Table m_Table;
        // if true translate as before trigger
        // else if false translate as after trigger
        public boolean m_IsBefore;
        // 0 - upd, 1 - ins, 2 - del
        public int m_Type;

        public Trigger(String name, Table table, boolean is_before, int type)
        {
            m_Name = name;
            m_Table = table;
            m_IsBefore = is_before;
            m_Type = type;
        }

        public static class Table
        {
            public String m_Name;
            // can be empty then it's a trigger across the whole table
            public List<String> m_Fields;

            public void AddField(String field)
            {
                m_Fields.add(field);
            }

            public Table(String name)
            {
                m_Name = name;
                m_Fields = new ArrayList<>();
            }
        }
    }

    public static class CompRuleBlock
    {
        // can be null or rule tag
        public String m_Name;
        // can be null or a rule tag
        public String m_AlternativeTo;
        // should only be true for last if/elif or else tag
        public boolean m_IsIfEnd;
        // should only be true for first else tag
        public boolean m_IsElseBegin;
        // global and local vars that are accessible
        public List<GlobalLocalVar> m_GlobalVars;
        // can only be non-null if it's an if, a for-loop or foreach-loop
        public List<CompRuleBlock> m_CompRuleBlocks;

        // body: select, if, for, foreach
        // only one can be unequals null/have more than 0 entries
        public List<Select> m_Selects;
        public String m_IfCondition;
        public ForLoop m_ForLoop;
        public ForEachLoop m_ForEachLoop;

        // head: assignment, compRuleCall, print, databaseAction, return
        // all can be unequal null/have more than 0 entries
        // order:
        // (1) assignments
        // (2) built-in functions (like print)
        // (3) ordered actions (dbActions + compRuleCall in order of declaration)
        // (4) return

        public List<OrderedAction> m_OrderedActions;
        public String m_ReturnValue;

        public CompRuleBlock(CompRuleBlock compRuleBlock)
        {
            m_Name = compRuleBlock.m_Name;
            m_AlternativeTo = compRuleBlock.m_AlternativeTo;
            m_IsIfEnd = compRuleBlock.m_IsIfEnd;
            m_IsElseBegin = compRuleBlock.m_IsElseBegin;

            m_GlobalVars = compRuleBlock.m_GlobalVars;
            m_CompRuleBlocks = compRuleBlock.m_CompRuleBlocks;

            m_Selects = compRuleBlock.m_Selects;
            m_IfCondition = compRuleBlock.m_IfCondition;
            m_ForLoop = compRuleBlock.m_ForLoop;
            m_ForEachLoop = compRuleBlock.m_ForEachLoop;

            m_OrderedActions = compRuleBlock.m_OrderedActions;
            m_ReturnValue = compRuleBlock.m_ReturnValue;
        }

        public CompRuleBlock(String name, String alternative_to)
        {
            m_Name = name;
            m_AlternativeTo = alternative_to;
            m_IsIfEnd = false;
            m_IsElseBegin = false;

            m_GlobalVars = new ArrayList<>();
            m_CompRuleBlocks = new ArrayList<>();

            m_Selects = new ArrayList<>();
            m_IfCondition = null;
            m_ForLoop = null;
            m_ForEachLoop = null;

            m_OrderedActions = new ArrayList<>();
            m_ReturnValue = null;
        }

        public void AddReturn(String returnValue)
        {
            m_ReturnValue = returnValue;
        }

        public void AddAssignment(String name, String type, String value)
        {
            m_OrderedActions.add(
                    new OrderedAction(
                            new Select.SelectVar(name, type, value),
                            null, null, null)
            );
        }

        public void AddPrintStat(String printStat)
        {
            m_OrderedActions.add(new OrderedAction(null, printStat, null, null));
        }

        public void AddCompRuleCall(String compRuleCall)
        {
            m_OrderedActions.add(new OrderedAction(null, null, compRuleCall, null));
        }

        public void AddDatabaseAction(OrderedAction.DatabaseAction databaseAction)
        {
            m_OrderedActions.add(new OrderedAction(null, null, null, databaseAction));
        }

        public void AddGlobalVar(GlobalLocalVar globalVar)
        {
            m_GlobalVars.add(globalVar);
        }

        public void AddMultipleGlobalVars(List<GlobalLocalVar> globalVars)
        {
            // m_GlobalVars.addAll(globalVars);
            for(var globalVar : globalVars)
            {
                m_GlobalVars.add(new GlobalLocalVar(globalVar));
            }
        }

        public GlobalLocalVar GetGlobalVar(String name)
        {
            GlobalLocalVar ret = null;

            for(var globalVar : m_GlobalVars)
            {
                if(globalVar.m_Name.equals(name))
                {
                    ret = globalVar;
                    break;
                }
            }

            return ret;
        }

        public GlobalLocalVar AccessGlobalVar(String name)
        {
            GlobalLocalVar ret = null;

            for(var globalVar : m_GlobalVars)
            {
                if(globalVar.m_Name.equals(name))
                {
                    ret = globalVar;

                    if(!globalVar.m_IgnoreAccess && globalVar.m_IsSet)
                    {
                        globalVar.m_WasAccessed = true;
                    }
                    break;
                }
            }

            return ret;
        }

        public void SetGlobalVar(String name)
        {
            SetGlobalVar(name, null);
        }

        public void SetGlobalVar(String name, String type)
        {
            for(var globalLocalVar : m_GlobalVars)
            {
                if(globalLocalVar.m_Name.equals(name))
                {
                    // if the type should be derived
                    if(type != null && globalLocalVar.m_Type == null)
                    {
                        globalLocalVar.m_Type = type;
                    }

                    globalLocalVar.m_IsSet = true;
                    break;
                }
            }
        }

        public void ApplyGlobalVarChanges(List<GlobalLocalVar> globalVars)
        {
            for(var globalVar : globalVars)
            {
                boolean found = false;

                for(var thisGlobalVar : m_GlobalVars)
                {
                    // if it's a known global var
                    if(globalVar.m_Name.equals(thisGlobalVar.m_Name))
                    {
                        // only apply changes if it's set
                        if(globalVar.m_IsSet)
                        {
                            thisGlobalVar.m_Type = globalVar.m_Type;
                            thisGlobalVar.m_IsSet = true;
                        }

                        found = true;
                        break;
                    }
                }

                // if it's a new global var
                if(!found)
                {
                    m_GlobalVars.add(globalVar);
                }
            }
        }

        public void AddCompRuleBlock(CompRuleBlock compRuleBlock)
        {
            boolean found = false;
            // default for first if and last elif or else
            if(compRuleBlock.m_AlternativeTo != null || compRuleBlock.m_IfCondition != null)
            {
                compRuleBlock.m_IsIfEnd = true;
            }

            // if it's a nested block add it at a specific index
            if(compRuleBlock.m_AlternativeTo != null)
            {
                // if it's an else block or an if/elif block
                boolean thisIsElse = compRuleBlock.m_IfCondition == null;
                // default for else
                if(thisIsElse)
                {
                    compRuleBlock.m_IsElseBegin = true;
                }

                for(int i = m_CompRuleBlocks.size() - 1; i >= 0; i--)
                {
                    var otherCompRuleBlock = m_CompRuleBlocks.get(i);

                    boolean ifFound = compRuleBlock.m_AlternativeTo.equals(otherCompRuleBlock.m_Name);
                    boolean elifOrElseFound = compRuleBlock.m_AlternativeTo.equals(otherCompRuleBlock.m_AlternativeTo);

                    if(ifFound || elifOrElseFound)
                    {
                        int insertIdx = i + 1;
                        boolean otherIsElse = otherCompRuleBlock.m_IfCondition == null;

                        // else should always be after last if, elif or else
                        // if last is an if or elif then set elseBegin to true
                        // else it's false
                        // also ifEnd should always be set to true
                        if(thisIsElse)
                        {
                            // we got a new if end
                            otherCompRuleBlock.m_IsIfEnd = false;

                            // if last element in this if-elif-else cond is an else
                            if(otherIsElse)
                            {
                                // we already got a beginning for else
                                compRuleBlock.m_IsElseBegin = false;
                            }
                        }
                        // elif should always be after if or elif
                        // if last is an else then simply continue until if/elif is found
                        // and don't set ifEnd to true
                        // else ifEnd should always be set to true
                        else
                        {
                            // skip over any else
                            if(otherIsElse)
                            {
                                compRuleBlock.m_IsIfEnd = false;
                                continue;
                            }

                            // we got a new if end
                            if(compRuleBlock.m_IsIfEnd)
                            {
                                otherCompRuleBlock.m_IsIfEnd = false;
                            }
                        }

                        // it's already the last element
                        if(insertIdx >= m_CompRuleBlocks.size())
                        {
                            m_CompRuleBlocks.add(compRuleBlock);
                        }
                        else
                        {
                            m_CompRuleBlocks.add(insertIdx, compRuleBlock);
                        }

                        found = true;
                        break;
                    }
                }
            }

            // if it's a normal block or we didn't find an if/else branch
            // just add it to the end
            if(!found)
            {
                m_CompRuleBlocks.add(compRuleBlock);
            }
        }

        public void AddSelect(Select select)
        {
            m_Selects.add(select);
        }

        public void AddForEachLoop(ForEachLoop forEachLoop)
        {
            m_ForEachLoop = forEachLoop;
        }

        public void AddForLoop(String fromVarValue, String toVarValue, String idxVar)
        {
            m_ForLoop = new ForLoop(fromVarValue, toVarValue, idxVar);
        }

        public void AddIfCondition(String ifCondition)
        {
            m_IfCondition = ifCondition;
        }

        public static class Select
        {
            public List<SelectVar> m_Vars;
            // list of table/view names for FROM-clause
            public List<TableView> m_TablesViews;
            // used in WHERE-clause
            public String m_Constraint;

            public Select(Select select)
            {
                m_Vars = select.m_Vars;
                m_TablesViews = select.m_TablesViews;
                m_Constraint = select.m_Constraint;
            }

            public Select()
            {
                m_Vars = new ArrayList<>();
                m_TablesViews = new ArrayList<>();
                m_Constraint = null;
            }

            public void AddVar(String name, String type, String value)
            {
                m_Vars.add(new SelectVar(name, type, value));
            }

            public void AddVar(String name, String type)
            {
                m_Vars.add(new SelectVar(name, type));
            }

            public SelectVar GetVar(String name)
            {
                SelectVar ret = null;

                for(var selectVar : m_Vars)
                {
                    if(selectVar.m_Name.equals(name))
                    {
                        ret = selectVar;
                        break;
                    }
                }

                return ret;
            }

            public void AddTableView(TableView tableView)
            {
                m_TablesViews.add(tableView);
            }

            public void AddConstraint(String constraint)
            {
                m_Constraint = constraint;
            }

            public static class SelectVar
            {
                // used in IFLog
                public String m_Name;
                public String m_Type;
                public String m_Value;

                private SelectVar(String name, String type)
                {
                    m_Name = name;
                    m_Type = type;
                    m_Value = null;
                }

                public SelectVar(String name, String type, String value)
                {
                    this(name, type);
                    m_Value = value;
                }
            }

            public static class TableView
            {
                public final static int TYPE_NORMAL = 0;
                public final static int TYPE_OLD = 1;
                public final static int TYPE_NEW = 2;

                // name of table/view
                public String m_Name;
                // alias used in translation as a prefix for variables
                // (something like t1, t100 etc.)
                public String m_Alias;
                public List<TableViewField> m_Fields;
                public boolean m_Negated;
                // 0 - normal, 1 - old, 2 - new
                public int m_Type;

                public TableView(String name, String alias, boolean negated)
                {
                    this(name, alias, negated, TYPE_NORMAL);
                }

                public TableView(String name, String alias, boolean negated, int type)
                {
                    m_Name = name;
                    m_Alias = alias;
                    m_Fields = new ArrayList<>();
                    m_Negated = negated;
                    m_Type = type;
                }

                public void AddField(String name, String selectVar, String value)
                {
                    m_Fields.add(new TableViewField(name, selectVar, value));
                }

                public static class TableViewField
                {
                    public TableViewField(String name, String selectVar, String value)
                    {
                        m_Name = name;
                        m_Var = selectVar;
                        m_Value = value;
                    }

                    // name in table or view
                    public String m_Name;
                    // optional, for non-fixed values
                    // used in iflog, before translation replace vars in values[]
                    // and constraints[] named alias with tableAlias + fieldName,
                    // also add to constraint one for any occurrences where alias between two tables is the same
                    public String m_Var;
                    // optional, for fixed values
                    public String m_Value;
                }
            }
        }

        public static class ForLoop
        {
            public String m_FromVarValue;
            public String m_ToVarValue;
            // unset local variable
            public String m_IdxVar;

            public ForLoop(String fromVarValue, String toVarValue, String idxVar)
            {
                m_FromVarValue = fromVarValue;
                m_ToVarValue = toVarValue;

                m_IdxVar = idxVar;
            }
        }

        public static class ForEachLoop
        {
            public Select m_Select;
            // accessible in loop
            // should be defined in multi()
            // should be unset local vars
            public List<String> m_Vars;

            public ForEachLoop(Select select)
            {
                m_Select = select;
                m_Vars = new ArrayList<>();
            }

            public void AddVar(String name)
            {
                m_Vars.add(name);
            }

            public String GetVar(String name)
            {
                String ret = null;

                for(var forEachLoopVar : m_Vars)
                {
                    if(forEachLoopVar.equals(name))
                    {
                        ret = forEachLoopVar;
                        break;
                    }
                }

                return ret;
            }
        }

        public static class OrderedAction
        {
            public Select.SelectVar m_Assignment;
            public String m_PrintStat;
            public String m_CompRuleCall;
            public DatabaseAction m_DatabaseAction;

            private OrderedAction(Select.SelectVar assignment, String printStat, String compRuleCall, DatabaseAction databaseAction)
            {
                m_Assignment = assignment;
                m_PrintStat = printStat;
                m_CompRuleCall = compRuleCall;
                m_DatabaseAction = databaseAction;
            }

            public static class DatabaseAction
            {
                // ins: table, fields, values

                // del: table
                // del: table, constraint

                // upd: table, fields, values, constraint

                public static class Insert extends DatabaseAction
                {
                    public String m_Table;
                    public List<FieldValuePair> m_FieldValuePairs;

                    public Insert(String table)
                    {
                        m_Table = table;
                        m_FieldValuePairs = new ArrayList<>();
                    }

                    public void AddFieldValuePair(String field, String value)
                    {
                        m_FieldValuePairs.add(new FieldValuePair(field, value));
                    }
                }

                public static class Update extends DatabaseAction
                {
                    public String m_Table;
                    public String m_Constraint;
                    public List<FieldValuePair> m_FieldValuePairs;

                    public Update(String table, String constraint)
                    {
                        m_Table = table;
                        m_Constraint = constraint;
                        m_FieldValuePairs = new ArrayList<>();
                    }

                    public void AddFieldValuePair(String field, String value)
                    {
                        m_FieldValuePairs.add(new FieldValuePair(field, value));
                    }
                }

                public static class Delete extends DatabaseAction
                {
                    public String m_Table;
                    public String m_Constraint;

                    public Delete(String table, String constraint)
                    {
                        m_Table = table;
                        m_Constraint = constraint;
                    }
                }

                public static class FieldValuePair
                {
                    public String m_Field;
                    public String m_Value;

                    public FieldValuePair(String field, String value)
                    {
                        m_Field = field;
                        m_Value = value;
                    }
                }

            }
        }
    }
}
