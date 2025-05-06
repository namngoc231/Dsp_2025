package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TRIEUNV
 */
public class DSPServicePriceTab implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long tabId;
    private Long serviceId;
    private Date startTime;
    private Date endTime;
    private String status;
    private String description;
    private String currency;
    private Integer defaultValue;
    private String filePath;
    private Long block;
    private String strBlock;

    private Integer comApply;

    public DSPServicePriceTab() {
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public Integer getComApply() {
        return comApply;
    }

    public void setComApply(Integer comApply) {
        this.comApply = comApply;
    }

    public Long getBlock() {
        return block;
    }

    public void setBlock(Long block) {
        this.block = block;
    }

    public String getStrBlock() {
        return strBlock;
    }

    public void setStrBlock(String strBlock) {
        this.strBlock = strBlock;
    }
}
