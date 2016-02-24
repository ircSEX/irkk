package se.alkohest.irkk.entity;

public interface SSHConnection {
    boolean isUseKeyPair();
    int getSshPort();
    String getSshPassword();
    String getSshUser();
    String getSshHost();
}
