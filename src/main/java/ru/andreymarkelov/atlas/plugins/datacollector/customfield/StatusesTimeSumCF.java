package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.util.concurrent.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.andreymarkelov.atlas.plugins.datacollector.CollectorUtils;
import ru.andreymarkelov.atlas.plugins.datacollector.RendererHelper;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.IssueDataKeeper;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.StatusUsers;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Statuses;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Users;

public class StatusesTimeSumCF extends CalculatedCFType<String, String> {
    private final StatusesTimeSumPluginData pluginData;
    private final TemplateRenderer renderer;

    public StatusesTimeSumCF(
            StatusesTimeSumPluginData pluginData,
            TemplateRenderer renderer) {
        this.pluginData = pluginData;
        this.renderer = renderer;
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        final List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new StatusesTimeSumCFConfig(renderer, pluginData));
        return configurationItemTypes;
    }

    private Double getFieldDoubleValue(CustomField field, Issue issue) {
        Object obj = field.getValue(issue);
        if (obj != null) {
            try {
                return Double.parseDouble(obj.toString().replaceAll(",", "."));
            } catch (Exception ex) {
                return Double.valueOf(0);
            }
        } else {
            return Double.valueOf(0);
        }
    }

    private long getIssueStatusesTime(CustomField field, Issue issue, StatusesTimeSumData data) {
        List<ChangeHistoryItem> items = ComponentAccessor.getChangeHistoryManager().getAllChangeItems(issue);
        Users users = new Users(CollectorUtils.getUserRanges(items, issue));
        Statuses statuses = new Statuses(CollectorUtils.getStatusRanges(items, issue));
        List<StatusUsers> statusUsers = CollectorUtils.getStatusUsers(users, statuses);
        statusUsers = CollectorUtils.reduceStatusUsers(statusUsers, data.getStatuses());
        return new IssueDataKeeper(issue.getKey(), issue.getSummary(), statusUsers).getTotalTime();
    }

    @Override
    public String getSingularObjectFromString(String str) throws FieldValidationException {
        return str;
    }

    @Override
    public String getStringFromSingularObject(String str) {
        return str;
    }

    @Override
    @Nullable
    public String getValueFromIssue(CustomField field, Issue issue) {
        if (issue == null || issue.getKey() == null) {
            return "";
        }

        StatusesTimeSumData data = pluginData.getJSONFieldData(field.getRelevantConfig(issue));
        return new RendererHelper().renderSpentTime(getIssueStatusesTime(field, issue, data));
    }

    @Override
    public Map<String, Object> getVelocityParameters(
            final Issue issue,
            final CustomField field,
            final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        if (issue == null || issue.getKey() == null) {
            return map;
        }

        StatusesTimeSumData data = pluginData.getJSONFieldData(field.getRelevantConfig(issue));
        long realVal = getIssueStatusesTime(field, issue, data);
        map.put("value", new RendererHelper().renderSpentTime(realVal));

        if (data.getCompareField() != null) {
            CustomField cf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(data.getCompareField());
            if (cf != null) {
                Long planVal = Double.valueOf(getFieldDoubleValue(cf, issue).doubleValue() * 60 * 60).longValue();
                String color = (realVal <= planVal) ? "green" : "red";
                map.put("color", color);
            }
        }

        return map;
    }
}
