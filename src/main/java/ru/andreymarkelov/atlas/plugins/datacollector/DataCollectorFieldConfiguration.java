package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

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

public class DataCollectorFieldConfiguration extends JiraWebActionSupport {
    private static final long serialVersionUID = 8667074025414801581L;

    private final ApplicationProperties applicationProperties;
    private final CustomFieldManager cfMgr;
    private final StatusManager statusMgr;
    private final StatusesTimeSumPluginData pluginData;

    private String currentField;
    private StatusesTimeSumDataFull data;
    private String[] statuses;
    private String compareField;
    private String approveTime;

    public DataCollectorFieldConfiguration(
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
    public String doDefault() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        if (StringUtils.isEmpty(currentField)) {
            return getRedirect("/secure/admin/DataCollectorConfiguration.jspa");
        }

        CustomField cf = cfMgr.getCustomFieldObject(currentField);
        if (cf == null || !cf.getCustomFieldType().getClass().equals(StatusesTimeSumCF.class)) {
            return getRedirect("/secure/admin/DataCollectorConfiguration.jspa");
        }

        StatusesTimeSumData storeData = pluginData.getJSONFieldData(cf.getId());
        if (storeData == null) {
            data = new StatusesTimeSumDataFull(cf.getId(), cf.getName());
        } else {
            data = new StatusesTimeSumDataFull(cf.getId(), cf.getName(), storeData);
        }

        return super.doDefault();
    }

    @Override
    @com.atlassian.jira.security.xsrf.RequiresXsrfCheck
    protected String doExecute() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        StatusesTimeSumData data = new StatusesTimeSumData();
        if (!StringUtils.isEmpty(approveTime)) {
            data.setApproveTime(Long.parseLong(approveTime));
        }
        data.setStatuses(Arrays.asList(statuses));
        pluginData.storeJSONFieldData(currentField, data);
        return getRedirect("/secure/admin/DataCollectorConfiguration.jspa");
    }

    @Override
    protected void doValidation() {
        if (!StringUtils.isEmpty(approveTime)) {
            try {
                Long val = Long.parseLong(approveTime);
                if (val <= 0) {
                    addErrorMessage(getText("datacollector.admin.conf.fields.approve.error"));
                }
            } catch (NumberFormatException nex) {
                addErrorMessage(getText("datacollector.admin.conf.fields.approve.error"));
            }
        }

        super.doValidation();
    }

    public String getApproveTime() {
        return approveTime;
    }

    public String getBackLink() {
        return getBaseUrl().concat("/secure/admin/DataCollectorConfiguration.jspa");
    }

    public String getBaseUrl() {
        return applicationProperties.getBaseUrl();
    }

    public String getCompareField() {
        return compareField;
    }

    public String getCurrentField() {
        return currentField;
    }

    public StatusesTimeSumDataFull getData() {
        return data;
    }

    public Collection<CustomField> getDefinedCustomFields() {
        return cfMgr.getCustomFieldObjects();
    }

    public Collection<Status> getDefinedStatuses() {
        return statusMgr.getStatuses();
    }

    public String[] getStatuses() {
        return statuses;
    }

    public String getTitle() {
        CustomField cf = cfMgr.getCustomFieldObject(currentField);
        if (cf != null) {
            return getText("datacollector.admin.conf.field.title", cf.getName());
        } else {
            return getText("datacollector.admin.conf.field.title", currentField);
        }
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

    public boolean isStatus(String status) {
        if (data == null || data.getStatuses() == null) {
            return false;
        }
        return data.getStatuses().contains(status);
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public void setCompareField(String compareField) {
        this.compareField = compareField;
    }

    public void setCurrentField(String currentField) {
        this.currentField = currentField;
    }

    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }
}
