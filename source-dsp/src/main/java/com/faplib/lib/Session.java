package com.faplib.lib;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ApplicationResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Session implements Serializable {
    public static String getRequestURL(FacesContext context) {
        Object request = context.getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).getRequestURL().toString();
        } else {
            return "";
        }
    }
    ////////////////////////////////////////////////////////////////////////

    public static String getContextPath(FacesContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String str = request.getServletPath();
        return request.getContextPath();
    }
    ////////////////////////////////////////////////////////////////////////

    public static String getRequestFunction(FacesContext context) {
        Object request = context.getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            String strReturn = ((HttpServletRequest) request).getRequestURL().toString();
            strReturn = strReturn.substring(strReturn.lastIndexOf("/"));
            return strReturn;

        } else {
            return "";
        }
    }
    ////////////////////////////////////////////////////////////////////////

    public static String getIPClient(FacesContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        return request.getRemoteHost();
    }
    ////////////////////////////////////////////////////////////////////////

    public static String getIPClient() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        return request.getRemoteHost();
    }
    ////////////////////////////////////////////////////////////////////////

    public static void reloadBundle(String strFileName) {
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
        ApplicationResourceBundle appBundle = ApplicationAssociate.getCurrentInstance().getResourceBundles().get(strFileName);
        Map<Locale, ResourceBundle> resources = getFieldValue(appBundle, "resources");
        resources.clear();
    }
    ////////////////////////////////////////////////////////////////////////

    private static <T> T getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);

        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            return null;
        }
    }
    ////////////////////////////////////////////////////////////////////////

    public static String getSessionId() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
        return session.getId();
    }
    ////////////////////////////////////////////////////////////////////////

    public static Object removeSession(String strKey) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getSessionMap().remove(strKey);
    }
    ////////////////////////////////////////////////////////////////////////

    public static Object getSessionValue(String strKey) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getSessionMap().get(strKey);
    }
    ////////////////////////////////////////////////////////////////////////

    public static void setSessionValue(String strKey, Object objValue) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            context.getExternalContext().getSessionMap().put(strKey, objValue);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
        }
    }
    ////////////////////////////////////////////////////////////////////////

    public static void destroySession() {
        removeSession("JSESSIONID");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    }
    ////////////////////////////////////////////////////////////////////////

    public static HttpServletResponse getResponse() {
        return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
    }
}
