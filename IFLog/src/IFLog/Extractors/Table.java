package IFLog.Extractors;

import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Core.Logger;
import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table extends Base
{
    /**
     *
     * Constructor.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     */
    public Table(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                 final Map<String, String> TranslationMap)
    {
        this(IFLogParser, IFLogLexer, SQLTargetParser, TranslationMap, null);
    }

    /**
     *
     * Constructor if there's already an existing table list which we want to extend.
     * In any other case we create a new one.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param TranslationMap
     * @param TableList
     */
    public Table(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                 final Map<String, String> TranslationMap, final List<IFLog.Components.Table> TableList)
    {
        super(IFLogParser, IFLogLexer, SQLTargetParser);

        m_TranslationMap = TranslationMap;

        if(TableList == null)
            m_TableList = new ArrayList<>();
    }

    /**
     *
     * Access function.
     *
     * @return ArrayList
     */
    public List<IFLog.Components.Table> Get() { return m_TableList; }

    /**
     *
     * Main function.
     *
     * (1) Add fields with all properties that are relevant for translation to their respective tables.
     * (2) Add foreign key constraints to their respective tables (for fields with extended field syntax).
     * (3) Add primary key constraints to their respective tables.
     * (4) Add unique key constraints to their respective tables.
     * (5) Check for duplicates (based on table name) in tables, views and composite rules.
     * (6) Add current table to list of tables.
     * (7) Add missing types to fields that were added in step (2).
     * (8) Add check constraints to their respective tables.
     * (9) Add data inserts to their respective tables.
     *
     */
    @Override
    public void Extract()
    {
        for(var tableCtx : m_IFLogParser.prog().table() )
        {
            IFLog.Components.Table curTable = new IFLog.Components.Table(tableCtx.name.getText());

            // add fields and foreign key constraints to table
            for(var field : tableCtx.table_field())
            {
                var fieldNormal = field.table_arg();
                var fieldExtended = field.table_arg_extended();

                // normal field definition with name, type and optional default value
                if(fieldNormal != null)
                {
                    AddFieldToTable(fieldNormal, curTable);
                }
                // extended field definition that also creates a foreign key constraint
                // while adding the approriate field
                else
                {
                    AddFieldWithFkConstraintToTable(fieldExtended, curTable);
                }
            }

            // add primary key constraints to table
            if(tableCtx.table_pk().size() > 0)
            {
                Assert.Equals(
                        tableCtx.table_pk().size(),
                        1,
                        "there can be at most one primary key definition"
                );

                var pkConstraint = tableCtx.table_pk().get(0).table_pk_uk_el();

                AddPkConstraintToTable(pkConstraint, curTable);
            }

            // add unique key constraints to table
            if(tableCtx.table_uks().size() > 0)
            {
                Assert.Equals(
                        tableCtx.table_uks().size(),
                        1,
                        "there can be at most one unique keys definition"
                );

                for(var ukConstraint : tableCtx.table_uks().get(0).table_pk_uk_el())
                {
                    AddUkConstraintToTable(ukConstraint, curTable);
                }
            }

            // check for duplicates before adding
            Assert.Equals(
                    ContainerExists(curTable.m_Name),
                    false,
                    "duplicate identifier: there's already a container with the name " + curTable.m_Name
            );

            m_TableList.add(curTable);
        }

        // add the missing types for the tables with foreign key constraints
        // by cycling through all tables and their respective foreign key constraints
        // then cycle through the fields of the parent table
        // and match the type of the field in this table with the one in the parent table
        for(var curTable : m_TableList)
        {
            for(var fkConstraint : curTable.m_FkConstraints)
            {
                AddTypeForFkConstraintFields(fkConstraint, curTable);
            }
        }

        // has to be reset between checks
        ResetIFLogParser();

        // do check constraints and data inserts after adding the missing types
        // to ensure that all occurrences of string concatenation can be found
        for(var tableCtx : m_IFLogParser.prog().table() )
        {
            IFLog.Components.Table curTable = null;
            for(var table : m_TableList)
            {
                if(tableCtx.name.getText().equals(table.m_Name))
                {
                    curTable = table;
                }
            }

            Assert.Unequals(curTable, null, "table not found");

            // add check constraints to table
            int checkConstraintIdx = 1;
            for (var checkConstraint : tableCtx.table_constraint())
            {
                checkConstraintIdx = AddCheckConstraintToTable(checkConstraint, checkConstraintIdx, curTable);
            }

            // add data inserts to table
            if(tableCtx.table_data().size() > 0)
            {
                Assert.Equals(
                        tableCtx.table_data().size(),
                        1,
                        "there can be at most one data definition"
                );

                for(var dataInsert : tableCtx.table_data().get(0).table_data_record())
                {
                    AddDataInsertToTable(dataInsert, curTable);
                }
            }
        }

        // always reset lexer/parser at the end when used in function
        ResetIFLogParser();
    }

    /**
     *
     * Add field with all properties that are relevant for translation to the specified table.
     *
     * (1) Extract name, default value and nullable property for field.
     * (2) Check if the provided type is a derivative of a base type or a valid base type itself.
     * (3) Check if field type and default value type match. (else throw a warning)
     * (4) Check for duplicates (based on field name) in the current table.
     * (5) Add field to the specified table.
     *
     * @param fieldNormal
     * @param curTable
     */
    private void AddFieldToTable(IFLogParser.Table_argContext fieldNormal, IFLog.Components.Table curTable)
    {
        String fieldDefaultValue;
        boolean fieldNullable;

        // check if type is valid
        // this will throw an error if it can't find a base type or user defined type with that name
        var fieldBaseType = GetBasetype(fieldNormal.type.getText());

        // set the default value for the field
        if(fieldNormal.default_value != null)
        {
            // by default the value gets simply assigned and nullable gets set to false
            if(fieldNormal.default_value.V_NULL() == null)
            {
                fieldDefaultValue = fieldNormal.default_value.getText();
                var fieldDefaultValueBaseType = GetBasetypeForValue(fieldNormal.default_value, curTable.m_Name);

                // check if valid default value for type of field
                // only throw a warning since there might be implicit conversions in ifl-target
                if(!fieldBaseType.equals(fieldDefaultValueBaseType))
                {
                    Logger.LogWarning(
                            fieldDefaultValue + " may not be valid default value for field " +
                                    fieldNormal.field.getText() + " in table " + curTable.m_Name +
                                    ", expected: " + fieldBaseType + ", found (as default value): " + fieldDefaultValueBaseType
                    );
                }

                fieldNullable = false;
            }
            // if null is the default value the field gets flagged as nullable
            // and set the default value gets set to null
            else
            {
                fieldDefaultValue = null;
                fieldNullable = true;
            }
        }
        // if there is no default value defined
        // then the field gets flagged as not nullable and the default value gets set to null
        else
        {
            fieldDefaultValue = null;
            fieldNullable = false;
        }

        // check for duplicates
        Assert.Equals(
                curTable.GetField(fieldNormal.field.getText()),
                null,
                "field " + fieldNormal.field.getText() + " already exists in table " + curTable.m_Name
        );

        // finally add the field to the current table
        curTable.AddField(
                fieldNormal.field.getText(),
                fieldNormal.type.getText(),
                (fieldDefaultValue != null) ? GetValueTranslation(fieldNormal.default_value) : null,
                fieldNullable
        );
    }

    /**
     *
     * Add foreign key constraint to the specified tables (for fields with extended field syntax).
     *
     * (1) Check for aliases for fields of fk constraint.
     * (2) Check for default values for fields of fk constraint.
     * (3) Set field name (alias if defined) else it's based on the table name.
     * (4) Set default value if one is defined and set the nullable property.
     * (5) Construct name for constraint if none is defined.
     * (6) Check for duplicates of the field inside the table.
     * (7) Add field to the table.
     * (8) Check for duplicates of the field inside the foreign key constraint.
     * (9) Add field to the foreign key constraint.
     * (10) Check for duplicates of the constraint (based on name) across all fk, pk, uk and check constraints.
     * (11) Add foreign key constraint to the table
     *
     * @param fieldExtended
     * @param curTable
     */
    private void AddFieldWithFkConstraintToTable(IFLogParser.Table_arg_extendedContext fieldExtended, IFLog.Components.Table curTable)
    {
        Assert.Unequals(fieldExtended, null, "null field (should never happen)");

        var ctxFields = fieldExtended.fields;
        // if _ then skip this field
        var ctxAliases = fieldExtended.aliases;
        // if _ then skip this field
        var ctxDefaultValues = fieldExtended.default_values;
        var ctxConstraint = fieldExtended.constraint;

        IFLog.Components.Table.FkConstraint tableFkConstraint = new IFLog.Components.Table.FkConstraint("",fieldExtended.parent.getText());

        StringBuilder fkConstraintName =
                new StringBuilder((ctxConstraint != null)
                        ? ctxConstraint.getText()
                        : (curTable.m_Name + "_fk"));

        // skip aliases if there are none defined
        // else we need to define as many as there are fields
        // if we only want to define one for some of them
        // we can make use of the wildcard symbol _
        boolean skipAliases;
        if(ctxAliases.size() > 0)
        {
            Assert.Equals(
                    ctxFields.size(),
                    ctxAliases.size(),
                    "number of aliases needs to be 0 or match number of fields"
            );

            skipAliases = false;
        }
        else
        {
            // constraint name requires alias definition
            // else it's not unambiguous
            Assert.Equals(ctxConstraint, null, "missing aliases");

            skipAliases = true;
        }

        // skip default values if there are none defined
        // else we need to define as many as there are fields
        // if we only want to define one for some of them
        // we can make use of the wildcard symbol _
        boolean skipDefaultValues;
        if(ctxDefaultValues.size() > 0)
        {
            Assert.Equals(
                    ctxFields.size(),
                    ctxDefaultValues.size(),
                    "number of default values needs to be 0 or match number of fields"
            );

            skipDefaultValues = false;
        }
        else
        {
            skipDefaultValues = true;
        }

        for(int i = 0; i < ctxFields.size(); ++i)
        {
            // this tables field
            String fieldName;
            // referenced tables field
            String fkParentFieldName = ctxFields.get(i).getText();
            String fieldDefaultValue;
            boolean fieldNullable;

            // the field name of this table should be the alias
            // if none are defined or there is a wildcard
            // for this respective field we use the name of the parent field
            if(!skipAliases && ctxAliases.get(i).SYM_WILDCARD_SINGLE() == null)
            {
                fieldName = ctxAliases.get(i).getText();
            }
            else
            {
                fieldName = fkParentFieldName;
            }

            // set the default value fo the field
            if(!skipDefaultValues && ctxDefaultValues.get(i).SYM_WILDCARD_SINGLE() == null)
            {
                // by default the value gets simply assigned and nullable gets set to false
                if(ctxDefaultValues.get(i).any_value().V_NULL() == null)
                {
                    fieldDefaultValue = GetValueTranslation(ctxDefaultValues.get(i).any_value());
                    fieldNullable = false;
                }
                // if null is the default value the field gets flagged as nullable
                // and set the default value gets set to null
                else
                {
                    fieldDefaultValue = null;
                    fieldNullable = true;
                }
            }
            // if there is no default value defined or there is a wildcard
            // then the field gets flagged as not nullable and the default value gets set to null
            else
            {
                fieldDefaultValue = null;
                fieldNullable = false;
            }

            // construct the name of the foreign key constraint
            // using the table name and the used fields
            // only if there isn't one defined yet
            if(ctxConstraint == null)
                fkConstraintName.append("_").append(fieldName);

            // check for duplicates
            Assert.Equals(
                    curTable.GetField(fieldName),
                    null,
                    "field " + fieldName + " already exists in table " + curTable.m_Name
            );

            // finally add the field to the current table
            curTable.AddField(fieldName, null, fieldDefaultValue, fieldNullable);

            // check for duplicates
            Assert.Equals(
                    tableFkConstraint.m_ParentFields.contains(fkParentFieldName),
                    false,
                    "field " + fkParentFieldName + " already defined as parent field for fk constraint "
                            + tableFkConstraint.m_Name + " in table " + curTable.m_Name
            );

            // add the field of this table and the parent table to the foreign key constraint
            tableFkConstraint.AddFieldPair(fieldName, fkParentFieldName);
        }

        // set the name of the foreign key constraint
        tableFkConstraint.m_Name = fkConstraintName.toString();

        // check for duplicates
        Assert.Equals(
                ConstraintExists(tableFkConstraint.m_Name, curTable),
                false,
                "constraint " + tableFkConstraint.m_Name + " already exists in table " + curTable.m_Name
        );

        // finally add foreign key constraint to the current table
        curTable.AddFkConstraint(tableFkConstraint);
    }

    /**
     *
     * Add primary key constraint to the specified table.
     *
     * (1) Set name for constraint (if not defined construct it based on table name).
     * (2) Check for duplicates of the constraint (based on name) across all fk, pk, uk and check constraints.
     * (3) Add primary key constraint to the specified table.
     * (4) Check for duplicates of field inside the pk constraint.
     * (5) Check if field is part of the table.
     * (6) Add field to the primary key constraint.
     *
     * @param pkConstraint
     * @param curTable
     */
    private void AddPkConstraintToTable(IFLogParser.Table_pk_uk_elContext pkConstraint, IFLog.Components.Table curTable)
    {
        // if name isn't set create one based on table name
        // and "_pk" as suffix
        String constraintName = (pkConstraint.constraint != null)
                ? pkConstraint.constraint.getText()
                : curTable.m_Name + "_pk" ;

        // check for duplicates
        Assert.Equals(
                ConstraintExists(constraintName, curTable),
                false,
                "constraint " + constraintName + " already exists in table " + curTable.m_Name
        );

        curTable.AddPkConstraint(constraintName);

        // add all fields to constraint
        for(var field : pkConstraint.fields)
        {
            // check for duplicates
            Assert.Equals(
                    curTable.m_PkConstraint.m_Fields.contains(field.getText()),
                    false,
                    "field " + field.getText() + " already defined for pk constraint "
                            + curTable.m_PkConstraint.m_Name + " in table " + curTable.m_Name
            );

            // check if field is part of table
            Assert.Unequals(
                    curTable.GetField(field.getText()),
                    null,
                    "field " + field.getText() + " doesn't exist in table " + curTable.m_Name
            );

            curTable.m_PkConstraint.AddField(field.getText());
        }
    }

    /**
     *
     * Add unique key constraint to the specified tables.
     *
     * (1) Set name for constraint (if not defined construct it based on table name and specified fields).
     * (2) Check for duplicates of field inside the uk constraint.
     * (3) Check if field is part of the table.
     * (4) Add field to the unique key constraint.
     * (5) Check for duplicates of the constraint (based on name) across all fk, pk, uk and check constraints.
     * (6) Add unique key constraint to the specified table.
     *
     * @param ukConstraint
     * @param curTable
     */
    private void AddUkConstraintToTable(IFLogParser.Table_pk_uk_elContext ukConstraint, IFLog.Components.Table curTable)
    {
        // if name isn't set create one based on table name
        // followed by "_uk" and then all the fields
        StringBuilder ukConstraintName =
                new StringBuilder((ukConstraint.constraint != null)
                        ? ukConstraint.constraint.getText()
                        : (curTable.m_Name + "_uk"));

        IFLog.Components.Table.UkConstraint curUkConstraint = new IFLog.Components.Table.UkConstraint("");

        for(var field : ukConstraint.fields)
        {
            // check for duplicates
            Assert.Equals(
                    curUkConstraint.m_Fields.contains(field.getText()),
                    false,
                    "field " + field.getText() + " already defined as field for uk constraint "
                            + curUkConstraint.m_Name + " in table " + curTable.m_Name
            );

            // check if field is part of table
            Assert.Unequals(
                    curTable.GetField(field.getText()),
                    null,
                    "field " + field.getText() + " doesn't exist in table " + curTable.m_Name
            );

            curUkConstraint.AddField(field.getText());

            // construct the name of the unique key constraint
            // only if there isn't one defined yet
            if(ukConstraint.constraint == null)
                ukConstraintName.append("_").append(field.getText());
        }

        curUkConstraint.m_Name = ukConstraintName.toString();

        // check for duplicates
        Assert.Equals(
                ConstraintExists(curUkConstraint.m_Name, curTable),
                false,
                "constraint " + curUkConstraint.m_Name + " already exists in table " + curTable.m_Name
        );

        curTable.AddUkConstraint(curUkConstraint);
    }

    /**
     *
     * Add missing types to fields that were added based on foreign key constraints.
     *
     * @param fkConstraint
     * @param curTable
     */
    private void AddTypeForFkConstraintFields(IFLog.Components.Table.FkConstraint fkConstraint, IFLog.Components.Table curTable)
    {
        for(int i = 0; i < fkConstraint.m_ParentFields.size(); ++i)
        {
            String type = GetTypeForField(fkConstraint.m_ParentFields.get(i), fkConstraint.m_Parent);

            for(var field : curTable.m_Fields)
            {
                if(field.m_Name.equals(fkConstraint.m_Fields.get(i)))
                {
                    field.m_Type = type;
                    break;
                }
            }
        }
    }

    /**
     *
     * Add check constraint to the specified table and return an index for name construction.
     *
     * (1) Check if constraint name defined.
     * (2) Set constraint name (if not defined construct based on table name and index).
     * (3) Parse and translate constraint body.
     * (4) Create Constraint.
     * (5) Check for duplicates of the constraint (based on name) across all fk, pk, uk and check constraints.
     * (6) Add check key constraint to the specified table.
     *
     * @param checkConstraint
     * @param checkConstraintIdx
     * @param curTable
     * @return int
     */
    private int AddCheckConstraintToTable(IFLogParser.Table_constraintContext checkConstraint, int checkConstraintIdx, IFLog.Components.Table curTable)
    {
        String checkConstraintName;

        // if there's a constraint name defined use it
        if(checkConstraint.constraint != null)
        {
            checkConstraintName = checkConstraint.constraint.getText();
        }
        // if there's no name defined build it using the name of the current table
        // and the suffix _check_ with an additional index
        else
        {
            checkConstraintName = curTable.m_Name + "_check_" + checkConstraintIdx;
            checkConstraintIdx++;
        }

        String bodyAsString = ParseConstraint(checkConstraint.body, curTable);

        // create the constraint
        IFLog.Components.Table.CheckConstraint curCheckConstraint = new IFLog.Components.Table.CheckConstraint(
                checkConstraintName,
                bodyAsString
        );

        // check for duplicates
        Assert.Equals(
                ConstraintExists(curCheckConstraint.m_Name, curTable),
                false,
                "constraint " + curCheckConstraint.m_Name + " already exists in table " + curTable.m_Name
        );

        curTable.AddCheckConstraint(curCheckConstraint);

        return checkConstraintIdx;
    }

    /**
     *
     * Add record (data insert) to the specified table.
     *
     * (1) Create helper map to keep track of which fields are allowed to have wildcards values
     *     (field has default value, is nullable or has type auto).
     * (2) Check if record only contains values (identification by position).
     *  (2.1) Check that number of values matches number of fields or contains a multi-wildcard.
     *  (2.2) Check if value is not a wildcard (else skip it).
     *  (2.3) Get field from specified table.
     *  (2.4) Check that field isn't of type auto and while having a non-wildcard value.
     *  (2.5) Parse the body as an arithmetic statement and parse it into a string.
     *  (2.6) Check if type of element and field are valid for the specified table.
     *  (2.7) Remove type tags.
     *  (2.8) Remove placeholders for special symbol (was added to avoid collisions).
     *  (2.9) Add field-value pair to the current data insert.
     *  (2.10) Remove field from helper map (only if it's not a wildcard).
     *  (2.11) Check that helper map only contains fields that are allowed to have wildcards values.
     * (3) Check if record contains fields and associated values (identification by name).
     *  (3.1) Check that number of values matches number of fields (less or equal).
     *  (3.2) Check if field exists in specified table (based on field name).
     *  (3.3) Get field from specified table.
     *  (3.4) Check that field isn't of type auto and while having a non-wildcard value.
     *  (3.5) Check for duplicates in data insert.
     *  (3.6) Parse the body as an arithmetic statement and parse it into a string.
     *  (3.7) Check if type of element and field are valid for the specified table.
     *  (3.8) Remove type tags.
     *  (3.9) Remove placeholders for special symbol (was added to avoid collisions).
     *  (3.10) Add field-value pair to the current data insert.
     *  (3.11) Remove field from helper map (always since there are no wildcards).
     *  (3.12) Check that helper map only contains fields that are allowed to have wildcards values.
     * (4) Add data insert to table.
     *
     * @param dataInsert
     * @param curTable
     */
    private void AddDataInsertToTable(IFLogParser.Table_data_recordContext dataInsert, IFLog.Components.Table curTable)
    {
        IFLog.Components.Table.DataInsert curDataInsert = new IFLog.Components.Table.DataInsert();

        // simple helper to keep track of which fields
        // have default values or are flagged as nullable
        // or are of type auto
        // in other words the ones that are allowed to have wildcards as a value
        Map<String, Boolean> fieldMap = new HashMap<>();
        for(var field : curTable.m_Fields)
        {
            fieldMap.put(
                    field.m_Name,
                    field.m_DefaultValue != null || field.m_Nullable
                            || GetBasetype(field.m_Type).equals(Globals.Translation.Fixed.BT_AUTO));
        }

        // if record only contains values (identification by position)
        if(dataInsert.table_record() != null)
        {
            // check that number of values matches number of fields
            Assert.Check(
                    dataInsert.table_record().values.size() <= curTable.m_Fields.size(),
                    "too many values for data insert in table " + curTable.m_Name
            );
            // or contains a multi-wildcard
            if(dataInsert.table_record().values.size() < curTable.m_Fields.size())
            {
                // multi-wildcard needs to be defined at the end
                var multiWildcard = dataInsert.table_record().SYM_WILDCARD_MULTI();
                Assert.Unequals(
                        multiWildcard, null,
                        "not enough values for data insert in table "
                                + curTable.m_Name + " (you might add a multi-wildcard at the end)"
                );
            }

            int curFieldIdx = 0;
            for(var curDataInsertCtx : dataInsert.table_record().values)
            {
                // if it's not a wildcard
                if(curDataInsertCtx.SYM_WILDCARD_SINGLE() == null)
                {
                    // retrieve the field from the current table
                    IFLog.Components.Table.Field field = curTable.m_Fields.get(curFieldIdx);

                    Assert.Unequals(
                            GetBasetype(field.m_Type),
                            Globals.Translation.Fixed.BT_AUTO,
                            "field " + field.m_Name + " in table " + curTable.m_Name +
                                    " is of type auto and has a non-wildcard value"
                    );

                    // check that it's not a coalesce statement
                    Assert.Check(
                            curDataInsertCtx.stat_coalesce() == null,
                            "coalesce statements are not allowed in table definition"
                    );

                    AddVarOrValueToDataInsert(field, curDataInsertCtx.stat_arithmetic(), curDataInsert, curTable);

                    // keep track of all fields
                    fieldMap.remove(field.m_Name);
                }

                // pointer to current field
                curFieldIdx++;
            }

            // there should only be fields left that contain a default value or are flagged as nullable
            Assert.Check(
                    !fieldMap.containsValue(false),
                    "field for data insert is missing a value in table " + curTable.m_Name
                            + "\n" + fieldMap
            );
        }
        // if record contains fields and associated values (identification by name)
        else
        {
            Assert.Check(
                    dataInsert.table_record_extended().fields.size() <= curTable.m_Fields.size(),
                    "too many values for data insert in table " + curTable.m_Name
            );

            for(int i = 0; i < dataInsert.table_record_extended().fields.size(); ++i)
            {
                var curElement = dataInsert.table_record_extended().values.get(i);

                // retrieve the fieldname and make sure it matches
                // with a field in the current table
                String fieldName = dataInsert.table_record_extended().fields.get(i).getText();
                IFLog.Components.Table.Field field = curTable.GetField(fieldName);

                // check if field is part of table
                Assert.Unequals(
                        field,
                        null,
                        "field " + fieldName + " doesn't exist in table " + curTable.m_Name
                );

                // check for fields with type auto and non-wildcard value
                Assert.Unequals(
                        GetBasetype(field.m_Type),
                        Globals.Translation.Fixed.BT_AUTO,
                        "field " + field.m_Name + " in table " + curTable.m_Name +
                                " is of type auto and has a non-wildcard value"
                );

                // check for duplicates
                Assert.Equals(
                        curDataInsert.m_Fields.contains(field.m_Name),
                        false,
                        "field " + field.m_Name + " already defined as field for data insert in table " + curTable.m_Name
                );

                // check that it's not a coalesce statement
                Assert.Check(
                        curElement.stat_coalesce() == null,
                        "coalesce statements are not allowed in table definition"
                );

                AddVarOrValueToDataInsert(field, curElement.stat_arithmetic(), curDataInsert, curTable);

                // keep track of all fields
                fieldMap.remove(fieldName);
            }

            // there should only be fields left that contain a default value or are flagged as nullable
            Assert.Check(
                    !fieldMap.containsValue(false),
                    "field for data insert is missing a value in table " + curTable.m_Name
                            + "\n" + fieldMap
            );
        }

        curTable.AddDataInsert(curDataInsert);
    }

    private void AddVarOrValueToDataInsert(
            IFLog.Components.Table.Field field, IFLogParser.Stat_arithmeticContext arithmeticStat,
            IFLog.Components.Table.DataInsert curDataInsert, IFLog.Components.Table curTable)
    {
        // parse the statement for the value
        String value = ParseArithmeticStatement(arithmeticStat, curTable, true);

        // check that the value is valid for field type
        ValidateTypeInDataInsert(field, value, curTable.m_Name);

        // remove type
        value = value.split(Globals.Translation.Fixed.SYM_FLAG)[0];

        // remove placeholder for SYM_FLAG
        value = value.replaceAll(
                Globals.Translation.Fixed.SYM_FLAG_STR,
                Globals.Translation.Fixed.SYM_FLAG
        );

        curDataInsert.AddFieldValuePair(field.m_Name, value);
    }

    /* ======================================================================== */
    /* Parse Statements */
    /* ======================================================================== */

    /**
     *
     * Parse a constraint in other words a logical expression, translate where appropriate and return it as a string.
     *
     * (1) Check if negated and get translation.
     * (2) Check if left bracket and add it.
     * (3) Check if the current element is another expression consisting of multiple elements.
     *  (3.1) Parse any additional elements recursively.
     *  (3.2) Check if there's a logical operator and get its translation.
     * (4) Check if it's a comparison statement
     *  (4.1) Check if it's an arithmetic statement.
     *   (4.1.1) Parse lhs and rhs of arithmetic statement recursively.
     *   (4.1.2) Also add type tags to the elements.
     *   (4.1.3) Check that the types of lhs and rhs are comparable (using type tags).
     *   (4.1.4) Get translation for comparison operator.
     *   (4.1.5) Remove type tags.
     *   (4.1.6) Swap lhs and rhs if lhs is null.
     *   (4.1.7) Remove placeholders for special symbol (was added to avoid collisions).
     *   (4.1.8) Add comparison statement to the string.
     *  (4.2) Check if it's an in-statement consisting of a field and a list of values.
     *    (4.2.1) Get base types for field and every value on rhs.
     *    (4.2.2) Check if base types on lhs and rhs are valid for a comparison.
     *    (4.2.3) Get translation for in-operator.
     *    (4.2.4) Get translation for list elements.
     *    (4.2.5) Add in-statement to the string.
     * (5) Check if right bracket and add it.
     * (6) Return parsed and translated constraint as a string.
     *
     * @param body
     * @param curTable
     * @return String
     */
    private String ParseConstraint(IFLogParser.Table_constraint_elContext body, IFLog.Components.Table curTable)
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
            bodyAsString.append(ParseConstraint(bodyEles.get(0), curTable));

            // if there's a logical operator parse the second element as well
            // and add its result to the body string
            // also add the operator
            var logicalOp = body.any_logical_op();
            if(logicalOp != null)
            {
                // get translation for logical operator
                bodyAsString.append(" ").append(GetLogicalOperatorTranslation(logicalOp)).append(" ");
                bodyAsString.append(ParseConstraint(bodyEles.get(1), curTable));
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

                String lhs = ParseArithmeticStatement(arithmeticStats.get(0), curTable, false);
                String rhs = ParseArithmeticStatement(arithmeticStats.get(1), curTable, false);

                // check that the types are comparable
                ValidateTypesInComparisonStatement(lhs, rhs, curTable.m_Name);

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
                String fieldName = inStat.field.getText();

                // check if type of lhs and type of every ele on rhs are valid for operation
                String lhsBasetype = GetBasetypeForField(fieldName, curTable);
                for(var value : inStat.value_list.values)
                {
                    ValidateTypeInList(
                            lhsBasetype,
                            GetBasetypeForValue(value, curTable.m_Name),
                            value.getText(),
                            curTable.m_Name
                    );
                }

                // get translation for in comparison operator
                String inOp = m_TranslationMap.get(
                        Globals.Translation.Operator.Key(Globals.Translation.Fixed.OP_IN)
                );

                bodyAsString.append(fieldName);
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

    /**
     *
     * Parse an arithmetic statement and return it as a string.
     *
     * (1) Check if the statement is another arithmetic statement in itself.
     *  (1.1) Parse additional statements (lhs and rhs) recursively.
     *  (1.2) Get final type of overall arithmetic statement.
     *  (1.3) Get translation for arithmetic operator.
     *  (1.4) Remove old type flags on lhs and rhs statements.
     * (2) Check if the statement is an arithmetic element.
     *  (2.1) Check if it's a field.
     *   (2.1.1) Check if it's not a record (since inserts aren't allowed to contain fields).
     *   (2.1.2) Get field name.
     *   (2.1.3) Get base type for field.
     *  (2.2) Check if it's a value.
     *   (2.2.1) Get value translation.
     *   (2.2.2) Get base type for value.
     * (3) Replace special symbols in strings to avoid collisions (re-replace later).
     * (4) Add left bracket to string.
     * (5) Add lhs statement to string.
     * (6) Add arithmetic operator to string.
     * (7) Add rhs statement to string.
     * (8) Add right bracket to string.
     * (9) Add flag for final type for statement as a whole.
     * (10) Return statement.
     *
     * @param stat
     * @param curTable
     * @param is_record
     * @return String
     */
    private String ParseArithmeticStatement(IFLogParser.Stat_arithmeticContext stat, IFLog.Components.Table curTable, boolean is_record)
    {
        StringBuilder statAsString= new StringBuilder();

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
            // parse any additional statements recursively
            // and add their result to the statement string
            lhs = ParseArithmeticStatement(arithmeticStats.get(0), curTable, is_record);

            // if there's an arithmetic operator parse the second element as well
            // and add its result to the statement string
            // also add the operator
            var arithmeticOp = stat.any_arithmetic_op();
            if(arithmeticOp != null)
            {
                rhs = ParseArithmeticStatement(arithmeticStats.get(1), curTable, is_record);

                // (1) check if valid op + lhs/rhs type + get res type for whole stat
                // (2) replace concat for string/char type
                // (3) remove types from lhs and rhs
                // (4) add lhs and rhs to statement string
                // (5) add res type to statement string
                statType = GetTypeOfArithmeticStatement(lhs, rhs, arithmeticOp, curTable.m_Name);

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
                Assert.Equals(
                        is_record,
                        false,
                        "records/data inserts are not allowed to contain fields"
                );

                String fieldName = arithmeticStatEle.field.getText();

                lhs = fieldName;
                statType = GetBasetypeForField(fieldName, curTable);
            }
            // if it's a value
            else
            {
                var valueCtx = arithmeticStatEle.value;

                lhs = GetValueTranslation(valueCtx);
                statType = GetBasetypeForValue(valueCtx, curTable.m_Name);
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

        return statAsString.toString();
    }

    /* ======================================================================== */
    /* Helpers */
    /* ======================================================================== */

    /**
     *
     * Helper function that checks if a constraint called name already exists.
     * (1) Check all foreign key constraints.
     * (2) Check all primary key constraints.
     * (3) Check all unique key constraints.
     * (4) Check all check key constraints.
     *
     * @param name
     * @param table
     * @return boolean
     */
    private boolean ConstraintExists(String name, IFLog.Components.Table table)
    {
        if(table.GetFkConstraint(name) != null)
            return true;
        if(table.GetPkConstraint(name) != null)
            return true;
        if(table.GetUkConstraint(name) != null)
            return true;
        if(table.GetCheckConstraint(name) != null)
            return true;

        return false;
    }
}
