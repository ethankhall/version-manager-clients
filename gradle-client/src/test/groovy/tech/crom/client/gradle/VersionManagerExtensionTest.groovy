package tech.crom.client.gradle


import tech.crom.client.java.http.VersionEntry
import spock.lang.Specification

class VersionManagerExtensionTest extends Specification {

  def 'can set the version customizer'() {
    given:
    def extension = new VersionManagerExtension()

    when:
    extension.customize { return "0.2.3.4" }

    then:
    extension.getVersionCustomizer().configure(new VersionEntry([], null, null)) == '0.2.3.4'
  }
}
