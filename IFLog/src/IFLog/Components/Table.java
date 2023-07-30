package IFLog.Components;

import java.util.ArrayList;
import java.util.List;

public class Table
{
    public String m_Name;
    public List<Field> m_Fields;
    public List<FkConstraint> m_FkConstraints;
    public PkConstraint m_PkConstraint;
    public List<UkConstraint> m_UkConstraints;
    public List<CheckConstraint> m_CheckConstraints;
    public List<DataInsert> m_DataInserts;

    public Table(String name)
    {
        m_Name = name;
        m_Fields = new ArrayList<>();
        m_FkConstraints = new ArrayList<>();
        m_UkConstraints = new ArrayList<>();
        m_CheckConstraints = new ArrayList<>();
        m_DataInserts = new ArrayList<>();
    }

    public void AddField(String name, String type, String default_value, boolean nullable)
    {
        m_Fields.add(new Field(name, type, default_value, nullable));
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

    public void AddFkConstraint(FkConstraint fk_constraint)
    {
        m_FkConstraints.add(fk_constraint);
    }

    public FkConstraint GetFkConstraint(String name)
    {
        FkConstraint ret = null;
        for(var fkConstraint : m_FkConstraints)
        {
            if(fkConstraint.m_Name.equals(name))
            {
                ret = fkConstraint;
                break;
            }
        }

        return ret;
    }

    public void AddPkConstraint(String name)
    {
        m_PkConstraint = new PkConstraint(name);
    }

    public PkConstraint GetPkConstraint(String name)
    {
        PkConstraint ret = null;

        if(m_PkConstraint != null && m_PkConstraint.m_Name.equals(name))
            ret = m_PkConstraint;

        return ret;
    }

    public void AddUkConstraint(UkConstraint uk_constraint)
    {
        m_UkConstraints.add(uk_constraint);
    }

    public UkConstraint GetUkConstraint(String name)
    {
        UkConstraint ret = null;
        for(var ukConstraint : m_UkConstraints)
        {
            if(ukConstraint.m_Name.equals(name))
            {
                ret = ukConstraint;
                break;
            }
        }

        return ret;
    }

    public void AddCheckConstraint(CheckConstraint check_constraint)
    {
        m_CheckConstraints.add(check_constraint);
    }

    public CheckConstraint GetCheckConstraint(String name)
    {
        CheckConstraint ret = null;
        for(var checkConstraint : m_CheckConstraints)
        {
            if(checkConstraint.m_Name.equals(name))
            {
                ret = checkConstraint;
                break;
            }
        }

        return ret;
    }

    public void AddDataInsert(DataInsert data_insert)
    {
        m_DataInserts.add(data_insert);
    }

    public static class Field
    {
        public Field(String name, String type, String default_value, boolean nullable)
        {
            m_Name = name;
            m_Type = type;
            m_DefaultValue = default_value;
            m_Nullable = nullable;
        }

        public String m_Name;
        public String m_Type;
        public String m_DefaultValue;
        // could also be handled via the default value
        // but for clarity it's better to use a flag here
        public boolean m_Nullable;
    }

    public static class FkConstraint
    {
        public FkConstraint(String name, String parent)
        {
            m_Name = name;
            m_Parent = parent;
            m_Fields = new ArrayList<>();
            m_ParentFields = new ArrayList<>();
        }

        public void AddFieldPair(String field, String parentField)
        {
            m_Fields.add(field);
            m_ParentFields.add(parentField);
        }

        public String m_Name;
        public String m_Parent;
        public List<String> m_Fields;
        public List<String> m_ParentFields;
    }

    public static class PkConstraint
    {
        public PkConstraint(String name)
        {
            m_Name = name;
            m_Fields = new ArrayList<>();
        }

        public void AddField(String field)
        {
            m_Fields.add(field);
        }

        public String m_Name;
        public List<String> m_Fields;
    }

    public static class UkConstraint
    {
        public UkConstraint(String name)
        {
            m_Name = name;
            m_Fields = new ArrayList<>();
        }

        public void AddField(String field)
        {
            m_Fields.add(field);
        }

        public String m_Name;
        public List<String> m_Fields;
    }

    public static class CheckConstraint
    {
        public CheckConstraint(String name, String body)
        {
            m_Name = name;
            m_Body = body;
        }

        public String m_Name;
        public String m_Body;
    }

    public static class DataInsert
    {
        public DataInsert()
        {
            m_Fields = new ArrayList<>();
            m_Values = new ArrayList<>();
        }

        public void AddFieldValuePair(String field, String value)
        {
            m_Fields.add(field);
            m_Values.add(value);
        }
        public List<String> m_Fields;
        public List<String> m_Values;
    }
}
