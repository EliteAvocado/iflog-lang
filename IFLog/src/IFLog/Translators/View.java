package IFLog.Translators;

import IFLog.Core.Globals;
import IFLog.Core.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class View extends Base
{
    /**
     *
     * Constructor.
     *
     * @param TranslationMap
     * @param ViewList
     */
    public View(final Map<String, String> TranslationMap, final List<IFLog.Components.View> ViewList)
    {
        super(TranslationMap);

        m_ViewList = ViewList;
        m_Views = new ArrayList<>();
    }

    /**
     *
     * Main function.
     *
     */
    @Override
    public void Translate()
    {
        for(var curView : m_ViewList)
        {
            String fieldBlock = GetFieldsTranslation(curView);

            List<String> selectBlocks = GetSelectTranslations(curView);

            // create view
            // get the template
            String curViewBlock = m_TranslationMap.get(
                    Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_CREATE)
            );

            StringBuilder curViewBody = new StringBuilder();
            for(var selectBlock : selectBlocks)
            {
                // add union  all between select blocks
                if(!curViewBody.isEmpty())
                {
                    curViewBody.append(Globals.Translation.Fixed.SEP_NEWLINE);
                    curViewBody.append(
                            m_TranslationMap.get(Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_UNION_ALL))
                    );
                }
                curViewBody.append(Globals.Translation.Fixed.SEP_NEWLINE).append(selectBlock);
            }

            // set the name
            curViewBlock = curViewBlock.replace(
                    Globals.Translation.View.V_NAME,
                    curView.m_Name
            );

            // set the fields
            curViewBlock = curViewBlock.replace(
                    Globals.Translation.View.V_FIELDS,
                    fieldBlock
            );

            // set the body
            curViewBlock = curViewBlock.replace(
                    Globals.Translation.View.V_BODY,
                    curViewBody
            );

            // add terminal symbol
            curViewBlock += m_TranslationMap.get(
                    Globals.Translation.Symbol.Key(Globals.Translation.Fixed.SYM_TERMINAL)
            );

            // add it
            m_Views.add(curViewBlock);
        }
    }

    private String GetFieldsTranslation(IFLog.Components.View curView)
    {
        StringBuilder fieldBlock = new StringBuilder();

        for(var field : curView.m_Fields)
        {
            if(!fieldBlock.isEmpty())
            {
                fieldBlock.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }
            fieldBlock.append(field.m_Name);
        }

        return fieldBlock.toString();
    }

    private List<String> GetSelectTranslations(IFLog.Components.View curView)
    {
        List<String> selectBlocks = new ArrayList<>();

        for(var curSelect : curView.m_Selects)
        {
            StringBuilder selectBlock = new StringBuilder();

            // head with vars
            selectBlock.append(GetSelectHeadTranslation(curView, curSelect));
            // from block
            if(curSelect.m_TablesViews.size() > 0)
            {
                selectBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                selectBlock.append(GetSelectFromTranslation(curView, curSelect));
            }
            // where block
            if(curSelect.m_Constraint != null)
            {
                selectBlock.append(Globals.Translation.Fixed.SEP_NEWLINE);
                selectBlock.append(GetSelectWhereTranslation(curView, curSelect));
            }

            selectBlocks.add(selectBlock.toString());
        }

        return selectBlocks;
    }

    private String GetSelectHeadTranslation(IFLog.Components.View curView, IFLog.Components.View.Select curSelect)
    {
        String selectHeadTemplate = m_TranslationMap.get(
                Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT)
        );

        StringBuilder selectVars = new StringBuilder();

        for(var field : curView.m_Fields)
        {
            String selectVarTemplate = m_TranslationMap.get(
                    Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_VAR)
            );

            var selectVar = curSelect.GetVar(field.m_Name);

            // set the name (as alias)
            selectVarTemplate = selectVarTemplate.replace(Globals.Translation.View.V_NAME, field.m_Name);

            // set the actual value
            selectVarTemplate = selectVarTemplate.replace(Globals.Translation.View.V_BODY, selectVar.m_Value);

            // add to the list of vars
            if(!selectVars.isEmpty())
            {
                selectVars.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }
            selectVars.append(selectVarTemplate);
        }

        selectHeadTemplate = selectHeadTemplate.replace(Globals.Translation.View.V_VARS, selectVars);

        return selectHeadTemplate;
    }

    private String GetSelectFromTranslation(IFLog.Components.View curView, IFLog.Components.View.Select curSelect)
    {
        String selectFromTemplate = m_TranslationMap.get(
                Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_FROM)
        );

        StringBuilder tableViews = new StringBuilder();

        for(var curTableView : curSelect.m_TablesViews)
        {
            String selectTableViewTemplate = m_TranslationMap.get(
                    Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_TABLEVIEW)
            );

            // set the name
            selectTableViewTemplate = selectTableViewTemplate.replace(Globals.Translation.View.V_NAME, curTableView.m_Name);

            // set the alias
            selectTableViewTemplate = selectTableViewTemplate.replace(Globals.Translation.View.V_ALIAS, curTableView.m_Alias);

            // add to the list of tables and views
            if(!tableViews.isEmpty())
            {
                tableViews.append(Globals.Translation.Fixed.SEP_SEQUENCE).append(" ");
            }
            tableViews.append(selectTableViewTemplate);
        }

        selectFromTemplate = selectFromTemplate.replace(Globals.Translation.View.V_TABLEVIEWS, tableViews);

        return selectFromTemplate;
    }

    private String GetSelectWhereTranslation(IFLog.Components.View curView, IFLog.Components.View.Select curSelect)
    {
        String selectWhereTemplate = m_TranslationMap.get(
                Globals.Translation.View.Key(Globals.Translation.Fixed.VIEW_SELECT_WHERE)
        );

        selectWhereTemplate = selectWhereTemplate.replace(Globals.Translation.View.V_CONDITION, curSelect.m_Constraint);

        return selectWhereTemplate;
    }

    public List<String> m_Views;
}
