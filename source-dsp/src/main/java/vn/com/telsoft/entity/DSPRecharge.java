package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DSPRecharge implements Serializable {
    private String isdn;
    private Date issueDate;
    private String ref;
    private String profileCode;
    private Long amount;
    private Integer days;
    private String serial;
    private String status;
    private String description;
    private Date retryDate;
    private String addon;

    public DSPRecharge() {
    }

    public DSPRecharge(String isdn, Date issueDate, String ref, String profileCode, Long amount, Integer days, String serial, String status, String description, Date retryDate, String addon) {
        this.isdn = isdn;
        this.issueDate = issueDate;
        this.ref = ref;
        this.profileCode = profileCode;
        this.amount = amount;
        this.days = days;
        this.serial = serial;
        this.status = status;
        this.description = description;
        this.retryDate = retryDate;
        this.addon = addon;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRetryDate() {
        return retryDate;
    }

    public void setRetryDate(Date retryDate) {
        this.retryDate = retryDate;
    }

    public String getAddon() {
        return addon;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }
}
