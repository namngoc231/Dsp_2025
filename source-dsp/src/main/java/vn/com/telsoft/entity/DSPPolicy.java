package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 *
 * @author TRIEUNV
 */
public class DSPPolicy implements Serializable {

    private String description;
    private String policyName;

    public DSPPolicy(String description, String policyName) {
        this.description = description;
        this.policyName = policyName;
    }

    public DSPPolicy() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
}
