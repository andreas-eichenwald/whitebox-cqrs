package io.dabrowa.whitebox

import io.dabrowa.whitebox.app.Main
import spock.lang.Specification

class MainTest extends Specification {
    def "setup verification"() {
        expect:
        Main.test() == "test"
    }
}
