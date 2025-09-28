package com.aem.translation.connector.buddycake.core.services;

import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;
import com.aem.translation.connector.buddycake.core.config.BuddyCakeTranslationCloudConfig;
import com.aem.translation.connector.buddycake.core.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_CATEGORY_MAPPING;
import static com.aem.translation.connector.buddycake.core.util.ConnectorConstants.PROP_LANGUAGE_MAPPING;

@Slf4j
@Component(service = TranslationServiceFactory.class, immediate = true)
@ServiceDescription("Factory for buddycake translation service")
@ServiceVendor("Buddy Cake Inc")
@Designate(ocd = BuddyCakeTranslationServiceFactoryImpl.BuddyCakeTranslationServiceFactoryConfig.class)
public class BuddyCakeTranslationServiceFactoryImpl implements TranslationServiceFactory {

    private Map<String, String> availableLanguageMap;
    private Map<String, String> availableCategoryMap;

    @Reference
    private TranslationCloudConfigUtil cloudConfigUtil;

    @Reference
    private TranslationConfig translationConfig;

    @Reference
    private ServiceUserResourceResolverProvider resolverProvider;

    private BuddyCakeTranslationServiceFactoryConfig factoryConfig;

    @Activate
    protected void activate(final BuddyCakeTranslationServiceFactoryConfig config) {
        this.factoryConfig = config;
        availableLanguageMap = getConnectorPropertyMap(factoryConfig.language_mapping_location(), PROP_LANGUAGE_MAPPING);
        availableCategoryMap = getConnectorPropertyMap(factoryConfig.category_mapping_location(), PROP_CATEGORY_MAPPING);
    }

    @Override
    public TranslationService createTranslationService(TranslationConstants.TranslationMethod translationMethod, String cloudConfigPath, Resource resource) throws TranslationException {

        final var connectorConfig = Optional.ofNullable(cloudConfigUtil.getCloudConfigObjectFromPath(resource, getServiceCloudConfigClass(), cloudConfigPath))
                .map(obj -> (BuddyCakeTranslationCloudConfig) obj)
                .orElse(BuddyCakeTranslationCloudConfig.getEmptyConfig());

        return new BuddyCakeTranslationServiceImpl(
                availableLanguageMap,
                availableCategoryMap,
                factoryConfig.factory_name(),
                connectorConfig.getServiceLabel(),
                connectorConfig.getServiceAttribution(),
                factoryConfig.translation_cloud_config_root_path(),
                TranslationConstants.TranslationMethod.HUMAN_TRANSLATION,
                translationConfig);

    }

    @Override
    public TranslationService createTranslationService(TranslationConstants.TranslationMethod translationMethod, String cloudConfigPath) throws TranslationException {
        return createTranslationService(translationMethod, cloudConfigPath, null);
    }

    @Override
    public List<TranslationConstants.TranslationMethod> getSupportedTranslationMethods() {
        return List.of(TranslationConstants.TranslationMethod.HUMAN_TRANSLATION);
    }

    @Override
    public String getServiceFactoryName() {
        return factoryConfig.factory_name();
    }

    @Override
    public Class<BuddyCakeTranslationCloudConfig> getServiceCloudConfigClass() {
        return BuddyCakeTranslationCloudConfig.class;
    }

    private Map<String, String> getConnectorPropertyMap(final String resourceLocation, final String propertyName) {
        try (final var resolver = resolverProvider.getTranslationConnectorResourceResolver()) {
            Resource resource = resolver.getResource(resourceLocation);
            if (resource == null) {
                log.info("Resource does not exists for path: {}", resourceLocation);
                return Map.of();
            }

            Function<Resource, String> getMappingProp = res -> {
                var valueMap = ResourceUtil.getValueMap(res);
                return valueMap.get(propertyName, StringUtils.EMPTY);
            };


            return StreamUtils.stream(resource.listChildren())
                    .collect(Collectors.toMap(
                            Resource::getName,
                            getMappingProp,
                            (current, next) -> current));

        } catch (LoginException e) {
            log.error("Failed to get resource {}, error message {}", resourceLocation, e.getMessage(), e);
            return Map.of();
        }
    }

    @ObjectClassDefinition(name  = "Config for BuddyCake Translation Service Factory")
    public @interface BuddyCakeTranslationServiceFactoryConfig {

        @AttributeDefinition(name = "BuddyCake Translation Factory Name")
        String factory_name() default "buddycaketranslation";

        @AttributeDefinition(name = "Translation Cloud Config Root Path")
        String translation_cloud_config_root_path() default "/conf/global/settings/cloudconfigs/translation/buddycake-translations";

        @AttributeDefinition(name = "Language Map Location")
        String language_mapping_location() default "/apps/buddycake/translation/resources/languageMapping";

        @AttributeDefinition(name = "Category Map Location")
        String category_mapping_location() default "/apps/buddycake/translation/resources/categoryMapping";
    }
}
