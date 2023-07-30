package IFLog.Core;

import IFLog.Evaluators.CompositeRule;
import IFLog.Evaluators.Table;
import IFLog.Evaluators.View;

import java.util.List;

/**
 * evaluation step
 */

public class Evaluator
{
    /**
     *
     * Constructor.
     *
     * @param extractor
     */
    public Evaluator(final Extractor extractor)
    {
        Evaluate(extractor);
    }

    /**
     *
     * Main function.
     *
     * @param extractor
     */
    private void Evaluate(final Extractor extractor)
    {
        Logger.Log("--- Evaluator: Evaluate Comments ---");
        Logger.Log("[NO CHECKS        ]");
        Logger.Log("[             SKIP]");

        Logger.Log("--- Evaluator: Evaluate Tables ---");
        Logger.Log("[CHECK      ]");
        Table tables = new Table(extractor.m_Tables.Get());
        tables.Evaluate();
        Logger.Log("[         OK]");

        Logger.Log("--- Evaluator: Evaluate Views ---");
        Logger.Log("[CHECK      ]");
        View views = new View(extractor.m_Views.Get());
        views.Evaluate();
        Logger.Log("[         OK]");

        Logger.Log("--- Evaluator: Evaluate Composite Rules ---");
        Logger.Log("[CHECK      ]");
        CompositeRule compRules = new CompositeRule(extractor.m_CompositeRules.Get());
        compRules.Evaluate();
        Logger.Log("[         OK]");
    }
}
