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
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

public class BuddyCakeTranslationServiceImpl extends AbstractTranslationService implements TranslationService {
    protected BuddyCakeTranslationServiceImpl(Map<String, String> availableLanguageMap, Map<String, String> availableCategoryMap, String factoryName, String label, String attribution, String translationCloudConfigRootPath, TranslationConstants.TranslationMethod supportedTranslationMethod, TranslationConfig tc) {
        super(availableLanguageMap, availableCategoryMap, factoryName, label, attribution, translationCloudConfigRootPath, supportedTranslationMethod, tc);
    }

    @Override
    public Map<String, String> supportedLanguages() {
        return Map.of();
    }

    @Override
    public boolean isDirectionSupported(String sourceLanguage, String targetLanguage) throws TranslationException {
        return false;
    }

    @Override
    public String detectLanguage(String detectSource, TranslationConstants.ContentType contentType) throws TranslationException {
        return StringUtils.EMPTY;
    }

    @Override
    public TranslationResult translateString(String sourceString, String sourceLanguage, String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        return null;
    }

    @Override
    public TranslationResult[] translateArray(String[] sourceStringArr, String sourceLanguage, String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
        return new TranslationResult[0];
    }

    @Override
    public TranslationResult[] getAllStoredTranslations(String sourceString, String sourceLanguage, String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory, String userId, int maxTranslations) throws TranslationException {
        return new TranslationResult[0];
    }

    @Override
    public void storeTranslation(String originalText, String sourceLanguage, String targetLanguage, String updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory, String userId, int rating, String path) throws TranslationException {

    }

    @Override
    public void storeTranslation(String[] originalText, String sourceLanguage, String targetLanguage, String[] updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory, String userId, int rating, String path) throws TranslationException {

    }

    @Override
    public String createTranslationJob(String name, String description, String strSourceLanguage, String strTargetLanguage, Date dueDate, TranslationState state, TranslationMetadata jobMetadata) throws TranslationException {
        return "";
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
