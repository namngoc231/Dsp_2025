package vn.com.telsoft.converter;

import vn.com.telsoft.controller.OrderPolicySponsorController;
import vn.com.telsoft.controller.ServiceDeclareSponsorController;
import vn.com.telsoft.entity.DSPCompany;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@FacesConverter(value = "companyTabOrderPolicyConverter", managed = true)
public class CompanyTabOrderPolicyConverter implements Converter {

    @Inject
    private OrderPolicySponsorController orderPolicySponsorController;

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                for (DSPCompany dsp : orderPolicySponsorController.getListCompanyAutoCom()) {
                    if (dsp.getComId().equals(Long.parseLong(value))) {
//                        List<DSPCompany> lst = orderPolicySponsorController.getListCompanyAutoComSelected();
//                        if (lst != null && lst.size() > 0) {
//                            for (DSPCompany dspCom : lst) {
//                                if(dspCom.getComId() == dsp.getComId()){
//                                    return null;
//                                }
//                            }
//                        }
                        return dsp;
                    }
                }

            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if (object != null) {
            return String.valueOf(((DSPCompany) object).getComId());
        } else {
            return null;
        }
    }
}
