package vn.com.telsoft.entity;

import java.io.Serializable;

public class DspNotifConfig implements Serializable {
    private Long id;
    private String srvName;
    private String packageCode;
    private String qtaStatus;
    private Long qtaValue;
    private String destApi;
    private String proxyApi;
    private String status;

    public DspNotifConfig() {
    }

    public DspNotifConfig(Long id, String srvName, String packageCode, String qtaStatus, Long qtaValue, String destApi, String proxyApi, String status) {
        this.id = id;
        this.srvName = srvName;
        this.packageCode = packageCode;
        this.qtaStatus = qtaStatus;
        this.qtaValue = qtaValue;
        this.destApi = destApi;
        this.proxyApi = proxyApi;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSrvName() {
        return srvName;
    }

    public void setSrvName(String srvName) {
        this.srvName = srvName;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getQtaStatus() {
        return qtaStatus;
    }

    public void setQtaStatus(String qtaStatus) {
        this.qtaStatus = qtaStatus;
    }

    public Long getQtaValue() {
        return qtaValue;
    }

    public void setQtaValue(Long qtaValue) {
        this.qtaValue = qtaValue;
    }

    public String getDestApi() {
        return destApi;
    }

    public void setDestApi(String destApi) {
        this.destApi = destApi;
    }

    public String getProxyApi() {
        return proxyApi;
    }

    public void setProxyApi(String proxyApi) {
        this.proxyApi = proxyApi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
