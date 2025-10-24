package com.aem.translation.connector.buddycake.core.http;

import com.day.cq.commons.PathInfo;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {

    @Test
    void testGetToken() throws IOException {
//        TokenProvider tokenProvider = new TokenProvider(
//                "dab4059f-147f-4c96-b4c6-82eab870f6c1",
//                "ODg4NTJhZGItZjM5ZS00NzVjLWFiODEtMTFiOTNhZWM5NmRl",
//                "https://us-south.appid.cloud.ibm.com/oauth/v4/18b0c2da-0f1b-4340-bd14-a6a9e8d36977/token",
//                HttpClients.createDefault());
//
//        String token = tokenProvider.getToken();
//        System.out.println(token);
    }

    @Test
    void name() throws URISyntaxException {
        String str = "/content/output/sites/en/adding-visual-impact-to-content-ditamap.html?wcmmode=disabled";




        PathInfo pathInfo = new PathInfo(str);
        System.out.println(pathInfo.getResourcePath());
        System.out.println(pathInfo.getExtension());


    }
}