package ca.ids.abms.modules.formulas;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;

import com.google.common.base.Preconditions;

@Service
class FormulaEvaluatorImpl implements FormulaEvaluator {
	
	public FormulaEvaluatorImpl (final JavascriptEngineFactory javascriptEngineFactory) {
		this.javascriptEngineFactory = javascriptEngineFactory;
	}
	
    @Override
    public double evalDouble (final String formula) throws Exception {
        return (Double)do_eval (formula, null);
    }
    
    @Override
    public double evalDouble (final String formula, final Map <String, Object> vars) throws Exception {
        final Number number = (Number)do_eval (formula, vars);
        return number.doubleValue();
    }
    
	public Number evalNumeric (final String formula) throws Exception {
		return (Number)do_eval (formula, null);
	}
	
	public Number evalNumeric (final String formula, final Map <String, Object> vars) throws Exception {
		return (Number)do_eval (formula, vars);
	}
	
	public Object eval (final String formula) throws Exception {
		return do_eval (formula, null);
	}
	
	public Object eval (final String formula, final Map <String, Object> vars) throws Exception {
		return do_eval (formula, vars);
	}
	
	private Object do_eval (final String script, final Map <String, Object> vars) throws Exception {
		Preconditions.checkNotNull (script);
		final ScriptEngine e = do_createScriptEngine();
		if (vars != null) {
		    final Map <String, Object> dollarVars = convertVarNames (vars);
		    dollarVars.keySet().stream().filter((k)->k != null).forEach((k)->{
				e.put (k, dollarVars.get (k));
			});
		};
		FormulaMath.addBindings(e);
		final String prologue = FormulaMath.getJsPrologue();
		final String dollarScript = convertVarRefs (script, vars);
		final String fullScript = prologue + "\n" + dollarScript;
		logger.trace ("Evaluating formula \"{}\" ; vars={}", script, vars);
		
		Object result = e.eval (fullScript); 
		if (result.equals(Double.NaN)) {
			throw new CustomParametrizedException("Formula returns NaN value");	
		}
		return result;
	}
	
    /*
	private static String createJsPrologue (final String obj) {
	    final StringBuilder buf = new StringBuilder();
        buf.append ("console.log ('obj = ' + typeof (" + obj + "))\n");
	    buf.append ("for (var __p in Object.keys (" + obj + ")) {\n");
	    buf.append ("  console.log ('__p = ' + typeof (__p))\n");
	    buf.append ("  if (typeof " + obj + "[__p] == 'function') {\n");
	    buf.append ("    this[__p] = (function() {\n");
	    buf.append ("      var __m = " + obj + "[__p]\n");
        buf.append ("      return function() {\n");
        buf.append ("        return __m.apply (" + obj + ", arguments)\n");
        buf.append ("      }\n");
        buf.append ("    })()\n");
        buf.append ("  }\n");
	    buf.append ("}\n");
	    return buf.toString();
	}
	*/
	
	private static final Pattern RE_VAR_REF = Pattern.compile ("\\[\\s*?([a-zA-Z][a-zA-Z0-9_]*)\\s*?\\]");
	private static String convertVarRefs (final String script, final Map <String, Object> vars) throws Exception {
	    final Matcher m = RE_VAR_REF.matcher (script);
	    final StringBuilder buf = new StringBuilder();
	    int start = 0;
	    while (m.find (start)) {
            buf.append (script.subSequence(start, m.start()));
            final String var = m.group(1);
            if (vars == null || !vars.containsKey(var)) {
                throw new ScriptException ("Invalid or undefined variable \"" + m.group() + "\"");
            };
            buf.append ("$" + var);
            start = m.end();
            continue;
	    };
        buf.append (script.subSequence(start, script.length()));
        return buf.toString();
	}
	private static Map <String, Object> convertVarNames (final Map <String, Object> srcMap) {
	    if (srcMap != null) {
	        final Map <String, Object> dstMap = new HashMap<> ();
	        for (final String srcKey: srcMap.keySet()) {
	            final String dstKey = "$" + srcKey;
	            final Object value = srcMap.get (srcKey);
	            dstMap.put (dstKey, value);
	        }
	        return dstMap;
	    }
	    return null;
	}
	
	private ScriptEngine do_createScriptEngine() {
        final ScriptEngine e = javascriptEngineFactory.createScriptEngine();
        return e;
	}

	private final Logger logger = LoggerFactory.getLogger (FormulaEvaluatorImpl.class);
	private JavascriptEngineFactory javascriptEngineFactory;
	
}
