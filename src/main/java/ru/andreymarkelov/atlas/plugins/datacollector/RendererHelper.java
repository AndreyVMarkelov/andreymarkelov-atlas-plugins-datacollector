package ru.andreymarkelov.atlas.plugins.datacollector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.DateRange;

import com.atlassian.core.util.DateUtils;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;

public class RendererHelper {
    private UserManager userMgr;
    private ConstantsManager constMgr;
    private JiraAuthenticationContext context;
    private String baseUrl;

    public RendererHelper() {
        baseUrl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl");
        userMgr = ComponentAccessor.getUserManager();
        constMgr = ComponentAccessor.getConstantsManager();
        context = ComponentAccessor.getJiraAuthenticationContext();
    }

    public String renderDate(DateRange range) {
        return context.getOutlookDate().formatDMYHMS(range.getFrom()).concat(" - ").concat(context.getOutlookDate().formatDMYHMS(range.getTo()));
    }

    public String renderSpentTime(long spent) {
        return DateUtils.getDurationPretty(spent, context.getI18nHelper().getDefaultResourceBundle());
    }

    public String renderStatus(String status) {
        Status statusObj = constMgr.getStatusObject(status);
        if (statusObj != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<span class=\"value\">");
            sb.append("<img src=\"").append(baseUrl).append(statusObj.getIconUrlHtml());
            sb.append("\" width=\"16\" height=\"16\" title=\"").append(statusObj.getDescTranslation());
            sb.append("\" alt=\"").append(statusObj.getDescTranslation()).append("\"/>");
            sb.append(statusObj.getNameTranslation()).append("</span>");
            return sb.toString();
        } else {
            return status;
        }
    }

    public String renderStatusRaw(String status) {
        Status statusObj = constMgr.getStatusObject(status);
        if (statusObj != null) {
            return statusObj.getNameTranslation();
        } else {
            return status;
        }
    }

    public String renderUser(String user) {
        User userObj = userMgr.getUserObject(user);
        if (userObj != null) {
           String encodedUser;
            try {
                encodedUser = URLEncoder.encode(userObj.getName(),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                encodedUser = userObj.getName();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<a class='user-hover' rel='").append(userObj.getName());
            sb.append("' id='issue_summary_assignee_'").append(userObj.getName());
            sb.append("' href='/secure/ViewProfile.jspa?name='").append(encodedUser).append("'>");
            sb.append(userObj.getDisplayName()).append("</a>");
            return sb.toString();
        } else {
            return user;
        }
    }

    public String renderUserRaw(String user) {
        User userObj = userMgr.getUserObject(user);
        if (userObj != null) {
            return userObj.getDisplayName();
        } else {
            return user;
        }
    }
}
