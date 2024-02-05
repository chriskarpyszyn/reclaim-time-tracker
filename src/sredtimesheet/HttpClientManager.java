package sredtimesheet;

import java.net.http.HttpClient;
import java.time.Duration;

public class HttpClientManager {

    private final HttpClient httpClient;

    public HttpClientManager() {
        httpClient = buildHttpClient();
    }

    private HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
