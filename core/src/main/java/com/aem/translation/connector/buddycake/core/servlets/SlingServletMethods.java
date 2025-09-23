package com.aem.translation.connector.buddycake.core.servlets;

import org.osgi.service.component.annotations.ComponentPropertyType;

@ComponentPropertyType
public @interface SlingServletMethods {
    String[] value();
}
