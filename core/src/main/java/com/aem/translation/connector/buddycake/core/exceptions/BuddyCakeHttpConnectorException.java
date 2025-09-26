package com.aem.translation.connector.buddycake.core.exceptions;

import lombok.Value;

import javax.servlet.http.HttpServletResponse;

@Value
public class BuddyCakeHttpConnectorException extends RuntimeException {
    int status;
    String error;

    public BuddyCakeHttpConnectorException(final int status, final String error, final String message) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public static BuddyCakeHttpConnectorException badRequest(final String message) {
        return new BuddyCakeHttpConnectorException(HttpServletResponse.SC_BAD_REQUEST, "Bad Request", message);
    }

    public static BuddyCakeHttpConnectorException internalServerError(final String message) {
        return new BuddyCakeHttpConnectorException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }
}
