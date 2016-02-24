package se.alkohest.irkk.irc

import spock.lang.Specification


class IrssiProxyBacklogHandlerTest extends Specification {
    BacklogHandler backlogHandler = new IrssiProxyBacklogHandler()

    def setup() {

    }

    def "test extractDate"() {
        String[] parts1 = ["", "PROXY", "start"]
        String[] parts2 = ["", "", "asdfasdf :123"]
        String[] parts3 = ["", "", "asdfasdf"]
        String[] parts4 = ["", "PROXY", "stop"]
        String[] parts5 = ["", "", "asdfasdf :123"]

        when:
        backlogHandler.extractDate(parts1)
        def time2 = backlogHandler.extractDate(parts2)
        def time3 = backlogHandler.extractDate(parts3)
        backlogHandler.extractDate(parts4)
        def time5 = backlogHandler.extractDate(parts5)

        then:
        time2.getTime() == 123000L
        time3.class == Date.class
        time5.getTime() != 123000L

    }

    def "test getBacklogRequest"() {
        when:
        def string = backlogHandler.getBacklogRequest(123L)

        then:
        string.equals("PROXY backlog 123")
    }

    def "test isReplaying"() {
        String[] parts1 = ["", "PROXY", "start"]
        String[] parts2 = ["", "", "asdfasdf :123"]
        String[] parts3 = ["", "", "asdfasdf"]
        String[] parts4 = ["", "PROXY", "stop"]
        String[] parts5 = ["", "", "asdfasdf :123"]

        expect:
        !backlogHandler.backlogReplaying

        when:
        backlogHandler.extractDate(parts1)
        backlogHandler.extractDate(parts2)
        backlogHandler.extractDate(parts3)

        then:
        backlogHandler.backlogReplaying

        when:
        backlogHandler.extractDate(parts4)

        then:
        !backlogHandler.backlogReplaying

        when:
        backlogHandler.extractDate(parts5)

        then:
        !backlogHandler.backlogReplaying
    }
}
