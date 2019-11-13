package org.options.bot.google.drive;

import org.options.bot.google.GoogleAPIException;

public class GoogleDriveException extends GoogleAPIException {
    public GoogleDriveException() {
    }

    public GoogleDriveException(String message) {
        super(message);
    }

    public GoogleDriveException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleDriveException(Throwable cause) {
        super(cause);
    }

    public GoogleDriveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
