package com.aem.translation.connector.buddycake.core.services;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ServiceUserResourceResolverProvider {

    ResourceResolver getTranslationConnectorResourceResolver() throws LoginException;
}
