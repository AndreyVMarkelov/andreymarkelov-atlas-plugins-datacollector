package ru.andreymarkelov.atlas.plugins.datacollector.struct;

import java.util.List;

public class IssueDataKeeper implements ICalculatedTotal {
    private String key;
    private String summary;
    private Object data;

    public IssueDataKeeper(String key, String summary, Object data) {
        this.key = key;
        this.summary = summary;
        this.data = data;
    }

    public String display() {
        return key.concat(": ").concat(summary);
    }

    public Object getData() {
        return data;
    }

    public String getKey() {
        return key;
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public long getTotalTime() {
        if (data instanceof List<?>) {
            long totalTime = 0;
            List<ICalculatedTotal> list = (List<ICalculatedTotal>) data;
            for (ICalculatedTotal item : list) {
                totalTime += item.getTotalTime();
            }
            return totalTime;
        } else {
            return 0;
        }
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "IssueDataKeeper[key=" + key + ", summary=" + summary + ", data=" + data + "]";
    }
}
