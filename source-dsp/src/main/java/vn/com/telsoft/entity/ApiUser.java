package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 *
 * @author TRIEUNV
 */
public class ApiUser implements Serializable {

    private Long userId;
    private Long apiId;

    public ApiUser(Long userId, Long apiId) {
        this.userId = userId;
        this.apiId = apiId;
    }

    public ApiUser() {

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }
}
