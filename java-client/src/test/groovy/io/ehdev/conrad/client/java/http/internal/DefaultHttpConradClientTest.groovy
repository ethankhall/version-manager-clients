package io.ehdev.conrad.client.java.http.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ehdev.conrad.client.java.http.HttpConradClientBuilder
import io.ehdev.conrad.model.version.CreateVersionRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import spock.lang.Shared
import spock.lang.Specification

class DefaultHttpConradClientTest extends Specification {

    @Shared
    def om = new ObjectMapper().registerModule(new KotlinModule())

    @Rule
    public final MockWebServer server = new MockWebServer();

    def 'can get version from web-server'() {
        given:
        server.enqueue(new MockResponse().setBody(DefaultHttpConradClientTest.getResource('/version-search-response.json').text))
        def client = new HttpConradClientBuilder('1', '2', '3').withBaseUrl("http://localhost:${server.port}").build()

        expect:
        client.getCurrentVersion(['a', 'b']).version == '0.0.2'
        server.takeRequest().getHeader(HttpConradClientBuilder.X_AUTH_TOKEN) == '3'
    }

    def 'can create a version from web-server'() {
        server.enqueue(new MockResponse().setBody(DefaultHttpConradClientTest.getResource('/version-create-response.json').text))
        def client = new HttpConradClientBuilder('1', '2', '3').withBaseUrl("http://localhost:${server.port}").build()

        when:
        def version = client.claimVersion('c', 'message', ['a', 'b'])
        def request = server.takeRequest()

        then:
        version.version == '0.0.2'
        request.getHeader(HttpConradClientBuilder.X_AUTH_TOKEN) == '3'

        def versionRequest = om.readValue(request.getBody().readUtf8(), CreateVersionRequest.class)

        versionRequest.commitId == 'c'
        versionRequest.commits == ['a', 'b']
        versionRequest.message == 'message'
    }
}
