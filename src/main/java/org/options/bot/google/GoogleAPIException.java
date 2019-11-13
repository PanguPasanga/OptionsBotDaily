package org.options.bot.google;

public class GoogleAPIException extends Exception {
    public GoogleAPIException() {
    }

    public GoogleAPIException(String message) {
        super(message);
    }

    public GoogleAPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleAPIException(Throwable cause) {
        super(cause);
    }

    public GoogleAPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
