package IFLog.Core;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * all the file work (read, write)
 */

public final class FileIO
{
    public static List<String> GetAllFilesInDirectory(final String directory)
    {
        File[] files = new File(directory).listFiles();
        Assert.Unequals(files, null, "no directory");
        Assert.Unequals(files.length, 0, "no files in directory");

        List<String> fileNames = new ArrayList<>();

        for (var file : files)
        {
            if (!file.isFile()) continue;

            fileNames.add(file.getName());
        }

        Assert.Unequals(fileNames.size(), 0, "no files in directory");

        // sort it for safety
        // since listFiles() doesn't guarantee an order
        Collections.sort(fileNames);

        return fileNames;
    }

    public static List<String> GetAllSubdirectories(final String root_directory)
    {
        File[] directories = new File(root_directory).listFiles();
        Assert.Unequals(directories, null, "no directory");
        Assert.Unequals(directories.length, 0, "no files in directory");

        List<String> directoryNames = new ArrayList<>();

        for (var directory : directories)
        {
            if (directory.isFile()) continue;

            directoryNames.add(directory.getName());
        }

        Assert.Unequals(directoryNames.size(), 0, "no files in directory");

        // sort it for safety
        // since listFiles() doesn't guarantee an order
        Collections.sort(directoryNames);

        return directoryNames;
    }

    public static String Read(final String file)
    {
        StringBuilder sb = new StringBuilder();
        try( BufferedReader br =
                     new BufferedReader( new InputStreamReader(new FileInputStream(file))))
        {
            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException( e );
        }

        return sb.toString();
    }

    public static CharStream ReadToStream(final String file)
    {
        CharStream input = null;
        try
        {
            input = CharStreams.fromFileName(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return input;
    }

    public static void WriteLine(final String str, final String file)
    {
        Write(str + System.lineSeparator(), file);
    }

    public static void Write(final String str, final String file)
    {
        DoWrite(str + System.lineSeparator(), file, true);
    }

    public static void Flush(final String file)
    {
        DoWrite("", file, false);
    }

    private static void DoWrite(final String str, final String file, final boolean append)
    {
        try
        {
            m_Writer = new FileWriter(file, append);
            m_Writer.write(str);
            m_Writer.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException( e );
        }
    }

    // so we don't accidentally instantiate it
    private FileIO() {}

    private static FileWriter m_Writer;
    private static FileReader m_Reader;
}
