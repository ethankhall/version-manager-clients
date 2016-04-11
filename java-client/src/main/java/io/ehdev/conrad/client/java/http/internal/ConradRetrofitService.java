package io.ehdev.conrad.client.java.http.internal;

import io.ehdev.conrad.model.commit.CommitIdCollection;
import io.ehdev.conrad.model.repository.GetRepoResponse;
import io.ehdev.conrad.model.version.CreateVersionRequest;
import io.ehdev.conrad.model.version.CreateVersionResponse;
import io.ehdev.conrad.model.version.VersionSearchResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConradRetrofitService {

    @GET("/api/v1/project/{projectName}/repo/{repoName}")
    Call<GetRepoResponse> getProjectDetails(@Path("projectName") String projectName,
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
