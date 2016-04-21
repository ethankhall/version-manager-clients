package io.ehdev.conrad.client.gradle;

import io.ehdev.conrad.client.java.common.ConradClient;
import io.ehdev.conrad.client.java.common.ConradClientBuilder;
import io.ehdev.conrad.client.java.exception.ConradException;
import io.ehdev.conrad.client.java.http.VersionEntry;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This task will be called to claim a version.
 */
public class ClaimVersionTask extends DefaultTask {

    private static final Logger logger = Logging.getLogger(ClaimVersionTask.class);

    private Optional<List<String>> historyIds = Optional.empty();
    private Optional<String> message = Optional.empty();
    private Optional<String> commitId = Optional.empty();
    private boolean tagCommit = true;

    private ConradClient conradClient;

    @TaskAction
    public void claimVersion() throws IOException {
        if(!commitId.isPresent() || !message.isPresent() || !historyIds.isPresent()) {
            throw new RuntimeException("CommitId, Message and History must not be null");
        }

        try {
            ConradClient conradClient = getConradClient();
            VersionEntry version = conradClient.claimVersion(getCommitId(), getMessage(), getHistoryIds());
            if(isTagCommit()) {
                conradClient.tagVersion(version);
            }
            String versionString = version.toString();
            logger.lifecycle("Version claimed was: {}", versionString);
        } catch (ConradException e) {
            logger.error("Unable to claim version! Reason: {}", e.getMessage());
            logger.info("Unable to claim version! Reason: {}", e.getMessage(), e);
            throw new UnableToClaimVersionException(e);
        }
    }

    static class UnableToClaimVersionException extends RuntimeException {
        UnableToClaimVersionException(Exception e) {
            super(e);
        }
    }

    private ConradClient getConradClient() {
        if(conradClient == null) {
            VersionManagerExtension versionExtension = getVersionExtension();
            conradClient = new ConradClientBuilder(versionExtension).build(getProject().getRootDir());
        }
        return conradClient;
    }

    @Input
    public VersionManagerExtension getVersionExtension() {
        return getProject().getExtensions().getByType(VersionManagerExtension.class);
    }

    @Input
    public List<String> getHistoryIds() throws IOException {
        ConradClient conradClient = getConradClient();
        if(!historyIds.isPresent()) {
            setHistoryIds(conradClient.getScmManager().getPreviousCommitIds(50));
        }
        return historyIds.orElseGet(null);
    }

    public void setHistoryIds(List<String> historyIds) {
        this.historyIds = Optional.of(historyIds);
    }

    @Input
    public String getMessage() throws IOException {
        VersionManagerExtension versionExtension = getVersionExtension();
        if (versionExtension.getVersionCustomizer() != null) {
            VersionEntry versionEntry = versionExtension.getVersionRequester().getVersionEntry();
            String nextVersion = versionExtension.getVersionCustomizer().configure(versionEntry);
            setMessage(String.format("[set version %s]", nextVersion));
        } else if (!message.isPresent()) {
            setMessage(getConradClient().getScmManager().getCurrentCommitDetails().getMessage());
        }

        return message.orElseGet(null);
    }

    public void setMessage(String message) {
        this.message = Optional.of(message);
    }

    @Input
    public String getCommitId() throws IOException {
        ConradClient conradClient = getConradClient();
        if(!commitId.isPresent()) {
            setCommitId(conradClient.getScmManager().getCurrentCommitDetails().getId());
        }
        return commitId.orElseGet(null);
    }

    public void setCommitId(String commitId) {
        this.commitId = Optional.of(commitId);
    }

    @Input
    public boolean isTagCommit() {
        return tagCommit;
    }

    public void setTagCommit(boolean tagCommit) {
        this.tagCommit = tagCommit;
    }
}
