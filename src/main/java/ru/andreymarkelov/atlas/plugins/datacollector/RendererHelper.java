package ru.andreymarkelov.atlas.plugins.datacollector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.DateRange;

import com.atlassian.core.util.DateUtils.Duration;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;

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

    private String getDurationPrettySeconds(
            long numSecs,
            I18nHelper i18n,
            boolean secondResolution) {
        if (numSecs == 0) {
            if (secondResolution) {
                return "0 " + i18n.getText("datacollector.dateutils.seconds");
            } else {
                return "0 " + i18n.getText("datacollector.dateutils.minutes");
            }
        }

        StringBuffer result = new StringBuffer();
        if (numSecs >= Duration.HOUR.getSeconds()) {
            long hours = numSecs / Duration.HOUR.getSeconds();
            result.append(hours).append(' ');
            if (hours > 1) {
                result.append(i18n.getText("datacollector.dateutils.hours"));
            } else {
                result.append(i18n.getText("datacollector.dateutils.hour"));
            }
            result.append(", ");
            numSecs = numSecs % Duration.HOUR.getSeconds();
        }

        if (numSecs >= Duration.MINUTE.getSeconds()) {
            long minute = numSecs / Duration.MINUTE.getSeconds();
            result.append(minute).append(' ');
            if (minute > 1) {
                result.append(i18n.getText("datacollector.dateutils.minutes"));
            } else {
                result.append(i18n.getText("datacollector.dateutils.minute"));
            }
            result.append(", ");
            if (secondResolution) {
                numSecs = numSecs % Duration.MINUTE.getSeconds();
            }
        }

        if (numSecs >= 1 && numSecs < Duration.MINUTE.getSeconds()) {
            result.append(numSecs).append(' ');
            if (numSecs > 1) {
                result.append(i18n.getText("datacollector.dateutils.seconds"));
            } else {
                result.append(i18n.getText("datacollector.dateutils.second"));
            }
            result.append(", ");
        }

        if (result.length() > 2) {
            return result.substring(0, result.length() - 2);
        } else {
            return result.toString();
        }
    }

    public String renderDate(DateRange range) {
        return context.getOutlookDate().formatDMYHMS(range.getFrom()).concat(" - ").concat(context.getOutlookDate().formatDMYHMS(range.getTo()));
    }

    public String renderSpentTime(long spent) {
        return getDurationPrettySeconds(spent, context.getI18nHelper(), false);
    }

    public String renderStatus(String status) {
        Status statusObj = constMgr.getStatusObject(status);
        if (statusObj != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<span class=\"value\">");
            sb.append("<img src=\"").append(baseUrl).append(statusObj.getIconUrlHtml());
            sb.append("\" width=\"16\" height=\"16\" title=\"").append(statusObj.getDescTranslation());
            sb.append("\" alt=\"").append(statusObj.getDescTranslation()).append("\"/>");
            sb.append(statusObj.getNameTranslation(context.getI18nHelper())).append("</span>");
            return sb.toString();
        } else {
            return status;
        }
    }

    public String renderStatusRaw(String status) {
        Status statusObj = constMgr.getStatusObject(status);
        if (statusObj != null) {
            return statusObj.getNameTranslation(context.getI18nHelper());
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
