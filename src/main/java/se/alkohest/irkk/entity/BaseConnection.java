package se.alkohest.irkk.entity;

public interface BaseConnection {
    int getPort();
    String getHost();
    boolean isUseSSH();
    SSHConnection getSSHConnection();
}
