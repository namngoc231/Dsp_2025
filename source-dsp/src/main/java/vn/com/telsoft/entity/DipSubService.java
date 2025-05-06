package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DipSubService implements Serializable {

    private String isdn;
    private Long packageId;
    private Long serviceId;
    private Long hId;
    private Date startTime;
    private Date endTime;
    private Long initialAmount;
    private Date lastUpdateTime;
    private Long activeDay;
    private String channel;
    private Long userId;
    private String userName;
    private String packageName;
    private String serviceName;

    public DipSubService() {

    }

    public DipSubService(String isdn, Long packageId, Long serviceId, Long hId, Date startTime, Date endTime, Long initialAmount, Date lastUpdateTime, Long activeDay, String channel, Long userId, String userName, String packageName, String serviceName) {
        this.isdn = isdn;
        this.packageId = packageId;
        this.serviceId = serviceId;
        this.hId = hId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.initialAmount = initialAmount;
        this.lastUpdateTime = lastUpdateTime;
        this.activeDay = activeDay;
        this.channel = channel;
        this.userId = userId;
        this.userName = userName;
        this.packageName = packageName;
        this.serviceName = serviceName;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long gethId() {
        return hId;
    }

    public void sethId(Long hId) {
        this.hId = hId;
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

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
