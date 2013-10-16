package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.templaterenderer.TemplateRenderer;

public class StatusesTimeSumCFConfig implements FieldConfigItemType {
    private static final Logger logger = Logger.getLogger(StatusesTimeSumCFConfig.class);

    private final StatusesTimeSumPluginData pluginData;
    private final TemplateRenderer renderer;

    public StatusesTimeSumCFConfig(
            TemplateRenderer renderer,
            StatusesTimeSumPluginData pluginData) {
        this.renderer = renderer;
        this.pluginData = pluginData;
    }

    @Override
    public String getBaseEditUrl() {
        return "StatusesDataEditConfiguration!default.jspa";
    }

    @Override
    public Object getConfigurationObject(Issue issue, FieldConfig config) {
        Map<String, Object> parms = new HashMap<String, Object>();
        StatusesTimeSumData data = pluginData.getJSONFieldData("");
        if (data != null) {
            parms.put("statuses", data.getStatuses());
            parms.put("compareField", data.getCompareField());
        }
        return parms;
    }

    @Override
    public String getDisplayName() {
        return "Configure Statuses Time Field";
    }

    @Override
    public String getDisplayNameKey() {
        return "Configure Statuses Time Field";
    }

    @Override
    public String getObjectKey() {
        return "StatusesTimeSumCFConfig";
    }

    @Override
    public String getViewHtml(FieldConfig config, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> parms = new HashMap<String, Object>();
        StatusesTimeSumData data = pluginData.getJSONFieldData("");
        if (data != null) {
            I18nHelper i18n = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
            List<String> transletedStatuses = new ArrayList<String>();
            for (String statusId : data.getStatuses()) {
                transletedStatuses.add(ComponentAccessor.getConstantsManager().getStatusObject(statusId).getNameTranslation(i18n));
            }

            parms.put("i18n", i18n);
            parms.put("statuses", transletedStatuses);
            if (data.getCompareField() != null && data.getCompareField().length() > 0) {
                parms.put("compareField", ComponentAccessor.getCustomFieldManager().getCustomFieldObject(data.getCompareField()).getName());
            }
        }
        StringWriter sw = new StringWriter();
        try {
            renderer.render("/templates/customfields/statuses-time-sum-cf/view-config.vm", parms, sw);
        } catch (Exception e) {
            logger.error("Render exception", e);
            sw.append("Render exception");
        }
        return sw.toString();
    }
}
