package io.ehdev.conrad.client.gradle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ehdev.conrad.client.java.common.ConradClient;
import io.ehdev.conrad.client.java.common.ConradClientBuilder;
import io.ehdev.conrad.client.java.exception.ConradException;
import io.ehdev.conrad.client.java.http.VersionEntry;
import org.gradle.api.Project;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

class VersionRequester {

    private static final Logger logger = Logging.getLogger(VersionRequester.class);
    private final static ObjectMapper om = new ObjectMapper();

    private final VersionManagerExtension extension;
    private final File rootProjectDir;
    private final File backupFile;
    private VersionEntry version;


    VersionRequester(VersionManagerExtension extension, Project rootProject) {
        this.extension = extension;
        this.rootProjectDir = rootProject.getProjectDir();
        this.backupFile = new File(rootProject.getProjectDir(), ".gradle/version-manager.txt");
    }

    private void evaluate() {
        try {
            ConradClient client = new ConradClientBuilder(extension).build(rootProjectDir);
            version = client.getCurrentVersion();
            persistBackup();
        } catch (ConradException e) {
            version = getBackup();
            if (null == version) {
                logger.error("Unable to get version ({}), using default version.", e.getMessage());
                version = new VersionEntry(Arrays.asList("0", "0", "1"), "SNAPSHOT", "unknown");
            } else {
                logger.warn("Unable to get version ({}), using last version.", e.getMessage());
            }
        }
    }

    private void persistBackup() {
        try {
            StringWriter sw = new StringWriter();
            om.writeValue(sw, version);
            GFileUtils.writeFile(sw.toString(), backupFile);
        } catch (IOException ignored) {

        }
    }

    private VersionEntry getBackup() {
        if (!backupFile.exists()) {
            return null;
        }

        try {
            String version = GFileUtils.readFile(backupFile);
            return om.readValue(version, VersionEntry.class);
        } catch (UncheckedIOException | IOException ignore) {
            return null;
        }
    }

    @Override
    public String toString() {
        if (null == version) {
            evaluate();
        }
        return version.getVersion();
    }
}