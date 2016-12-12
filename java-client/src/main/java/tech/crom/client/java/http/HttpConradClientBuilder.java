package tech.crom.client.java.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.crom.client.java.RepoDetails;
import tech.crom.client.java.http.internal.ConradRetrofitService;
import tech.crom.client.java.http.internal.DefaultHttpConradClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpConradClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(HttpConradClientBuilder.class);
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
            okHttpClientBuilder.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader(X_AUTH_TOKEN, repoDetails.authToken).build();
                    return chain.proceed(request);
                }
            });
        }
        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(10, TimeUnit.SECONDS);

        if (logger.isInfoEnabled()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            okHttpClientBuilder.addInterceptor(logging);
        } else if (logger.isDebugEnabled()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(logging);
        }

        ObjectMapper kotlinObjectMapper = new ObjectMapper();
        kotlinObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(repoDetails.baseUrl)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(kotlinObjectMapper))
                .build();

        return new DefaultHttpConradClient(repoDetails, retrofit.create(ConradRetrofitService.class));
    }
}
