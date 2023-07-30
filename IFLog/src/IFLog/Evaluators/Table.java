package IFLog.Evaluators;

import IFLog.Core.Assert;

import java.util.List;

public class Table extends Base
{
    /**
     *
     * Constructor.
     *
     * @param TableList
     */
    public Table(final List<IFLog.Components.Table> TableList)
    {
        super();

        m_TableList = TableList;
    }

    /**
     *
     * Main function.
     *
     * (1) Check if every foreign key constraint references only primary keys or unique keys.
     * (2) Check for duplicate keys (pk/uk constraints should never reference exactly the same fields).
     *
     */
    @Override
    public void Evaluate()
    {
        for(var curTable : m_TableList)
        {
            // check if every fk constraint references only primary keys or unique keys
            CheckFkConstraints(curTable);

            // check for duplicate keys
            // (pk/uk constraints should never reference exactly the same fields)
            CheckPkUkConstraintsUnique(curTable);
        }
    }

    /**
     *
     * Check if every foreign key constraint references only primary keys or unique keys.
     *
     * (1) Check against the primary key.
     * (2) Check against all unique keys.
     *
     * @param curTable
     */
    private void CheckFkConstraints(IFLog.Components.Table curTable)
    {
        for(var fkConstraint : curTable.m_FkConstraints)
        {
            for(var pkTable : m_TableList)
            {
                if(fkConstraint.m_Parent.equals(pkTable.m_Name))
                {
                    boolean isValid = false;

                    // check primary key
                    if(pkTable.m_PkConstraint != null)
                    {
                        isValid = (
                                fkConstraint.m_ParentFields.containsAll(pkTable.m_PkConstraint.m_Fields) &&
                                        pkTable.m_PkConstraint.m_Fields.containsAll(fkConstraint.m_ParentFields)
                        );
                    }

                    // check unique keys
                    if(!isValid)
                    {
                        for(var ukConstraint : pkTable.m_UkConstraints)
                        {
                            isValid = (
                                    fkConstraint.m_ParentFields.containsAll(ukConstraint.m_Fields) &&
                                            ukConstraint.m_Fields.containsAll(fkConstraint.m_ParentFields)
                            );

                            if(isValid) break;
                        }
                    }

                    Assert.Check(
                            isValid,
                            "fk constraint " + fkConstraint.m_Name + " in table "
                                    + fkConstraint.m_Parent + " doesn't reference a valid primary or unique key"
                    );

                    break;
                }
            }
        }
    }

    /**
     *
     * Check for duplicate keys (pk/uk constraints should never reference exactly the same fields).
     *
     * (1) Check primary key against all unique keys.
     * (2) Check unique keys against all other unique keys.
     *
     * @param curTable
     */
    private void CheckPkUkConstraintsUnique(IFLog.Components.Table curTable)
    {
        // check primary key against all unique keys
        if(curTable.m_PkConstraint != null)
        {
            for(var ukConstraint : curTable.m_UkConstraints)
            {
                Assert.Check(
                        !(curTable.m_PkConstraint.m_Fields.containsAll(ukConstraint.m_Fields) &&
                                ukConstraint.m_Fields.containsAll(curTable.m_PkConstraint.m_Fields)),
                        "duplicate unique key in table " + curTable.m_Name +
                                " with fields " + curTable.m_PkConstraint.m_Fields
                );
            }
        }
        // check unique keys against each other
        int ukIdx = 1;
        for(var ukConstraint : curTable.m_UkConstraints)
        {
            for(int i = ukIdx; i < curTable.m_UkConstraints.size(); i++)
            {
                Assert.Check(
                        !(ukConstraint.m_Fields.containsAll(curTable.m_UkConstraints.get(i).m_Fields) &&
                                curTable.m_UkConstraints.get(i).m_Fields.containsAll(ukConstraint.m_Fields)),
                        "duplicate unique key in table " + curTable.m_Name +
                                " with fields " + ukConstraint.m_Fields
                );
            }

            ukIdx++;
        }
    }
}
