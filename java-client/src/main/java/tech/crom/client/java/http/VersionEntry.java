package tech.crom.client.java.http;

/**
 * A container of version information.
 * <p>
 * The {@literal postfix} is "SNAPSHOT" for "1.2.3-SNAPSHOT"
 * The {@literal commitId} is the commit that the current version comes from
 * The {@literal versionParts} are a list of String that represent the version.
 */
public class VersionEntry {
    private final String version;
    private final String commitId;

    public VersionEntry(String version, String commitId) {
        this.version = version;
        this.commitId = commitId;
    }

    /**
     * @return The version string
     */
    public String getVersion() {
        return this.version;
    }

    public String getCommitId() {
        return commitId;
    }

    public VersionEntry toNextSnapshot() {
        String beforePostfix = version.split("-")[0];
        String[] parts = beforePostfix.split("\\.");
        parts[parts.length - 1] = Integer.toString(Integer.parseInt(parts[parts.length - 1]) + 1);
        StringBuilder newVersion = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            newVersion.append(parts[i]);
            if (i != parts.length - 1) {
                newVersion.append(".");
            }
        }

        newVersion.append("-SNAPSHOT");
        return new VersionEntry(newVersion.toString(), null);
    }

    @Override
    public String toString() {
        return getVersion();
    }
}
