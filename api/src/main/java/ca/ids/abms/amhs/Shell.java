package ca.ids.abms.amhs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class Shell {

    @Inject
    public Shell(final AmhsPropertiesProvider amhsPropertiesProvider) {
        this.shellArgs = createShellArgs (amhsPropertiesProvider.getAmhsProperties().getServerShell());
        LOG.trace("serverShell: {}", shellArgs);
    }
    
    
    public int run(final String cmdName, final String cmd) {
        return run (cmdName, cmd, false, null, null);
    }
    
    public int run(final String cmdName, final String cmd, boolean quiet) {
        return run (cmdName, cmd, quiet, null, null);
    }
    
    private interface ThrowingRunnable {
        @SuppressWarnings ("squid:S00112")
        void run() throws Exception;
    }
    private void ignoreIoException (final ThrowingRunnable callback) {
        try {
            callback.run();
        }
        catch (IOException x) {
            // no-op
        }
        catch (final Exception x) {
            throw new ShellException (x);
        }
    }
    
    @SuppressWarnings ("squid:S2142")
    public int run (final String cmdName, final String cmd, boolean quiet, final String input, final StringBuilder output) {
        final List<String> args = args(cmd);
        try {
            final String logPrefix = String.format("(%s)", cmdName);
            final QuietLogger quietLogger = new QuietLogger (quiet);
            quietLogger.log ("{} executing: {}", logPrefix, ShellQuote.quote(args));
            final Process p = new ProcessBuilder(args)
                    .redirectErrorStream(true)
                    .start();
            try {
                return waitChild (quietLogger, logPrefix, input, output, p);
            } catch (final InterruptedException x) {
                throw new ShellException(x);
            } finally {
                destroyChild (p);
            }
        } catch (final IOException x) {
            throw new ShellException (x);
        }
    }
    
    // ----------------------- private ---------------------------
    
    private void destroyChild (final Process p) {
        p.destroy();
        try {
            p.waitFor();
        } catch (final InterruptedException x) {
            Thread.currentThread().interrupt();
            throw new ShellException(x);
        }
    }
    
    private int waitChild (final QuietLogger quietLogger, final String logPrefix, final String input, final StringBuilder output, final Process p) throws InterruptedException, IOException {
        // feed input to the command
        final Reader childStdinReader = new StringReader (input == null ? "" : input);
        final Writer childStdinWriter = new OutputStreamWriter (p.getOutputStream(), StandardCharsets.UTF_8);
        final String childStdinLogPrefix = String.format ("%s >> ", logPrefix);
        copy (quietLogger, childStdinLogPrefix, childStdinReader, childStdinWriter);
        ignoreIoException (childStdinWriter::flush);
        ignoreIoException (p.getOutputStream()::close);
        
        // read all output
        final Reader childStdoutReader = new InputStreamReader (p.getInputStream(), StandardCharsets.UTF_8);
        final StringWriter childStdoutWriter = new StringWriter();
        final String childStdoutLogPrefix = String.format ("%s << ", logPrefix);
        final String outputStr = copy (quietLogger, childStdoutLogPrefix, childStdoutReader, childStdoutWriter);
        if (output != null) {
            output.replace (0, output.length(), outputStr);
        }
        ignoreIoException (p.getInputStream()::close);
        
        // wait for it
        int exitStatus = p.waitFor();
        quietLogger.log ("{} exited with status {}", logPrefix, exitStatus);
        return exitStatus;
    }
    
    private class QuietLogger {
        final boolean quiet;
        public QuietLogger (final boolean quiet) {
            this.quiet = quiet;
        }
        public void log (final String msg, Object... args) {
            if (quiet) {
                LOG.trace (msg, args);
            }
            else {
                LOG.debug (msg, args);
            }
        }
    }
    
    private String copy (final QuietLogger quietLogger, final String logPrefix, final Reader r, final Writer w) throws IOException {
        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = nextLine (r)) != null) {
            quietLogger.log ("{}{}", logPrefix, rtrim (line));
            w.write (line);
            buf.append (line);
        }
        return buf.toString();
    }
    
    @SuppressWarnings ("squid:S135")
    private String nextLine (final Reader r) throws IOException {
        final StringBuilder buf = new StringBuilder();
        while (true) {
            final int c = r.read();
            if (c < 0) {
                if (buf.length() <= 0)
                    return null;
                break;
            }
            buf.append ((char)c);
            if (c == (int)'\n')
                break;
        }
        return buf.toString();
    }
    
    private List<String> args(final String shellCommand) {
        final List<String> args = new ArrayList<>(shellArgs);
        args.add(shellCommand);
        return args;
    }

    private static List<String> createShellArgs(final String shell) {
        final List<String> args = ShellQuote.unquote(shell);
        if (args.isEmpty()) {
            if (SystemUtils.IS_OS_WINDOWS) {
                return Arrays.asList("cmd", "/c");
            }
            return Arrays.asList("/bin/sh", "-c");
        }
        return args;
    }
    
    private static final Pattern RE_RWS = Pattern.compile ("\\s+$");
    private static String rtrim (final String s) {
        return RE_RWS.matcher(s).replaceAll ("");
    }

    private final List<String> shellArgs;
    private static final Logger LOG = LoggerFactory.getLogger(Shell.class);
}
