package ca.ids.abms.amhs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLRecoverableException;
import java.sql.SQLTransientException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.MoreObjects;

import ca.ids.abms.plugins.amhs.AmhsMessageConsumer;
import ca.ids.abms.plugins.amhs.AmhsPluginConfig;
import ca.ids.abms.plugins.amhs.AmhsPluginConfigLoader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Component
class InputFileWatcher {

    public InputFileWatcher (
            final AmhsPropertiesProvider amhsPropertiesProvider,
            final AftnParser aftnParser,
            final AmhsParser amhsParser,
            final AmhsPluginConfigLoader amhsPluginConfigLoader,
            final AmhsMessageConsumer consumer) {
        this.inputDirStr = amhsPropertiesProvider.getAmhsProperties().getInputDir();
        this.failedDirStr = amhsPropertiesProvider.getAmhsProperties().getFailedDir();
        this.maxFailedFileCount = amhsPropertiesProvider.getAmhsProperties().getMaxFailedFileCount();
        this.minFileLength = 20; // 20 bytes
        this.maxFileAge = 1000l * 3; // 3 seconds
        this.aftnParser = aftnParser;
        this.amhsParser = amhsParser;
        this.amhsPluginConfigLoader = amhsPluginConfigLoader;
        this.consumer = consumer;
    }

    // -------------------- private -----------------------

    /** Read and process files from inputDir (once) */
    @Scheduled(fixedDelayString = "${amhs.inputScanDelay}")
    void scheduledScan() {
        synchronized (this) {
            if (scanInProgress) {
                return;
            }
            scanInProgress = true;
        }
        try {
            processFiles();
        } finally {
            synchronized (this) {
                scanInProgress = false;
            }
        }
    }

    /** Process input files */
    private void processFiles() {
        final AmhsPluginConfig config = amhsPluginConfigLoader.getCachedConfig();

        // plugin disabled -- bail out
        if (!config.isEnabled()) {
            LOG.trace ("AMHS plugin is disabled -- skipping AMHS directory scan");
            return;
        }

        // Validate and return the plugin's input directory, or the default from app.properties
        final Path inputDir = checkDirWritable (MoreObjects.firstNonNull(config.getMessageDir(), inputDirStr));
        if (inputDir == null) {
            return;
        }

        // process message files
        processFiles (inputDir);
    }

    private void processFiles (final Path inputDir) {
        LOG.trace("scanning directory `{}'", inputDir);
        getFiles (inputDir).forEach(f -> {
            try {
                processOneFile(f);
            } catch (final Exception x) {
                if (ExceptionUtils.hasCause(x, InterruptedException.class)) {
                    throw new AmhsAgentException (x);
                } else if (ExceptionUtils.hasCause(x, SQLRecoverableException.class)
                        || ExceptionUtils.hasCause(x, SQLTransientException.class)) {
                    LOG.error("Caught transient exception, ignoring: {}", x.getMessage(), x);
                    return;
                }
                LOG.error("Caught exception: {}", x.getMessage(), x);
                processBad (f);
            }
        });
    }

    /** Process one file */
    private void processOneFile(final File f) {
        // Ignore non-files, dot-files and .tmp files
        if (f.isFile() && !f.getName().startsWith(".") && !f.getName().toLowerCase(Locale.US).endsWith(".tmp")) {

            // Is file too small?
            if (f.length() < minFileLength) {
                checkFileAge(f);
                return;
            }

            // Read it
            final byte[] bytes = slurp(f);
            final String content = new String(bytes, StandardCharsets.ISO_8859_1).trim();
            LOG.trace("Received message:\n{}\n", content);

            // Does it look like XML?
            final Matcher m = RE_START_TAG.matcher(content);
            if (m.matches()) {
                LOG.trace ("Looks like AMHS/XML format, parsing");
                final String endTag = String.format("</%s>", m.group(1));
                if (content.endsWith(endTag)) {
                    final AmhsMessage message = amhsParser.parse(bytes, content, f.getName(), utcDateTimeFromEpoch (f.lastModified()));
                    if (RE_PING.matcher(message.getBody()).find()) {
                        LOG.trace("ignoring ping message {}", message);
                        removeFile(f);
                        return;
                    }
                    consumer.consume(message);
                    removeFile(f);
                    return;
                }
                // Doesn't end with a closing tag -- maybe it's not fully written
                checkFileAge(f);
                return;
            }

            // Assume it's in AFTN format
            LOG.trace ("Looks like AFTN format, parsing");
            consumer.consume(aftnParser.parse(bytes, content, f.getName(), utcDateTimeFromEpoch (f.lastModified())));
            removeFile(f);
        }
    }

    private static LocalDateTime utcDateTimeFromEpoch (final long millis) {
        return LocalDateTime.ofInstant (Instant.ofEpochMilli (millis), ZoneOffset.UTC);
    }

    private static final Pattern RE_START_TAG = Pattern.compile("^<([a-zA-Z_-]+)\\b.*$", Pattern.DOTALL);

    /** Move a bad file into the failed directory */
    private void processBad(final File f) {
        try {
            final Path failedDir = checkDirWritable (failedDirStr);
            if (failedDir != null) {
                moveFile (f, failedDir);
                deleteExtraFiles (failedDir, maxFailedFileCount);
            }
        }
        catch (final AmhsAgentException x) {
            removeFile (f);
        }
    }

    private void moveFile (final File f, final Path targetDir) {
        LOG.error("moving file `{}' to directory `{}'", f, targetDir);
        final Path target = targetDir.resolve(f.getName());
        try {
            Files.move(f.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (final NoSuchFileException | FileNotFoundException x) {
            // no-op
        } catch (final Exception x) {
            throw new AmhsAgentException (x);
        }
    }

    /** Remove a message file after processing */
    private void removeFile(final File f) {
        LOG.debug("removing file `{}'", f);
        try {
            Files.delete(f.toPath());
        } catch (final NoSuchFileException | FileNotFoundException x) {
            // no-op
        } catch (final Exception x) {
            throw new AmhsAgentException (x);
        }
    }

    /** Return all file names from inputDir sorted by name */
    private static List<File> getFiles (final Path inputDir) {
        final List<File> list = new ArrayList<>();
        scan(inputDir, p -> {
            final File f = p.toFile();
            if (f.isFile() && !f.getName().startsWith(".") && !f.getName().toLowerCase(Locale.US).endsWith(".tmp")) {
                list.add(f);
                return true;
            }
            if (f.isFile()) {
                LOG.trace ("skipping file {}", f);
            }
            return true;
        });
        Collections.sort(list);
        LOG.trace ("found {} file(s)", list.size());
        return list;
    }

    /** If given file is too old, throw an exception */
    private void checkFileAge(final File f) {
        final long ageMs = Instant.now().toEpochMilli() - f.lastModified();
        if (ageMs > maxFileAge) {
            throw new AmhsAgentException (String.format("%s: invalid file format", f));
        }
    }

    /** Read file to a string */
    private byte[] slurp(final File f) {
        LOG.debug("reading {}", f);
        try {
            return FileUtils.readFileToByteArray(f);
        } catch (final IOException x) {
            throw new AmhsAgentException (x);
        }
    }

    /** Make sure input directory is readable and writable */
    public Path ensureDirExists (final String dirStr) {
        final Path dir = Paths.get (dirStr);
        if (!dir.toFile().isDirectory()) {
            throw new AmhsAgentException (String.format ("%s: directory not found", dir));
        }
        // Make sure we can read it
        scan(dir, p -> false);
        // Make sure we can write it
        if (!dir.toFile().canWrite()) {
            throw new AmhsAgentException (String.format("%s: directory is not writeable", dir));
        }
        return dir;
    }

    /**
     * Read each filename from a directory and call the provided function, stop if
     * it returns false
     */
    public static void scan(final Path dir, final Function<Path, Boolean> callback) {
        if (dir.toFile().isDirectory()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (final Path p : stream) {
                    if (!callback.apply(p)) {
                        break;
                    }
                }
            } catch (final IOException | DirectoryIteratorException x) {
                throw new AmhsAgentException (x);
            }
        }
    }

    /** Remove extra files from a directory */
    public void deleteExtraFiles(final Path dir, final int keep) {
        if (dir.toFile().isDirectory()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                final List<File> list = new ArrayList<>();
                for (final Path p : stream) {
                    final File f = p.toFile();
                    if (!f.getName().startsWith(".") && !f.isDirectory()) {
                        list.add(f);
                    }
                }
                // sort by modification date in reverse order
                list.sort((a, b) -> -1 * Long.valueOf(a.lastModified()).compareTo(b.lastModified()));
                // Delete extra files
                if (list.size() > keep) {
                    for (int i = keep; i < list.size(); ++i) {
                        final File f = list.get(i);
                        LOG.info("removing {}", f);
                        Files.deleteIfExists (f.toPath());
                    }
                }
            } catch (final IOException | DirectoryIteratorException x) {
                throw new AmhsAgentException (x);
            }
        }
    }

    private Path checkDirWritable (final String dirStr) {
        final Path dir = Paths.get (dirStr);
        if (!dir.toFile().isDirectory()) {
            LOG.error ("{}: directory not found", dir);
            return null;
        }
        // Make sure we can write it
        if (!dir.toFile().canWrite()) {
            LOG.error ("{}: permission denied", dir);
            return null;
        }
        // Make sure we can read it
        try {
            scan(dir, p -> false);
        }
        catch (final AmhsAgentException x) {
            LOG.error ("{}: {}", dir, x.getMessage(), x);
            return null;
        }
        return dir;
    }

    private static final Logger LOG = LoggerFactory.getLogger(InputFileWatcher.class);
    private static final Pattern RE_PING = Pattern.compile("^X-PING-SELF");
    private volatile boolean scanInProgress = false;
    private final String inputDirStr;
    private final String failedDirStr;
    private final int maxFailedFileCount;
    private final long minFileLength;
    private final long maxFileAge;
    private final AftnParser aftnParser;
    private final AmhsParser amhsParser;
    private final AmhsPluginConfigLoader amhsPluginConfigLoader;
    private final AmhsMessageConsumer consumer;

}
