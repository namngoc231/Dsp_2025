package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author TRIEUNV
 */
public class DSPServicePriceBak implements Serializable {

    private Long priceId;
    private Long serviceId;
    private String name;
    private String description;
    private String status;
    private Long capMin;
    private Long capMax;
    private Long price;
    private String currency;
    private Date startTime;
    private Date endTime;
    private Long activeDay;
    private Long block;
    private String strCapMin;
    private String strCapMax;
    private String strBlock;


    public DSPServicePriceBak(Long priceId, Long serviceId, String name, String description, String status, Long capMin, Long capMax, Long price, String currency, Date startTime, Date endTime, Long activeDay, Long block) {
        this.priceId = priceId;
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.capMin = capMin;
        this.capMax = capMax;
        this.price = price;
        this.currency = currency;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activeDay = activeDay;
        this.block = block;
    }

    public DSPServicePriceBak() {

    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getCapMin() {
        return capMin;
    }

    public void setCapMin(Long capMin) {
        this.capMin = capMin;
    }

    public Long getCapMax() {
        return capMax;
    }

    public void setCapMax(Long capMax) {
        this.capMax = capMax;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
    }

    public Long getBlock() {
        return block;
    }

    public void setBlock(Long block) {
        this.block = block;
    }

    public String getStrCapMin() {
        return strCapMin;
    }

    public void setStrCapMin(String strCapMin) {
        this.strCapMin = strCapMin;
    }

    public String getStrCapMax() {
        return strCapMax;
    }

    public void setStrCapMax(String strCapMax) {
        this.strCapMax = strCapMax;
    }

    public String getStrBlock() {
        return strBlock;
    }

    public void setStrBlock(String strBlock) {
        this.strBlock = strBlock;
    }
}
