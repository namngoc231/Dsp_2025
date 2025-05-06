package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DSPDcRequest implements Serializable {
    private Long transactionId;
    private String cardName;
    private Long amount;
    private Long totalCost;
    private String status;
    private Date requestTime;
    private Date processTime;
    private Long retries;
    private Long priceId;
    private String cardTypeName;
    private Long cardTypePrice;

    public DSPDcRequest() {
    }

    public DSPDcRequest(Long transactionId, String cardName, Long amount, Long totalCost, String status, Date requestTime, Date processTime, Long retries, Long priceId, String cardTypeName, Long cardTypePrice) {
        this.transactionId = transactionId;
        this.cardName = cardName;
        this.amount = amount;
        this.totalCost = totalCost;
        this.status = status;
        this.requestTime = requestTime;
        this.processTime = processTime;
        this.retries = retries;
        this.priceId = priceId;
        this.cardTypeName = cardTypeName;
        this.cardTypePrice = cardTypePrice;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Long totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public Long getRetries() {
        return retries;
    }

    public void setRetries(Long retries) {
        this.retries = retries;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public Long getCardTypePrice() {
        return cardTypePrice;
    }

    public void setCardTypePrice(Long cardTypePrice) {
        this.cardTypePrice = cardTypePrice;
    }
}
