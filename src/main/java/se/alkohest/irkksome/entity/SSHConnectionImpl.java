package se.alkohest.irkksome.entity;

public class SSHConnectionImpl implements SSHConnection {
    private String sshHost;
    private String sshUser;
    private String sshPassword;
    private int sshPort;
    private boolean useKeyPair;

    public SSHConnectionImpl(String sshHost, String sshUser, String sshPassword, int sshPort, boolean useKeyPair) {
        this.sshHost = sshHost;
        this.sshUser = sshUser;
        this.sshPassword = sshPassword;
        this.sshPort = sshPort;
        this.useKeyPair = useKeyPair;
    }

    public String getSshHost() {
        return sshHost;
    }

    public String getSshUser() {
        return sshUser;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public int getSshPort() {
        return sshPort;
    }

    public boolean isUseKeyPair() {
        return useKeyPair;
    }
}
