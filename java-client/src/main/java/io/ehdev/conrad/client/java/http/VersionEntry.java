package io.ehdev.conrad.client.java.http;

import org.apache.commons.lang3.StringUtils;

public class VersionEntry {
    private final String version;
    private final String commitId;

    public VersionEntry(String version, String commitId) {
        this.version = version;
        this.commitId = commitId;
    }

    public String getVersion() {
        return version;
    }

    public String getCommitId() {
        return commitId;
    }

    public VersionEntry toSnapshot() {
        if(!StringUtils.endsWith(version, "-SNAPSHOT")) {
            return new VersionEntry(version + "-SNAPSHOT", null);
        }
        return this;
    }
}
