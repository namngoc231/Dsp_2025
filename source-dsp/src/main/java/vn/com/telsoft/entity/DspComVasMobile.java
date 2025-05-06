package vn.com.telsoft.entity;

import vn.com.telsoft.util.Utils;

import java.io.Serializable;

public class DspComVasMobile implements Serializable {
    private Long comId;
    private String vasMobile;

    public DspComVasMobile() {
    }

    public Long getComId() {
        return comId;
    }

    public void setComId(Long comId) {
        this.comId = comId;
    }

    public String getVasMobile() {
        return vasMobile;
    }

    public void setVasMobile(String vasMobile) {
        this.vasMobile = vasMobile;
        if (vasMobile != null) {
            this.vasMobile = Utils.fixIsdnWithout0and84(vasMobile.replaceAll("[ ()]", ""));
        }
    }
}
