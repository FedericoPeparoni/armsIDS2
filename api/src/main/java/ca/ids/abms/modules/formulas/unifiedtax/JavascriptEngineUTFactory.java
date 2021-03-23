package ca.ids.abms.modules.formulas.unifiedtax;

import javax.script.ScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

@SuppressWarnings("restriction")
@Component
public
class JavascriptEngineUTFactory {
	
	public JavascriptEngineUTFactory() {
		scriptEngineFactory = new NashornScriptEngineFactory();
		logger.info ("Found JavaScript interpreter for formulas: {} {} THREADING={}",
				scriptEngineFactory.getParameter (ScriptEngine.ENGINE),
				scriptEngineFactory.getParameter (ScriptEngine.ENGINE_VERSION),
				scriptEngineFactory.getParameter ("THREADING"));
	}
	
	public ScriptEngine createScriptEngine() {
		final ScriptEngine e = scriptEngineFactory.getScriptEngine(new String[] { "--no-java", "--no-syntax-extensions" });
		//final ScriptEngine e = scriptEngineFactory.getScriptEngine();
		e.put ("console", new Console (logger));
		return e;
	}
	
	public static class Console {
		private final Logger logger;
		public Console (final Logger logger) {
			this.logger = logger;
		}
		public void debug (final String fmt, Object... args) {
			log (fmt, args);
		}
		public void info (final String fmt, Object... args) {
			log (fmt, args);
		}
		public void log (final String fmt, Object... args) {
			if (fmt != null) {
				logger.debug ("{}", String.format (fmt, args));
			}
		}
	}

	private final Logger logger = LoggerFactory.getLogger (JavascriptEngineUTFactory.class);
	private final NashornScriptEngineFactory scriptEngineFactory;

}
