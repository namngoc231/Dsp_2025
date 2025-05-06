/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.apache.commons.lang.SerializationUtils;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.model.DSPCompanyUpdatedKeyModel;
import vn.com.telsoft.util.PGPUtils;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author HoangNH
 */
@Named
@ViewScoped
public class DspCompanyUpdatedKeyController extends TSFuncTemplate implements Serializable {
    static final String RESOURCE_BUNDLE = "PP_DSPCOMPANY";
    private List<DSPCompany> mlistCompany;
    private DSPCompany mtmpCompany;
    //    private List<DSPCompany> mselectedSMS;
    private int publicKeyOrApi;
    private DSPCompanyUpdatedKeyModel mmodel = new DSPCompanyUpdatedKeyModel();

    public DspCompanyUpdatedKeyController() throws Exception {
        long user_id = AdminUser.getUserLogged().getUserId();
        this.mlistCompany = this.mmodel.getCompanyUser(user_id);
        Date now = new Date();
        this.mtmpCompany = (DSPCompany) SerializationUtils.clone(this.mlistCompany.get(0));
        this.mtmpCompany.setPublicKey("");
        this.mtmpCompany.setApiPublicKey("");
        this.mtmpCompany.setUpdatedKey(now);
        this.mtmpCompany.setApiUpdatedKey(now);
    }

    public void changeStateEdit(DSPCompany app) throws Exception {
        super.changeStateEdit();
        this.selectedIndex = this.mlistCompany.indexOf(app);
        this.mtmpCompany = (DSPCompany) SerializationUtils.clone(app);
    }

    public void handleOK() throws Exception {
        if (publicKeyOrApi == 0) {
            if (check_PublicKey(mtmpCompany.getPublicKey().trim(), "Public key")) {
                this.mmodel.edit(this.mtmpCompany, 0);
                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString("PP_DSPCOMPANY", "mUpdate"));
                handleCancel();
                mtmpCompany.setPublicKey("");
            }
        }
        if (publicKeyOrApi == 1) {
            if (check_PublicKey(mtmpCompany.getApiPublicKey().trim(), "API Public key")) {

                this.mmodel.edit(this.mtmpCompany, 1);
                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString("PP_DSPCOMPANY", "mUpdate"));
                handleCancel();
                mtmpCompany.setApiPublicKey("");
            }
        }
        return;
    }

    private boolean check_PublicKey(String pubKey, String namePubKey) throws Exception {
        try {
            if (!pubKey.startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----") || !pubKey.endsWith("-----END PGP PUBLIC KEY BLOCK-----")) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, namePubKey + ": " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_PUBLIC_KEY_VALID"));
                return false;
            }
            byte[] publicKey = pubKey.getBytes();
            ByteArrayInputStream bPublicKey = new ByteArrayInputStream(publicKey);
            String PUBLIC_VALID = SystemConfig.getConfig("PUBLIC_KEY_VALID_DAY");
            if (PUBLIC_VALID == null || PUBLIC_VALID.isEmpty())
                PUBLIC_VALID = "90";
            KeyFingerPrintCalculator keyFingerPrintCalculator = new BcKeyFingerprintCalculator();
            PGPPublicKey pgpPublicKey = PGPUtils.readPublicKey(bPublicKey, keyFingerPrintCalculator);
            long validSeconds = pgpPublicKey.getValidSeconds();
            long createTime = pgpPublicKey.getCreationTime().getTime();
            if (validSeconds == 0) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, namePubKey + ": " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_PUBLIC_LIMIT"));
                return false;
            }
            long currentTime = System.currentTimeMillis();
            long expTime = createTime + validSeconds * 1000;
            long validTime = (Long.parseLong(PUBLIC_VALID) * 86400 * 1000) + currentTime;
            // public key phải có hiệu lực tối thiểu là 90 ngày
            if (expTime <= validTime) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, namePubKey + ": " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_PUBLIC_KEY_DAY"));
                return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, namePubKey + ": " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_PUBLIC_KEY"));
        }
        return false;
    }

    public void onChangeType() {
        Date now = new Date();
        if (publicKeyOrApi == 0) {
            mtmpCompany.setUpdatedKey(now);
        }
        if (publicKeyOrApi == 1) {
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

    public int getPublicKeyOrApi() {
        return publicKeyOrApi;
    }

    public void setPublicKeyOrApi(int publicKeyOrApi) {
        this.publicKeyOrApi = publicKeyOrApi;
    }
}
