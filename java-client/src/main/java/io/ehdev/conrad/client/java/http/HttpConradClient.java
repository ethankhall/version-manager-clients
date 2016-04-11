package io.ehdev.conrad.client.java.http;

import java.io.IOException;
import java.util.List;

public interface HttpConradClient {

    VersionEntry claimVersion(String sha, String message, List<String> history) throws IOException;

    VersionEntry getCurrentVersion(List<String> history) throws IOException;
}
