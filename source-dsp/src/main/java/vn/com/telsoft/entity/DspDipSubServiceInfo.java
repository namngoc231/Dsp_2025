package vn.com.telsoft.entity;

import java.util.Date;

public class DspDipSubServiceInfo {
    private String serviceName;
    private String profile_code;
    private Long dataAmt;
    private Long totalAmount;
    private Date startDate;
    private Date expDate;
    private String isdn;

    private String channel;

    private Long userId;

    public DspDipSubServiceInfo() {

    }

    public DspDipSubServiceInfo(String serviceName, String profile_code, Long dataAmt, Long totalAmount, Date startDate, Date expDate, String isdn, String channel, Long userId) {
        this.serviceName = serviceName;
        this.profile_code = profile_code;
        this.dataAmt = dataAmt;
        this.totalAmount = totalAmount;
        this.startDate = startDate;
        this.expDate = expDate;
        this.isdn = isdn;
        this.channel = channel;
        this.userId = userId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProfile_code() {
        return profile_code;
    }

    public void setProfile_code(String profile_code) {
        this.profile_code = profile_code;
    }

    public Long getDataAmt() {
        return dataAmt;
    }

    public void setDataAmt(Long dataAmt) {
        this.dataAmt = dataAmt;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
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
}
