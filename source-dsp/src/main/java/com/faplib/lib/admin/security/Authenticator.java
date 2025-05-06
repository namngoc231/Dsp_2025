package com.faplib.lib.admin.security;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.Session;
import com.faplib.lib.SessionListener;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.TSCookie;
import com.faplib.lib.TelsoftException;
import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.admin.data.AuthenticatorModel;
import com.faplib.lib.admin.gui.data.UserGuiModel;
import com.faplib.lib.admin.gui.entity.AccessTimeAu;
import com.faplib.lib.admin.gui.entity.AddressDTL;
import com.faplib.lib.admin.gui.entity.UserDTL;
import com.faplib.lib.config.Config;
import com.faplib.lib.config.Constant;
import com.faplib.lib.util.IPMask;
import com.faplib.util.StringUtil;
import com.faplib.ws.client.ClientRequestProcessor;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.naming.directory.Attributes;

public class Authenticator extends AMDataPreprocessor implements Serializable {
    public static Map<String, Object> mapIpAddressBot = new HashMap();

    public Authenticator() {
    }

    public static boolean isIpAddressBot() {
        return mapIpAddressBot.containsKey(Session.getIPClient());
    }

    public static void addIpAddressBot() {
        mapIpAddressBot.put(Session.getIPClient(), (Object)null);
    }

    public static void removeIpAddressBot() {
        mapIpAddressBot.remove(Session.getIPClient());
    }

    public static void validatePassword(String strRealPassword) throws Exception {
        (new AuthenticatorModel()).validatePassword(strRealPassword);
    }

    public static void changePassword(String strUserName, String strOldPassword, String strNewPassword) throws Exception {
        if (PolicyProcessor.getPolicy("REQUIRE_STRONG_PASSWORD").equals("1") && strOldPassword.equals(strNewPassword)) {
            throw new TelsoftException("TS-10014", "validatePassword");
        }
    }

    public static boolean isPasswordExpired(String strUserName) throws Exception {
        return (new AuthenticatorModel()).isPasswordExpired(strUserName);
    }

    public static boolean checkAccessTime(long userId) throws Exception {
        List<AccessTimeAu> listAccess = (new AuthenticatorModel()).getAccessTime(userId);
        if (listAccess.isEmpty()) {
            return true;
        } else {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
            Date dt = new Date();
            calendar.setTime(dt);
            int iDayOfWeek = calendar.get(7);
            String strTime = fmt.format(dt);

            for(AccessTimeAu ett : listAccess) {
                if (ett.getDayId() == (long)iDayOfWeek) {
                    String strStartTime = ett.getStartTime();
                    String strEndTime = ett.getEndTime();
                    if ((strStartTime.length() == 0 || strTime.compareTo(strStartTime) >= 0) && (strEndTime.length() == 0 || strTime.compareTo(strEndTime) <= 0)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean checkAddress(long userId) throws Exception {
        List<AddressDTL> listAddress = (new AuthenticatorModel()).getAddress(userId);
        if (listAddress.isEmpty()) {
            return true;
        } else {
            String strIP = Session.getIPClient(FacesContext.getCurrentInstance());
            boolean isBlock = true;
            boolean blIpinRank = false;

            for(AddressDTL ettAddr : listAddress) {
                String strIPSubnet;
                if (IPMask.validateIP(ettAddr.getAddress())) {
                    strIPSubnet = IPMask.getAddressSubnetMask(ettAddr.getAddress(), ettAddr.getSubnet());
                } else {
                    strIPSubnet = ettAddr.getAddress();
                }

                isBlock = "0".equals(ettAddr.getGrantType());
                IPMask ipmask = IPMask.getIPMask(strIPSubnet);
                if (IPMask.checkInRank(ipmask, strIP)) {
                    blIpinRank = true;
                    if (isBlock) {
                        return false;
                    }
                }
            }

            return isBlock != blIpinRank;
        }
    }

    public static boolean checkMaxSessionUser() throws Exception {
        int iSessionCount = Integer.MAX_VALUE;
        String strTmp = PolicyProcessor.getPolicy("MAX_OPEN_SESSION_USER");
        if (!strTmp.isEmpty()) {
            iSessionCount = Integer.parseInt(strTmp);
        }

        return iSessionCount <= SessionListener.getListActiveSession().size();
    }

    public static boolean authenticateLDAPUser(String strUser, String strPass, String strDomain, String strHost) throws Exception {
        LDAPAuthentication ldap = new LDAPAuthentication();
        Attributes att = ldap.authenticateUser(strUser, strPass, strDomain, strHost, "");
        if (att != null) {
            UserDTL tmpUser = (new UserGuiModel()).getInfByUserName(strUser);
            tmpUser.setPassword(strPass);
            if (!otherAuthenticate(tmpUser)) {
                removeUserState();
                return false;
            } else {
                pushUser(tmpUser);
                return true;
            }
        } else {
            updateLoginFail();
            if (processLoginFaile()) {
                ClientMessage.logPAdminMessage("ERR-10010");
            }

            return false;
        }
    }

    private static boolean processLoginFaile() throws Exception {
        String strMaxLoginFailure = PolicyProcessor.getPolicy("MAX_LOGIN_FAILURE");
        if (!strMaxLoginFailure.trim().isEmpty()) {
            Integer intTmp = (Integer)Session.getSessionValue(Constant.K_LOGIN_FAIL_COUNT);
            if (intTmp == null) {
                return false;
            } else if (Integer.parseInt(strMaxLoginFailure) < intTmp) {
                addIpAddressBot();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static void updateLoginFail() {
        Integer count = (Integer)Session.getSessionValue(Constant.K_LOGIN_FAIL_COUNT);
        count = count == null ? new Integer(1) : count + 1;
        Session.setSessionValue(Constant.K_LOGIN_FAIL_COUNT, count);
    }

    public static boolean authenticateUser(String strUser, String strPass) throws Exception {
        if (processLoginFaile()) {
            ClientMessage.logPAdminMessage("ERR-10010");
            return false;
        } else {
            String strLDAPAuthentication = SystemConfig.getConfig("LDAPAuthentication");
            if (!strLDAPAuthentication.equals("0") && !strLDAPAuthentication.equals("1")) {
                ClientMessage.logPAdminMessage("ERR-10006");
                return false;
            } else if (strLDAPAuthentication.equals("0")) {
                return authenticateDBUser(strUser, StringUtil.encrypt(strPass + Constant.V_SALT_PWD, SystemConfig.getConfig("EncryptAlgorithm")));
            } else if (strLDAPAuthentication.equals("1")) {
                String strDomain = SystemConfig.getConfig("Domain");
                String strHost = SystemConfig.getConfig("Host");
                if (authenticateLDAPUser(strUser, strPass, strDomain, strHost)) {
                    return true;
                } else {
                    ClientMessage.logPErr("PP_ADMINMESSAGE", "TS-10011");
                    return authenticateDBUser(strUser, StringUtil.encrypt(strPass + Constant.V_SALT_PWD, SystemConfig.getConfig("EncryptAlgorithm")));
                }
            } else {
                return false;
            }
        }
    }

    private static boolean otherAuthenticate(UserDTL user) throws Exception {
        if (checkMaxSessionUser()) {
            ClientMessage.logPAdminMessage("ERR-10005");
            return false;
        } else if (!checkAccessTime(user.getUserId())) {
            ClientMessage.logPAdminMessage("ERR-10007");
            return false;
        } else if (!checkAddress(user.getUserId())) {
            ClientMessage.logPAdminMessage("ERR-10009");
            return false;
        } else {
            return true;
        }
    }

    public static void removeUserState() {
        Session.removeSession(Constant.K_USER_LOGGED);
        TSCookie.removeCookie(Constant.K_COOKIE_USER_COMEBACK);
    }

    private static void pushUser(UserDTL user) {
        Session.destroySession();
        Session.setSessionValue(Constant.K_USER_LOGGED, user);
    }

    public static boolean authenticateDBUser(String strUser, String strPass) throws Exception {
        UserGuiModel userGuiModel = new UserGuiModel();
        if (Config.isWSEnabled()) {
            String strLoginResult = ClientRequestProcessor.login(strUser, strPass, Session.getIPClient());
            if (strLoginResult.equals("00")) {
                UserDTL user = userGuiModel.getInfByUserName(strUser);
                pushUser(user);
                return true;
            } else {
                ClientMessage.logPAdminMessage(strLoginResult);
                return false;
            }
        } else {
            UserDTL user = userGuiModel.getInfByUserName(strUser);
            if (user.getUserId() != 0L) {
                if (!user.getPassword().equals(strPass)) {
                    userGuiModel.updateLoginFaile(strUser);
                    ClientMessage.logPAdminMessage("TS-10011");
                    updateLoginFail();
                    if (processLoginFaile()) {
                        ClientMessage.logPAdminMessage("ERR-10010");
                    }

                    return false;
                } else if (user.getStatus() == 0L) {
                    ClientMessage.logPAdminMessage("ERR-10003");
                    return false;
                } else if (!otherAuthenticate(user)) {
                    removeUserState();
                    return false;
                } else {
                    if (isPasswordExpired(user.getUserName())) {
                        user.setExpireStatus(0L);
                    }

                    removeIpAddressBot();
                    pushUser(user);
                    return true;
                }
            } else {
                ClientMessage.logPAdminMessage("TS-10011");
                updateLoginFail();
                return false;
            }
        }
    }
}
