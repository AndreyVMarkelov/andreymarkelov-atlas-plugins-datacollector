package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;

public class ProjectValuesGenerator implements ValuesGenerator {
    @Override
    public Map getValues(Map params) {
        Map<String, String> projectMap = new HashMap<String,String>();
        List<Project> allProjects = ComponentAccessor.getProjectManager().getProjectObjects();
        for (Project project : allProjects) {
            projectMap.put(project.getId().toString(), project.getName());
        }
        return projectMap;
    }
}
