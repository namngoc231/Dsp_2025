package vn.com.telsoft.entity;

import java.io.Serializable;

public class DipPackage implements Serializable {
    private Long packageId;
    private Long serviceId;
    private String packageCode;
    private Long status;
    private String description;
    private String provCode;
    private Double initialAmount;
    private Long activeDay;
    private String amountInput;

    public DipPackage() {
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public Double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
    }

    public String getAmountInput() {
        return amountInput;
    }

    public void setAmountInput(String amountInput) {
        this.amountInput = amountInput;
    }
}
