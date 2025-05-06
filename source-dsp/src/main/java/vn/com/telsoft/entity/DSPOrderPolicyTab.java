package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TRIEUNV
 */
public class DSPOrderPolicyTab implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long tabId;
    private Long serviceId;
    private Date startDate;
    private Date endDate;
    private String status;
    private String description;
    private Integer defaultValue;
    private String filePath;
    private String custType;
    private Integer comApply;

    public DSPOrderPolicyTab() {
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

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public Integer getComApply() {
        return comApply;
    }

    public void setComApply(Integer comApply) {
        this.comApply = comApply;
    }
}
