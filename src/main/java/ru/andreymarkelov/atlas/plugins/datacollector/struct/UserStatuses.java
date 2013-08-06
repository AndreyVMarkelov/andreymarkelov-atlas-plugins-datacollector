package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserStatuses implements ICalculatedTotal {
    private String user;
    private Map<String, List<DateRange>> statuses;

    public UserStatuses(String user) {
        this.user = user;
        statuses = new HashMap<String, List<DateRange>>();
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

    public Map<String, List<DateRange>> getStatuses() {
        return statuses;
    }

    public int getStatusesCount() {
        return statuses.size();
    }

    public long getStatusTime(String status) {
        return getRangesTime(statuses.get(status));
    }

    @Override
    public long getTotalTime() {
        long totalTime = 0;
        for (Map.Entry<String, List<DateRange>> entry : statuses.entrySet()) {
            totalTime += getRangesTime(entry.getValue());
        }
        return totalTime;
    }

    public String getUser() {
        return user;
    }

    public void putStatus(String status, List<DateRange> range) {
        if (statuses.containsKey(status)) {
            statuses.get(status).addAll(range);
        } else {
            statuses.put(status, range);
        }
    }

    @Override
    public String toString() {
        return "UserStatuses[user=" + user + ", statuses=" + statuses + "]";
    }
}
