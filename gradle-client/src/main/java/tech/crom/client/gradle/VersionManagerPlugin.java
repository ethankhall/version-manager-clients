package tech.crom.client.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import tech.crom.shade.org.apache.commons.lang.StringUtils;

public class VersionManagerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        VersionManagerExtension versionManager = project.getExtensions().create("versionManager", VersionManagerExtension.class);

        if(StringUtils.isNotBlank(System.getenv("VERSION_MANAGER_AUTH_TOKEN"))) {
            versionManager.authToken = System.getenv("VERSION_MANAGER_AUTH_TOKEN");
        } else if(project.hasProperty("versionManager.authToken")) {
            versionManager.authToken = (String) project.property("versionManager.authToken");
        }

        final VersionRequester versionRequester = new VersionRequester(versionManager, project);
        project.setVersion(versionRequester);
        project.allprojects(p -> { p.setVersion(versionRequester); });

        ClaimVersionTask claimVersion = project.getTasks().create("claimVersion", ClaimVersionTask.class);
        claimVersion.setDescription("Claims a version from the version-manager service.");
        claimVersion.setGroup("Version Manager");
    }
}
