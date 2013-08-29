package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class PluginDataImpl implements PluginData {
    /**
     * Plug-In Jira db key.
     */
    private final String PLUGIN_KEY = "StatusesTimeSumData";

    /**
     * Plug-In settings.
     */
    private final PluginSettings pluginSettings;

    public PluginDataImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY);;
    }

    @Override
    public StatusesTimeSumData getJSONFieldData(FieldConfig config) {
        Object obj = getPluginSettings().get(getKey(config));
        if (obj != null) {
            return StatusesTimeSumDataTranslator.jsonDataFromString(obj.toString());
        } else {
            return null;
        }
    }

    private String getKey(FieldConfig config) {
        return config.getFieldId().concat("_").concat(config.getId().toString()).concat("_").concat("config");
    }

    private synchronized PluginSettings getPluginSettings() {
        return pluginSettings;
    }

    @Override
    public void storeJSONFieldData(FieldConfig config, StatusesTimeSumData data) {
        getPluginSettings().put(getKey(config), StatusesTimeSumDataTranslator.jsonDataToString(data));
    }
}
