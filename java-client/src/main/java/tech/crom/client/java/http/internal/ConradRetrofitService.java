package tech.crom.client.java.http.internal;

import crom.tech.api.models.repo.RepoDescription;
import crom.tech.api.models.version.CreateVersionRequest;
import crom.tech.api.models.version.VersionDescription;
import crom.tech.api.models.version.VersionSearchRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConradRetrofitService {

    @GET("/api/v1/project/{projectName}/repo/{repoName}")
    Call<RepoDescription> getProjectDetails(@Path("projectName") String projectName,
                                            @Path("repoName") String repoName);

    @POST("/api/v1/project/{projectName}/repo/{repoName}/search/version")
    Call<VersionDescription> findVersion(@Path("projectName") String projectName,
                                  @Path("repoName") String repoName,
                                  @Body VersionSearchRequest commitIdCollection);

    @POST("/api/v1/project/{projectName}/repo/{repoName}/version")
    Call<VersionDescription> claimVersion(@Path("projectName") String projectName,
                                          @Path("repoName") String repoName,
                                          @Body CreateVersionRequest createVersionRequest);
}
