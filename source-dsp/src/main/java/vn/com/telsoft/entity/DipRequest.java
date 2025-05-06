package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DipRequest implements Serializable {
    private Long requestId;
    private Long fileId;
    private String channel;
    private String isdn;
    private String packageCode;
    private Date requestTime;
    private Long status;
    private String apiReqId;
    private Long transactionId;
    private Double initialAmount;
    private Long activeDay;
    private Long priceId;
    private Long moneyAmount;

    private Long serviceId;
    private String index;
    private String logErr;
    private String displayIsdn;

    public DipRequest() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getApiReqId() {
        return apiReqId;
    }

    public void setApiReqId(String apiReqId) {
        this.apiReqId = apiReqId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Long getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(Long moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getLogErr() {
        return logErr;
    }

    public void setLogErr(String logErr) {
        this.logErr = logErr;
    }

    public String getDisplayIsdn() {
        return displayIsdn;
    }

    public void setDisplayIsdn(String displayIsdn) {
        this.displayIsdn = displayIsdn;
    }

    public String uniqueAttributes() {
        return isdn + serviceId;
    }
}
