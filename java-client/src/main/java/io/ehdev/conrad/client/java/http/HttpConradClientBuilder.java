package io.ehdev.conrad.client.java.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.ehdev.conrad.client.java.http.internal.ConradRetrofitService;
import io.ehdev.conrad.client.java.http.internal.DefaultHttpConradClient;
import io.ehdev.conrad.client.java.RepoDetails;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public class HttpConradClientBuilder {

    public static final String X_AUTH_TOKEN = "X-AUTH-TOKEN";

    private final RepoDetails repoDetails;

    public HttpConradClientBuilder(String projectName, String repoName, String autoToken) {
        this(new RepoDetails(projectName, repoName, autoToken));
    }

    public HttpConradClientBuilder(RepoDetails repoDetails) {
        this.repoDetails = repoDetails;
    }

    public HttpConradClientBuilder withBaseUrl(String baseUrl) {
        repoDetails.baseUrl = baseUrl;
        return this;
    }

    public DefaultHttpConradClient build() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if (null != repoDetails.authToken && !repoDetails.authToken.isEmpty()) {
            okHttpClientBuilder.interceptors().add(chain -> {
                Request request = chain.request().newBuilder().addHeader(X_AUTH_TOKEN, repoDetails.authToken).build();
                return chain.proceed(request);
            });
        }
        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS);

        ObjectMapper kotlinObjectMapper = new ObjectMapper();
        kotlinObjectMapper.registerModule(new KotlinModule());
        kotlinObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(repoDetails.baseUrl)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(kotlinObjectMapper))
                .build();

        return new DefaultHttpConradClient(repoDetails, retrofit.create(ConradRetrofitService.class));
    }
}
