package IFLog.Core;

/**
 * custom logger (for console output or log files)
 */

public final class Logger
{
    public static void Flush()
    {
        FileIO.Flush(Globals.LOG_FILE);
    }

    public static void Log(final String str)
    {
        LogInfo(str);
    }

    public static void LogInfo(final String info)
    {
        String msg = "INFO: "  + info;
        DoLog(msg, false);

    }
    public static void LogWarning(final String warning)
    {
        String msg = "WARNING: "  + warning;
        DoLog(msg, false);

    }
    public static void LogError(final String error)
    {
        String msg = "ERROR: "  + error;
        DoLog(msg, true);
    }

    public static void LogNL()
    {
        DoLog("", false);
    }

    private static void DoLog(final String str, final boolean is_err)
    {
        if(Globals.DEBUG)
        {
            if (is_err) System.err.println(str);
            else System.out.println(str);
        }

        if(Globals.LOG_FILE != null)
        {
            FileIO.Write(str, Globals.LOG_FILE);
        }
    }

    // so we don't accidentally instantiate it
    private Logger() {}
}
