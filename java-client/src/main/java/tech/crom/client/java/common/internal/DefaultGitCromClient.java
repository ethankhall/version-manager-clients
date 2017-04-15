package tech.crom.client.java.common.internal;

import tech.crom.client.java.common.CromClient;
import tech.crom.client.java.exception.ConradException;
import tech.crom.client.java.http.HttpConradClient;
import tech.crom.client.java.http.VersionEntry;
import tech.crom.client.java.scm.CommitDetails;
import tech.crom.client.java.scm.ScmManager;
import tech.crom.client.java.scm.git.GitManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class DefaultGitCromClient implements CromClient {

    private final GitManager gitManager;
    private final HttpConradClient httpConradClient;

    public DefaultGitCromClient(HttpConradClient httpConradClient, GitManager gitManager) {
        this.httpConradClient = httpConradClient;
        this.gitManager = gitManager;
    }

    @Override
    public VersionEntry claimVersion() throws ConradException {
        try {
            CommitDetails headCommitDetails = gitManager.getCurrentCommitDetails();
            return claimVersion(headCommitDetails.getId(), headCommitDetails.getMessage(), gitManager.getPreviousCommitIds(50));
        } catch (IOException e) {
            throw new ConradException(e);
        }
    }

    @Override
    public VersionEntry claimVersion(String commitId, String message, List<String> historyIds) throws ConradException {
        try {
            return httpConradClient.claimVersion(commitId, message, historyIds);
        } catch (IOException e) {
            throw new ConradException(e);
        }
    }

    @Override
    public void tagVersion(VersionEntry versionEntry) throws ConradException {
        gitManager.tag(versionEntry);
    }

    @Override
    public VersionEntry getCurrentVersion() throws ConradException {
        return getCurrentVersion(50);
    }

    @Override
    public VersionEntry getCurrentVersion(int historyLength) throws ConradException {
        try {
            VersionEntry currentVersion = httpConradClient.getCurrentVersion(gitManager.getPreviousCommitIds(historyLength));

            if (!StringUtils.equals(currentVersion.getCommitId(), gitManager.getCurrentCommitDetails().getId())) {
                return currentVersion.toNextSnapshot();
            }

            if (gitManager.hasChanges()) {
                return currentVersion.toNextSnapshot();
            }

            return currentVersion;
        } catch (IOException e) {
            throw new ConradException(e);
        }
    }

    @Override
    public ScmManager getScmManager() {
        return gitManager;
    }
}
