package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;

public class CollectorUtils {
    public static String getInitialStatus(List<ChangeHistoryItem> items, Issue issue) {
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("status")) {
                return item.getFroms().keySet().iterator().next();
            }
        }
        return issue.getStatusObject().getName();
    }

    public static String getInitialUser(List<ChangeHistoryItem> items, Issue issue) {
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("assignee")) {
                return item.getFroms().keySet().iterator().next();
            }
        }
        return issue.getAssigneeId();
    }

    public static List<DateRange> getRanges(List<ChangeHistoryItem> items, Issue issue) {
        List<DateRange> ranges = new ArrayList<DateRange>();
        boolean isFirst = true;
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("status") || item.getField().equals("assignee")) {
                if (isFirst) {
                    ranges.add(new DateRange(new Date(issue.getCreated().getTime()), new Date(item.getCreated().getTime())));
                    isFirst = false;
                } else {
                    ranges.add(new DateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime())));
                }
            }
        }
        return ranges;
    }

    public static Set<String> getStatuses(List<ChangeHistoryItem> items) {
        Set<String> statuses = new TreeSet<String>();
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("status")) {
                Map<String, String> from = item.getFroms();
                Map<String, String> to = item.getTos();
                statuses.add(from.keySet().iterator().next());
                statuses.add(to.keySet().iterator().next());
            }
        }
        return statuses;
    }

    public static List<StatusDateRange> getStatusRanges(List<ChangeHistoryItem> items, Issue issue) {
        List<StatusDateRange> ranges = new ArrayList<StatusDateRange>();
        boolean isFirst = true;
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("status")) {
                if (isFirst) {
                    ranges.add(new StatusDateRange(new Date(issue.getCreated().getTime()), new Date(item.getCreated().getTime()), getInitialStatus(items, issue)));
                    isFirst = false;
                } else {
                    ranges.add(new StatusDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime()), item.getFroms().keySet().iterator().next()));
                }
            }
        }
        if (ranges.isEmpty()) {
            ranges.add(new StatusDateRange(new Date(issue.getCreated().getTime()), new Date(), getInitialStatus(items, issue)));
        } else {
            ranges.add(new StatusDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(), issue.getStatusObject().getId()));
        }

        return ranges;
    }

    public static List<StatusUsers> getStatusUsers(Users users, Statuses statuses) {
        List<StatusUsers> statusUsersList = new ArrayList<StatusUsers>();

        for (Map.Entry<String, List<DateRange>> statusEntry : statuses.getStatuses().entrySet()) {
            String status = statusEntry.getKey();
            List<DateRange> statusRanges = statusEntry.getValue();

            StatusUsers statusUsers = new StatusUsers(status);
            for (Map.Entry<String, List<DateRange>> usersEntry : users.getUsers().entrySet()) {
                String user = usersEntry.getKey();
                List<DateRange> userRanges = usersEntry.getValue();

                List<DateRange> range = RangeUtils.intersectRanges(statusRanges, userRanges);
                if (!range.isEmpty()) {
                    statusUsers.putUser(user, range);
                }
            }
            statusUsersList.add(statusUsers);
        }
        return statusUsersList;
    }

    public static List<UserDateRange> getUserRanges(List<ChangeHistoryItem> items, Issue issue) {
        List<UserDateRange> ranges = new ArrayList<UserDateRange>();
        boolean isFirst = true;
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("assignee")) {
                if (isFirst) {
                    ranges.add(new UserDateRange(new Date(issue.getCreated().getTime()), new Date(item.getCreated().getTime()), getInitialUser(items, issue)));
                    isFirst = false;
                } else {
                    ranges.add(new UserDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime()), item.getFroms().keySet().iterator().next()));
                }
            }
        }
        if (ranges.isEmpty()) {
            ranges.add(new UserDateRange(new Date(issue.getCreated().getTime()), new Date(), getInitialUser(items, issue)));
        } else {
            ranges.add(new UserDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(), issue.getAssignee().getName()));
        }

        return ranges;
    }

    public static Set<String> getUsers(List<ChangeHistoryItem> items) {
        Set<String> users = new TreeSet<String>();
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("assignee")) {
                Map<String, String> from = item.getFroms();
                Map<String, String> to = item.getTos();
                users.add(from.keySet().iterator().next());
                users.add(to.keySet().iterator().next());
            }
        }
        return users;
    }

    public static List<UserStatuses> getUserStatuses(Users users, Statuses statuses) {
        List<UserStatuses> userStatusesList = new ArrayList<UserStatuses>();

        for (Map.Entry<String, List<DateRange>> usersEntry : users.getUsers().entrySet()) {
            String user = usersEntry.getKey();
            List<DateRange> userRanges = usersEntry.getValue();

            UserStatuses userStatuses = new UserStatuses(user);
            for (Map.Entry<String, List<DateRange>> statusEntry : statuses.getStatuses().entrySet()) {
                String status = statusEntry.getKey();
                List<DateRange> statusRanges = statusEntry.getValue();

                List<DateRange> range = RangeUtils.intersectRanges(userRanges, statusRanges);
                if (!range.isEmpty()) {
                    userStatuses.putStatus(status, range);
                }
            }
            userStatusesList.add(userStatuses);
        }
        return userStatusesList;
    }

    public static List<StatusUsers> reduceStatusUsers(List<StatusUsers> statusUsers, List<String> statusIds) {
        Iterator<StatusUsers> iter = statusUsers.iterator();
        while (iter.hasNext()) {
            StatusUsers su = iter.next();
            if (!statusIds.contains(su.getStatus())) {
                iter.remove();
            }
        }
        return statusUsers;
    }

    public static List<UserStatuses> reduceUserStatuses(List<UserStatuses> userStatuses, List<String> statusIds) {
        for (UserStatuses us : userStatuses) {
            Iterator<String> iter = us.getStatuses().keySet().iterator();
            while (iter.hasNext()) {
                String status = iter.next();
                if (!statusIds.contains(status)) {
                    iter.remove();
                }
            }
        }
        return userStatuses;
    }

    private CollectorUtils() {
    }
}
