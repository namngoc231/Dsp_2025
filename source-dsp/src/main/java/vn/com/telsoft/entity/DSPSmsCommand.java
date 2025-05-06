package vn.com.telsoft.entity;

import java.io.Serializable;

/**
 * @author TUNGLM
 */
public class DSPSmsCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cmdId;
    private String cmdCode;
    private String cmdType;
    private String cmdMsgContent;
    private Long cmdParamCount;
    private String description;
    private String status;

    public DSPSmsCommand() {
    }

    public Long getCmdId() {
        return cmdId;
    }

    public void setCmdId(Long cmdId) {
        this.cmdId = cmdId;
    }

    public String getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(String cmdCode) {
        this.cmdCode = cmdCode;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public String getCmdMsgContent() {
        return cmdMsgContent;
    }

    public void setCmdMsgContent(String cmdMsgContent) {
        this.cmdMsgContent = cmdMsgContent;
    }

    public Long getCmdParamCount() {
        return cmdParamCount;
    }

    public void setCmdParamCount(Long cmdParamCount) {
        this.cmdParamCount = cmdParamCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
