package com.aem.translation.connector.buddycake.core.services;

import com.adobe.granite.comments.Comment;
import com.adobe.granite.comments.CommentCollection;
import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationMetadata;
import com.adobe.granite.translation.api.TranslationObject;
import com.adobe.granite.translation.api.TranslationResult;
import com.adobe.granite.translation.api.TranslationScope;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationState;
import com.adobe.granite.translation.core.common.AbstractTranslationService;
import com.ddy.kotlin.core.dto.TranslationJob;
import com.ddy.kotlin.core.services.TmsIntegrationService;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

@Slf4j
public class BuddyCakeTranslationServiceImpl extends AbstractTranslationService implements TranslationService {

    public static final String THIS_FUNCTION_IS_NOT_IMPLEMENTED = "This function is not implemented";

    private TmsIntegrationService tmsService;

    protected BuddyCakeTranslationServiceImpl(Map<String, String> availableLanguageMap,
                                              Map<String, String> availableCategoryMap,
                                              String factoryName, String label, String attribution,
                                              String translationCloudConfigRootPath,
                                              TranslationConstants.TranslationMethod supportedTranslationMethod,
                                              TranslationConfig tc,
                                              TmsIntegrationService tmsService) {
        super(availableLanguageMap, availableCategoryMap, factoryName, label, attribution, translationCloudConfigRootPath, supportedTranslationMethod, tc);

        this.tmsService = tmsService;
    }

    @Override
    public Map<String, String> supportedLanguages() {
        return Map.copyOf(availableLanguageMap);
    }

    @Override
    public boolean isDirectionSupported(String sourceLanguage, String targetLanguage) throws TranslationException {
        return availableLanguageMap.containsKey(sourceLanguage) && availableLanguageMap.containsKey(targetLanguage);
    }

    @Override
    public String detectLanguage(String detectSource, TranslationConstants.ContentType contentType) throws TranslationException {
        throw new TranslationException(THIS_FUNCTION_IS_NOT_IMPLEMENTED, TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public TranslationResult translateString(String sourceString, String sourceLanguage, String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        throw new TranslationException(THIS_FUNCTION_IS_NOT_IMPLEMENTED, TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public TranslationResult[] translateArray(String[] sourceStringArr, String sourceLanguage, String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        throw new TranslationException(THIS_FUNCTION_IS_NOT_IMPLEMENTED, TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public TranslationResult[] getAllStoredTranslations(String sourceString, String sourceLanguage, String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory, String userId, int maxTranslations) throws TranslationException {
        throw new TranslationException(THIS_FUNCTION_IS_NOT_IMPLEMENTED, TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void storeTranslation(String originalText, String sourceLanguage, String targetLanguage, String updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory, String userId, int rating, String path) throws TranslationException {
        throw new TranslationException(THIS_FUNCTION_IS_NOT_IMPLEMENTED, TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public void storeTranslation(String[] originalText, String sourceLanguage, String targetLanguage, String[] updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory, String userId, int rating, String path) throws TranslationException {
        throw new TranslationException(THIS_FUNCTION_IS_NOT_IMPLEMENTED, TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
    }

    @Override
    public String createTranslationJob(String name, String description, String strSourceLanguage, String strTargetLanguage,
                                       Date dueDate, TranslationState state, TranslationMetadata jobMetadata) throws TranslationException {

        final String jobId = tmsService.createTranslationJob(new TranslationJob(
                name, description, strSourceLanguage, strTargetLanguage, dueDate, state, jobMetadata
        ));
        log.info("Job was created, job id: {}", jobId);
        return jobId;
    }

    @Override
    public void updateTranslationJobMetadata(String strTranslationJobID, TranslationMetadata jobMetadata, TranslationConstants.TranslationMethod translationMethod) throws TranslationException {

    }

    @Override
    public String uploadTranslationObject(String strTranslationJobID, TranslationObject translationObject) throws TranslationException {
        return "";
    }

    @Override
    public TranslationScope getFinalScope(String strTranslationJobID) throws TranslationException {
        return null;
    }

    @Override
    public TranslationConstants.TranslationStatus updateTranslationJobState(String strTranslationJobID, TranslationState state) throws TranslationException {
        return null;
    }

    @Override
    public TranslationConstants.TranslationStatus getTranslationJobStatus(String strTranslationJobID) throws TranslationException {
        return null;
    }

    @Override
    public CommentCollection<Comment> getTranslationJobCommentCollection(String strTranslationJobID) throws TranslationException {
        return null;
    }

    @Override
    public void addTranslationJobComment(String strTranslationJobID, Comment comment) throws TranslationException {

    }

    @Override
    public InputStream getTranslatedObject(String strTranslationJobID, TranslationObject translationObject) throws TranslationException {
        return null;
    }

    @Override
    public TranslationConstants.TranslationStatus updateTranslationObjectState(String strTranslationJobID, TranslationObject translationObject, TranslationState state) throws TranslationException {
        return null;
    }

    @Override
    public TranslationConstants.TranslationStatus getTranslationObjectStatus(String strTranslationJobID, TranslationObject translationObject) throws TranslationException {
        return null;
    }

    @Override
    public TranslationConstants.TranslationStatus[] updateTranslationObjectsState(String strTranslationJobID, TranslationObject[] translationObjects, TranslationState[] states) throws TranslationException {
        return new TranslationConstants.TranslationStatus[0];
    }

    @Override
    public TranslationConstants.TranslationStatus[] getTranslationObjectsStatus(String strTranslationJobID, TranslationObject[] translationObjects) throws TranslationException {
        return new TranslationConstants.TranslationStatus[0];
    }

    @Override
    public CommentCollection<Comment> getTranslationObjectCommentCollection(String strTranslationJobID, TranslationObject translationObject) throws TranslationException {
        return null;
    }

    @Override
    public void addTranslationObjectComment(String strTranslationJobID, TranslationObject translationObject, Comment comment) throws TranslationException {

    }
}
