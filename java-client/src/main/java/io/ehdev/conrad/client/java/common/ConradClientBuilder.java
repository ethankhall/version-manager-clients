package io.ehdev.conrad.client.java.common;

import io.ehdev.conrad.client.java.RepoDetails;
import io.ehdev.conrad.client.java.common.internal.DefaultGitConradClient;
import io.ehdev.conrad.client.java.git.GitManager;
import io.ehdev.conrad.client.java.git.GitManagerBuilder;
import io.ehdev.conrad.client.java.http.HttpConradClient;
import io.ehdev.conrad.client.java.http.HttpConradClientBuilder;

import java.io.File;
import java.io.IOException;

public class ConradClientBuilder {

    private RepoDetails repoDetails;

    public ConradClientBuilder(RepoDetails repoDetails) {
        this.repoDetails = repoDetails;
    }

    public ConradClientBuilder(String projectName, String repoName, String authToken) {
        this(new RepoDetails(projectName, repoName, authToken));
    }

    public ConradClient build(File projectDir) {
        try {
            GitManager gitManager = new GitManagerBuilder(projectDir).build();
            HttpConradClient httpClient = new HttpConradClientBuilder(repoDetails).build();

            return new DefaultGitConradClient(httpClient, gitManager);
        } catch (IOException ioe) {
            throw new RuntimeException("Only the git client is supported at this time.");
        }
    }
}
