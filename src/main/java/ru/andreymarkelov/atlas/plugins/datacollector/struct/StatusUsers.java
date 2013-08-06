package ru.andreymarkelov.atlas.plugins.datacollector.struct;

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

    private long getRangesTime(List<DateRange> ranges) {
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

    public String getStatus() {
        return status;
    }

    public long getTotalTime() {
        long totalTime = 0;
        for (Map.Entry<String, List<DateRange>> entry : users.entrySet()) {
            totalTime += getRangesTime(entry.getValue());
        }
        return totalTime;
    }

    public Map<String, List<DateRange>> getUsers() {
        return users;
    }

    public int getUsersCount() {
        return users.size();
    }

    public long getUserTime(String user) {
        return getRangesTime(users.get(user));
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
