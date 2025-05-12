package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.DateUtil;
import com.faplib.util.FileUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.FilterMeta;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.model.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@ViewScoped
@Named
public class ServiceDeclareSponsorController extends TSFuncTemplate implements Serializable {
    private List<DSPService> listService;
    private List<DSPService> listServiceFiltered;
    private List<DSPOrderPolicy> listOrderPolicy;
    private List<DSPOrderPolicy> listOrderPolicyChange;
    private List<DSPOrderPolicy> listOrderPolicyFiltered;
    private List<DSPServicePolicy> listServicePolicy;
    private List<DSPServicePolicy> listServicePolicyChange;
    private List<DSPServicePrice> listServicePriceChange;
    private List<DSPServicePolicy> listServicePolicyFiltered;
    private List<DSPPolicy> listDspPolicy;
    private List<DSPPolicy> listDspPolicyChange;
    private List<DSPServicePrice> listServicePrice;
    private List<DSPServicePrice> listServicePriceFiltered;
    private List<DSPOrderPolicyTab> listOrderPolicyTab;
    private List<DSPServicePriceTab> listServicePriceTab;
    private List<DSPOrderPolicyTab> listOrderPolicyTabFiltered;

    private DSPService dspService;
    private DSPOrderPolicy dspOrderPolicy;
    private DSPServicePrice dspServicePrice;
    private DSPServicePolicy dspServicePolicy;
    private DspServiceModel dspServiceModel;
    private DspOrderPolicyModel dspOrderPolicyModel;
    private DspServicePolicyModel dspServicePolicyModel;
    private DspPolicyModel dspPolicyModel;
    private DspServicePriceModel dspServicePriceModel;

    private Map<String, FilterMeta> filterState = new HashMap<>();
    private Map<String, FilterMeta> filterStateOrderPolicy = new HashMap<>();
    private Map<String, FilterMeta> filterStateServicePolicy = new HashMap<>();
    private Map<String, FilterMeta> filterStateServicePrice = new HashMap<>();
    private Map<String, FilterMeta> filterStateOrderPolicyTab = new HashMap<>();
    private Map<String, FilterMeta> filterStateServicePriceTab = new HashMap<>();

    private Long serviceId;
    private String isAddService;
    private String isEditService;
    private String isOrderPolicy;
    private String isServicePolicy;
    private String isAddServicePolicy;
    private String isAddServicePrice;
    private String isServicePrice;
    private String isListService;
    private String isEditServicePrice;
    private Integer indexFilterService;
    private Integer isEditOrderPolicy;
    private Integer indexEditService;
    private String isEditServicePolicy;

    private Integer indexEditOrderPolicy;
    private Integer indexEditOrderPolicyChange;
    private Integer indexEditOrderPolicyFiltered;

    private Integer indexEditServicePolicy;
    private Integer indexEditServicePolicyChange;
    private Integer indexEditServicePolicyFilter;

    private Integer indexEditServicePriceFiltered;
    private Integer indexEditServicePrice;
    private SelectItem[] mFilterOptionStatus;
    private SelectItem[] mFilterOptionDefault;
    private SelectItem[] mFilterOptionStatusService;
    private Boolean checkAddService;
    private Boolean checkAddServicePrice;
    //flag order policy
    private final static String DELETE = "D";
    private final static String UPDATE = "U";
    private final static String INSERT = "I";
    private final static String DSP_COM_ORDER_POL = "DSP_COM_ORDER_POL";
    private final static String DSP_COM_PRICE = "DSP_COM_PRICE";
    //new
    private String isOrderPolicyTab;
    private String isServicePriceTab;
    private String isViewOrderPolicyTab;
    private String isViewServicePriceTab;
    private String isAddOrderPolicyTab;
    private String isAddServicePriceTab;
    private String isEditOrderPolicyTab;
    private String isEditServicePriceTab;
    private DSPOrderPolicyTab dspOrderPolicyTab;
    private DspOrderPolicyTabModel dspOrderPolicyTabModel;

    private Integer indexFilterOrderPolicyTab;
    private Integer indexFilterServicePriceTab;
    private Integer indexEditOrderPolicyTab;
    private Integer indexEditServicePriceTab;
    private Boolean checkAddOrderPolicyTab;
    private Boolean checkAddServicePriceTab;
    private List<DSPCompany> listCompanySelected;
    private List<DSPCompany> listCompanyTable;
    private List<DSPCompanyExt> listCompanyTableInit;
    private List<DSPCompany> listCompanyTableFiltered;
    private List<DSPCompanyExt> listCompanyTableChange;
    private List<DSPCompany> listCompanyAutoCom;
    private List<DSPCompany> listCompanyAutoComSelected;
    private List<Long> listComId;
    private List<DSPComOrderPol> listComOrderTable;
    private List<DSPComPrice> listComPriceTable;
    private String isCopyOrderPolicy;
    private String isCopyServicePrice;
    private DSPCompanyModel dspCompanyModel;
    private DspComOrderPolModel dspComOrderPolModel;
    private DspComPriceModel dspComPriceModel;
    private String isAddComOrder;
    private Long tabIdOrder;
    private Date startDateAddCompany;
    private Date endDateAddCompany;
    private String statusOrderAddCom;
    private DSPServicePriceTab dspServicePriceTab;
    private DSPServicePriceTabModel dspServicePriceTabModel;
    private List<DSPServicePriceTab> listServicePriceTabFiltered;
    private String isAddComServicePrice;
    private Map<String, FilterMeta> filterStateComOrder = new HashMap<>();
    private Integer indexListComOrder;
    private Integer indexListComPrice;
    private Integer indexEditServicePriceChange;
    private Long blockServicePrice;
    private Boolean isDatacodeService;
    private String isEditDetailServicePrice;
    private String isEditDetailOrderPolicy;
    private String isActionServicePrice;
    private String isActionOrderPolicy;
    private String isDisplayAddComServicePrice;

    public ServiceDeclareSponsorController() throws Exception {
        dspService = new DSPService();
        dspOrderPolicy = new DSPOrderPolicy();
        dspServicePolicy = new DSPServicePolicy();
        dspServiceModel = new DspServiceModel();
        dspOrderPolicyModel = new DspOrderPolicyModel();
        dspServicePolicyModel = new DspServicePolicyModel();
        dspPolicyModel = new DspPolicyModel();
        listDspPolicyChange = new ArrayList<>();
        listDspPolicy = dspPolicyModel.getDSPPolicy(null);
        listService = new ArrayList<>();
        listOrderPolicy = new ArrayList<>();
        listOrderPolicyChange = new ArrayList<>();
        listServicePolicy = new ArrayList<>();
        listServicePolicyChange = new ArrayList<>();
        serviceId = 0L;
        isAddService = "0";
        isEditService = "0";
        isEditServicePrice = "0";
        isServicePolicy = "0";
        isAddServicePrice = "0";
        isServicePrice = "0";
        isServicePolicy = "0";
        isOrderPolicy = "0";
        isListService = "1";
        isEditOrderPolicy = 0;
        isEditServicePolicy = "0";
        isOrderPolicyTab = "0";
        isAddOrderPolicyTab = "0";
        isEditOrderPolicyTab = "0";
        isEditServicePriceTab = "0";
        indexEditOrderPolicyTab = 0;
        indexEditServicePriceTab = 0;
        listServicePrice = new ArrayList<>();
        dspServicePriceModel = new DspServicePriceModel();
        dspServicePrice = new DSPServicePrice();
        initFilterOptionStatus();
        initFilterOptionDefault();
        checkAddService = false;
        checkAddServicePrice = false;
        checkAddOrderPolicyTab = false;
        checkAddServicePriceTab = false;
        loadListService(null);
//        new
        dspOrderPolicyTab = new DSPOrderPolicyTab();
        dspServicePriceTab = new DSPServicePriceTab();
        dspOrderPolicyTabModel = new DspOrderPolicyTabModel();
        dspComOrderPolModel = new DspComOrderPolModel();
        dspCompanyModel = new DSPCompanyModel();
        listCompanySelected = dspCompanyModel.getListCompany();
        listCompanyTableChange = new ArrayList<>();
        listCompanyTable = new ArrayList<>();
        listCompanyAutoCom = new ArrayList<>();
        isAddComOrder = "0";
        listCompanyAutoComSelected = new ArrayList<>();
        listCompanyTableInit = new ArrayList<>();
        listComOrderTable = new ArrayList<>();
        tabIdOrder = 0L;
        isCopyOrderPolicy = "0";
        isViewOrderPolicyTab = "0";
        statusOrderAddCom = "0";
        isServicePriceTab = "0";
        isAddServicePriceTab = "0";
        isViewServicePriceTab = "0";
        isCopyServicePrice = "0";
        dspComPriceModel = new DspComPriceModel();
        listServicePriceChange = new ArrayList<>();
        listComPriceTable = new ArrayList<>();
        listServicePriceTab = new ArrayList<>();
        dspServicePriceTabModel = new DSPServicePriceTabModel();
        isAddComServicePrice = "0";
        isDatacodeService = false;
        isEditDetailServicePrice = "0";
        isEditDetailOrderPolicy = "0";
        isActionServicePrice = "0";
        isActionOrderPolicy = "0";
        initFilterOptionStatusService();
        isDisplayAddComServicePrice = "1";
    }

    public void changeStateEditService(DSPService dto) {
        dspService = (DSPService) SerializationUtils.clone(dto);
        isEditService = "1";
        isListService = "0";
        isAddService = "1";
        indexEditService = listService.indexOf(dto);
        if (listServiceFiltered != null && listServiceFiltered.size() > 0) {
            indexFilterService = listServiceFiltered.indexOf(dto);
        }
    }


    public void changeStateEditOrderPolicy(DSPOrderPolicy dto) {
        dspOrderPolicy = (DSPOrderPolicy) SerializationUtils.clone(dto);
        isEditOrderPolicy = 1;
        isViewOrderPolicyTab = "0";
        indexEditOrderPolicy = listOrderPolicy.indexOf(dto);
//        indexEditOrderPolicyFiltered = listOrderPolicyFiltered.indexOf(dto);
        indexEditOrderPolicyChange = listOrderPolicyChange.indexOf(dto);
    }

    public void loadListService(DSPService dto) throws Exception {
        listService = dspServiceModel.getDSPService(dto);
    }

    public void loadListOrderPolicy() {
        dspOrderPolicy = new DSPOrderPolicy();
        dspOrderPolicy.setTabId(serviceId);
//        listOrderPolicy = dspOrderPolicyModel.getDSPOrderPolicy(dspOrderPolicy);
    }


    public void changeStateAddSevice() {
        isAddService = "1";
        isListService = "0";
        checkAddService = false;
        dspOrderPolicy = new DSPOrderPolicy();
//        dspOrderPolicy.setServiceId(serviceId);
    }

    public void changeStateAddOrderPolicyTab() {
        isAddOrderPolicyTab = "1";
        isEditOrderPolicyTab = "0";
        isListService = "0";
        isViewOrderPolicyTab = "0";
        isOrderPolicyTab = "0";
        checkAddOrderPolicyTab = false;
        dspOrderPolicyTab = new DSPOrderPolicyTab();
        dspOrderPolicyTab.setServiceId(serviceId);
    }


    public String getIsAddServicePriceTab() {
        return isAddServicePriceTab;
    }

    public void setIsAddServicePriceTab(String isAddServicePriceTab) {
        this.isAddServicePriceTab = isAddServicePriceTab;
    }

    public void changeStateAddSevicePrice() {
        isAddServicePrice = "1";
        isServicePrice = "0";
        isEditServicePrice = "0";
        checkAddServicePrice = false;
        dspServicePrice = new DSPServicePrice();
        dspServicePrice.setTabId(tabIdOrder);
        dspServicePrice.setIndex(listServicePrice.size() + 1);
    }


    public void handleCancelService() {
        isAddService = "0";
        isListService = "1";
        isEditService = "0";
        dspService = new DSPService();
        indexFilterService = null;
        if (checkAddService) {
            PrimeFaces.current().executeScript("PF('table_service').clearFilters();");
            listServiceFiltered = null;
            filterState = new HashMap<>();
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service");
            dataTable.setValueExpression("sortBy", null);
        }
    }

    public void handleCancelOrderPolicyTab() {
        isAddOrderPolicyTab = "0";
        isOrderPolicyTab = "1";
        isListService = "0";
        isCopyOrderPolicy = "0";
        isViewOrderPolicyTab = "0";
        dspOrderPolicyTab = new DSPOrderPolicyTab();
        dspOrderPolicyTab.setServiceId(serviceId);
        indexFilterOrderPolicyTab = null;
        if (checkAddOrderPolicyTab) {
            PrimeFaces.current().executeScript("PF('table_order_policy_tab').clearFilters();");
            listOrderPolicyTabFiltered = null;
            filterStateOrderPolicyTab = new HashMap<>();
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_order_policy_tab");
            dataTable.setValueExpression("sortBy", null);
        }
    }

    public void handleCancelServicePriceTab() {
        isAddServicePriceTab = "0";
        isServicePriceTab = "1";
        isListService = "0";
        isCopyServicePrice = "0";
        isViewServicePriceTab = "0";
        dspServicePriceTab = new DSPServicePriceTab();
        dspServicePriceTab.setServiceId(serviceId);
        indexFilterServicePriceTab = null;
        if (checkAddServicePriceTab) {
            PrimeFaces.current().executeScript("PF('table_service_price_tab').clearFilters();");
            listServicePriceTabFiltered = null;
            filterStateServicePriceTab = new HashMap<>();
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_price_tab");
            dataTable.setValueExpression("sortBy", null);
        }
    }


    public void handleFileUploadOrderPolicy(FileUploadEvent event) {
        try {
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
            String pathFile = file.getName();
            dspOrderPolicyTab.setFilePath(pathFile);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public void handleFileUploadServicePrice(FileUploadEvent event) {
        try {
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
            String pathFile = file.getName();
            dspServicePriceTab.setFilePath(pathFile);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }


    public void handleCancelServicePrice() {
        isAddServicePrice = "0";
        isServicePrice = "1";
        isEditServicePrice = "0";
        indexFilterService = null;
        if (checkAddServicePrice) {
            PrimeFaces.current().executeScript("PF('table_service_price').clearFilters();");
            listServicePriceFiltered = null;
            filterStateServicePrice = new HashMap<>();
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_price");
            dataTable.setValueExpression("sortBy", null);
        }
        resetTableFilters();
    }

    public void resetTableFilters() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance()
                .getViewRoot()
                .findComponent("form_main:table_service_price");

        if (dataTable != null) {
            dataTable.setFirst(0);
            dataTable.reset();
        }
    }


    public void handleBackComOrder() {

        isAddComOrder = "0";
        statusOrderAddCom = "0";
        if (indexListComOrder != null) {
            isOrderPolicyTab = "1";
            if (listCompanyTableChange.size() == 0) {
                listOrderPolicyTab.get(indexListComOrder).setComApply(listCompanyTable.size());
            }
        }
        if (indexListComPrice != null) {
            isServicePriceTab = "1";
            if (listCompanyTableChange.size() == 0) {
                listServicePriceTab.get(indexListComPrice).setComApply(listCompanyTable.size());
            }
        }
        startDateAddCompany = null;
        endDateAddCompany = null;
        listCompanyAutoComSelected = new ArrayList<>();
        listCompanyTable = new ArrayList<>();
        listComOrderTable = new ArrayList<>();
        listCompanyAutoCom = new ArrayList<>();
        listCompanyTableInit = new ArrayList<>();
        indexListComOrder = null;
        indexListComPrice = null;
    }

    public void handleBackServiceFromServicePolicy() {
        isListService = "1";
        isServicePolicy = "0";
        dspServicePolicy = new DSPServicePolicy();
        listServicePolicy = new ArrayList<>();
        listServicePolicyChange = new ArrayList<>();
        listServicePolicyFiltered = null;
    }

    public void handDeleteService(DSPService dto) throws Exception {
        if (!dspServiceModel.checkServiceId(dto.getServiceId())) {
            dspServiceModel.delete(dto);
            listService.remove(dto);
            PrimeFaces.current().executeScript("PF('table_service').clearFilters();");
            ClientMessage.logDelete();
        } else {
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "valueInvolveExist"));
        }
    }


    public void handDeleteOrderPolicyTab(DSPOrderPolicyTab dto) throws Exception {
        dspOrderPolicyTabModel.delete(dto);
        listOrderPolicyTab.remove(dto);
        dspOrderPolicyTab = new DSPOrderPolicyTab();
        dspOrderPolicyTab.setServiceId(serviceId);
        ClientMessage.logDelete();
    }

    public void handDeleteServicePriceTab(DSPServicePriceTab dto) throws Exception {
        int index = listServicePriceTab.indexOf(dto);
        dspServicePriceTabModel.delete(dto);
        dto.setStatus("2");
        listServicePriceTab.set(index, dto);
//        listServicePriceTab.remove(dto);
        dspServicePriceTab = new DSPServicePriceTab();
        dspServicePriceTab.setServiceId(serviceId);
        ClientMessage.logDelete();
    }


    public void changeStateEditOrderPolicyTab(DSPOrderPolicyTab dto) {
        dspOrderPolicyTab = (DSPOrderPolicyTab) SerializationUtils.clone(dto);
        isEditOrderPolicyTab = "1";
        isOrderPolicyTab = "0";
        isAddOrderPolicyTab = "1";
        isViewOrderPolicyTab = "0";
        checkAddOrderPolicyTab = false;
        indexEditOrderPolicyTab = listOrderPolicyTab.indexOf(dto);
        if (listOrderPolicyTabFiltered != null && listOrderPolicyTabFiltered.size() > 0) {
            indexFilterOrderPolicyTab = listOrderPolicyTabFiltered.indexOf(dto);
        }
    }

    public String getIsEditServicePriceTab() {
        return isEditServicePriceTab;
    }

    public void setIsEditServicePriceTab(String isEditServicePriceTab) {
        this.isEditServicePriceTab = isEditServicePriceTab;
    }

    public void changeStateEditServicePriceTab(DSPServicePriceTab dto) {
        dspServicePriceTab = (DSPServicePriceTab) SerializationUtils.clone(dto);
        isEditServicePriceTab = "1";
        isServicePriceTab = "0";
        isViewServicePriceTab = "0";
        isAddServicePriceTab = "1";
        checkAddServicePriceTab = false;
        dspServicePriceTab.setStrBlock(dspServicePriceTab.getStrBlock().replace('.', ' ').replaceAll("\\s+", ""));
        indexEditServicePriceTab = listServicePriceTab.indexOf(dto);
        if (listServicePriceTabFiltered != null && listServicePriceTabFiltered.size() > 0) {
            indexFilterServicePriceTab = listServicePriceTabFiltered.indexOf(dto);
        }
    }


    public void changeStateViewOrderPolicyTab(DSPOrderPolicyTab dto) {
        dspOrderPolicyTab = (DSPOrderPolicyTab) SerializationUtils.clone(dto);
        isViewOrderPolicyTab = "1";
        isOrderPolicyTab = "0";
        isEditOrderPolicyTab = "1";
        isAddOrderPolicyTab = "1";
    }

    public void changeStateViewServicePriceTab(DSPServicePriceTab dto) {
        dspServicePriceTab = (DSPServicePriceTab) SerializationUtils.clone(dto);
        isViewServicePriceTab = "1";
        isServicePriceTab = "0";
        isEditServicePriceTab = "1";
        isAddServicePriceTab = "1";
    }


    public DefaultStreamedContent downloadFileOrderPolicyTab(DSPOrderPolicyTab dto) {
        try {
            String path = SystemConfig.getConfig("FileUploadPath") + dto.getFilePath();
            return FileUtil.downloadFile(dto.getFilePath(), path);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    public DefaultStreamedContent downloadFileServicePriceTab(DSPServicePriceTab dto) {
        try {
            String path = SystemConfig.getConfig("FileUploadPath") + dto.getFilePath();
            return FileUtil.downloadFile(dto.getFilePath(), path);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }


    public void changeStateAddComOrderPolicy(DSPOrderPolicyTab dto) throws Exception {
        isAddComServicePrice = "0";
//        isApplyComServicePrice = false;
        tabIdOrder = dto.getTabId();
        if (dto.getStatus().equals("1")) {
            statusOrderAddCom = "1";
            startDateAddCompany = dto.getStartDate();
            endDateAddCompany = dto.getEndDate();
        }
        isAddComOrder = "1";
        isOrderPolicyTab = "0";
        indexListComOrder = listOrderPolicyTab.indexOf(dto);
        DSPComOrderPol dspCom = new DSPComOrderPol();
        dspCom.setTabId(dto.getTabId());
        dspCom.setStatus("1");
        listComOrderTable = dspComOrderPolModel.getDSPComOrderPol(dspCom, DSP_COM_ORDER_POL);
        if (listCompanySelected.size() > 0) {
            for (DSPCompany dsp : listCompanySelected) {
                listCompanyAutoCom.add(dsp);
            }
            if (listComOrderTable.size() > 0) {
                for (int i = 0; i < listComOrderTable.size(); i++) {
                    for (int j = 0; j < listCompanyAutoCom.size(); j++) {
                        if (listComOrderTable.get(i).getComId().equals(listCompanyAutoCom.get(j).getComId())) {
                            DSPCompanyExt dspComEx = new DSPCompanyExt();
                            dspComEx.setDspCompany(listCompanyAutoCom.get(j));
                            dspComEx.setAppliedDate(listComOrderTable.get(i).getAppliedDate());
                            listCompanyTableInit.add(dspComEx);
                            listCompanyTable.add(listCompanyAutoCom.get(j));
                            listCompanyAutoCom.remove(j);
                            break;
                        }
                    }
                }
            }
        }

    }

    public void changeStateAddComServicePrice(DSPServicePriceTab dto) throws Exception {
        isAddComServicePrice = "1";
        tabIdOrder = dto.getTabId();
//        isApplyComServicePrice= true;
        if (dto.getStatus().equals("1")) {
            statusOrderAddCom = "1";
            startDateAddCompany = dto.getStartTime();
            endDateAddCompany = dto.getEndTime();
        }
        if (dto.getStatus().equals("2")) {
            isDisplayAddComServicePrice = "0";
        } else {
            isDisplayAddComServicePrice = "1";
        }
        isAddComOrder = "1";
        isServicePriceTab = "0";
        indexListComPrice = listServicePriceTab.indexOf(dto);
        DSPComPrice dspCom = new DSPComPrice();
        dspCom.setTabId(dto.getTabId());
        dspCom.setStatus("1");
        listComPriceTable = dspComPriceModel.getDSPComPrice(dspCom, DSP_COM_PRICE);
        if (listCompanySelected.size() > 0) {
            for (DSPCompany dsp : listCompanySelected) {
                listCompanyAutoCom.add(dsp);
            }
            if (listComPriceTable.size() > 0) {
                for (int i = 0; i < listComPriceTable.size(); i++) {
                    for (int j = 0; j < listCompanyAutoCom.size(); j++) {
                        if (listComPriceTable.get(i).getComId().equals(listCompanyAutoCom.get(j).getComId())) {
                            DSPCompanyExt dspComEx = new DSPCompanyExt();
                            dspComEx.setDspCompany(listCompanyAutoCom.get(j));
                            dspComEx.setAppliedDate(listComPriceTable.get(i).getAppliedDate());
                            listCompanyTableInit.add(dspComEx);
                            listCompanyTable.add(listCompanyAutoCom.get(j));
                            listCompanyAutoCom.remove(j);
                            break;
                        }
                    }
                }
            }
        }
    }


    public String validateAddCompany(Long comId, DSPOrderPolicyTab dto1, DSPServicePriceTab dto2) throws Exception {
        List<DSPOrderPolicyTab> lstOrTab = new ArrayList<>();
        List<DSPServicePriceTab> lstPriceTab = new ArrayList<>();
        if (dto2 == null) {
            lstOrTab = dspOrderPolicyTabModel.validateNotDefault(comId, dto1);
        }
        if (dto1 == null) {
            lstPriceTab = dspServicePriceTabModel.validateNotDefault(comId, dto2);
        }

        if (lstOrTab.size() > 0) {
            String nameDescription = "";
            for (DSPOrderPolicyTab dspOrOverlap : lstOrTab) {
                nameDescription += dspOrOverlap.getDescription();
            }
//            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "orderPolicyNotDefaultDup") + nameDescription.substring(0, nameDescription.length() - 1));
            return nameDescription.substring(0, nameDescription.length());
        }
        if (lstPriceTab.size() > 0) {
            String nameDescription = "";
            for (DSPServicePriceTab dspOrOverlap : lstPriceTab) {
                nameDescription += dspOrOverlap.getDescription();
            }
//            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "orderPolicyNotDefaultDup") + nameDescription.substring(0, nameDescription.length() - 1));
            return nameDescription.substring(0, nameDescription.length());
        }
        return "";
    }

    public void addListComOrder() throws Exception {
        Boolean checkMsg = false;
        List<DSPCompany> lstComValidate = new ArrayList<>();
        if (listCompanyAutoComSelected != null && listCompanyAutoComSelected.size() > 0) {
            for (DSPCompany dspComAuto : listCompanyAutoComSelected) {
                Boolean check = true;
                for (DSPCompany dspTable : listCompanyTable) {
                    if (dspTable.getComId().equals(dspComAuto.getComId())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    lstComValidate.add(dspComAuto);
                }
            }
        }
        if (lstComValidate.size() > 0) {
            String checkValid = "";
            if (statusOrderAddCom.equals("1")) {
                List<String> lstNameOrder = new ArrayList<>();

                for (DSPCompany dspValid : lstComValidate) {
                    DSPOrderPolicyTab dspOrderValid = new DSPOrderPolicyTab();
                    DSPServicePriceTab dspPriceValid = new DSPServicePriceTab();
                    String str = "";
                    if (isAddComServicePrice.equals("1")) {
                        dspPriceValid.setServiceId(serviceId);
                        dspPriceValid.setStartTime(startDateAddCompany);
                        dspPriceValid.setEndTime(endDateAddCompany);
                        str = validateAddCompany(dspValid.getComId(), null, dspPriceValid);
                    } else {
                        dspOrderValid.setServiceId(serviceId);
                        dspOrderValid.setStartDate(startDateAddCompany);
                        dspOrderValid.setEndDate(endDateAddCompany);
                        str = validateAddCompany(dspValid.getComId(), dspOrderValid, null);
                    }
                    if (lstNameOrder.size() == 0) {
                        lstNameOrder.add(str);
                    } else {
                        int i = lstNameOrder.indexOf(str);
                        if (i < 0) {
                            lstNameOrder.add(str);
                        }
                    }
                }
                if (lstNameOrder.size() > 0) {
                    for (String strName : lstNameOrder) {
                        if (strName != null && !strName.trim().equals("")) {
                            checkValid += strName + ",";
                        }
                    }
                }

                if (!"".equals(checkValid)) {
                    checkMsg = false;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "comAddError") + checkValid.substring(0, checkValid.length() - 1));
                } else {
                    for (DSPCompany dspValid : lstComValidate) {
                        checkMsg = addComOrderTable(dspValid);
                    }
                }
            } else {
                for (DSPCompany dspValid : lstComValidate) {
                    checkMsg = addComOrderTable(dspValid);
                }
            }
        }
        if (checkMsg) {
            ClientMessage.logAdd();
        }
        if (!statusOrderAddCom.equals("1") || statusOrderAddCom.equals("1") && checkMsg) {
            listCompanyAutoComSelected = new ArrayList<>();
        }
    }

    public Boolean addComOrderTable(DSPCompany dto) {
        listCompanyTable.add(0, dto);
        listCompanyAutoCom.remove(dto);
        if (listCompanyTableInit.size() > 0) {
            if (listCompanyTableChange.size() > 0) {
                for (DSPCompanyExt dspComEx : listCompanyTableChange) {
                    if (dspComEx.getDspCompany().getComId().equals(dto.getComId())) {
                        listCompanyTableChange.remove(dspComEx);
                        break;
                    }
                }
            }
        }
        DSPCompanyExt dspExt = new DSPCompanyExt();
        dspExt.setDspCompany(dto);
        dspExt.setTypeCom(INSERT);
        listCompanyTableChange.add(dspExt);
        return true;
    }

    public void handDeleteComOrder(DSPCompany dto) {
        listCompanyTable.remove(dto);
        listCompanyAutoCom.add(dto);
        if (listCompanyTableInit.size() > 0) {
            if (listCompanyTableChange.size() > 0) {
                for (DSPCompanyExt dspComEx : listCompanyTableChange) {
                    if (dspComEx.getDspCompany().getComId().equals(dto.getComId())) {
                        listCompanyTableChange.remove(dspComEx);
                        break;
                    }
                }
            }
            DSPCompanyExt dspExt = new DSPCompanyExt();
            dspExt.setDspCompany(dto);
            dspExt.setTypeCom(DELETE);
            listCompanyTableChange.add(dspExt);
        }
        ClientMessage.logDelete();
    }

    public void handleSaveComOrder() throws Exception {
        if (listCompanyTableInit.size() > 0) {
//            int index = 0;
            Long comIdComPol = 0L;
            for (DSPCompanyExt dspExt : listCompanyTableChange) {
                Boolean check = true;
                DSPComOrderPol dspComPol = new DSPComOrderPol();
                for (DSPCompanyExt dsp : listCompanyTableInit) {
                    if (dspExt.getDspCompany().getComId().equals(dsp.getDspCompany().getComId())) {
                        check = false;
                        if (DELETE.equals(dspExt.getTypeCom())) {
                            dspComPol.setTabId(tabIdOrder);
                            dspComPol.setComId(dspExt.getDspCompany().getComId());
                            dspComPol.setStatus("0");
                            dspComPol.setAppliedDate(dsp.getAppliedDate());
                            if (isAddComServicePrice.equals("1")) {
                                dspComOrderPolModel.delete(dspComPol, DSP_COM_PRICE);
                            } else {
                                dspComOrderPolModel.delete(dspComPol, DSP_COM_ORDER_POL);
                            }
                            break;
                        } else if (INSERT.equals(dspExt.getTypeCom())) {
                            break;
                        }
                    }
                }
//                ++index;
                if (check == true && INSERT.equals(dspExt.getTypeCom())) {
                    comIdComPol = dspExt.getDspCompany().getComId();
                    dspComPol.setTabId(tabIdOrder);
                    dspComPol.setComId(comIdComPol);
                    dspComPol.setStatus("1");
                    if (isAddComServicePrice.equals("1")) {
                        dspComOrderPolModel.insert(dspComPol, DSP_COM_PRICE);
                    } else {
                        dspComOrderPolModel.insert(dspComPol, DSP_COM_ORDER_POL);
                    }
                }
            }
        } else {
            for (DSPCompany dsp : listCompanyTable) {
                DSPComOrderPol dspComPol = new DSPComOrderPol();
                dspComPol.setStatus("1");
                dspComPol.setTabId(tabIdOrder);
                dspComPol.setComId(dsp.getComId());
                if (isAddComServicePrice.equals("1")) {
                    dspComOrderPolModel.insert(dspComPol, DSP_COM_PRICE);
                } else {
                    dspComOrderPolModel.insert(dspComPol, DSP_COM_ORDER_POL);
                }
            }
        }
        listCompanyAutoComSelected = new ArrayList<>();
        listCompanyTableChange = new ArrayList<>();
        if (isAddComServicePrice.equals("1")) {
            listCompanyTableInit = dspComOrderPolModel.getDSPCompanyInit(tabIdOrder, DSP_COM_PRICE);
        } else {
            listCompanyTableInit = dspComOrderPolModel.getDSPCompanyInit(tabIdOrder, DSP_COM_ORDER_POL);
        }

        ClientMessage.log(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "saveDataSuccess"));
    }


//    public void unselectCompany(UnselectEvent event) throws Exception{
//        DSPCompany dsp = (DSPCompany) event.getObject();
//        listCompanyAutoComSelected.remove(dsp);
//        listCompanyAutoCom.add(dsp);
//    }
//
//    public void selectCompany(SelectEvent event){
//        try{
//        DSPCompany dsp = (DSPCompany) event.getObject();
//        listCompanyAutoComSelected.add(dsp);
//        listCompanyAutoCom.remove(dsp);}
//        catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }

    public List<DSPCompany> completeTheme(String query) {
        String queryLowerCase = query.toLowerCase();
        List<DSPCompany> allThemes = listCompanyAutoCom.stream().filter(t -> t.getComName().toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
//        for(DSPCompany dspSelected: listCompanyAutoComSelected){
//            for(DSPCompany dspComTheme: allThemes){
//                if(dspComTheme.getComId() == dspSelected.getComId()){
//                    allThemes.remove(dspComTheme);
//                    break;
//                }
//            }
//        }
        return allThemes;
    }

    public void changeStateCopyOrderPolicy(DSPOrderPolicyTab dto) {
        isAddOrderPolicyTab = "1";
        isOrderPolicyTab = "0";
        isEditOrderPolicyTab = "0";
        checkAddOrderPolicyTab = false;
        dspOrderPolicyTab = (DSPOrderPolicyTab) SerializationUtils.clone(dto);
        isCopyOrderPolicy = "1";
    }

    public void changeStateCopyServicePrice(DSPServicePriceTab dto) throws Exception {
        isAddServicePriceTab = "1";
        isServicePriceTab = "0";
        isEditServicePriceTab = "0";
        checkAddServicePriceTab = false;
        dspServicePriceTab = (DSPServicePriceTab) SerializationUtils.clone(dto);
        isCopyServicePrice = "1";

        tabIdOrder = dto.getTabId();
        dspServicePrice.setTabId(tabIdOrder);
        listServicePrice = dspServicePriceModel.getDSPServicePrice(dspServicePrice);
        for (DSPServicePrice servicePrice : listServicePrice) {
            servicePrice.setPriceId(null);
        }
    }


    public void changeStateActiveOrderPolicy(DSPOrderPolicyTab dto) throws Exception {
        Boolean check = false;
        Boolean checkDefault = false;
        if (dto.getDefaultValue() == 1) {
            Boolean checkDefaultNull = dspOrderPolicyTabModel.validateDefaultNull(dto, true);
            if (!checkDefaultNull) {
                check = true;
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "RecordDefaultManyOne"));
            } else {
                if (!dspOrderPolicyTabModel.validateDefaultNull(dto, false)) {
                    check = true;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "RecordDefaultNotDup"));
                }
            }
        }

        if (dto.getDefaultValue() == 0) {
            if (dto.getEndDate() == null && dto.getStartDate() == null) {
                check = true;
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "StartAndEndNotNullPolicy"));
            } else {
                DSPComOrderPol dspPol = new DSPComOrderPol();
                dspPol.setTabId(dto.getTabId());
                dspPol.setStatus("1");
                List<DSPComOrderPol> lstCom = dspComOrderPolModel.getDSPComOrderPol(dspPol, DSP_COM_ORDER_POL);
                if (lstCom.size() > 0) {
                    for (DSPComOrderPol dspComPol : lstCom) {
                        List<DSPOrderPolicyTab> lstOrTab = dspOrderPolicyTabModel.validateNotDefault(dspComPol.getComId(), dto);
                        if (lstOrTab.size() > 0) {
                            String nameDescription = "";
                            for (DSPOrderPolicyTab dspOrOverlap : lstOrTab) {
                                nameDescription += dspOrOverlap.getDescription() + ",";
                            }
                            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "orderPolicyNotDefaultDup") + nameDescription.substring(0, nameDescription.length() - 1));
                            check = true;
                        }
                    }
                }
            }
        }

        if (!check) {
            //cho phep ap dung
            dto.setStatus("1");
            dspOrderPolicyTabModel.update(dto);
            ClientMessage.log(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "ActiveSuccess"));
        }
    }

    public Boolean validateServicePriceActive(DSPServicePriceTab dto) throws Exception {
        Boolean check = true;
        if (!isDatacodeService) {
            DSPServicePrice dsp = new DSPServicePrice();
            Long block = dto.getBlock();
            dsp.setTabId(dto.getTabId());
            List<DSPServicePrice> lstServicePrice = dspServicePriceModel.getDSPServicePrice(dsp);
            if (lstServicePrice.size() > 0) {
                for (DSPServicePrice dsp1 : lstServicePrice) {
                    if (dsp1.getCapMin() % block == 0 && dsp1.getCapMax() % block == 0) {
                        continue;
                    } else {
                        check = false;
                        ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "capMinValueandCapMaxValueError"));
                        break;
                    }
                }
            }
        }
        return check;
    }

    public void changeStateActiveServicePrice(DSPServicePriceTab dto) throws Exception {
        Boolean check = false;
        if (validateServicePriceActive(dto)) {
            if (dto.getDefaultValue() == 1) {
                Boolean checkDefaultNull = dspServicePriceTabModel.validateDefaultNull(dto, true);
                if (!checkDefaultNull) {
                    check = true;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "RecordDefaultManyOne"));
                } else {
                    if (!dspServicePriceTabModel.validateDefaultNull(dto, false)) {
                        check = true;
                        ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "RecordDefaultNotDup"));
                    }
                }
            }

            if (dto.getDefaultValue() == 0) {
                if (dto.getEndTime() == null && dto.getStartTime() == null) {
                    check = true;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "StartAndEndNotNullPrice"));
                } else {
                    DSPComPrice dspPol = new DSPComPrice();
                    dspPol.setTabId(dto.getTabId());
                    dspPol.setStatus("1");
                    List<DSPComPrice> lstCom = dspComPriceModel.getDSPComPrice(dspPol, DSP_COM_PRICE);
                    if (lstCom.size() > 0) {
                        for (DSPComPrice dspComPol : lstCom) {
                            List<DSPServicePriceTab> lstOrTab = dspServicePriceTabModel.validateNotDefault(dspComPol.getComId(), dto);
                            if (lstOrTab.size() > 0) {
                                String nameDescription = "";
                                for (DSPServicePriceTab dspOrOverlap : lstOrTab) {
                                    nameDescription += dspOrOverlap.getDescription() + ",";
                                }
                                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "orderPolicyNotDefaultDup") + nameDescription.substring(0, nameDescription.length() - 1));
                                check = true;
                            }
                        }
                    }
                }
            }
        } else {
            check = true;
        }
        if (!check) {
            //cho phep ap dung
            dto.setStatus("1");
            dspServicePriceTabModel.update(dto);
            ClientMessage.log(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "ActiveSuccess"));
        }
    }


    public void handleOKService(DSPService dto) throws Exception {
        if (dto.getServiceName() != null && !dto.getServiceName().trim().equals("") &&
                dto.getServiceCode() != null && !dto.getServiceCode().trim().equals("")) {
            if ("1".equals(isEditService) && dto.getStatus() != null && !dto.getStatus().trim().equals("")) {
                dspServiceModel.update(dto);
                listService.set(indexEditService, dto);
                if (listServiceFiltered != null && listServiceFiltered.size() > 0) {
                    listServiceFiltered.set(indexFilterService, dto);
                }
                ClientMessage.logUpdate();
                isEditService = "0";
                isListService = "1";
                isAddService = "0";
            } else {
                dto.setStatus("1");
                Long index = dspServiceModel.insert(dto);
                if (index > 0) {
                    dto.setServiceId(index);
                    listService.add(0, dto);
                    dspService = new DSPService();
                    checkAddService = true;
                    ClientMessage.logAdd();
                }
            }
        }
    }

    public void handleOKOrderPolicyTab(DSPOrderPolicyTab dto) throws Exception {
        if ("1".equals(isEditOrderPolicyTab)) {
            dspOrderPolicyTabModel.update(dto);
            listOrderPolicyTab.set(indexEditOrderPolicyTab, dto);
            if (listOrderPolicyTabFiltered != null && listOrderPolicyTabFiltered.size() > 0) {
                listOrderPolicyTabFiltered.set(indexFilterOrderPolicyTab, dto);
            }
            ClientMessage.logUpdate();
            isEditOrderPolicyTab = "0";
            isOrderPolicyTab = "1";
            isAddOrderPolicyTab = "0";
        } else {
            dto.setStatus("0");
            Long index = dspOrderPolicyTabModel.insert(dto);
            if (index > 0) {
                dto.setTabId(index);
                dto.setComApply(0);
                listOrderPolicyTab.add(0, dto);
                dspOrderPolicyTab = new DSPOrderPolicyTab();
                dspOrderPolicyTab.setServiceId(serviceId);
                dspOrderPolicyTab.setComApply(0);
                checkAddOrderPolicyTab = true;
                if (isCopyOrderPolicy.equals("1")) {
                    isCopyOrderPolicy = "0";
                    isAddOrderPolicyTab = "0";
                    isOrderPolicyTab = "1";
                    PrimeFaces.current().executeScript("PF('table_order_policy_tab').clearFilters();");
                    listOrderPolicyTabFiltered = null;
                    filterStateOrderPolicyTab = new HashMap<>();
                    DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_order_policy_tab");
                    dataTable.setValueExpression("sortBy", null);
                }
                ClientMessage.logAdd();
            }
        }
    }

    public void handleOKServicePriceTab(DSPServicePriceTab dto) throws Exception {
        if (validateServicePriceTab(dto)) {
            if ("1".equals(isEditServicePriceTab)) {
                dspServicePriceTabModel.update(dto);
                listServicePriceTab.set(indexEditServicePriceTab, dto);
                if (listServicePriceTabFiltered != null && listServicePriceTabFiltered.size() > 0) {
                    listServicePriceTabFiltered.set(indexFilterServicePriceTab, dto);
                }
                ClientMessage.logUpdate();
                isEditServicePriceTab = "0";
                isServicePriceTab = "1";
                isAddServicePriceTab = "0";
            } else {
                dto.setStatus("0");
                Long index = dspServicePriceTabModel.insert(dto);
                if (index > 0) {
                    dto.setTabId(index);
                    dto.setComApply(0);
                    listServicePriceTab.add(0, dto);
                    dspServicePriceTab = new DSPServicePriceTab();
                    dspServicePriceTab.setServiceId(serviceId);
                    dspServicePriceTab.setCurrency("VND");
                    dspServicePriceTab.setComApply(0);
                    checkAddServicePriceTab = true;
                    if (isCopyServicePrice.equals("1")) {
                        isCopyServicePrice = "0";
                        isAddServicePriceTab = "0";
                        isServicePriceTab = "1";
                        PrimeFaces.current().executeScript("PF('table_service_price_tab').clearFilters();");
                        listServicePriceTabFiltered = null;
                        for (DSPServicePrice dsp : listServicePrice) {
                            if (!DELETE.equals(dsp.getType())) {
                                dsp.setTabId(index);
                                Long id = dspServicePriceModel.insert(dsp);
                                dsp.setPriceId(id);
                            }
                        }
                        filterStateServicePriceTab = new HashMap<>();
                        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_price_tab");
                        dataTable.setValueExpression("sortBy", null);
                    }
                    ClientMessage.logAdd();
                }
            }
        }
    }

    public Boolean validateServicePriceTab(DSPServicePriceTab dto) {
        if (dto.getStrBlock().contains("G")) {
            blockServicePrice = Long.parseLong(dto.getStrBlock().substring(0, dto.getStrBlock().length() - 2)) * 1024 * 1024;
        } else if (dto.getStrBlock().contains("M")) {
            blockServicePrice = Long.parseLong(dto.getStrBlock().substring(0, dto.getStrBlock().length() - 2)) * 1024;
        } else {
            blockServicePrice = Long.parseLong(dto.getStrBlock().substring(0, dto.getStrBlock().length()));
        }
        if (blockServicePrice > 9999999999L) {
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "errorBlock"));
            return false;
        }
        dto.setStrBlock(numberPrice(dto.getStrBlock()));
        dto.setBlock(blockServicePrice);
        return true;
    }

    public void handleOKOrderPolicy(DSPOrderPolicy dto) {
        if (1 == isEditOrderPolicy) {
            if (dspOrderPolicy.getMinValue() > dspOrderPolicy.getMaxValue()) {
                PrimeFaces.current().ajax().update("form_main:OrderPolicyMsg");
                FacesContext.getCurrentInstance().addMessage("dialogOrderPolicy:OrderPolicyMsg", new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "errorOccur"), ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "minValuemoremaxValue")));
            } else {
                listOrderPolicy.set(indexEditOrderPolicy, dspOrderPolicy);
//                listOrderPolicyFiltered.set(indexEditOrderPolicyFiltered, dspOrderPolicy);
                if (dspOrderPolicy.getType() == null || !(dspOrderPolicy.getType() != null && !dspOrderPolicy.getType().equals(INSERT) || dspOrderPolicy.getType() != null && !dspOrderPolicy.getType().equals(DELETE))) {
                    dspOrderPolicy.setType(UPDATE);
                }
                listOrderPolicyChange.set(indexEditOrderPolicyChange, dspOrderPolicy);
                PrimeFaces.current().executeScript("PF('dialogOrderPolicy').hide();");
                isActionOrderPolicy = "1";
                ClientMessage.logUpdate();
            }
        } else {
            if (dto.getMinValue() > dto.getMaxValue()) {
                PrimeFaces.current().ajax().update("form_main:OrderPolicyMsg");
                FacesContext.getCurrentInstance().addMessage("dialogOrderPolicy:OrderPolicyMsg", new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "errorOccur"), ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "minValuemoremaxValue")));
            } else {
                insertOrderPolicy(dspOrderPolicy);
                PrimeFaces.current().executeScript("PF('dialogOrderPolicy').hide();");
                isActionOrderPolicy = "1";
                ClientMessage.logAdd();
            }
        }
    }


    public void handleOKServicePrice() throws Exception {
        if (validateServicePrice()) {
            if ("1".equals(isEditServicePrice)) {
                listServicePrice.set(indexEditServicePrice, dspServicePrice);
//                if (listServicePriceFiltered != null && listServicePriceFiltered.size() > 0) {
//                    listServicePriceFiltered.set(indexEditServicePriceFiltered, dspServicePrice);
//                }
                if (dspServicePrice.getType() == null || !(dspServicePrice.getType() != null && !dspServicePrice.getType().equals(INSERT) || dspServicePrice.getType() != null && !dspServicePrice.getType().equals(DELETE))) {
                    dspServicePrice.setType(UPDATE);
                }
                listServicePriceChange.set(indexEditServicePriceChange, dspServicePrice);
                ClientMessage.logUpdate();
                isEditServicePrice = "0";
                isServicePrice = "1";
                isAddServicePrice = "0";
            } else {
                insertServicePrice(dspServicePrice);
            }
            isActionServicePrice = "1";
        }
        resetTableFilters();
    }

    public Boolean validateServicePrice() {
        if (!checkDuplicateServicePriceName()) {
            return false;
        }
        Long capMinPrice = 0L;
        Long capMaxPrice = 0L;
        Boolean check = true;
        if (dspServicePrice.getStrCapMin().contains("G")) {
            capMinPrice = Long.parseLong(dspServicePrice.getStrCapMin().substring(0, dspServicePrice.getStrCapMin().length() - 2)) * 1024 * 1024;
        } else if (dspServicePrice.getStrCapMin().contains("M")) {
            capMinPrice = Long.parseLong(dspServicePrice.getStrCapMin().substring(0, dspServicePrice.getStrCapMin().length() - 2)) * 1024;
        } else {
            capMinPrice = Long.parseLong(dspServicePrice.getStrCapMin().substring(0, dspServicePrice.getStrCapMin().length()));
        }
        if (dspServicePrice.getStrCapMax().contains("G")) {
            capMaxPrice = Long.parseLong(dspServicePrice.getStrCapMax().substring(0, dspServicePrice.getStrCapMax().length() - 2)) * 1024 * 1024;
        } else if (dspServicePrice.getStrCapMax().contains("M")) {
            capMaxPrice = Long.parseLong(dspServicePrice.getStrCapMax().substring(0, dspServicePrice.getStrCapMax().length() - 2)) * 1024;
        } else {
            capMaxPrice = Long.parseLong(dspServicePrice.getStrCapMax().substring(0, dspServicePrice.getStrCapMax().length()));
        }
        if (capMinPrice > 9999999999L) {
            check = false;
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "errorCapmin"));
        } else if (capMaxPrice > 9999999999L) {
            check = false;
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "errorCapmax"));
        } else if (capMinPrice > capMaxPrice) {
            check = false;
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "capMinValuemorecapMaxValue"));
        } else {
            if (!isDatacodeService) {
                Long remainCapMin = capMinPrice % blockServicePrice;
                Long remainCapMax = capMaxPrice % blockServicePrice;
                if (remainCapMin > 0 && remainCapMax > 0) {
                    check = false;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "capMinValueandCapMaxValueError"));
                } else if (remainCapMin > 0) {
                    check = false;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "capMinValueError"));
                } else if (remainCapMax > 0) {
                    check = false;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "capMaxValueError"));
                }
                if (check) {
                    dspServicePrice.setStrCapMin(numberPrice(dspServicePrice.getStrCapMin()));
                    dspServicePrice.setStrCapMax(numberPrice(dspServicePrice.getStrCapMax()));
                    dspServicePrice.setCapMin(capMinPrice);
                    dspServicePrice.setCapMax(capMaxPrice);
                    dspServicePrice.setCapMinBlock(capMinPrice / blockServicePrice);
                    dspServicePrice.setCapMaxBlock(capMaxPrice / blockServicePrice);
                }
            } else {
                dspServicePrice.setStrCapMin(numberPrice(dspServicePrice.getStrCapMin()));
                dspServicePrice.setStrCapMax(numberPrice(dspServicePrice.getStrCapMax()));
                dspServicePrice.setCapMin(capMinPrice);
                dspServicePrice.setCapMax(capMaxPrice);
            }
        }
        return check;
    }

    public boolean checkDuplicateServicePriceName() {
        Boolean check = true;
        if (listServicePrice != null && listServicePrice.size() > 0) {
            for (DSPServicePrice item : listServicePrice) {
                if (item.getIndex() != dspServicePrice.getIndex() && item.getName().trim().equals(dspServicePrice.getName())) {
                    check = false;
                    ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "nameDuplicateErr"));
                }
            }
        }
        return check;
    }

    public void insertServicePrice(DSPServicePrice dto) {
        checkAddServicePrice = true;
        listServicePrice.add(0, dto);
        dto.setType(INSERT);
        listServicePriceChange.add(dto);
        dspServicePrice = new DSPServicePrice();
        dspServicePrice.setTabId(tabIdOrder);
        ClientMessage.logAdd();
    }

    public void insertOrderPolicy(DSPOrderPolicy dto) {
        listOrderPolicy.add(0, dto);
        dto.setType(INSERT);
        listOrderPolicyChange.add(dto);
        PrimeFaces.current().executeScript("PF('table_order_policy').clearFilters();");
        dspOrderPolicy = new DSPOrderPolicy();
        dspOrderPolicy.setServiceId(serviceId);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_order_policy");
        dataTable.setValueExpression("sortBy", null);
    }


    public void handleBackOrderPolicyTab() {
        isOrderPolicy = "0";
        isOrderPolicyTab = "1";
        dspOrderPolicy = new DSPOrderPolicy();
        listOrderPolicy = new ArrayList<>();
        listOrderPolicyChange = new ArrayList<>();
    }

    public void handleBackServicePriceTab() {
        isServicePrice = "0";
        isServicePriceTab = "1";
        dspServicePrice = new DSPServicePrice();
        listServicePrice = new ArrayList<>();
        listServicePriceChange = new ArrayList<>();
    }

    public void handleBackService() {
        isListService = "1";
        isOrderPolicy = "0";
        isServicePrice = "0";
        isOrderPolicyTab = "0";
        isServicePriceTab = "0";
        dspOrderPolicy = new DSPOrderPolicy();
        listOrderPolicy = new ArrayList<>();
        listOrderPolicyChange = new ArrayList<>();
        dspServicePrice = new DSPServicePrice();
        listServicePrice = new ArrayList<>();
        dspOrderPolicyTab = new DSPOrderPolicyTab();
        listOrderPolicyTab = new ArrayList<>();
        listServicePriceTab = new ArrayList<>();
        dspServicePriceTab = new DSPServicePriceTab();
    }

    public void changeStateOrderPolicyTab(DSPService dto) throws Exception {
        isOrderPolicyTab = "1";
        isListService = "0";
        serviceId = dto.getServiceId();
        dspOrderPolicyTab.setServiceId(serviceId);
        dspOrderPolicyTab.setStatus("1,0");
        listOrderPolicyTab = dspOrderPolicyTabModel.getDSPOrderPolicyTab(dspOrderPolicyTab);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_order_policy_tab");
        dataTable.setValueExpression("sortBy", null);
    }


    public void changeStateServicePriceTab(DSPService dto) throws Exception {
        DSPServicePolicy dtoServicePolicy = new DSPServicePolicy();
        dtoServicePolicy.setPolicyName("CHECK_PRICE_TABLE");
        dtoServicePolicy.setServiceId(dto.getServiceId());
        List<DSPServicePolicy> lst = dspServicePolicyModel.getDSPServicePolicy(dtoServicePolicy);
        if (lst.size() > 0) {
            isDatacodeService = true;
        }
        isServicePriceTab = "1";
        isListService = "0";
        serviceId = dto.getServiceId();
        dspServicePriceTab.setServiceId(serviceId);
        listServicePriceTab = dspServicePriceTabModel.getDSPServicePriceTab(dspServicePriceTab);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_price_tab");
        dataTable.setValueExpression("sortBy", null);
    }

    public void changeStateServicePrice(DSPServicePriceTab dto) throws Exception {
        isServicePriceTab = "0";
        isServicePrice = "1";
        dto.setStrBlock(dto.getStrBlock().replace('.', ' ').replaceAll("\\s+", ""));
        if (dto.getStrBlock().contains("G")) {
            blockServicePrice = Long.parseLong(dto.getStrBlock().substring(0, dto.getStrBlock().length() - 2)) * 1024 * 1024;
        } else if (dto.getStrBlock().contains("M")) {
            blockServicePrice = Long.parseLong(dto.getStrBlock().substring(0, dto.getStrBlock().length() - 2)) * 1024;
        } else {
            blockServicePrice = Long.parseLong(dto.getStrBlock().substring(0, dto.getStrBlock().length()));
        }
        if ("1".equals(dto.getStatus()) || "2".equals(dto.getStatus())) {
            isEditDetailServicePrice = "1";
        } else {
            isEditDetailServicePrice = "0";
        }
        tabIdOrder = dto.getTabId();
        dspServicePrice.setTabId(tabIdOrder);
        listServicePrice = dspServicePriceModel.getDSPServicePrice(dspServicePrice);
        for (DSPServicePrice dsp : listServicePrice) {
            listServicePriceChange.add(dsp);
        }
        isActionServicePrice = "0";
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_price");
        dataTable.setValueExpression("sortBy", null);
    }

    public void changeStateOrderPolicy(DSPOrderPolicyTab dto) throws Exception {
        isOrderPolicyTab = "0";
        isOrderPolicy = "1";
        tabIdOrder = dto.getTabId();
        dspOrderPolicy.setTabId(tabIdOrder);
        listOrderPolicy = dspOrderPolicyModel.getDSPOrderPolicy(dspOrderPolicy);
        if ("1".equals(dto.getStatus())) {
            isEditDetailOrderPolicy = "1";
        } else {
            isEditDetailOrderPolicy = "0";
        }
        for (DSPOrderPolicy dsp : listOrderPolicy) {
            listOrderPolicyChange.add(dsp);
        }
        isActionOrderPolicy = "0";
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_order_policy");
        dataTable.setValueExpression("sortBy", null);
    }

    public void changeStateServicePolicy(DSPService dto) throws Exception {
        isServicePolicy = "1";
        isListService = "0";
        serviceId = dto.getServiceId();
        dspServicePolicy.setServiceId(serviceId);
        listServicePolicy = dspServicePolicyModel.getDSPServicePolicy(dspServicePolicy);
        for (DSPServicePolicy dsp : listServicePolicy) {
            listServicePolicyChange.add(dsp);
        }
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_policy");
        dataTable.setValueExpression("sortBy", null);
    }


    public void handleOKServicePolicy(DSPServicePolicy dto) {
        if (!"-1".equals(dspServicePolicy.getPolicyName())) {
            if ("1".equals(isEditServicePolicy)) {
                listServicePolicy.set(indexEditServicePolicy, dspServicePolicy);
                listServicePolicyFiltered.set(indexEditServicePolicyFilter, dspServicePolicy);
                if (!(dspServicePolicy.getType() != null && !dspServicePolicy.getType().equals(INSERT) || dspServicePolicy.getType() != null && !dspServicePolicy.getType().equals(DELETE))) {
                    dspServicePolicy.setType(UPDATE);
                }
                listServicePolicyChange.set(indexEditServicePolicyChange, dspServicePolicy);
                ClientMessage.logUpdate();
            } else {
                listServicePolicy.add(0, dto);
                dto.setType(INSERT);
                listServicePolicyChange.add(dto);
                PrimeFaces.current().executeScript("PF('table_service_policy').clearFilters();");
                dspServicePolicy = new DSPServicePolicy();
                dspServicePolicy.setServiceId(serviceId);
                loadListPolicy();
                ClientMessage.logAdd();
            }
            PrimeFaces.current().executeScript("PF('dialogServicePolicy').hide();");
        } else {
            PrimeFaces.current().ajax().update("form_main:ServicePolicyMsg");
            FacesContext.getCurrentInstance().addMessage("dialogServicePolicy:ServicePolicyMsg", new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "errorOccur"), ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "policyNameNotNull")));
        }
    }


    public void changeStateEditServicePolicy(DSPServicePolicy dto) {
        dspServicePolicy = (DSPServicePolicy) SerializationUtils.clone(dto);
        isEditServicePolicy = "1";
        isAddServicePolicy = "0";
        indexEditServicePolicy = listServicePolicy.indexOf(dto);
        indexEditServicePolicyFilter = listServicePolicyFiltered.indexOf(dto);
        indexEditServicePolicyChange = listServicePolicyChange.indexOf(dto);
        loadListPolicy();
    }

    public void loadListPolicy() {
        listDspPolicyChange = new ArrayList<>();
        if (listDspPolicy != null && listDspPolicy.size() > 0) {
            for (DSPPolicy dsp : listDspPolicy) {
                listDspPolicyChange.add(dsp);
            }
            if (listServicePolicy.size() > 0) {
                for (DSPServicePolicy dspSer : listServicePolicy) {
                    if (listDspPolicyChange.size() > 0) {
                        for (DSPPolicy dspPo : listDspPolicyChange) {
                            if (dspSer.getPolicyName().equals(dspPo.getPolicyName())) {
                                if ((dspServicePolicy.getPolicyName() != null && !dspServicePolicy.getPolicyName().equals(dspSer.getPolicyName())) || "1".equals(isAddServicePolicy)) {
                                    listDspPolicyChange.remove(dspPo);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void onDescriptionServicePolicyChanged() {
        for (DSPPolicy dsp : listDspPolicyChange) {
            if (dspServicePolicy.getPolicyName() != null && "-1".equals(dspServicePolicy.getPolicyName())) {
                dspServicePolicy.setDescription("");
            } else if (dsp.getPolicyName().equals(dspServicePolicy.getPolicyName())) {
                dspServicePolicy.setDescription(dsp.getDescription());
                break;
            }
        }
    }

    public void handDeleteOrderPolicy(DSPOrderPolicy dto) {
        int indexDto = listOrderPolicy.indexOf(dto);
        int indexChange = listOrderPolicyChange.indexOf(dto);
        listOrderPolicy.remove(indexDto);
        dto.setType(DELETE);
        listOrderPolicyChange.set(indexChange, dto);
        isActionOrderPolicy = "1";
        ClientMessage.logDelete();
    }

    public void handDeleteServicePolicy(DSPServicePolicy dto) {
        if (!dto.getPolicyName().equals("CHECK_PRICE_TABLE")) {
            int indexChange = listServicePolicyChange.indexOf(dto);
            int indexDto = listServicePolicy.indexOf(dto);
            listServicePolicy.remove(indexDto);
            dto.setType(DELETE);
            listServicePolicyChange.set(indexChange, dto);
            PrimeFaces.current().executeScript("PF('table_service_policy').clearFilters();");
            DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_service_policy");
            dataTable.setValueExpression("sortBy", null);
            ClientMessage.logDelete();
        } else {
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "NotDelCheckPriceTable"));
        }
    }


    public void changeStateAddOrderPolicy() {
        isEditOrderPolicy = 0;
        dspOrderPolicy = new DSPOrderPolicy();
        dspOrderPolicy.setTabId(tabIdOrder);
    }

    public void changeStateAddServicePolicy() {
        isEditServicePolicy = "0";
        isAddServicePolicy = "1";
        dspServicePolicy = new DSPServicePolicy();
        dspServicePolicy.setServiceId(serviceId);
        dspServicePolicy.setStatus("1");
        loadListPolicy();
    }


    public void handDeleteServicePrice(DSPServicePrice dto) {
        int indexDto, indexChange ;
        if (dto.getPriceId() != null) {
             indexDto = findIndexByIdOfListServicePrice(dto.getPriceId());
             indexChange = findIndexByIdOfListServicePriceChange(dto.getPriceId());
        } else {
            indexDto = listServicePrice.indexOf(dto);
            indexChange = listServicePriceChange.indexOf(dto);
        }
        listServicePrice.remove(indexDto);
        dto.setType(DELETE);
        listServicePriceChange.set(indexChange, dto);
        isActionServicePrice = "1";
        ClientMessage.logDelete();
        resetTableFilters();
    }

    public Boolean validateSaveOrderPolicy() {
        Boolean check = true;
        if (listOrderPolicy.size() > 1) {
            List<DSPOrderPolicy> sortedOrderPolicy = listOrderPolicy.stream()
                    .sorted(Comparator.comparing(DSPOrderPolicy::getMinValue))
                    .collect(Collectors.toList());

            for (int i = 0; i < sortedOrderPolicy.size(); i++) {
                if (i != sortedOrderPolicy.size() - 1) {
                    if (!sortedOrderPolicy.get(i).getMaxValue().equals(sortedOrderPolicy.get(i + 1).getMinValue())) {
                        check = false;
                        ClientMessage.logErr(MessageFormat.format(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "RangeMinMaxError"), sortedOrderPolicy.get(i + 1).getMinValue(), sortedOrderPolicy.get(i + 1).getMaxValue()));
                        break;
                    }
                }
            }
        }
        if (!check) {
            return check;
        }
        return true;
    }


    public Boolean validateSaveServicePrice() {
        Boolean check = true;
        if (!isDatacodeService) {
            if (listServicePrice.size() > 1) {
                List<DSPServicePrice> sortedOrderPolicy = listServicePrice.stream()
                        .sorted(Comparator.comparing(DSPServicePrice::getCapMin))
                        .collect(Collectors.toList());

                for (int i = 0; i < sortedOrderPolicy.size(); i++) {
                    if (i != sortedOrderPolicy.size() - 1) {
                        if (!sortedOrderPolicy.get(i).getCapMax().equals(sortedOrderPolicy.get(i + 1).getCapMin())) {
                            check = false;
                            ClientMessage.logErr(MessageFormat.format(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "nameError"), sortedOrderPolicy.get(i + 1).getName()));
                            break;
                        }
                    }
                }
            }
        }
        if (!check) {
            return check;
        }
        return true;
    }

    public void changeStateEditServicePrice(DSPServicePrice dto) {
        dspServicePrice = (DSPServicePrice) SerializationUtils.clone(dto);
        dspServicePrice.setStrCapMin(dspServicePrice.getStrCapMin().replace('.', ' ').replaceAll("\\s+", ""));
        dspServicePrice.setStrCapMax(dspServicePrice.getStrCapMax().replace('.', ' ').replaceAll("\\s+", ""));
        isEditServicePrice = "1";
        isServicePrice = "0";
        isAddServicePrice = "1";

        if (dto.getPriceId() != null) {
            indexEditServicePrice = findIndexByIdOfListServicePrice(dto.getPriceId());
            indexEditServicePriceChange = findIndexByIdOfListServicePriceChange(dto.getPriceId());
        } else {
            indexEditServicePrice = listServicePrice.indexOf(dto);
//        if (listServicePriceFiltered != null && listServicePriceFiltered.size() > 0) {
//            indexEditServicePriceFiltered = listServicePriceFiltered.indexOf(dto);
//        }
            indexEditServicePriceChange = listServicePriceChange.indexOf(dto);
        }
    }

    private int findIndexByIdOfListServicePrice(Long priceId) {
        for (int i = 0; i < listServicePrice.size(); i++) {
            if (listServicePrice.get(i).getPriceId() != null && listServicePrice.get(i).getPriceId().equals(priceId)) {
                return i;
            }
        }
        return -1;
    }

    private int findIndexByIdOfListServicePriceChange(Long priceId) {
        for (int i = 0; i < listServicePriceChange.size(); i++) {
            if (listServicePriceChange.get(i).getPriceId() != null && listServicePriceChange.get(i).getPriceId().equals(priceId)) {
                return i;
            }
        }
        return -1;
    }

    public String numberPrice(String price) {
        int size = price.length();
        String resultAf = "";
        String resultBef = "";
        if (size >= 5) {
            if (price.contains("G") || price.contains("M")) {
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
        String strr = resultBef.substring(0, resultBef.length()) + resultAf;
        return strr;
    }


    public void changeStateAddServicePriceTab() {
        isAddServicePriceTab = "1";
        isEditServicePriceTab = "0";
        isViewServicePriceTab = "0";
        isListService = "0";
        isServicePriceTab = "0";
        checkAddServicePriceTab = false;
        dspServicePriceTab = new DSPServicePriceTab();
        dspServicePriceTab.setServiceId(serviceId);
        dspServicePriceTab.setCurrency("VND");
    }

    public void handleSaveServicePrice() throws Exception {
        if (validateSaveServicePrice()) {
            List<DSPServicePrice> listServicePriceChangeId = new ArrayList<>();
            List<DSPServicePrice> listServicePriceChangeNotId = new ArrayList<>();
            for (DSPServicePrice dsp : listServicePriceChange) {
                if (dsp.getPriceId() == null) {
                    listServicePriceChangeNotId.add(dsp);
                } else {
                    listServicePriceChangeId.add(dsp);
                }
            }
            for (DSPServicePrice dsp : listServicePriceChangeId) {
                if (dsp.getType() != null) {
                    if (UPDATE.equals(dsp.getType())) {
                        dspServicePriceModel.update(dsp);
                    } else if (DELETE.equals(dsp.getType())) {
                        dspServicePriceModel.delete(dsp);
                    }
                }
            }
            for (DSPServicePrice dsp : listServicePriceChangeNotId) {
                if (!DELETE.equals(dsp.getType())) {
                    Integer index = listServicePrice.indexOf(dsp);
                    Long id = dspServicePriceModel.insert(dsp);
                    dsp.setPriceId(id);
                    listServicePrice.set(index, dsp);
                }
            }
            listServicePriceChange = new ArrayList<>();
            if (listServicePrice.size() > 0) {
                for (DSPServicePrice dspOrder : listServicePrice) {
                    dspOrder.setType(null);
                    listServicePriceChange.add(dspOrder);
                }
            }
            isActionServicePrice = "0";
            ClientMessage.log(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "saveDataSuccess"));
        }
        resetTableFilters();
    }

    public void handleSaveOrderPolicy() throws Exception {
        if (validateSaveOrderPolicy()) {
            List<DSPOrderPolicy> listOrderPolicyChangeId = new ArrayList<>();
            List<DSPOrderPolicy> listOrderPolicyChangeNotId = new ArrayList<>();
            for (DSPOrderPolicy dsp : listOrderPolicyChange) {
                if (dsp.getId() == null) {
                    listOrderPolicyChangeNotId.add(dsp);
                } else {
                    listOrderPolicyChangeId.add(dsp);
                }
            }
            for (DSPOrderPolicy dsp : listOrderPolicyChangeId) {
                if (dsp.getType() != null) {
                    if (UPDATE.equals(dsp.getType())) {
                        dspOrderPolicyModel.update(dsp);
                    } else if (DELETE.equals(dsp.getType())) {
                        dspOrderPolicyModel.delete(dsp);
                    }
                }
            }
            for (DSPOrderPolicy dsp : listOrderPolicyChangeNotId) {
                if (!DELETE.equals(dsp.getType())) {
                    Integer index = listOrderPolicy.indexOf(dsp);
                    Long id = dspOrderPolicyModel.insert(dsp);
                    dsp.setId(id);
                    listOrderPolicy.set(index, dsp);
                }
            }
            listOrderPolicyChange = new ArrayList<>();
            if (listOrderPolicy.size() > 0) {
                for (DSPOrderPolicy dspOrder : listOrderPolicy) {
                    dspOrder.setType(null);
                    listOrderPolicyChange.add(dspOrder);
                }
            }
            isActionOrderPolicy = "0";
            ClientMessage.log(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "saveDataSuccess"));
        }
    }


    public void handleSaveServicePolicy() throws Exception {
        Map<KeyServicePolicy, List<DSPServicePolicy>> mapServicePolicyChange = new HashMap<>();
        for (DSPServicePolicy dto : listServicePolicyChange) {
            if (dto.getType() != null && !dto.getType().equals("")) {
                if (mapServicePolicyChange.size() == 0) {
                    List<DSPServicePolicy> lst = new ArrayList<>();
                    lst.add(dto);
                    KeyServicePolicy key = new KeyServicePolicy();
                    key.setPolicyName(dto.getPolicyName());
                    key.setServiceId(dto.getServiceId());
                    mapServicePolicyChange.put(key, lst);
                } else {
                    KeyServicePolicy keyInit = new KeyServicePolicy();
                    keyInit.setPolicyName(dto.getPolicyName());
                    keyInit.setServiceId(dto.getServiceId());
                    boolean checkKey = false;
                    for (KeyServicePolicy key : mapServicePolicyChange.keySet()) {
                        if (keyInit.equals(key)) {
                            mapServicePolicyChange.get(key).add(dto);
                            checkKey = true;
                            break;
                        }
                    }
                    if (!checkKey) {
                        List<DSPServicePolicy> lst = new ArrayList<>();
                        lst.add(dto);
                        mapServicePolicyChange.put(keyInit, lst);
                    }
                }
            }
        }
        if (mapServicePolicyChange.size() > 0) {
            for (KeyServicePolicy key : mapServicePolicyChange.keySet()) {
                if (mapServicePolicyChange.get(key).size() == 1) {
                    DSPServicePolicy dsp = mapServicePolicyChange.get(key).get(0);
                    if (INSERT.equals(dsp.getType())) {
                        dspServicePolicyModel.insert(dsp);
                    } else if (UPDATE.equals(dsp.getType())) {
                        dspServicePolicyModel.update(dsp);
                    } else if (DELETE.equals(dsp.getType())) {
                        dspServicePolicyModel.delete(dsp);
                    }
                } else {
                    boolean check = false;
                    for (DSPServicePolicy dspInit : listServicePolicy) {
                        if (dspInit.getPolicyName().equals(key.getPolicyName()) && dspInit.getServiceId() == key.getServiceId()) {
                            DSPServicePolicy dtoServicePolicy = new DSPServicePolicy();
                            dtoServicePolicy.setPolicyName(key.getPolicyName());
                            dtoServicePolicy.setServiceId(key.getServiceId());
                            List<DSPServicePolicy> lst = dspServicePolicyModel.getDSPServicePolicy(dtoServicePolicy);
                            if (lst.size() == 0) {
                                dspServicePolicyModel.insert(dspInit);
                            } else {
                                dspServicePolicyModel.update(dspInit);
                            }
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        DSPServicePolicy dtoServicePolicy = new DSPServicePolicy();
                        dtoServicePolicy.setPolicyName(key.getPolicyName());
                        dtoServicePolicy.setServiceId(key.getServiceId());
                        dspServicePolicyModel.delete(dtoServicePolicy);
                    }
                }
            }
        }
        listServicePolicyChange = new ArrayList<>();
        if (listServicePolicy.size() > 0) {
            for (DSPServicePolicy dspDto : listServicePolicy) {
                dspDto.setType(null);
                listServicePolicyChange.add(dspDto);
            }
        }
        ClientMessage.log(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "saveDataSuccess"));
    }

    private void initFilterOptionStatus() {
        mFilterOptionStatus = new SelectItem[4];
        mFilterOptionStatus[0] = new SelectItem("", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "all"));
        mFilterOptionStatus[1] = new SelectItem("1", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "enable"));
        mFilterOptionStatus[2] = new SelectItem("0", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "statusNotActive"));
        mFilterOptionStatus[3] = new SelectItem("2", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "disable"));
    }

    private void initFilterOptionStatusService() {
        mFilterOptionStatusService = new SelectItem[3];
        mFilterOptionStatusService[0] = new SelectItem("", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "all"));
        mFilterOptionStatusService[1] = new SelectItem("1", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "enable"));
        mFilterOptionStatusService[2] = new SelectItem("0", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "disable"));
    }

    private void initFilterOptionDefault() {
        mFilterOptionDefault = new SelectItem[3];
        mFilterOptionDefault[0] = new SelectItem("", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "all"));
        mFilterOptionDefault[1] = new SelectItem("1", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "yes"));
        mFilterOptionDefault[2] = new SelectItem("0", ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARESPONSOR", "no"));
    }

    public void handleCancelTableOrderPolicy() {
        isOrderPolicy = "1";
        isEditOrderPolicy = 0;
    }

    public void handleCancelTableServicePolicy() {
        isServicePolicy = "1";
        isEditServicePolicy = "0";
    }

    public void onFilterChange(FilterEvent filterEvent) {
        filterState = filterEvent.getFilterBy();
        listServiceFiltered = (List<DSPService>) filterEvent.getData();
    }

    public void onFilterChangeOrderPolicy(FilterEvent filterEvent) {
        filterStateOrderPolicy = filterEvent.getFilterBy();
        listOrderPolicyFiltered = (List<DSPOrderPolicy>) filterEvent.getData();
    }

    public void onFilterChangeServicePolicy(FilterEvent filterEvent) {
        filterStateServicePolicy = filterEvent.getFilterBy();
        listServicePolicyFiltered = (List<DSPServicePolicy>) filterEvent.getData();
    }

    public void onFilterChangeServicePrice(FilterEvent filterEvent) {
        filterStateServicePrice = filterEvent.getFilterBy();
        listServicePriceFiltered = (List<DSPServicePrice>) filterEvent.getData();
    }

    public void onFilterOrderPolicyTabChange(FilterEvent filterEvent) {
        filterStateOrderPolicyTab = filterEvent.getFilterBy();
        listOrderPolicyTabFiltered = (List<DSPOrderPolicyTab>) filterEvent.getData();
    }

    public void onFilterServicePriceTabChange(FilterEvent filterEvent) {
        filterStateServicePriceTab = filterEvent.getFilterBy();
        listServicePriceTabFiltered = (List<DSPServicePriceTab>) filterEvent.getData();
    }


    public void onFilterComOrder(FilterEvent filterEvent) {
        filterStateComOrder = filterEvent.getFilterBy();
        listCompanyTableFiltered = (List<DSPCompany>) filterEvent.getData();
    }


    public String getDateToStr(Date date) {
        return DateUtil.getDateStr(date);
    }

    @Override
    public void handleDelete() throws Exception {

    }

    public SelectItem[] getmFilterOptionStatusService() {
        return mFilterOptionStatusService;
    }

    public void setmFilterOptionStatusService(SelectItem[] mFilterOptionStatusService) {
        this.mFilterOptionStatusService = mFilterOptionStatusService;
    }

    public Map<String, FilterMeta> getFilterStateServicePriceTab() {
        return filterStateServicePriceTab;
    }

    public String getIsViewServicePriceTab() {
        return isViewServicePriceTab;
    }

    public List<DSPServicePrice> getListServicePriceChange() {
        return listServicePriceChange;
    }

    public void setListServicePriceChange(List<DSPServicePrice> listServicePriceChange) {
        this.listServicePriceChange = listServicePriceChange;
    }

    public void setIsViewServicePriceTab(String isViewServicePriceTab) {
        this.isViewServicePriceTab = isViewServicePriceTab;
    }

    public void setFilterStateServicePriceTab(Map<String, FilterMeta> filterStateServicePriceTab) {
        this.filterStateServicePriceTab = filterStateServicePriceTab;
    }

    public List<DSPServicePriceTab> getListServicePriceTabFiltered() {
        return listServicePriceTabFiltered;
    }

    public void setListServicePriceTabFiltered(List<DSPServicePriceTab> listServicePriceTabFiltered) {
        this.listServicePriceTabFiltered = listServicePriceTabFiltered;
    }

    public DSPOrderPolicyTab getDspOrderPolicyTab() {
        return dspOrderPolicyTab;
    }

    public void setDspOrderPolicyTab(DSPOrderPolicyTab dspOrderPolicyTab) {
        this.dspOrderPolicyTab = dspOrderPolicyTab;
    }

    public List<DSPCompany> getListCompanySelected() {
        return listCompanySelected;
    }

    public void setListCompanySelected(List<DSPCompany> listCompanySelected) {
        this.listCompanySelected = listCompanySelected;
    }

    public String getIsDisplayAddComServicePrice() {
        return isDisplayAddComServicePrice;
    }

    public void setIsDisplayAddComServicePrice(String isDisplayAddComServicePrice) {
        this.isDisplayAddComServicePrice = isDisplayAddComServicePrice;
    }

    public Map<String, FilterMeta> getFilterStateComOrder() {
        return filterStateComOrder;
    }

    public void setFilterStateComOrder(Map<String, FilterMeta> filterStateComOrder) {
        this.filterStateComOrder = filterStateComOrder;
    }

    public String getIsAddOrderPolicyTab() {
        return isAddOrderPolicyTab;
    }

    public void setIsAddOrderPolicyTab(String isAddOrderPolicyTab) {
        this.isAddOrderPolicyTab = isAddOrderPolicyTab;
    }

    public String getIsOrderPolicyTab() {
        return isOrderPolicyTab;
    }

    public void setIsOrderPolicyTab(String isOrderPolicyTab) {
        this.isOrderPolicyTab = isOrderPolicyTab;
    }

    public String getIsEditServicePrice() {
        return isEditServicePrice;
    }

    public void setIsEditServicePrice(String isEditServicePrice) {
        this.isEditServicePrice = isEditServicePrice;
    }

    public boolean getIsDisplayBtnConfirm() {
        return this.isADD || this.isEDIT || this.isCOPY;
    }

    public List<DSPCompany> getListCompanyAutoComSelected() {
        return listCompanyAutoComSelected;
    }

    public String getIsCopyServicePrice() {
        return isCopyServicePrice;
    }

    public void setIsCopyServicePrice(String isCopyServicePrice) {
        this.isCopyServicePrice = isCopyServicePrice;
    }

    public String getIsAddComServicePrice() {
        return isAddComServicePrice;
    }

    public void setIsAddComServicePrice(String isAddComServicePrice) {
        this.isAddComServicePrice = isAddComServicePrice;
    }

    public void setListCompanyAutoComSelected(List<DSPCompany> listCompanyAutoComSelected) {
        this.listCompanyAutoComSelected = listCompanyAutoComSelected;
    }

    public List<DSPService> getListService() {
        return listService;
    }

    public void setListService(List<DSPService> listService) {
        this.listService = listService;
    }

    public String getIsEditDetailServicePrice() {
        return isEditDetailServicePrice;
    }

    public void setIsEditDetailServicePrice(String isEditDetailServicePrice) {
        this.isEditDetailServicePrice = isEditDetailServicePrice;
    }

    public String getIsEditDetailOrderPolicy() {
        return isEditDetailOrderPolicy;
    }

    public void setIsEditDetailOrderPolicy(String isEditDetailOrderPolicy) {
        this.isEditDetailOrderPolicy = isEditDetailOrderPolicy;
    }

    public List<DSPService> getListServiceFiltered() {
        return listServiceFiltered;
    }

    public void setListServiceFiltered(List<DSPService> listServiceFiltered) {
        this.listServiceFiltered = listServiceFiltered;
    }

    public SelectItem[] getmFilterOptionStatus() {
        return mFilterOptionStatus;
    }

    public void setmFilterOptionStatus(SelectItem[] mFilterOptionStatus) {
        this.mFilterOptionStatus = mFilterOptionStatus;
    }

    public SelectItem[] getmFilterOptionDefault() {
        return mFilterOptionDefault;
    }

    public void setmFilterOptionDefault(SelectItem[] mFilterOptionDefault) {
        this.mFilterOptionDefault = mFilterOptionDefault;
    }

    public String getIsAddService() {
        return isAddService;
    }

    public void setIsAddService(String isAddService) {
        this.isAddService = isAddService;
    }

    public String getIsEditService() {
        return isEditService;
    }

    public void setIsEditService(String isEditService) {
        this.isEditService = isEditService;
    }

    public List<DSPPolicy> getListDspPolicyChange() {
        return listDspPolicyChange;
    }

    public void setListDspPolicyChange(List<DSPPolicy> listDspPolicyChange) {
        this.listDspPolicyChange = listDspPolicyChange;
    }

    public String getIsViewOrderPolicyTab() {
        return isViewOrderPolicyTab;
    }

    public void setIsViewOrderPolicyTab(String isViewOrderPolicyTab) {
        this.isViewOrderPolicyTab = isViewOrderPolicyTab;
    }

    public String getIsServicePolicy() {
        return isServicePolicy;
    }

    public void setIsServicePolicy(String isServicePolicy) {
        this.isServicePolicy = isServicePolicy;
    }

    public String getIsAddServicePrice() {
        return isAddServicePrice;
    }

    public void setIsAddServicePrice(String isAddServicePrice) {
        this.isAddServicePrice = isAddServicePrice;
    }

    public String getIsServicePrice() {
        return isServicePrice;
    }

    public void setIsServicePrice(String isServicePrice) {
        this.isServicePrice = isServicePrice;
    }

    public String getIsCopyOrderPolicy() {
        return isCopyOrderPolicy;
    }

    public void setIsCopyOrderPolicy(String isCopyOrderPolicy) {
        this.isCopyOrderPolicy = isCopyOrderPolicy;
    }

    public DSPService getDspService() {
        return dspService;
    }

    public void setDspService(DSPService dspService) {
        this.dspService = dspService;
    }

    public String getIsListService() {
        return isListService;
    }

    public void setIsListService(String isListService) {
        this.isListService = isListService;
    }

    public Map<String, FilterMeta> getFilterState() {
        return filterState;
    }

    public void setFilterState(Map<String, FilterMeta> filterState) {
        this.filterState = filterState;
    }

    public String getIsOrderPolicy() {
        return isOrderPolicy;
    }

    public List<DSPCompany> getListCompanyTable() {
        return listCompanyTable;
    }

    public void setListCompanyTable(List<DSPCompany> listCompanyTable) {
        this.listCompanyTable = listCompanyTable;
    }


    public void setIsOrderPolicy(String isOrderPolicy) {
        this.isOrderPolicy = isOrderPolicy;
    }

    public List<DSPOrderPolicy> getListOrderPolicy() {
        return listOrderPolicy;
    }

    public void setListOrderPolicy(List<DSPOrderPolicy> listOrderPolicy) {
        this.listOrderPolicy = listOrderPolicy;
    }

    public Map<String, FilterMeta> getFilterStateServicePrice() {
        return filterStateServicePrice;
    }

    public String getIsServicePriceTab() {
        return isServicePriceTab;
    }

    public void setIsServicePriceTab(String isServicePriceTab) {
        this.isServicePriceTab = isServicePriceTab;
    }

    public List<DSPServicePriceTab> getListServicePriceTab() {
        return listServicePriceTab;
    }

    public void setListServicePriceTab(List<DSPServicePriceTab> listServicePriceTab) {
        this.listServicePriceTab = listServicePriceTab;
    }

    public DSPServicePriceTab getDspServicePriceTab() {
        return dspServicePriceTab;
    }

    public void setDspServicePriceTab(DSPServicePriceTab dspServicePriceTab) {
        this.dspServicePriceTab = dspServicePriceTab;
    }

    public DSPServicePriceTabModel getDspServicePriceTabModel() {
        return dspServicePriceTabModel;
    }

    public void setDspServicePriceTabModel(DSPServicePriceTabModel dspServicePriceTabModel) {
        this.dspServicePriceTabModel = dspServicePriceTabModel;
    }

    public void setFilterStateServicePrice(Map<String, FilterMeta> filterStateServicePrice) {
        this.filterStateServicePrice = filterStateServicePrice;
    }

    public String getIsAddComOrder() {
        return isAddComOrder;
    }

    public void setIsAddComOrder(String isAddComOrder) {
        this.isAddComOrder = isAddComOrder;
    }

    public Map<String, FilterMeta> getFilterStateOrderPolicyTab() {
        return filterStateOrderPolicyTab;
    }

    public void setFilterStateOrderPolicyTab(Map<String, FilterMeta> filterStateOrderPolicyTab) {
        this.filterStateOrderPolicyTab = filterStateOrderPolicyTab;
    }

    public List<DSPOrderPolicy> getListOrderPolicyFiltered() {
        return listOrderPolicyFiltered;
    }

    public void setListOrderPolicyFiltered(List<DSPOrderPolicy> listOrderPolicyFiltered) {
        this.listOrderPolicyFiltered = listOrderPolicyFiltered;
    }

    public DSPOrderPolicy getDspOrderPolicy() {
        return dspOrderPolicy;
    }

    public void setDspOrderPolicy(DSPOrderPolicy dspOrderPolicy) {
        this.dspOrderPolicy = dspOrderPolicy;
    }

    public Map<String, FilterMeta> getFilterStateOrderPolicy() {
        return filterStateOrderPolicy;
    }

    public void setFilterStateOrderPolicy(Map<String, FilterMeta> filterStateOrderPolicy) {
        this.filterStateOrderPolicy = filterStateOrderPolicy;
    }

    public List<DSPCompany> getListCompanyAutoCom() {
        return listCompanyAutoCom;
    }

    public void setListCompanyAutoCom(List<DSPCompany> listCompanyAutoCom) {
        this.listCompanyAutoCom = listCompanyAutoCom;
    }

    public List<DSPOrderPolicy> getListOrderPolicyChange() {
        return listOrderPolicyChange;
    }

    public void setListOrderPolicyChange(List<DSPOrderPolicy> listOrderPolicyChange) {
        this.listOrderPolicyChange = listOrderPolicyChange;
    }

    public DSPServicePolicy getDspServicePolicy() {
        return dspServicePolicy;
    }

    public void setDspServicePolicy(DSPServicePolicy dspServicePolicy) {
        this.dspServicePolicy = dspServicePolicy;
    }

    public List<DSPServicePolicy> getListServicePolicy() {
        return listServicePolicy;
    }

    public void setListServicePolicy(List<DSPServicePolicy> listServicePolicy) {
        this.listServicePolicy = listServicePolicy;
    }

    public List<DSPServicePolicy> getListServicePolicyChange() {
        return listServicePolicyChange;
    }

    public String getIsAddServicePolicy() {
        return isAddServicePolicy;
    }

    public void setIsAddServicePolicy(String isAddServicePolicy) {
        this.isAddServicePolicy = isAddServicePolicy;
    }

    public Integer getIsEditOrderPolicy() {
        return isEditOrderPolicy;
    }

    public void setIsEditOrderPolicy(Integer isEditOrderPolicy) {
        this.isEditOrderPolicy = isEditOrderPolicy;
    }

    public String getIsEditServicePolicy() {
        return isEditServicePolicy;
    }

    public void setIsEditServicePolicy(String isEditServicePolicy) {
        this.isEditServicePolicy = isEditServicePolicy;
    }

    public void setListServicePolicyChange(List<DSPServicePolicy> listServicePolicyChange) {
        this.listServicePolicyChange = listServicePolicyChange;
    }

    public List<DSPServicePolicy> getListServicePolicyFiltered() {
        return listServicePolicyFiltered;
    }

    public void setListServicePolicyFiltered(List<DSPServicePolicy> listServicePolicyFiltered) {
        this.listServicePolicyFiltered = listServicePolicyFiltered;
    }

    public Map<String, FilterMeta> getFilterStateServicePolicy() {
        return filterStateServicePolicy;
    }

    public void setFilterStateServicePolicy(Map<String, FilterMeta> filterStateServicePolicy) {
        this.filterStateServicePolicy = filterStateServicePolicy;
    }

    public String getIsActionServicePrice() {
        return isActionServicePrice;
    }

    public void setIsActionServicePrice(String isActionServicePrice) {
        this.isActionServicePrice = isActionServicePrice;
    }

    public String getIsActionOrderPolicy() {
        return isActionOrderPolicy;
    }

    public void setIsActionOrderPolicy(String isActionOrderPolicy) {
        this.isActionOrderPolicy = isActionOrderPolicy;
    }

    public List<DSPCompany> getListCompanyTableFiltered() {
        return listCompanyTableFiltered;
    }

    public void setListCompanyTableFiltered(List<DSPCompany> listCompanyTableFiltered) {
        this.listCompanyTableFiltered = listCompanyTableFiltered;
    }

    public List<DSPServicePrice> getListServicePrice() {
        return listServicePrice;
    }

    public void setListServicePrice(List<DSPServicePrice> listServicePrice) {
        this.listServicePrice = listServicePrice;
    }

    public List<DSPServicePrice> getListServicePriceFiltered() {
        return listServicePriceFiltered;
    }

    public void setListServicePriceFiltered(List<DSPServicePrice> listServicePriceFiltered) {
        this.listServicePriceFiltered = listServicePriceFiltered;
    }

    public List<DSPCompanyExt> getListCompanyTableChange() {
        return listCompanyTableChange;
    }

    public void setListCompanyTableChange(List<DSPCompanyExt> listCompanyTableChange) {
        this.listCompanyTableChange = listCompanyTableChange;
    }

    public DSPServicePrice getDspServicePrice() {
        return dspServicePrice;
    }

    public void setDspServicePrice(DSPServicePrice dspServicePrice) {
        this.dspServicePrice = dspServicePrice;
    }

    public List<DSPOrderPolicyTab> getListOrderPolicyTab() {
        return listOrderPolicyTab;
    }

    public void setListOrderPolicyTab(List<DSPOrderPolicyTab> listOrderPolicyTab) {
        this.listOrderPolicyTab = listOrderPolicyTab;
    }

    public List<DSPOrderPolicyTab> getListOrderPolicyTabFiltered() {
        return listOrderPolicyTabFiltered;
    }

    public void setListOrderPolicyTabFiltered(List<DSPOrderPolicyTab> listOrderPolicyTabFiltered) {
        this.listOrderPolicyTabFiltered = listOrderPolicyTabFiltered;
    }

    public String getIsEditOrderPolicyTab() {
        return isEditOrderPolicyTab;
    }

    public void setIsEditOrderPolicyTab(String isEditOrderPolicyTab) {
        this.isEditOrderPolicyTab = isEditOrderPolicyTab;
    }


    private static class KeyServicePolicy {
        String policyName;
        Long serviceId;

        public KeyServicePolicy() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyServicePolicy)) return false;
            KeyServicePolicy that = (KeyServicePolicy) o;
            return Objects.equals(policyName, that.policyName) &&
                    Objects.equals(serviceId, that.serviceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(policyName, serviceId);
        }

        public KeyServicePolicy(String policyName, Long serviceId) {
            this.policyName = policyName;
            this.serviceId = serviceId;
        }

        public String getPolicyName() {
            return policyName;
        }


        public void setPolicyName(String policyName) {
            this.policyName = policyName;
        }

        public Long getServiceId() {
            return serviceId;
        }

        public void setServiceId(Long serviceId) {
            this.serviceId = serviceId;
        }
    }

    @Override
    public void handleOK() throws Exception {

    }
}
