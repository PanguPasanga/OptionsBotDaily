package org.options.bot.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleAPIHelper {
    public static final String USER_ID = "user";
    public static final String APPLICATION_NAME = "NSE_DAILY";
    public static final String CREDENTIALS_PATH = "credentials.json";
    public static final String TOKEN_DIR_PATH = "tokens";

    /**
     * creates Google's HTTP Transport.
     *
     * @return
     */
    public static NetHttpTransport createHTTPTransport() throws GoogleAPIException {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException e) {
            throw new GoogleAPIException(e);
        } catch (IOException e) {
            throw new GoogleAPIException(e);
        }
    }

    public static JsonFactory createJsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, JsonFactory jsonFactory, String credentialsPath,
                                            String tokenDirPath, List<String> scopes, int port) throws GoogleAPIException {
        try {
            GoogleClientSecrets clientSecrets = loadCredentials(credentialsPath, jsonFactory);
            FileDataStoreFactory dataStoreFactory = createDataStoreFactory(tokenDirPath);
            GoogleAuthorizationCodeFlow authFlow = buildAuthorizationFlow(HTTP_TRANSPORT, jsonFactory, clientSecrets, dataStoreFactory, scopes);
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(port).build();
            return new AuthorizationCodeInstalledApp(authFlow, receiver).authorize(GoogleAPIHelper.USER_ID);
        } catch (IOException e) {
            throw new GoogleAPIException(e);
        }
    }

    /**
     * loads credentials from credentials.json file.
     * @param credentialsPath path of the credentials.json file
     * @param jsonFactory {@link JsonFactory} to parse credentials.json file
     * @return
     * @throws IOException
     */
    public static GoogleClientSecrets loadCredentials(String credentialsPath, JsonFactory jsonFactory) throws IOException {
        InputStream in = GoogleAPIHelper.class.getClassLoader().getResourceAsStream(credentialsPath);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsPath);
        }
        GoogleClientSecrets clientSecrets = null;
        return GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
    }

    /**
     * creates {@link DataStoreFactory} based on provided directory path, where the authorization tokens are stored.
     * @param dirPath
     * @return
     * @throws IOException
     */
    public static FileDataStoreFactory createDataStoreFactory(String dirPath) throws IOException {
        return new FileDataStoreFactory(new File(dirPath));
    }

    /**
     * builds Google's AuthorizationFlow
     * @param HTTP_TRANSPORT
     * @param jsonFactory
     * @param clientSecrets
     * @param dataStoreFactory
     * @param scopes
     * @return
     * @throws IOException
     */
    public static GoogleAuthorizationCodeFlow buildAuthorizationFlow(final NetHttpTransport HTTP_TRANSPORT, JsonFactory jsonFactory,
                                                                     GoogleClientSecrets clientSecrets, DataStoreFactory dataStoreFactory, List<String> scopes) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build();
    }
}
