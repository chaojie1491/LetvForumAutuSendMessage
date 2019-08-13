package db;

import java.util.Objects;

public class LetvLinkEntity {
    private long letvLinkId;
    private String letvLinkName;
    private String letvLink;
    private String letvLastTime;
    private String letvStatus;
    private long letvModuleId;

    public long getLetvLinkId() {
        return letvLinkId;
    }

    public void setLetvLinkId(long letvLinkId) {
        this.letvLinkId = letvLinkId;
    }

    public String getLetvLinkName() {
        return letvLinkName;
    }

    public void setLetvLinkName(String letvLinkName) {
        this.letvLinkName = letvLinkName;
    }

    public String getLetvLink() {
        return letvLink;
    }

    public void setLetvLink(String letvLink) {
        this.letvLink = letvLink;
    }

    public String getLetvLastTime() {
        return letvLastTime;
    }

    public void setLetvLastTime(String letvLastTime) {
        this.letvLastTime = letvLastTime;
    }

    public String getLetvStatus() {
        return letvStatus;
    }

    public void setLetvStatus(String letvStatus) {
        this.letvStatus = letvStatus;
    }

    public long getLetvModuleId() {
        return letvModuleId;
    }

    public void setLetvModuleId(long letvModuleId) {
        this.letvModuleId = letvModuleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetvLinkEntity that = (LetvLinkEntity) o;
        return letvLinkId == that.letvLinkId &&
                letvModuleId == that.letvModuleId &&
                Objects.equals(letvLinkName, that.letvLinkName) &&
                Objects.equals(letvLink, that.letvLink) &&
                Objects.equals(letvLastTime, that.letvLastTime) &&
                Objects.equals(letvStatus, that.letvStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(letvLinkId, letvLinkName, letvLink, letvLastTime, letvStatus, letvModuleId);
    }
}
