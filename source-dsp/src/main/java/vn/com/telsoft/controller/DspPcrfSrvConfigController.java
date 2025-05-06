package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.StringUtil;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DspPcrfSrvConfig;
import vn.com.telsoft.model.DspPcrfSrvConfigModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Named
@ViewScoped
public class DspPcrfSrvConfigController extends TSFuncTemplate implements Serializable {

    private DspPcrfSrvConfig dspPcrfSrvConfig;
    private List<DspPcrfSrvConfig> lstDspPcrfSrvConfig, lstDspPcrfSrvConfigFilter;
    private DspPcrfSrvConfigModel dspPcrfSrvConfigModel;

    private int render = 0;

    public DspPcrfSrvConfigController() throws Exception {
        dspPcrfSrvConfig = new DspPcrfSrvConfig();
        dspPcrfSrvConfigModel = new DspPcrfSrvConfigModel();
        lstDspPcrfSrvConfig = dspPcrfSrvConfigModel.getListAll();
    }

    public void preAdd() {
        isDISABLE = false;
        isADD = true;
        render = 1;
        dspPcrfSrvConfig = new DspPcrfSrvConfig();
    }

    public void preUpdate(DspPcrfSrvConfig object) {
        isDISABLE = false;
        render = 1;
        dspPcrfSrvConfig = object;
    }

    @Override
    public void handleOK() throws Exception {
        if (isADD) {
            try {
                dspPcrfSrvConfigModel.insert(dspPcrfSrvConfig);
                ClientMessage.logAdd();
            } catch (SQLException ex) {
                showMessErrDuplicateRecord(ex);
            }
        } else {
            try {
                dspPcrfSrvConfigModel.update(dspPcrfSrvConfig);
                ClientMessage.logUpdate();
            } catch (SQLException ex) {
                showMessErrDuplicateRecord(ex);
            }
        }
        handleCancel();
        PrimeFaces.current().executeScript("PF('table_noti').clearFilters();");
    }

    public void showMessErrDuplicateRecord(SQLException ex) throws SQLException {
        if (ex != null && StringUtil.nvl(ex.getMessage(), "").contains("ORA-00001")) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_DSP_PCRF_SRV_CONFIG", "err_exist_record"));
        } else {
            throw ex;
        }
    }

    public void delete(DspPcrfSrvConfig object) throws Exception {
        dspPcrfSrvConfigModel.delete(object);
        handleCancel();
        PrimeFaces.current().executeScript("PF('table_noti').clearFilters();");
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        lstDspPcrfSrvConfig = dspPcrfSrvConfigModel.getListAll();
        render = 0;
    }

    @Override
    public void handleDelete() throws Exception {
    }

    public DspPcrfSrvConfig getDspPcrfSrvConfig() {
        return dspPcrfSrvConfig;
    }

    public void setDspPcrfSrvConfig(DspPcrfSrvConfig dspPcrfSrvConfig) {
        this.dspPcrfSrvConfig = dspPcrfSrvConfig;
    }

    public List<DspPcrfSrvConfig> getLstDspPcrfSrvConfig() {
        return lstDspPcrfSrvConfig;
    }

    public void setLstDspPcrfSrvConfig(List<DspPcrfSrvConfig> lstDspPcrfSrvConfig) {
        this.lstDspPcrfSrvConfig = lstDspPcrfSrvConfig;
    }

    public List<DspPcrfSrvConfig> getLstDspPcrfSrvConfigFilter() {
        return lstDspPcrfSrvConfigFilter;
    }

    public void setLstDspPcrfSrvConfigFilter(List<DspPcrfSrvConfig> lstDspPcrfSrvConfigFilter) {
        this.lstDspPcrfSrvConfigFilter = lstDspPcrfSrvConfigFilter;
    }

    public DspPcrfSrvConfigModel getDspPcrfSrvConfigModel() {
        return dspPcrfSrvConfigModel;
    }

    public void setDspPcrfSrvConfigModel(DspPcrfSrvConfigModel dspPcrfSrvConfigModel) {
        this.dspPcrfSrvConfigModel = dspPcrfSrvConfigModel;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }
}
