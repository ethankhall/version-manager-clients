package tech.crom.client.gradle;

import org.gradle.api.tasks.Internal;
import tech.crom.client.java.common.CromClient;
import tech.crom.client.java.common.ConradClientBuilder;
import tech.crom.client.java.exception.ConradException;
import tech.crom.client.java.http.VersionEntry;
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

    @Internal
    private CromClient cromClient;

    @TaskAction
    public void claimVersion() throws IOException {
        if(!commitId.isPresent() || !message.isPresent() || !historyIds.isPresent()) {
            throw new RuntimeException("CommitId, Message and History must not be null");
        }

        try {
            CromClient cromClient = getCromClient();
            VersionEntry version = cromClient.claimVersion(getCommitId(), getMessage(), getHistoryIds());
            if(isTagCommit()) {
                cromClient.tagVersion(version);
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

    private CromClient getCromClient() {
        if(cromClient == null) {
            VersionManagerExtension versionExtension = getVersionExtension();
            cromClient = new ConradClientBuilder(versionExtension).build(getProject().getRootDir());
        }
        return cromClient;
    }

    @Input
    public VersionManagerExtension getVersionExtension() {
        return getProject().getExtensions().getByType(VersionManagerExtension.class);
    }

    @Input
    public List<String> getHistoryIds() throws IOException {
        CromClient cromClient = getCromClient();
        if(!historyIds.isPresent()) {
            setHistoryIds(cromClient.getScmManager().getPreviousCommitIds(50));
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
            setMessage(getCromClient().getScmManager().getCurrentCommitDetails().getMessage());
        }

        return message.orElseGet(null);
    }

    public void setMessage(String message) {
        this.message = Optional.of(message);
    }

    @Input
    public String getCommitId() throws IOException {
        CromClient cromClient = getCromClient();
        if(!commitId.isPresent()) {
            setCommitId(cromClient.getScmManager().getCurrentCommitDetails().getId());
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
