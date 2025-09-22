package com.aem.translation.connector.buddycake.core.services;

import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;
import com.adobe.granite.translation.core.common.AbstractTranslationServiceFactory;
import com.aem.translation.connector.buddycake.core.config.BuddyCakeTranslationCloudConfig;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;

import java.util.List;
import java.util.Map;

@Component(service = TranslationServiceFactory.class, immediate = true, property = {
        TranslationServiceFactory.PROPERTY_TRANSLATION_FACTORY + "=" + "buddycake"
})
@ServiceDescription("Factory for buddycake translation service")
@ServiceVendor("Buddy Cake Inc")
public class BuddyCakeTranslationServiceFactoryImpl extends AbstractTranslationServiceFactory implements TranslationServiceFactory {

    private Map<String, String> availableLanguageMap;
    private Map<String, String> availableCategoryMap;

    @Reference
    private TranslationCloudConfigUtil cloudConfigUtil;

    @Reference
    private TranslationConfig translationConfig;

    protected void activate(ComponentContext ctx) {
        super.activate(ctx);

        availableLanguageMap = Map.of();
        availableCategoryMap = Map.of();
    }

    @Override
    public TranslationService createTranslationService(TranslationConstants.TranslationMethod translationMethod, String cloudConfigPath) throws TranslationException {
        BuddyCakeTranslationCloudConfig mainConfig = (BuddyCakeTranslationCloudConfig) cloudConfigUtil.getCloudConfigObjectFromPath(BuddyCakeTranslationCloudConfig.class, cloudConfigPath);
        //mainConfig.getServiceLabel()

        //Label name appears in an AEM translation project 'Translation provider dropdown'
        return new BuddyCakeTranslationServiceImpl(
                availableLanguageMap,
                availableCategoryMap,
                factoryName,
                "Buddycake Translations",
                "fake-attributation",
                "/fake/cloud/config/root",
                TranslationConstants.TranslationMethod.HUMAN_TRANSLATION, translationConfig);
    }

    @Override
    public List<TranslationConstants.TranslationMethod> getSupportedTranslationMethods() {
        return List.of(TranslationConstants.TranslationMethod.HUMAN_TRANSLATION);
    }

    @Override
    public Class<?> getServiceCloudConfigClass() {
        return BuddyCakeTranslationCloudConfig.class;
    }
}
