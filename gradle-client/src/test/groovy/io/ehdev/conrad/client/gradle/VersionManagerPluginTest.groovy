package io.ehdev.conrad.client.gradle

import nebula.test.PluginProjectSpec
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import spock.lang.Unroll

class VersionManagerPluginTest extends PluginProjectSpec {

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Override
    String getPluginName() {
        return 'version-manager'
    }

    def 'extension added'() {
        when:
        project.apply plugin: pluginName

        then:
        null != project.getExtensions().findByName('versionManager')
        null != project.getExtensions().findByType(VersionManagerExtension)
    }

    @Unroll
    def 'when error happens it will return with a default value with status code #statusCode'() {
        when:
        project.apply plugin: pluginName

        configureProject(project.getExtensions().getByType(VersionManagerExtension), "SOME TEXT", statusCode)

        then:
        project.getVersion().toString() == '0.0.1-SNAPSHOT'

        where:
        statusCode << [201, 200]
    }

    public void configureProject(VersionManagerExtension configuration, String body, int statusCode = 200) {
        server.enqueue(new MockResponse().setBody(body).setResponseCode(statusCode))

        configure(configuration)
    }

    private void configure(VersionManagerExtension configuration) {
        configuration.baseUrl = 'http://localhost:' + server.getPort()
        configuration.projectName = 'project'
        configuration.repoName = 'repo'
    }
}
