package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TUNGLM
 */
public class DSPOrderStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String oldStatus;
    private String newStatus;
    private Date issueTime;
    private String description;
    private Long userId;
    //
    private String filePath;
    private String userName;
    private Date oldExpireTime;
    private Date newExpireTime;



    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public Date getOldExpireTime() {
        return oldExpireTime;
    }

    public void setOldExpireTime(Date oldExpireTime) {
        this.oldExpireTime = oldExpireTime;
    }

    public Date getNewExpireTime() {
        return newExpireTime;
    }

    public void setNewExpireTime(Date newExpireTime) {
        this.newExpireTime = newExpireTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
