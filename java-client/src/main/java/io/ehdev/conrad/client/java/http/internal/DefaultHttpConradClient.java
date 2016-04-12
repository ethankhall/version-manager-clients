package io.ehdev.conrad.client.java.http.internal;

import io.ehdev.conrad.client.java.RepoDetails;
import io.ehdev.conrad.client.java.http.HttpConradClient;
import io.ehdev.conrad.client.java.http.VersionEntry;
import io.ehdev.conrad.model.commit.CommitIdCollection;
import io.ehdev.conrad.model.version.CreateVersionRequest;
import io.ehdev.conrad.model.version.CreateVersionResponse;
import io.ehdev.conrad.model.version.VersionSearchResponse;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DefaultHttpConradClient implements HttpConradClient {

    private final RepoDetails repoDetails;
    private final ConradRetrofitService conradRetrofitService;

    private static final VersionEntry FALLBACK_VERSION = new VersionEntry(Arrays.asList("0", "0", "0"), null, "<unknown>");

    public DefaultHttpConradClient(RepoDetails repoDetails, ConradRetrofitService conradRetrofitService) {
        this.repoDetails = repoDetails;
        this.conradRetrofitService = conradRetrofitService;
    }

    @Override
    public VersionEntry claimVersion(String commitId, String message, List<String> history) throws IOException {
        if (StringUtils.isBlank(repoDetails.authToken)) {
            throw new RuntimeException("The auth token is not set. Please set the API via VERSION_MANAGER_AUTH_TOKEN or versionManager.authToken");
        }

        CreateVersionRequest createVersionRequest = new CreateVersionRequest(history, message, commitId);
        Response<CreateVersionResponse> execute = conradRetrofitService.claimVersion(repoDetails.projectName, repoDetails.repoName, createVersionRequest).execute();

        if(!execute.isSuccessful()) {
            throw new RuntimeException("Unable to claim version");
        }
        CreateVersionResponse response = execute.body();
        return new VersionEntry(response.getVersionParts(), response.getPostfix(), response.getCommitId());
    }

    @Override
    public VersionEntry getCurrentVersion(List<String> history) throws IOException {
        CommitIdCollection commitIdCollection = new CommitIdCollection(history);
        Response<VersionSearchResponse> execute = conradRetrofitService.findVersion(repoDetails.projectName, repoDetails.repoName, commitIdCollection).execute();

        if (execute.isSuccessful()) {
            VersionSearchResponse version = execute.body();
            return new VersionEntry(version.getVersionParts(), version.getPostfix(), version.getCommitId());
        } else {
            return FALLBACK_VERSION.toNextSnapshot();
        }
    }
}
