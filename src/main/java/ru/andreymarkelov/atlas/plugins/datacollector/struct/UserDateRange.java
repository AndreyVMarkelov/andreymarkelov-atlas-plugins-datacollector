package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.Date;

public class UserDateRange extends DateRange {
    private String user;

    public UserDateRange(Date from, Date to, String user) {
        super(from, to);
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserDateRange[user=" + user + ", from=" + getFrom() + ", to=" + getTo() + "]";
    }
}
