package vn.com.telsoft.ws;

import vn.com.telsoft.entity.DspDipSubServiceInfo;

import java.util.List;

public class DspDipSubApiResponse {
    private String transId;
    private String code;
    private String description;
    private List<DspDipSubServiceInfo> listServices;

    public DspDipSubApiResponse(){

    }

    public DspDipSubApiResponse(String transId, String code, String description, List<DspDipSubServiceInfo> listServices) {
        this.transId = transId;
        this.code = code;
        this.description = description;
        this.listServices = listServices;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
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

    public List<DspDipSubServiceInfo> getListServices() {
        return listServices;
    }

    public void setListServices(List<DspDipSubServiceInfo> listServices) {
        this.listServices = listServices;
    }
}
