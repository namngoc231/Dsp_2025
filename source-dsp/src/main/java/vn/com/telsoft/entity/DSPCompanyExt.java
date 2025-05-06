package vn.com.telsoft.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TRIEUNV
 */
public class DSPCompanyExt implements Serializable {
    private DSPCompany dspCompany;
    private String typeCom;
    private Date appliedDate;

    public DSPCompanyExt() {
    }

    public DSPCompany getDspCompany() {
        return dspCompany;
    }

    public void setDspCompany(DSPCompany dspCompany) {
        this.dspCompany = dspCompany;
    }

    public String getTypeCom() {
        return typeCom;
    }

    public void setTypeCom(String typeCom) {
        this.typeCom = typeCom;
    }

    public Date getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(Date appliedDate) {
        this.appliedDate = appliedDate;
    }
}
