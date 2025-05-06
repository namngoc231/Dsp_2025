package vn.com.telsoft.ws;

import java.util.List;

/**
 * @author TUNGLM
 */
public class DspApiRequest {

    private String transactionId;
    private String msisdn;
    private String serial;
    private String comment;
    private String datacode;
    private String webUsername;
    private List<String> services;


    public DspApiRequest() {
    }

    public DspApiRequest(String msisdn, String serial, String comment, String datacode) {
        this.msisdn = msisdn;
        this.serial = serial;
        this.comment = comment;
        this.datacode = datacode;
    }

    public DspApiRequest(String msisdn, String serial, String comment, String datacode, String webUsername) {
        this.msisdn = msisdn;
        this.serial = serial;
        this.comment = comment;
        this.datacode = datacode;
        this.webUsername = webUsername;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWebUsername() {
        return webUsername;
    }

    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDatacode() {
        return datacode;
    }

    public void setDatacode(String datacode) {
        this.datacode = datacode;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}
