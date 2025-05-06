package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 *
 * @author TRIEUNV
 */
public class DSPOrderPolicyBak implements Serializable {

    private Long id;
    private Long serviceId;
    private Long minValue;
    private Long maxValue;
    private Long activeDays;
    private String description;
    private String type;

    public DSPOrderPolicyBak(Long id, Long serviceId, Long minValue, Long maxValue, Long activeDays, String description, String type) {
        this.id = id;
        this.serviceId = serviceId;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.activeDays = activeDays;
        this.description = description;
        this.type = type;
    }

    public DSPOrderPolicyBak() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public Long getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(Long activeDays) {
        this.activeDays = activeDays;
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
