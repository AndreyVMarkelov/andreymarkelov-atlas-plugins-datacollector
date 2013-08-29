package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import com.atlassian.jira.issue.fields.config.FieldConfig;

public interface PluginData {
    StatusesTimeSumData getJSONFieldData(FieldConfig config);

    void storeJSONFieldData(FieldConfig config, StatusesTimeSumData data);
}
