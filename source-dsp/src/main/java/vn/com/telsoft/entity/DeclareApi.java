package vn.com.telsoft.entity;

import java.io.Serializable;

public class DeclareApi implements Serializable {
    private Long apiId;
    private String apiName;
    private String apiPath;
    private String description;
    private Long status;

    public DeclareApi() {
    }

    public DeclareApi(Long apiId, String apiName, String apiPath, String description, Long status) {
        this.apiId = apiId;
        this.apiName = apiName;
        this.apiPath = apiPath;
        this.description = description;
        this.status = status;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
