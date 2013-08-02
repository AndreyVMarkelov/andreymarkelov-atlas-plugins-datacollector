package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusUsers {
    private String status;
    private Map<String, List<DateRange>> users;

    public StatusUsers(String status) {
        this.status = status;
        users = new HashMap<String, List<DateRange>>();
    }

    public String getStatus() {
        return status;
    }

    public Map<String, List<DateRange>> getUsers() {
        return users;
    }

    public int getUsersCount() {
        return users.hashCode();
    }

    public long getUserTime(String user) {
        List<DateRange> ranges = users.get(user);
        if (ranges != null) {
            long sum = 0;
            for (DateRange range : ranges) {
                sum += range.getDistance();
            }
            return (sum/1000);
        } else {
            return 0;
        }
    }

    public void putUser(String user, List<DateRange> range) {
        if (users.containsKey(user)) {
            users.get(user).addAll(range);
        } else {
            users.put(user, range);
        }
    }

    @Override
    public String toString() {
        return "StatusUsers [status=" + status + ", users=" + users + "]";
    }
}
