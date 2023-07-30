package IFLog.Core;

import IFLog.Extractors.Translation;
import IFLog.Extractors.Comment;
import IFLog.Extractors.Table;
import IFLog.Extractors.View;
import IFLog.Extractors.CompositeRule;
import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetLexer;
import IFLog.Generated.SQLTargetParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.util.*;

/**
 * post-parsing step
 */

public class Extractor
{
    /* ======================================================================== */
    /* Constructor */
    /* ======================================================================== */

    /**
     *
     * Constructor.
     *
     * @param ifl_file
     * @param sql_target_file
     */
    public Extractor(final String ifl_file, final String sql_target_file)
    {
        Init(ifl_file, sql_target_file);

        Extract();
    }

    /**
     *
     * Init function.
     *
     * @param ifl_file
     * @param sql_target_file
     */
    private void Init(final String ifl_file, final String sql_target_file)
    {
        Logger.Log("--- Extractor: Initial Parse (IFLog) ---");
        Logger.Log("[PARSE      ]");
        BuildIFLogParseTree(ifl_file);
        Logger.Log("[         OK]");

        Logger.Log("--- Extractor: Initial Parse (SQLTarget) ---");
        Logger.Log("[PARSE      ]");
        BuildSQLTargetParseTree(sql_target_file);
        Logger.Log("[         OK]");
    }

    /**
     *
     * Main function.
     *
     */
    private void Extract()
    {
        Logger.Log("--- Extractor: Build Translation Map ---");
        Logger.Log("[BUILD      ]");
        m_Translations = new Translation(m_IFLogParser, m_IFLogLexer, m_SQLTargetParser);
        m_Translations.Extract();
        Logger.Log("[         OK]");

        Logger.Log("--- Extractor: Build Comment List ---");
        Logger.Log("[BUILD      ]");
        m_Comments = new Comment(m_IFLogParser, m_IFLogLexer, m_SQLTargetParser);
        m_Comments.Extract();
        Logger.Log("[         OK]");

        Logger.Log("--- Extractor: Build Table List ---");
        Logger.Log("[BUILD      ]");
        m_Tables = new Table(
                m_IFLogParser, m_IFLogLexer, m_SQLTargetParser,
                m_Translations.Get()
        );
        m_Tables.Extract();
        Logger.Log("[         OK]");

        Logger.Log("--- Extractor: Build View List ---");
        Logger.Log("[BUILD      ]");
        m_Views = new View(
                m_IFLogParser, m_IFLogLexer, m_SQLTargetParser,
                m_Translations.Get(), m_Tables.Get()
        );
        m_Views.Extract();
        Logger.Log("[         OK]");

        Logger.Log("--- Extractor: Build Composite Rule List ---");
        Logger.Log("[BUILD      ]");
        m_CompositeRules = new CompositeRule(
                m_IFLogParser, m_IFLogLexer, m_SQLTargetParser,
                m_Translations.Get(), m_Tables.Get(), m_Views.Get()
        );
        m_CompositeRules.Extract();
        Logger.Log("[         OK]");
    }

    /* ======================================================================== */
    /* IFLogParseTree */
    /* ======================================================================== */

    /**
     *
     * Initial parse of IFLog file.
     *
     * @param ifl_file
     */
    private void BuildIFLogParseTree(final String ifl_file)
    {
        m_IFLogLexer = new IFLogLexer(FileIO.ReadToStream(ifl_file));
        m_IFLogParser = new IFLogParser(new CommonTokenStream(m_IFLogLexer));
        m_IFLogParser.setBuildParseTree(true);

        // always reset lexer/parser at the end when used in function
        ResetIFLogLexer();
    }

    /**
     *
     * Helper function to reset IFLog Lexer.
     *
     */
    private void ResetIFLogLexer()
    {
        m_IFLogLexer.reset();
    }

    /* ======================================================================== */
    /* SQLTargetParseTree */
    /* ======================================================================== */

    /**
     *
     * Initial parse of SQLTarget file.
     *
     * @param sql_target_file
     */
    private void BuildSQLTargetParseTree(final String sql_target_file)
    {
        SQLTargetLexer lexer = new SQLTargetLexer(FileIO.ReadToStream(sql_target_file));
        m_SQLTargetParser = new SQLTargetParser(new CommonTokenStream(lexer));
        m_SQLTargetParser.setBuildParseTree(true);
    }

    /* ======================================================================== */
    /* Members */
    /* ======================================================================== */

    public IFLog.Extractors.Translation          m_Translations;
    public IFLog.Extractors.Comment              m_Comments;
    public IFLog.Extractors.Table                m_Tables;
    public IFLog.Extractors.View                 m_Views;
    public IFLog.Extractors.CompositeRule        m_CompositeRules;

    private IFLogParser         m_IFLogParser;
    private IFLogLexer          m_IFLogLexer;
    private SQLTargetParser     m_SQLTargetParser;
}
