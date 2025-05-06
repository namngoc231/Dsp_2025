package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 *
 * @author TRIEUNV
 */
public class DSPServicePolicy implements Serializable {

    private Long serviceId;
    private String policyName;
    private String policyValue;
    private String status;
    private String description;
    private String type;

    public DSPServicePolicy(Long serviceId, String policyName, String policyValue, String status, String description, String type) {
        this.serviceId = serviceId;
        this.policyName = policyName;
        this.policyValue = policyValue;
        this.status = status;
        this.description = description;
        this.type = type;
    }

    public DSPServicePolicy() {

    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyValue() {
        return policyValue;
    }

    public void setPolicyValue(String policyValue) {
        this.policyValue = policyValue;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
