package com.aem.translation.connector.buddycake.core.servlets;

import com.aem.translation.connector.buddycake.core.dto.ErrorResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.JSONResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class BuddyCakeBaseServlet extends SlingAllMethodsServlet {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        doResponse(request, response);
    }

    private void doResponse(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        try {
            int responseStatusCode = getStatusCodeBasedOnMethod(request.getMethod());
            response.setStatus(responseStatusCode);
            writeJsonOutput(request, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            setResponseHeaders(response);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                MAPPER.writeValue(outputStream, new ErrorResponseBody(e.getMessage()));
            }
        }
    }

    private int getStatusCodeBasedOnMethod(final String httpMethod) {
        switch (httpMethod) {
            case HttpConstants.METHOD_POST:
                return HttpServletResponse.SC_CREATED;
            default:
                return HttpServletResponse.SC_OK;
        }
    }

    private void writeJsonOutput(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        setResponseHeaders(response);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            Object result = handleRequest(request);
            MAPPER.writeValue(outputStream, result);
        }
    }

    protected abstract Object handleRequest(final SlingHttpServletRequest request);

    private void setResponseHeaders(final SlingHttpServletResponse response) {
        response.setContentType(JSONResponse.RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
    }
}
