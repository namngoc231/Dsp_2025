package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class LockDelEntity implements Serializable {
    private String lockedObject;
    private Date issueDate;
    private Long count;
    private String type;

    public LockDelEntity() {

    }

    public LockDelEntity(LockDelEntity ent) {
        this.lockedObject = ent.lockedObject;
        this.issueDate = ent.issueDate;
        this.count = ent.count;
        this.type = ent.type;
    }

    public String getLockedObject() {
        return lockedObject;
    }

    public void setLockedObject(String lockedObject) {
        this.lockedObject = lockedObject;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
