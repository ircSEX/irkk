package se.alkohest.irkk.entity;

public class BaseConnectionImpl implements BaseConnection {
    private int port;
    private String host;
    private SSHConnection sshConnection;

    public BaseConnectionImpl(String host, int port) {
        this(host, port, null);
    }

    public BaseConnectionImpl(String host, int port, SSHConnection sshConnection) {
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
