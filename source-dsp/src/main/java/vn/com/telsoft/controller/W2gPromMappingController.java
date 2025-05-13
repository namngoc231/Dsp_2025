package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DSPServicePrice;
import vn.com.telsoft.entity.W2gPromMapping;
import vn.com.telsoft.model.W2gPromMappingModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named
@ViewScoped
public class W2gPromMappingController extends TSFuncTemplate implements Serializable {
    static final String RESOURCE_BUNDLE = "PP_W2G_PROM_MAPPING";
    static final String ERR_DUPLICATE_COMID_PROFILECODE = "DSP_W2G_PROM_MAPPING_UK1";
    static final String ERR_DUPLICATE_PROMCODE = "DSP_W2G_PROM_MAPPING_UK2";

    private List<W2gPromMapping> w2gPromMappingList;
    private List<W2gPromMapping> w2gPromMappingFiltered;
    private W2gPromMapping w2gPromMapping;
    private List<W2gPromMapping> selectW2gPromMapping;
    private W2gPromMappingModel w2gPromMappingModel;
    private List<DSPCompany> listDSPCompany;
    private List<DSPServicePrice> listProfileCode;

    public W2gPromMappingController() throws Exception {
        w2gPromMappingModel = new W2gPromMappingModel();
        this.w2gPromMappingList = w2gPromMappingModel.getListW2gPromMapping();
        w2gPromMapping = new W2gPromMapping();
        listDSPCompany = w2gPromMappingModel.getListDSPCompany("");
        DSPCompany dspCompany = new DSPCompany();
        dspCompany.setComId(-1L);
        dspCompany.setComName(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "select_dest_system"));
        listDSPCompany.add(0, dspCompany);
        listProfileCode = new ArrayList<>();
    }

    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        this.w2gPromMapping = new W2gPromMapping();
    }

    public void changeStateEdit(W2gPromMapping p) throws Exception {
        listProfileCode = w2gPromMappingModel.getListProfileCode(p.getComId());
        super.changeStateEdit();
        this.selectedIndex = this.w2gPromMappingList.indexOf(p);
        this.w2gPromMapping = SerializationUtils.clone(p);
    }

    public void changeStateView(W2gPromMapping p) throws Exception {
        listProfileCode = w2gPromMappingModel.getListProfileCode(p.getComId());
        super.changeStateView();
        this.w2gPromMapping = SerializationUtils.clone(p);
    }

    @Override
    public void handleOK() throws Exception {
        try {
            if (this.isADD) {
                this.w2gPromMappingModel.add(this.w2gPromMapping);
                this.w2gPromMappingList.add(0, this.w2gPromMapping);
                this.w2gPromMapping = new W2gPromMapping();
                ClientMessage.logAdd();
            } else {
                this.w2gPromMappingModel.edit(this.w2gPromMapping);
                this.w2gPromMappingList.set(this.selectedIndex, this.w2gPromMapping);
                ClientMessage.logUpdate();
            }
        } catch (Exception ex) {
            if (ex.getMessage().contains(ERR_DUPLICATE_COMID_PROFILECODE)) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "err_exist_comid_profilecode"));
                return;
            } else if (ex.getMessage().contains(ERR_DUPLICATE_PROMCODE)) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "err_exist_promcode"));
                return;
            } else {
                throw ex;
            }
        }
        handleCancel();
    }

    @Override
    public void handleDelete() throws Exception {
    }

    public void handleDelete(W2gPromMapping w2gPromMapping) throws Exception {
        this.w2gPromMappingModel.delete(Collections.singletonList(w2gPromMapping));
        handleCancel();
        ClientMessage.logDelete();
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        this.w2gPromMappingList = w2gPromMappingModel.getListW2gPromMapping();
        PrimeFaces.current().executeScript("PF('table_w2g_prom_mapping').clearFilters();");
    }

    public void listProfileCodeByCodeId() throws Exception {
        Long comId = w2gPromMapping.getComId();
        listProfileCode = comId == -1 ? null : w2gPromMappingModel.getListProfileCode(comId);
    }

    public List<DSPServicePrice> getListProfileCode() {
        return listProfileCode;
    }

    public void setListProfileCode(List<DSPServicePrice> listProfileCode) {
        this.listProfileCode = listProfileCode;
    }

    public List<DSPCompany> getListDSPCompany() {
        return listDSPCompany;
    }

    public void setListDSPCompany(List<DSPCompany> listDSPCompany) {
        this.listDSPCompany = listDSPCompany;
    }

    public List<W2gPromMapping> getSelectW2gPromMapping() {
        return selectW2gPromMapping;
    }

    public void setSelectW2gPromMapping(List<W2gPromMapping> selectW2gPromMapping) {
        this.selectW2gPromMapping = selectW2gPromMapping;
    }

    public W2gPromMapping getW2gPromMapping() {
        return w2gPromMapping;
    }

    public void setW2gPromMapping(W2gPromMapping w2gPromMapping) {
        this.w2gPromMapping = w2gPromMapping;
    }

    public List<W2gPromMapping> getW2gPromMappingFiltered() {
        return w2gPromMappingFiltered;
    }

    public void setW2gPromMappingFiltered(List<W2gPromMapping> w2gPromMappingFiltered) {
        this.w2gPromMappingFiltered = w2gPromMappingFiltered;
    }

    public List<W2gPromMapping> getW2gPromMappingList() {
        return w2gPromMappingList;
    }

    public void setW2gPromMappingList(List<W2gPromMapping> w2gPromMappingList) {
        this.w2gPromMappingList = w2gPromMappingList;
    }

}

