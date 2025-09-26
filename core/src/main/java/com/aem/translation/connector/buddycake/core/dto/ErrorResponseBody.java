package com.aem.translation.connector.buddycake.core.dto;

import com.aem.translation.connector.buddycake.core.exceptions.BuddyCakeHttpConnectorException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

@Value
@Builder
@JsonDeserialize(builder = ErrorResponseBody.ErrorResponseBodyBuilder.class) //This tells Jackson: “Use the Lombok builder to deserialize JSON into this class.”
public class ErrorResponseBody implements Serializable {

    @Builder.Default
    int status = 500;
    @Builder.Default
    String error = "Internal Server Error";
    String message;
    String path;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Date timestamp = new Date();

    public static ErrorResponseBody from(final BuddyCakeHttpConnectorException e, final String path) {
        return ErrorResponseBody.builder()
                .status(e.getStatus())
                .error(e.getError())
                .message(e.getMessage())
                .path(path)
                .build();
    }

    /**
     * @JsonPOJOBuilder(withPrefix = "")
     *
     * By default, Jackson expects builder methods like withStatus(int) instead of status(int).
     *
     * Lombok generates plain methods (status(int)), not withStatus(...).
     *
     * This annotation configures Jackson to look for methods without a prefix.
     *
     * Without this, deserialization would fail.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class ErrorResponseBodyBuilder {}
}
