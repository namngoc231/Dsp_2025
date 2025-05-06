package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DeclareApi;
import vn.com.telsoft.model.DeclareApiModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DeclareApiController extends TSFuncTemplate implements Serializable {

    private List<DeclareApi> mlistDeclareApi;
    private List<DeclareApi> mlistDeclareApiFilterred;
    private DeclareApi[] mselectedDeclareApi;
    private DeclareApi mtmpDeclareApi;
    private DeclareApiModel declareApiModel;

    public DeclareApiController() throws Exception {
        mtmpDeclareApi = new DeclareApi();
        declareApiModel = new DeclareApiModel();
        mlistDeclareApi = declareApiModel.getlistDeclareApi();
    }

    @Override
    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        mtmpDeclareApi = new DeclareApi();
        PrimeFaces.current().executeScript("PF('tableDeclareAPI').clearFilters();");
    }

    public void changeStateEdit(DeclareApi obj) throws Exception {
        super.changeStateEdit();
        mtmpDeclareApi = obj;
        PrimeFaces.current().executeScript("PF('tableDeclareAPI').clearFilters();");
    }

    public void changeStateDel(DeclareApi obj) throws Exception {
        super.changeStateDel();
        mtmpDeclareApi = obj;
    }


    @Override
    public void handleOK() throws Exception {
        if(isADD) {
            declareApiModel.insert(mtmpDeclareApi);
            mlistDeclareApi.add(0, mtmpDeclareApi);
            changeStateAdd();
            ClientMessage.logAdd();
        } else {
            declareApiModel.update(mtmpDeclareApi);
            ClientMessage.logUpdate();
        }
        handleCancel();
    }

    @Override
    public void handleDelete() throws Exception {
        declareApiModel.delete(mtmpDeclareApi);
        for (int i = 0; i < mlistDeclareApi.size(); i++) {
            if (mlistDeclareApi.get(i).getApiId() == mtmpDeclareApi.getApiId()) {
                mlistDeclareApi.remove(i);
                break;
            }
        }
        ClientMessage.logDelete();
        PrimeFaces.current().executeScript("PF('tableDeclareAPI').clearFilters();");
    }

    @Override
    public void handleCancel() throws Exception {
        super.handleCancel();
        PrimeFaces.current().executeScript("PF('tableDeclareAPI').clearFilters();");
    }

    public List<DeclareApi> getMlistDeclareApi() {
        return mlistDeclareApi;
    }

    public void setMlistDeclareApi(List<DeclareApi> mlistDeclareApi) {
        this.mlistDeclareApi = mlistDeclareApi;
    }

    public List<DeclareApi> getMlistDeclareApiFilterred() {
        return mlistDeclareApiFilterred;
    }

    public void setMlistDeclareApiFilterred(List<DeclareApi> mlistDeclareApiFilterred) {
        this.mlistDeclareApiFilterred = mlistDeclareApiFilterred;
    }

    public DeclareApi[] getMselectedDeclareApi() {
        return mselectedDeclareApi;
    }

    public void setMselectedDeclareApi(DeclareApi[] mselectedDeclareApi) {
        this.mselectedDeclareApi = mselectedDeclareApi;
    }

    public DeclareApi getMtmpDeclareApi() {
        return mtmpDeclareApi;
    }

    public void setMtmpDeclareApi(DeclareApi mtmpDeclareApi) {
        this.mtmpDeclareApi = mtmpDeclareApi;
    }

    public DeclareApiModel getDeclareApiModel() {
        return declareApiModel;
    }

    public void setDeclareApiModel(DeclareApiModel declareApiModel) {
        this.declareApiModel = declareApiModel;
    }
}
