package ru.andreymarkelov.atlas.plugins.datacollector;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.StatusDateRange;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;

public class ServletInfo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String issue = req.getParameter("issue");
        MutableIssue mu = ComponentAccessor.getIssueManager().getIssueObject(issue);
        List<ChangeHistoryItem> items = ComponentAccessor.getChangeHistoryManager().getAllChangeItems(mu);
        List<StatusDateRange> sts = CollectorUtils.getStatusRanges(items, mu);
        resp.getWriter().write("<html>");
        resp.getWriter().write("<table border=\"1\">");
        for (ChangeHistoryItem item : items) {
            resp.getWriter().write("<tr><td>" + item.getField() + "|" + item.getCreated() + "|" + item.getFrom() + "|" + item.getTo() + "</td></tr>");
        }
        resp.getWriter().write("</table>");
        resp.getWriter().write("<table border=\"1\">");
        for (StatusDateRange st : sts) {
            resp.getWriter().write("<tr><td>" + ComponentAccessor.getConstantsManager().getStatusObject(st.getStatus()).getName() + "|" + st.getFrom() + "|" + st.getTo() + "</td></tr>");
        }
        resp.getWriter().write("</table>");
        resp.getWriter().write("</html>");
    }
}
