package org.options.bot.google;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.options.bot.google.drive.GoogleDriveHelper;
import org.options.bot.google.sheets.GoogleSheetHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleAPIAdapter {
    public GoogleAPIAdapter() {

    }

    public static void main(String[] args) throws GoogleAPIException, IOException {
        NetHttpTransport httpTransport = GoogleAPIHelper.createHTTPTransport();
        JsonFactory jsonFactory = GoogleAPIHelper.createJsonFactory();

        Sheets sheetService = GoogleSheetHelper.createSheetService(httpTransport, jsonFactory);

        List<Sheet> sheets = GoogleSheetHelper.createSheets(Arrays.asList("11500", "11600", "11700", "11800"));
        String spreadSheetName = "NSE_" + LocalDate.now();
        Spreadsheet spreadSheet = GoogleSheetHelper.createSpreadSheet(spreadSheetName, sheets);
        Spreadsheet spreadSheet1 = GoogleSheetHelper.createSpreadSheetToDrive(spreadSheet, sheetService);
        String spreadSheetId = spreadSheet1.getSpreadsheetId();


        Drive driveService = GoogleDriveHelper.createDriveService(httpTransport, jsonFactory);
        Permission permission = GoogleDriveHelper.shareFile(driveService, spreadSheetId, "user", "reader", "karthik131289@gmail.com");
        System.out.println("Permission : " + permission);

        List<List<Object>> data = new ArrayList<>();
        data.add(Collections.singletonList("Hello"));
        GoogleSheetHelper.writeValues(sheetService, spreadSheetId, "11600!D4", data);
    }
}
