import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import gsheetsapi.SheetsServiceUtil;
import reclaimapi.Event;
import sredtimesheet.TimeEntry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class Main {

    private static final String PERSONAL_TYPE = "PERSONAL";


    public static void main(String[] args) {

        //api key
        final Secrets secrets = new Secrets();
        final String apiKey = secrets.getApiSecret();

        final LocalDate today = LocalDate.now();
        final LocalDate tomorrow = today.plusDays(1);

        try {
            HttpClient client = buildHttpClient();
            HttpRequest request = buildHttpRequest(apiKey, buildRequestUrl(today.toString(), tomorrow.toString()));
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Event> myEvents = serializeResponse(response);
//          System.out.println("Status Code: " + response.statusCode());
//          System.out.println("Response Body: " + response.body());
            TimeEntry timeEntry = new TimeEntry();
            for (Event e : myEvents) {
//              //adding a filter for large blocks, typically "all day" blocks.
                //i quickly don't see an obvious field to track that.
                if (e.getTimeChunks() <=30 && !e.getType().equals(PERSONAL_TYPE)) {
                    timeEntry.addTime(e);
                }
            }
            System.out.println("TimeEntry for: " + timeEntry.getEntryDate());
            System.out.println("Total Time Today: " + timeEntry.getTotalTime());
            System.out.println("Total Sredable Time: " + timeEntry.getSredableTime());
            System.out.println("Total Irapable Time: " + timeEntry.getIrapableTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //spreadsheet id and range
        final String sheetId = secrets.getSpreadSheetId();
        String range = secrets.getSpreadSheetTabName()+"!A:A";

        //find the row
        List<List<Object>> sheetsResponseValues = null;
        Sheets sheetsService = null;
        try {
            sheetsService = SheetsServiceUtil.getSheetsService("src/resources/never-update-a-timesheet-abc8359b0609.json");
            Sheets.Spreadsheets.Values.Get request = sheetsService.spreadsheets().values().get(sheetId, range);
            ValueRange sheetsResponse = request.execute();
            sheetsResponseValues = sheetsResponse.getValues();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int rowIndex = -1;
        String searchValue = "Feb 2 (Fri)"; //todo-ck need to generate this string

        if (sheetsResponseValues == null || sheetsResponseValues.isEmpty()) {
            System.out.println("No Data Found.");
        } else {
            for (List row : sheetsResponseValues) {
                rowIndex++;
                if (!row.isEmpty() && row.get(0).toString().equals(searchValue)) {
                    System.out.println("Found the row at: " + (rowIndex+1));
                    break;
                }
            }
        }

        //update the row
        if (rowIndex != -1) {
            //we have the row index of the row with the date
            String cellRange = secrets.getSpreadSheetTabName()+"!B" + (rowIndex+1); //B column
            List<List<Object>> newValues = List.of(
                    List.of(100) //todo-ck put in real values for the rows
            );

            ValueRange body = new ValueRange().setValues(newValues);
            try {
                sheetsService.spreadsheets().values()
                        .update(sheetId, cellRange, body)
                        .setValueInputOption("USER_ENTERED")
                        .execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("CELL UPDATED!");

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