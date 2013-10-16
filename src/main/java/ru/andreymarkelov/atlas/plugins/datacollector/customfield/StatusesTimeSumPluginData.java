package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

public interface StatusesTimeSumPluginData {
    StatusesTimeSumData getJSONFieldData(String customFieldId);
    void storeJSONFieldData(String customFieldId, StatusesTimeSumData data);
}
