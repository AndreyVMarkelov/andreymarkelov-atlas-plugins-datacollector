package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.Statuses;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Users;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;

public class StatusCollectorAction extends AbstractIssueAction {
    private Issue issue;

    public StatusCollectorAction(
            IssueTabPanelModuleDescriptor descriptor,
            Issue issue) {
        super(descriptor);
        this.issue = issue;
    }

    @Override
    public Date getTimePerformed() {
        return new Date();
    }

    @Override
    protected void populateVelocityParams(Map params) {
        List<ChangeHistoryItem> items = ComponentAccessor.getChangeHistoryManager().getAllChangeItems(issue);
        params.put("helper", new RendererHelper());
        params.put("statusinfo", CollectorUtils.getStatusUsers(new Users(CollectorUtils.getUserRanges(items, issue)), new Statuses(CollectorUtils.getStatusRanges(items, issue))));
    }
}
