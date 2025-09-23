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

@Slf4j
@Model(adaptables = SlingHttpServletRequest.class)
public class BuddyCakeTranslationConnector {

    public static final String JCR_DESCRIPTION_PROP = "jcr:description";
    public static final String DESCRIPTION_EXTENDED_PROP = "descriptionExtended";
    public static final String CONFIG_NODE_RESOURCE_TYPE = "cq/translation/components/mt-cloudconfig";
    public static final String SERVICE_LABEL_PROP = "servicelabel";
    public static final String SERVICE_ATTRIBUTION_PROP = "serviceattribution";

    @Self
    private SlingHttpServletRequest request;
    private ValueMap configProps = ValueMap.EMPTY;
    private ValueMap rootConfigProps = ValueMap.EMPTY;
    @Getter
    private String confOverlayPath;

    @PostConstruct
    public void init() {
        final Page configPage = getValidConfigPage(request);

        if (configPage == null) {
           return;
        }
        confOverlayPath = configPage.getContentResource().getPath();
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
        return rootConfigProps.get(JCR_DESCRIPTION_PROP, StringUtils.EMPTY);
    }

    public String getExtendedDescription() {
        return rootConfigProps.get(DESCRIPTION_EXTENDED_PROP, StringUtils.EMPTY);
    }

    public String getFormAction() {
        return BuddyCakeTranslationConfigEncryptionServlet.TRANSLATION_CONFIG_ENCRYPTION_SERVLET_PATH;
    }

    public String getServiceLabel() {
        return configProps.get(SERVICE_LABEL_PROP, StringUtils.EMPTY);
    }

    public String getServiceAttribution() {
        return configProps.get(SERVICE_ATTRIBUTION_PROP, StringUtils.EMPTY);
    }
}
