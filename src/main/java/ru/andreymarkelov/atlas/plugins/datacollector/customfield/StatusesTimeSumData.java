package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

import java.util.ArrayList;
import java.util.List;

public class StatusesTimeSumData {
    private List<String> statuses;
    private String compareField;
    private long approveTime;

    public StatusesTimeSumData() {
        this.statuses = new ArrayList<String>();
    }

    public StatusesTimeSumData(List<String> statuses, String compareField) {
        this.statuses = statuses;
        this.compareField = compareField;
    }

    public void addStatus(String statusId) {
        this.statuses.add(statusId);
    }

    public long getApproveTime() {
        return approveTime;
    }

    public String getCompareField() {
        return compareField;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setApproveTime(long approveTime) {
        this.approveTime = approveTime;
    }

    public void setCompareField(String compareField) {
        this.compareField = compareField;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    @Override
    public String toString() {
        return "StatusesTimeSumData [statuses=" + statuses + ", compareField=" + compareField + ", approveTime=" + approveTime + "]";
    }
}
