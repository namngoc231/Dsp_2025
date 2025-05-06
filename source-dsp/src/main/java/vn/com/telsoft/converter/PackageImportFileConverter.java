
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
@FacesConverter("PackageImportFileConverter")
public class PackageImportFileConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            String[] data = value.split("\\|", -1);
            if (data.length == 10) {
                if (data[0] == null || "null".equals(data[0]) || data[0].isEmpty()) return null;
                try {
                    if (data[1] == null || "null".equals(data[1]) || data[1].isEmpty()) {
                        return new DSPCompany(Long.parseLong(data[0]), data[1]);
                    }
                    return new DSPCompany(Long.parseLong(data[0]), data[1]);
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
