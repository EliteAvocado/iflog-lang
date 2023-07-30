package IFLog.Translators;

import IFLog.Core.Assert;
import IFLog.Core.Globals;

import java.util.List;
import java.util.Map;

public abstract class Base
{
    /**
     *
     * Constructor.
     *
     * @param TranslationMap
     */
    public Base(final Map<String, String> TranslationMap)
    {
        this(TranslationMap, null, null, null, null);
    }

    /**
     *
     * Constructor if there are already existing elements that we want to extend.
     *
     * @param TranslationMap
     * @param CommentList
     * @param TableList
     * @param ViewList
     * @param CompositeRuleList
     */
    public Base(final Map<String, String> TranslationMap, final List<IFLog.Components.Comment> CommentList,
                final List<IFLog.Components.Table> TableList, final List<IFLog.Components.View> ViewList,
                final List<IFLog.Components.CompositeRule> CompositeRuleList)
    {
        m_TranslationMap = TranslationMap;
        m_CommentList = CommentList;
        m_TableList = TableList;
        m_ViewList = ViewList;
        m_CompositeRuleList = CompositeRuleList;
    }

    /**
     *
     * Translate function that should be overridden by all child classes.
     *
     */
    public void Translate() {}

    /* ======================================================================== */
    /* Types */
    /* ======================================================================== */

    /**
     *
     * Helper function to get type translation.
     *
     * (1) Check in base types.
     * (2) Check in custom types.
     *
     * @param type
     * @param allow_only_base_types
     * @return String
     */
    final protected String GetTypeTranslation(final String type, boolean allow_only_base_types)
    {
        // check in base types
        String ret = m_TranslationMap.get(Globals.Translation.Basetype.Key(type));
        if (allow_only_base_types) Assert.Unequals(ret, null, "not a base type");

        // if not found there, check in types
        if (ret == null) ret = m_TranslationMap.get(Globals.Translation.Type.Key(type));
        Assert.Unequals(ret, null, "no such type in translation target");

        // do something with it
        return ret;
    }

    /* ======================================================================== */
    /* Members */
    /* ======================================================================== */

    // these should all be null by default
    protected Map<String, String>                      m_TranslationMap;
    protected List<IFLog.Components.Comment>           m_CommentList;
    protected List<IFLog.Components.Table>             m_TableList;
    protected List<IFLog.Components.View>              m_ViewList;
    protected List<IFLog.Components.CompositeRule>     m_CompositeRuleList;
}

