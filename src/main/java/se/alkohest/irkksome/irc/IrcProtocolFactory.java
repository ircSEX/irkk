package se.alkohest.irkksome.irc;

import se.alkohest.irkksome.entity.BaseConnection;

/**
 * Created by oed on 7/18/14.
 */
public class IrcProtocolFactory {

    public static IrcProtocol getIrcProtocol(BaseConnection data) {
        // for now..
        BacklogHandler bh = new IrssiProxyBacklogHandler();
        if (data.isUseSSH()) {
            return new IrcProtocolAdapter(getSSHConnection(data), bh);
        }
        return new IrcProtocolAdapter(getNormalConnection(data), bh);
    }

    private static ServerConnection getSSHConnection(BaseConnection data) {
        return new SSHIrkkForwarder(data);
    }

    private static ServerConnection getNormalConnection(BaseConnection data) {
        return new NormalConnection(data.getHost(), data.getPort());
    }
}
