package IFLog.Core;


import IFLog.Translators.Comment;
import IFLog.Translators.CompositeRule;
import IFLog.Translators.Table;
import IFLog.Translators.View;

import java.util.List;

/**
 * actual translator that uses the extractor and translation targets
 */

public class Translator
{
    /**
     *
     * Constructor.
     *
     * @param extractor
     */
    public Translator(final Extractor extractor)
    {
        Translate(extractor);
    }

    /**
     *
     * Main function.
     *
     * @param extractor
     */
    private void Translate(final Extractor extractor)
    {
        Logger.Log("--- Translator: Translate Comments ---");
        Logger.Log("[TRANSLATE      ]");
        Comment comments = new Comment(
                extractor.m_Translations.Get(), extractor.m_Comments.Get()
        );
        comments.Translate();
        Logger.Log("[             OK]");

        Logger.Log("--- Translator: Translate Tables ---");
        Logger.Log("[TRANSLATE      ]");
        Table tables = new Table(
                extractor.m_Translations.Get(), extractor.m_Tables.Get()
        );
        tables.Translate();
        Logger.Log("[             OK]");

        Logger.Log("--- Translator: Translate Views ---");
        Logger.Log("[TRANSLATE      ]");
        View views = new View(
                extractor.m_Translations.Get(), extractor.m_Views.Get()
        );
        views.Translate();
        Logger.Log("[             OK]");

        Logger.Log("--- Translator: Translate Composite Rules ---");
        Logger.Log("[TRANSLATE      ]");
        CompositeRule compRules = new CompositeRule(
                extractor.m_Translations.Get(), extractor.m_CompositeRules.Get()
        );
        compRules.Translate();
        Logger.Log("[             OK]");

        // add them for easier access
        m_Comments = comments.m_Comments;
        m_Tables = tables.m_Tables;
        m_DataInserts = tables.m_DataInserts;
        m_Views = views.m_Views;
        m_CompositeRules = compRules.m_CompositeRules;
        m_Triggers = compRules.m_Triggers;
    }

    /* ======================================================================== */
    /* Members */
    /* ======================================================================== */

    public List<String> m_Comments;
    public List<String> m_Tables;
    public List<String> m_DataInserts;
    public List<String> m_Views;
    public List<String> m_CompositeRules;
    public List<String> m_Triggers;
}
