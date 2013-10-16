package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class StatusesTimeSumPluginDataImpl implements StatusesTimeSumPluginData {
    private final String PLUGIN_KEY = "StatusesTimeSumData";

    private final PluginSettings pluginSettings;

    public StatusesTimeSumPluginDataImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY);;
    }

    @Override
    public StatusesTimeSumData getJSONFieldData(String customFieldId) {
        Object obj = getPluginSettings().get(customFieldId);
        if (obj != null) {
            return StatusesTimeSumDataTranslator.jsonDataFromString(obj.toString());
        } else {
            return null;
        }
    }

    private synchronized PluginSettings getPluginSettings() {
        return pluginSettings;
    }

    @Override
    public void storeJSONFieldData(String customFieldId, StatusesTimeSumData data) {
        getPluginSettings().put(customFieldId, StatusesTimeSumDataTranslator.jsonDataToString(data));
    }
}
