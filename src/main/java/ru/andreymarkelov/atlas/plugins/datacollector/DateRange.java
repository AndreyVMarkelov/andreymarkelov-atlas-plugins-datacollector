package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.Date;

public class DateRange {
    private Date from;
    private Date to;

    public DateRange(Date from, Date to) {
        this.from = from;
        this.to = to;
    }

    public long getDistance() {
        return to.getTime() - from.getTime();
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "DateRange[from=" + from + ", to=" + to + "]";
    }
}
