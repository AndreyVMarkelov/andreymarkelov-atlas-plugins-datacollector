package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;

public class GrouperValuesGenerator implements ValuesGenerator {
    @Override
    public Map getValues(Map params) {
        Map<String, String> grouperMap = new HashMap<String,String>();
        grouperMap.put("null", "-");
        List<CustomField> fields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
        for (CustomField field : fields) {
            grouperMap.put(field.getId(), field.getName());
        }
        return grouperMap;
    }
}
