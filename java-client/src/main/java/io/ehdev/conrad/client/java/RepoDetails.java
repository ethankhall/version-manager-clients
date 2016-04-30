package io.ehdev.conrad.client.java;

public class RepoDetails {
    public String projectName;
    public String repoName;
    public String authToken;
    public String baseUrl = "http://api.crom.tech";

    public RepoDetails(String projectName, String repoName, String authToken) {
        this.projectName = projectName;
        this.repoName = repoName;
        this.authToken = authToken;
    }

    public RepoDetails() {
    }
}
