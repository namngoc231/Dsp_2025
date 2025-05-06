package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TRIEUNV
 */
public class DSPComPrice implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long tabId;
    private Long comId;
    private String status;
    private String description;
    private Date appliedDate;
    private Date removedDate;

    public DSPComPrice() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }

    public Date getRemovedDate() {
        return removedDate;
    }

    public void setRemovedDate(Date removedDate) {
        this.removedDate = removedDate;
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

}
