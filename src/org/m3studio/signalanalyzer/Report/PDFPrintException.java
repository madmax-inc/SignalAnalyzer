package org.m3studio.signalanalyzer.Report;

/**
 * Created by madmax on 07.03.15.
 */
public class PDFPrintException extends Exception {
    public PDFPrintException() {
        super();
    }

    public PDFPrintException(String message) {
        super(message);
    }

    public PDFPrintException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDFPrintException(Throwable cause) {
        super(cause);
    }

    public PDFPrintException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
