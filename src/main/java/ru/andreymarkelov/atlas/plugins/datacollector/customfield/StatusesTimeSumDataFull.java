package ru.andreymarkelov.atlas.plugins.datacollector.customfield;

public class StatusesTimeSumDataFull extends StatusesTimeSumData {
    private String cfId;
    private String cfName;

    public StatusesTimeSumDataFull(String cfId, String cfName) {
        this.cfId = cfId;
        this.cfName = cfName;
    }

    public StatusesTimeSumDataFull(String cfId, String cfName, StatusesTimeSumData data) {
        this.cfId = cfId;
        this.cfName = cfName;
        setApproveTime(data.getApproveTime());
        setCompareField(data.getCompareField());
        setStatuses(data.getStatuses());
    }

    public String getCfId() {
        return cfId;
    }

    public String getCfName() {
        return cfName;
    }

    @Override
    public String toString() {
        return "StatusesTimeSumDataFull [cfId=" + cfId + ", cfName=" + cfName + ", getApproveTime()="
            + getApproveTime() + ", getCompareField()=" + getCompareField() + ", getStatuses()=" + getStatuses() + "]";
    }
}
