package org.options.bot.google.sheets;

import org.options.bot.google.GoogleAPIException;

public class GoogleSheetException extends GoogleAPIException {
    public GoogleSheetException() {
    }

    public GoogleSheetException(String message) {
        super(message);
    }

    public GoogleSheetException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleSheetException(Throwable cause) {
        super(cause);
    }

    public GoogleSheetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
