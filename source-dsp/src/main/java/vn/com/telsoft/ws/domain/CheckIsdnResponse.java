package vn.com.telsoft.ws.domain;

public class CheckIsdnResponse {
    private String code;
    private String description;
    private String transId;
    private String isdn;
    private Boolean vasIsdn;

    public CheckIsdnResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public Boolean getVasIsdn() {
        return vasIsdn;
    }

    public void setVasIsdn(Boolean vasIsdn) {
        this.vasIsdn = vasIsdn;
    }
}
