package com.vsobakekot.natlex.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ExportInProgressException extends RuntimeException{

    public ExportInProgressException() {
        super();
    }

    public ExportInProgressException(String message) {
        super(message);
    }

    public ExportInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportInProgressException(Throwable cause) {
        super(cause);
    }
}
