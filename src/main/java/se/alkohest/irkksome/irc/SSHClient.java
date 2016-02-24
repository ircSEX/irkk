package se.alkohest.irkksome.irc;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.ConnectionInfo;
import com.trilead.ssh2.ConnectionMonitor;
import com.trilead.ssh2.DebugLogger;
import com.trilead.ssh2.LocalPortForwarder;
import com.trilead.ssh2.ServerHostKeyVerifier;
import com.trilead.ssh2.log.Logger;

import java.io.IOException;
import java.util.Random;

import se.alkohest.irkksome.entity.SSHConnection;
import se.alkohest.irkksome.util.KeyEncodingUtil;
import se.alkohest.irkksome.util.KeyProvider;

// test keys can be generated with
// $ ssh-keygen -t rsa -b 1024 -f dummy-ssh-keygen.pem -N '' -C "keyname"
public abstract class SSHClient implements ConnectionMonitor {
    public static final String AUTH_PUBLIC_KEY = "publickey";
    public static final String AUTH_PASSWORD = "password";
    public static final String TAG = "irkksomeSSH";
    private static final Random PORTFORWARD_RANDOM = new Random();
    private static final boolean DEBUG_SSH = true;
    private static final int MIN_PORT = 49152;
    private static final int MAX_PORT = 65535;
    private static final int AUTH_ATTEMPTS = 4;

    protected final int localPort = getRandomLocalPort();
    protected SSHConnection sshConnectionData;
    protected LocalPortForwarder portForwarder;
    protected Connection connection;
    protected boolean connected;
    protected ConnectionInfo connectionInfo;

    static {
        if (DEBUG_SSH) {
            DebugLogger logger = new DebugLogger() {
                @Override
                public void log(int level, String className, String message) {
                    se.alkohest.irkksome.util.Logger.log(TAG, message);
                }
            };
            Logger.enabled = true;
            Logger.logger = logger;
        }
    }


    public SSHClient(SSHConnection data) {
        this.sshConnectionData = data;
    }

    protected void establishConnection() throws ConnectionIOException {
        connected = true;
        connection = new Connection(sshConnectionData.getSshHost(), sshConnectionData.getSshPort());
        connection.addConnectionMonitor(this);

        try {
            connectionInfo = connection.connect(new HostKeyVerifier());
        } catch (IOException e) {
            closeAll();
            throw new ConnectionIOException(ConnectionIOException.ErrorPhase.PRE, "Error while connecting. Host cannot be reached."); // This is thrown when there is no internet
        }


        if (authLoop()) {
            postAuthAction();
        }
        else {
            closeAll();
            // Auth loop never managed to auth, incorrect details
            throw new ConnectionIOException(ConnectionIOException.ErrorPhase.AUTH, "Authentication failed. Incorrect details?");
        }


    }

    private boolean authLoop() throws ConnectionIOException {
        int tries = 0;
        boolean shouldRetryAuth = true;
        while (connected && shouldRetryAuth) {
            try {
                authenticate();
            } catch (IOException e) {
                // Got IO during auth, network error
                throw new ConnectionIOException(ConnectionIOException.ErrorPhase.AUTH, "Connection failed.");
            }
            tries++;
            shouldRetryAuth = tries < AUTH_ATTEMPTS && !connection.isAuthenticationComplete() ;
            if (shouldRetryAuth) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // just die
                }
            }
        }
        return connection.isAuthenticationComplete();
    }

    private boolean authenticate() throws IOException {
        if (connection.authenticateWithNone(sshConnectionData.getSshUser())) {
            return true;
        }

        if (sshConnectionData.isUseKeyPair() && KeyProvider.hasKeys()) {
            if (connection.isAuthMethodAvailable(sshConnectionData.getSshUser(), AUTH_PUBLIC_KEY)) {
                if (connection.authenticateWithPublicKey(sshConnectionData.getSshUser(), KeyProvider.getPrivkey(), null)) {
                    return true;
                }
            }
        }
        if (connection.isAuthMethodAvailable(sshConnectionData.getSshUser(), AUTH_PASSWORD)) {
            if (sshConnectionData.getSshPassword() != null && connection.authenticateWithPassword(sshConnectionData.getSshUser(), sshConnectionData.getSshPassword())) {
                return true;
            }
        }
        return false;
    }

    protected abstract void postAuthAction() throws ConnectionIOException;

    public void closeAll() {
        if (connected) {
            connected = false;
            if (portForwarder != null) {
                try {
                    portForwarder.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void connectionLost(Throwable reason) {
        closeAll();
        se.alkohest.irkksome.util.Logger.log(TAG, "Connection lost: " + reason);
        // propagate up??
    }

    private static int getRandomLocalPort() {
        return PORTFORWARD_RANDOM.nextInt(MAX_PORT - MIN_PORT) + MIN_PORT;
    }

    public class HostKeyVerifier implements ServerHostKeyVerifier {
        @Override
        public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws IOException {
            return true;
        }
    }
}
