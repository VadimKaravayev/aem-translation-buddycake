package com.aem.translation.connector.buddycake.core.services;

import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.core.common.AbstractTranslationServiceFactory;
import com.aem.translation.connector.buddycake.core.config.BuddyCakeTranslationCloudConfig;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;

import java.util.List;

@Component(service = TranslationServiceFactory.class, immediate = true, property = {
        TranslationServiceFactory.PROPERTY_TRANSLATION_FACTORY + "=" + "buddycake"
})
@ServiceDescription("Factory for buddycake translation service")
@ServiceVendor("Buddy Cake Inc")
public class BuddyCakeTranslationServiceFactoryImpl extends AbstractTranslationServiceFactory implements TranslationServiceFactory {

    protected void activate(ComponentContext ctx) {
        super.activate(ctx);
    }

    @Override
    public TranslationService createTranslationService(TranslationConstants.TranslationMethod translationMethod, String cloudConfigPath) throws TranslationException {
        return null;
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
