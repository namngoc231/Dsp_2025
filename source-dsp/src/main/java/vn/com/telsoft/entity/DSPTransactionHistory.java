package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DSPTransactionHistory implements Serializable {
    private Long transactionId;
    private Long actionType;
    private String description;
    private String filePath;
    private Date issueTime;
    private Long userId;
    private Long extendedDays;

    //
    private String comName;

    public DSPTransactionHistory() {
    }

    public DSPTransactionHistory(Long transactionId, Long actionType, String description, String filePath, Date issueTime, Long userId, Long extendedDays, String comName) {
        this.transactionId = transactionId;
        this.actionType = actionType;
        this.description = description;
        this.filePath = filePath;
        this.issueTime = issueTime;
        this.userId = userId;
        this.extendedDays = extendedDays;
        this.comName = comName;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getActionType() {
        return actionType;
    }

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getExtendedDays() {
        return extendedDays;
    }

    public void setExtendedDays(Long extendedDays) {
        this.extendedDays = extendedDays;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }
}
