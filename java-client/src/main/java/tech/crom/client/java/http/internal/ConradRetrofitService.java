package tech.crom.client.java.http.internal;

import crom.tech.api.models.CommitIdCollection;
import crom.tech.api.models.CreateVersionRequest;
import crom.tech.api.models.CreateVersionResponse;
import crom.tech.api.models.VersionSearchResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tech.crom.client.java.RepoDetails;

public interface ConradRetrofitService {

    @GET("/api/v1/project/{projectName}/repo/{repoName}")
    Call<RepoDetails> getRepoDetails(@Path("projectName") String projectName,
                                     @Path("repoName") String repoName);

    @POST("/api/v1/project/{projectName}/repo/{repoName}/search/version")
    Call<VersionSearchResponse> findVersion(@Path("projectName") String projectName,
                                            @Path("repoName") String repoName,
                                            @Body CommitIdCollection commitIdCollection);

    @POST("/api/v1/project/{projectName}/repo/{repoName}/version")
    Call<CreateVersionResponse> claimVersion(@Path("projectName") String projectName,
                                             @Path("repoName") String repoName,
                                             @Body CreateVersionRequest createVersionRequest);
}
