package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DspNotifConfig;
import vn.com.telsoft.model.DspNotifConfigModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DspNotifConfigController extends TSFuncTemplate implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_NOTI_CONFIG";

    private DspNotifConfig dspNotifConfig;
    private List<DspNotifConfig> lstDspNotifConfig, lstDspNotifConfigFilter;
    private DspNotifConfigModel dspNotifConfigModel;

    private int render = 0;

    public DspNotifConfigController() throws Exception {
        dspNotifConfig = new DspNotifConfig();
        dspNotifConfigModel = new DspNotifConfigModel();
        lstDspNotifConfig = dspNotifConfigModel.getListAll();
    }

    public void preAdd() {
        isDISABLE = false;
        isADD = true;
        render = 1;
        dspNotifConfig = new DspNotifConfig();
    }

    public void preUpdate(DspNotifConfig object) {
        isDISABLE = false;
        render = 1;
        dspNotifConfig = object;
    }

    @Override
    public void handleOK() throws Exception {
        if (isADD) {
            dspNotifConfigModel.insert(dspNotifConfig);
            ClientMessage.logAdd();
        } else {
            dspNotifConfigModel.update(dspNotifConfig);
            ClientMessage.logUpdate();
        }
        handleCancel();
        PrimeFaces.current().executeScript("PF('table_noti').clearFilters();");
    }

    public void delete(DspNotifConfig object) throws Exception {
        dspNotifConfigModel.delete(object);
        ClientMessage.logDelete();
        handleCancel();
        PrimeFaces.current().executeScript("PF('table_noti').clearFilters();");
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        lstDspNotifConfig = dspNotifConfigModel.getListAll();
        render = 0;
    }

    @Override
    public void handleDelete() throws Exception {
    }

    public DspNotifConfig getDspNotifConfig() {
        return dspNotifConfig;
    }

    public void setDspNotifConfig(DspNotifConfig dspNotifConfig) {
        this.dspNotifConfig = dspNotifConfig;
    }

    public List<DspNotifConfig> getLstDspNotifConfig() {
        return lstDspNotifConfig;
    }

    public void setLstDspNotifConfig(List<DspNotifConfig> lstDspNotifConfig) {
        this.lstDspNotifConfig = lstDspNotifConfig;
    }

    public List<DspNotifConfig> getLstDspNotifConfigFilter() {
        return lstDspNotifConfigFilter;
    }

    public void setLstDspNotifConfigFilter(List<DspNotifConfig> lstDspNotifConfigFilter) {
        this.lstDspNotifConfigFilter = lstDspNotifConfigFilter;
    }

    public DspNotifConfigModel getDspNotifConfigModel() {
        return dspNotifConfigModel;
    }

    public void setDspNotifConfigModel(DspNotifConfigModel dspNotifConfigModel) {
        this.dspNotifConfigModel = dspNotifConfigModel;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }
}
