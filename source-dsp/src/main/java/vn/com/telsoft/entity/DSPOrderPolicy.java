package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 * @author TRIEUNV
 */
public class DSPOrderPolicy implements Serializable {

    private Long id;
    private Long serviceId;
    private Long tabId;
    private Long minValue;
    private Long maxValue;
    private Long activeDays;
    private Long promPct;
    private String type;

    public DSPOrderPolicy(Long id, Long tabId, Long minValue, Long maxValue, Long activeDays, Long promPct) {
        this.id = id;
        this.tabId = tabId;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.activeDays = activeDays;
        this.promPct = promPct;
    }

    public DSPOrderPolicy() {

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

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    public Long getPromPct() {
        return promPct;
    }

    public void setPromPct(Long promPct) {
        this.promPct = promPct;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
