package IFLog.Evaluators;

import java.util.List;

public abstract class Base
{
    /**
     *
     * Constructor.
     *
     */
    public Base()
    {
        m_TableList = null;
        m_ViewList = null;
        m_CompositeRuleList = null;
    }

    /**
     *
     * Evaluate function that should be overridden by all child classes.
     *
     */
    public void Evaluate() {}

    /* ======================================================================== */
    /* Members */
    /* ======================================================================== */

    // these should all be null by default
    protected List<IFLog.Components.Table>             m_TableList;
    protected List<IFLog.Components.View>              m_ViewList;
    protected List<IFLog.Components.CompositeRule>     m_CompositeRuleList;
}
