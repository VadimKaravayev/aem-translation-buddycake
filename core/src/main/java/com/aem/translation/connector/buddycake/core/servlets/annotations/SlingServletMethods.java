package com.aem.translation.connector.buddycake.core.servlets.annotations;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
public @interface SlingServletMethods {
    String[] value();
}
