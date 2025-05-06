package vn.com.telsoft.entity;
// Generated Jan 25, 2014 4:35:06 PM by Hibernate Tools 3.6.0

import java.util.Date;

public class DSPSubService implements java.io.Serializable {

    private String isdn;
    private String service;
    private Date startTime;
    private Date endTime;
    private Long initialAmount;
    private Date lastUpdate;
    private String groupName;
    private String alertEndTime;
    private String serviceName;
    private Long totalCycle;
    private Long currCycle;


    public DSPSubService() {
    }

    public DSPSubService(DSPSubService ett) {
        this.isdn = ett.getIsdn();
        this.service = ett.getService();
        this.startTime = ett.startTime;
        this.endTime = ett.endTime;
        this.initialAmount = ett.getInitialAmount();
        this.lastUpdate = ett.getLastUpdate();
        this.groupName = ett.getGroupName();
        this.alertEndTime = ett.getAlertEndTime();
        this.totalCycle = ett.getTotalCycle();
        this.currCycle = ett.getCurrCycle();
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public Long getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Long initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAlertEndTime() {
        return alertEndTime;
    }

    public void setAlertEndTime(String alertEndTime) {
        this.alertEndTime = alertEndTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getTotalCycle() {
        return totalCycle;
    }

    public void setTotalCycle(Long totalCycle) {
        this.totalCycle = totalCycle;
    }

    public Long getCurrCycle() {
        return currCycle;
    }

    public void setCurrCycle(Long currCycle) {
        this.currCycle = currCycle;
    }
}
