package IFLog;

import IFLog.Core.*;

/**
 * main file for import (basically creates instances for extractor, evaluator and translator
 * and then runs them one after another)
 */

public class IFLog
{
    public IFLog()
    {
        if(Globals.LOG_FILE != null && !Globals.KEEP_LOG) Logger.Flush();
        else Logger.LogNL();

        Logger.Log("Current Working Directory = " + System.getProperty("user.dir"));
    }

    public String GenerateOutput()
    {
        // check for init
        Assert.Unequals(m_Extractor, null, "not initialized");

        StringBuilder output = new StringBuilder();

        // add tables
        for(String table : m_Translator.m_Tables)
        {
            output.append(table);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
        }

        // add inserts
        for(String dataInsert : m_Translator.m_DataInserts)
        {
            output.append(dataInsert);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
        }

        // add views
        for(String view : m_Translator.m_Views)
        {
            output.append(view);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
        }

        // add composite rules
        for(String compositeRule : m_Translator.m_CompositeRules)
        {
            output.append(compositeRule);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
        }

        // add triggers
        for(String trigger : m_Translator.m_Triggers)
        {
            output.append(trigger);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
            output.append(Globals.Translation.Fixed.SEP_NEWLINE);
        }

        return output.toString();
    }

    public void Run(String ifl_file, String sql_target_file)
    {
        Initialize(ifl_file, sql_target_file);
    }

    private void Initialize(String ifl_file, String sql_target_file)
    {
        m_Extractor = new Extractor(ifl_file, sql_target_file);
        m_Evaluator  = new Evaluator(m_Extractor);
        m_Translator = new Translator(m_Extractor);
    }

    public Extractor m_Extractor;
    public Evaluator m_Evaluator;
    public Translator m_Translator;
}
