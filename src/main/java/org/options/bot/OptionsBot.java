package org.options.bot;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.options.bot.chain.analysis.OptionChainPuller;
import org.options.bot.google.GoogleAPIException;
import org.options.bot.google.GoogleAPIHelper;
import org.options.bot.google.drive.GoogleDriveHelper;
import org.options.bot.google.sheets.GoogleSheetException;
import org.options.bot.google.sheets.GoogleSheetHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OptionsBot {
    public static final LocalTime NSE_SOD = LocalTime.of(9, 0, 1);
    public static final LocalTime NSE_EOD = LocalTime.of(15, 29, 59);

    public static void main(String[] args) throws GoogleAPIException {
        OptionChainPuller optionsPuller = OptionChainPuller.getInstance();
        optionsPuller.initOptionsPuller();
        List<String> strikePriceList = optionsPuller.getStrikePriceList();
        System.out.println(strikePriceList);

        NetHttpTransport httpTransport = GoogleAPIHelper.createHTTPTransport();
        JsonFactory jsonFactory = GoogleAPIHelper.createJsonFactory();

        Sheets sheetService = GoogleSheetHelper.createSheetService(httpTransport, jsonFactory);

        List<Sheet> sheets = GoogleSheetHelper.createSheets(strikePriceList);
        String spreadSheetName = "OptionsBot_" + LocalDate.now();
        Spreadsheet spreadSheet = GoogleSheetHelper.createSpreadSheet(spreadSheetName, sheets);
        Spreadsheet spreadSheet1 = GoogleSheetHelper.createSpreadSheetToDrive(spreadSheet, sheetService);
        String spreadSheetId = spreadSheet1.getSpreadsheetId();

        /*Drive driveService = GoogleDriveHelper.createDriveService(httpTransport, jsonFactory);
        Permission permission = GoogleDriveHelper.shareFile(driveService, spreadSheetId, "user", "reader", "firekarthik007@gmail.com");
        System.out.println("Permission : " + permission);*/

        int lastRow = 1;
        writeHeaders(strikePriceList, sheetService, spreadSheetId, lastRow);
        lastRow++;
        writeOptionsDataPeriodically(optionsPuller, spreadSheetId, sheetService, strikePriceList, lastRow);
    }

    private static void writeHeaders(List<String> strikePriceList, Sheets sheetService, String spreadSheetId, int lastRow) throws GoogleSheetException {
        final String CELL_RANGE = "!A"+lastRow+":L"+lastRow;
        List<List<Object>> data = new ArrayList<>();
        List<Object> rowData = new ArrayList<>();
        rowData.add("Time");
        rowData.add("CE_OI");
        rowData.add("CE_ChangeInOI");
        rowData.add("CE_Vol");
        rowData.add("CE_IV");
        rowData.add("CE_LTP");
        rowData.add("PE_LTP");
        rowData.add("PE_IV");
        rowData.add("PE_Vol");
        rowData.add("PE_ChangeInOI");
        rowData.add("PE_OI");
        rowData.add("LivePrice");
        data.add(rowData);
        for (String strikePrice : strikePriceList) {
            String range = strikePrice + CELL_RANGE;
            GoogleSheetHelper.writeValues(sheetService, spreadSheetId, range, data);
        }
    }

    private static void writeOptionsData(OptionChainPuller optionsPuller, String spreadSheetId, Sheets sheetService, List<String> strikePriceList, int lastRow) throws GoogleSheetException {
        final String CELL_RANGE = "!A"+lastRow+":L"+lastRow;
        for (String strikePrice : strikePriceList) {
            List<Object> optionData = optionsPuller.getOptionData(strikePrice);
            List<List<Object>> data = new ArrayList<>();
            data.add(optionData);
            String range = strikePrice + CELL_RANGE;
            GoogleSheetHelper.writeValues(sheetService, spreadSheetId, range, data);
        }
    }

    public static void writeOptionsDataPeriodically(OptionChainPuller optionsPuller, String spreadSheetId, Sheets sheetService, List<String> strikePriceList, int lastRow) throws GoogleSheetException {
        final long INITIAL_DELAY = 0;
        final long INTERVAL = 900000;    // 15mins
        Timer timer = new Timer("OptionsBot");
        TimerTask task = new TimerTask() {
            int row = lastRow;
            @Override
            public void run() {
                try {
                    System.out.println("Polling data @ " + LocalTime.now());
                    optionsPuller.refresh();
                    writeOptionsData(optionsPuller, spreadSheetId, sheetService, strikePriceList, row++);
                } catch (GoogleSheetException e) {
                    System.out.println(e);
                }
                if (checkTimeToQuit()) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        System.out.println("Scheduler started @ " + LocalDateTime.now());
        timer.schedule(task, INITIAL_DELAY, INTERVAL);
    }

    public static boolean checkTimeToQuit() {
        LocalTime now = LocalTime.now();
        return now.isAfter(NSE_EOD);
    }

}