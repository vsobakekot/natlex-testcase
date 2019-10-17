package com.vsobakekot.natlex.exсeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExportErrorResultException extends RuntimeException {

    public ExportErrorResultException(String message) {
        super(message);
    }
}
