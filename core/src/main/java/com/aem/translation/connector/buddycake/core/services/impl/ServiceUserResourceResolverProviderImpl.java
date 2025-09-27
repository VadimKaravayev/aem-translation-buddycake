package com.aem.translation.connector.buddycake.core.services.impl;

import com.aem.translation.connector.buddycake.core.services.ServiceUserResourceResolverProvider;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

@Component(service = ServiceUserResourceResolverProvider.class, immediate = true)
public class ServiceUserResourceResolverProviderImpl implements ServiceUserResourceResolverProvider {

    private static final String TRANSLATION_CONNECTOR_SERVICE_NAME = "buddycake-translation-connector-service";
    private static final Map<String, Object> TRANSLATION_CONNECTOR_SERVICE_AUTH_INFO = Map.of(ResourceResolverFactory.SUBSERVICE, TRANSLATION_CONNECTOR_SERVICE_NAME);


    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getTranslationConnectorResourceResolver() throws LoginException {

        return resourceResolverFactory.getServiceResourceResolver(TRANSLATION_CONNECTOR_SERVICE_AUTH_INFO);
    }
}
