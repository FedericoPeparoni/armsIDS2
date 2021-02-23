package ca.ids.abms.util.ssh;

import com.jcraft.jsch.*;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHBase;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;

/**
 * Override default {@link Scp} class to add session timeout.
 */
public class AbmsScp extends Scp {

    private String knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";
    private int sessionTimeout = 3600000;

    /**
     * Sets the path to the file that has the identities of
     * all known hosts.  This is used by SSH protocol to validate
     * the identity of the host.  The default is
     * <i>${user.home}/.ssh/known_hosts</i>.
     *
     * @param knownHosts a path to the known hosts file.
     */
    @Override
    public void setKnownhosts(String knownHosts) {
        super.setKnownhosts(knownHosts);
        this.knownHosts = knownHosts;
    }

    /**
     * Used to define session timeout in milliseconds, 0 is infinite. Defaults to 3600000 milliseconds.
     */
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    /**
     * Open an ssh session.
     *
     * Overrides {@link SSHBase#openSession()} method to add session timeout.
     *
     * @return the opened session
     * @throws JSchException on error
     */
    @Override
    protected Session openSession() throws JSchException {
        JSch jsch = new JSch();
        final SSHBase base = this;
        if(getVerbose()) {
            JSch.setLogger(new com.jcraft.jsch.Logger(){
                public boolean isEnabled(int level){
                    return true;
                }
                public void log(int level, String message){
                    base.log(message, Project.MSG_INFO);
                }
            });
        }
        if (null != getUserInfo().getKeyfile()) {
            jsch.addIdentity(getUserInfo().getKeyfile());
        }

        if (!getUserInfo().getTrust() && knownHosts != null) {
            log("Using known hosts: " + knownHosts, Project.MSG_DEBUG);
            jsch.setKnownHosts(knownHosts);
        }

        Session session = jsch.getSession(getUserInfo().getName(), getHost(), getPort());
        session.setConfig("PreferredAuthentications",
            "publickey,keyboard-interactive,password");
        session.setUserInfo(getUserInfo());
        log("Connecting to " + getHost() + ":" + getPort());
        session.setTimeout(sessionTimeout);
        session.connect();
        return session;
    }
}
