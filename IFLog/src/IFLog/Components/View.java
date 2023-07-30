package IFLog.Components;

import java.util.ArrayList;
import java.util.List;

public class View
{
    public String m_Name;
    // have all to be set in every selects
    public List<Field> m_Fields;
    // connected by union
    public List<Select> m_Selects;

    public View(String name)
    {
        m_Name = name;
        m_Fields = new ArrayList<>();
        m_Selects = new ArrayList<>();
    }

    public void AddField(String name, String type)
    {
        m_Fields.add(new Field(name, type));
    }

    public Field GetField(String name)
    {
        Field ret = null;
        for(var field : m_Fields)
        {
            if(field.m_Name.equals(name))
            {
                ret = field;
                break;
            }
        }

        return ret;
    }

    public void SetFieldTypeIfNull(String name, String type)
    {
        int i = 0;
        for(var field : m_Fields)
        {
            if(field.m_Name.equals(name))
            {
                if(field.m_Type == null)
                    m_Fields.set(i, new Field(field.m_Name, type));

                break;
            }

            i++;
        }
    }

    public void AddSelect(Select select)
    {
        m_Selects.add(select);
    }

    public static class Field
    {
        public Field(String name, String type)
        {
            m_Name = name;
            m_Type = type;
        }

        public String m_Name;
        // technically there's no need for it since they get derived
        // but allows easier type checking
        // also these should always be base types and not custom types
        public String m_Type;
    }

    public static class Select
    {
        // optionally, doesn't get translated
        // useful for debugging purposes
        public String m_Name;
        public List<SelectVar> m_Vars;
        // list of table/view names for FROM-clause
        public List<TableView> m_TablesViews;
        // used in WHERE-clause
        public String m_Constraint;

        public Select()
        {
            this(null);
        }

        public Select(String name)
        {
            m_Name = name;
            m_Vars = new ArrayList<>();
            m_TablesViews = new ArrayList<>();
            m_Constraint = null;
        }

        public void AddVar(String name, String type, String value)
        {
            m_Vars.add(new SelectVar(name, type, value));
        }

        public void AddVar(String name, String type, List<SelectVar.ConditionalValue> conditionalValues)
        {
            m_Vars.add(new SelectVar(name, type, conditionalValues));
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
            // for case-whens
            public List<ConditionalValue> m_ConditionalValues;

            private SelectVar(String name, String type)
            {
                m_Name = name;
                m_Type = type;
                m_Value = null;
                m_ConditionalValues = new ArrayList<>();
            }

            public SelectVar(String name, String type, String value)
            {
                this(name, type);
                m_Value = value;
            }

            public SelectVar(String name, String type, List<ConditionalValue> conditionalValues)
            {
                this(name, type);
                m_ConditionalValues = conditionalValues;
            }

            public static class ConditionalValue
            {
                // basically a constraint
                public String m_Condition;
                public String m_Value;

                public ConditionalValue(String condition, String value)
                {
                    m_Condition = condition;
                    m_Value = value;
                }
            }
        }

        public static class TableView
        {
            // name of table/view
            public String m_Name;
            // alias used in translation as a prefix for variables
            // (something like t1, t100 etc.)
            public String m_Alias;
            public List<TableViewField> m_Fields;
            public boolean m_Negated;

            public TableView(String name, String alias, boolean negated)
            {
                m_Name = name;
                m_Alias = alias;
                m_Fields = new ArrayList<>();
                m_Negated = negated;
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
}
