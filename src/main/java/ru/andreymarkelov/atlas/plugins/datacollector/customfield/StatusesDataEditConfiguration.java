package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import java.util.Arrays;
import java.util.Collection;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;

public class StatusesDataEditConfiguration extends AbstractEditConfigurationItemAction {
    /**
     * Unique ID.
     */
    private static final long serialVersionUID = -2008545966379826688L;

    private final StatusesTimeSumPluginData pluginData;

    private String[] statuses;
    private String compareField;

    public StatusesDataEditConfiguration(
            ManagedConfigurationItemService managedConfigurationItemService,
            StatusesTimeSumPluginData pluginData) {
        super(managedConfigurationItemService);
        this.pluginData = pluginData;
    }

    @Override
    public String doDefault() throws Exception {
        StatusesTimeSumData data = pluginData.getJSONFieldData(getFieldConfig());
        if (data != null) {
            this.compareField = data.getCompareField();
            this.statuses = data.getStatuses().toArray(new String[data.getStatuses().size()]);
        }

        return INPUT;
    }

    @Override
    protected String doExecute() throws Exception {
        if (!isHasPermission(Permissions.ADMINISTER)) {
            return "securitybreach";
        }

        if (statuses == null) {
            statuses = new String[0];
        }
        pluginData.storeJSONFieldData(getFieldConfig(), new StatusesTimeSumData(Arrays.asList(statuses), compareField));
        return getRedirect("/secure/admin/ConfigureCustomField!default.jspa?customFieldId=" + getFieldConfig().getCustomField().getIdAsLong().toString());
    }

    public String getCompareField() {
        return compareField;
    }

    public Collection<CustomField> getDefinedCustomFields() {
        return ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
    }

    public Collection<Status> getDefinedStatuses() {
        return ComponentAccessor.getConstantsManager().getStatusObjects();
    }

    public String[] getStatuses() {
        return statuses;
    }

    public boolean isStatus(String status) {
        if (statuses == null || statuses.length == 0) {
            return false;
        }
        return Arrays.binarySearch(statuses, status) >= 0;
    }

    public void setCompareField(String compareField) {
        this.compareField = compareField;
    }

    public void setStatuses(String[] statuses) {
        this.statuses = statuses;
    }
}
