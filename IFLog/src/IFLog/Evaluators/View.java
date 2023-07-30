package IFLog.Evaluators;

import IFLog.Core.Assert;
import IFLog.Core.Logger;

import java.util.List;

public class View extends Base
{
    /**
     *
     * Constructor.
     *
     * @param ViewList
     */
    public View(final List<IFLog.Components.View> ViewList)
    {
        super();

        m_ViewList = ViewList;
    }

    /**
     *
     * Main function.
     *
     */
    @Override
    public void Evaluate()
    {
        for(var curView : m_ViewList)
        {
            // check that there's a type defined or derived for all fields in view
            CheckFieldTypes(curView);
        }
    }

    private void CheckFieldTypes(IFLog.Components.View curView)
    {
        for(var field : curView.m_Fields)
        {
            Assert.Check(
                    field.m_Type != null,
                    "no type defined and/or derived for field " +
                            field.m_Name + " in view " + curView.m_Name
            );
        }
    }
}
