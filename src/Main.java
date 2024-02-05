import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import config.Secrets;
import gsheetsapi.SheetsServiceUtil;
import reclaimapi.Event;
import sredtimesheet.EventFetcher;
import sredtimesheet.EventProcessor;
import sredtimesheet.HttpClientManager;
import sredtimesheet.TimeEntry;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {


    private static final String USER_ENTERED = "USER_ENTERED";
    public static final String SHEETS_SECRET_JSON = "src/resources/never-update-a-timesheet-abc8359b0609.json";

    public static void main(String[] args) throws IOException, InterruptedException {
        final HttpClient httpClient = new HttpClientManager().getHttpClient();
        final List<Event> myEvents = new EventFetcher(httpClient).fetchEvents();
        final TimeEntry timeEntry = new EventProcessor(myEvents).processEvents();


        //todo-ck refactor below into a "SheetsUpdater" class
        //spreadsheet id and range
        final Secrets secrets = new Secrets();
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
                if (!row.isEmpty() && row.get(0).toString().equals(formatDateToSearchableString(LocalDate.now()))) {
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

    private static String formatDateToSearchableString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM d (E)"));
    }

    private static void executeSheetsUpdate(Sheets sheetsService, String sheetId, String cellRange, List<List<Object>> listOfListOfValues) throws IOException {
        final ValueRange body = new ValueRange().setValues(listOfListOfValues);
        sheetsService.spreadsheets().values()
                .update(sheetId, cellRange, body)
                .setValueInputOption(USER_ENTERED)
                .execute();
    }
}