import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import gsheetsapi.SheetsServiceUtil;
import reclaimapi.Event;
import sredtimesheet.TimeEntry;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    private static final String PERSONAL_TYPE = "PERSONAL";
    private static final String USER_ENTERED = "USER_ENTERED";
    public static final String SHEETS_SECRET_JSON = "src/resources/never-update-a-timesheet-abc8359b0609.json";

    public static void main(String[] args) {
        //api key
        final Secrets secrets = new Secrets();
        final String apiKey = secrets.getApiSecret();

        //today and tomorrow!
        final LocalDate today = LocalDate.now();
        final LocalDate tomorrow = today.plusDays(1);
        final String dateStringToSearchFor = today.format(DateTimeFormatter.ofPattern("MMM d (E)"));

        TimeEntry timeEntry = new TimeEntry();
        try {
            final HttpClient client = buildHttpClient();
            final HttpRequest request = buildHttpRequest(apiKey, buildRequestUrl(today.toString(), tomorrow.toString()));
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final List<Event> myEvents = serializeResponse(response);
            for (Event e : myEvents) {
                //todo-ck figure out a better way to filter out all-day time blocks, this <=30 hack should work for now
                if (e.getTimeChunks() <=30 && !e.getType().equals(PERSONAL_TYPE)) {
                    timeEntry.addTime(e);
                    timeEntry.addDescription(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //spreadsheet id and range
        final String sheetId = secrets.getSpreadSheetId();
        final String dateRange = secrets.getSpreadSheetTabName()+"!A:A";

        //find the row to edit
        List<List<Object>> sheetsResponseValues = null;
        Sheets sheetsService = null;
        try {
            sheetsService = SheetsServiceUtil.getSheetsService(SHEETS_SECRET_JSON);
            Sheets.Spreadsheets.Values.Get request = sheetsService.spreadsheets().values().get(sheetId, dateRange);
            ValueRange sheetsResponse = request.execute();
            sheetsResponseValues = sheetsResponse.getValues();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int rowIndex = -1;

        if (sheetsResponseValues == null || sheetsResponseValues.isEmpty()) {
            System.out.println("No Data Found.");
        } else {
            for (List row : sheetsResponseValues) {
                rowIndex++;
                if (!row.isEmpty() && row.get(0).toString().equals(dateStringToSearchFor)) {
                    System.out.println("Found the row at: " + (rowIndex+1));
                    break;
                }
            }
        }

        //update the row
        if (rowIndex != -1) {
            //Get the cell range of column b to d, at the found row
            final String timeCellRange = secrets.getSpreadSheetTabName()+"!B" + (rowIndex+1) + ":D" + (rowIndex+1);
            final List<List<Object>> timeValues = List.of(List.of(
                            timeEntry.getTotalTime(), timeEntry.getIrapableTime(), timeEntry.getSredableTime())
            );
            try {
                executeSheetsUpdate(sheetsService, sheetId, timeCellRange, timeValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String descriptionCellRange = secrets.getSpreadSheetTabName()+"!J" + (rowIndex+1) + ":K" + (rowIndex+1);
            final List<List<Object>> descriptionValues = List.of(List.of(
                            timeEntry.getIrapDescription(), timeEntry.getSredDescription())
            );
            try {
                executeSheetsUpdate(sheetsService, sheetId, descriptionCellRange, descriptionValues);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("CELL UPDATED!");
        }
    }

    private static void executeSheetsUpdate(Sheets sheetsService, String sheetId, String cellRange, List<List<Object>> listOfListOfValues) throws IOException {
        final ValueRange body = new ValueRange().setValues(listOfListOfValues);
        sheetsService.spreadsheets().values()
                .update(sheetId, cellRange, body)
                .setValueInputOption(USER_ENTERED)
                .execute();
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