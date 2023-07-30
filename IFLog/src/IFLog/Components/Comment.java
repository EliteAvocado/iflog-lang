package IFLog.Components;

public class Comment
{
    public Comment(final int type, final int line, final String comment)
    {
        // init super
        super();

        m_Type = type;
        m_Line = line;
        m_Comment = comment;
    }

    public enum Type
    {
        LINE,
        BLOCK
    }

    public int m_Type;
    public int m_Line;
    public String m_Comment;
}
