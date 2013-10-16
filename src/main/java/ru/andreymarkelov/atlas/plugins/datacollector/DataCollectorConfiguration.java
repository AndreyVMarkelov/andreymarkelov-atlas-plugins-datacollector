package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.List;

import ru.andreymarkelov.atlas.plugins.datacollector.customfield.StatusesTimeSumCF;
import ru.andreymarkelov.atlas.plugins.datacollector.customfield.StatusesTimeSumData;
import ru.andreymarkelov.atlas.plugins.datacollector.customfield.StatusesTimeSumDataFull;
import ru.andreymarkelov.atlas.plugins.datacollector.customfield.StatusesTimeSumPluginData;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

public class DataCollectorConfiguration extends JiraWebActionSupport {
    private static final long serialVersionUID = -2008545966379826688L;

    private final ApplicationProperties applicationProperties;
    private final CustomFieldManager cfMgr;
    private final StatusManager statusMgr;
    private final StatusesTimeSumPluginData pluginData;

    private List<StatusesTimeSumDataFull> datas;

    public DataCollectorConfiguration(
            StatusesTimeSumPluginData pluginData,
            ApplicationProperties applicationProperties,
            CustomFieldManager cfMgr,
            StatusManager statusMgr) {
        this.pluginData = pluginData;
        this.applicationProperties = applicationProperties;
        this.cfMgr = cfMgr;
        this.statusMgr = statusMgr;
    }

    @Override
    public String doExecute() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        datas = new ArrayList<StatusesTimeSumDataFull>();
        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList) {
            if (cf.getCustomFieldType().getClass().equals(StatusesTimeSumCF.class)) {
                StatusesTimeSumData data = pluginData.getJSONFieldData(cf.getId());
                if (data == null) {
                    datas.add(new StatusesTimeSumDataFull(cf.getId(), cf.getName()));
                } else {
                    datas.add(new StatusesTimeSumDataFull(cf.getId(), cf.getName(), data));
                }
            }
        }

        return SUCCESS;
    }

    public String getBaseUrl() {
        return applicationProperties.getBaseUrl();
    }

    public List<StatusesTimeSumDataFull> getDatas() {
        return datas;
    }

    public boolean hasAdminPermission() {
        User user = getLoggedInUser();
        if (user == null) {
            return false;
        }
        if (getPermissionManager().hasPermission(Permissions.ADMINISTER, getLoggedInUser())) {
            return true;
        }
        return false;
    }

    public String renderStatus(String statusId) {
        Status status = statusMgr.getStatus(statusId);
        return (status != null) ? status.getNameTranslation() : statusId;
    }
}
