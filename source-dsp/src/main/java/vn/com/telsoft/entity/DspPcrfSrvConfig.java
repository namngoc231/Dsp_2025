package vn.com.telsoft.entity;

import java.io.Serializable;

public class DspPcrfSrvConfig implements Serializable {
    private Long id;
    private String sysType;
    private String serviceType;
    private String serviceName;
    private String serviceNameUnique;
    private String pinPrefix;
    private String addOn;
    private String description;
    private String status;

    public DspPcrfSrvConfig() {
    }

    public DspPcrfSrvConfig(Long id, String sysType, String serviceType, String serviceName, String serviceNameUnique, String pinPrefix, String addOn, String description, String status) {
        this.id = id;
        this.sysType = sysType;
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.serviceNameUnique = serviceNameUnique;
        this.pinPrefix = pinPrefix;
        this.addOn = addOn;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceNameUnique() {
        return serviceNameUnique;
    }

    public void setServiceNameUnique(String serviceNameUnique) {
        this.serviceNameUnique = serviceNameUnique;
    }

    public String getPinPrefix() {
        return pinPrefix;
    }

    public void setPinPrefix(String pinPrefix) {
        this.pinPrefix = pinPrefix;
    }

    public String getAddOn() {
        return addOn;
    }

    public void setAddOn(String addOn) {
        this.addOn = addOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
