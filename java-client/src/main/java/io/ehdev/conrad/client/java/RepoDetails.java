package io.ehdev.conrad.client.java;

public class RepoDetails {
    public String projectName;
    public String repoName;
    public String authToken;
    public String baseUrl = "http://159.203.226.149:8080";

    public RepoDetails(String projectName, String repoName, String authToken) {
        this.projectName = projectName;
        this.repoName = repoName;
        this.authToken = authToken;
    }

    public RepoDetails() {
    }
}
