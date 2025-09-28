package com.aem.translation.connector.buddycake.core.services;

import com.adobe.granite.translation.core.TranslationCloudConfigUtil;
import com.aem.translation.connector.buddycake.core.config.BuddyCakeTranslationCloudConfig;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = AdapterFactory.class, property = {
        AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
        AdapterFactory.ADAPTER_CLASSES + "=com.aem.translation.connector.buddycake.core.config.BuddyCakeTranslationCloudConfig"
})
public class BuddyCakeTranslationAdapterFactory implements AdapterFactory {

    @Reference
    private TranslationCloudConfigUtil cloudConfigUtil;

    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
        if (adaptable instanceof Resource && isValidTranslationConfigResource(type, (Resource) adaptable)) {
            return (AdapterType) (new BuddyCakeTranslationCloudConfig((Resource) adaptable));
        }
        return null;
    }



    private boolean isValidTranslationConfigResource(Class<?> type, Resource resource) {
        return type.equals(BuddyCakeTranslationCloudConfig.class)
                && cloudConfigUtil.isCloudConfigAppliedOnImmediateResource(resource, "cq/translation/components/mt-cloudconfig");
    }
}
