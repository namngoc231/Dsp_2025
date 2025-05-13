package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DipPackage;
import vn.com.telsoft.entity.DipService;
import vn.com.telsoft.model.DipPackageModel;

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
public class DipPackageController extends TSFuncTemplate implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_DIP_PACKAGE";

    private List<DipPackage> mlistDipPackages;
    private List<DipPackage> mlistDipPackagesFilterred;
    private DipPackage[] mselectedDipPackage;
    private DipPackage mtmpDipPackage;

    private List<DipService> mlistDipServices;

    private DipPackageModel dipPackageModel;
    private int render = 0;

    private long serviceId;
    private String serviceName;

    public DipPackageController() throws Exception {
        mtmpDipPackage = new DipPackage();
        dipPackageModel = new DipPackageModel();
        mlistDipPackages = new ArrayList<>();
        mlistDipServices = dipPackageModel.getlistDipServices();
        serviceId = 0;
        serviceName = null;
        init();
    }

    public void init() throws Exception {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.get("serviceId") != null && !params.get("serviceId").equals("")) {
            String paramServiceId = params.get("serviceId");
            serviceId = Long.parseLong(paramServiceId);
            mlistDipPackages = dipPackageModel.getlistDipPackagesById(serviceId);
            for (DipService obj: mlistDipServices) {
                if (obj.getServiceId().equals(serviceId)) {
                    serviceName = obj.getCode();
                    break;
                }
            }
        }
    }

    public void backToDipService() throws IOException {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = req.getRequestURL().toString();
        url = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/module/dipService";
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        render = 0;
        mlistDipPackages = dipPackageModel.getlistDipPackagesById(serviceId);
        PrimeFaces.current().executeScript("PF('table_dip_package').clearFilters();");
    }

    @Override
    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        render = 1;
        mtmpDipPackage = new DipPackage();
        mtmpDipPackage.setServiceId(serviceId);
        PrimeFaces.current().executeScript("PF('table_dip_package').clearFilters();");
    }

    public void changeStateEdit(DipPackage obj) throws Exception {
        super.changeStateEdit();
        mtmpDipPackage = new DipPackage();
        mtmpDipPackage = obj;
//        mtmpDipPackage.setDisplayAmount(String.valueOf(mtmpDipPackage.getInitialAmount()));
        render = 1;
        PrimeFaces.current().executeScript("PF('table_dip_package').clearFilters();");
    }

    @Override
    public void handleOK() throws Exception {
        if (validateData(mtmpDipPackage)) {
            if (dipPackageModel.checkProvCode(mtmpDipPackage) || dipPackageModel.checkPackageCode(mtmpDipPackage)) {
                if (dipPackageModel.checkPackageCode(mtmpDipPackage)) {
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "duplicatePackageCode"));
                }
                if (dipPackageModel.checkProvCode(mtmpDipPackage)) {
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "duplicateProvCode"));
                }
            } else {
                if(isADD) {
                    dipPackageModel.insert(mtmpDipPackage);
                    ClientMessage.logAdd();
                } else {
                    dipPackageModel.update(mtmpDipPackage);
                    ClientMessage.logUpdate();
                }
                handleCancel();
            }
        }
    }

    public void handleDelete(DipPackage obj) throws Exception {
        mtmpDipPackage = new DipPackage();
        mtmpDipPackage = obj;
        dipPackageModel.delete(mtmpDipPackage);
        mlistDipPackages = dipPackageModel.getlistDipPackagesById(serviceId);
        ClientMessage.logDelete();
        PrimeFaces.current().executeScript("PF('table_dip_package').clearFilters();");
    }

    public Boolean validateData(DipPackage obj) throws Exception {
        double data;
        if (obj.getAmountInput().contains("GB")) {
            data = Double.parseDouble(obj.getAmountInput().substring(0, obj.getAmountInput().length() - 2)) * 1024 * 1024;
        } else if (obj.getAmountInput().contains("MB")) {
            data = Double.parseDouble(obj.getAmountInput().substring(0, obj.getAmountInput().length() - 2)) * 1024;
        } else if (obj.getAmountInput().contains("KB")) {
            data = Double.parseDouble(obj.getAmountInput().substring(0, obj.getAmountInput().length() - 2));
        } else {
            data = Double.parseDouble(obj.getAmountInput());
        }
        if (data > 999999999999999L) {
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "dataFullError"));
            return false;
        }
//        obj.setAmountInput(numberPrice(obj.getAmountInput()));
        obj.setInitialAmount(data);
        return true;
    }

    public String numberPrice(String price) {
        int size = price.length();
        String resultAf = "";
        String resultBef = "";
        if (size >= 5) {
            if (price.contains("G") || price.contains("M") || price.contains("K")) {
                resultAf = price.substring(size - 5, size);
                size -= 5;
            } else {
                resultAf = price.substring(size - 4, size);
                size -= 4;
            }

            if (size == 1) {
                price = price.substring(0, 1);
            } else {
                price = price.substring(0, size);
            }

            int thuong = size / 3;
            int du = size % 3;
            if (du == 0) {
                for (int i = 0; i < size; i = i + 3) {
                    resultBef += price.substring(i, i + 3) + ".";
                }
            } else {
                if (du == 1) {
                    resultBef += price.substring(0, 1) + ".";
                    for (int i = 1; i < size; i = i + 3) {
                        resultBef += price.substring(i, i + 3) + ".";
                    }
                }
                if (du == 2) {
                    resultBef += price.substring(0, 2) + ".";
                    for (int i = 2; i < size; i = i + 3) {
                        resultBef += price.substring(i, i + 3) + ".";
                    }
                }
            }
        } else {
            return price;
        }
        return resultBef.substring(0, resultBef.length()) + resultAf;
    }

    @Override
    public void handleDelete() throws Exception {
    }

    ////////get, set
    public List<DipPackage> getMlistDipPackages() {
        return mlistDipPackages;
    }

    public void setMlistDipPackages(List<DipPackage> mlistDipPackages) {
        this.mlistDipPackages = mlistDipPackages;
    }

    public List<DipPackage> getMlistDipPackagesFilterred() {
        return mlistDipPackagesFilterred;
    }

    public void setMlistDipPackagesFilterred(List<DipPackage> mlistDipPackagesFilterred) {
        this.mlistDipPackagesFilterred = mlistDipPackagesFilterred;
    }

    public DipPackage[] getMselectedDipPackage() {
        return mselectedDipPackage;
    }

    public void setMselectedDipPackage(DipPackage[] mselectedDipPackage) {
        this.mselectedDipPackage = mselectedDipPackage;
    }

    public DipPackage getMtmpDipPackage() {
        return mtmpDipPackage;
    }

    public void setMtmpDipPackage(DipPackage mtmpDipPackage) {
        this.mtmpDipPackage = mtmpDipPackage;
    }

    public List<DipService> getMlistDipServices() {
        return mlistDipServices;
    }

    public void setMlistDipServices(List<DipService> mlistDipServices) {
        this.mlistDipServices = mlistDipServices;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
