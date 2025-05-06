/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.admin.gui.entity.UserDTL;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.StringUtil;
import org.apache.commons.lang.SerializationUtils;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.model.DSPCompanyUpdatedKeyModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author HoangNH
 */
@Named
@ViewScoped
public class DspCompanyChangePasswordController extends TSFuncTemplate implements Serializable {
    static final String RESOURCE_BUNDLE = "PP_DSPCOMPANY";
    private List<DSPCompany> mlistCompany;
    private DSPCompany mtmpCompany;
    private int userOrApi;
    private UserDTL mUser;
    private String password, rePassword;
    private DSPCompanyUpdatedKeyModel mmodel = new DSPCompanyUpdatedKeyModel();
    private boolean disableBtn = true;

    public DspCompanyChangePasswordController() throws Exception {
        mUser = new UserDTL();
        long user_id = AdminUser.getUserLogged().getUserId();
        this.mlistCompany = this.mmodel.getCompanyUser(user_id);
        this.mtmpCompany = (DSPCompany) SerializationUtils.clone(this.mlistCompany.get(0));
        if (mtmpCompany.getApiUserId() != 0)
            disableBtn = false;
    }

    public void changeStateEdit(DSPCompany app) throws Exception {
        super.changeStateEdit();
        this.selectedIndex = this.mlistCompany.indexOf(app);
        this.mtmpCompany = (DSPCompany) SerializationUtils.clone(app);
    }

    public void handleOK() throws Exception {
        if (check_Password(password, rePassword)) {
            String passwordChange = StringUtil.encryptPassword(password);
            mUser.setPassword(passwordChange);
            mUser.setModifiedPassword(GregorianCalendar.getInstance().getTime());
            if (userOrApi == 1) {
                mUser.setUserId(mtmpCompany.getUserId());
                mmodel.editPasswordUser(mUser);
            }
            if (userOrApi == 0) {
                mUser.setUserId(mtmpCompany.getApiUserId());
                mmodel.editPasswordUser(mUser);
            }
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString("PP_DSPCOMPANY", "mUpdate"));
            handleCancel();
            password = "";
        }
    }

    private boolean check_Password(String password, String rePassword) throws Exception {
        try {
            if (!password.equals(rePassword)) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "duplicatePassword"));
                return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
        }
        return false;
    }

    public void onChangeType() {
        Date now = new Date();
        if (userOrApi == 0) {
            mtmpCompany.setUpdatedKey(now);
        }
        if (userOrApi == 1) {
            mtmpCompany.setApiUpdatedKey(now);
        }
    }

    public String getMyFormattedDate(Date myDate) {
        return new SimpleDateFormat("dd/MM/yyyy").format(myDate);
    }

    public void handleDelete() throws Exception {
    }

    public List<DSPCompany> getMlistCompany() {
        return mlistCompany;
    }

    public void setMlistCompany(List<DSPCompany> mlistCompany) {
        this.mlistCompany = mlistCompany;
    }

    public DSPCompany getMtmpCompany() {
        return mtmpCompany;
    }

    public void setMtmpCompany(DSPCompany mtmpCompany) {
        this.mtmpCompany = mtmpCompany;
    }

    public int getUserOrApi() {
        return userOrApi;
    }

    public void setUserOrApi(int userOrApi) {
        this.userOrApi = userOrApi;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDisableBtn() {
        return disableBtn;
    }

    public void setDisableBtn(boolean disableBtn) {
        this.disableBtn = disableBtn;
    }
}
