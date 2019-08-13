package db;

import java.util.Objects;

public class LetvUserEntity {
    private long letvUserId;
    private String letvUserUid;
    private String letvUserLink;
    private String letvUserStatus;
    private short letvLink;

    public long getLetvUserId() {
        return letvUserId;
    }

    public void setLetvUserId(long letvUserId) {
        this.letvUserId = letvUserId;
    }

    public String getLetvUserUid() {
        return letvUserUid;
    }

    public void setLetvUserUid(String letvUserUid) {
        this.letvUserUid = letvUserUid;
    }

    public String getLetvUserLink() {
        return letvUserLink;
    }

    public void setLetvUserLink(String letvUserLink) {
        this.letvUserLink = letvUserLink;
    }

    public String getLetvUserStatus() {
        return letvUserStatus;
    }

    public void setLetvUserStatus(String letvUserStatus) {
        this.letvUserStatus = letvUserStatus;
    }

    public short getLetvLink() {
        return letvLink;
    }

    public void setLetvLink(short letvLink) {
        this.letvLink = letvLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetvUserEntity that = (LetvUserEntity) o;
        return letvUserId == that.letvUserId &&
                letvLink == that.letvLink &&
                Objects.equals(letvUserUid, that.letvUserUid) &&
                Objects.equals(letvUserLink, that.letvUserLink) &&
                Objects.equals(letvUserStatus, that.letvUserStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(letvUserId, letvUserUid, letvUserLink, letvUserStatus, letvLink);
    }
}
