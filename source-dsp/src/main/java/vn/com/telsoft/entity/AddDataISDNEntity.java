package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class AddDataISDNEntity implements Serializable {
    private Long transactionID;
    private Long serviceID;
    private Date requestTime;
    private String status;
    private String description;
    private Long userID;
    private Long userName;
    private Date deliveryTime;
    private Date planTime;
    private Long amount;
    private Long totalCap;
    private String comName;

    private String ISDN;
    private String statusRequest;
    private Long amountRequest;
    private Long activeDay;
    private Date requestTimeRequest;
    private Date processTimeRequest;
    private String group;
    private Long retries;
    private String serviceName;
    private String serviceCode;
    private Long comID;
    private Long dataAmount;
    private String vasMobile;
    private Long reqCost;

    public AddDataISDNEntity() {

    }

    public AddDataISDNEntity(AddDataISDNEntity ent) {
        this.transactionID = ent.transactionID;
        this.serviceID = ent.serviceID;
        this.requestTime = ent.requestTime;
        this.status = ent.status;
        this.description = ent.description;
        this.userID = ent.userID;
        this.userName = ent.userName;
        this.deliveryTime = ent.deliveryTime;
        this.planTime = ent.planTime;
        this.amount = ent.amount;
        this.totalCap = ent.totalCap;
        this.ISDN = ent.ISDN;
        this.statusRequest = ent.statusRequest;
        this.amountRequest = ent.amountRequest;
        this.activeDay = ent.activeDay;
        this.requestTimeRequest = ent.requestTimeRequest;
        this.processTimeRequest = ent.processTimeRequest;
        this.group = ent.group;
        this.retries = ent.retries;
        this.comName = ent.comName;
        this.serviceName = ent.serviceName;
        this.comID = ent.comID;
        this.dataAmount = ent.dataAmount;
        this.serviceCode = ent.serviceCode;
        this.vasMobile = ent.vasMobile;
        this.reqCost = ent.reqCost;
    }

    public Long getReqCost() {
        return reqCost;
    }

    public void setReqCost(Long reqCost) {
        this.reqCost = reqCost;
    }

    public String getVasMobile() {
        return vasMobile;
    }

    public void setVasMobile(String vasMobile) {
        this.vasMobile = vasMobile;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Long getDataAmount() {
        return dataAmount;
    }

    public void setDataAmount(Long dataAmount) {
        this.dataAmount = dataAmount;
    }

    public Long getComID() {
        return comID;
    }

    public void setComID(Long comID) {
        this.comID = comID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public Long getTotalCap() {
        return totalCap;
    }

    public void setTotalCap(Long totalCap) {
        this.totalCap = totalCap;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getServiceID() {
        return serviceID;
    }

    public void setServiceID(Long serviceID) {
        this.serviceID = serviceID;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
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

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getUserName() {
        return userName;
    }

    public void setUserName(Long userName) {
        this.userName = userName;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getISDN() {
        return ISDN;
    }

    public void setISDN(String ISDN) {
        this.ISDN = ISDN;
    }

    public String getStatusRequest() {
        return statusRequest;
    }

    public void setStatusRequest(String statusRequest) {
        this.statusRequest = statusRequest;
    }

    public Long getAmountRequest() {
        return amountRequest;
    }

    public void setAmountRequest(Long amountRequest) {
        this.amountRequest = amountRequest;
    }

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
    }

    public Date getRequestTimeRequest() {
        return requestTimeRequest;
    }

    public void setRequestTimeRequest(Date requestTimeRequest) {
        this.requestTimeRequest = requestTimeRequest;
    }

    public Date getProcessTimeRequest() {
        return processTimeRequest;
    }

    public void setProcessTimeRequest(Date processTimeRequest) {
        this.processTimeRequest = processTimeRequest;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getRetries() {
        return retries;
    }

    public void setRetries(Long retries) {
        this.retries = retries;
    }
}
