package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.IssueDataKeeper;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Statuses;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.UserStatuses;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Users;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.jira.web.bean.PagerFilter;

public class AssigneeHistoryReport extends AbstractReport {
    @Override
    public String generateReportExcel(ProjectActionSupport action, Map reqParams) throws Exception {
        return descriptor.getHtml("excel", getVelocityParams(action, reqParams));
    }

    @Override
    public String generateReportHtml(ProjectActionSupport action, Map reqParams) throws Exception {
        return descriptor.getHtml("view", getVelocityParams(action, reqParams));
    }

    private List<Issue> getIssuesFromProject(Long pid) throws SearchException {
        SearchResults results = ComponentManager.getInstance().getSearchService().search(
            ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(),
            JqlQueryBuilder.newBuilder().where().project(pid).buildQuery(),
            PagerFilter.getUnlimitedFilter());
        return results.getIssues();
    }

    public long getIssueTotalTime(List<IssueDataKeeper> issues) {
        long res = 0;
        for (IssueDataKeeper idk : issues) {
            res += idk.getTotalTime();
        }

        return res;
    }

    private Map<String, Object> getVelocityParams(ProjectActionSupport action, Map reqParams) throws SearchException {
        User remoteUser = action.getRemoteUser();
        I18nHelper i18nBean = new I18nBean(remoteUser);
        Date startDate = ParameterUtils.getDateParam(reqParams, "startDate", i18nBean.getLocale());
        Date endDate = null;//ParameterUtils.getDateParam(reqParams, "endDate", i18nBean.getLocale());
        List<String> statusIds = ParameterUtils.getListParam(reqParams, "statusIds");
        if (statusIds == null || statusIds.isEmpty()) {
            statusIds = new ArrayList<String>();
            statusIds.add(ParameterUtils.getStringParam(reqParams, "statusIds"));
        }
        Project proj = ComponentAccessor.getProjectManager().getProjectObj(Long.valueOf(reqParams.get("selectedProjectId").toString()));

        Map<String, List<IssueDataKeeper>> usersData = new HashMap<String, List<IssueDataKeeper>>();

        List<Issue> issues = getIssuesFromProject(proj.getId());
        for (Issue issue : issues) {
            List<ChangeHistoryItem> items = ComponentAccessor.getChangeHistoryManager().getAllChangeItems(issue);

            List<UserStatuses> userStatuses = CollectorUtils.reduceUserStatuses(
                    CollectorUtils.getUserStatuses(
                        new Users(CollectorUtils.getUserRanges(items, issue)),
                        new Statuses(CollectorUtils.getStatusRanges(items, issue))),
                    statusIds);
            for (UserStatuses userStatus : userStatuses) {
                if (usersData.containsKey(userStatus.getUser())) {
                    List<UserStatuses> userStatuses1 = new ArrayList<UserStatuses>();
                    userStatuses1.add(userStatus);
                    usersData.get(userStatus.getUser()).add(new IssueDataKeeper(issue.getKey(), issue.getSummary(), userStatuses1));
                } else {
                    List<IssueDataKeeper> id = new ArrayList<IssueDataKeeper>();
                    List<UserStatuses> userStatuses1 = new ArrayList<UserStatuses>();
                    userStatuses1.add(userStatus);
                    id.add(new IssueDataKeeper(issue.getKey(), issue.getSummary(), userStatuses1));
                    usersData.put(userStatus.getUser(), id);
                }
            }
        }

        final Map<String, Object> velocityParams = new HashMap<String, Object>();
        velocityParams.put("report", this);
        velocityParams.put("action", action);
        velocityParams.put("helper", new RendererHelper());
        velocityParams.put("dataranges", true);
        velocityParams.put("usersData", usersData);

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
