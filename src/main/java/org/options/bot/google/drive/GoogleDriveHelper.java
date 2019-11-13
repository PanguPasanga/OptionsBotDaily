package org.options.bot.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import org.options.bot.google.GoogleAPIException;
import org.options.bot.google.GoogleAPIHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoogleDriveHelper {
    public static final int PORT = 8889;
    private static final List<String> DRIVE_SCOPES = Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_METADATA);

    public static Drive createDriveService(NetHttpTransport httpTransport, JsonFactory jsonFactory) throws GoogleDriveException {
        Credential credentials = null;
        try {
            credentials = GoogleAPIHelper.getCredentials(httpTransport, jsonFactory, GoogleAPIHelper.CREDENTIALS_PATH, GoogleAPIHelper.TOKEN_DIR_PATH, DRIVE_SCOPES, PORT);
            return new Drive.Builder(httpTransport, jsonFactory, credentials)
                    .setApplicationName(GoogleAPIHelper.APPLICATION_NAME)
                    .build();
        } catch (GoogleAPIException e) {
            throw new GoogleDriveException(e);
        }
    }

    public static Permission shareFile(Drive drive, String fileId, String type, String role, String email) throws GoogleDriveException {
        Permission permission = new Permission()
                .setType(type)
                .setRole(role)
                .setEmailAddress(email);
        try {
            Permission permission1 = drive.permissions().create(fileId, permission).setSendNotificationEmail(false).execute();
            return permission;
        } catch (IOException e) {
            throw new GoogleDriveException(e);
        }
    }

    public static String searchSpreadSheet(Drive drive, String spreadSheetName) throws GoogleDriveException {
        String pageToken = null;
        do {
            Drive.Files.List request = null;
            try {
                request = drive.files().list().setQ(
                        "mimeType='application/vnd.google-apps.spreadsheet' and trashed=false")
                        .setPageToken(pageToken);
                FileList files = request.execute();
                for (File file : files.getFiles()) {
                    System.out.println("Id : " + file.getId() + " Name : " + file.getName());
                    if (file.getName().equals(spreadSheetName)) {
                        return file.getId();
                    }
                }
                pageToken = files.getNextPageToken();
            } catch (IOException e) {
                throw new GoogleDriveException(e);
            }
        } while (pageToken!=null);
        return null;
    }
}
