package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class LetvCookieTable extends RecursiveTreeObject<LetvCookieTable> {

    public long cookieId;

    public StringProperty cookieKey;

    public StringProperty cookieValue;

    public LetvCookieTable(long cookieId,String cookieKey, String cookieValue) {
        this.cookieId = cookieId;
        this.cookieKey = new SimpleStringProperty(cookieKey);
        this.cookieValue = new SimpleStringProperty(cookieValue);
    }
}
