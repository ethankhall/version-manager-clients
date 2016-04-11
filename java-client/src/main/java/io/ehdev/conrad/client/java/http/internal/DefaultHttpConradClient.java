package io.ehdev.conrad.client.java.http.internal;

import io.ehdev.conrad.client.java.RepoDetails;
import io.ehdev.conrad.client.java.http.HttpConradClient;
import io.ehdev.conrad.client.java.http.VersionEntry;
import io.ehdev.conrad.model.commit.CommitIdCollection;
import io.ehdev.conrad.model.version.CreateVersionRequest;
import io.ehdev.conrad.model.version.CreateVersionResponse;
import io.ehdev.conrad.model.version.VersionSearchResponse;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class DefaultHttpConradClient implements HttpConradClient {

    private final RepoDetails repoDetails;
    private final ConradRetrofitService conradRetrofitService;

    private static final VersionEntry FALLBACK_VERSION = new VersionEntry("0.0.1", "<unknown>");

    public DefaultHttpConradClient(RepoDetails repoDetails, ConradRetrofitService conradRetrofitService) {
        this.repoDetails = repoDetails;
        this.conradRetrofitService = conradRetrofitService;
    }

    @Override
    public VersionEntry claimVersion(String commitId, String message, List<String> history) throws IOException {
        CreateVersionRequest createVersionRequest = new CreateVersionRequest(history, message, commitId);
        Response<CreateVersionResponse> execute = conradRetrofitService.claimVersion(repoDetails.projectName, repoDetails.repoName, createVersionRequest).execute();

        CreateVersionResponse response = execute.body();
        return new VersionEntry(response.getVersion(), response.getCommitId());
    }

    @Override
    public VersionEntry getCurrentVersion(List<String> history) throws IOException {
        CommitIdCollection commitIdCollection = new CommitIdCollection(history);
        Response<VersionSearchResponse> execute = conradRetrofitService.findVersion(repoDetails.projectName, repoDetails.repoName, commitIdCollection).execute();

        if (execute.isSuccessful()) {
            VersionSearchResponse version = execute.body();
            return new VersionEntry(version.getVersion(), version.getCommitId());
        } else {
            return FALLBACK_VERSION.toSnapshot();
        }
    }
}
