/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.admin.gui.entity.UserDTL;
import com.faplib.lib.config.Constant;
import com.faplib.lib.util.PasswordUtil;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.FileUtil;
import com.faplib.util.StringUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.model.DSPCompanyModel;
import vn.com.telsoft.util.PGPUtils;
import vn.com.telsoft.util.Utils;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static com.faplib.lib.admin.security.PolicyProcessor.getPolicy;

/**
 * @author HoangNH
 */
@Named
@ViewScoped
public class DspCompanyController extends TSFuncTemplate implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_DSPCOMPANY";
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    private String password;
    private String passwordAPI;
    private List<DSPCompany> mListCompanyTree;
    private List<DSPCompany> mListCompany;
    private DSPCompany mcompanyTree;
    private DSPCompany mcompany;
    private List<DSPCompany> mFilter;
    private DSPCompany selectedDN;
    private String dnSearch;
    private UserDTL userDTL;
    private boolean renderBtn = false;
    private DSPCompany mcompanySearch;
    private List<DSPCompany> mFilterCompany;
    private List<UserDTL> mListUser;
    private UserDTL mUser;
    private UserDTL mUserApi;
    private TreeNode mtreeCompany;
    private DefaultTreeNode mselectedCompanyNode;
    private DSPCompany mSelectedCompanyTree;
    private DSPCompany userCompany;
    private String publicKeyClone;
    private String publicKeyApiClone;
    private boolean disableUserName = false;
    private boolean disableUserNameApi = false;
    private boolean disableAdd = false;
    private boolean isRETAILERorCOMPANY = false;
    private boolean isROOTorChildren = false;
    private boolean dowloadFile = false;
    private boolean checkPolicyPassword;

    //model
    private DSPCompanyModel mmodel;


    public DspCompanyController() throws Exception {
        try {
            this.mListCompanyTree = new ArrayList<>();
            this.mcompanyTree = new DSPCompany();
            this.mmodel = new DSPCompanyModel();
            this.mListCompany = new ArrayList<>();
            this.mListUser = new ArrayList<>();
            this.mUser = new UserDTL();
            this.mUserApi = new UserDTL();
            this.mcompanySearch = new DSPCompany();
            this.mSelectedCompanyTree = new DSPCompany();
            userDTL = AdminUser.getUserLogged();
            userCompany = mmodel.getCompanyUser(userDTL.getUserId());
            this.resetTreeCompany();
            setSelectedToFirstGroupTreeNode();
            onNodeSelect();
            checkPolicyPassword = "1".equalsIgnoreCase(getPolicy(Constant.GP_REQUIRE_STRONG_PASSWORD).trim());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void updateAddress(List<DSPCompany> mListCompany) {
        if (mListCompany != null) {
            mListCompany.forEach(company -> updateAddress(company));
        }
    }

    private static void updateAddress(DSPCompany company) {
        String address = "";
        if (company.getAddress() != null && !company.getAddress().trim().isEmpty())
            address += company.getAddress().trim() + ", ";
        if (company.getWard() != null && !company.getWard().trim().isEmpty())
            address += company.getWard().trim() + ", ";
        if (company.getDistrict() != null && !company.getDistrict().trim().isEmpty())
            address += company.getDistrict().trim() + ", ";
        if (company.getCity() != null && !company.getCity().trim().isEmpty())
            address += company.getCity().trim() + ", ";
        if (company.getProvince() != null && !company.getProvince().trim().isEmpty())
            address += company.getProvince().trim();
        company.setShowAddress(address);
    }

    private static boolean check_PublicKey(String pubKey, String namePubKey) throws Exception {
        try {
            if (!pubKey.startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----") || !pubKey.endsWith("-----END PGP PUBLIC KEY BLOCK-----")) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, namePubKey + ": " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_PUBLIC_KEY_VALID"));
                return false;
            }
            byte[] publicKey = pubKey.getBytes();
            ByteArrayInputStream bPublicKey = new ByteArrayInputStream(publicKey);
            String PUBLIC_VALID = SystemConfig.getConfig("PUBLIC_KEY_VALID_DAY");
            if (PUBLIC_VALID == null || "".equals(PUBLIC_VALID))
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

    private void setSelectedToFirstGroupTreeNode() throws Exception {
        try {
            if (this.mtreeCompany.getChildCount() != 0) {
                this.mtreeCompany.getChildren().get(0).setSelected(true);
                mselectedCompanyNode = (DefaultTreeNode) mtreeCompany.getChildren().get(0);
                mSelectedCompanyTree = (DSPCompany) mselectedCompanyNode.getData();
                this.mcompany = (DSPCompany) this.mselectedCompanyNode.getData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getTreeChildren(TreeNode node) throws Exception {
        List<DSPCompany> listCompany = mmodel.getDSPCompanyTree(((DSPCompany) node.getData()).getComId());
        node.getChildren().clear();
        buildTreeCompany(listCompany, node);
    }

    public void onExpandTree(NodeExpandEvent event) {
        try {
            TreeNode expdNode = event.getTreeNode();
            getTreeChildren(expdNode);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    private void buildTreeCompany(List<DSPCompany> listValue, TreeNode parent) throws Exception {
        try {
            DSPCompany treeValue = (DSPCompany) parent.getData();
            // Add child - group
            for (DSPCompany item : listValue) {
                if (item.getParentId().equals(treeValue.getComId())) {
                    DSPCompany tmpDSPCompany = new DSPCompany(item);
                    TreeNode tmpTN;
                    tmpTN = new DefaultTreeNode(item, parent);

                    if (tmpDSPCompany.getLevel() == 3) {
                        break;
                    }

                    if (tmpDSPCompany.getComId().equals(mSelectedCompanyTree.getComId())) {
                        tmpTN.setSelected(false);
                    }

                    if (item.getLevel() <= 1) {
                        tmpTN.setExpanded(true);
                    }
                    buildTreeCompany(listValue, tmpTN);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetTreeCompany() throws Exception {
        try {
            this.mListCompanyTree = mmodel.getDSPCompanyTree(userCompany.getComId());
            DSPCompany rootTN = new DSPCompany();
            rootTN.setComId(this.mListCompanyTree.get(0).getParentId());
            this.mtreeCompany = new DefaultTreeNode(rootTN, null);
            this.buildTreeCompany(mListCompanyTree, this.mtreeCompany);

            //Set selected to first node
            mtreeCompany.getChildren().get(0).setSelected(true);
            mselectedCompanyNode = (DefaultTreeNode) mtreeCompany.getChildren().get(0);
            mSelectedCompanyTree = (DSPCompany) mselectedCompanyNode.getData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onNodeSelect() throws Exception {
        mSelectedCompanyTree = (DSPCompany) mselectedCompanyNode.getData();
        mListCompany = mmodel.getNodeList(mSelectedCompanyTree.getComId());
//      show address
        updateAddress(mListCompany);
        if (!mSelectedCompanyTree.getComId().equals(userCompany.getComId()) || userCompany.getType().equals(3L)) {
            isVIEW = true;
            disableAdd = true;
        } else {
            isVIEW = false;
            disableAdd = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleOK() throws Exception {
        Long comId = mSelectedCompanyTree.getComId();
        mcompany.setParentId(comId);
        if (validInput()) {
            if (isADD) {
                if (!"".equals(mcompany.getPublicKey()) && mcompany.getPublicKey() != null)
                    mcompany.setUpdatedKey(GregorianCalendar.getInstance().getTime());
                if (!"".equals(mcompany.getApiPublicKey()) && mcompany.getApiPublicKey() != null)
                    mcompany.setApiUpdatedKey(GregorianCalendar.getInstance().getTime());
                if (checkUserSave() && checkUserAPISave()) {
                    createUser();
                    mmodel.add(mcompany, mUser, mUserApi);
                    mListCompany.add(0, mcompany);
                    updateAddress(mListCompany.get(0));
                    if (mcompany.getStatus().equals("0"))
                        mListCompany.remove(mcompany);

                    ClientMessage.logAdd();
//                    String showAM = mUser.getUserName() + "/" + password;
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "USER/PASS", showAM));
                    handleCancel();
                }
                //Message to client
            } else if (isEDIT) {
                String passwordChange;
                if ("0".equals(mcompany.getStatus())) {
                    mUser.setStatus(0L);
                    mUser.setExpireStatus(0L);
                    mUserApi.setStatus(0L);
                    mUserApi.setExpireStatus(0L);
                }
                if (!"".equals(password) && password != null) {
                    mUser.setPassword(password);
                }
                if (!"".equals(passwordAPI) && passwordAPI != null && mcompany.getApiUserId() != null) {
                    mUserApi.setPassword(passwordAPI);
                } else {
                    if (!"".equals(mcompany.getApiUserName()) && mcompany.getApiUserName() != null && mcompany.getApiUserId() == null) {
                        if (checkUserAPISave()) {
                            createUser();
                        } else {
                            return;
                        }
                    } else {
                        mUserApi = null;
                    }
                }
                if (!publicKeyClone.equals(mcompany.getPublicKey()))
                    mcompany.setUpdatedKey(GregorianCalendar.getInstance().getTime());
                if (!publicKeyApiClone.equals(mcompany.getApiPublicKey()))
                    mcompany.setApiUpdatedKey(GregorianCalendar.getInstance().getTime());

                mmodel.edit(mcompany, mUser, mUserApi);
                mListCompany.set(selectedIndex, mcompany);
                updateAddress(mListCompany.get(selectedIndex));
                if (mcompany.getStatus().equals("0"))
                    mListCompany.remove(selectedIndex);

                //Message to client
                ClientMessage.logUpdate();
                handleCancel();
            }
//        resetTreeCompany();
            getTreeChildren(mselectedCompanyNode);
        }
    }

    private boolean validInput() throws Exception {
        if (mcompany.getVasMobile() != null && !"".equals(mcompany.getVasMobile())) {
            if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(mcompany.getVasMobile()))) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "valiPhoneNumber"));
                return false;
            }
            if (!mmodel.checkExistVasMobile(mcompany)) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "dupvasMobile"));
                return false;
            }
        }
        if (mcompany.getRepMobile() != null && !"".equals(mcompany.getRepMobile())) {
            if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(mcompany.getRepMobile()))) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "valiRepPhoneNumber"));
                return false;
            }
        }
        if (mcompany.getCpsMobile() != null && !"".equals(mcompany.getCpsMobile())) {
            if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(mcompany.getCpsMobile()))) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "valiPhoneNumber"));
                return false;
            }
            if (!Utils.checkVasMobile(mcompany.getCpsMobile())) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errCspMobile"));
                return false;
            }
        }
        if (mcompany.getUserName().equalsIgnoreCase(mcompany.getApiUserName())) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_DUPLICATE_USER"));
            return false;
        }
        if (!"".equals(mcompany.getApiUserName()) && mcompany.getApiUserName() != null && ("".equals(mcompany.getApiPublicKey()) || mcompany.getApiPublicKey() == null)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "apiPublicKeyRequired"));
            return false;
        }
        if (!mmodel.checkBHTTCode(mcompany)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_BHTT_CODE"));
            return false;
        }

        if (!"".equals(password) && password != null || !"".equals(passwordAPI) && passwordAPI != null) {
            if (checkPolicyPassword) {
                boolean passUser = PasswordUtil.isValidPassword(password);
                boolean passUserApi = PasswordUtil.isValidPassword(passwordAPI);
                if (!passUser || !passUserApi) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "require_strong_password"));
                    return false;
                }
            }
        }

        if (!"".equals(mcompany.getPublicKey()) && mcompany.getPublicKey() != null && !check_PublicKey(mcompany.getPublicKey().trim(), "Public key"))
            return false;
        return "".equals(mcompany.getApiPublicKey()) || mcompany.getApiPublicKey() == null || check_PublicKey(mcompany.getApiPublicKey().trim(), "API public key");
    }

    private void createUser() throws Exception {
        String userName = this.mcompany.getUserName().toLowerCase();

        String status = this.mcompany.getStatus();
//        password = generateRandomPassword(6, 48, 122);
        String passwordChange = StringUtil.encryptPassword(password);
        if (!"".equals(mcompany.getApiUserName())) {
            String userNameApi = this.mcompany.getApiUserName().toLowerCase();
            String passwordChangeAPI = StringUtil.encryptPassword(passwordAPI);
            if (mUserApi == null) {
                mUserApi = new UserDTL();
                mUserApi.setUserName(userNameApi);
                mUserApi.setPassword(passwordChangeAPI);
                if ("0".equals(status)) {
                    mUserApi.setStatus(0L);
                    mUserApi.setExpireStatus(0L);
                } else {
                    mUserApi.setStatus(1L);
                    mUserApi.setExpireStatus(1L);
                }
            }
        } else
            mUserApi = null;
        if (mUser == null) {
            mUser = new UserDTL();
            mUser.setUserName(userName);
            mUser.setPassword(passwordChange);
            if ("0".equals(status)) {
                mUser.setStatus(0L);
                mUser.setExpireStatus(0L);
            } else {
                mUser.setStatus(1L);
                mUser.setExpireStatus(1L);
            }
        }
    }

    public List<DSPCompany> searchDN() throws Exception {
        mFilterCompany = null;
        Long comId;
        if (this.mselectedCompanyNode != null) {
            comId = ((DSPCompany) this.mselectedCompanyNode.getData()).getComId();
        } else {
            comId = null;
        }
        mcompanySearch.setParentId(comId);
        mListCompany = mmodel.searchCompany(this.mcompanySearch);
        updateAddress(mListCompany);
        return mListCompany;
    }

    private boolean checkUserSave() throws Exception {
        String userName = this.mcompany.getUserName().toLowerCase();
        mUser = new UserDTL();
        mUser.setUserName(userName);
        mUser = mmodel.getAmUser(mUser);
        if (mUser != null) {
//            DSPCompany company = mmodel.getCompanyUser(mUser.getUserId());
//            if (company != null) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_user_fail"));
            return false;
//            } else {
//                return true;
//            }
        }
        return true;
    }

    private boolean checkUserAPISave() throws Exception {
        String userNameAPI = this.mcompany.getApiUserName().toLowerCase();
        if (("").equals(userNameAPI))
            return true;
        mUserApi = new UserDTL();
        mUserApi.setUserName(userNameAPI);
        mUserApi = mmodel.getAmUser(mUserApi);
        if (mUserApi != null) {
//            DSPCompany company = mmodel.getCompanyUser(mUserApi.getUserId());
//            if (company != null) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_userAPI_fail"));
            return false;
//            } else {
//                return true;
        }
//        } else {
        return true;
    }

//}

    public boolean checkUser() throws Exception {
        String userName = this.mcompany.getUserName().toLowerCase();
        mUser = new UserDTL();
        mUser.setUserName(userName);
        mUser = mmodel.getAmUser(mUser);
        if (mUser != null) {
//            DSPCompany company = mmodel.getCompanyUser(mUser.getUserId());
//            if (company != null) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_user_fail"));
            return false;
//            } else {
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_user_success"));
//                return true;
        }
//        } else {
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_user_success"));
        return true;
//        }
    }

    public boolean checkUserAPI() throws Exception {
        String userNameApi = this.mcompany.getApiUserName().toLowerCase();
        if (("").equals(userNameApi)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "checkUserApiRequired"));
            return false;
        }
        mUserApi = new UserDTL();
        mUserApi.setUserName(userNameApi);
        mUserApi = mmodel.getAmUser(mUserApi);
        if (mUserApi != null) {
//            DSPCompany company = mmodel.getCompanyUser(mUserApi.getUserId());
//            if (company != null) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_userAPI_fail"));
            return false;
//            } else {
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_userAPI_success"));
//                return true;
        }
//        } else {
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "AM_userAPI_success"));
        return true;
//        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean getIsDisplayBtnConfirm() throws Exception {
        return this.isADD || this.isEDIT;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        this.mcompany = new DSPCompany();
        this.mUser = new UserDTL();
        this.mUserApi = new UserDTL();
        if (userCompany.getType().equals(0L))
            this.mcompany.setType(1L);
        else if (userCompany.getType().equals(1L))
            this.mcompany.setType(2L);
        else if (userCompany.getType().equals(2L))
            this.mcompany.setType(3L);
        onChangeType();

        renderBtn = true;
        disableUserName = false;
        disableUserNameApi = false;
        isDISABLE = false;
        mFilterCompany = null;
        dowloadFile = false;
    }

    public void onChangeType() {
        if (mcompany.getType().equals(2L) || mcompany.getType().equals(3L))
            isRETAILERorCOMPANY = true;
        else
            isRETAILERorCOMPANY = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void changeStateEdit(DSPCompany com) throws Exception {
        super.changeStateEdit();
        if (com.getPublicKey() != null) {
            publicKeyClone = com.getPublicKey();
        } else
            publicKeyClone = "";
        if (com.getApiPublicKey() != null) {
            publicKeyApiClone = com.getApiPublicKey();
        } else
            publicKeyApiClone = "";
        selectedIndex = mListCompany.indexOf(com);
        mcompany = SerializationUtils.clone(com);
        mUser = new UserDTL();
        mUser.setUserId(com.getUserId());
        mcompany.setUserName(mcompany.getUserName());
        disableUserNameApi = false;
        if (com.getApiUserId() != null) {
            mUserApi = new UserDTL();
            mUserApi.setUserId(com.getApiUserId());
            mcompany.setApiUserName(mcompany.getApiUserName());
            disableUserNameApi = true;
        }
        onChangeType();
        disableUserName = true;
        renderBtn = true;
        isDISABLE = false;
        mFilterCompany = null;
        if (mcompany.getFilePath() == null) {
            dowloadFile = false;
        } else {
            dowloadFile = true;
        }
    }

    public void changeStateView(DSPCompany com) throws Exception {
        this.changeStateEdit(com);
        disableUserNameApi = true;
        super.changeStateView();
    }

    public void goToDspVasMobile(DSPCompany obj) throws IOException {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        url = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/module/vasMobile?comId=" + obj.getComId();
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleDelete() throws Exception {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void handleCancel() throws Exception {
        super.handleCancel();
        mFilterCompany = null;
        try {

            if (mselectedCompanyNode != null) {
                if (!((DSPCompany) mselectedCompanyNode.getData()).getComId().equals(userCompany.getComId())) {
                    isVIEW = true;
                    disableAdd = true;
                } else {
                    isVIEW = false;
                    disableAdd = false;
                }
            } else
                resetTreeCompany();

        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    ////////////////////////////////////////
    public void handleFileUploadCompany(FileUploadEvent event) throws Exception {
        try {
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
            String pathFile = file.getName();
            mcompany.setFilePath(pathFile);
            if (isADD) {
                dowloadFile = false;
            } else {
                if (mcompany.getFilePath() == null) {
                    dowloadFile = false;
                } else {
                    dowloadFile = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public DefaultStreamedContent downloadFile(DSPCompany dto) throws Exception {
        try {
            String path = SystemConfig.getConfig("FileUploadPath") + dto.getFilePath();
            return FileUtil.downloadFile(dto.getFilePath(), path);
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    //    Setter getter
    public List<DSPCompany> getmListCompany() {
        return mListCompany;
    }

    public void setmListCompany(List<DSPCompany> mListCompany) {
        this.mListCompany = mListCompany;
    }

    public DSPCompany getSelectedDN() {
        return selectedDN;
    }

    public void setSelectedDN(DSPCompany selectedDN) {
        this.selectedDN = selectedDN;
    }

    public List<DSPCompany> getmListCompanyTree() {
        return mListCompanyTree;
    }

    public void setmListCompanyTree(List<DSPCompany> mListCompanyTree) {
        this.mListCompanyTree = mListCompanyTree;
    }

    public DSPCompany getMcompany() {
        return mcompany;
    }

    public void setMcompany(DSPCompany mcompany) {
        this.mcompany = mcompany;
    }

    public List<DSPCompany> getmFilter() {
        return mFilter;
    }

    public void setmFilter(List<DSPCompany> mFilter) {
        this.mFilter = mFilter;
    }

    public TreeNode getMtreeCompany() {
        return mtreeCompany;
    }

    public void setMtreeCompany(TreeNode mtreeCompany) {
        this.mtreeCompany = mtreeCompany;
    }


    public DSPCompany getMcompanyTree() {
        return mcompanyTree;
    }

    public void setMcompanyTree(DSPCompany mcompanyTree) {
        this.mcompanyTree = mcompanyTree;
    }

    public String getDnSearch() {
        return dnSearch;
    }

    public void setDnSearch(String dnSearch) {
        this.dnSearch = dnSearch;
    }

    public boolean isRenderBtn() {
        return renderBtn;
    }

    public void setRenderBtn(boolean renderBtn) {
        this.renderBtn = renderBtn;
    }

    public DSPCompany getMcompanySearch() {
        return mcompanySearch;
    }

    public void setMcompanySearch(DSPCompany mcompanySearch) {
        this.mcompanySearch = mcompanySearch;
    }

    public boolean isDisableUserName() {
        return disableUserName;
    }

    public void setDisableUserName(boolean disableUserName) {
        this.disableUserName = disableUserName;
    }

    public boolean isDisableUserNameApi() {
        return disableUserNameApi;
    }

    public void setDisableUserNameApi(boolean disableUserNameApi) {
        this.disableUserNameApi = disableUserNameApi;
    }

    public List<DSPCompany> getmFilterCompany() {
        return mFilterCompany;
    }

    public void setmFilterCompany(List<DSPCompany> mFilterCompany) {
        this.mFilterCompany = mFilterCompany;
    }

    public boolean isDisableAdd() {
        return disableAdd;
    }

    public void setDisableAdd(boolean disableAdd) {
        this.disableAdd = disableAdd;
    }

    public UserDTL getmUser() {
        return mUser;
    }

    public void setmUser(UserDTL mUser) {
        this.mUser = mUser;
    }

    public DefaultTreeNode getMselectedCompanyNode() {
        return mselectedCompanyNode;
    }

    public void setMselectedCompanyNode(DefaultTreeNode mselectedCompanyNode) {
        this.mselectedCompanyNode = mselectedCompanyNode;
    }

    public DSPCompany getmSelectedCompanyTree() {
        return mSelectedCompanyTree;
    }

    public void setmSelectedCompanyTree(DSPCompany mSelectedCompanyTree) {
        this.mSelectedCompanyTree = mSelectedCompanyTree;
    }

    public boolean isIsRETAILERorCOMPANY() {
        return isRETAILERorCOMPANY;
    }

    public boolean isIsROOTorChildren() {
        return isROOTorChildren;
    }

    public DSPCompany getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(DSPCompany userCompany) {
        this.userCompany = userCompany;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDowloadFile() {
        return dowloadFile;
    }

    public void setDowloadFile(boolean dowloadFile) {
        this.dowloadFile = dowloadFile;
    }

    public String getPasswordAPI() {
        return passwordAPI;
    }

    public void setPasswordAPI(String passwordAPI) {
        this.passwordAPI = passwordAPI;
    }

    public UserDTL getmUserApi() {
        return mUserApi;
    }

    public void setmUserApi(UserDTL mUserApi) {
        this.mUserApi = mUserApi;
    }
}
