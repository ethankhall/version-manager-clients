package tech.crom.client.java.http

import spock.lang.Specification

class VersionEntryTest extends Specification {

    def 'will make the next snapshot version'() {
        def entry = new VersionEntry("1.2.3", 'abcdef')

        expect:
        entry.toString() == '1.2.3'
        entry.toNextSnapshot().toString() == '1.2.4-SNAPSHOT'
    }

    def 'when snapshot it will be right'() {
        def entry = new VersionEntry("1.2.3-SNAPSHOT", 'abcdef')

        expect:
        entry.toString() == '1.2.3-SNAPSHOT'
        entry.toNextSnapshot().toString() == '1.2.4-SNAPSHOT'
    }
}
