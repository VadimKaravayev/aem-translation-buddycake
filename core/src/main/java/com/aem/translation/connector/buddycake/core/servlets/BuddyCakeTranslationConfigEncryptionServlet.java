package com.aem.translation.connector.buddycake.core.servlets;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.aem.translation.connector.buddycake.core.exceptions.BuddyCakeHttpConnectorException;
import com.aem.translation.connector.buddycake.core.servlets.annotations.SlingServletMethods;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletPathsStrict;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component(service = Servlet.class)
@SlingServletPathsStrict(paths = BuddyCakeTranslationConfigEncryptionServlet.TRANSLATION_CONFIG_ENCRYPTION_SERVLET_PATH)
@SlingServletMethods(value = HttpConstants.METHOD_POST)
public class BuddyCakeTranslationConfigEncryptionServlet extends BuddyCakeBaseServlet {

    public static final String TRANSLATION_CONFIG_ENCRYPTION_SERVLET_PATH = "/bin/buddycake/translation/config/encrypt";
    public static final String TRANSLATION_CONNECTOR_CONFIG_PATH_PARAM = "translationConnectorConfigPath";
    private static final List<String> EXPECTED_PARAMS_TO_ENCRYPT = List.of("./clientSecret", "./clientId");
    private static final List<String> EXPECTED_PARAMS = List.of("./servicelabel", "./serviceattribution", "./workspaceId", "./generateTokenUrl");


    @Reference
    private CryptoSupport cryptoSupport;

    @Override
    protected Map<String, String> handleRequest(SlingHttpServletRequest request) {
        final String translationConnectorConfigPath = request.getParameter(TRANSLATION_CONNECTOR_CONFIG_PATH_PARAM);

        if (StringUtils.isEmpty(translationConnectorConfigPath)) {
            throw BuddyCakeHttpConnectorException.badRequest(
                    "Missing parameter: " + TRANSLATION_CONNECTOR_CONFIG_PATH_PARAM);
        }

        final ResourceResolver resourceResolver = request.getResourceResolver();

        final Resource cofigResource = resourceResolver.getResource(translationConnectorConfigPath);

        if (cofigResource == null) {
            throw BuddyCakeHttpConnectorException.internalServerError("Failed to find resource by path: " + translationConnectorConfigPath);
        }

        final ModifiableValueMap modifiableValueMap = cofigResource.adaptTo(ModifiableValueMap.class);

        if (modifiableValueMap == null) {
            throw BuddyCakeHttpConnectorException.internalServerError("Failed adapt resource to ModifiableValueMap, resource path: " + cofigResource.getPath());
        }

        try {
            final Map<String, String> paramsToEncrypt = collectParamsForEncryption(request);

            paramsToEncrypt.forEach((propName, propValue) -> {
                try {
                    modifiableValueMap.put(propName, encrypt(propValue));
                } catch (CryptoException e) {
                    throw BuddyCakeHttpConnectorException.internalServerError(e.getMessage());
                }
            });


            final Map<String, String> queryParams = collectParamsFromRequest(request);

            queryParams.forEach((propName, newPropValue) -> {
                final String currentValue = modifiableValueMap.get(propName, String.class);
                if (!newPropValue.equals(currentValue)) {
                    modifiableValueMap.put(propName, newPropValue);
                }
            });

            if (resourceResolver.hasChanges()) {
                resourceResolver.commit();
            }

        } catch (PersistenceException e) {
            throw BuddyCakeHttpConnectorException.internalServerError(e.getMessage());
        }

        return Map.of(
                "message",
                "Successfully saved config properties under path: " + translationConnectorConfigPath
        );
    }

    private Map<String, String> collectParamsForEncryption(final SlingHttpServletRequest request) {
        return prepareParamsMap(EXPECTED_PARAMS_TO_ENCRYPT, request);
    }

    private Map<String, String> collectParamsFromRequest(final SlingHttpServletRequest request) {
        return prepareParamsMap(EXPECTED_PARAMS, request);
    }

    private Map<String, String> prepareParamsMap(final List<String> params, final SlingHttpServletRequest request) {
        return params.stream()
                .map(param -> Map.entry(stripFromSlash(param), request.getParameter(param)))
                .filter(entry -> StringUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String stripFromSlash(String val) {
        return val.startsWith("./") ? val.substring(2) : val;
    }

    private String encrypt(String value) throws CryptoException {
        return StringUtils.isNotEmpty(value) ? cryptoSupport.protect(value) : null;
    }
}
