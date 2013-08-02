package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {
    private Map<String, List<DateRange>> users;

    public Users(List<UserDateRange> ranges) {
        users = new HashMap<String, List<DateRange>>();
        for (UserDateRange range : ranges) {
            if (users.containsKey(range.getUser())) {
                users.get(range.getUser()).add(range);
            } else {
                List<DateRange> userRange = new ArrayList<DateRange>();
                userRange.add(range);
                users.put(range.getUser(), userRange);
            }
        }
    }

    public Map<String, List<DateRange>> getUsers() {
        return users;
    }
}
