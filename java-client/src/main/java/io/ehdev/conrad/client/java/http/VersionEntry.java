package io.ehdev.conrad.client.java.http;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VersionEntry {
    private final List<String> versionParts;
    private final String postfix;
    private final String commitId;

    public VersionEntry(List<String> versionParts, String postfix, String commitId) {
        this.versionParts = versionParts;
        this.postfix = postfix;
        this.commitId = commitId;
    }

    public String getVersion() {
        String mainPart = versionParts.stream().collect(Collectors.joining("."));
        if (StringUtils.isNotBlank(postfix)) {
            return mainPart + "-" + postfix;
        } else {
            return mainPart;
        }
    }

    public String getCommitId() {
        return commitId;
    }

    public VersionEntry toNextSnapshot() {
        List<String> nextVersionParts = new ArrayList<>(versionParts);
        for(int i = versionParts.size() - 1; i >= 0; i--) {
            if(StringUtils.isNumeric(versionParts.get(i))) {
                nextVersionParts.set(i, Integer.toString(Integer.parseInt(versionParts.get(i)) + 1));
                break;
            }
        }
        return new VersionEntry(nextVersionParts, "SNAPSHOT", null);
    }

    @Override
    public String toString() {
        return getVersion();
    }
}
