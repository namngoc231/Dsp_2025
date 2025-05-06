package vn.com.telsoft.entity;

import java.io.Serializable;

public class ApParam implements Serializable {
    private String parName;
    private String parType;
    private String parValue;
    private String description;

    public ApParam() {
    }

    public ApParam(String parName, String parType, String parValue, String description) {
        this.parName = parName;
        this.parType = parType;
        this.parValue = parValue;
        this.description = description;
    }

    public String getParName() {
        return parName;
    }

    public void setParName(String parName) {
        this.parName = parName;
    }

    public String getParType() {
        return parType;
    }

    public void setParType(String parType) {
        this.parType = parType;
    }

    public String getParValue() {
        return parValue;
    }

    public void setParValue(String parValue) {
        this.parValue = parValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
