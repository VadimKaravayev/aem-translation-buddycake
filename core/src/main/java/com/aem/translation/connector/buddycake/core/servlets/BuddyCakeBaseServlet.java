package com.aem.translation.connector.buddycake.core.servlets;

import com.aem.translation.connector.buddycake.core.dto.ErrorResponseBody;
import com.aem.translation.connector.buddycake.core.exceptions.BuddyCakeHttpConnectorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.JSONResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;

import static com.aem.translation.connector.buddycake.core.util.JsonUtil.getMapper;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.sling.api.servlets.HttpConstants.METHOD_POST;

@Slf4j
public abstract class BuddyCakeBaseServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        doResponse(request, response);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        doResponse(request, response);
    }

    private void doResponse(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        setResponseHeaders(response);
        try {
            writeJsonOutput(response, () -> handleRequest(request));
            response.setStatus(resolveStatusCode(request.getMethod()));
        } catch (BuddyCakeHttpConnectorException e) {
            log.error(e.getMessage(), e);

            writeJsonOutput(response, () -> ErrorResponseBody.from(e, request.getRequestURI()));
            response.setStatus(e.getStatus());
        }
    }

    private int resolveStatusCode(final String httpMethod) {
        final Map<String, Integer> statusMap = Map.of(
                METHOD_POST, SC_CREATED);
        return statusMap.getOrDefault(httpMethod, SC_OK);
    }

    private void writeJsonOutput(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        Object payload = handleRequest(request);
        response.getWriter().write(getMapper().writeValueAsString(payload));
    }

    private void writeJsonOutput(final SlingHttpServletResponse response, final Object payload) throws IOException {
        response.getWriter().write(getMapper().writeValueAsString(payload));
    }

    private void writeJsonOutput(final SlingHttpServletResponse response, final Supplier<Object> payloadSupplier) throws IOException {
        response.getWriter().write(getMapper().writeValueAsString(payloadSupplier.get()));
    }

    protected abstract Object handleRequest(final SlingHttpServletRequest request);

    private void setResponseHeaders(final SlingHttpServletResponse response) {
        response.setContentType(JSONResponse.RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
    }
}
