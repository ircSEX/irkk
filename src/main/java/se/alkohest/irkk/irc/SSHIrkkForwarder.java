package se.alkohest.irkk.irc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import se.alkohest.irkk.entity.BaseConnection;

public class SSHIrkkForwarder extends SSHClient implements ServerConnection {
    private ServerConnection forwardingConnection;
    private BaseConnection ircConnection;

    public SSHIrkkForwarder(BaseConnection data) {
        super(data.getSSHConnection());
        this.ircConnection = data;
        this.forwardingConnection = new NormalConnection(data.getHost(), localPort);
    }

    @Override
    public void connect() throws IOException {
        establishConnection();
        forwardingConnection.connect();
    }

    @Override
    public String readLine() throws IOException {
        return forwardingConnection.readLine();
    }

    @Override
    public void write(String s) throws IOException {
        forwardingConnection.write(s);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    protected void postAuthAction() throws ConnectionIOException {
        try {
            portForwarder = connection.createLocalPortForwarder(new InetSocketAddress(InetAddress.getLocalHost(), localPort), ircConnection.getHost(), ircConnection.getPort());
        } catch (IOException e) {
            throw new ConnectionIOException(ConnectionIOException.ErrorPhase.POST, "Portforward could not be created.");
        }
    }

    @Override
    public void close() {
        closeAll();
    }
}
