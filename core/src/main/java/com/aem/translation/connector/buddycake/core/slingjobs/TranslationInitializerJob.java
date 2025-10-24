package com.aem.translation.connector.buddycake.core.slingjobs;

import com.aem.translation.connector.buddycake.core.services.ServiceUserResourceResolverProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.servlethelpers.internalrequests.InternalRequest;
import org.apache.sling.servlethelpers.internalrequests.SlingInternalRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.query.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.aem.translation.connector.buddycake.core.slingjobs.TranslationInitializerJob.TOPIC;

@Slf4j
@Component(service = JobConsumer.class, immediate = true, property = {
    JobConsumer.PROPERTY_TOPICS + "=" +TOPIC
})
public class TranslationInitializerJob implements JobConsumer {
    public static final String TOPIC = "buddycake/translation/project/addcontent";
    private static final String JOB_POD_RESOURCE_TYPE = "cq/gui/components/projects/admin/pod/translationjobpod";
    private static final String PROP_DESTINATION_LANGUAGE = "destinationLanguage";

    @Reference
    private SlingRequestProcessor slingRequestProcessor;

    @Reference
    private ServiceUserResourceResolverProvider resolverProvider;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {
        String projectPath = job.getProperty("projectPath", StringUtils.EMPTY);
        String translationPage = job.getProperty("translationPage", StringUtils.EMPTY);
        String targetLanguage = job.getProperty("targetLanguage", StringUtils.EMPTY);

        if (StringUtils.isAnyEmpty(projectPath, translationPage, targetLanguage)) {
            log.info("Canceling the job with projectPath: {}, sourceContentPath: {}, targetLanguage: {}",
                    projectPath, translationPage, targetLanguage);
            return JobResult.CANCEL;
        }



        try (var resourceResolver = resourceResolverFactory.getResourceResolver(Map.of(
                ResourceResolverFactory.USER, "admin",
                ResourceResolverFactory.PASSWORD, "admin".toCharArray()))) {

            String jobPodPath = findTranslationJobPodPath(resourceResolver, projectPath, targetLanguage);


            if (StringUtils.isEmpty(jobPodPath)) {
                log.error("Canceling the job: {} with 'translationJobPodPath' is empty", job.getId());
                return JobResult.CANCEL;
            }


            InternalRequest addPagesRequest = new SlingInternalRequest(resourceResolver, slingRequestProcessor, jobPodPath)
                    .withResourceType(JOB_POD_RESOURCE_TYPE)
                    .withRequestMethod(HttpConstants.METHOD_POST)
                    .withContentType("application/x-www-form-urlencoded")
                    .withParameter(":operation", "ADD_TRANSLATION_PAGES")
                    .withParameter(":translationJobPath", jobPodPath)
                    .withParameter("_charset_", "UTF-8")
                    .withParameter("createLanguageCopy", "true")
                    .withParameter("allowChildren", "true")
                    .withParameter("type", "ASSET/SITE")
                    .withParameter("translationpage", translationPage)
                    .execute()
                    .checkStatus(200);

            String responseAsString = addPagesRequest.getResponseAsString();
            int status = addPagesRequest.getStatus();
            log.info(responseAsString, status);

            InternalRequest startTranslationRequest = new SlingInternalRequest(resourceResolver, slingRequestProcessor, jobPodPath)
                    .withResourceType(JOB_POD_RESOURCE_TYPE)
                    .withRequestMethod(HttpConstants.METHOD_POST)
                    .withContentType("application/x-www-form-urlencoded")
                    .withParameter(":operation", "START_TRANSLATION")
                    .withParameter(":translationJobPath", jobPodPath)
                    .execute()
                    .checkStatus(200);

            String startTranslationAsString = startTranslationRequest.getResponseAsString();
            int startTranslationStatus = startTranslationRequest.getStatus();
            log.info(startTranslationAsString, startTranslationStatus);


        } catch (LoginException e) {
            log.error("Failed to process job: {}", job.getId(), e);
            return JobResult.FAILED;
        } catch (IOException e) {
            log.error("Failed to process the job: {}", job.getId(), e);
            return JobResult.FAILED;
        }

        return JobResult.OK;
    }

    private String findTranslationJobPodPath(final ResourceResolver resourceResolver, final String projectPath, final String targetLanguage) {
        String query = String.format(
                "select * from [nt:base] " +
                "where isdescendantnode('%s')" +
                "and [sling:resourceType] = '%s'" +
                "and [destinationLanguage] = '%s'",
                projectPath, JOB_POD_RESOURCE_TYPE, targetLanguage
        );

        Iterator<Resource> resources = resourceResolver.findResources(query, Query.JCR_SQL2);

        return resources.hasNext()
                ? resources.next().getPath()
                : StringUtils.EMPTY;
    }

    private List<String> findAllTranslationJobPodPaths(final ResourceResolver resourceResolver, final String projectPath) {
        String query = String.format(
                "select * from [nt:base] " +
                        "where isdescendantnode('%s')" +
                        "and [sling:resourceType] = '%s'",
                projectPath, JOB_POD_RESOURCE_TYPE
        );

        Iterator<Resource> resources = resourceResolver.findResources(query, Query.JCR_SQL2);

        List<String> result = new ArrayList<>();

        while (resources.hasNext()) {
            Resource next = resources.next();
            String path = next.getPath();
            result.add(path);
        }

        return result;
    }
}
