package vn.com.telsoft.entity;

import vn.com.telsoft.util.Utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author HoangNH
 */
public class DSPCompany implements Serializable {
    private Long comId;
    private String comName;
    private String taxCode;
    private String busCode;
    private String address;
    private String vasMobile;
    private String representative;
    private String repPhone;
    private String repMobile;
    private String repPosition;
    private String email;
    private String publicKey;
    private Date updatedKey;
    private Long userId;
    private Long parentId;
    private String status;
    private String description;
    private Long type;
    private String province;
    private String city;
    private String district;
    private String ward;
    private int level;
    private String userName;
    private String showAddress;
    private String filePath;
    private String cpsMobile;
    private String serialPrefix;
    private String apiPublicKey;
    private Long apiUserId;
    private String apiUserName;
    private Date apiUpdatedKey;
    private String bhttCode;
    private Date checkDate;
    private Date bkCheckDate;
    private String custType;

    public DSPCompany() {
    }

    public DSPCompany(Long comId, String comName, String taxCode, String busCode, String vasMobile) {
        this.comId = comId;
        this.comName = comName;
        this.taxCode = taxCode;
        this.busCode = busCode;
        this.vasMobile = vasMobile;
    }

    public DSPCompany(DSPCompany dto) {
        this.comId = dto.getComId();
        this.comName = dto.getComName();
        this.taxCode = dto.getTaxCode();
        this.busCode = dto.getBusCode();
        this.address = dto.getAddress();
        this.vasMobile = dto.getVasMobile();
        this.representative = dto.getRepresentative();
        this.repPhone = dto.getRepPhone();
        this.repMobile = dto.getRepMobile();
        this.repPosition = dto.getRepPosition();
        this.email = dto.getEmail();
        this.publicKey = dto.getPublicKey();
        this.updatedKey = dto.getUpdatedKey();
        this.userId = dto.getUserId();
        this.parentId = dto.getParentId();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.type = dto.getType();
        this.province = dto.getProvince();
        this.city = dto.getCity();
        this.district = dto.getDistrict();
        this.ward = dto.getWard();
        this.level = dto.getLevel();
        this.userName = dto.getUserName();
        this.showAddress = dto.getShowAddress();
        this.filePath = dto.getFilePath();
        this.cpsMobile = dto.getCpsMobile();
        this.serialPrefix = dto.getSerialPrefix();
        this.apiPublicKey = dto.getApiPublicKey();
        this.apiUserId = dto.getApiUserId();
        this.apiUserName = dto.getApiUserName();
        this.apiUpdatedKey = dto.getApiUpdatedKey();
        this.bhttCode = dto.getBhttCode();
        this.checkDate = dto.getCheckDate();
        this.bkCheckDate = dto.getBkCheckDate();
        this.custType = dto.getCustType();
    }

    public DSPCompany(Long comId, String comName, String taxCode, String busCode, String vasMobile, String cpsMobile) {
        this.comId = comId;
        this.comName = comName;
        this.taxCode = taxCode;
        this.busCode = busCode;
        this.vasMobile = vasMobile;
        this.cpsMobile = cpsMobile;
    }

    public DSPCompany(Long comId, String comName, Long type, String taxCode, String busCode, String vasMobile, String cpsMobile, String custType, String checkDate) throws Exception {
        this.comId = comId;
        this.comName = comName;
        this.type = type;
        this.taxCode = taxCode;
        this.busCode = busCode;
        this.vasMobile = vasMobile;
        this.cpsMobile = cpsMobile;
        this.custType = custType;
        this.checkDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(checkDate);
    }

    public DSPCompany(Long comId, String comName, Long type, String taxCode, String busCode, String vasMobile, String cpsMobile, String custType, String checkDate, String bkCheckDate) throws Exception {
        this.comId = comId;
        this.comName = comName;
        this.type = type;
        this.taxCode = taxCode;
        this.busCode = busCode;
        this.vasMobile = vasMobile;
        this.cpsMobile = cpsMobile;
        this.custType = custType;
        this.checkDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(checkDate);
        this.bkCheckDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(bkCheckDate);
    }

    public DSPCompany(Long comId, String comName) throws Exception {
        this.comId = comId;
        this.comName = comName;
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getBusCode() {
        return busCode;
    }

    public void setBusCode(String busCode) {
        this.busCode = busCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVasMobile() {
        return vasMobile;
    }

    public void setVasMobile(String vasMobile) {
        if (vasMobile != null) {
            this.vasMobile = Utils.fixIsdnWithout0and84(vasMobile.replaceAll("[ ()]", ""));
        }
    }

    public String getRepPosition() {
        return repPosition;
    }

    public void setRepPosition(String repPosition) {
        this.repPosition = repPosition;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getRepPhone() {
        return repPhone;
    }

    public void setRepPhone(String repPhone) {
        this.repPhone = repPhone;
    }

    public String getRepMobile() {
        return repMobile;
    }

    public void setRepMobile(String repMobile) {
        if (repMobile != null) {
            this.repMobile = Utils.fixIsdnWithout0and84(repMobile.replaceAll("[ ()]", ""));
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Date getUpdatedKey() {
        return updatedKey;
    }

    public void setUpdatedKey(Date updatedKey) {
        this.updatedKey = updatedKey;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShowAddress() {
        return showAddress;
    }

    public void setShowAddress(String showAddress) {
        this.showAddress = showAddress;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCpsMobile() {
        return cpsMobile;
    }

    public void setCpsMobile(String cpsMobile) {
        this.cpsMobile = cpsMobile;
        if (cpsMobile != null) {
            this.cpsMobile = Utils.fixIsdnWithout0and84(cpsMobile.replaceAll("[ ()]", ""));
        }
    }

    public String getSerialPrefix() {
        return serialPrefix;
    }

    public void setSerialPrefix(String serialPrefix) {
        this.serialPrefix = serialPrefix;
    }

    public String getApiPublicKey() {
        return apiPublicKey;
    }

    public void setApiPublicKey(String apiPublicKey) {
        this.apiPublicKey = apiPublicKey;
    }

    public Long getApiUserId() {
        return apiUserId;
    }

    public void setApiUserId(Long apiUserId) {
        this.apiUserId = apiUserId;
    }

    public String getApiUserName() {
        return apiUserName;
    }

    public void setApiUserName(String apiUserName) {
        this.apiUserName = apiUserName;
    }

    public Date getApiUpdatedKey() {
        return apiUpdatedKey;
    }

    public void setApiUpdatedKey(Date apiUpdatedKey) {
        this.apiUpdatedKey = apiUpdatedKey;
    }

    public String getBhttCode() {
        return bhttCode;
    }

    public void setBhttCode(String bhttCode) {
        this.bhttCode = bhttCode;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Date getBkCheckDate() {
        return bkCheckDate;
    }

    public void setBkCheckDate(Date bkCheckDate) {
        this.bkCheckDate = bkCheckDate;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }
}
