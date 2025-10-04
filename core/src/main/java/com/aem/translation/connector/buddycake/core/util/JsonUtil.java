package com.aem.translation.connector.buddycake.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public final class JsonUtil {
    private JsonUtil() {}

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static JsonNode readTree(final InputStream content) throws IOException {
        return MAPPER.readTree(content);
    }
}
