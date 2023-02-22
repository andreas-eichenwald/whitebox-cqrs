package io.dabrowa.whitebox

import spock.lang.Specification

class MainTest extends Specification {
    def "setup verification"() {
        expect:
        Main.test() == "test"
    }
}
