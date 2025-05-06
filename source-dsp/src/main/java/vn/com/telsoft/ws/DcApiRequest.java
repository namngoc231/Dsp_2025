package vn.com.telsoft.ws;

/**
 * @author TUNGLM
 */
public class DcApiRequest {

    private String transactionId;
    private String orderId;
    private Long extendDays;

    public DcApiRequest() {
    }

    public DcApiRequest(String transactionId, String orderId) {
        this.transactionId = transactionId;
        this.orderId = orderId;
    }

    public DcApiRequest(String transactionId, String orderId, Long extendDays) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.extendDays = extendDays;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getExtendDays() {
        return extendDays;
    }

    public void setExtendDays(Long extendDays) {
        this.extendDays = extendDays;
    }
}
