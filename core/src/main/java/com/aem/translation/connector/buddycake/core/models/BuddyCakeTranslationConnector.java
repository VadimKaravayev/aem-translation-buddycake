package com.aem.translation.connector.buddycake.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
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

    @Self
    private SlingHttpServletRequest request;
    private ValueMap configProps;
    private ValueMap rootConfigProps;

    @PostConstruct
    public void init() {
        final String configPath = StringUtils.defaultIfEmpty(request.getRequestPathInfo().getSuffix(), StringUtils.EMPTY);

        final Page configPage = Optional.ofNullable(request.getResourceResolver().adaptTo(PageManager.class))
                .map(pm -> pm.getContainingPage(configPath))
                .orElse(null);

        configProps = getConfigProps(configPage);
        rootConfigProps = getRootConfigProps(configPage);

        log.info("hello world");
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
}
