import IFLog.Core.FileIO;
import IFLog.Core.Globals;
import IFLog.IFLog;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "iflog", mixinStandardHelpOptions = true, version = "iflog 1.0",
        description = "Parse and translates IFLog-Files into SQL-Files.")
public class Main implements Callable<Integer>
{
    @Parameters(index = "0", paramLabel = "INPUT_FILE",
            description = "The .ifl-file that should be parsed and translated.")
    private String m_InputFile;
    @Parameters(index = "1", paramLabel = "TARGET_FILE",
            description = "The .ifltarget-file that should contain the output definition used for translation.")
    private String m_TargetFile;
    @Option(names = {"-o", "--out"}, paramLabel = "OUTPUT_FILE",
            description = "The .sql-file that should contain the output.")
    private String m_OutputFile;
    @Option(names = {"-l", "--log"}, paramLabel = "LOG_FILE",
            description = "The optional log-file.")
    private String m_LogFile;
    @Option(names = {"-k", "--keep"}, paramLabel = "KEEP_LOG",
            description = "Whether or not to append to the log-file.")
    private boolean m_KeepLog = false;
    @Option(names = {"-s", "--silent"}, paramLabel = "SILENT_MODE",
            description = "Whether or not to output to the console.")
    private boolean m_Silent = false;

    @Override
    public Integer call() throws Exception
    {
        Globals.DEBUG = !m_Silent;

        if(m_LogFile != null)
        {
            Globals.LOG_FILE = m_LogFile;
        }
        Globals.KEEP_LOG = m_KeepLog;

        // create the IFLog-Parser
        IFLog ifLog = new IFLog();

        // parse and translate the file
        ifLog.Run(m_InputFile, m_TargetFile);

        // generate the output file
        String PSQLFile = ifLog.GenerateOutput();

        if(m_OutputFile != null)
        {
            // clear the output file of any previous content
            FileIO.Flush(m_OutputFile);
            // write to the output file
            FileIO.Write(PSQLFile, m_OutputFile);
        }

        return 0;
    }

    public static void main(String[] args)
    {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}