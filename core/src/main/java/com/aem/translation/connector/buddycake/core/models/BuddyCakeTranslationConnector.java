package com.aem.translation.connector.buddycake.core.models;

import com.aem.translation.connector.buddycake.core.servlets.BuddyCakeTranslationConfigEncryptionServlet;
import com.day.cq.wcm.api.Page;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Optional;

import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_CLIENT_ID;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_CLIENT_SECRET;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_GENERATE_TOKEN_URL;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_JCR_DESCRIPTION;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_SERVICE_ATTRIBUTION;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_SERVICE_LABEL;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_WORKSPACE_ID;

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class)
public class BuddyCakeTranslationConnector {


    public static final String DESCRIPTION_EXTENDED_PROP = "descriptionExtended";
    public static final String CONFIG_NODE_RESOURCE_TYPE = "cq/translation/components/mt-cloudconfig";

    @Self
    private SlingHttpServletRequest request;
    private ValueMap configProps = ValueMap.EMPTY;
    private ValueMap rootConfigProps = ValueMap.EMPTY;
    @Getter
    private String translationConnectorConfigPath;

    @PostConstruct
    public void init() {
        final Page configPage = getValidConfigPage(request);

        if (configPage == null) {
           return;
        }
        translationConnectorConfigPath = configPage.getContentResource().getPath();
        configProps = getConfigProps(configPage);
        rootConfigProps = getRootConfigProps(configPage);

        log.info("BuddyCakeTranslationConnector.init() finished");
    }

    private Page getValidConfigPage(final SlingHttpServletRequest request) {
        final ResourceResolver resourceResolver = request.getResourceResolver();
        return Optional.ofNullable(request.getRequestPathInfo().getSuffix())
                .filter(StringUtils::isNotBlank)
                .map(resourceResolver::resolve)
                .filter(this::isValidConfigResource)
                .map(res -> res.adaptTo(Page.class))
                .orElse(null);
    }

    private boolean isValidConfigResource(final Resource resource) {
        return Optional.ofNullable(resource.getChild(JcrConstants.JCR_CONTENT))
                .filter(res -> res.isResourceType(CONFIG_NODE_RESOURCE_TYPE))
                .isPresent();
    }

    private ValueMap getConfigProps(Page configPage) {
        return Optional.ofNullable(configPage)
                .map(Page::getProperties)
                .orElse(emptyValueMap());
    }

    private ValueMap getRootConfigProps(Page configPage) {
        return Optional.ofNullable(configPage)
                .map(Page::getParent)
                .map(Page::getProperties)
                .orElse(emptyValueMap());
    }

    private ValueMap emptyValueMap() {
        return new ValueMapDecorator(new HashMap<>());
    }

    public String getDescription() {
        return rootConfigProps.get(PROP_JCR_DESCRIPTION, StringUtils.EMPTY);
    }

    public String getExtendedDescription() {
        return rootConfigProps.get(DESCRIPTION_EXTENDED_PROP, StringUtils.EMPTY);
    }

    public String getFormAction() {
        return BuddyCakeTranslationConfigEncryptionServlet.TRANSLATION_CONFIG_ENCRYPTION_SERVLET_PATH;
    }

    public String getServiceLabel() {
        return configProps.get(PROP_SERVICE_LABEL, StringUtils.EMPTY);
    }

    public String getServiceAttribution() {
        return configProps.get(PROP_SERVICE_ATTRIBUTION, StringUtils.EMPTY);
    }

    public String getWorkspaceId() {
        return configProps.get(PROP_WORKSPACE_ID, StringUtils.EMPTY);
    }

    public String getGenerateTokenUrl() {
        return configProps.get(PROP_GENERATE_TOKEN_URL, StringUtils.EMPTY);
    }

    public String getClientId() {
        return configProps.get(PROP_CLIENT_ID, StringUtils.EMPTY);
    }

    public String getClientSecret() {
        return configProps.get(PROP_CLIENT_SECRET, StringUtils.EMPTY);
    }
}
