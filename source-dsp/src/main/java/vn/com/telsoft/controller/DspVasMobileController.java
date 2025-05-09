package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DspComVasMobile;
import vn.com.telsoft.model.DspVasMobileModel;
import vn.com.telsoft.util.Utils;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class DspVasMobileController extends TSFuncTemplate implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_DSPCOMPANY";

    private List<DspComVasMobile> lstVasMobile;
    private List<DspComVasMobile> lstVasMobileFilterred;
    private DspComVasMobile[] mselectedVasMobile;
    private DspComVasMobile vasMobile;

    private DspVasMobileModel dspVasMobileModel;

    //info company
    private long comId = 0;
    private DSPCompany dspCompany;
//    private long parentId;

    //info userLogin
    private long userId;
    private long comIdLogin;

    public DspVasMobileController() throws Exception {
        vasMobile = new DspComVasMobile();
        dspVasMobileModel = new DspVasMobileModel();
        lstVasMobile = new ArrayList<>();
        userId = AdminUser.getUserLogged().getUserId();
        comIdLogin = dspVasMobileModel.getComIdLogin(userId);
        init();
        dspCompany = dspVasMobileModel.getInfoCompany(comId);
    }

    public void init() throws Exception {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.get("comId") != null && !params.get("comId").equals("")) {
            String paramComId = params.get("comId");
            comId = Long.parseLong(paramComId);
            lstVasMobile = dspVasMobileModel.getListById(comId, comIdLogin);
        }
    }

    public void changeStateAdd() throws Exception {
        vasMobile = new DspComVasMobile();
        vasMobile.setComId(comId);
    }

    @Override
    public void handleOK() throws Exception {
        if (validInput()) {
            if (dspVasMobileModel.checkExistVasMobile(vasMobile)) {
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "duplicateVasMobile"));
            } else {
                dspVasMobileModel.insert(vasMobile);
                lstVasMobile = dspVasMobileModel.getListById(comId, comIdLogin);
                PrimeFaces.current().executeScript("PF('dialog_add_mobile').hide();");
                PrimeFaces.current().executeScript("PF('table_vasmobile').clearFilters();");
                ClientMessage.logAdd();
            }
        }
    }

    public boolean validInput() {
        if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(vasMobile.getVasMobile()))) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "valiPhoneNumber"));
            return false;
        }
        return true;
    }

    public void handleDelete(DspComVasMobile obj) throws Exception {
        vasMobile = obj;
        dspVasMobileModel.delete(vasMobile);
        lstVasMobile = dspVasMobileModel.getListById(comId, comIdLogin);
        ClientMessage.logDelete();
    }

    public void backToCompany() throws IOException {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        url = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/module/dspCompany";
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    @Override
    public void handleDelete() throws Exception {
    }

    //get, set
    public List<DspComVasMobile> getLstVasMobile() {
        return lstVasMobile;
    }

    public void setLstVasMobile(List<DspComVasMobile> lstVasMobile) {
        this.lstVasMobile = lstVasMobile;
    }

    public List<DspComVasMobile> getLstVasMobileFilterred() {
        return lstVasMobileFilterred;
    }

    public void setLstVasMobileFilterred(List<DspComVasMobile> lstVasMobileFilterred) {
        this.lstVasMobileFilterred = lstVasMobileFilterred;
    }

    public DspComVasMobile[] getMselectedVasMobile() {
        return mselectedVasMobile;
    }

    public void setMselectedVasMobile(DspComVasMobile[] mselectedVasMobile) {
        this.mselectedVasMobile = mselectedVasMobile;
    }

    public DspComVasMobile getVasMobile() {
        return vasMobile;
    }

    public void setVasMobile(DspComVasMobile vasMobile) {
        this.vasMobile = vasMobile;
    }

    public DspVasMobileModel getDspVasMobileModel() {
        return dspVasMobileModel;
    }

    public void setDspVasMobileModel(DspVasMobileModel dspVasMobileModel) {
        this.dspVasMobileModel = dspVasMobileModel;
    }

    public long getComId() {
        return comId;
    }

    public void setComId(long comId) {
        this.comId = comId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getComIdLogin() {
        return comIdLogin;
    }

    public void setComIdLogin(long comIdLogin) {
        this.comIdLogin = comIdLogin;
    }

    public DSPCompany getDspCompany() {
        return dspCompany;
    }

    public void setDspCompany(DSPCompany dspCompany) {
        this.dspCompany = dspCompany;
    }
}
