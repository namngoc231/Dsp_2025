package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author TRIEUNV
 */
public class DSPService implements Serializable {

    private Long serviceId;
    private String serviceName;
    private String status;
    private String serviceCode;
    private String description;
    private String path;

    public DSPService(Long serviceId, String serviceName, String status, String serviceCode, String description, String path) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.status = status;
        this.serviceCode = serviceCode;
        this.description = description;
        this.path = path;
    }

    public DSPService() {

    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
