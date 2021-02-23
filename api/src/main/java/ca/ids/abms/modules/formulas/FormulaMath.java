package ca.ids.abms.modules.formulas;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.script.ScriptEngine;

import com.google.common.base.Preconditions;

public class FormulaMath {
    
    public Number abs (final Number num) {
        return Math.abs(num.doubleValue());
    }

    public Number min (final Number arg1, Number arg2) {
        return Math.min (arg1.doubleValue(), arg2.doubleValue());
    }
    
    public Number max (final Number arg1, Number arg2) {
        return Math.max (arg1.doubleValue(), arg2.doubleValue());
    }

    public Number ceil (final Number num) {
        return Math.ceil(num.doubleValue());
    }
    
    public Number floor (final Number num) {
        return Math.floor(num.doubleValue());
    }
    
    public Number round (final Number num) {
        return round (num, 0d);
    }
    public Number round (final Number num, final Number places) {
        Preconditions.checkNotNull (num);
        final int sign = num.doubleValue() >= 0d ? 1 : -1;
        double res;
        if (places != null && places.intValue() > 0) {
            final double factor = Math.pow (10.0, places.intValue());
            res = sign * (Math.round (Math.abs (num.doubleValue()) * factor) / factor);
        }
        else {
            res = sign * Math.round(Math.abs (num.doubleValue()));
        }
        return res;
    }
    
    public Number pow (final Number base, final Number exponent) {
        return Math.pow (base.doubleValue(), exponent.doubleValue());
    }

    public Number sqr (final Number num) {
        return Math.pow (num.doubleValue(), 2);
    }
    
    public Number sqrt (final Number num) {
        return Math.sqrt(num.doubleValue());
    }
    
    public Number trunc (final Number num) {
        return trunc (num, 0);
    }
    public Number trunc (final Number num, final Number places) {
        Preconditions.checkNotNull (num);
        if (places != null && places.intValue() > 0) {
            final int intPlaces = places.intValue();
            return new BigDecimal (num.doubleValue())
                    .setScale (intPlaces, RoundingMode.DOWN)
                    .stripTrailingZeros()
                    .doubleValue()
            ;
        }
        return new Double (num.longValue());
    }
    
    static void addBindings (final ScriptEngine e) {
        e.put("__FormulaMath", new FormulaMath());
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
