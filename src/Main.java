import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import reclaimapi.Event;
import sredtimesheet.TimeEntry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class Main {

    private static final String PERSONAL_TYPE = "PERSONAL";

    public static void main(String[] args) {

        //api key
        final String apiKey = ""; //DONT LOOK AT MY KEY!

        try {
            HttpClient client = buildHttpClient();
            HttpRequest request = buildHttpRequest(apiKey, buildRequestUrl("2024-02-02", "2024-02-03"));
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Event> myEvents = serializeResponse(response);
//            System.out.println("Status Code: " + response.statusCode());
//            System.out.println("Response Body: " + response.body());
            TimeEntry timeEntry = new TimeEntry();
            for (Event e : myEvents) {
//                System.out.println(e.getTitle() + "  " + e.getType());
                if (e.getTimeChunks() <=30 && !e.getType().equals(PERSONAL_TYPE)) {
                    timeEntry.addTime(e);
                }
            }
            System.out.println("TimeEntry for: " + timeEntry.getEntryDate());
            System.out.println("Total Time Today: " + timeEntry.getTotalTime());
            System.out.println("Total Sredable Time: " + timeEntry.getSredableTime());
            System.out.println("Total Irapable Time: " + timeEntry.getIrapableTime());
        } catch (Exception e) {
            System.out.println("Crap!");
            e.printStackTrace();
        }
    }

    private static List<Event> serializeResponse(HttpResponse response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(response.body().toString(), new TypeReference<>() {});
    }

    private static HttpRequest buildHttpRequest(String apiKey, String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Authorization", "Bearer " + apiKey)
                .GET()
                .build();
    }

    private static HttpClient buildHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    private static String buildRequestUrl(String start, String end) {
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
}