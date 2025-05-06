
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.converter;

import com.faplib.util.DateUtil;
import com.faplib.util.StringUtil;
import vn.com.telsoft.entity.DSPCompany;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author TungLM
 */
@FacesConverter("CompanyItemConverter")
public class CompanyItemConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            String[] data = value.split("\\|", -1);
            if (data.length == 10) {
                if (data[0] == null || "null".equals(data[0]) || "".equals(data[0])) return null;
                try {
                    if (data[9] == null || "null".equals(data[9]) || "".equals(data[9])) {
                        return new DSPCompany(Long.parseLong(data[0]), data[1], Long.parseLong(data[2]), data[3], data[4], data[5], data[6], data[7], data[8]);
                    }
                    return new DSPCompany(Long.parseLong(data[0]), data[1], Long.parseLong(data[2]), data[3], data[4], data[5], data[6], data[7], data[8], data[9]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null) {
            return ((DSPCompany) value).getComId()
                    + "|" + StringUtil.fix(((DSPCompany) value).getComName())
                    + "|" + ((DSPCompany) value).getType()
                    + "|" + StringUtil.fix(((DSPCompany) value).getTaxCode())
                    + "|" + StringUtil.fix(((DSPCompany) value).getBusCode())
                    + "|" + StringUtil.fix(((DSPCompany) value).getVasMobile())
                    + "|" + StringUtil.fix(((DSPCompany) value).getCpsMobile())
                    + "|" + StringUtil.fix(((DSPCompany) value).getCustType())
                    + "|" + StringUtil.fix(DateUtil.getDateStr(((DSPCompany) value).getCheckDate(), "dd/MM/yyyy hh:mm:ss"))
                    + "|" + StringUtil.fix(DateUtil.getDateStr(((DSPCompany) value).getBkCheckDate(), "dd/MM/yyyy hh:mm:ss"));
        } else {
            return null;
        }
    }
}
