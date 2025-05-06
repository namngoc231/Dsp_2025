package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class AddDataDDRequestEntity implements Serializable {
    private Long transactionID;
    private Long ISDN;
    private String status;
    private Long amount;
    private Long activeDay;
    private Date requestTime;
    private Date processTime;
    private String group;
    private Long retries;
    private Long reqCost;

    public AddDataDDRequestEntity() {

    }

    public AddDataDDRequestEntity(AddDataDDRequestEntity ent) {
        this.transactionID = ent.transactionID;
        this.ISDN = ent.ISDN;
        this.status = ent.status;
        this.amount = ent.amount;
        this.activeDay = ent.activeDay;
        this.requestTime = ent.requestTime;
        this.processTime = ent.processTime;
        this.group = ent.group;
        this.retries = ent.retries;
        this.reqCost = ent.reqCost;
    }

    public Long getReqCost() {
        return reqCost;
    }

    public void setReqCost(Long reqCost) {
        this.reqCost = reqCost;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getISDN() {
        return ISDN;
    }

    public void setISDN(Long ISDN) {
        this.ISDN = ISDN;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getRetries() {
        return retries;
    }

    public void setRetries(Long retries) {
        this.retries = retries;
    }
}
