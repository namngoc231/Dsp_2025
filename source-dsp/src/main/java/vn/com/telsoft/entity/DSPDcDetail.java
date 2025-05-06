package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

public class DSPDcDetail implements Serializable {
    private Long transactionId;
    private String cardName;
    private Long amount;
    private Long totalCost;
    private String status;
    private Date requestTime;
    private Long priceId;

    private String cardTypeName;
    private Long cardTypePrice;

    public DSPDcDetail() {
    }

    public DSPDcDetail(Long transactionId, String cardName, Long amount, Long totalCost, String status, Date requestTime, Long priceId, String cardTypeName, Long cardTypePrice) {
        this.transactionId = transactionId;
        this.cardName = cardName;
        this.amount = amount;
        this.totalCost = totalCost;
        this.status = status;
        this.requestTime = requestTime;
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
