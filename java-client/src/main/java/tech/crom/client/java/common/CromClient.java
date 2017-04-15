package tech.crom.client.java.common;

import tech.crom.client.java.exception.ConradException;
import tech.crom.client.java.http.VersionEntry;
import tech.crom.client.java.scm.ScmManager;

import java.util.List;

public interface CromClient {

    /**
     * This implementation calls to {@link #claimVersion(String, String, List)} with the commit id,
     * message for the commit and the last 50 commitIds
     * @return the version claimed
     * @throws ConradException wrapping all exceptions
     */
    VersionEntry claimVersion() throws ConradException;

    /**
     * This provides the flexable API for users to specify how they want the api to function.
     *
     * If you are using the {@code atomic} bumper, then in most cases you want to set the
     * commitId to be the current timestamp, message to be null, and historyIds to be ["latest"]
     *
     * @param commitId commit to claim a version for
     * @param message message to use to determine the next version
     * @param historyIds a list of id's to determine history from
     *
     * @return a version claimed
     * @throws ConradException wrapping all exceptions
     */
    VersionEntry claimVersion(String commitId, String message, List<String> historyIds) throws ConradException;

    /**
     * Tags the repo with a version entry. Depending on the underlying implementation it may do nothing in the
     * case of SVN or may push to Git when using a Git repo.
     *
     * @param versionEntry version to tag the repo with.
     * @throws ConradException wrapping all exceptions
     */
    void tagVersion(VersionEntry versionEntry) throws ConradException;

    VersionEntry getCurrentVersion() throws ConradException;

    VersionEntry getCurrentVersion(int historyLength) throws ConradException;

    ScmManager getScmManager();
}
