package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.DateUtil;
import com.faplib.util.FileUtil;
import org.apache.commons.lang.SerializationUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.model.AddDataISDNModel;
import vn.com.telsoft.model.DSPOrderModel;
import vn.com.telsoft.model.DataCodeModel;
import vn.com.telsoft.model.DspServiceModel;
import vn.com.telsoft.util.CsvReader;
import vn.com.telsoft.util.ExcelCellEtt;
import vn.com.telsoft.util.ExcelUtil;
import vn.com.telsoft.util.Utils;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static vn.com.telsoft.controller.CreateOrderController.RESOURCE_BUNDLE;


@Named
@ViewScoped
public class AddDataISDNController extends TSFuncTemplate implements Serializable {
    static final Long SIZE_LIMIT = 2500000L;
    static final String DATA_SERVICE = "ADD";
    static final String DATA_GROUP_SERVICE = "ADD_GROUP";
    static final String CAP_MIN = "100MB";


    private String mflag;
    private AddDataISDNModel model;
    private DSPOrderModel modelOrder;
    private DataCodeModel modelDataCode;
    private String searchValues;
    private int render = 0;
    private int renderTable = 0;
    private int disableButton = 0;

    private Long lDataAmount;
    private String description;

    private AddDataISDNEntity addDataISDNEntity;
    private AddDataISDNEntity addDataISDNEntityTemp;

    private List<AddDataISDNEntity> lsRequest;
    private List<AddDataISDNEntity> lsUpload;
    private List<AddDataDDRequestEntity> lsPackageData;
    private List<AddDataDDRequestEntity> lsDDRequest;
    private List<DSPOrder> lsOrder;
    private List<DSPComPrice> lsComPrice;
    private List<DSPServicePriceTab> lsServicePriceTab;

    private List<DSPService> mlistDspService;
    private DspServiceModel dspServiceModel;
    private Long serviceId;
    private Long serviceIdGroup;
    private List<DSPServicePrice> lsPrice;
    private Long userID;
    private Long comType;
    private Long comID;
    private String valueButton;
    private Long totalRemainValues;
    private Long inputAmountData;
    private Long inputAmountDataTemp;
    private String pattern = "###,###.###";
    private DecimalFormat decimalFormat;

    private DspOrderTransactionEntity orderTranEntity;


    public AddDataISDNController() throws Exception {
        decimalFormat = new DecimalFormat(pattern);
        userID = AdminUser.getUserLogged().getUserId();
        model = new AddDataISDNModel();
        modelOrder = new DSPOrderModel();
        modelDataCode = new DataCodeModel();
        dspServiceModel = new DspServiceModel();
        addDataISDNEntity = new AddDataISDNEntity();
        orderTranEntity = new DspOrderTransactionEntity();
        lsRequest = new ArrayList<>();
        lsUpload = new ArrayList<>();
        lsPackageData = new ArrayList<>();
        lsDDRequest = new ArrayList<>();
        lsComPrice = new ArrayList<>();
        lsServicePriceTab = new ArrayList<>();
        mlistDspService = new ArrayList<>();
        lsOrder = new ArrayList<>();
        getLink();
        comType = model.checkUserType(userID);
        comID = model.checkComID(userID);
        lsRequest = model.getListTrans(userID, serviceId, serviceIdGroup);

    }

    public void onView(AddDataISDNEntity ent) throws Exception {
        render = 4;
        lsDDRequest = model.getLsDDrequest(ent.getTransactionID());
    }

    public void onCancelView() {
        render = 0;
    }

    public void getLink() throws Exception {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String parameter = params.get("userModule");
        if (parameter.endsWith("_dn")) {
            parameter = parameter.substring(0, parameter.length() - 3);
        }
        DSPService dsp = new DSPService();
        dsp.setPath(parameter);
        mlistDspService = dspServiceModel.getDSPService(dsp);
        if (mlistDspService.size() > 0) {
            if (DATA_SERVICE.equals(mlistDspService.get(0).getServiceCode())) {
                serviceId = mlistDspService.get(0).getServiceId();
            }
            if (DATA_GROUP_SERVICE.equals(mlistDspService.get(0).getServiceCode())) {
                serviceIdGroup = mlistDspService.get(0).getServiceId();
            }
            if (mlistDspService.size() > 1) {
                if (DATA_SERVICE.equals(mlistDspService.get(1).getServiceCode())) {
                    serviceId = mlistDspService.get(1).getServiceId();
                }
                if (DATA_GROUP_SERVICE.equals(mlistDspService.get(1).getServiceCode())) {
                    serviceIdGroup = mlistDspService.get(1).getServiceId();
                }
            }
        }
    }

    @Override
    public void handleOK() throws Exception {
        List<DSPOrder> lsDataOrder = new ArrayList<>();
        List<DspOrderTransactionEntity> lsDataOrderTransaction = new ArrayList<>();
        orderTranEntity = new DspOrderTransactionEntity();
        Long lDataAmountInsert = 0L;
        try {
            if ("single".equals(mflag)) {
                lsOrder = modelOrder.getListOrderForTrans(comID);
                totalRemainValues = modelOrder.getTotalRemainOrder(comID);
                lDataAmountInsert = lDataAmount;
                if (lDataAmount > totalRemainValues) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN",
                                    "not_enough_remain") + decimalFormat.format(totalRemainValues) + "VND");
                    return;
                }


                for (DSPOrder dspOrder : lsOrder) {
                    orderTranEntity = new DspOrderTransactionEntity();
                    if (lDataAmount > dspOrder.getRemainQuotaValue() && dspOrder.getRemainQuotaValue() != 0) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + dspOrder.getRemainQuotaValue());
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(lsUpload.get(0).getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(userID);
                        orderTranEntity.setAmount(dspOrder.getRemainQuotaValue());
                        lsDataOrderTransaction.add(orderTranEntity);
                        lDataAmount = lDataAmount - dspOrder.getRemainQuotaValue();
                    } else if (lDataAmount < dspOrder.getRemainQuotaValue()) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(lsUpload.get(0).getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(userID);
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    } else if (lDataAmount.equals(dspOrder.getRemainQuotaValue())) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(lsUpload.get(0).getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(userID);
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    }
                }
                model.insertGroup(lsServicePriceTab.get(0).getTabId(), serviceId, lDataAmountInsert, description, lsUpload, null, lsDataOrder, lsDataOrderTransaction);
                //Message to client
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "creat_success"));
                ClientMessage.logAdd();
            }

            if ("group".equals(mflag)) {
                if ((inputAmountData * 1024 * 1024) != (inputAmountDataTemp)) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_amount_wrong"));
                    return;
                }
                lsOrder = modelOrder.getListOrderForTrans(comID);
                totalRemainValues = modelOrder.getTotalRemainOrder(comID);
                if (lDataAmount > totalRemainValues) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN",
                                    "not_enough_remain") + decimalFormat.format(totalRemainValues) + "VND");
                    return;
                }
                lDataAmountInsert = lDataAmount;
                for (DSPOrder dspOrder : lsOrder) {
                    orderTranEntity = new DspOrderTransactionEntity();
                    if (lDataAmount > dspOrder.getRemainQuotaValue() && dspOrder.getRemainQuotaValue() != 0) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + dspOrder.getRemainQuotaValue());
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(lsUpload.get(0).getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(userID);
                        orderTranEntity.setAmount(dspOrder.getRemainQuotaValue());
                        lsDataOrderTransaction.add(orderTranEntity);
                        lDataAmount = lDataAmount - dspOrder.getRemainQuotaValue();
                    } else if (lDataAmount < dspOrder.getRemainQuotaValue()) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(lsUpload.get(0).getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(userID);
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    } else if (lDataAmount.equals(dspOrder.getRemainQuotaValue())) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(lsUpload.get(0).getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(userID);
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    }
                }
                model.insertGroup(lsServicePriceTab.get(0).getTabId(), serviceIdGroup, lDataAmountInsert, description, lsUpload, inputAmountData, lsDataOrder, lsDataOrderTransaction);
                //Message to client
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "creat_success"));
                ClientMessage.logAdd();
            }
            if ("singleUpdate".equals(mflag)) {
                totalRemainValues = modelOrder.getTotalRemainOrder(comID);
                if (lDataAmount > totalRemainValues + CostBefore) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN",
                                    "not_enough_remain") + decimalFormat.format(totalRemainValues) + "VND");
                    return;
                }
                lsOrder = modelOrder.getListOrderForTrans(comID);
                List<DSPOrderTransaction> mlistDspOrderTransactionOld = modelDataCode.getlistOrderTransaction(userID, addDataISDNEntity.getTransactionID());
                for (DSPOrder dspOrder : lsOrder) {
                    Long orderTranAmount = model.getAmountOrderTrans(dspOrder.getOrderId(), addDataISDNEntity.getTransactionID());
                    if (orderTranAmount != null && orderTranAmount > 0) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() - orderTranAmount);
                    }
                }
                for (DSPOrder dspOrder : lsOrder) {
                    orderTranEntity = new DspOrderTransactionEntity();
                    if (lDataAmount > dspOrder.getRemainQuotaValue() && dspOrder.getRemainQuotaValue() != 0) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + dspOrder.getRemainQuotaValue());
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(addDataISDNEntity.getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(AdminUser.getUserLogged().getUserId());
                        orderTranEntity.setAmount(dspOrder.getRemainQuotaValue());
                        lsDataOrderTransaction.add(orderTranEntity);
                        lDataAmount = lDataAmount - dspOrder.getRemainQuotaValue();
                    } else if (lDataAmount < dspOrder.getRemainQuotaValue()) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(addDataISDNEntity.getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(AdminUser.getUserLogged().getUserId());
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    } else if (lDataAmount.equals(dspOrder.getRemainQuotaValue())) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(addDataISDNEntity.getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(AdminUser.getUserLogged().getUserId());
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    }
                }
                List<DSPOrder> listDspOrderChange = new ArrayList<>();
                DSPOrder mtmpDSPOrder;

                for (DspOrderTransactionEntity dspOrderTransaction : lsDataOrderTransaction) {
                    mtmpDSPOrder = new DSPOrder();
                    mtmpDSPOrder.setOrderId(dspOrderTransaction.getOderID());
                    mtmpDSPOrder.setReservedValue(dspOrderTransaction.getAmount());
                    listDspOrderChange.add(mtmpDSPOrder);
                }

                boolean bExist;
                for (DSPOrderTransaction dspOrderTransactionOld : mlistDspOrderTransactionOld) {
                    bExist = false;
                    for (DSPOrder dspOrder : listDspOrderChange) {
                        if (dspOrder.getOrderId().equals(dspOrderTransactionOld.getOrderId()) && dspOrder.getReservedValue().equals(dspOrderTransactionOld.getAmount())) {
                            bExist = true;
                            listDspOrderChange.remove(dspOrder);
                            break;
                        }
                        if (dspOrder.getOrderId().equals(dspOrderTransactionOld.getOrderId())) {
                            bExist = true;
                            dspOrder.setReservedValue(dspOrder.getReservedValue() - dspOrderTransactionOld.getAmount());
                            break;
                        }
                    }
                    if (!bExist) {
                        mtmpDSPOrder = new DSPOrder();
                        mtmpDSPOrder.setOrderId(dspOrderTransactionOld.getOrderId());
                        mtmpDSPOrder.setReservedValue(0 - dspOrderTransactionOld.getAmount());
                        listDspOrderChange.add(mtmpDSPOrder);
                    }
                }
//                model.updateInfo(AmountbfChange, dspOrdersUpdate, comID, serviceId, addDataISDNEntity.getTransactionID(), addDataISDNEntity.getStatus(),
//                        lDataAmount, description, lsUpload);
                model.updateInfo(listDspOrderChange, lsDataOrderTransaction, addDataISDNEntity,
                        lDataAmount, description, lsUpload);
                //Message to client
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "update_success"));
                ClientMessage.logUpdate();
            }
            if ("groupUpdate".equals(mflag)) {
                if ((inputAmountData * 1024 * 1024) != (inputAmountDataTemp)) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_amount_wrong"));
                    return;
                }
                totalRemainValues = modelOrder.getTotalRemainOrder(comID);
                if (lDataAmount > totalRemainValues) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN",
                                    "not_enough_remain") + decimalFormat.format(totalRemainValues) + "VND");
                    return;
                }
                lsOrder = modelOrder.getListOrderForTrans(comID);
                List<DSPOrderTransaction> mlistDspOrderTransactionOld = modelDataCode.getlistOrderTransaction(userID, addDataISDNEntity.getTransactionID());

                for (DSPOrder dspOrder : lsOrder) {
                    Long orderTranAmount = model.getAmountOrderTrans(dspOrder.getOrderId(), addDataISDNEntity.getTransactionID());
                    if (orderTranAmount != null && orderTranAmount != 0) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() - orderTranAmount);
                    }
                }
                orderTranEntity = new DspOrderTransactionEntity();
                for (DSPOrder dspOrder : lsOrder) {
                    if (lDataAmount > dspOrder.getRemainQuotaValue() && dspOrder.getRemainQuotaValue() != 0) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + dspOrder.getRemainQuotaValue());
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(addDataISDNEntity.getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(AdminUser.getUserLogged().getUserId());
                        orderTranEntity.setAmount(dspOrder.getRemainQuotaValue());
                        lsDataOrderTransaction.add(orderTranEntity);
                        lDataAmount = lDataAmount - dspOrder.getRemainQuotaValue();
                    } else if (lDataAmount < dspOrder.getRemainQuotaValue()) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(addDataISDNEntity.getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(AdminUser.getUserLogged().getUserId());
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    } else if (lDataAmount.equals(dspOrder.getRemainQuotaValue())) {
                        dspOrder.setReservedValue(dspOrder.getReservedValue() + lDataAmount);
                        lsDataOrder.add(dspOrder);
                        orderTranEntity.setOderID(dspOrder.getOrderId());
                        orderTranEntity.setIssueTime(addDataISDNEntity.getRequestTime());
                        orderTranEntity.setDescription(dspOrder.getDescription());
                        orderTranEntity.setUserId(AdminUser.getUserLogged().getUserId());
                        orderTranEntity.setAmount(lDataAmount);
                        lsDataOrderTransaction.add(orderTranEntity);
                        break;
                    }
                }
                List<DSPOrder> listDspOrderChange = new ArrayList<>();
                DSPOrder mtmpDSPOrder;

                for (DspOrderTransactionEntity dspOrderTransaction : lsDataOrderTransaction) {
                    mtmpDSPOrder = new DSPOrder();
                    mtmpDSPOrder.setOrderId(dspOrderTransaction.getOderID());
                    mtmpDSPOrder.setReservedValue(dspOrderTransaction.getAmount());
                    listDspOrderChange.add(mtmpDSPOrder);
                }

                boolean bExist;
                for (DSPOrderTransaction dspOrderTransactionOld : mlistDspOrderTransactionOld) {
                    bExist = false;
                    for (DSPOrder dspOrder : listDspOrderChange) {
                        if (dspOrder.getOrderId().equals(dspOrderTransactionOld.getOrderId()) && dspOrder.getReservedValue().equals(dspOrderTransactionOld.getAmount())) {
                            bExist = true;
                            listDspOrderChange.remove(dspOrder);
                            break;
                        }
                        if (dspOrder.getOrderId().equals(dspOrderTransactionOld.getOrderId())) {
                            bExist = true;
                            dspOrder.setReservedValue(dspOrder.getReservedValue() - dspOrderTransactionOld.getAmount());
                            break;
                        }
                    }
                    if (!bExist) {
                        mtmpDSPOrder = new DSPOrder();
                        mtmpDSPOrder.setOrderId(dspOrderTransactionOld.getOrderId());
                        mtmpDSPOrder.setReservedValue(0 - dspOrderTransactionOld.getAmount());
                        listDspOrderChange.add(mtmpDSPOrder);
                    }
                }
                model.updateInfoGroup(listDspOrderChange, lsDataOrderTransaction, comID, serviceId, addDataISDNEntity.getTransactionID(), addDataISDNEntity.getStatus(), lDataAmount, description, lsUpload, inputAmountData);
                //Message to client
                ClientMessage.logUpdate();
            }
            lsRequest = model.getListTrans(userID, serviceId, serviceIdGroup);
            lsUpload = new ArrayList<>();
            render = 0;
            disableButton = 0;
            PrimeFaces.current().executeScript("PF('tableRequest').clearFilters();");
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            throw ex;
        }
    }

    public void onCancel() {
        render = 0;
    }

    @Override
    public void handleDelete() throws Exception {

    }

    //////////////////////////////////////////////////////////////////////////////////
    public String getDateToStr(Date date) {
        return DateUtil.getDateStr(date);
    }

    private Long CostBefore;

    public void onEdit(AddDataISDNEntity ent) {
        addDataISDNEntity = (AddDataISDNEntity) SerializationUtils.clone(ent);

        addDataISDNEntityTemp = (AddDataISDNEntity) SerializationUtils.clone(ent);
        disableButton = 1;
        valueButton = ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "update");
        inputAmountDataTemp = ent.getDataAmount();
        CostBefore = ent.getAmount();

        if (addDataISDNEntity.getServiceID().equals(serviceId)) {
            mflag = "singleUpdate";
            render = 1;
            description = addDataISDNEntity.getDescription();
            lDataAmount = addDataISDNEntity.getAmount();
        } else {
            mflag = "groupUpdate";
            render = 2;
            description = addDataISDNEntity.getDescription();
            lDataAmount = addDataISDNEntity.getAmount();
            inputAmountData = addDataISDNEntity.getDataAmount() / 1024 / 1024;
        }
    }

    public void onShow() throws Exception {
        renderTable = 1;
        lsPackageData = model.getListPackageData(addDataISDNEntity.getTransactionID());
    }

    public void onTransferApprove(AddDataISDNEntity ent) throws Exception {
        addDataISDNEntity = (AddDataISDNEntity) SerializationUtils.clone(ent);
        addDataISDNEntity.setStatus("1");
        model.updateStatus(addDataISDNEntity, null);
        lsRequest = model.getListTrans(userID, serviceId, serviceIdGroup);
        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "transfer_success"));
    }

    public void onDel(AddDataISDNEntity ent) throws Exception {
        List<DSPOrder> lsOrderUpdate = new ArrayList<>();
        addDataISDNEntity = (AddDataISDNEntity) SerializationUtils.clone(ent);
        addDataISDNEntity.setStatus("4");

        //Cập nhật dsp_order
        lsOrder = modelOrder.getListOrderForTrans(comID);
        for (DSPOrder dspOrder : lsOrder) {
            Long orderTranAmount = model.getAmountOrderTrans(dspOrder.getOrderId(), ent.getTransactionID());
            if (orderTranAmount != null && orderTranAmount != 0) {
                dspOrder.setReservedValue(dspOrder.getReservedValue() - orderTranAmount);
                lsOrderUpdate.add(dspOrder);
            }
        }
        model.updateStatus(addDataISDNEntity, lsOrderUpdate);
        lsRequest = model.getListTrans(userID, serviceId, serviceIdGroup);
        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "del_success"));
    }

    public void onConfirm(AddDataISDNEntity ent) throws Exception {
        List<DSPOrder> lsOrderUpdate = new ArrayList<>();
        addDataISDNEntity = (AddDataISDNEntity) SerializationUtils.clone(ent);
        addDataISDNEntity.setStatus("6");
        //Cập nhật dsp_order
        lsOrder = modelOrder.getListOrderForTrans(comID);
        for (DSPOrder dspOrder : lsOrder) {
            Long orderTranAmount = model.getAmountOrderTrans(dspOrder.getOrderId(), ent.getTransactionID());
            if (orderTranAmount != null && orderTranAmount != 0) {
                dspOrder.setRemainValue(dspOrder.getRemainValue() - orderTranAmount);
                dspOrder.setReservedValue(dspOrder.getReservedValue() - orderTranAmount);
                lsOrderUpdate.add(dspOrder);
            }
        }
        model.updateStatus(addDataISDNEntity, lsOrderUpdate);
        lsRequest = model.getListTrans(userID, serviceId, serviceIdGroup);
        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "confirm_success"));
    }

    public void onApprove(AddDataISDNEntity ent) throws Exception {
        render = 3;
        addDataISDNEntity = (AddDataISDNEntity) SerializationUtils.clone(ent);
    }

    public void doApprove(AddDataISDNEntity ent) throws Exception {
        addDataISDNEntity = (AddDataISDNEntity) SerializationUtils.clone(ent);
        addDataISDNEntity.setStatus("2");
        List<AddDataDDRequestEntity> lsDD = new ArrayList<>();
        lsDD = model.getLsDDrequest(addDataISDNEntity.getTransactionID());
        model.updateStatusApprove(addDataISDNEntity, lsDD);
        lsRequest = model.getListTrans(userID, serviceId, serviceIdGroup);

        if (addDataISDNEntity.getStatus().equals("2")) {
            //Message to client
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "confirm_success"));
        } else {
            //Message to client
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "reject_approve_success"));
        }
        render = 0;
        renderTable = 0;
    }

    public void onCancelApprove() {
        render = 0;
        renderTable = 0;
    }

    public void onCreat() throws Exception {
        disableButton = 0;
        addDataISDNEntity = new AddDataISDNEntity();
        lDataAmount = 0L;
        description = "";
        inputAmountData = null;
        render = 1;
        mflag = "single";
        valueButton = ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "ok");
    }

    public void onCreatGroup() {
        disableButton = 0;
        addDataISDNEntity = new AddDataISDNEntity();
        lDataAmount = 0L;
        description = "";
        inputAmountData = null;
        inputAmountDataTemp = 0L;
        render = 2;
        mflag = "group";
        valueButton = ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "ok");
    }

    String checkISDN;

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        CsvReader reader = null;
        String strFilePath = "";
        String strFileName = "";
        Long lCapacity = null;
        Long lActiveDays;
        Long lblock;
        Double numBlock = null;
        Double lTotalAmount = null;
        try {
            lsUpload = new ArrayList<>();
            lDataAmount = 0L;
//            File file = new File(FileUtil.uploadTempFile(event.getFile()));
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
//            strFilePath = file.getPath();
//            strFileName = file.getName();
            reader = new CsvReader(file.getPath(), '-');
            if ("text/plain".equals(event.getFile().getContentType())) {
                int i = 0;
                if (event.getFile().getSize() > SIZE_LIMIT) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_too_large"));
                    return;
                }
                if (event.getFile().getSize() <= 0) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_empty"));
                    return;
                }

                lsComPrice = model.getListComPrice(comID);
                if (lsComPrice.size() <= 0) {
                    lsServicePriceTab = model.getListServicePriceTab(null, serviceId, 1L);
                } else {
                    List<Long> lsTab = new ArrayList<>();
                    for (DSPComPrice dspComPrice : lsComPrice) {
                        lsTab.add(dspComPrice.getTabId());
                    }
                    lsServicePriceTab = model.getListServicePriceTab(lsTab, serviceId, 0L);
                    if (lsServicePriceTab.size() <= 0) {
                        lsServicePriceTab = model.getListServicePriceTab(null, serviceId, 1L);
                    }
                }
                if (lsServicePriceTab.size() <= 0) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "tab_none"));
                    return;
                }
                List<Long> lsTab1 = new ArrayList<>();
                lsTab1.add(lsServicePriceTab.get(0).getTabId());

                lsPrice = model.getPrice(lsTab1);

                while (reader.readRecord()) {
                    i++;
                    String strRecords[] = reader.getValues();
                    if (strRecords.length != 3) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                        addDataISDNEntity = new AddDataISDNEntity();
                        lDataAmount = 0L;
                        return;
                    }
                    String strISDN = strRecords[0];
                    String strSuffixes = strRecords[1].substring(strRecords[1].length() - 2);
                    String strCapacity = strRecords[1].substring(0, strRecords[1].length() - 2).trim();

                    checkISDN = strISDN;
                    if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(this.checkISDN))) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                                ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "isdn_fail")
                                        + String.valueOf(i));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    if (!strSuffixes.equalsIgnoreCase("MB") && !strSuffixes.equalsIgnoreCase("GB")) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    try {
                        lCapacity = Long.parseLong(strCapacity);
                        lActiveDays = Long.parseLong(strRecords[2]);
                    } catch (Exception ex) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }

                    //qui đổi ra kb
                    if (strSuffixes.equalsIgnoreCase("MB")) {
                        lCapacity = lCapacity * 1024;
                    }
                    if (strSuffixes.equalsIgnoreCase("GB")) {
                        lCapacity = lCapacity * (1024 * 1024);
                    }
                    //Check mức dung lượng tối thiểu
                    if (lCapacity < 100 * 1024) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file.invalid") + " " + CAP_MIN);
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    //Check ngày sử dựng
                    for (DSPServicePrice dspServicePrice : lsPrice) {
                        if (dspServicePrice.getCapMin() != null && dspServicePrice.getCapMax() != null) {
                            if (lCapacity < dspServicePrice.getCapMax() && lCapacity >= dspServicePrice.getCapMin()) {
                                if (lActiveDays > dspServicePrice.getActiveDay()) {
                                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_fail")
                                                    + String.valueOf(i) + ", "
                                                    + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_max")
                                                    + dspServicePrice.getActiveDay().toString());
                                    if (mflag.equals("singleUpdate")) {
                                        addDataISDNEntity = addDataISDNEntityTemp;
                                        description = addDataISDNEntity.getDescription();
                                        lDataAmount = addDataISDNEntity.getAmount();
                                        lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                                    } else {
                                        addDataISDNEntity = new AddDataISDNEntity();
                                        lDataAmount = 0L;
                                    }
                                    return;
                                }
                                break;
                            }
                            if (dspServicePrice.getCapMax() == 0 && lCapacity >= dspServicePrice.getCapMin()) {
                                if (lActiveDays > dspServicePrice.getActiveDay()) {
                                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_fail")
                                                    + String.valueOf(i) + ", "
                                                    + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_max")
                                                    + dspServicePrice.getActiveDay().toString());
                                    if (mflag.equals("singleUpdate")) {
                                        addDataISDNEntity = addDataISDNEntityTemp;
                                        description = addDataISDNEntity.getDescription();
                                        lDataAmount = addDataISDNEntity.getAmount();
                                        lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                                    } else {
                                        addDataISDNEntity = new AddDataISDNEntity();
                                        lDataAmount = 0L;
                                    }
                                    return;
                                }
                                break;
                            }
                        }
                    }
                    for (DSPServicePrice dspServicePrice : lsPrice) {

                        lblock = lsServicePriceTab.get(0).getBlock();
                        numBlock = Double.valueOf(lCapacity) / Double.valueOf(lblock);
//                        if (0 != lCapacity % lblock) {
//                            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
//                                    ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "capacity_fail")
//                                            + String.valueOf(i));
//                            if (mflag.equals("singleUpdate")) {
//                                addDataISDNEntity = addDataISDNEntityTemp;
//                                description = addDataISDNEntity.getDescription();
//                                lDataAmount = addDataISDNEntity.getAmount();
//                                lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
//                            } else {
//                                addDataISDNEntity = new AddDataISDNEntity();
//                                lDataAmount = 0L;
//                            }
//                            return;
//                        }

                        if (dspServicePrice.getCapMin() != null && dspServicePrice.getCapMax() != null) {
                            if (lCapacity < dspServicePrice.getCapMax() && lCapacity >= dspServicePrice.getCapMin()) {
                                lTotalAmount = numBlock * dspServicePrice.getPrice();
                                break;
                            }
                        }
                        if (dspServicePrice.getCapMax() == null || dspServicePrice.getCapMax() == 0) {
                            if (lCapacity >= dspServicePrice.getCapMin()) {
                                lTotalAmount = numBlock * dspServicePrice.getPrice();
                                break;
                            }
                        }
                    }

                    if (lTotalAmount == null) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data.invalid"));
                        lDataAmount = 0L;
                        break;
                    }

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    addDataISDNEntity.setRequestTime(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_MONTH, +3);
                    addDataISDNEntity.setPlanTime(calendar.getTime());
                    String fromdate = df.format(addDataISDNEntity.getPlanTime());
                    addDataISDNEntity.setPlanTime(df.parse(fromdate));
                    addDataISDNEntity.setUserID(AdminUser.getUserLogged().getUserId());
                    addDataISDNEntity.setISDN(Utils.fixIsdnWithout0and84(strISDN));
                    addDataISDNEntity.setAmountRequest(lCapacity);
                    addDataISDNEntity.setActiveDay(lActiveDays);
                    addDataISDNEntity.setReqCost(Math.round(lTotalAmount));

                    AddDataISDNEntity dataTemp = new AddDataISDNEntity(addDataISDNEntity);
                    lsUpload.add(dataTemp);
                    lDataAmount += Math.round(lTotalAmount);
                    if (lDataAmount == null || lDataAmount == 0.0) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data.invalid"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    if (lDataAmount.toString().length() > 15) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "total_out"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }

                }

                disableButton = 1;
            } else {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_invalid"));
//                return;
                File inputWorkbook = null;
//                inputWorkbook = new File(FileUtil.uploadTempFile(event.getFile()));
                inputWorkbook = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
                List excelResult = ExcelUtil.read(inputWorkbook);

                if (event.getFile().getSize() > SIZE_LIMIT) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_too_large"));
                    return;
                }
                if (event.getFile().getSize() <= 0) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_empty"));
                    return;
                }

                lsComPrice = model.getListComPrice(comID);
                if (lsComPrice.size() <= 0) {
                    lsServicePriceTab = model.getListServicePriceTab(null, serviceId, 1L);
                } else {
                    List<Long> lsTab = new ArrayList<>();
                    for (DSPComPrice dspComPrice : lsComPrice) {
                        lsTab.add(dspComPrice.getTabId());
                    }
                    lsServicePriceTab = model.getListServicePriceTab(lsTab, serviceId, 0L);
                    if (lsServicePriceTab.size() <= 0) {
                        lsServicePriceTab = model.getListServicePriceTab(null, serviceId, 1L);
                    }
                }
                if (lsServicePriceTab.size() <= 0) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "tab_none"));
                    return;
                }
                List<Long> lsTab1 = new ArrayList<>();
                lsTab1.add(lsServicePriceTab.get(0).getTabId());

                lsPrice = model.getPrice(lsTab1);

                for (int i = 0; i < excelResult.size(); i++) {
                    lTotalAmount = null;
                    List<ExcelCellEtt> row = (List<ExcelCellEtt>) excelResult.get(i);
                    //Get values
                    String strISDN = row.get(0).getStringValue();
                    String strCapacity = row.get(1).getStringValue();
                    String strActiveDay = row.get(2).getStringValue();

                    String strSuffixes = strCapacity.substring(strCapacity.length() - 2);
                    strCapacity = strCapacity.substring(0, strCapacity.length() - 2).trim();

                    if (strISDN == null && strCapacity == null && strActiveDay == null) {
                        continue;
                        // thieu du lieu
                    }

                    checkISDN = strISDN;
                    if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(this.checkISDN))) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                                ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "isdn_fail")
                                        + String.valueOf(i));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    if (!strSuffixes.equalsIgnoreCase("MB") && !strSuffixes.equalsIgnoreCase("GB")) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    try {
                        lCapacity = Long.parseLong(strCapacity);
                        lActiveDays = Long.parseLong(strActiveDay);
                    } catch (Exception ex) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }

                    //qui đổi ra kb
                    if (strSuffixes.equalsIgnoreCase("MB")) {
                        lCapacity = lCapacity * 1024;
                    }
                    if (strSuffixes.equalsIgnoreCase("GB")) {
                        lCapacity = lCapacity * (1024 * 1024);
                    }
                    //Check mức dung lượng tối thiểu
                    if (lCapacity < 100 * 1024) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file.invalid") + " " + CAP_MIN);
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    //Check ngày sử dựng
                    for (DSPServicePrice dspServicePrice : lsPrice) {
                        if (dspServicePrice.getCapMin() != null && dspServicePrice.getCapMax() != null) {
                            if (lCapacity < dspServicePrice.getCapMax() && lCapacity >= dspServicePrice.getCapMin()) {
                                if (lActiveDays > dspServicePrice.getActiveDay()) {
                                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_fail")
                                                    + String.valueOf(i) + ", "
                                                    + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_max")
                                                    + dspServicePrice.getActiveDay().toString());
                                    if (mflag.equals("singleUpdate")) {
                                        addDataISDNEntity = addDataISDNEntityTemp;
                                        description = addDataISDNEntity.getDescription();
                                        lDataAmount = addDataISDNEntity.getAmount();
                                        lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                                    } else {
                                        addDataISDNEntity = new AddDataISDNEntity();
                                        lDataAmount = 0L;
                                    }
                                    return;
                                }
                                break;
                            }
                            if (dspServicePrice.getCapMax() == 0 && lCapacity >= dspServicePrice.getCapMin()) {
                                if (lActiveDays > dspServicePrice.getActiveDay()) {
                                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                                            ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_fail")
                                                    + String.valueOf(i) + ", "
                                                    + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_max")
                                                    + dspServicePrice.getActiveDay().toString());
                                    if (mflag.equals("singleUpdate")) {
                                        addDataISDNEntity = addDataISDNEntityTemp;
                                        description = addDataISDNEntity.getDescription();
                                        lDataAmount = addDataISDNEntity.getAmount();
                                        lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                                    } else {
                                        addDataISDNEntity = new AddDataISDNEntity();
                                        lDataAmount = 0L;
                                    }
                                    return;
                                }
                                break;
                            }
                        }
                    }
                    for (DSPServicePrice dspServicePrice : lsPrice) {

                        lblock = lsServicePriceTab.get(0).getBlock();
                        numBlock = Double.valueOf(lCapacity) / Double.valueOf(lblock);
//                        if (0 != lCapacity % lblock) {
//                            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
//                                    ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "capacity_fail")
//                                            + String.valueOf(i));
//                            if (mflag.equals("singleUpdate")) {
//                                addDataISDNEntity = addDataISDNEntityTemp;
//                                description = addDataISDNEntity.getDescription();
//                                lDataAmount = addDataISDNEntity.getAmount();
//                                lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
//                            } else {
//                                addDataISDNEntity = new AddDataISDNEntity();
//                                lDataAmount = 0L;
//                            }
//                            return;
//                        }

                        if (dspServicePrice.getCapMin() != null && dspServicePrice.getCapMax() != null) {
                            if (lCapacity < dspServicePrice.getCapMax() && lCapacity >= dspServicePrice.getCapMin()) {
                                lTotalAmount = numBlock * dspServicePrice.getPrice();
                                break;
                            }
                        }
                        if (dspServicePrice.getCapMax() == null || dspServicePrice.getCapMax() == 0) {
                            if (lCapacity >= dspServicePrice.getCapMin()) {
                                lTotalAmount = numBlock * dspServicePrice.getPrice();
                                break;
                            }
                        }

                    }

                    if (lTotalAmount == null) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data.invalid"));
                        lDataAmount = 0L;
                        break;
                    }

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    addDataISDNEntity.setRequestTime(calendar.getTime());
                    calendar.add(Calendar.DAY_OF_MONTH, +3);
                    addDataISDNEntity.setPlanTime(calendar.getTime());
                    String fromdate = df.format(addDataISDNEntity.getPlanTime());
                    addDataISDNEntity.setPlanTime(df.parse(fromdate));
                    addDataISDNEntity.setUserID(AdminUser.getUserLogged().getUserId());
                    addDataISDNEntity.setISDN(Utils.fixIsdnWithout0and84(strISDN));
                    addDataISDNEntity.setAmountRequest(lCapacity);
                    addDataISDNEntity.setActiveDay(lActiveDays);
                    addDataISDNEntity.setReqCost(Math.round(lTotalAmount));

                    AddDataISDNEntity dataTemp = new AddDataISDNEntity(addDataISDNEntity);
                    lsUpload.add(dataTemp);
                    lDataAmount += Math.round(lTotalAmount);
                    if (lDataAmount == null || lDataAmount == 0.0) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data.invalid"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }
                    if (lDataAmount.toString().length() > 15) {
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "total_out"));
                        if (mflag.equals("singleUpdate")) {
                            addDataISDNEntity = addDataISDNEntityTemp;
                            description = addDataISDNEntity.getDescription();
                            lDataAmount = addDataISDNEntity.getAmount();
                            lsUpload = model.getListDdDetailBAK(addDataISDNEntity.getTransactionID());
                        } else {
                            addDataISDNEntity = new AddDataISDNEntity();
                            lDataAmount = 0L;
                        }
                        return;
                    }

                }

                disableButton = 1;
            }

        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            throw ex;
        } finally {
            if (reader != null) {
                reader.close();
            }
//            FileUtil.deleteFile(strFilePath + strFileName);
        }
    }

    Long active_day;

//    public void handleFileUploadGroup(FileUploadEvent event) throws Exception {
//        CsvReader reader = null;
//        String strFilePath = "";
//        String strFileName = "";
//        Long lCapacity;
//        Long lActiveDays;
//        Long totalCap = 0L;
//        if (inputAmountData == null) {
//            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "input_amount"));
//            return;
//        }
//        Long AmTemp = inputAmountData;
//        inputAmountDataTemp = AmTemp * 1024 * 1024;
//        try {
//            lsUpload = new ArrayList<>();
////            File file = new File(FileUtil.uploadTempFile(event.getFile()));
//            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
//            strFilePath = file.getPath();
//            strFileName = file.getName();
//            reader = new CsvReader(file.getPath(), '-');
//
//            Long lblock;
//            Long numBlock = null;
//            active_day = null;
//
//            lsComPrice = model.getListComPrice(comID);
//            if (lsComPrice.size() <= 0) {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "tab_none"));
//                return;
//            }
//            List<Long> lsTab = new ArrayList<>();
//            for (DSPComPrice dspComPrice : lsComPrice) {
//                lsTab.add(dspComPrice.getTabId());
//            }
//            lsServicePriceTab = model.getListServicePriceTab(lsTab, serviceIdGroup, 0L);
//            if (lsServicePriceTab.size() <= 0) {
//                lsServicePriceTab = model.getListServicePriceTab(lsTab, serviceIdGroup, 1L);
//            }
//            if (lsServicePriceTab.size() <= 0) {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "tab_none"));
//                return;
//            }
//            List<Long> lsTab1 = new ArrayList<>();
//            for (DSPServicePriceTab dspServicePriceTab : lsServicePriceTab) {
//                lsTab1.add(dspServicePriceTab.getTabId());
//            }
//            lsPrice = model.getPrice(lsTab1);
//            //qui đổi ra kb
//            inputAmountData = inputAmountData * 1024 * 1024;
//            //Check mức dung lượng tối thiểu
//            if (inputAmountData < 300 * 1024) {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file.invalid") + String.valueOf(CAP_MIN));
//                addDataISDNEntity = new AddDataISDNEntity();
//                return;
//            }
//
//
//            for (DSPServicePrice dspServicePrice : lsPrice) {
//                for (DSPServicePriceTab dspServicePriceTab : lsServicePriceTab) {
//                    if (dspServicePrice.getTabId().equals(dspServicePriceTab.getTabId())) {
//                        lblock = dspServicePriceTab.getBlock();
//                        numBlock = inputAmountData / lblock;
//                    }
//                }
//                if (dspServicePrice.getCapMin() != null && dspServicePrice.getCapMax() != null) {
//                    if (inputAmountData < dspServicePrice.getCapMax() && inputAmountData >= dspServicePrice.getCapMin()) {
//                        lDataAmount = numBlock * dspServicePrice.getPrice();
//                        active_day = dspServicePrice.getActiveDay();
//                        break;
//                    }
//                }
//                if (dspServicePrice.getCapMax() == null || dspServicePrice.getCapMax() == 0) {
//                    if (inputAmountData >= dspServicePrice.getCapMin()) {
//                        lDataAmount = numBlock * dspServicePrice.getPrice();
//                        active_day = dspServicePrice.getActiveDay();
//                        break;
//                    }
//                }
//            }
//            if (lDataAmount == null || lDataAmount == 0.0) {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data.invalid"));
//                addDataISDNEntity = new AddDataISDNEntity();
//                return;
//            }
//            if (lDataAmount.toString().length() > 15) {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "total_out"));
//                return;
//            }
//
//
//            if ("text/plain".equals(event.getFile().getContentType())) {
//                int i = 0;
//                if (event.getFile().getSize() > SIZE_LIMIT) {
//                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_too_large"));
//                    return;
//                }
//                if (event.getFile().getSize() <= 0) {
//                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_too_large"));
//                    return;
//                }
//                while (reader.readRecord()) {
//                    i++;
////                    addDataISDNEntity = new AddDataISDNEntity();
//                    String strRecords[] = reader.getValues();
//                    String strISDN = strRecords[0];
//                    String strSuffixes = strRecords[1].substring(strRecords[1].length() - 2);
//                    String strCapacity = strRecords[1].substring(0, strRecords[1].length() - 2).trim();
//                    checkISDN = strISDN.substring(strISDN.length() - 9);
//                    if (!Utils.validateIsdn(Utils.fixIsdnWithout0and84(this.checkISDN))) {
//                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
//                                ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "isdn_fail")
//                                        + String.valueOf(i));
//                        return;
//
//                    }
//                    if (!strSuffixes.equalsIgnoreCase("MB") && !strSuffixes.equalsIgnoreCase("GB")) {
//                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_invalid"));
//                        return;
//                    }
//                    lCapacity = Long.parseLong(strCapacity);
//                    //qui đổi ra kb
//                    if (strSuffixes.equalsIgnoreCase("MB")) {
//                        lCapacity = lCapacity * 1024;
//                    }
//                    if (strSuffixes.equalsIgnoreCase("GB")) {
//                        lCapacity = lCapacity * (1024 * 1024);
//                    }
//                    totalCap += lCapacity;
//                    lActiveDays = Long.parseLong(strRecords[2]);
//                    if (active_day < lActiveDays) {
//                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
//                                ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_fail")
//                                        + String.valueOf(i) + ", "
//                                        + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "activeday_max")
//                                        + active_day.toString());
//                        return;
//                    }
//
//                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                    Calendar calendar = Calendar.getInstance();
//                    addDataISDNEntity.setRequestTime(calendar.getTime());
//                    calendar.add(Calendar.DAY_OF_MONTH, +3);
//                    addDataISDNEntity.setPlanTime(calendar.getTime());
//                    String fromdate = df.format(addDataISDNEntity.getPlanTime());
//                    addDataISDNEntity.setPlanTime(df.parse(fromdate));
//
//                    addDataISDNEntity.setUserID(AdminUser.getUserLogged().getUserId());
//                    addDataISDNEntity.setISDN(strISDN);
//                    addDataISDNEntity.setAmountRequest(lCapacity);
//                    addDataISDNEntity.setActiveDay(lActiveDays);
//
//                    AddDataISDNEntity dataTemp = new AddDataISDNEntity(addDataISDNEntity);
//                    lsUpload.add(dataTemp);
//                }
//                disableButton = 1;
//            } else {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_invalid"));
//                return;
//            }
//            if (totalCap > inputAmountData) {
//                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "isdn_data_fail"));
//                return;
//            }
//
//        } catch (Exception ex) {
//            SystemLogger.getLogger().error(ex);
//            throw ex;
//        } finally {
//            if (reader != null) {
//                reader.close();
//            }
//            FileUtil.deleteFile(strFilePath + strFileName);
//        }
//    }

    public DefaultStreamedContent downloadFileLsSuccess(AddDataISDNEntity add) {
        try {
            String strFileName = model.getListSuccess(add.getTransactionID());

            return FileUtil.downloadFile(new File(strFileName));
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    public DefaultStreamedContent downloadFileLsFail(AddDataISDNEntity add) {
        try {
            String strFileName = model.getListFail(add.getTransactionID());


            return FileUtil.downloadFile(new File(strFileName));

        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());

        }
        return null;
    }

    public Long getlDataAmount() {
        return lDataAmount;
    }

    public void setlDataAmount(Long lDataAmount) {
        this.lDataAmount = lDataAmount;
    }

    public List<AddDataDDRequestEntity> getLsPackageData() {
        return lsPackageData;
    }

    public void setLsPackageData(List<AddDataDDRequestEntity> lsPackageData) {
        this.lsPackageData = lsPackageData;
    }

    public int getRenderTable() {
        return renderTable;
    }

    public void setRenderTable(int renderTable) {
        this.renderTable = renderTable;
    }

    public String getSearchValues() {
        return searchValues;
    }

    public void setSearchValues(String searchValues) {
        this.searchValues = searchValues;
    }

    public List<AddDataISDNEntity> getLsRequest() {
        return lsRequest;
    }

    public void setLsRequest(List<AddDataISDNEntity> lsRequest) {
        this.lsRequest = lsRequest;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public AddDataISDNEntity getAddDataISDNEntity() {
        return addDataISDNEntity;
    }

    public void setAddDataISDNEntity(AddDataISDNEntity addDataISDNEntity) {
        this.addDataISDNEntity = addDataISDNEntity;
    }

    public int getDisableButton() {
        return disableButton;
    }

    public void setDisableButton(int disableButton) {
        this.disableButton = disableButton;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getComType() {
        return comType;
    }

    public void setComType(Long comType) {
        this.comType = comType;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public List<AddDataDDRequestEntity> getLsDDRequest() {
        return lsDDRequest;
    }

    public void setLsDDRequest(List<AddDataDDRequestEntity> lsDDRequest) {
        this.lsDDRequest = lsDDRequest;
    }

    public String getValueButton() {
        return valueButton;
    }

    public void setValueButton(String valueButton) {
        this.valueButton = valueButton;
    }

    public Long getInputAmountData() {
        return inputAmountData;
    }

    public void setInputAmountData(Long inputAmountData) {
        this.inputAmountData = inputAmountData;
    }

//    <!--<p:commandLink id="btn_approval"-->
//                                                <!--actionListener="#{addDataISDNController.onApprove(re)}"-->
//                                                <!--rendered="#{re.status eq '1' and addDataISDNController.comType eq 0}"-->
//                                                <!--process="@this" styleClass="fa fa-check-square mar"-->
//                                                <!--update=":form_main:panel_data_request :form_main:tableRequest">&nbsp;&nbsp;-->
//                                                <!--</p:commandLink>-->
//                                                <!--<p:tooltip for="btn_approval" value="#{PP_ADDDATAISDN.btn_approval}"/>-->

//fa fa-arrow-circle-right
//     <p:commandLink id="btn_confirm"
//    actionListener="#{addDataISDNController.onConfirm(re)}"
//    rendered="#{re.status eq '3' and addDataISDNController.userID eq re.userID}"
//    process="@this" styleClass="fa fa-check"
//    oncomplete="PF('tableRequest').clearFilters();"
//    update=":form_main:panel_data_request :form_main:tableRequest">&nbsp;&nbsp;
//                                                    <p:confirm header="#{PP_ADDDATAISDN.done_dialog_header}"
//    message="#{PP_ADDDATAISDN.done}"/>
//                                                </p:commandLink>
//                                                <p:tooltip for="btn_confirm" value="#{PP_ADDDATAISDN.btn_done}"/>
}
