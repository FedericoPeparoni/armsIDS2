package ca.ids.abms.modules.formulas.unifiedtax;

import java.util.Map;

import org.eclipse.birt.report.engine.javascript.JavascriptEngineFactory;


public interface FormulaEvaluatorUT {
    /**
     * Evaluate a formula; returns a double result
     * 
     * Same as <code>evalDouble (expr, vars);</code>
     */
    
    public double evalDouble (final String expr) throws Exception;
    
}
