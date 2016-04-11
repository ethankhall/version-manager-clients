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

/**
 * This task will be called to claim a version.
 */
public class ClaimVersionTask extends DefaultTask {

    private static final Logger logger = Logging.getLogger(ClaimVersionTask.class);

    @Input
    public VersionManagerExtension getVersionExtension() {
        return getProject().getExtensions().getByType(VersionManagerExtension.class);
    }

    @TaskAction
    public void claimVersion() {
        VersionManagerExtension versionExtension = getVersionExtension();

        try {
            ConradClient client = new ConradClientBuilder(versionExtension).build(getProject().getRootDir());
            VersionEntry version = client.claimVersion();
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
}
