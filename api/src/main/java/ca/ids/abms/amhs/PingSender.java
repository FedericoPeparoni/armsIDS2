package ca.ids.abms.amhs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import ca.ids.abms.amhs.parsers.amhs.RawAmhsFormatter;
import ca.ids.abms.amhs.parsers.amhs.RawAmhsMessage;
import ca.ids.abms.atnmsg.amhs.addr.AmhsAddress;
import ca.ids.abms.atnmsg.amhs.addr.AmhsAddressCreator;
import ca.ids.abms.atnmsg.amhs.addr.InvalidAmhsAddressException;
import ca.ids.abms.modules.amhsconfiguration.AmhsAccount;
import ca.ids.abms.modules.amhsconfiguration.AmhsConfiguration;

@Component
class PingSender {

    public PingSender(
            final AmhsPropertiesProvider amhsPropertiesProvider,
            final AmhsAgentManager amhsAgentManager,
            final AmhsAgentDatabaseHelper amhsAgentDatabaseHelper,
            final AmhsAddressCreator amhsAddressCreator) {
        this.outputDir = checkWritableDir (amhsPropertiesProvider.getAmhsProperties().getOutputDir());
        this.forcePing = amhsPropertiesProvider.getAmhsProperties().getForcePing();
        this.amhsAgentManager = amhsAgentManager;
        this.amhsAgentDatabaseHelper = amhsAgentDatabaseHelper;
        this.amhsAddressCreator = amhsAddressCreator;
        this.pingConfigCache = createPingConfigCache();
    }
    
    public void clearCache() {
        this.pingConfigCache.invalidateAll();
    }

    // ------------------ private --------------------------------

    @Scheduled(fixedDelay = 5000)
    protected void sendPing() {
        synchronized (sendPingMutex) {
            if (inSendPing) {
                return;
            }
            inSendPing = true;
        }
        try {
            final List <Path> oldPingFiles = this.findPingMessageFiles();
            final boolean amhsAgentInstalled = amhsAgentManager.isInstalledLocally();
            
            // No old files, amhs agent not installed and forcePing == false: bail out
            if (oldPingFiles.isEmpty() && !amhsAgentInstalled && !forcePing) {
                return;
            }

            // Get config
            final PingConfig pingConfig = getPingConfig();
            
            // delete old files
            final List<AccountInfo> accountsRequiringPing = deleteOldPingMessages (oldPingFiles, pingConfig.accounts, 30);
            
            // create new ping files
            if (pingConfig.pingEnabled && (forcePing || amhsAgentInstalled)) {
                final LocalDateTime now = LocalDateTime.now();
                final long secondsSinceLastPing = secondsBetween(lastPingDateTime, now);
                if (secondsSinceLastPing >= pingConfig.pingDelaySeconds) {
                    if (forcePing || amhsAgentManager.isStartedLocally()) {
                        accountsRequiringPing.forEach(this::sendPing);
                    }
                    this.lastPingDateTime = now;
                }
            }
            
        } finally {
            inSendPing = false;
        }
    }

    private LoadingCache <Boolean, PingConfig> createPingConfigCache() {
        final CacheLoader <Boolean, PingConfig> loader = new CacheLoader <Boolean, PingConfig>() {
            @Override
            public PingConfig load(Boolean key) throws Exception {
                final List <AccountInfo> accounts = findAllAccounts();
                final AmhsConfiguration x = amhsAgentDatabaseHelper.getActiveConfiguration();
                if (x != null) {
                    return new PingConfig (x.getPingEnabled(), x.getPingDelay(), accounts);
                }
                return new PingConfig (false, 0, accounts);
            }
        };
        return CacheBuilder.newBuilder().expireAfterWrite (30, TimeUnit.SECONDS).build (loader);
    }
    
    private PingConfig getPingConfig() {
        return pingConfigCache.getUnchecked (true);
    }

    private static final long secondsBetween(final LocalDateTime lo, final LocalDateTime hi) {
        return hi.toInstant(ZoneOffset.UTC).getEpochSecond() - lo.toInstant(ZoneOffset.UTC).getEpochSecond();
    }

    /** Account + (parsed) AMHS address */
    private static class AccountInfo {
        final AmhsAccount account;
        final AmhsAddress addr;
        final String pingFilenameSuffix;

        @Override
        public boolean equals(final Object o) {
            if (o instanceof AccountInfo) {
                return Objects.equals(account, ((AccountInfo) o).account);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return account == null ? 0 : account.hashCode();
        }

        @Override
        public String toString() {
            return "AccountInfo [" + (account != null ? "account=" + account + ", " : "")
                    + (pingFilenameSuffix != null ? "pingFilenameSuffix=" + pingFilenameSuffix : "") + "]";
        }

        AccountInfo(final AmhsAccount account, final AmhsAddress addr) {
            this.account = account;
            this.addr = addr;
            this.pingFilenameSuffix = createPingFilenameSuffix(addr);
        }
        
        /** Create a ping filename suffix for the given AMHS address */
        private static String createPingFilenameSuffix(final AmhsAddress from) {
            return String.format("_ping_%s.mta", from.toAftnString());
        }
    }

    /** Get all accounts with valid AMHS addresses from the database */
    private List<AccountInfo> findAllAccounts() {
        return amhsAgentDatabaseHelper.getAllAccounts().stream().map(e -> {
            try {
                return new AccountInfo(e, amhsAddressCreator.parseUbimexString(e.getAddr()));
            } catch (final InvalidAmhsAddressException x) {
                LOG.error("Invalid address in AMHS account {}", e, x);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Delete existing ping messages that are too old. Returns the list of accounts
     * that need new ping messages created.
     */
    private List<AccountInfo> deleteOldPingMessages(
            final List <Path> oldPingFiles,
            final List<AccountInfo> accounts,
            final int pingFileLifetimeSeconds) {
        final Set<AccountInfo> pingNotRequired = new HashSet<>();
        for (Path p : oldPingFiles) {
            boolean keepFile = false;
            for (final AccountInfo a : accounts) {
                if (p.toFile().getName().endsWith(a.pingFilenameSuffix) && !isOlderThan(p, pingFileLifetimeSeconds)) {
                    pingNotRequired.add(a);
                    keepFile = true;
                    break;
                }
            }
            if (!keepFile) {
                LOG.trace("deleting old ping file `{}'", p);
                deleteFile(p);
            }
        }
        return accounts.stream().filter(x -> x.account.getActive() && !pingNotRequired.contains(x)).collect(Collectors.toList());
    }

    /** Check whether the given file is older than N seconds */
    private boolean isOlderThan(final Path p, int seconds) {
        final long cutoff = (Instant.now().getEpochSecond() - (long) seconds) * 1000l;
        try {
            final long lastMod = p.toFile().lastModified();
            return lastMod < cutoff;
        } catch (final SecurityException x) {
            LOG.warn("Error accessing file {}", p, x);
            return false;
        }
    }

    /** Delete a file; log and ignore IO errors */
    private static void deleteFile(final Path p) {
        try {
            Files.deleteIfExists (p);
        } catch (final IOException x) {
            LOG.warn("Error accessing file {}", p, x);
        }
    }

    /** Create a ping message file */
    private void sendPing(final AccountInfo accountInfo) {
        final LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
        final byte[] content = formatPing(utcNow, accountInfo);
        final String filename = createPingFilename(utcNow, accountInfo);
        saveOutputFile(filename, content);
    }

    /** Create a ping filename for the given AMHS address */
    private static String createPingFilename(final LocalDateTime utcNow, final AccountInfo from) {
        final String timestamp = utcNow.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return timestamp + from.pingFilenameSuffix;
    }

    /** Format a ping message -- to be saved to a file */
    private byte[] formatPing(final LocalDateTime utcNow, final AccountInfo from) {
        final RawAmhsMessage m = new RawAmhsMessage();
        m.setSkipTransmit(true);
        m.setBody("X-PING-SELF " + utcNow.toString());
        m.setBodyId("X-PING-SELF#" + utcNow.toString());
        m.setCreationDate(new Date(utcNow.toInstant(ZoneOffset.UTC).toEpochMilli()));
        m.setDestAddrList(Arrays.asList(from.addr.toString()));
        m.setOriginatorAddr(from.addr.toString());
        m.setPriority("GG");
        RawAmhsFormatter f = new RawAmhsFormatter();
        return f.format(m);
    }

    /** Save message file to outputDir; log and ignore IO errors */
    @SuppressWarnings("squid:S4087")
    private void saveOutputFile(final String filename, final byte[] content) {
        final Path path = this.outputDir.resolve(filename);
        final Path tmpPath = this.outputDir.resolve(filename + ".tmp");
        LOG.trace("creating ping file `{}'", path);
        try (final FileOutputStream f = new FileOutputStream(tmpPath.toString())) {
            f.write(content);
            f.close();
            Files.move(tmpPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException | SecurityException x) {
            LOG.error("{}", x.getMessage(), x);
        }
    }

    /** Make sure input directory is readable and writable */
    public static Path checkWritableDir(final String dirStr) {
        final Path dir = Paths.get(dirStr);
        if (dir.toFile().isDirectory()) {
            // Make sure we can read it
            scan(dir, p -> false);
            // Make sure we can write it
            if (!dir.toFile().canWrite()) {
                throw new AmhsAgentException (String.format("%s: directory is not writeable", dir));
            }
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
                throw new AmhsAgentException(x);
            }
        }
    }

    /** Find all existing output ping files */
    public List<Path> findPingMessageFiles() {
        final List<Path> files = new ArrayList<>();
        scan(outputDir, p -> {
            if (RE_PING_FILENAME.matcher(p.toFile().getName()).matches()) {
                files.add(p);
            }
            return true;
        });
        return files;
    }

    /** Ping parameters */
    private static class PingConfig {
        final boolean pingEnabled;
        final int pingDelaySeconds;
        final List <AccountInfo> accounts;
        public PingConfig (final boolean pingEnabled, final int pingDelaySeconds, final List <AccountInfo> accounts) {
            this.pingEnabled = pingEnabled;
            this.pingDelaySeconds = pingDelaySeconds;
            this.accounts = accounts;
        }
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(PingSender.class);
    private static final Pattern RE_PING_FILENAME = Pattern.compile("^\\d{14}_ping_[A-Z0-9]{8}[.]mta$");
    private final AmhsAgentManager amhsAgentManager;
    private final AmhsAgentDatabaseHelper amhsAgentDatabaseHelper;
    private final AmhsAddressCreator amhsAddressCreator;
    private final Path outputDir;
    private boolean forcePing;
    private final Object sendPingMutex = new Object();
    private volatile boolean inSendPing = false;
    private volatile LocalDateTime lastPingDateTime = LocalDateTime.now();
    private final LoadingCache <Boolean, PingConfig> pingConfigCache;
}
