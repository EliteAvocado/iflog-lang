package IFLog.Evaluators;

import IFLog.Core.Assert;
import IFLog.Core.Globals;
import IFLog.Core.Logger;

import java.util.List;

public class CompositeRule extends Base
{
    /**
     *
     * Constructor.
     *
     * @param CompositeRuleList
     */
    public CompositeRule(final List<IFLog.Components.CompositeRule> CompositeRuleList)
    {
        super();

        m_CompositeRuleList = CompositeRuleList;
    }

    /**
     *
     * Main function.
     *
     */
    @Override
    public void Evaluate()
    {
        for(var curCompositeRule : m_CompositeRuleList)
        {
            // check that there's a type defined or derived for all comp rule vars
            CheckCompRuleVars(curCompositeRule);

            // check that there's a type defined or derived for all globals vars
            // check that every global var is set
            // check that every global var was Accessed (else warning)
            CheckGlobalVars(curCompositeRule);

            // check that there's a return type defined or derived for every composite rules
            // check that there are no compRuleVars for trigger defined
            CheckReturnType(curCompositeRule);

            // check that there's a return value defined
            // (for now only warning since it doesn't check every branch)
            CheckReturnValues(curCompositeRule);
        }
    }

    private void CheckCompRuleVars(IFLog.Components.CompositeRule curCompositeRule)
    {
        for(var compRuleVar : curCompositeRule.m_CompRuleVars)
        {
            Assert.Check(
                    compRuleVar.m_Type != null,
                    "no type defined and/or derived for compRuleVar " +
                            compRuleVar.m_Name + " in composite rule " + curCompositeRule.m_Name
            );
        }
    }

    private void CheckGlobalVars(IFLog.Components.CompositeRule curCompositeRule)
    {
        for(var globalVar : curCompositeRule.m_AllVars)
        {
            Assert.Check(
                    globalVar.m_Type != null,
                    "no type defined and/or derived for globalVar " +
                            globalVar.m_Name + " in composite rule " + curCompositeRule.m_Name
            );

            if(!globalVar.m_IsSet)
            {
                Logger.LogWarning(
                        "globalVar " + globalVar.m_Name + " was never set in composite rule " +
                                curCompositeRule.m_Name
                );
            }

            if(!globalVar.m_WasAccessed && !globalVar.m_IgnoreAccess)
            {
                Logger.LogWarning(
                        "globalVar " + globalVar.m_Name + " was never accessed in any rule head in composite rule " +
                                curCompositeRule.m_Name
                );
            }
        }
    }

    private void CheckReturnType(IFLog.Components.CompositeRule curCompositeRule)
    {
        Assert.Check(
                curCompositeRule.m_ReturnType != null,
                "no return type defined and/or derived for composite rule " +
                        curCompositeRule.m_Name
        );

        if(curCompositeRule.m_ReturnType.equals(Globals.Translation.Fixed.BT_TRIGGER))
        {
            Assert.Check(
                    curCompositeRule.m_CompRuleVars.size() == 0,
                    "no compRuleVars allowed for compositeRules of return type trigger for composite rule " +
                            curCompositeRule.m_Name
            );
        }
    }

    private void CheckReturnValues(IFLog.Components.CompositeRule curCompositeRule)
    {
        // skip any composite rules with return type void
        if(curCompositeRule.m_ReturnType.equals(Globals.Translation.Fixed.BT_VOID)) return;

        boolean found = false;

        for(var compRule : curCompositeRule.m_CompRuleBlocks)
        {
            // if it's not an if/elif/else/for/foreach and there's a return value
            if(compRule.m_IfCondition == null &&
                    compRule.m_AlternativeTo == null &&
                    compRule.m_ForEachLoop == null &&
                    compRule.m_ForLoop == null &&
                    compRule.m_ReturnValue != null)
            {
                found = true;
                break;
            }
        }

        if(!found)
        {
            Logger.LogWarning(
                    "composite rule " + curCompositeRule.m_Name + " with return type " +
                            curCompositeRule.m_ReturnType + " might be missing a return value"
            );
        }
    }
}
