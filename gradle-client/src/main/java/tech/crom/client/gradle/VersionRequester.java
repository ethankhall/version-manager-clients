package tech.crom.client.gradle;

import org.gradle.api.Project;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.util.GFileUtils;
import tech.crom.client.java.RepoDetails;
import tech.crom.client.java.common.ConradClient;
import tech.crom.client.java.common.ConradClientBuilder;
import tech.crom.client.java.exception.ConradException;
import tech.crom.client.java.http.VersionEntry;
import tech.crom.shade.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

class VersionRequester {

    private static final Logger logger = Logging.getLogger(VersionRequester.class);
    private final static ObjectMapper om = new ObjectMapper();

    private final RepoDetails extension;
    private final File rootProjectDir;
    private final File backupFile;
    private VersionEntry version;


    VersionRequester(VersionManagerExtension extension, Project rootProject) {
        this.extension = extension;
        this.rootProjectDir = rootProject.getProjectDir();
        this.backupFile = new File(rootProject.getProjectDir(), ".gradle/version-manager.txt");
        extension.setVersionRequester(this);
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
                version = new VersionEntry("0.0.1-SNAPSHOT", "unknown");
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

    public VersionEntry getVersionEntry() {
        if(null == version) {
            evaluate();
        }
        return version;
    }

    @Override
    public String toString() {
        if (null == version) {
            evaluate();
        }
        return version.getVersion();
    }
}
