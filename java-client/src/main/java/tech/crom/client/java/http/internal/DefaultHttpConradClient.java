package tech.crom.client.java.http.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import crom.tech.api.models.version.CreateVersionRequest;
import crom.tech.api.models.version.VersionDescription;
import crom.tech.api.models.version.VersionSearchRequest;
import tech.crom.client.java.RepoDetails;
import tech.crom.client.java.http.HttpConradClient;
import tech.crom.client.java.http.VersionEntry;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DefaultHttpConradClient implements HttpConradClient {

    private final RepoDetails repoDetails;
    private final ConradRetrofitService conradRetrofitService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        Response<VersionDescription> execute = conradRetrofitService.claimVersion(repoDetails.projectName, repoDetails.repoName, createVersionRequest).execute();

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
        VersionDescription response = execute.body();
        return new VersionEntry(response.getVersionParts(), response.getPostfix(), response.getCommitId());
    }

    @Override
    public VersionEntry getCurrentVersion(List<String> history) throws IOException {
        VersionSearchRequest commits = new VersionSearchRequest(history);
        Response<VersionDescription> execute = conradRetrofitService.findVersion(repoDetails.projectName, repoDetails.repoName, commits).execute();

        if (execute.isSuccessful()) {
            VersionDescription version = execute.body();
            return new VersionEntry(version.getVersionParts(), version.getPostfix(), version.getCommitId());
        } else {
            return FALLBACK_VERSION.toNextSnapshot();
        }
    }
}
