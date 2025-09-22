package com.aem.translation.connector.buddycake.core.config;

public class BuddyCakeTranslationCloudConfigImpl implements BuddyCakeTranslationCloudConfig {

    private String translationServiceLabel;

    @Override
    public String getServiceLabel() {
        return translationServiceLabel;
    }
}
