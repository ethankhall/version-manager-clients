package io.ehdev.conrad.client.gradle;

import io.ehdev.conrad.client.java.common.ConradClient;
import io.ehdev.conrad.client.java.common.ConradClientBuilder;
import io.ehdev.conrad.client.java.exception.ConradException;
import io.ehdev.conrad.client.java.http.VersionEntry;
import org.apache.commons.lang.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.util.GFileUtils;

import java.io.File;

class VersionRequester {

    private static final Logger logger = Logging.getLogger(VersionRequester.class);

    private final VersionManagerExtension extension;
    private final File rootProjectDir;
    private final File backupFile;
    private boolean onlyFinalVersion;
    private VersionEntry version;

    VersionRequester(VersionManagerExtension extension, boolean finalVersion, Project rootProject) {
        this.extension = extension;
        this.rootProjectDir = rootProject.getProjectDir();
        this.backupFile = new File(rootProject.getProjectDir(), ".gradle/version-manager.txt");
        this.onlyFinalVersion = finalVersion;

        for (String taskName : rootProject.getGradle().getStartParameter().getTaskNames()) {
            if(taskName.matches("(:?)claimVersion$")) {
                onlyFinalVersion = true;
                break;
            }
        }
    }

    private void evaluate() {
        try {
            ConradClient client = new ConradClientBuilder(extension).build(rootProjectDir);
            if(onlyFinalVersion) {
                version = client.getCurrentVersion(1);
            } else {
                version = client.getCurrentVersion();
            }
            persistBackup();
        } catch (ConradException e) {
            String versionString = getBackup();
            if(null == versionString) {
                logger.error("Unable to get version ({}), using default version.", e.getMessage());
                versionString = "0.0.1-SNAPSHOT";
            } else {
                logger.warn("Unable to get version ({}), using last version.", e.getMessage());
            }
            version = new VersionEntry(versionString, "unknown");
        }
    }

    private void persistBackup() {
        GFileUtils.writeFile(version.getVersion(), backupFile);
    }

    private String getBackup() {
        if(!backupFile.exists()) {
            return null;
        }

        try {
            String version = GFileUtils.readFile(backupFile);
            return StringUtils.trimToNull(version);
        } catch(UncheckedIOException ignore) {
            return null;
        }
    }

    @Override
    public String toString() {
        if(null == version) {
            evaluate();
        }
        return version.getVersion();
    }
}
