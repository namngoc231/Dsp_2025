package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class AmUser implements Serializable {
    private Long userId;
    private String userName;
    private String passWord;
    private Integer expireStatus;
    private String fullName;
    private Integer status;
    private String phone;
    private String mobile;
    private String fax;
    private String email;
    private String address;
    private Date modifiedPassword;
    private Date lockedDate;
    private Long failureCount;
    private Long createId;
    private Long deleteId;
    private Long modifiedId;
    private String config;

    public AmUser(Long userId, String userName, String passWord, Integer expireStatus, String fullName, Integer status, String phone, String mobile, String fax, String email, String address, Date modifiedPassword, Date lockedDate, Long failureCount, Long createId, Long deleteId, Long modifiedId, String config) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.expireStatus = expireStatus;
        this.fullName = fullName;
        this.status = status;
        this.phone = phone;
        this.mobile = mobile;
        this.fax = fax;
        this.email = email;
        this.address = address;
        this.modifiedPassword = modifiedPassword;
        this.lockedDate = lockedDate;
        this.failureCount = failureCount;
        this.createId = createId;
        this.deleteId = deleteId;
        this.modifiedId = modifiedId;
        this.config = config;
    }

    public AmUser() {
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

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Integer getExpireStatus() {
        return expireStatus;
    }

    public void setExpireStatus(Integer expireStatus) {
        this.expireStatus = expireStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getModifiedPassword() {
        return modifiedPassword;
    }

    public void setModifiedPassword(Date modifiedPassword) {
        this.modifiedPassword = modifiedPassword;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

    public Long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Long failureCount) {
        this.failureCount = failureCount;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public Long getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(Long deleteId) {
        this.deleteId = deleteId;
    }

    public Long getModifiedId() {
        return modifiedId;
    }

    public void setModifiedId(Long modifiedId) {
        this.modifiedId = modifiedId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
