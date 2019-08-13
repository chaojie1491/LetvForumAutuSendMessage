package entity;


public enum Status {

    TRUE("TRUE","已发送"),
    FALSE("FALSE","未发送");

    String code;

    String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    Status(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
