package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.status.Status;

public class StatusValuesGenerator implements ValuesGenerator {
    @Override
    public Map getValues(Map params) {
        Map<String, String> statusMap = new HashMap<String,String>();
        Collection<Status> allStatuses = ComponentAccessor.getConstantsManager().getStatusObjects();
        for (Status status : allStatuses) {
            statusMap.put(status.getId().toString(), status.getName());
        }
        return statusMap;
    }
}
