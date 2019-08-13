package db;

import lombok.Data;


public class LetvCookieEntity {

    private long cookieId;

    public String cookieKey;

    public String cookieValue;

    public LetvCookieEntity(long cookieId, String cookieKey, String cookieValue) {
        this.cookieId = cookieId;
        this.cookieKey = cookieKey;
        this.cookieValue = cookieValue;
    }

    public LetvCookieEntity() {
    }

    public long getCookieId() {
        return cookieId;
    }

    public void setCookieId(long cookieId) {
        this.cookieId = cookieId;
    }

    public String getCookieKey() {
        return cookieKey;
    }

    public void setCookieKey(String cookieKey) {
        this.cookieKey = cookieKey;
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }
}
