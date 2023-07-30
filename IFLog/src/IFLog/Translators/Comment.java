package IFLog.Translators;

import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Comment extends Base
{
    /**
     *
     * Constructor.
     *
     * @param TranslationMap
     * @param CommentList
     */
    public Comment(final Map<String, String> TranslationMap, final List<IFLog.Components.Comment> CommentList)
    {
        super(TranslationMap);

        m_CommentList = CommentList;
        m_Comments = new ArrayList<>();
    }

    /**
     *
     * Main function.
     *
     */
    @Override
    public void Translate()
    {
        for(IFLog.Components.Comment comment : m_CommentList)
        {
            String translation = null;

            if (comment.m_Type == IFLog.Components.Comment.Type.LINE.ordinal())
                translation = m_TranslationMap.get(Globals.Translation.Comment.LINE);
            else if (comment.m_Type == IFLog.Components.Comment.Type.BLOCK.ordinal())
                translation = m_TranslationMap.get(Globals.Translation.Comment.BLOCK);
            Assert.Unequals(translation, null, "no translation target found");

            translation = translation.replaceAll(Globals.Translation.Comment.TEXT, comment.m_Comment);

            m_Comments.add(translation);
        }
    }

    public List<String> m_Comments;
}
