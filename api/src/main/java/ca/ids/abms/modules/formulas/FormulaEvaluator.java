package ca.ids.abms.modules.formulas;

import java.util.Map;

/**
 * Evaluate formulas.
 * <p>
 * Formulas are mathematical expressions with dynamic variables enclosed in square brackets.
 * Variables may be supplied by the JavaCode, as a map of name (string) and value (any numeric type)
 * pairs.
 * A number of built-in math functions are available, as follows:
 * <ul>
 *   <li><b>abs(num)</b> - returns the absolute value of a number
 *   <li><b>min(n1, n2)</b> - returns the minimum of two numbers
 *   <li><b>max(n1, n2)</b> - returns the maximum of two numbers
 *   <li><b>ceil(n)</b> or <b>ceiling(n)</b> - round up to nearest integer
 *   <li><b>floor(n)</b> - round down to nearest integer
 *   <li><b>round(n)</b> - round to nearest integer
 *   <li><b>pow(n, e)</b> - "n" to the power of "e"
 *   <li><b>sqr(n)</b> - same as pow (n, 2)
 *   <li><b>sqrt(n)</b> - square root of n
 *   <li><b>trunc(n)</b> or <b>truncate(n)</b> - truncate fractional part
 *   <li><b>trunc(n,p)</b> or <b>truncate(n,p)</b> - truncate to "p" fractional digits
 * </ul>
 * 
 * <p>
 * Currently evaluation is performed using a JavaScript interpreter.
 * 
 * <p>
 * <b>CAUTION</b>: variable names are spelled without square brackets in Java code, when
 *  creating the variable map, but with brackets in the formula expression itself.
 *
 * <h2>EXAMPLES</h2>
 * <pre><code>
 * ...
 * // at class level
 * private FormulaEvaluator formulaEvaluator;
 * ...
 * // at method level
 * ...
 * // create a map of variables
 * final Map&lt;String, Object> vars = new HashMap<>();
 * vars.put ("MTOW", 120.25);            // No square brackets here
 * vars.put ("SchedCrossDist", 510.12);
 * vars.put ("LandingFee", 1100.21);
 * 
 * // evaluate various fomulas; note the square brackets
 * double res;
 * res = formulaEvaluator.evalDouble ("sqrt ([MTOW] / 50) * ([CrossDist] / 100) * 33.28", vars);
 * res = formulaEvaluator.evalDouble ("([CrossDist] / 100) * sqrt ([MTOW] / 10) * 12", vars);
 * res = formulaEvaluator.evalDouble ("([CrossDist] / 100) * sqrt ([MTOW] / 10) * 12", vars);
 * res = formulaEvaluator.evalDouble ("(max (0.0, [CrossDist] - 37) / 100) * sqrt ([MTOW] * 10) * 12", vars);
 * 
 * </code></pre>
 * 
 * <p>
 * <b>NOTE</b>: variables provided in the Map to the eval method must not include square brackets.
 * 
 * @author dpanech
 *
 */
public interface FormulaEvaluator {

    /**
     * Evaluate a formula; returns a double result
     * 
     */
    public double evalDouble (final String expr, final Map <String, Object> vars) throws Exception;

    /**
     * Evaluate a formula; returns a double result
     * 
     * Same as <code>evalDouble (expr, vars);</code>
     */
    
    public double evalDouble (final String expr) throws Exception;
    
}
