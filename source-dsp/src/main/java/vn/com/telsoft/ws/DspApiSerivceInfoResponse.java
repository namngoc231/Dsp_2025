package vn.com.telsoft.ws;

import vn.com.telsoft.entity.DSPSubService;

import java.util.List;


public class DspApiSerivceInfoResponse {

    private String code;
    private String description;
    private String transId;
    private List<DSPSubService> listServices;


    public DspApiSerivceInfoResponse() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public List<DSPSubService> getListServices() {
        return listServices;
    }

    public void setListServices(List<DSPSubService> listServices) {
        this.listServices = listServices;
    }
}
