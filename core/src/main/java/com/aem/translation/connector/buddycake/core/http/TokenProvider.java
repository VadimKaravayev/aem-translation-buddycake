package com.aem.translation.connector.buddycake.core.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import static com.adobe.granite.rest.Constants.CT_JSON;
import static com.adobe.granite.rest.Constants.CT_WWW_FORM_URLENCODED;
import static org.apache.http.HttpStatus.SC_OK;

public class TokenProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String clientId;
    private final String clientSecret;
    private final String generateTokenUrl;
    private final CloseableHttpClient httpClient;
    private String accessToken;
    private Instant expiryTime;

    public TokenProvider(String clientId, String clientSecret, String generateTokenUrl, CloseableHttpClient httpClient) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.generateTokenUrl = generateTokenUrl;
        this.httpClient = httpClient;
        expiryTime = Instant.EPOCH;
    }

    public synchronized String getToken() throws IOException {
        if (isExpired()) {
            requestAccessToken();
        }

        return accessToken;
    }

    private void requestAccessToken() throws IOException {
        HttpPost post = new HttpPost(generateTokenUrl);
        post.setHeader(HttpHeaders.CONTENT_TYPE, CT_WWW_FORM_URLENCODED);
        post.setHeader(HttpHeaders.ACCEPT, CT_JSON);
        post.setHeader(HttpHeaders.AUTHORIZATION, getBasicAuthValue());

        List<NameValuePair> params = List.of(
                new BasicNameValuePair("grant_type", "client_credentials")
        );

        post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != SC_OK) {
                throw new IOException("Failed to fetch token, status: " + statusCode);
            }

            try (InputStream content = response.getEntity().getContent()) {
                JsonNode json = MAPPER.readTree(content);
                accessToken = json.get("access_token").asText();
                long expiresIn = json.get("expires_in").asLong();
                expiryTime = Instant.now().plusSeconds(expiresIn - 60);
            }
        }
    }

    private boolean isExpired() {
        return accessToken == null || Instant.now().isAfter(expiryTime);
    }

    private String getBasicAuthValue() {
        return "Basic " + encodeAuth();
    }

    private String encodeAuth() {
        return Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret)
                        .getBytes(StandardCharsets.UTF_8));
    }
}
