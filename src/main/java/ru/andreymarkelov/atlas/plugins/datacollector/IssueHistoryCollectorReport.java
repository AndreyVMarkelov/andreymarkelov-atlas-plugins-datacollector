package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;

public class IssueHistoryCollectorReport extends AbstractReport {

    @Override
    public String generateReportHtml(ProjectActionSupport action, Map reqParams) throws Exception {
        return descriptor.getHtml("view", getVelocityParams(action, reqParams));
    }

    private List<Issue> getIssuesFromProject(Long pid, Long creationWeeks) throws SearchException {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.WEEK_OF_YEAR, (-1) * creationWeeks.intValue());

        SearchResults results = ComponentManager.getInstance().getSearchService().search(
            ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(),
            JqlQueryBuilder.newBuilder().where().project(pid).and().createdAfter(c.getTime()).buildQuery(),
            PagerFilter.getUnlimitedFilter());
        return results.getIssues();
    }

    private Map<String, Object> getVelocityParams(ProjectActionSupport action, Map reqParams) throws SearchException {
        Long projectId = ParameterUtils.getLongParam(reqParams, "projectId");
        Long creationWeeks = ParameterUtils.getLongParam(reqParams, "creationWeeks");
        String grouperId = ParameterUtils.getStringParam(reqParams, "groupField");
        List<String> statusIds = ParameterUtils.getListParam(reqParams, "statusIds");
        boolean isUserStatus = ParameterUtils.getBooleanParam(reqParams, "userstatus");

        final Map<String, Object> velocityParams = new HashMap<String, Object>();
        velocityParams.put("grouperId", grouperId);
        velocityParams.put("isUserStatus", isUserStatus);
        velocityParams.put("report", this);
        velocityParams.put("action", action);
        velocityParams.put("helper", new RendererHelper());

        Map<String, Object> data = new HashMap<String, Object>();
        List<Issue> issues = getIssuesFromProject(projectId, creationWeeks);
        for (Issue issue : issues) {
            List<ChangeHistoryItem> items = ComponentAccessor.getChangeHistoryManager().getAllChangeItems(issue);
            if (isUserStatus) {
                List<UserStatuses> userStatuses = CollectorUtils.reduceUserStatuses(
                    CollectorUtils.getUserStatuses(
                        new Users(CollectorUtils.getUserRanges(items, issue)),
                        new Statuses(CollectorUtils.getStatusRanges(items, issue))),
                    statusIds);
                data.put(issue.getKey(), userStatuses);
            } else {
                List<StatusUsers> statusUsers = CollectorUtils.reduceStatusUsers(
                    CollectorUtils.getStatusUsers(
                        new Users(CollectorUtils.getUserRanges(items, issue)),
                        new Statuses(CollectorUtils.getStatusRanges(items, issue))),
                    statusIds);
                data.put(issue.getKey(), statusUsers);
            }
        }

        velocityParams.put("data", data);
        return velocityParams;
    }

    @Override
    public boolean isExcelViewSupported() {
        return true;
    }

    @Override
    public boolean showReport() {
        return true;
    }

    @Override
    public void validate(ProjectActionSupport action, Map params) {
        Long creationWeeks = ParameterUtils.getLongParam(params, "creationWeeks");
        if (creationWeeks == null || creationWeeks <= 0) {
            action.addError("creationWeeks", ComponentAccessor.getJiraAuthenticationContext().getI18nHelper().getText("datacollector.report.date.error"));
        }
        List<String> statusIds = ParameterUtils.getListParam(params, "statusIds");
        if (statusIds == null || statusIds.isEmpty()) {
            action.addError("statusIds", ComponentAccessor.getJiraAuthenticationContext().getI18nHelper().getText("datacollector.report.statuses.error"));
        }
        super.validate(action, params);
    }
}
