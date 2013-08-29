package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.andreymarkelov.atlas.plugins.datacollector.RangeUtils;

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

    public boolean isValid() {
        return !statuses.isEmpty();
    }

    public void putStatus(String status, List<DateRange> range) {
        if (statuses.containsKey(status)) {
            statuses.get(status).addAll(range);
        } else {
            statuses.put(status, range);
        }
    }

    public void reduceByRange(DateRange intDr) {
        Iterator<Map.Entry<String, List<DateRange>>> entryIter = statuses.entrySet().iterator();
        while (entryIter.hasNext()) {
            Map.Entry<String, List<DateRange>> entry = entryIter.next();
            List<DateRange> ranges = entry.getValue();
            List<DateRange> newRanges = new ArrayList<DateRange>();
            for (DateRange dr : ranges) {
                DateRange intersection = RangeUtils.getIntersectionRange(dr, intDr);
                if (intersection != null) {
                    newRanges.add(intersection);
                }
            }
            if (newRanges.isEmpty()) {
                entryIter.remove();
            } else {
                entry.setValue(newRanges);
            }
        }
    }

    @Override
    public String toString() {
        return "UserStatuses[user=" + user + ", statuses=" + statuses + "]";
    }
}
