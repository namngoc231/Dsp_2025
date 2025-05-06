package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DipService;
import vn.com.telsoft.model.DipServiceModel;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DipServiceController extends TSFuncTemplate implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_DIP_SERVICE";

    private List<DipService> mlistDipServices;
    private List<DipService> mlistDipServicesFilterred;
    private DipService[] mselectedDipService;
    private DipService mtmpDipService;

    private DipServiceModel dipServiceModel;
    private int render = 0;

    public DipServiceController() throws Exception {
        mtmpDipService = new DipService();
        dipServiceModel = new DipServiceModel();
        mlistDipServices = dipServiceModel.getlistDipServices();
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        render = 0;
        mlistDipServices = dipServiceModel.getlistDipServices();
        PrimeFaces.current().executeScript("PF('table_dip_service').clearFilters();");
    }

    @Override
    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        render = 1;
        mtmpDipService = new DipService();
        PrimeFaces.current().executeScript("PF('table_dip_service').clearFilters();");
    }

    public void changeStateEdit(DipService obj) throws Exception {
        super.changeStateEdit();
        mtmpDipService = new DipService();
        mtmpDipService = obj;
        render = 1;
        PrimeFaces.current().executeScript("PF('table_dip_service').clearFilters();");
    }

    @Override
    public void handleOK() throws Exception {
        if (dipServiceModel.checkCode(mtmpDipService)) {
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "duplicateCode"));
        } else {
            if (isADD) {
                dipServiceModel.insert(mtmpDipService);
                ClientMessage.logAdd();
            } else {
                dipServiceModel.update(mtmpDipService);
                ClientMessage.logUpdate();
            }
            handleCancel();
        }
    }

    public void handleDelete(DipService obj) throws Exception {
        mtmpDipService = new DipService();
        mtmpDipService = obj;
        dipServiceModel.delete(mtmpDipService);
        mlistDipServices = dipServiceModel.getlistDipServices();
        ClientMessage.logDelete();
        PrimeFaces.current().executeScript("PF('table_dip_service').clearFilters();");
    }

    public void goToDipPackage(DipService obj) throws IOException {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        url = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/module/dipPackage?serviceId=" + obj.getServiceId();
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    @Override
    public void handleDelete() throws Exception {
    }

    /////get, set
    public List<DipService> getMlistDipServices() {
        return mlistDipServices;
    }

    public void setMlistDipServices(List<DipService> mlistDipServices) {
        this.mlistDipServices = mlistDipServices;
    }

    public List<DipService> getMlistDipServicesFilterred() {
        return mlistDipServicesFilterred;
    }

    public void setMlistDipServicesFilterred(List<DipService> mlistDipServicesFilterred) {
        this.mlistDipServicesFilterred = mlistDipServicesFilterred;
    }

    public DipService[] getMselectedDipService() {
        return mselectedDipService;
    }

    public void setMselectedDipService(DipService[] mselectedDipService) {
        this.mselectedDipService = mselectedDipService;
    }

    public DipService getMtmpDipService() {
        return mtmpDipService;
    }

    public void setMtmpDipService(DipService mtmpDipService) {
        this.mtmpDipService = mtmpDipService;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }
}
