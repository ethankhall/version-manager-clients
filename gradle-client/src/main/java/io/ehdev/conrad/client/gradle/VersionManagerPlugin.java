package io.ehdev.conrad.client.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class VersionManagerPlugin implements Plugin<Project> {

    public static final String FINAL_VERSION_PROPERTY = "finalVersion";

    @Override
    public void apply(Project project) {
        VersionManagerExtension versionManager = project.getExtensions().create("versionManager", VersionManagerExtension.class);
        final VersionRequester versionRequester = new VersionRequester(versionManager, isFinalVersionBuild(project), project);
        project.setVersion(versionRequester);
        project.allprojects(p -> { p.setVersion(versionRequester); });

        ClaimVersionTask claimVersion = project.getTasks().create("claimVersion", ClaimVersionTask.class);
        claimVersion.setDescription("Claims a version from the version-manager service.");
        claimVersion.setGroup("Version Manager");
    }

    boolean isFinalVersionBuild(Project project) {
        return project.hasProperty(FINAL_VERSION_PROPERTY)
            && "true".equalsIgnoreCase(project.property(FINAL_VERSION_PROPERTY).toString());
    }
}
