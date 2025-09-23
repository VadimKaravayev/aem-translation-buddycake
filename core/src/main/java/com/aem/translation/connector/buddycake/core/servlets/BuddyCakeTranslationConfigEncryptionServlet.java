package com.aem.translation.connector.buddycake.core.servlets;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletPathsStrict;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.util.Map;

@Slf4j
@Component(service = Servlet.class)
@SlingServletPathsStrict(paths = BuddyCakeTranslationConfigEncryptionServlet.TRANSLATION_CONFIG_ENCRYPTION_SERVLET_PATH)
@SlingServletMethods(HttpConstants.METHOD_POST)
public class BuddyCakeTranslationConfigEncryptionServlet extends BuddyCakeBaseServlet {

    public static final String TRANSLATION_CONFIG_ENCRYPTION_SERVLET_PATH = "/bin/buddycake/translation/config/encrypt";

    @Override
    protected Map<String, String> handleRequest(SlingHttpServletRequest request) {
        return Map.of("message", "Hello from BuddyCake Translation Encryption Servlet");
    }
}
