package com.aem.translation.connector.buddycake.core.config;

import com.aem.translation.connector.buddycake.core.util.ConnectorConstants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import java.util.Date;
import java.util.Objects;

@Getter
public class BuddyCakeTranslationCloudConfig {
    private final String clientId;
    private final String clientSecret;
    private final String generateTokenUrl;
    private final String serviceAttribution;
    private final String serviceLabel;
    private final String workspaceId;
    private final long lastTimeUsed;

    public BuddyCakeTranslationCloudConfig(final Resource translationConfigResource) {
        final Resource configContent = JcrConstants.JCR_CONTENT.equals(translationConfigResource.getName())
                ? translationConfigResource
                : translationConfigResource.getChild(JcrConstants.JCR_CONTENT);

        final ValueMap valueMap = ResourceUtil.getValueMap(configContent);

        clientId = valueMap.get(ConnectorConstants.PROP_CLIENT_ID, StringUtils.EMPTY);
        clientSecret = valueMap.get(ConnectorConstants.PROP_CLIENT_SECRET, StringUtils.EMPTY);
        generateTokenUrl = valueMap.get(ConnectorConstants.PROP_GENERATE_TOKEN_URL, StringUtils.EMPTY);
        serviceAttribution = valueMap.get(ConnectorConstants.PROP_SERVICE_ATTRIBUTION, StringUtils.EMPTY);
        serviceLabel = valueMap.get(ConnectorConstants.PROP_SERVICE_LABEL, StringUtils.EMPTY);
        workspaceId = valueMap.get(ConnectorConstants.PROP_WORKSPACE_ID, StringUtils.EMPTY);
        lastTimeUsed = new Date().getTime();
    }

    private BuddyCakeTranslationCloudConfig(String clientId, String clientSecret, String generateTokenUrl, String serviceAttribution, String serviceLabel, String workspaceId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.generateTokenUrl = generateTokenUrl;
        this.serviceAttribution = serviceAttribution;
        this.serviceLabel = serviceLabel;
        this.workspaceId = workspaceId;
        this.lastTimeUsed = new Date().getTime();
    }

    public static BuddyCakeTranslationCloudConfig getEmptyConfig() {
        return new BuddyCakeTranslationCloudConfig(
                "", "", "", "", "", "");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BuddyCakeTranslationCloudConfig that = (BuddyCakeTranslationCloudConfig) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(clientSecret, that.clientSecret) && Objects.equals(generateTokenUrl, that.generateTokenUrl) && Objects.equals(serviceAttribution, that.serviceAttribution) && Objects.equals(serviceLabel, that.serviceLabel) && Objects.equals(workspaceId, that.workspaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientSecret, generateTokenUrl, serviceAttribution, serviceLabel, workspaceId);
    }
}
