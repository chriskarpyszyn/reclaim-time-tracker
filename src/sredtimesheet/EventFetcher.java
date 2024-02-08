package sredtimesheet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Secrets;
import reclaimapi.Event;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


public class EventFetcher
{
    private final HttpRequest httpRequest;
    private final HttpClient httpClient;
    private final String apiKey;

    private LocalDate today;
    private LocalDate tomorrow;

    public EventFetcher(HttpClient httpClient, LocalDate today) {
        this.httpClient = httpClient;
        this.apiKey = new Secrets().getApiSecret();
        final LocalDate tomorrow = today.plusDays(1);
        this.httpRequest = buildHttpRequest(this.apiKey, buildRequestUrl(today.toString(), tomorrow.toString()));
    }

    private HttpRequest buildHttpRequest(String apiKey, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
    }

    private String buildRequestUrl(String start, String end) {
        //https://api.app.reclaim.ai/api/events?start=2024-02-02&end=2024-02-03&sourceDetails=true&calendarIds=680951%2C1391596
        final String base_url = "https://api.app.reclaim.ai/api/";
        final String api = "events";
        final String sourceDetails = "true";
        final String calendarIds = "2C1391596";

        return base_url + api +
                "?start=" + start +
                "&end=" + end +
                "&sourceDtails=" + sourceDetails +
                "&calendarIds=" + calendarIds;
    }

    private List<Event> serializeResponse(HttpResponse response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(response.body().toString(), new TypeReference<>() {});
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public List<Event> fetchEvents() throws IOException, InterruptedException {
        final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        final List<Event> myEvents = serializeResponse(response);
        return myEvents;
    }
}
