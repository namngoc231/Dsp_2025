package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TUNGLM
 */
public class DSPOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long comId;
    private Long tabId;
    private String orderCode;
    private Date orderTime;
    private String status;
    private Long contractValue;
    private Long paidCost;
    private String payMethod;
    private String description;
    private String contractCode;
    private Long approvalId;
    private String approvalUser;
    private String approvalDesc;
    private Date approvalTime;
    private Date effectiveTime;
    private Date expireTime;
    private String rejectReason;
    private Long remainValue;
    private Long reservedValue;
    private Long remainQuotaValue;
    private String serial;
    private Date cancelTime;
    private String type;
    private String discountType;
    private Long discountAmt;
    //Add
    private String oldStatus;
    private Long userId;
    private String userName;
    private String filePath;
    private Date activatedDate;
    private Long moneyUsed;
    private String adminVasMobile;
    private Long managerUserId;
    private String managerMobile;
    private Double discountPercent; // tỷ lệ phần trăm hiển thị ở màn thêm mới và màn sửa. mail HuyNQ 02/08/2023

    private DSPCompany dspCompany;

    public DSPOrder() {
        this.dspCompany = new DSPCompany();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getContractValue() {
        return contractValue;
    }

    public void setContractValue(Long contractValue) {
        this.contractValue = contractValue;
    }

    public Long getPaidCost() {
        return paidCost;
    }

    public void setPaidCost(Long paidCost) {
        this.paidCost = paidCost;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public String getApprovalUser() {
        return approvalUser;
    }

    public void setApprovalUser(String approvalUser) {
        this.approvalUser = approvalUser;
    }

    public String getApprovalDesc() {
        return approvalDesc;
    }

    public void setApprovalDesc(String approvalDesc) {
        this.approvalDesc = approvalDesc;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Long getRemainValue() {
        return remainValue;
    }

    public void setRemainValue(Long remainValue) {
        this.remainValue = remainValue;
    }

    public Long getReservedValue() {
        return reservedValue;
    }

    public void setReservedValue(Long reservedValue) {
        this.reservedValue = reservedValue;
    }

    public Long getRemainQuotaValue() {
        return remainQuotaValue;
    }

    public void setRemainQuotaValue(Long remainQuotaValue) {
        this.remainQuotaValue = remainQuotaValue;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
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

    public DSPCompany getDspCompany() {
        return dspCompany;
    }

    public void setDspCompany(DSPCompany dspCompany) {
        this.dspCompany = dspCompany;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(Date activatedDate) {
        this.activatedDate = activatedDate;
    }

    public Long getMoneyUsed() {
        return moneyUsed;
    }

    public void setMoneyUsed(Long moneyUsed) {
        this.moneyUsed = moneyUsed;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Long getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(Long discountAmt) {
        this.discountAmt = discountAmt;
    }

    public String getAdminVasMobile() {
        return adminVasMobile;
    }

    public void setAdminVasMobile(String adminVasMobile) {
        this.adminVasMobile = adminVasMobile;
    }

    public Long getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(Long managerUserId) {
        this.managerUserId = managerUserId;
    }

    public String getManagerMobile() {
        return managerMobile;
    }

    public void setManagerMobile(String managerMobile) {
        this.managerMobile = managerMobile;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }
}
