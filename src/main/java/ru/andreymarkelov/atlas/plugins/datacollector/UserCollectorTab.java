package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;

public class UserCollectorTab implements IssueTabPanel {
    private IssueTabPanelModuleDescriptor descriptor;

    @Override
    public List<IssueAction> getActions(Issue issue, User user) {
        List<IssueAction> panelActions = new ArrayList<IssueAction>();
        panelActions.add(new UserCollectorAction(descriptor, issue));
        return panelActions;
    }

    @Override
    public void init(IssueTabPanelModuleDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public boolean showPanel(Issue issue, User user) {
        return true;
    }
}
