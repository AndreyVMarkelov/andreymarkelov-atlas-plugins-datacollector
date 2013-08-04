package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Statuses {
    private Map<String, List<DateRange>> statuses;

    public Statuses(List<StatusDateRange> ranges) {
        statuses = new HashMap<String, List<DateRange>>();
        for (StatusDateRange range : ranges) {
            if (statuses.containsKey(range.getStatus())) {
                statuses.get(range.getStatus()).add(range);
            } else {
                List<DateRange> userRange = new ArrayList<DateRange>();
                userRange.add(range);
                statuses.put(range.getStatus(), userRange);
            }
        }
    }

    public Map<String, List<DateRange>> getStatuses() {
        return statuses;
    }
}
