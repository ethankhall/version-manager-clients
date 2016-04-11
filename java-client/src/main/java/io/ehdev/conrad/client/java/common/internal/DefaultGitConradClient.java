package io.ehdev.conrad.client.java.common.internal;

import io.ehdev.conrad.client.java.common.ConradClient;
import io.ehdev.conrad.client.java.exception.ConradException;
import io.ehdev.conrad.client.java.git.CommitDetails;
import io.ehdev.conrad.client.java.git.GitManager;
import io.ehdev.conrad.client.java.http.HttpConradClient;
import io.ehdev.conrad.client.java.http.VersionEntry;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class DefaultGitConradClient implements ConradClient {

    private final GitManager gitManager;
    private final HttpConradClient httpConradClient;

    public DefaultGitConradClient(HttpConradClient httpConradClient, GitManager gitManager) {
        this.httpConradClient = httpConradClient;
        this.gitManager = gitManager;
    }

    @Override
    public VersionEntry claimVersion() throws ConradException {
        try {
            CommitDetails headCommitDetails = gitManager.getHeadCommitDetails();
            VersionEntry versionEntry = httpConradClient.claimVersion(headCommitDetails.getId(),
                    headCommitDetails.getMessage(),
                    gitManager.findCommitsFromHead(50));

            gitManager.tag(versionEntry.getVersion());
            return versionEntry;
        } catch (GitAPIException | IOException e) {
            throw new ConradException(e);
        }
    }

    @Override
    public VersionEntry getCurrentVersion() throws ConradException {
        return getCurrentVersion(50);
    }

    @Override
    public VersionEntry getCurrentVersion(int historyLength) throws ConradException {
        try {
            VersionEntry currentVersion = httpConradClient.getCurrentVersion(gitManager.findCommitsFromHead(historyLength));

            if (!StringUtils.equals(currentVersion.getCommitId(), gitManager.getHeadCommitDetails().getId())) {
                return currentVersion.toSnapshot();
            }

            if (gitManager.hasChanges()) {
                return currentVersion.toSnapshot();
            }

            return currentVersion;
        } catch (IOException | GitAPIException e) {
            throw new ConradException(e);
        }
    }
}
