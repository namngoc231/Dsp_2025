package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DSPOrderTransaction implements Serializable {
    private Long transactionId;
    private Long orderId;
    private Date issueTime;
    private String description;
    private Long userId;
    private Long amount;

    public DSPOrderTransaction() {
    }

    public DSPOrderTransaction(Long transactionId, Long orderId, Date issueTime, String description, Long userId, Long amount) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.issueTime = issueTime;
        this.description = description;
        this.userId = userId;
        this.amount = amount;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
