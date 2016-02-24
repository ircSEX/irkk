package se.alkohest.irkksome.entity;

public class BaseConnectionImpl implements BaseConnection {
    private int port;
    private String host;
    private SSHConnection sshConnection;

    public BaseConnectionImpl(int port, String host) {
        this(port, host, null);
    }

    public BaseConnectionImpl(int port, String host, SSHConnection sshConnection) {
        this.port = port;
        this.host = host;
        this.sshConnection = sshConnection;
    }

    @Override
    public boolean isUseSSH() {
        return sshConnection != null;
    }

    @Override
    public SSHConnection getSSHConnection() {
        return sshConnection;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getHost() {
        return host;
    }
}
