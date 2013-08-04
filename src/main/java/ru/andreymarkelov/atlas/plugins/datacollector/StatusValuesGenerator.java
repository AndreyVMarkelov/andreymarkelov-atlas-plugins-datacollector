package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.util.I18nHelper;

public class StatusValuesGenerator implements ValuesGenerator {
    @Override
    public Map getValues(Map params) {
        Map<String, String> statusMap = new HashMap<String,String>();
        I18nHelper i18n = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
        Collection<Status> allStatuses = ComponentAccessor.getConstantsManager().getStatusObjects();
        for (Status status : allStatuses) {
            statusMap.put(status.getId().toString(), status.getNameTranslation(i18n));
        }
        return statusMap;
    }
}
