/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import vn.com.telsoft.entity.AmUser;
import vn.com.telsoft.entity.ApiUser;
import vn.com.telsoft.entity.DeclareApi;
import vn.com.telsoft.model.AmUserModel;
import vn.com.telsoft.model.ApiUserModel;
import vn.com.telsoft.model.DeclareApiModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TRIEUNV
 */
@Named
@ViewScoped
public class DecentralizeUserController extends TSFuncTemplate implements Serializable {

    private List<DeclareApi> listComboApi;
    private List<DeclareApi> listTableApi;
    private DeclareApiModel apiModel;
    private ApiUserModel apiUserModel;
    private String user;
    private AmUserModel amUserModel;
    private String isApi;
    private Long apiId;
    private Long userId;
    private List<AmUser> lstUser;
    private List<Long> listApiId;

    public DecentralizeUserController() throws Exception {
        listComboApi = new ArrayList<>();
        listTableApi = new ArrayList<>();
        apiUserModel = new ApiUserModel();
        apiModel = new DeclareApiModel();
        listComboApi = apiModel.getlistDeclareApi();
        amUserModel = new AmUserModel();
        listApiId = new ArrayList<>();
        isApi = "0";
        apiId = 0L;
    }

    public void search(SelectEvent event) throws Exception {
        String strName = (String) event.getObject();
        for (AmUser amUser : lstUser) {
            if (strName.equals(amUser.getUserName())) {
                userId = amUser.getUserId();
                break;
            }
        }
        listTableApi = apiModel.getListApiAllByUserId(userId);
        listComboApi = apiModel.getListApiAllByNotUserId(userId);
        isApi = "1";
    }

    public List<String> getListUser(String query) throws Exception {
        lstUser = amUserModel.getListAmUserAll(query);
        List<String> lstStr = new ArrayList<>();
        if (lstUser.size() > 0) {
            for (AmUser amUser : lstUser) {
                lstStr.add(amUser.getUserName());
            }
        }
        if(lstStr.size() == 0){
            listComboApi = new ArrayList<>();
            listTableApi = new ArrayList<>();
            user = "";
            PrimeFaces.current().ajax().update("form_main:tbl_api");
        }
        return lstStr;
    }


    public void viewApi() {
        if (user != null && !"".equals(user)) {
//            isProfile = "0";
            isApi = "1";
        }
    }

    public void addListApiTable() throws Exception {
        if(listApiId != null && listApiId.size()>0) {
            for (Long idApi : listApiId) {
                for (DeclareApi declareApi : listComboApi) {
                    if (idApi == declareApi.getApiId()) {
                        listComboApi.remove(declareApi);
                        listTableApi.add(0, declareApi);
                        ApiUser apiUser = new ApiUser();
                        apiUser.setApiId(idApi);
                        apiUser.setUserId(userId);
                        apiUserModel.insert(apiUser);
                        break;
                    }
                }
            }
            listApiId = new ArrayList<>();
            ClientMessage.logAdd();
        }
    }

    public void handDeleteApi(DeclareApi api) throws Exception {
        listComboApi.add(api);
        listTableApi.remove(api);
        ApiUser apiUser = new ApiUser();
        apiUser.setApiId(api.getApiId());
        apiUser.setUserId(userId);
        apiUserModel.delete(apiUser);
        ClientMessage.logDelete();
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleDelete() throws Exception {

    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleCancel() throws Exception {
    }

    public String getIsApi() {
        return isApi;
    }

    public void setIsApi(String isApi) {
        this.isApi = isApi;
    }

    public List<DeclareApi> getListComboApi() {
        return listComboApi;
    }

    public void setListComboApi(List<DeclareApi> listComboApi) {
        this.listComboApi = listComboApi;
    }

    public List<DeclareApi> getListTableApi() {
        return listTableApi;
    }

    public void setListTableApi(List<DeclareApi> listTableApi) {
        this.listTableApi = listTableApi;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public List<AmUser> getLstUser() {
        return lstUser;
    }

    public void setLstUser(List<AmUser> lstUser) {
        this.lstUser = lstUser;
    }

    public List<Long> getListApiId() {
        return listApiId;
    }

    public void setListApiId(List<Long> listApiId) {
        this.listApiId = listApiId;
    }
}
