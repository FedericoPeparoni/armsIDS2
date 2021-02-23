package ca.ids.abms.modules.common.services;

import ca.ids.abms.config.ContentTypes;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.dataimport.DataImportType;
import ca.ids.abms.modules.uploadedfiles.UploadedFileService;
import ca.ids.abms.util.ssh.AbmsScp;
import com.emc.ecs.nfsclient.nfs.io.*;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import com.emc.ecs.nfsclient.rpc.Credential;
import com.emc.ecs.nfsclient.rpc.CredentialUnix;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.Util;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tools.ant.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.ids.xyzmodem.ZModem;
import ca.ids.xyzmodem.util.CustomFile;
import ca.ids.xyzmodem.util.FileAdapter;
import ca.ids.xyzmodem.xfer.zm.packet.Format;
import ca.ids.xyzmodem.xfer.zm.packet.Header;
import ca.ids.xyzmodem.xfer.zm.util.ZMOptions;
import ca.ids.xyzmodem.xfer.zm.util.ZModemCharacter;
import ca.ids.xyzmodem.zm.io.ZMPacketOutputStream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public abstract class AbstractDataImporter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDataImporter.class);

    private static final String PROTOCOL_FILE = "file";
    private static final String PROTOCOL_FTP = "ftp";
    private static final String PROTOCOL_NFS = "nfs";
    private static final String PROTOCOL_SCP = "scp";
    private static final String PROTOCOL_SFTP = "sftp";
    private static final String PROTOCOL_ZR = "zr";
    private static final String PROTOCOL_ZM = "zm";
    private static final String PROTOCOL_ZMODEM = "zmodem";
    private static final Integer DEFAULT_SOCKET_PORT = 0;

    private final List<ContentTypes> contentTypes = Arrays.asList(ContentTypes.CSV, ContentTypes.TXT, ContentTypes.XLS,
        ContentTypes.XLSX, ContentTypes.OCTET_STREAM);

    private final DataImportType dataImportType;

    private final UploadedFileService uploadedFileService;

    private ArrayList<File> removableDirectories;

    public AbstractDataImporter(UploadedFileService uploadedFileService, DataImportType dataImportType) {
        this.uploadedFileService = uploadedFileService;
        this.dataImportType = dataImportType;
        this.removableDirectories = new ArrayList<>();
    }

    /**
     * Starting point, use to import files from system configuration settings based on
     * data import type.
     */
    public List<BulkLoaderSummary> doImport() throws IOException {

        try {

            // get valid location from system configuration settings
            URI location = this.doFindLocation();

            // validate that location exists
            if (location == null) {
                LOG.trace("{} - Could NOT find a location string.", this.dataImportType.getValue());
                return Collections.emptyList();
            }

            // validate that location scheme exists
            if (location.getScheme() == null || location.getScheme().isEmpty()) {
                LOG.error("{} - Location string must contain a protocol: {}",
                    this.dataImportType.getValue(), location);
                return Collections.emptyList();
            }

            LOG.trace("{} - Looking for files within {}.", this.dataImportType.getValue(), location);

            // get all files within location based on location type (FILE, SCP, FTP, HTTP, ETC..)
            File[] files;
            switch (location.getScheme().toLowerCase()){
                case PROTOCOL_FILE:
                    files = this.doFindFilesFileSystem(location);
                    break;
                case PROTOCOL_FTP:
                    files = this.doFindFilesFtpProtocol(location);
                    break;
                case PROTOCOL_NFS:
                    files = this.doFindFilesNfsProtocol(location);
                    break;
                case PROTOCOL_SCP:
                case PROTOCOL_SFTP:
                    files = this.doFindFilesSshProtocol(location);
                    break;
                case PROTOCOL_ZR:
                case PROTOCOL_ZM:
                case PROTOCOL_ZMODEM:
                    files = this.doFindFilesModemProtocol(location);
                    break;
                default:
                    LOG.error("{} - System does not support location strings with a {} protocol.",
                        this.dataImportType.getValue(), location.getScheme());
                    return Collections.emptyList();
            }

            // if not files, continue to next data import type
            if (files == null || files.length == 0) {
                LOG.trace("{} - Could NOT find any files within {}.", this.dataImportType.getValue(), location);
                return Collections.emptyList();
            }

            // create list to hold results
            List<BulkLoaderSummary> results = new ArrayList<>();

            // for each file, validate and process with bulk upload service
            for(File file : files) {
                try {

                    // process file and get result of upload
                    BulkLoaderSummary result = this.doProcessFile(file);

                    // if result is null, nothing was uploaded
                    if (result != null)
                        results.add(result);

                } catch (Exception ex) {
                    LOG.error("{} - File {} within {} could not be uploaded because: {}", this.dataImportType.getValue(),
                        file.getName(), location, ex.getMessage());
                    doHandleException(file, ex);
                }
            }

            // return results of upload
            return results;

        } finally {

            // clean up all temporary directories if any when finished
            this.doCleanRemovableDirectories(removableDirectories);
        }
    }

    /**
     * Deletes location directories and content recursively. Used for temporary directories that should be
     * removed/cleaned after processing their contents.
     *
     * @param locations list of directory locations
     */
    private void doCleanRemovableDirectories(ArrayList<File> locations) {

        // iterate through each location provided
        Iterator<File> i = locations.iterator();
        while (i.hasNext()) {
            File location = i.next();
            try {

                // attempt to delete directory or file
                Files.walkFileTree(location.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path directory, IOException ex) throws IOException {
                        Files.delete(directory);
                        return FileVisitResult.CONTINUE;
                    }
                });

            } catch (IOException ex) {
                LOG.error("{} - Temporary directory '{}' within '{}' could not be deleted after import: {}",
                    this.dataImportType.getValue(), location.getName(), location.getPath(), ex.getMessage());
                continue;
            }

            // remove whether successful or not at deleting
            i.remove();
        }
    }

    /**
     * Creates a removable temporary directory and if successful.
     *
     * @param prefix temporary directory prefix
     * @return File - temporary directory
     */
    private File doCreateRemovableDirectory(String prefix) {

        try {

            // create temporary directory to hold files
            File directory = Files.createTempDirectory(prefix).toFile();

            // add temporary directory to removable directories list
            // this will allow it to be removed once we are finished with it
            this.removableDirectories.add(directory);

            // require that the directory be deleted on exit/shutdown as a fallback
            // useful if process exits unexpectedly and this flag will prevent the file from presenting indefinitely
            // or until the user clears the temporary directory of their operating system
            directory.deleteOnExit();

            // confirm directory exists and readable
            if (!directory.exists() || !directory.canRead() || !directory.isDirectory()) {
                LOG.error("File temp directory '{}' does not exists, is not a directory or system does not have read permission.",
                    directory.toPath());
                return null;
            }

            // return newly created temporary directory
            return directory;

        } catch (IOException ex) {
            LOG.error("Temporary directory for '{}' could not be created. System may not have write permission",
                this.dataImportType.getValue());
            return null;
        }
    }

    /**
     * Get list of files within a directory on the local file system. Will return null if readable directory could
     * not be found or an empty array of files if no readable files could be found.
     *
     * @param uri directory of files
     * @return readable files in directory
     */
    private File[] doFindFilesFileSystem(URI uri) {

        // if scheme is not file, log error and return null
        if (uri.getScheme() == null || uri.getScheme().isEmpty() ||
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_FILE)) {
            LOG.error("{} - Mishandled protocol '{}:' as a 'file:' protocol.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // get directory from local file system
        File directory = new File(uri);

        // confirm directory exists and readable
        if (!directory.exists() || !directory.canRead() || !directory.isDirectory()) {
            LOG.error("{} - File directory '{}' does not exists, is not a directory or system does not have read permission.",
                this.dataImportType.getValue(), directory.toPath());
            return new File[0];
        }

        // return list of files that exists and can be read
        return directory.listFiles((FileFilter) this::shouldProcess);
    }

    /**
     * Get list of files in a temporary directory retrieved via FTP protocol. Will return null if ftp location
     * could not be found or an empty array of files if no readable files could be found.
     *
     * Note: The temporary directory created should be deleted when finished processing its contents.
     *
     * @param uri ftp location of files
     * @return readable files in temp directory
     */
    private File[] doFindFilesFtpProtocol(URI uri) throws IOException {

        // if scheme is not ftp, log error and return null
        if (uri.getScheme() == null || uri.getScheme().isEmpty() ||
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_FTP)) {
            LOG.error("{} - Mishandled protocol '{}:' as a 'ftp:' protocol.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // validate uri host value is defined
        if (uri.getHost() == null || uri.getHost().isEmpty()) {
            return handleEmptyHost(uri);
        }

        // get user info from uri, default to anonymous and nopass
        // when anonymous password value does not matter but cannot be NULL
        String[] userInfo = new String[] { "anonymous", "guest", null };
        if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty()) {
            String[] split = uri.getUserInfo().split(":");
            System.arraycopy(split, 0, userInfo, 0,
                userInfo.length < split.length ? userInfo.length : split.length);
        }

        // set remote directory location if not null, default to root
        String remoteDirectory = uri.getPath();
        if (remoteDirectory == null || remoteDirectory.isEmpty())
            remoteDirectory = "/";

        // make sure to logout and disconnect ftp client no matter what
        FTPClient ftpClient = null;
        try {

            // ftp client to communicate with service
            ftpClient = new FTPClient();

            // connect on host and port if defined, default port is 22
            if (uri.getPort() > 0) {
                LOG.trace("{} - Attempting to connect to FTP host '{}' on port '{}'.", this.dataImportType.getValue(),
                    uri.getHost(), uri.getPort());
                ftpClient.connect(uri.getHost(), uri.getPort());
            } else {
                LOG.trace("{} - Attempting to connect to FTP host '{}'.", this.dataImportType.getValue(),
                    uri.getHost());
                ftpClient.connect(uri.getHost());
            }

            // attempt to login to the server
            if (!ftpClient.login(userInfo[0], userInfo[1], userInfo[2])) {
                LOG.error("{} - Failed to login to FTP Client at host '{}' using username '{}'.",
                    this.dataImportType.getValue(), uri.getHost(), userInfo[0]);
                return new File[0];
            }

            // confirm server response is successful
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                LOG.error("{} - FTP Client responded with code '{}' which indicates a bad request.",
                    this.dataImportType.getValue(), reply);
                return new File[0];
            }

            // enter passive mode between server and client
            ftpClient.enterLocalPassiveMode();

            // change current directory
            ftpClient.changeWorkingDirectory(remoteDirectory);

            // get list of files within the remote directory
            FTPFile[] files = ftpClient.listFiles();

            // if no files found, return empty list
            if (files.length == 0) {
                LOG.trace("{} - No new files detected within '{}'.", this.dataImportType.getValue(), uri);
                return new File[0];
            }

            // create a temporary directory to place transferred files
            File toDirectory = this.doCreateRemovableDirectory(this.dataImportType.getValue());

            // return if directory is null
            if (toDirectory == null)
                return emptyFileArray();

            // loop through each remote file and process if it is a file
            // and uploadedFileService returns true for should process
            // files are places in temporary to directory for processing later
            for (FTPFile file : files) {
                if (file.isFile() && this.shouldProcess(file.getName(), file.getTimestamp().getTimeInMillis())) {
                    File localFile = new File(toDirectory.toString() + File.separator + file.getName());
                    try (FileOutputStream output = new FileOutputStream(localFile)) {

                        // copy ftp file to local temporary directory
                        LOG.trace("{} - Copying remote file '{}' to local temporary directory '{}'.",
                            this.dataImportType.getValue(), file.getName(), localFile.getName());
                        ftpClient.retrieveFile(file.getName(), output);

                    } catch (IOException ex) {
                        LOG.error("{} - Could not transfer '{}' to temporary directory '{}' via '{}:' protocol.. " +
                                "file will be skipped: {}", this.dataImportType.getValue(), file.getName(),
                            localFile.getAbsolutePath(), uri.getScheme(), ex.getMessage());
                    }

                    // set local file last modified date to ftp file timestamp
                    if (!localFile.setLastModified(file.getTimestamp().getTimeInMillis()))
                        LOG.error("{} - Last modified date for '{}' could not be handled using '{}:' protocol.. " +
                                "will not be able to distinguish new versions from old.",
                            this.dataImportType.getValue(), file.getName(), uri.getScheme());
                }
            }

            // return list of files that exists and can be read
            return toDirectory.listFiles((File file) -> file.exists() && file.canRead() && file.isFile());

        } finally {

            // disconnect from ftp server if exists
            if (ftpClient != null && ftpClient.isConnected())
            {
                try {
                    LOG.trace("{} - Attempting to disconnect from FTP host '{}'.", this.dataImportType.getValue(),
                        uri.getHost());
                    ftpClient.disconnect();
                } catch (IOException ignored) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Browse a shared network drive via NFS. Returns null if NFS location could not be found or
     * an empty array of files if no readable files could be found.
     *
     * @param uri nfs location of files
     * @return readable files in shared directory
     */
    private File[] doFindFilesNfsProtocol(URI uri) throws IOException {

        // if scheme is not ftp, log error and return null
        if (uri.getScheme() == null || uri.getScheme().isEmpty() ||
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_NFS)) {
            LOG.error("{} - Mishandled protocol '{}:' as a 'nfs:' protocol.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // validate uri host value is defined
        if (uri.getHost() == null || uri.getHost().isEmpty()) {
            return handleEmptyHost(uri);
        }

        // get user info from uri, default to anonymous and nopass
        // when anonymous password value does not matter but cannot be NULL
        String[] userInfo = new String[] { null, null, uri.getHost() };
        if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty()) {
            String[] split = uri.getUserInfo().split(":");
            System.arraycopy(split, 0, userInfo, 0,
                userInfo.length < split.length ? userInfo.length : split.length);
        }

        // set remote directory location if not null, default to root
        String remoteDirectory = uri.getPath();
        if (remoteDirectory == null || remoteDirectory.isEmpty())
            remoteDirectory = "/";

        // define credentials based on user info
        // default to empty which represents "nobody"
        Credential credential = new CredentialUnix();
        if (userInfo[0] != null && !userInfo[0].isEmpty() && userInfo[1] != null && !userInfo[1].isEmpty()) {
            try {

                // user name and user password must be integers
                // these are actually user id and group id
                int uid = Integer.parseInt(userInfo[0]);
                int gid = Integer.parseInt(userInfo[1]);

                // attempt to create credential using uid and gid
                credential = new CredentialUnix(uid, gid, null);

            } catch (NumberFormatException ex) {
                LOG.error("{} - '{}:' protocol requires user and password be number values only: {}",
                    this.dataImportType.getValue(), uri.getScheme(), ex.getMessage());
                return new File[0];
            }
        }

        // open connection to remote directory export
        LOG.trace("{} - Attempting to connect to NFS host '{}' at '{}' using credentials '{}'.",
            this.dataImportType.getValue(), uri.getHost(), remoteDirectory, credential);
        Nfs3 nfs3 = new Nfs3(uri.getHost(), remoteDirectory,
            credential, 3);

        // get directory from local file system
        Nfs3File fromDirectory = new Nfs3File(nfs3, "/");

        // confirm directory exists and readable
        if (!fromDirectory.exists() || !fromDirectory.canRead() || !fromDirectory.isDirectory()) {
            LOG.error("{} - File directory '{}' does not exists, is not a directory or system does not have read permission.",
                this.dataImportType.getValue(), fromDirectory.getAbsolutePath());
            return new File[0];
        }

        // list of files that should be imported
        List<Nfs3File> files = fromDirectory.listFiles((NfsFileFilter) this::shouldProcess);

        // if no files found, return empty list
        if (files.isEmpty()) {
            LOG.trace("{} - No new files detected within '{}'.", this.dataImportType.getValue(), uri);
            return new File[0];
        }

        // create a temporary directory to place transferred files
        File toDirectory = this.doCreateRemovableDirectory(this.dataImportType.getValue());

        // return if directory is null
        if (toDirectory == null)
            return emptyFileArray();

        // loop through each file and transfer into temp directory
        for (Nfs3File file : files) {
            File localFile = new File(toDirectory.toString() + File.separator + file.getName());
            try (NfsFileInputStream input = new NfsFileInputStream(file);
                 FileOutputStream output = new FileOutputStream(localFile)) {

                // copy nfs file to local temporary directory
                LOG.trace("{} - Copying remote file '{}' to local temporary directory '{}'.",
                    this.dataImportType.getValue(), file.getName(), localFile.getName());
                Util.copyStream(input, output);

            } catch (IOException ex) {
                LOG.error("{} - Could not transfer '{}' to temporary directory '{}' via '{}:' protocol.. " +
                    "file will be skipped: {}", this.dataImportType.getValue(), file.getAbsolutePath(),
                    localFile.getAbsolutePath(), uri.getScheme(), ex.getMessage());
            }

            // set local file last modified date to ftp file timestamp
            if (!localFile.setLastModified(file.lastModified()))
                LOG.error("{} - Last modified date for '{}' could not be handled using '{}:' protocol.. " +
                    "will not be able to distinguish new versions from old.",
                    this.dataImportType.getValue(), file.getName(), uri.getScheme());
        }

        // return list of files that exists and can be read
        return toDirectory.listFiles((File file) -> file.exists() && file.canRead() && file.isFile());
    }

    /**
     * Get list of files in a temporary directory retrieved via SCP protocol. Will return null if scp location could
     * not be found or an empty array of files if no readable files could be found.
     *
     * Note: The temporary directory created should be deleted when finished processing its contents.
     *
     * @param uri scp location of files
     * @return readable files in temp directory
     */
    private File[] doFindFilesSshProtocol(URI uri) {

        // if scheme is not scp or sftp, log error and return null
        if (uri.getScheme() == null || uri.getScheme().isEmpty() || (
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_SCP) &&
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_SFTP))) {
            LOG.error("{} - Mishandled protocol '{}:' as a 'scp:' or 'sftp:' protocol.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // create scp command to execute
        AbmsScp scp = new AbmsScp();

        // set protocol as SFTP if scheme is SFTP
        if (uri.getScheme().equalsIgnoreCase(PROTOCOL_SFTP))
            scp.setSftp(true);

        // add port to session if defined, default is 22
        if (uri.getPort() > 0)
            scp.setPort(uri.getPort());

        // if remote directory is empty, log error and return null
        if (uri.getHost() == null || uri.getHost().isEmpty()) {
            LOG.error("{} - Location strings with protocol '{}:' must have an authority value, " +
                "'[user[:password]@]host'.", this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // remote directory to transfer files from, start with authority; user, password, host
        // do NOT include port number as that is set via `scp.setPort(num)`
        String remoteDirectory;
        if (uri.getUserInfo() != null && !uri.getUserInfo().isEmpty())
            remoteDirectory = uri.getUserInfo() + "@" + uri.getHost();
        else
            remoteDirectory = uri.getHost();

        // add path if exists and separate by colon, required for scp
        if (uri.getPath() != null && !uri.getPath().isEmpty())
            remoteDirectory += ":" + uri.getPath();

        // add wildcard if not exist already
        String remoteDirectoryLastChar = remoteDirectory.substring(remoteDirectory.length() - 1);
        if (remoteDirectoryLastChar.equalsIgnoreCase("/"))
            remoteDirectory += "*";
        else if (!remoteDirectoryLastChar.equalsIgnoreCase("*"))
            remoteDirectory += "/*";

        // add from remote directory to scp command
        scp.setRemoteFile(remoteDirectory);

        // create a temporary directory to place transferred files
        File toDirectory = this.doCreateRemovableDirectory(this.dataImportType.getValue());

        // return if directory is null
        if (toDirectory == null)
            return emptyFileArray();

        // local temp directory where files are transferred too
        scp.setLocalTodir(toDirectory.getPath());

        // trust host and preserve last modified date
        scp.setTrust(true);
        scp.setPreservelastmodified(true);

        // add project as a new project, not sure purpose but SCP documentation requires Project not be null
        scp.setProject(new Project());

        // execute scp command and wait for transfer to complete
        scp.execute();

        // return list of files that exists and can be read
        return toDirectory.listFiles((FileFilter) this::shouldProcess);
    }

    /**
     * Get list of files in a temporary directory retrieved via a modem protocol. Will return null if location
     * could not be found or an empty array of files if no readable files could be found.
     *
     * Note: The temporary directory created should be deleted when finished processing its contents.
     *
     * @param uri modem location of files
     * @return readable files in temp directory
     */
    private File[] doFindFilesModemProtocol(URI uri) throws IOException {

        // if scheme is not zr, zm or zmodem, log error and return null
        if (uri.getScheme() == null || uri.getScheme().isEmpty() || (
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_ZR) &&
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_ZM) &&
            !uri.getScheme().equalsIgnoreCase(PROTOCOL_ZMODEM))) {
            LOG.error("{} - Mishandled protocol '{}:' as a modem protocol. " +
                "Currently only accepts 'zr:', 'zm:', or 'zmodem:'.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // validate uri host value is defined
        if (uri.getHost() == null || uri.getHost().isEmpty()) {
            return handleEmptyHost(uri);
        }

        // validate uri post value is defined
        if (uri.getPort() <= 0) {
            LOG.error("{} - '{}:' protocol requires a port value be present.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // validate socket local address is defined
        String localAddress = this.getSocketAddress();
        if (localAddress == null || localAddress.isEmpty()){
            LOG.error("{} - '{}:' protocol requires system configuration ip address be assigned a value.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // validate socket local port is defined
        int localPort = this.getSocketPort();
        if (localPort <= 0){
            LOG.error("{} - '{}:' protocol requires system configuration port number be assigned a value.",
                this.dataImportType.getValue(), uri.getScheme());
            return new File[0];
        }

        // create a temporary directory to place transferred files
        File toDirectory = this.doCreateRemovableDirectory(this.dataImportType.getValue());

        // return if directory is null
        if (toDirectory == null)
            return emptyFileArray();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        LOG.trace("{} - Attempting to connect to remote client host '{}' on port '{}'.",
            this.dataImportType.getValue(), uri.getHost(), uri.getPort());

        try (
            // create socket to remote client
            Socket socket = new Socket(InetAddress.getByName(uri.getHost()), uri.getPort(),
                InetAddress.getByName(localAddress), localPort)
        ) {
            // set max amount of time a read can block operations
            // this is important to prevent zmodem receiver from blocking indefinitely
            // if the sender does not answer back in a reasonable amount of time
            socket.setSoTimeout(1000 * 60);

            // define input and output streams from connected socket
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            // run appropriate modem protocol, only accepts zmodem protocol at this time
            switch (uri.getScheme().toLowerCase()){
                case PROTOCOL_ZR:
                case PROTOCOL_ZM:
                case PROTOCOL_ZMODEM:
                    this.runZModemReceive(inputStream, outputStream, toDirectory);
                    break;
                default:
                    // do nothing if no supported match
                    break;
            }

        } finally {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
        }

        // return list of files that exists and can be read
        return toDirectory.listFiles((FileFilter) this::shouldProcess);
    }

    /**
     * Finds the appropriate URI value depending on the data import type.
     *
     * @return uniform resource identifier
     */
    private URI doFindLocation() {

        // get appropriate value from system configuration
        String value = this.getLocation();

        // if value is null, return null
        if (value == null || value.isEmpty()) {
            LOG.trace("Location string not set in system configuration.");
            return null;
        }

        try {

            URIBuilder uriBuilder = new URIBuilder(value);

            // create uri from url values without query and fragment
            // and normalize newly created URI
            URI uri = uriBuilder.build().normalize();

            // only return if exists and has scheme else return null
            if (uri.getScheme() != null && !uri.getScheme().isEmpty())
                return uri;

        } catch (URISyntaxException ex) {
            LOG.error("Could not parse '{}' location string with the value of '{}' because: {}",
                this.getClass().getSimpleName(), value, ex.getMessage());
        }

        // if code reaches this point, because of an exception, empty or missing scheme
        return null;
    }

    /**
     * Adds an entry into the upload history table to prevent reimport of
     * a file that throws an exception.
     *
     * @param file that throws exception
     * @param exception thrown during import
     */
    private void doHandleException(File file, Exception exception) {

        byte[] bytes;
        String contentType;
        try {
            bytes = Files.readAllBytes(file.toPath());
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException ee) {
            bytes = null;
            contentType = null;
        }

        uploadedFileService.createUploadFileRecordFromException(exception,
            uploadedFileService.getUploadedFileRecordType(dataImportType),
            file.getName(), contentType, bytes, false,
            uploadedFileService.formatFileLastModifiedDate(file.lastModified()));
    }

    /**
     * Use to process a file and call the upload method if file should be uploaded.
     *
     * @param file file to process
     * @return bulk loader summary, null if not uploaded
     */
    private BulkLoaderSummary doProcessFile(File file) throws IOException {

        // filter out unsupported content types
        String contentType = Files.probeContentType(file.toPath());
        if (!contentTypes.contains(ContentTypes.forValue(contentType))) {
            LOG.warn("{} - Content Type '{}' for File '{}' within '{}' is not supported. Must use a supported Content Type:",
                this.dataImportType.getValue(), contentType, file.getName(), file.getPath());

            // log each supported type only if info is enabled
            if (LOG.isInfoEnabled()) {
                contentTypes.forEach(type -> LOG.info("Supported Content Type ({}): {}", type.name(), type.toValue()));
            }

            return null;
        }

        // filter out if already processed under history table
        LOG.info("{} - File '{}' within '{}' is being uploaded...",
            this.dataImportType.getValue(), file.getName(), file.getPath());

        // load file into appropriate bulk loader based on data import type
        // exceptions are logged and the problem file is skipped
        BulkLoaderSummary bulkLoaderSummary;
        try {
            bulkLoaderSummary = this.upload(file);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getName(), ex.getMessage());
            bulkLoaderSummary = null;
        }

        // if no summary, continue to next as file was not uploaded
        if (bulkLoaderSummary == null) {
            LOG.warn("{} - File '{}' within '{}' could not be uploaded.",
                this.dataImportType.getValue(), file.getName(), file.getPath());
            return null;
        }

        LOG.info("{} - File '{}' within '{}' has finished uploading with the following results: {}",
            this.dataImportType.getValue(), file.getName(), file.getPath(), bulkLoaderSummary);

        // add bulk loader summary to results
        return bulkLoaderSummary;
    }

    /**
     * Log error that we must abort because we cannot create a temporary directory and
     * return default empty file array.
     *
     * @return new empty File array
     */
    private File[] emptyFileArray() {
        LOG.error("{} - Could not create a temporary directory and must abort.",
            this.dataImportType.getValue());
        return new File[0];
    }

    /**
     * Log appropriate error message and return empty list of files.
     */
    private File[] handleEmptyHost(final URI uri) {
        LOG.error("{} - '{}:' protocol requires a host value be present.",
            this.dataImportType.getValue(), uri.getScheme());
        return new File[0];
    }

    /**
     * Run zmodem protocol using the input and output streams to receive files and write them
     * to the supplied directory.
     *
     * @param inputStream socket input stream
     * @param outputStream socket output stream
     * @param directory directory to place received files
     */
    private void runZModemReceive(InputStream inputStream, OutputStream outputStream, File directory) throws IOException {

        // create zmodem from input and output streams supplied
        ZModem zModem = new ZModem(inputStream, outputStream);

        // send ZRINIT header to initialize receive request
        ZMPacketOutputStream os = new ZMPacketOutputStream(outputStream);
        os.write(new Header(Format.HEX, ZModemCharacter.ZRINIT,
            new byte[]{0,4,0, ZMOptions.with(ZMOptions.ESCCTL,ZMOptions.ESC8)}));

        // wait to receive files and place in directory
        FileAdapter fileAdapterDirectory = new CustomFile(directory);
        zModem.receive(fileAdapterDirectory);
    }

    /**
     * Validate if file with name and last import data has already been processed.
     *
     * @param fileName file name to process
     * @param lastModified last modified date
     * @return true if unprocessed, else false
     */
    private boolean shouldProcess(String fileName, long lastModified) {
        if (uploadedFileService.shouldProcessFile(fileName, this.dataImportType, lastModified)) {
            return true;
        } else {
            LOG.trace("{} - File '{}' has already been uploaded.", dataImportType.getValue(),
                fileName);
            return false;
        }
    }

    /**
     * Validate if file with name and last import data has already been processed.
     *
     * @param file file to process
     * @return true if unprocessed, else false
     */
    private boolean shouldProcess(File file) {
        return file.exists() && file.canRead() && file.isFile()
            && this.shouldProcess(file.getName(), file.lastModified());
    }

    /**
     * Validate if file with name and last import data has already been processed.
     *
     * @param file file to process
     * @return true if unprocessed, else false
     */
    private boolean shouldProcess(NfsFile<?, ?> file) {
        try {
            return file.exists() && file.canRead() && file.isFile()
                && this.shouldProcess(file.getName(), file.lastModified());
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Get address for TCP/IP Socket to bind onto. Used in combination with a MOXA serial to
     * USB converter. This method should be overridden.
     *
     * @return returns null indicating no address set
     */
    protected String getSocketAddress() {
        return null;
    }

    /**
     * Get port for TCP/IP Socket to bind onto. Used in combination with a MOXA serial to USB
     * converter. This method should be overridden.
     *
     * @return returns 0 indicating no port set
     */
    protected int getSocketPort() {
        return DEFAULT_SOCKET_PORT;
    }

    /**
     * Use to get the location string form system configuration settings.
     *
     * @return system configuration value
     */
    protected abstract String getLocation();

    /**
     * Use to upload a file to the appropriate bulk loader service.
     *
     * @param file file to bulk upload
     * @return bulk loader summary
     */
    protected abstract BulkLoaderSummary upload(final File file) throws IOException;
}
