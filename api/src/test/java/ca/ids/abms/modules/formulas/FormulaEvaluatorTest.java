package ca.ids.abms.modules.formulas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.flightmovements.enumerate.CostFormulaVar;

public class FormulaEvaluatorTest {
	private FormulaEvaluator formulaEvaluator;

    @Before
    public void setup() {
    	this.formulaEvaluator = new FormulaEvaluatorImpl (new JavascriptEngineFactory());
    }

    // Make sure we can evaluate JavaScript exceptions
    @Test
    public void testBasic() throws Exception {
    	final Map <String, Object> vars = new HashMap <>();
    	vars.put ("TEST", 25);
    	final double res = formulaEvaluator.evalDouble ("Math.sqrt ([TEST])", vars);
    	assertThat (res).isEqualTo (5);
        final double res2 = formulaEvaluator.evalDouble ("pow (7 - sqrt ([TEST]), 3)", vars);
        assertThat (res2).isEqualTo (8);
    }
    
    // Make sure we can call console.log() from within JavaScript
    @Test
    public void testLog() throws Exception {
    	final Map <String, Object> vars = new HashMap <>();
    	vars.put ("TEST", 25);
    	final double res = formulaEvaluator.evalDouble ("console.log ('Test log message: %s', 'arg1'); Math.sqrt ([TEST])", vars);
    	assertThat (res).isEqualTo (5);
    }

    // Make sure we can NOT call any other Java APIs from within JavaScript
    @Test(expected=ScriptException.class)
    public void testNoJava() throws Exception {
    	formulaEvaluator.evalDouble ("java.lang.System.out.println ('Hello')");
    }

    // Make sure we can call console.log() from within JavaScript
    @Test(expected=ScriptException.class)
    public void testBadVarRef() throws Exception {
        final Map <String, Object> vars = new HashMap <>();
        formulaEvaluator.evalDouble ("Math.sqrt ([TEST])", vars);
    }
    
    // Make sure we can call console.log() from within JavaScript
    @Test(expected=ScriptException.class)
    public void testBadVarRefNullVarMap() throws Exception {
        formulaEvaluator.evalDouble ("Math.sqrt ([TEST])");
    }
    
    // Test FormulaMath.abs()
    @Test
    public void testMath_abs() throws Exception {
        assertThat (formulaEvaluator.evalDouble("abs(2)")).isEqualTo(2);
        assertThat (formulaEvaluator.evalDouble("abs(2.0)")).isEqualTo(2);
        assertThat (formulaEvaluator.evalDouble("abs(-3)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("abs(-3.1)")).isEqualTo(3.1);
    }
    
    // Test FormulaMath.min()
    @Test
    public void testMath_min() throws Exception {
        assertThat (formulaEvaluator.evalDouble("min(22,33)")).isEqualTo(22);
        assertThat (formulaEvaluator.evalDouble("min(22,11)")).isEqualTo(11);
        assertThat (formulaEvaluator.evalDouble("min(22.01, 22.02)")).isEqualTo(22.01);
        assertThat (formulaEvaluator.evalDouble("min(22.02, 22.01)")).isEqualTo(22.01);
    }
    
    // Test FormulaMath.max()
    @Test
    public void testMath_max() throws Exception {
        assertThat (formulaEvaluator.evalDouble("max(22,33)")).isEqualTo(33);
        assertThat (formulaEvaluator.evalDouble("max(22,11)")).isEqualTo(22);
        assertThat (formulaEvaluator.evalDouble("max(22.01, 22.02)")).isEqualTo(22.02);
        assertThat (formulaEvaluator.evalDouble("max(22.02, 22.01)")).isEqualTo(22.02);
    }
    
    // Test FormulaMath.ceil()
    @Test
    public void testMath_ceil() throws Exception {
        assertThat (formulaEvaluator.evalDouble("ceil(3)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("ceiling(3)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("ceil(3.1)")).isEqualTo(4);
        assertThat (formulaEvaluator.evalDouble("ceil(-3.1)")).isEqualTo(-3);
    }
    
    // Test FormulaMath.floor()
    @Test
    public void testMath_floor() throws Exception {
        assertThat (formulaEvaluator.evalDouble("floor(3)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("floor(3.1)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("floor(-3.1)")).isEqualTo(-4);
    }
    
    // Test FormulaMath.round()
    @Test
    public void testMath_round() throws Exception {
        // precision 0
        assertThat (formulaEvaluator.evalDouble("round(3)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("round(3, 0)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("round(-3)")).isEqualTo(-3);
        assertThat (formulaEvaluator.evalDouble("round(-3, 0)")).isEqualTo(-3);
        assertThat (formulaEvaluator.evalDouble("round(125.0)")).isEqualTo(125.0);
        assertThat (formulaEvaluator.evalDouble("round(125.0, 0)")).isEqualTo(125.0);
        assertThat (formulaEvaluator.evalDouble("round(-125.0)")).isEqualTo(-125);
        assertThat (formulaEvaluator.evalDouble("round(-125.0, 0)")).isEqualTo(-125);
        
        // precision 0
        assertThat (formulaEvaluator.evalDouble("round(3.4)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("round(3.5)")).isEqualTo(4);
        assertThat (formulaEvaluator.evalDouble("round(3.8)")).isEqualTo(4);
        assertThat (formulaEvaluator.evalDouble("round(-3.4)")).isEqualTo(-3);
        assertThat (formulaEvaluator.evalDouble("round(-3.5)")).isEqualTo(-4);
        assertThat (formulaEvaluator.evalDouble("round(-3.8)")).isEqualTo(-4);

        // precision 1
        assertThat (formulaEvaluator.evalDouble("round(3.24, 1)")).isEqualTo(3.2);
        assertThat (formulaEvaluator.evalDouble("round(3.25, 1)")).isEqualTo(3.3);
        assertThat (formulaEvaluator.evalDouble("round(3.26, 1)")).isEqualTo(3.3);
        assertThat (formulaEvaluator.evalDouble("round(-3.24, 1)")).isEqualTo(-3.2);
        assertThat (formulaEvaluator.evalDouble("round(-3.25, 1)")).isEqualTo(-3.3);
        assertThat (formulaEvaluator.evalDouble("round(-3.26, 1)")).isEqualTo(-3.3);

        // precision 2
        assertThat (formulaEvaluator.evalDouble("round(2.734, 2)")).isEqualTo(2.73);
        assertThat (formulaEvaluator.evalDouble("round(2.735, 2)")).isEqualTo(2.74);
        assertThat (formulaEvaluator.evalDouble("round(2.736, 2)")).isEqualTo(2.74);
        assertThat (formulaEvaluator.evalDouble("round(-2.734, 2)")).isEqualTo(-2.73);
        assertThat (formulaEvaluator.evalDouble("round(-2.735, 2)")).isEqualTo(-2.74);
        assertThat (formulaEvaluator.evalDouble("round(-2.736, 2)")).isEqualTo(-2.74);

        // precision 3
        assertThat (formulaEvaluator.evalDouble("round(2.1114, 3)")).isEqualTo(2.111);
        assertThat (formulaEvaluator.evalDouble("round(2.1115, 3)")).isEqualTo(2.112);
        assertThat (formulaEvaluator.evalDouble("round(2.1116, 3)")).isEqualTo(2.112);
        assertThat (formulaEvaluator.evalDouble("round(-2.1114, 3)")).isEqualTo(-2.111);
        assertThat (formulaEvaluator.evalDouble("round(-2.1115, 3)")).isEqualTo(-2.112);
        assertThat (formulaEvaluator.evalDouble("round(-2.1116, 3)")).isEqualTo(-2.112);
    }
    
    // Test FormulaMath.trunc (num)
    @Test
    public void testMath_trunc_0() throws Exception {
        assertThat (formulaEvaluator.evalDouble("trunc(3)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("trunc(3.4)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("trunc(3.5)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("trunc(3.8)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("trunc(-3.4)")).isEqualTo(-3);
        assertThat (formulaEvaluator.evalDouble("trunc(-3.5)")).isEqualTo(-3);
        assertThat (formulaEvaluator.evalDouble("trunc(-3.8)")).isEqualTo(-3);
    }
    
    // Test FormulaMath.trunc (num, places)
    @Test
    public void testMath_trunc_places() throws Exception {
        assertThat (formulaEvaluator.evalDouble("trunc(3, 0)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("trunc(3, 1)")).isEqualTo(3);
        assertThat (formulaEvaluator.evalDouble("trunc(3.435, 1)")).isEqualTo(3.4);
    }
    
    // Test FormulaMath.pow()
    @Test
    public void testMath_pow() throws Exception {
        assertThat (formulaEvaluator.evalDouble("pow(2,3)")).isEqualTo(8);
        assertThat (formulaEvaluator.evalDouble("pow(-2,3)")).isEqualTo(-8);
        assertThat (formulaEvaluator.evalDouble("trunc (pow(-2.1,3), 3)")).isEqualTo(-9.261);
    }
    
    // Test FormulaMath.sqr()
    @Test
    public void testMath_sqr() throws Exception {
        assertThat (formulaEvaluator.evalDouble("sqr(3)")).isEqualTo(9);
        assertThat (formulaEvaluator.evalDouble("sqr(4.0)")).isEqualTo(16);
        assertThat (formulaEvaluator.evalDouble("sqr(-4)")).isEqualTo(16);
        assertThat (formulaEvaluator.evalDouble("sqr(-5.0)")).isEqualTo(25);
    }
    
    // Test FormulaMath.sqrt()
    @Test
    public void testMath_sqrt() throws Exception {
        assertThat (formulaEvaluator.evalDouble("sqrt(25)")).isEqualTo(5);
        assertThat (formulaEvaluator.evalDouble("sqrt(16.0)")).isEqualTo(4);
        assertThat (formulaEvaluator.evalDouble("sqrt(9.61)")).isEqualTo(3.1);
    }
    
    /**
     * This unit tests was created as a part of investigation of the following issue
     * Task 66650:FormulaEvaluator: using coma as thousands separator creates issues 
     */
    @Test
    public void testComplexFormula() throws Exception {
    	
    	final Map <String, Object> vars2 = new HashMap <>();
    	vars2.put (CostFormulaVar.SCHEDCROSSDIST.varName(), 539.5);
    	vars2.put (CostFormulaVar.MTOW.varName(), 212.0);
    	
    	String formulaStr2 = "16.32 * (([CrossDist] / 1.854) / 100) * (sqrt(([MTOW] * 907.185) / 20000))";
    	
    	final double res2 = formulaEvaluator.evalDouble (formulaStr2, vars2);
    	assertThat (res2).isEqualTo (147.26608335560903);
    	
    }     
}
