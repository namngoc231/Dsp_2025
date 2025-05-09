package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 *
 * @author TRIEUNV
 */
public class DSPServicePrice implements Serializable  {

    private Long priceId;
    private Long tabId;
    private Long ServiceId;
    private String name;
    private Long capMin;
    private Long capMax;
    private Long price;
    private Long activeDay;
    private String strCapMin;
    private String strCapMax;
    private String type;
    private Long capMinBlock;
    private Long capMaxBlock;

    //huyNQ 30/12/2021
    private int index;



    public DSPServicePrice(Long priceId, Long tabId, String name, Long capMin, Long capMax, Long price, Long activeDay, Long block, String strCapMin, String strCapMax, String strBlock) {
        this.priceId = priceId;
        this.tabId = tabId;
        this.name = name;
        this.capMin = capMin;
        this.capMax = capMax;
        this.price = price;
        this.activeDay = activeDay;
        this.strCapMin = strCapMin;
        this.strCapMax = strCapMax;
    }

    public DSPServicePrice(Long priceId, Long tabId, String name, Long capMin, Long capMax, Long price, Long activeDay, String strCapMin, String strCapMax, String type, Long capMinBlock, Long capMaxBlock) {
        this.priceId = priceId;
        this.tabId = tabId;
        this.name = name;
        this.capMin = capMin;
        this.capMax = capMax;
        this.price = price;
        this.activeDay = activeDay;
        this.strCapMin = strCapMin;
        this.strCapMax = strCapMax;
        this.type = type;
        this.capMinBlock = capMinBlock;
        this.capMaxBlock = capMaxBlock;
    }

    public DSPServicePrice() {

    }


    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Long getServiceId() {
        return ServiceId;
    }

    public void setServiceId(Long serviceId) {
        ServiceId = serviceId;
    }

    public Long getTabId() {
        return tabId;
    }

    public void setTabId(Long tabId) {
        this.tabId = tabId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(Long activeDay) {
        this.activeDay = activeDay;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCapMinBlock() {
        return capMinBlock;
    }

    public void setCapMinBlock(Long capMinBlock) {
        this.capMinBlock = capMinBlock;
    }

    public Long getCapMaxBlock() {
        return capMaxBlock;
    }

    public void setCapMaxBlock(Long capMaxBlock) {
        this.capMaxBlock = capMaxBlock;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
