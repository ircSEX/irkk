package se.alkohest.irkk.irc

import spock.lang.Specification

class IrcBacklogIntegrationTest extends Specification {
    IrcProtocolListener subscriber = Mock()
    BacklogHandler backlogHandler = new IrssiProxyBacklogHandler()
    IrcProtocolAdapter ipa = new IrcProtocolAdapter(new NormalConnection("test.se", 22), backlogHandler);

    def setup() {
        ipa.setListener(subscriber)
    }

    def "replay before live"() {
        when:
        ipa.handleReply(":0 PROXY start")
        ipa.handleReply("irkksome:Norrland!Norrland@smurf-e591gr PRIVMSG #ircsex-asp :Är säkert 50% S 25% V och 25% SD :1424940364")
        ipa.handleReply(":0 PROXY stop")
        ipa.handleReply(":Heissman!hajsman@proxy PRIVMSG #ircsex-asp :den stora korvdagen")

        then:
        1 * subscriber.channelMessageReceived("#ircsex-asp", "Norrland", "Är säkert 50% S 25% V och 25% SD", {it.getTime()==1424940364000})
        1 * subscriber.channelMessageReceived("#ircsex-asp", "Heissman", "den stora korvdagen", _)
        !ipa.isBacklogReplaying()
        ipa.shouldPassMessageEvents()
    }

    /*def "replay intermixed with live"() {
        when:
        ipa.handleReply(":0 PROXY start")
        ipa.handleReply("irkksome:Norrland!Norrland@smurf-e591gr PRIVMSG #ircsex-asp :Är säkert 50% S 25% V och 25% SD :1424940364")
        ipa.handleReply(":Heissman!hajsman@proxy PRIVMSG #ircsex-asp :den stora korvdagen")

        then:
        1 * subscriber.channelMessageReceived("#ircsex-asp", "Norrland", "Är säkert 50% S 25% V och 25% SD", {it.getTime()==1424940364000})
        ipa.isBacklogReplaying()
        !ipa.shouldPassMessageEvents()
        0 * _

        when:
        ipa.handleReply(":0 PROXY stop")

        then:
        1 * subscriber.channelMessageReceived("#ircsex-asp", "Heissman", "den stora korvdagen", _)
        !ipa.isBacklogReplaying()
        ipa.shouldPassMessageEvents()
        0 * _
    }*/

    def "no replay"() {
        expect:
        !ipa.isBacklogReplaying()
        !ipa.shouldPassMessageEvents()

        when:
        ipa.handleReply(":0 PROXY start")
        ipa.handleReply(":0 PROXY stop")
        ipa.handleReply(":Heissman!hajsman@proxy PRIVMSG #ircsex-asp :den stora korvdagen")

        then:
        1 * subscriber.channelMessageReceived("#ircsex-asp", "Heissman", "den stora korvdagen", _)
        !ipa.isBacklogReplaying()
        ipa.shouldPassMessageEvents()
    }
}
