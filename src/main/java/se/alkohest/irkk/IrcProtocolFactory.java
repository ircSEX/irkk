package se.alkohest.irkk;

import se.alkohest.irkk.entity.BaseConnection;
import se.alkohest.irkk.irc.*;

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
