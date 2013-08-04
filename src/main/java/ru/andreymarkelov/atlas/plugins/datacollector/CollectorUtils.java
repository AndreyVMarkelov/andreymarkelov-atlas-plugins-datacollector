package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.DateRange;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.StatusDateRange;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.StatusUsers;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Statuses;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.UserDateRange;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.UserStatuses;
import ru.andreymarkelov.atlas.plugins.datacollector.struct.Users;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;

public class CollectorUtils {
    public static String getInitialStatus(List<ChangeHistoryItem> items, Issue issue) {
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("status")) {
                if (!item.getFroms().isEmpty()) {
                    return item.getFroms().keySet().iterator().next();
                } else {
                    return "unknown";
                }
            }
        }
        return issue.getStatusObject().getId();
    }

    public static String getInitialUser(List<ChangeHistoryItem> items, Issue issue) {
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("assignee")) {
                if (!item.getFroms().isEmpty()) {
                    return item.getFroms().keySet().iterator().next();
                } else {
                    return "unknown";
                }
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
                if (!from.isEmpty()) {
                    statuses.add(from.keySet().iterator().next());
                } else {
                    statuses.add("unknown");
                }
                if (!to.isEmpty()) {
                    statuses.add(to.keySet().iterator().next());
                } else {
                    statuses.add("unknown");
                }
            }
        }
        return statuses;
    }

    public static List<StatusDateRange> getStatusRanges(List<ChangeHistoryItem> items, Issue issue) {
        try {
            List<StatusDateRange> ranges = new ArrayList<StatusDateRange>();
            boolean isFirst = true;
            for (ChangeHistoryItem item : items) {
                if (item.getField().equals("status")) {
                    if (isFirst) {
                        ranges.add(new StatusDateRange(new Date(issue.getCreated().getTime()), new Date(item.getCreated().getTime()), getInitialStatus(items, issue)));
                        isFirst = false;
                    } else {
                        if (!item.getFroms().isEmpty()) {
                            ranges.add(new StatusDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime()), item.getFroms().keySet().iterator().next()));
                        } else {
                            ranges.add(new StatusDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime()), "unknown"));
                        }
                    }
                }
            }
            if (ranges.isEmpty()) {
                ranges.add(new StatusDateRange(new Date(issue.getCreated().getTime()), new Date(), getInitialStatus(items, issue)));
            } else {
                ranges.add(new StatusDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(), issue.getStatusObject().getId()));
            }
            return ranges;
        } catch (Exception ex) {
            throw new RuntimeException("Issue: " + issue.getKey(), ex);
        }
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
        try {
            List<UserDateRange> ranges = new ArrayList<UserDateRange>();
            boolean isFirst = true;
            for (ChangeHistoryItem item : items) {
                if (item.getField().equals("assignee")) {
                    if (isFirst) {
                        ranges.add(new UserDateRange(new Date(issue.getCreated().getTime()), new Date(item.getCreated().getTime()), getInitialUser(items, issue)));
                        isFirst = false;
                    } else {
                        if (!item.getFroms().isEmpty()) {
                            ranges.add(new UserDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime()), item.getFroms().keySet().iterator().next()));
                        } else {
                            ranges.add(new UserDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(item.getCreated().getTime()), "unknown"));
                        }
                    }
                }
            }
            if (ranges.isEmpty()) {
                ranges.add(new UserDateRange(new Date(issue.getCreated().getTime()), new Date(), getInitialUser(items, issue)));
            } else {
                ranges.add(new UserDateRange(ranges.get(ranges.size() - 1).getTo(), new Date(), issue.getAssignee().getName()));
            }
            return ranges;
        } catch (Exception ex) {
            throw new RuntimeException("Issue: " + issue.getKey(), ex);
        }
    }

    public static Set<String> getUsers(List<ChangeHistoryItem> items) {
        Set<String> users = new TreeSet<String>();
        for (ChangeHistoryItem item : items) {
            if (item.getField().equals("assignee")) {
                Map<String, String> from = item.getFroms();
                Map<String, String> to = item.getTos();
                if (!from.isEmpty()) {
                    users.add(from.keySet().iterator().next());
                } else {
                    users.add("unknown");
                }
                if (!to.isEmpty()) {
                    users.add(to.keySet().iterator().next());
                } else {
                    users.add("unknown");
                }
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
        Iterator<UserStatuses> usIter = userStatuses.iterator();
        while (usIter.hasNext()) {
            UserStatuses us = usIter.next();
            Iterator<String> iter = us.getStatuses().keySet().iterator();
            while (iter.hasNext()) {
                String status = iter.next();
                if (!statusIds.contains(status)) {
                    iter.remove();
                }
            }
            if (us.getStatuses().isEmpty()) {
                usIter.remove();
            }
        }
        return userStatuses;
    }

    private CollectorUtils() {
    }
}
