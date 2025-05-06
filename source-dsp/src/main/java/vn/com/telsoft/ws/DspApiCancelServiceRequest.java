package vn.com.telsoft.ws;

public class DspApiCancelServiceRequest {

    private String isdn;
    private String serviceType;
    private String reason;


    public DspApiCancelServiceRequest() {
    }

    public DspApiCancelServiceRequest(String isdn, String serviceType, String reason) {
        this.isdn = isdn;
        this.serviceType = serviceType;
        this.reason = reason;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
