package ca.ids.abms.modules.formulas.unifiedtax;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.script.ScriptEngine;

import com.google.common.base.Preconditions;

public class FormulaMathUT {
    
  
    static void addBindings (final ScriptEngine e) {
        e.put("__FormulaMath", new FormulaMathUT());
    }
    
    static String getJsPrologue() {
        return
                "function abs(n)         { return __FormulaMath.abs (n) }\n" +
                "function min(n1, n2)    { return __FormulaMath.min (n1, n2) }\n" +
                "function max(n1, n2)    { return __FormulaMath.max (n1, n2) }\n" +
                "function ceil(n)        { return __FormulaMath.ceil (n) }\n" +
                "function ceiling(n)     { return __FormulaMath.ceil (n) }\n" +
                "function floor(n)       { return __FormulaMath.floor (n) }\n" +
                "function round(n, p)    { return __FormulaMath.round (n, p) }\n" +
                "function pow(n, e)      { return __FormulaMath.pow (n, e) }\n" +
                "function sqr(n)         { return __FormulaMath.sqr (n) }\n" +
                "function sqrt(n)        { return __FormulaMath.sqrt (n) }\n" +
                "function trunc(n, p)    { return __FormulaMath.trunc (n, p) }\n" +
                "function truncate(n, p) { return __FormulaMath.trunc (n, p) }\n" +
                ""
        ;
    }
}
