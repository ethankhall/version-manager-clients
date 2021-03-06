package tech.crom.client.java.http.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import crom.tech.api.models.CommitIdCollection;
import crom.tech.api.models.CreateVersionRequest;
import crom.tech.api.models.CreateVersionResponse;
import crom.tech.api.models.VersionSearchResponse;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;
import tech.crom.client.java.RepoDetails;
import tech.crom.client.java.http.HttpConradClient;
import tech.crom.client.java.http.VersionEntry;

import java.io.IOException;
import java.util.List;

public class DefaultHttpConradClient implements HttpConradClient {

    private final RepoDetails repoDetails;
    private final ConradRetrofitService conradRetrofitService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final VersionEntry FALLBACK_VERSION = new VersionEntry("0.0.0", "<unknown>");

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

        if(execute.code() == 401) {
            throw new RuntimeException("The token that you used was not accepted, check that it is valid.");
        }
        if(!execute.isSuccessful()) {
            try {
                ErrorResponse err = objectMapper.readValue(execute.errorBody().string(), ErrorResponse.class);
                throw new RuntimeException(
                    String.format("(HTTP %d) Error %s: %s", err.getStatus(), err.getErrorCode(), err.getMessage()));
            } catch(Exception e) {
                throw new RuntimeException("Unable to claim version: " + execute.errorBody().string());
            }
        }
        CreateVersionResponse response = execute.body();
        return new VersionEntry(response.getVersion(), response.getCommitId());
    }

    @Override
    public VersionEntry getCurrentVersion(List<String> history) throws IOException {
        CommitIdCollection commits = new CommitIdCollection(history);
        Response<VersionSearchResponse> execute = conradRetrofitService.findVersion(repoDetails.projectName, repoDetails.repoName, commits).execute();

        if (execute.isSuccessful()) {
            VersionSearchResponse version = execute.body();
            return new VersionEntry(version.getVersion(), version.getCommitId());
        } else {
            return FALLBACK_VERSION.toNextSnapshot();
        }
    }
}
