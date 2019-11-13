package org.options.bot.google.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.options.bot.google.GoogleAPIException;
import org.options.bot.google.GoogleAPIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSheetHelper {

    public static final int PORT = 8888;
    private static final List<String> SHEET_SCOPES = Arrays.asList(SheetsScopes.DRIVE, SheetsScopes.SPREADSHEETS);

    public static Sheets createSheetService(NetHttpTransport httpTransport, JsonFactory jsonFactory) throws GoogleSheetException {
        Credential credentials = null;
        try {
            credentials = GoogleAPIHelper.getCredentials(httpTransport, jsonFactory, GoogleAPIHelper.CREDENTIALS_PATH, GoogleAPIHelper.TOKEN_DIR_PATH, SHEET_SCOPES, PORT);
            return new Sheets.Builder(httpTransport, jsonFactory, credentials)
                    .setApplicationName(GoogleAPIHelper.APPLICATION_NAME)
                    .build();
        } catch (GoogleAPIException e) {
            throw new GoogleSheetException(e);
        }
    }

    public static Spreadsheet createSpreadSheetToDrive(Spreadsheet spreadsheet, Sheets sheetService) throws GoogleSheetException {
        try {
            return sheetService.spreadsheets()
                    .create(spreadsheet)
                    .setFields("spreadsheetId")
                    .execute();
        } catch (IOException e) {
            throw new GoogleSheetException(e);
        }
    }

    public static Spreadsheet createSpreadSheet(String name) {
        return createSpreadSheet(name, null);
    }

    public static Spreadsheet createSpreadSheet(String name, List<Sheet> sheets) {
        SpreadsheetProperties properties = new SpreadsheetProperties();
        properties.setTitle(name);
        return new Spreadsheet()
                .setProperties(properties)
                .setSheets(sheets);
    }

    public static Sheet createSheet(String name) {
        SheetProperties properties = new SheetProperties()
                .setTitle(name);
        return new Sheet().setProperties(properties);
    }

    public static List<Sheet> createSheets(List<String> names) {
        List<Sheet> sheets = new ArrayList<>();
        for (String name : names) {
            Sheet sheet = createSheet(name);
            sheets.add(sheet);
        }
        return sheets;
    }

    public static ValueRange getValues(Sheets sheets, String spreadSheetId, String range) throws GoogleSheetException {
        try {
            return sheets.spreadsheets().values().get(spreadSheetId, range).execute();
        } catch (IOException e) {
            throw new GoogleSheetException(e);
        }
    }

    public static void writeValues(Sheets service, String spreadsheetId, String range, List<List<Object>> data) throws GoogleSheetException {
        ValueRange valueRange = new ValueRange();
        valueRange.setRange(range);
        valueRange.setValues(data);
        try {
            UpdateValuesResponse response = service.spreadsheets()
                    .values()
                    .update(spreadsheetId, range, valueRange)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            throw new GoogleSheetException(e);
        }
    }
}
