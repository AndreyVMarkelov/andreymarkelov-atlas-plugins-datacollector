package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.Date;

public class StatusDateRange extends DateRange {
    private String status;

    public StatusDateRange(Date from, Date to, String status) {
        super(from, to);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusDateRange[status=" + status + ", from=" + getFrom() + ", to=" + getTo() + "]";
    }
}
