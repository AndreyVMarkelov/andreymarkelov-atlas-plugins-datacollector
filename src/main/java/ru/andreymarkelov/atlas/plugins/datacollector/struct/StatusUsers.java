package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.andreymarkelov.atlas.plugins.datacollector.RangeUtils;

public class StatusUsers implements ICalculatedTotal {
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

    @Override
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

    public boolean isValid() {
        return !users.isEmpty();
    }

    public void putUser(String user, List<DateRange> range) {
        if (users.containsKey(user)) {
            users.get(user).addAll(range);
        } else {
            users.put(user, range);
        }
    }

    public void reduceByRange(DateRange intDr) {
        Iterator<Map.Entry<String, List<DateRange>>> entryIter = users.entrySet().iterator();
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
        return "StatusUsers[status=" + status + ", users=" + users + "]";
    }
}
