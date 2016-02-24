package se.alkohest.irkksome.entity;

public interface BaseConnection {
    int getPort();
    String getHost();
    boolean isUseSSH();
    SSHConnection getSSHConnection();
}
