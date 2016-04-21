package io.ehdev.conrad.client.java.http;

import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * A container of version information.
 *
 * The {@literal postfix} is "SNAPSHOT" for "1.2.3-SNAPSHOT"
 * The {@literal commitId} is the commit that the current version comes from
 * The {@literal versionParts} are a list of String that represent the version.
 */
public class VersionEntry {
    private final List<String> versionParts;
    private final String postfix;
    private final String commitId;

    public VersionEntry(List<String> versionParts, String postfix, String commitId) {
        this.versionParts = versionParts;
        this.postfix = postfix;
        this.commitId = commitId;
    }

  /**
   * @return The version string
   */
  public String getVersion() {
        String mainPart = versionParts.stream().collect(Collectors.joining("."));
        if (StringUtils.isNotBlank(postfix)) {
            return mainPart + "-" + postfix;
        } else {
            return mainPart;
        }
    }

  /**
   * Get the parts of the version.
   *
   * For a version string `1.2.3` you would get a list of ['1', '2', '3']. For a version string containing a postfix
   * you will not get the postfix.
   *
   * @return an UnmodifiableList of the version parts. If you try to change this list, it will throw.
   */
  public List<String> getVersionParts() {
        return Collections.unmodifiableList(versionParts);
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
