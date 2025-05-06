package vn.com.telsoft.ws;

import java.util.Date;

/**
 * @author TUNGLM
 */
public class DcApiResponse {

    private String code;
    private String description;
    private String transactionId;
    private String jobId;

    //HuyNQ 09/11/2020
    private String status;
    private String dataAmount;
    private String activeDays;
    private String expDate;
    private String creDate;
    private String actDate;
    private String suspDate;
    private String useDate;
    private String ref;
    private String addon;
    private String reseller;
    private String orderCode;

    public DcApiResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataAmount() {
        return dataAmount;
    }

    public void setDataAmount(String dataAmount) {
        this.dataAmount = dataAmount;
    }

    public String getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCreDate() {
        return creDate;
    }

    public void setCreDate(String creDate) {
        this.creDate = creDate;
    }

    public String getActDate() {
        return actDate;
    }

    public void setActDate(String actDate) {
        this.actDate = actDate;
    }

    public String getSuspDate() {
        return suspDate;
    }

    public void setSuspDate(String suspDate) {
        this.suspDate = suspDate;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getAddon() {
        return addon;
    }

    public void setAddon(String addon) {
        this.addon = addon;
    }

    public String getReseller() {
        return reseller;
    }

    public void setReseller(String reseller) {
        this.reseller = reseller;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
