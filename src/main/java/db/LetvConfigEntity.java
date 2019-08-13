package db;

public class LetvConfigEntity {
    private long configId;

    public long getConfigId() {
        return configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }

    private String configMessageText;

    public String getConfigMessageText() {
        return configMessageText;
    }

    public void setConfigMessageText(String configMessageText) {
        this.configMessageText = configMessageText;
    }
}
