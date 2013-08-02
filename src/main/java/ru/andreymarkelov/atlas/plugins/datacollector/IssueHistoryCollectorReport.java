package ru.andreymarkelov.atlas.plugins.datacollector;

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
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;

public class IssueHistoryCollectorReport extends AbstractReport {

    @Override
    public String generateReportHtml(ProjectActionSupport action, Map reqParams) throws Exception {
        return descriptor.getHtml("view", getVelocityParams(action, reqParams));
    }

    private List<Issue> getIssuesFromProject(Long pid) throws SearchException {
        SearchResults results = ComponentManager.getInstance().getSearchService().search(
            ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(),
            JqlQueryBuilder.newBuilder().where().project(pid).buildQuery(),
            PagerFilter.newPageAlignedFilter(0, 10));
        return results.getIssues();
    }

    private Map<String, Object> getVelocityParams(ProjectActionSupport action, Map reqParams) throws SearchException {
        final String projectid = (String) reqParams.get("projectId");
        final Long pid = new Long(projectid);
        final Map<String, Object> velocityParams = new HashMap<String, Object>();
        velocityParams.put("report", this);
        velocityParams.put("action", action);
        velocityParams.put("helper", new RendererHelper());

        Map<String, List<UserStatuses>> data = new HashMap<String, List<UserStatuses>>();
        List<Issue> issues = getIssuesFromProject(pid);
        for (Issue issue : issues) {
            List<ChangeHistoryItem> items = ComponentAccessor.getChangeHistoryManager().getAllChangeItems(issue);
            List<UserStatuses> userStatuses = CollectorUtils.getUserStatuses(new Users(CollectorUtils.getUserRanges(items, issue)), new Statuses(CollectorUtils.getStatusRanges(items, issue)));
            data.put(issue.getKey(), userStatuses);
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
        super.validate(action, params);
    }
}
