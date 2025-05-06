package com.faplib.lib.admin.gui;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.Session;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.admin.gui.data.AppGuiModel;
import com.faplib.lib.admin.gui.data.LogAccessGuiModel;
import com.faplib.lib.admin.gui.entity.LogAccess;
import com.faplib.lib.admin.gui.entity.MenuGUIAuthorizator;
import com.faplib.lib.admin.security.Authorizator;
import com.faplib.lib.config.Config;
import com.faplib.lib.config.Constant;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.FileUtil;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class CenterProcess implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FUNC_NAME_KEY = "telsoftfunction";
    private List<MenuGUIAuthorizator> mlistMenuAuthorizated;
    private List<String> mlistMessage = new ArrayList<>();
    private String mstrPath;

    public CenterProcess() throws Exception {
        if (!AdminUser.isLogged()) {
            return;
        }

        //Check IP changed
        /*if(!Session.getIPClient().equals(Session.getSessionValue(Constant.K_LOGGED_IP))) {
            mstrPath = "/modules/centers/ipchanged.xhtml";
            return;
        }*/

        //Check modified password
        if (AdminUser.getUserLogged().getModifiedPassword() == null) {
            mlistMessage.add(ResourceBundleUtil.getAdminMessage("TS-10027"));
            mstrPath = "/modules/user/changepwd.xhtml";
            return;
        }

        if (AdminUser.getUserLogged().getExpireStatus() == 0) {
            mlistMessage.add(ResourceBundleUtil.getAdminMessage("TS-10026"));
            mstrPath = "/modules/user/changepwd.xhtml";
            return;
        }

        //Get auth list
        mlistMenuAuthorizated = Authorizator.getListModuleAuthorization(AdminUser.getUserLogged().getUserId());

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String strAdminModule = request.getParameter("adminModule");
        String strUserModule = request.getParameter("userModule");
        String strReportModule = request.getParameter("reportModule");
        String strFolder1 = request.getParameter("folder1");
        String strFolder2 = request.getParameter("folder2");
        String strFolder3 = request.getParameter("folder3");
        String strFolder4 = request.getParameter("folder4");

        if (strUserModule != null && strUserModule.equals("changepwd")) {
            mstrPath = "/modules/user/changepwd.xhtml";

        } else if (strAdminModule == null && strUserModule == null && strReportModule == null) {
            mstrPath = "/modules/centers/centermain.xhtml";

        } else if (strReportModule != null) {
            logAccess(strReportModule, null, null, null, null, Side.REPORT);
            mstrPath = checkAuthorization(strReportModule, null, null, null, null, Side.REPORT);

        } else if (strUserModule != null) {
            logAccess(strUserModule, strFolder1, strFolder2, strFolder3, strFolder4, Side.USER);
            mstrPath = checkAuthorization(strUserModule, strFolder1, strFolder2, strFolder3, strFolder4, Side.USER);

        } else {
            logAccess(strAdminModule, strFolder1, strFolder2, strFolder3, strFolder4, Side.ADMIN);
            mstrPath = checkAuthorization(strAdminModule, strFolder1, strFolder2, strFolder3, strFolder4, Side.ADMIN);
        }

        //Get app
        if (Session.getSessionValue(Constant.K_LIST_APP_OBJECT) == null) {
            Session.setSessionValue(Constant.K_LIST_APP_OBJECT, (new AppGuiModel()).getListAppObj(SystemConfig.getConfig("APPCode")));
        }
    }

    private void logAccess(String functionName, String folder1, String folder2, String folder3, String folder4, Side side) throws Exception {
        try {
            functionName = appendPath(folder1, folder2, folder3, folder4, functionName);
            String strPrevFunctionName = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(FUNC_NAME_KEY);
            if (strPrevFunctionName == null) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FUNC_NAME_KEY, functionName);

            } else if (strPrevFunctionName.equals(functionName)) {
                return;

            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(FUNC_NAME_KEY, functionName);
            }

            LogAccess logAccess = new LogAccess();
            switch (side) {
                case ADMIN: {
                    logAccess.setObjectPath("/admin/" + functionName);
                    break;
                }
                case USER: {
                    logAccess.setObjectPath("/module/" + functionName);
                    break;
                }
                case REPORT: {
                    logAccess.setObjectPath("/report/" + functionName);
                    break;
                }
            }

            logAccess.setAccessDate(new Date());
            logAccess.setClientAddr(Session.getIPClient(FacesContext.getCurrentInstance()));
            logAccess.setUserId(AdminUser.getUserLogged().getUserId());
            (new LogAccessGuiModel()).add(logAccess);

        } catch (Exception e) {
            if (e.toString().contains("ORA-01400")) {
                SystemLogger.getLogger().warn("Module not found [" + functionName + "]");

            } else {
                throw e;
            }
        }
    }

    private String checkAuthorization(String strModuleName, String folder1, String folder2, String folder3, String folder4, Side side) throws Exception {
        String strTmpPath = "";
        strModuleName = appendPath(folder1, folder2, folder3, folder4, strModuleName);
        switch (side) {
            case USER: {
                strTmpPath = "/module/" + strModuleName;
                break;
            }
            case ADMIN: {
                strTmpPath = "/admin/" + strModuleName;
                break;
            }
            case REPORT: {
                strTmpPath = "/report/" + strModuleName;
                break;
            }
        }

        String strXhtmlFilePath = "";
        for (MenuGUIAuthorizator entity : mlistMenuAuthorizated) {
            if (strTmpPath.equals(entity.getPath())) {
                switch (side) {
                    case USER: {
                        if (Config.isMobileDevice()) {
                            strXhtmlFilePath = "/modules/mobile/" + strModuleName + ".xhtml";
                            if (FileUtil.isExist(strXhtmlFilePath, false)) {
                                break;
                            }
                        }

                        strXhtmlFilePath = "/modules/user/" + strModuleName + ".xhtml";
                        break;
                    }
                    case ADMIN: {
                        if (Config.isMobileDevice()) {
                            strXhtmlFilePath = "/modules/mobile/" + strModuleName + ".xhtml";
                            if (FileUtil.isExist(strXhtmlFilePath, false)) {
                                break;
                            }
                        }

                        strXhtmlFilePath = "/modules/admin/" + strModuleName + ".xhtml";
                        break;
                    }
                    case REPORT: {
                        strXhtmlFilePath = "/includes/report.xhtml";
                        break;
                    }
                }
            }
        }

        if (strXhtmlFilePath.isEmpty()) {
            return "/modules/centers/permission.xhtml";
        }

        //Check file exist
        if (!FileUtil.isExist(strXhtmlFilePath, false)) {
            return "/modules/centers/nofacelet.xhtml";

        } else {
            return strXhtmlFilePath;
        }
    }

    private String appendPath(String folder1, String folder2, String folder3, String folder4, String strModuleName) {
        String strReturn = strModuleName;
        if (folder4 != null) {
            strReturn = folder4 + "/" + strReturn;
        }
        if (folder3 != null) {
            strReturn = folder3 + "/" + strReturn;
        }
        if (folder2 != null) {
            strReturn = folder2 + "/" + strReturn;
        }
        if (folder1 != null) {
            strReturn = folder1 + "/" + strReturn;
        }
        return strReturn;
    }

    public String getMstrPath() {
        return mstrPath;
    }

    public void setMstrPath(String mstrPath) {
        this.mstrPath = mstrPath;
    }

    public List<String> getMlistMessage() {
        return mlistMessage;
    }

    private enum Side {
        USER, ADMIN, REPORT
    }
}
