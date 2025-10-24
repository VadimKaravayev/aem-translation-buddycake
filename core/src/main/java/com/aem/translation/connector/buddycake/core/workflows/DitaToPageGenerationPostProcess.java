package com.aem.translation.connector.buddycake.core.workflows;

import com.aem.translation.connector.buddycake.core.services.ServiceUserResourceResolverProvider;
import com.aem.translation.connector.buddycake.core.slingjobs.TranslationInitializerJob;
import com.day.cq.commons.PathInfo;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlethelpers.internalrequests.InternalRequest;
import org.apache.sling.servlethelpers.internalrequests.SlingInternalRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component(service = WorkflowProcess.class, property = {
        "process.label=Dita to CQ Page Generation Post Process"
})
public class DitaToPageGenerationPostProcess implements WorkflowProcess {

    public static final String PROP_IS_SUCCESS = "isSuccess";
    public static final String PROP_GENERATED_PATH = "generatedPath";
    private static final String[] TARGET_LANGUAGES = new String[] {"de", "es", "fr"};

    @Reference
    private SlingRequestProcessor slingRequestProcessor;

    @Reference
    private ServiceUserResourceResolverProvider resolverProvider;

    @Reference
    private JobManager jobManager;


    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {

        final MetaDataMap metaDataMap = item.getWorkflowData().getMetaDataMap();
        Boolean isSuccessfullyGenerated = metaDataMap.get(PROP_IS_SUCCESS, Boolean.class);

        if (isSuccessfullyGenerated) {
            String generatedPath = metaDataMap.get(PROP_GENERATED_PATH, StringUtils.EMPTY);


            try (var resourceResolver = resolverProvider.getTranslationConnectorResourceResolver()) {
                String userId = getUserId(session);

                InternalRequest execute = new SlingInternalRequest(resourceResolver, slingRequestProcessor, "/content/projects")
                        .withResourceType("cq/projects")
                        .withRequestMethod(HttpConstants.METHOD_POST)
                        .withContentType("application/x-www-form-urlencoded")
                        .withParameter(":operation", "projectcreate")
                        .withParameter("_charset_", "UTF-8")
                        .withParameter("wizard", "/libs/cq/core/content/projects/wizard/translationproject/defaultproject.html")
                        .withParameter("template", "/libs/cq/core/content/projects/templates/translation-project")
                        .withParameter("templateorproject", "/libs/cq/core/content/projects/templates/translation-project")
                        .withParameter("templateorproject@Delete", "")
                        .withParameter("jcr:title", "Dita To cqPage Translation Project Title")
                        .withParameter("jcr:description", "Dita To cqPage Translation Project Description")
                        .withParameter("project.startDate@Delete", "")
                        .withParameter("project.startDate@TypeHint", "Date")
                        .withParameter("project.dueDate@Delete", "")
                        .withParameter("project.dueDate@TypeHint", "Date")
                        .withParameter("teamMemberUserId", userId)
                        .withParameter("teamMemberRoleId", "owner")
                        .withParameter("parentPath", "/content/projects")
                        .withParameter("name", "dita-to-cqpages-translation")
                        .withParameter("isMultiLanguage", "true")
                        .withParameter("processProperty", TARGET_LANGUAGES)
                        .withParameter("sourceLanguage", "en")
                        .withParameter("processGadget", "translationjob")
                        .withParameter("gadgetProperty", "destinationLanguage")
                        .withParameter("DRAFT", "3")
                        .withParameter("initiatorUserId", userId)
                        .withParameter("./destinationLanguage@Delete", "")
                        .withParameter("./cq:conf", "")
                        .withParameter("./cq:conf@Delete", "")
                        .withParameter("translationMethod", "HUMAN_TRANSLATION")
                        .withParameter("translationProvider", "Gtech Connector")
                        .withParameter("contentCategory", "general")
                        .withParameter("translationCloudConfigPath", "cloudconfigs/translation/gtech-translations/gtech")
                        .withParameter("translationCloudConfigName", "Gtech")
                        .withParameter("translationAutomaticApproveEnable@Delete", "")
                        .withParameter("translationAutomaticApproveEnable@DefaultValue", "false")
                        .withParameter("translationAutomaticApproveEnable@UseDefaultWhenMissing", "true")
                        .withParameter("translationAutomaticApproveEnable", "true")
                        .withParameter("translationSchedulerRepeatType", "0")
                        .withParameter("translationSchedulerRepeatDay", "1")
                        .withParameter("translationSchedulerRepeatWeek", "1")
                        .withParameter("translationSchedulerRepeatWeekDay", "2")
                        .withParameter("translationSchedulerRepeatStartTime", "0")
                        .withParameter("translationSchedulerRepeatMonth", "1")
                        .withParameter("translationAutomaticPromoteLaunchEnable", "true")
                        .withParameter("translationAutomaticDeleteLaunchEnable", "true")
                        .execute().checkStatus(201);

                String html = execute.getResponseAsString();
                Document document = Jsoup.parse(html);
                String href = document.select(".cq-projects-admin-createproject-edit").attr("href");
                String projectPath = href.replaceFirst("^/projects/details\\.html", "");

                PathInfo pathInfo = new PathInfo(generatedPath);
                String translationPagePath = pathInfo.getResourcePath();

//                jobManager.addJob(TranslationInitializerJob.TOPIC, Map.of(
//                        "projectPath", projectPath,
//                        "translationPage", translationPagePath
//                ));

                for (String targetLanguage : TARGET_LANGUAGES) {
                    jobManager.addJob(TranslationInitializerJob.TOPIC, Map.of(
                            "projectPath", projectPath,
                            "translationPage", translationPagePath,
                            "targetLanguage", targetLanguage

                    ));
                }


            } catch (LoginException | IOException e) {
                throw new WorkflowException("Failed to create translation project", e);
            }
        }
    }

    private String getUserId(final WorkflowSession session) {
        final Authorizable user = session.getUser();
        try {
            return user.getID();
        } catch (RepositoryException e) {
            log.info("Failed to get user id", e);
            return StringUtils.EMPTY;
        }
    }
}
