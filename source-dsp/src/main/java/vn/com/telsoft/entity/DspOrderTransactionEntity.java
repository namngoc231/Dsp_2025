package vn.com.telsoft.entity;


import java.io.Serializable;
import java.util.Date;

public class DspOrderTransactionEntity implements Serializable {
    private Long transactionID;
    private Long oderID;
    private Date issueTime;
    private String description;
    private Long userId;
    private Long Amount;

    public DspOrderTransactionEntity() {

    }

    public DspOrderTransactionEntity(DspOrderTransactionEntity ent) {
        this.transactionID = ent.getTransactionID();
        this.oderID = ent.getOderID();
        this.issueTime = ent.getIssueTime();
        this.description = ent.getDescription();
        this.userId = ent.getUserId();
        this.Amount = ent.getAmount();
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getOderID() {
        return oderID;
    }

    public void setOderID(Long oderID) {
        this.oderID = oderID;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }
}

