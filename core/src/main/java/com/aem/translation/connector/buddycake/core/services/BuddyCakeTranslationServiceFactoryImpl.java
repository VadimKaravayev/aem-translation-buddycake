package com.aem.translation.connector.buddycake.core.services;

import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;
import com.aem.translation.connector.buddycake.core.config.BuddyCakeTranslationCloudConfig;
import org.apache.sling.api.resource.Resource;
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

@Component(service = TranslationServiceFactory.class, immediate = true, property = {
        TranslationServiceFactory.PROPERTY_TRANSLATION_FACTORY + "=" + "buddycake"
})
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

    private BuddyCakeTranslationServiceFactoryConfig factoryConfig;

    @Activate
    protected void activate(final BuddyCakeTranslationServiceFactoryConfig config) {
        this.factoryConfig = config;


        availableLanguageMap = Map.of();
        availableCategoryMap = Map.of();
    }

    @Override
    public TranslationService createTranslationService(TranslationConstants.TranslationMethod translationMethod, String cloudConfigPath, Resource resource) throws TranslationException {
        BuddyCakeTranslationCloudConfig mainConfig = (BuddyCakeTranslationCloudConfig) cloudConfigUtil.getCloudConfigObjectFromPath(BuddyCakeTranslationCloudConfig.class, cloudConfigPath);
        //mainConfig.getServiceLabel()

        //Label name appears in an AEM translation project 'Translation provider dropdown'
        return new BuddyCakeTranslationServiceImpl(
                availableLanguageMap,
                availableCategoryMap,
                factoryConfig.factory_name(),
                "Buddycake Translations",
                "fake-attributation",
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
    public Class<?> getServiceCloudConfigClass() {
        return BuddyCakeTranslationCloudConfig.class;
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
