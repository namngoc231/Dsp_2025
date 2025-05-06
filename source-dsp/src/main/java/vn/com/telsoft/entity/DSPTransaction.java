package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DSPTransaction implements Serializable {
    private Long transactionId;
    private Long tabId;
    private Long serviceId;
    private Date requestTime;
    private String status;
    private String description;
    private Long userId;
    private Date deliveryTime;
    private Date planTime;
    private Long amount;
    private Long comId;
    private Long dataAmount;
    private String resOrderId;

    private String comName;
    private String comNumber;

    private DSPService dspService;
    private DSPCompany dspCompany;

    private String userName;

    public DSPTransaction() {
    }

    public DSPTransaction(DSPService dspService, DSPCompany dspCompany) {
        this.dspService = dspService;
        this.dspCompany = dspCompany;
    }

    public DSPTransaction(Long transactionId, Long tabId, Long serviceId, Date requestTime, String status, String description, Long userId, Date deliveryTime, Date planTime, Long amount, Long comId, Long dataAmount, String resOrderId, String comName, String comNumber) {
        this.transactionId = transactionId;
        this.tabId = tabId;
        this.serviceId = serviceId;
        this.requestTime = requestTime;
        this.status = status;
        this.description = description;
        this.userId = userId;
        this.deliveryTime = deliveryTime;
        this.planTime = planTime;
        this.amount = amount;
        this.comId = comId;
        this.dataAmount = dataAmount;
        this.resOrderId = resOrderId;
        this.comName = comName;
        this.comNumber = comNumber;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public Long getDataAmount() {
        return dataAmount;
    }

    public void setDataAmount(Long dataAmount) {
        this.dataAmount = dataAmount;
    }

    public String getResOrderId() {
        return resOrderId;
    }

    public void setResOrderId(String resOrderId) {
        this.resOrderId = resOrderId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public DSPService getDspService() {
        return dspService;
    }

    public void setDspService(DSPService dspService) {
        this.dspService = dspService;
    }

    public DSPCompany getDspCompany() {
        return dspCompany;
    }

    public void setDspCompany(DSPCompany dspCompany) {
        this.dspCompany = dspCompany;
    }

    public String getComNumber() {
        return comNumber;
    }

    public void setComNumber(String comNumber) {
        this.comNumber = comNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
