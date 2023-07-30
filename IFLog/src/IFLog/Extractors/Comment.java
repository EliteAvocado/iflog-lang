package IFLog.Extractors;

import IFLog.Generated.IFLogLexer;
import IFLog.Generated.IFLogParser;
import IFLog.Generated.SQLTargetParser;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment extends Base
{
    /**
     *
     * Constructor.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     */
    public Comment(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser)
    {
        this(IFLogParser, IFLogLexer, SQLTargetParser, null);
    }

    /**
     *
     * Constructor if there's already an existing comment list which we want to extend.
     * In any other case we create a new one.
     *
     * @param IFLogParser
     * @param IFLogLexer
     * @param SQLTargetParser
     * @param CommentList
     */
    public Comment(final IFLogParser IFLogParser, final IFLogLexer IFLogLexer, final SQLTargetParser SQLTargetParser,
                   final List<IFLog.Components.Comment> CommentList)
    {
        super(IFLogParser, IFLogLexer, SQLTargetParser);

        if(CommentList == null)
            m_CommentList = new ArrayList<>();
    }

    /**
     *
     * Access function.
     *
     * @return ArrayList
     */
    public List<IFLog.Components.Comment> Get() { return m_CommentList; }

    /**
     *
     * Main function.
     *
     * (1) Check if line or block comment.
     * (2) Remove comment symbols.
     * (3) Create new Comment and add it to the list.
     *
     */
    @Override
    public void Extract()
    {
        for (Token t : m_IFLogLexer.getAllTokens())
        {
            if (t.getChannel() == IFLogLexer.LINE_COMMENT)
            {
                String comment = t.getText();
                // remove first two chars: "//"
                comment = comment.substring(2);

                m_CommentList.add(new IFLog.Components.Comment(
                        IFLog.Components.Comment.Type.LINE.ordinal(),
                        t.getLine(),
                        comment));
            }
            else if (t.getChannel() == IFLogLexer.BLOCK_COMMENT)
            {
                String comment = t.getText();
                // remove first two and last two chars: "/**/"
                comment = comment.substring(2, comment.length() - 2);

                m_CommentList.add(new IFLog.Components.Comment(
                        IFLog.Components.Comment.Type.BLOCK.ordinal(),
                        t.getLine(),
                        comment));
            }
        }

        // always reset lexer/parser at the end when used in function
        ResetIFLogLexer();
    }
}
