package com.vsobakekot.natlex.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExportErrorResultException extends RuntimeException {

    public ExportErrorResultException() {
        super();
    }

    public ExportErrorResultException(String message) {
        super(message);
    }

    public ExportErrorResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportErrorResultException(Throwable cause) {
        super(cause);
    }
}
