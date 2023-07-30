package IFLog.Translators;

import IFLog.Core.Globals;
import IFLog.Core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table extends Base
{
    /**
     *
     * Constructor.
     *
     * @param TranslationMap
     * @param TableList
     */
    public Table(final Map<String, String> TranslationMap, final List<IFLog.Components.Table> TableList)
    {
        super(TranslationMap);

        m_TableList = TableList;
        m_Tables = new ArrayList<>();
        m_DataInserts = new ArrayList<>();
    }

    /**
     *
     * Main function.
     *
     * (1) Translate fields.
     * (2) Translate primary key constraint.
     * (3) Translate unique key constraints.
     * (4) Translate foreign key constraints.
     * (5) Translate check constraints.
     * (6) Translate data inserts.
     * (7) Translate table block.
     * (8) Add fields.
     * (9) Add primary key constraints.
     * (10) Add unique key constraints.
     * (11) Add foreign key constraints.
     * (12) Add check constraints.
     * (13) Set name.
     * (14) Set body.
     * (15) Add terminal symbol.
     *
     */
    @Override
    public void Translate()
    {
        for(IFLog.Components.Table curTable : m_TableList)
        {
            // create field blocks
            List<String> fieldBlocks = GetFieldTranslations(curTable);

            // create pk constraint block
            String pkBlock = GetPkConstraintTranslations(curTable);

            // create uk constraint blocks
            List<String> ukBlocks = GetUkConstraintTranslations(curTable);

            // create fk constraint blocks
            List<String> fkBlocks = GetFkConstraintTranslations(curTable);

            // create check constraint blocks
            List<String> checkBlocks = GetCheckConstraintTranslations(curTable);

            // create data inserts
            TranslateDataInserts(curTable);

            // create table
            // get the template
            String curTableBlock = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_CREATE)
            );

            StringBuilder curTableBody = new StringBuilder();

            // add the fields
            int tableEleIdx = 0;
            for(var fieldBlock : fieldBlocks)
            {
                if(tableEleIdx > 0)
                    curTableBody.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(Globals.Translation.Fixed.SEP_NEWLINE);

                curTableBody.append(fieldBlock);

                tableEleIdx++;
            }

            // add pk constraint
            if(pkBlock != null)
            {
                // there will always be at least one field
                // so we can safely add the separator
                curTableBody.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(Globals.Translation.Fixed.SEP_NEWLINE);
                curTableBody.append(pkBlock);
            }

            // add uk constraints
            for(var ukBlock : ukBlocks)
            {
                curTableBody.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(Globals.Translation.Fixed.SEP_NEWLINE);
                curTableBody.append(ukBlock);
            }

            // add fk constraints
            for(var fkBlock : fkBlocks)
            {
                curTableBody.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(Globals.Translation.Fixed.SEP_NEWLINE);
                curTableBody.append(fkBlock);
            }

            // add check constraints
            for(var checkBlock : checkBlocks)
            {
                curTableBody.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(Globals.Translation.Fixed.SEP_NEWLINE);
                curTableBody.append(checkBlock);
            }

            // set the name
            curTableBlock = curTableBlock.replace(
                    Globals.Translation.Table.V_NAME,
                    curTable.m_Name
            );

            // set the body
            curTableBlock = curTableBlock.replace(
                    Globals.Translation.Table.V_BODY,
                    Globals.Translation.Fixed.SEP_NEWLINE + curTableBody + Globals.Translation.Fixed.SEP_NEWLINE
            );

            // add terminal symbol
            curTableBlock += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            // add it
            m_Tables.add(curTableBlock);
        }
    }

    /**
     *
     * Translate fields.
     *
     * @param curTable
     * @return ArrayList
     */
    private List<String> GetFieldTranslations(IFLog.Components.Table curTable)
    {
        List<String> fieldBlocks = new ArrayList<>();

        if(curTable.m_Fields.size() > 0)
        {
            // get the template
            String blockTemplate = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_FIELD)
            );

            for(var field : curTable.m_Fields)
            {
                // set the name
                String curBlock = blockTemplate.replace(
                        Globals.Translation.Table.V_NAME,
                        field.m_Name
                );

                // set the type
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_TYPE,
                        GetTypeTranslation(field.m_Type, false)
                );

                // set default value
                String defaultValue = "";
                if(field.m_DefaultValue != null)
                {
                    defaultValue = m_TranslationMap.get(
                            Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_FIELD_DEFAULT)
                    );

                    defaultValue += " " + field.m_DefaultValue + " ";
                }
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_DEFAULT,
                        defaultValue
                );

                // set nullable
                String nullable;
                if(field.m_Nullable)
                {
                    nullable = m_TranslationMap.get(
                            Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_FIELD_NULLABLE)
                    );
                }
                else
                {
                    nullable = m_TranslationMap.get(
                            Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_FIELD_NOT_NULLABLE)
                    );
                }
                // set default value
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_NULLABLE,
                        nullable
                );

                fieldBlocks.add(curBlock);
            }
        }

        return fieldBlocks;
    }

    /**
     *
     * Translate primary key constraint.
     *
     * @param curTable
     * @return String
     */
    private String GetPkConstraintTranslations(IFLog.Components.Table curTable)
    {
        String pkBlock = null;

        if(curTable.m_PkConstraint != null)
        {
            // get the template
            pkBlock = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_PK)
            );

            StringBuilder fieldList = new StringBuilder();
            int pkFieldIdx = 0;
            for(String field : curTable.m_PkConstraint.m_Fields)
            {
                if(pkFieldIdx > 0)
                    fieldList.append(", ");

                fieldList.append(field);

                pkFieldIdx++;
            }

            // set the name
            pkBlock = pkBlock.replace(
                    Globals.Translation.Table.V_NAME, curTable.m_PkConstraint.m_Name
            );

            // add the fields
            pkBlock = pkBlock.replace(
                    Globals.Translation.Table.V_FIELDS, fieldList
            );
        }

        return pkBlock;
    }

    /**
     Translate unique key constraints.
     *
     * @param curTable
     * @return ArrayList
     */
    private List<String> GetUkConstraintTranslations(IFLog.Components.Table curTable)
    {
        List<String> ukBlocks = new ArrayList<>();

        if(curTable.m_UkConstraints.size() > 0)
        {
            // get the template
            String blockTemplate = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_UK)
            );

            for(var ukConstraint : curTable.m_UkConstraints)
            {
                StringBuilder fieldList = new StringBuilder();
                int ukFieldIdx = 0;
                for(String field : ukConstraint.m_Fields)
                {
                    if(ukFieldIdx > 0)
                        fieldList.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");

                    fieldList.append(field);

                    ukFieldIdx++;
                }

                // set the name
                String curBlock = blockTemplate.replace(
                        Globals.Translation.Table.V_NAME, ukConstraint.m_Name
                );

                // add the fields
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_FIELDS, fieldList
                );

                ukBlocks.add(curBlock);
            }
        }

        return ukBlocks;
    }

    /**
     *
     * Translate foreign key constraints.
     *
     * @param curTable
     * @return ArrayList
     */
    private List<String> GetFkConstraintTranslations(IFLog.Components.Table curTable)
    {
        List<String> fkBlocks = new ArrayList<>();

        if(curTable.m_FkConstraints.size() > 0)
        {
            // get the template
            String blockTemplate = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_FK)
            );

            for(var fkConstraint : curTable.m_FkConstraints)
            {
                StringBuilder fieldList = new StringBuilder();
                int fkFieldIdx = 0;
                for(String field : fkConstraint.m_Fields)
                {
                    if(fkFieldIdx > 0)
                        fieldList.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");

                    fieldList.append(field);

                    fkFieldIdx++;
                }

                StringBuilder parentFieldList = new StringBuilder();
                fkFieldIdx = 0;
                for(String field : fkConstraint.m_ParentFields)
                {
                    if(fkFieldIdx > 0)
                        parentFieldList.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");

                    parentFieldList.append(field);

                    fkFieldIdx++;
                }

                // set the name
                String curBlock = blockTemplate.replace(
                        Globals.Translation.Table.V_NAME, fkConstraint.m_Name
                );

                // add the parent table
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_PARENT_TABLE, fkConstraint.m_Parent
                );

                // add the fields
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_FIELDS, fieldList
                );

                // add the parent fields
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_PARENT_FIELDS, parentFieldList
                );

                fkBlocks.add(curBlock);
            }
        }

        return fkBlocks;
    }

    /**
     *
     * Translate check constraints.
     *
     * @param curTable
     * @return ArrayList
     */
    private List<String> GetCheckConstraintTranslations(IFLog.Components.Table curTable)
    {
        List<String> checkBlocks = new ArrayList<>();

        if(curTable.m_CheckConstraints.size() > 0)
        {
            // get the template
            String blockTemplate = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_CHECK)
            );

            for(var checkConstraint : curTable.m_CheckConstraints)
            {
                // set the name
                String curBlock = blockTemplate.replace(
                        Globals.Translation.Table.V_NAME, checkConstraint.m_Name
                );

                // add the body with conditions
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_BODY, checkConstraint.m_Body
                );

                checkBlocks.add(curBlock);
            }
        }

        return checkBlocks;
    }

    /**
     *
     * Translate data inserts.
     *
     * @param curTable
     */
    private void TranslateDataInserts(IFLog.Components.Table curTable)
    {
        if(curTable.m_DataInserts.size() > 0)
        {
            // get the template
            String blockTemplate = m_TranslationMap.get(
                    Globals.Translation.Table.Key(Globals.Translation.Fixed.TABLE_DATA_INSERT)
            );

            for(var dataInsert : curTable.m_DataInserts)
            {
                StringBuilder fieldList = new StringBuilder();
                int diFieldIdx = 0;
                for(String field : dataInsert.m_Fields)
                {
                    if(diFieldIdx > 0)
                        fieldList.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");

                    fieldList.append(field);

                    diFieldIdx++;
                }

                StringBuilder valueList = new StringBuilder();
                int valueIdx = 0;
                for(String value : dataInsert.m_Values)
                {
                    if(valueIdx > 0)
                        valueList.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");

                    valueList.append(value);

                    valueIdx++;
                }

                // set the table name
                String curBlock = blockTemplate.replace(
                        Globals.Translation.Table.V_NAME, curTable.m_Name
                );

                // add the fields
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_FIELDS, fieldList
                );

                // add the values
                curBlock = curBlock.replace(
                        Globals.Translation.Table.V_VALUES, valueList
                );

                // add terminal symbol
                curBlock += m_TranslationMap.get(
                        Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
                );

                m_DataInserts.add(curBlock);
            }
        }
    }

    public List<String> m_Tables;
    public List<String> m_DataInserts;
}
