package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.*;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.FileUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.model.DSPCompanyModel;
import vn.com.telsoft.model.DSPOrderModel;
import vn.com.telsoft.model.DataCodeModel;
import vn.com.telsoft.model.DspServiceModel;
import vn.com.telsoft.ws.DcApiRequest;
import vn.com.telsoft.ws.DcApiResponse;
import vn.com.telsoft.ws.DspApiClient;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Named
@ViewScoped
public class ClipTvController extends TSFuncTemplate implements Serializable {
    static final String RESOURCE_BUNDLE = "PP_DATACODE";
    static final String MODULE_RESOURCE_BUNDLE = "PP_DEPOSITDATABYDATACODE";
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";

    private List<DSPTransaction> mlistDSPTransaction;
    private List<DSPTransaction> mlistDSPTransactionFilterred;
    private DSPTransaction[] mselectedDSPTransaction;
    private DSPTransaction mtmpDSPTransaction;

    private List<DSPDcDetail> mlistDspDcDetail;
    private List<DSPDcDetail> mlistDspDcDetailFilterred;
    private DSPDcDetail[] mselectedDSPDcDetail;
    private DSPDcDetail mtmpDcDetail;

    private DSPServicePrice mtmpDSPServicePrice;
    private List<DSPServicePrice> mlistCardType;
    private long time;
    private DspServiceModel dspServiceModel;
    private List<DSPService> mlistDspService;

    private List<DSPCompany> mlistDSPCompany;
    private DSPCompanyModel dspCompanyModel;

    private List<DSPOrder> mlistDSPOrder;
    private DSPOrderModel dspOrderModel;
    private DSPOrder mtmpDspOrder;

    private List<DSPOrderTransaction> mlistDspOrderTransaction;
    private DSPOrderTransaction mtmpDspOrderTransaction;

    private DataCodeModel dataCodeModel;
    private Long serviceId;
    private long mlUserId;

    private boolean isViewDetail = false;
    private boolean isAddDetail = false;
    private boolean isEditDetail = false;
    private boolean isDeleteDetail = false;
    private boolean isUserMVAS = false;
    private int indexEditDspDcDetail;

    //    private String comNumber = null;
    private Long tabId = 0l;
    //    private Long comId = 0l;
    private Long userId;
    //    private Long comType;
    private int render = 0;
    private long cost;

    private DSPCompany userCompany;

    //27/10
    private List<DSPTransactionHistory> mlistDSPTransactionHistory;
    private List<DSPTransactionHistory> mlistDSPTransactionHistoryFilterred;
    private DSPTransactionHistory[] mselectedDSPTransactionHistory;
    private DSPTransactionHistory mtmpDSPTransactionHistory;

    public ClipTvController() throws Exception {
        userId = AdminUser.getUserLogged().getUserId();
        mlistDspService = new ArrayList<>();
        dspServiceModel = new DspServiceModel();
        dspOrderModel = new DSPOrderModel();
        dspCompanyModel = new DSPCompanyModel();
        getLink();
        dataCodeModel = new DataCodeModel();
        mtmpDSPTransaction = new DSPTransaction();
        mtmpDcDetail = new DSPDcDetail();
        mtmpDSPServicePrice = new DSPServicePrice();

        mtmpDspOrder = new DSPOrder();
        mlistDspDcDetail = new ArrayList<>();
        mlistDSPOrder = new ArrayList<>();
        mlistDspOrderTransaction = new ArrayList<>();
        mlistDSPCompany = new ArrayList<>();
        userCompany = dataCodeModel.getUserCompany(userId);
        tabId = dataCodeModel.getTabId(serviceId, userCompany.getComId());
        mlistCardType = dataCodeModel.getlistCardType(serviceId, userCompany.getComId());
        mlistDSPTransaction = dataCodeModel.getlistDataCode(serviceId, userId);
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
        serviceId = mlistDspService.get(0).getServiceId();
    }

    private String getCardTypeName(Long cardTypeId) {
        if (cardTypeId == null) {
            return "";
        }
        for (DSPServicePrice servicePrice : mlistCardType) {
            if (servicePrice.getPriceId().equals(cardTypeId)) {
                return servicePrice.getName();
            }
        }
        return "";
    }

    private Long getCardTypePrice(Long cardTypeId) {
        if (cardTypeId == null) {
            return null;
        }
        for (DSPServicePrice servicePrice : mlistCardType) {
            if (servicePrice.getPriceId().equals(cardTypeId)) {
                return servicePrice.getPrice();
            }
        }
        return null;
    }

    public void data() {
        long amount = 0l;
        for (DSPDcDetail dspDcDetail : mlistDspDcDetail) {
            amount += dspDcDetail.getAmount().longValue();
        }
        long number = amount % 1000000;
        long temp = amount / 1000000;
        if (temp == 0) {
            time = 6;
        } else if (temp > 0 && number != 0) {
            time = 6 + temp * 2;
        } else if (temp > 0 && number == 0) {
            time = 6 + (temp - 1) * 2;
        }

        long totalCost = 0l;
        for (DSPDcDetail dspDcDetail : mlistDspDcDetail) {
            totalCost += dspDcDetail.getCardTypePrice().longValue() * dspDcDetail.getAmount().longValue();
        }
        mtmpDSPTransaction.setAmount(totalCost);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mtmpDSPTransaction.getRequestTime());
        calendar.add(Calendar.HOUR, (int) time);
        mtmpDSPTransaction.setPlanTime(calendar.getTime());
    }

    private boolean validAmount() {
        Long totalCost = 0L;
        for (DSPDcDetail dspDcDetail : mlistDspDcDetail) {
            totalCost += dspDcDetail.getCardTypePrice().longValue() * dspDcDetail.getAmount().longValue();
        }
        totalCost = totalCost + mtmpDcDetail.getAmount().longValue() * getCardTypePrice(mtmpDcDetail.getPriceId()).longValue();
        if (totalCost.toString().length() > 15) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "total_out"));
            PrimeFaces.current().executeScript("PF('dialog_add_data_code').hide();");
            return false;
        }
        return true;
    }

    private boolean checkPublicKey() throws Exception{
        Date sysDate = new Date();
        Calendar updateDate = Calendar.getInstance();
        updateDate.setTime(userCompany.getUpdatedKey());
        updateDate.add(Calendar.MONTH, 3);
        if (updateDate.getTime().before(sysDate)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "public_key_expired"));
            return false;
        }
        return true;
    }

    public String dateToString(Date input) {
        if (input == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return df.format(input);
    }

    public String customFormat(long value) throws Exception {
        String pattern = "###,###.###";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    @Override
    public void handleOK() throws Exception {
//        long totalRemain = dspOrderModel.getTotalRemainOrder(userCompany.getComId(), serviceId);
        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId());
        long totalRemain = dspOrderModel.getTotalRemainOrder(userCompany.getComId());
        if (checkPublicKey()) {
            if (isADD) {
                data();
                if (mtmpDSPTransaction.getAmount() > totalRemain) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "Total_money_is_greater_than_the_contract_value") + customFormat(totalRemain) + " " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "VND"));
                } else {
                    long amount = mtmpDSPTransaction.getAmount();
                    for (DSPOrder dspOrder : mlistDSPOrder) {
                        if (dspOrder.getRemainQuotaValue() < amount && dspOrder.getRemainQuotaValue() != 0) {
                            mtmpDspOrderTransaction = new DSPOrderTransaction();
                            amount = amount - dspOrder.getRemainQuotaValue();
                            dspOrder.setReservedValue(dspOrder.getRemainQuotaValue() + dspOrder.getReservedValue());
                            mtmpDspOrderTransaction.setTransactionId(mtmpDSPTransaction.getTransactionId());
                            mtmpDspOrderTransaction.setOrderId(dspOrder.getOrderId());
                            mtmpDspOrderTransaction.setIssueTime(mtmpDSPTransaction.getRequestTime());
                            mtmpDspOrderTransaction.setUserId(mtmpDSPTransaction.getUserId());
                            mtmpDspOrderTransaction.setAmount(dspOrder.getRemainQuotaValue());
                            mlistDspOrderTransaction.add(mtmpDspOrderTransaction);
                        } else if (dspOrder.getRemainQuotaValue() >= amount) {
                            mtmpDspOrderTransaction = new DSPOrderTransaction();
                            dspOrder.setReservedValue(amount + dspOrder.getReservedValue());
                            dspOrder.setRemainQuotaValue(dspOrder.getRemainQuotaValue() - amount);
                            mtmpDspOrderTransaction.setTransactionId(mtmpDSPTransaction.getTransactionId());
                            mtmpDspOrderTransaction.setOrderId(dspOrder.getOrderId());
                            mtmpDspOrderTransaction.setIssueTime(mtmpDSPTransaction.getRequestTime());
                            mtmpDspOrderTransaction.setUserId(mtmpDSPTransaction.getUserId());
                            mtmpDspOrderTransaction.setAmount(amount);
                            mlistDspOrderTransaction.add(mtmpDspOrderTransaction);
                            amount = 0;
                            break;
                        }
                    }

                    dataCodeModel.insert(mtmpDSPTransaction, mlistDspDcDetail, mlistDspOrderTransaction, mlistDSPOrder);
                    mlistDSPTransaction.add(0, mtmpDSPTransaction);
                    PrimeFaces.current().executeScript("PF('table_data_code').clearFilters()");
                    ClientMessage.logAdd();
                    setTime(0);
                    handleCancel();
                }
            } else {
                if (mtmpDSPTransaction.getAmount() > (totalRemain + cost)) {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "Total_money_is_greater_than_the_contract_value") + customFormat(totalRemain + cost) + " " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "VND"));
                } else {
                    List<DSPOrderTransaction> mlistDspOrderTransactionOld = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());
                    for (DSPOrderTransaction dspOrderTransaction : mlistDspOrderTransactionOld) {
                        for (DSPOrder dspOrder : mlistDSPOrder) {
                            if (dspOrder.getOrderId().equals(dspOrderTransaction.getOrderId())) {
                                dspOrder.setReservedValue(dspOrder.getReservedValue() - dspOrderTransaction.getAmount());
                                dspOrder.setRemainQuotaValue(dspOrder.getRemainQuotaValue() + dspOrderTransaction.getAmount());
                                break;
                            }
                        }
                    }

                    long amount = mtmpDSPTransaction.getAmount();
                    for (DSPOrder dspOrder : mlistDSPOrder) {
                        if (dspOrder.getRemainQuotaValue() < amount && dspOrder.getRemainQuotaValue() != 0) {
                            mtmpDspOrderTransaction = new DSPOrderTransaction();
                            amount = amount - dspOrder.getRemainQuotaValue();
                            dspOrder.setReservedValue(dspOrder.getRemainQuotaValue() + dspOrder.getReservedValue());
                            mtmpDspOrderTransaction.setTransactionId(mtmpDSPTransaction.getTransactionId());
                            mtmpDspOrderTransaction.setOrderId(dspOrder.getOrderId());
                            mtmpDspOrderTransaction.setIssueTime(mtmpDSPTransaction.getRequestTime());
                            mtmpDspOrderTransaction.setUserId(mtmpDSPTransaction.getUserId());
                            mtmpDspOrderTransaction.setAmount(dspOrder.getRemainQuotaValue());
                            mlistDspOrderTransaction.add(mtmpDspOrderTransaction);
                        } else if (dspOrder.getRemainQuotaValue() >= amount) {
                            mtmpDspOrderTransaction = new DSPOrderTransaction();
                            dspOrder.setReservedValue(amount + dspOrder.getReservedValue());
                            dspOrder.setRemainQuotaValue(dspOrder.getRemainQuotaValue() - amount);
                            mtmpDspOrderTransaction.setTransactionId(mtmpDSPTransaction.getTransactionId());
                            mtmpDspOrderTransaction.setOrderId(dspOrder.getOrderId());
                            mtmpDspOrderTransaction.setIssueTime(mtmpDSPTransaction.getRequestTime());
                            mtmpDspOrderTransaction.setUserId(mtmpDSPTransaction.getUserId());
                            mtmpDspOrderTransaction.setAmount(amount);
                            mlistDspOrderTransaction.add(mtmpDspOrderTransaction);
                            amount = 0;
                            break;
                        }
                    }

                    List<DSPOrder> listDspOrderChange = new ArrayList<>();
                    DSPOrder mtmpDSPOrder;

                    for (DSPOrderTransaction dspOrderTransaction : mlistDspOrderTransaction) {
                        mtmpDSPOrder = new DSPOrder();
                        mtmpDSPOrder.setOrderId(dspOrderTransaction.getOrderId());
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

                    mtmpDSPTransaction.setRequestTime(new Date());
                    if (mtmpDSPTransaction.getStatus().equals("0")) {
                        mtmpDSPTransaction.setStatus("0");
                    } else {
                        mtmpDSPTransaction.setStatus("1");
                    }
                    dataCodeModel.update(mtmpDSPTransaction, mlistDspDcDetail, mlistDspOrderTransaction, listDspOrderChange);
                    PrimeFaces.current().executeScript("PF('table_data_code').clearFilters();");
                    ClientMessage.logUpdate();
                    handleCancel();
                }
            }
        }
    }

    @Override
    public void handleDelete() throws Exception {

    }

    public void handOkDcRequest() throws Exception {
        if (validAmount()) {
            mtmpDcDetail.setCardName(getCardTypeName(mtmpDcDetail.getPriceId()));
            mtmpDcDetail.setCardTypePrice(getCardTypePrice(mtmpDcDetail.getPriceId()));
            if (isEditDetail) {
                mlistDspDcDetail.remove(indexEditDspDcDetail);
                boolean bCheckAdd = true;
                for (DSPDcDetail dspDcDetail : mlistDspDcDetail) {
                    if (dspDcDetail.getPriceId() == mtmpDcDetail.getPriceId().longValue()) {
                        bCheckAdd = false;
                        dspDcDetail.setAmount(dspDcDetail.getAmount() + mtmpDcDetail.getAmount());
                        dspDcDetail.setTotalCost(dspDcDetail.getAmount().longValue() * dspDcDetail.getCardTypePrice().longValue());
                        break;
                    }
                }
                if (bCheckAdd) {
                    mtmpDcDetail.setTotalCost(mtmpDcDetail.getAmount().longValue() * mtmpDcDetail.getCardTypePrice().longValue());
                    mlistDspDcDetail.add(0, mtmpDcDetail);
                }

                PrimeFaces.current().executeScript("PF('dialog_add_data_code').hide();");
                PrimeFaces.current().executeScript("PF('table_add_data_code').clearFilters();");
                data();
            } else {
                boolean bCheckAdd = true;
                for (DSPDcDetail dspDcDetail : mlistDspDcDetail) {
                    if (dspDcDetail.getPriceId() == mtmpDcDetail.getPriceId().longValue()) {
                        bCheckAdd = false;
                        dspDcDetail.setAmount(dspDcDetail.getAmount() + mtmpDcDetail.getAmount());
                        dspDcDetail.setTotalCost(dspDcDetail.getAmount().longValue() * dspDcDetail.getCardTypePrice().longValue());
                        break;
                    }
                }
                if (bCheckAdd) {
                    mtmpDcDetail.setTotalCost(mtmpDcDetail.getAmount().longValue() * mtmpDcDetail.getCardTypePrice().longValue());
                    mlistDspDcDetail.add(0, mtmpDcDetail);
                }
                PrimeFaces.current().executeScript("PF('dialog_add_data_code').hide();");
                PrimeFaces.current().executeScript("PF('table_add_data_code').clearFilters();");
                data();
            }
        }
    }


    public void handleDelete(DSPTransaction obj) throws Exception {
        mtmpDSPTransaction = obj;
        mlistDspOrderTransaction = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());
        mtmpDSPTransaction.setStatus("4");
        dataCodeModel.updateStatus(mtmpDSPTransaction, mlistDspOrderTransaction);
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgDeleteSucc"));
    }

    public void handleDeleteDcRequest(DSPDcDetail obj) throws Exception {
        mtmpDcDetail = obj;
        mtmpDcDetail.setCardName(getCardTypeName(mtmpDcDetail.getPriceId()));
        mtmpDcDetail.setCardTypePrice(getCardTypePrice(mtmpDcDetail.getPriceId()));
        mlistDspDcDetail.remove(mtmpDcDetail);
        data();
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        render = 0;
        setTime(0);
    }

    public void changeStateView(DSPTransaction obj) throws Exception {
        super.changeStateView();
        mtmpDSPTransaction = obj;
        mlistDspDcDetail = dataCodeModel.getlistDcDetail(mtmpDSPTransaction.getTransactionId(), mtmpDSPTransaction.getTabId());
        PrimeFaces.current().executeScript("PF('table_add_data_code').clearFilters();");
        data();
    }

    @Override
    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        mtmpDSPTransaction = new DSPTransaction();
        mtmpDSPTransaction.setRequestTime(new Date());
        mtmpDSPTransaction.setStatus("0");
        mtmpDSPTransaction.setServiceId(serviceId);
        mtmpDSPTransaction.setUserId(AdminUser.getUserLogged().getUserId());
        mtmpDSPTransaction.setUserName(AdminUser.getUserLogged().getUserName());
        mtmpDSPTransaction.setComId(userCompany.getComId());
        mtmpDSPTransaction.setTabId(tabId);
        mtmpDSPTransaction.setComName(userCompany.getComName());
        mtmpDSPTransaction.setComNumber(userCompany.getVasMobile());
        mlistDspDcDetail = new ArrayList<>();
        mlistDspOrderTransaction = new ArrayList<>();
        PrimeFaces.current().executeScript("PF('table_add_data_code').clearFilters();");
//        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId());
//        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId(), mtmpDSPTransaction.getServiceId());
    }

    public void changeStateAddDcRequest() throws Exception {
        isAddDetail = true;
        isEditDetail = false;
        mtmpDcDetail = new DSPDcDetail();
        mtmpDcDetail.setStatus("1");
        mtmpDcDetail.setRequestTime(new Date());
        mtmpDcDetail.setTransactionId(mtmpDSPTransaction.getTransactionId());
    }

    public void changeStateEdit(DSPTransaction obj) throws Exception {
        super.changeStateEdit();
        mlistDspOrderTransaction = new ArrayList<>();
        mlistDspDcDetail = new ArrayList<>();
        cost = 0l;
        mtmpDSPTransaction = obj;
        cost = mtmpDSPTransaction.getAmount();
        mlistDspDcDetail = dataCodeModel.getlistDcDetail(mtmpDSPTransaction.getTransactionId(), mtmpDSPTransaction.getTabId());
        mtmpDcDetail.setTransactionId(mtmpDSPTransaction.getTransactionId());
        PrimeFaces.current().executeScript("PF('table_add_data_code').clearFilters();");
        data();
//        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId());
//        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId(), mtmpDSPTransaction.getServiceId());
    }

    public void changeStateEditDcRequest(DSPDcDetail obj) throws Exception {
        isEditDetail = true;
        isAddDetail = false;
        indexEditDspDcDetail = mlistDspDcDetail.indexOf(obj);
        mtmpDcDetail = SerializationUtils.clone(obj);
    }

    public void changeStateDel(DSPTransaction obj) throws Exception {
        super.changeStateDel();
        mtmpDSPTransaction = obj;
        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId());
//        mlistDSPOrder = dspOrderModel.getListOrderForTrans(mtmpDSPTransaction.getComId(), mtmpDSPTransaction.getServiceId());
        mlistDspOrderTransaction = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());
    }

    public void changeStateDelDcRequest(DSPDcDetail obj) throws Exception {
        isDeleteDetail = true;
        mtmpDcDetail = obj;
    }

//    public void onApprove(DSPTransaction obj) throws Exception {
//        mlistDspDcDetail = new ArrayList<>();
//        render = 3;
//        mtmpDSPTransaction = obj;
//        mlistDspDcDetail = dataCodeModel.getlistDcDetail(mtmpDSPTransaction.getTransactionId(), mtmpDSPTransaction.getTabId());
//        PrimeFaces.current().executeScript("PF('table_approval').clearFilters();");
//        data();
//    }

    public void onConfirm(DSPTransaction obj) throws Exception {
        mlistDspOrderTransaction = new ArrayList<>();
        mtmpDSPTransaction = obj;
        mlistDspOrderTransaction = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());

//        validInput();
//        DcApiRequest request = new DcApiRequest();
//        request.setTransactionId(mtmpDSPTransaction.getTransactionId().toString());
//        request.setOrderId(mtmpDSPTransaction.getResOrderId());
//        DcApiClient dcApiClient = new DcApiClient();
//        DcApiResponse response = dcApiClient.activedc(request);

        DcApiRequest request = new DcApiRequest();
        request.setTransactionId(mtmpDSPTransaction.getTransactionId().toString());
        request.setOrderId(mtmpDSPTransaction.getResOrderId());
        DspApiClient dspApiClient = new DspApiClient();
        DcApiResponse response = dspApiClient.activedc(request);
        if (response != null) {
            //Message to client
            if ("0".equals(response.getCode())) {
                mtmpDSPTransaction.setStatus("6");
                dataCodeModel.updateStatus(mtmpDSPTransaction, mlistDspOrderTransaction);
                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
            } else {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
            }
        }
        PrimeFaces.current().executeScript("PF('table_approval').clearFilters();");
        chargingProxy();
    }

    //tac dong vao he thong Charging Proxy.
    public void chargingProxy() {

    }

    public void validInput() throws TelsoftException {
        if (mtmpDSPTransaction.getTransactionId() == null) {
            throw new TelsoftException("ERR_NOTVALID_ISDN");
        }
        if (mtmpDSPTransaction.getResOrderId() == null) {
            throw new TelsoftException("ERR_NOTVALID_SERIAL");
        }
    }

    public void handTransferApproval(DSPTransaction obj) throws Exception {
        if (checkPublicKey()) {
            mtmpDSPTransaction = obj;
            mlistDspOrderTransaction = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());
            mtmpDSPTransaction.setStatus("2");
            dataCodeModel.updateStatus(mtmpDSPTransaction, mlistDspOrderTransaction);
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "confirm_success"));
            PrimeFaces.current().executeScript("PF('table_approval').clearFilters();");
        }
    }

    public void onExtend(DSPTransaction obj) throws Exception {
        render = 6;
        mtmpDSPTransaction = obj;
        mlistDSPTransactionHistory = new ArrayList<>();
        mlistDSPTransactionHistory = dataCodeModel.getListDSPTransactionHistory(userId, mtmpDSPTransaction.getTransactionId());
        mtmpDSPTransactionHistory = new DSPTransactionHistory();
        mtmpDSPTransactionHistory.setTransactionId(mtmpDSPTransaction.getTransactionId());
        mtmpDSPTransactionHistory.setUserId(userId);
        mtmpDSPTransactionHistory.setActionType(1L);
        mtmpDSPTransactionHistory.setIssueTime(new Date());
    }

    public void doExtend() throws Exception {
        DcApiRequest request = new DcApiRequest();
        request.setTransactionId(mtmpDSPTransaction.getTransactionId().toString());
        request.setOrderId(mtmpDSPTransaction.getResOrderId());
        request.setExtendDays(mtmpDSPTransactionHistory.getExtendedDays());
        DspApiClient dspApiClient = new DspApiClient();
        DcApiResponse response = dspApiClient.extenddc(request);
        if (response != null) {
            //Message to client
            if ("0".equals(response.getCode())) {
                dataCodeModel.insertDSPTransactionHistory(mtmpDSPTransactionHistory);
                mlistDSPTransactionHistory.add(0, mtmpDSPTransactionHistory);
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
                ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgExtendSucc"));
            } else {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
            }
        }
        render = 0;
    }

    public void handleFileUploadDSPTransactionHistory(FileUploadEvent event) throws Exception {
        try {
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
            String pathFile = file.getName();
            mtmpDSPTransactionHistory.setFilePath(pathFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public DefaultStreamedContent downloadFileDSPTransactionHistory(DSPTransactionHistory obj) throws Exception {
        try {
            String path = SystemConfig.getConfig("FileUploadPath") + obj.getFilePath();
            return FileUtil.downloadFile(obj.getFilePath(), path);
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

//    public void doApprove() throws Exception {
//        mlistDspOrderTransaction = new ArrayList<>();
//        mlistDspOrderTransaction = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());
//        mtmpDSPTransaction.setStatus("2");
//        dataCodeModel.updateStatus(mtmpDSPTransaction, mlistDspOrderTransaction);
//        render = 0;
//        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgApprovalSucc"));
//        PrimeFaces.current().executeScript("PF('table_data_code').clearFilters();");
//    }
//
//    public void onCancelApprove() throws Exception {
//        mlistDspOrderTransaction = dataCodeModel.getlistOrderTransaction(userId, mtmpDSPTransaction.getTransactionId());
//        mtmpDSPTransaction.setStatus("5");
//        dataCodeModel.updateStatus(mtmpDSPTransaction, mlistDspOrderTransaction);
//        render = 0;
//        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgRejectSucc"));
//        PrimeFaces.current().executeScript("PF('table_data_code').clearFilters();");
//    }

    public List<DSPTransaction> getMlistDSPTransaction() {
        return mlistDSPTransaction;
    }

    public void setMlistDSPTransaction(List<DSPTransaction> mlistDSPTransaction) {
        this.mlistDSPTransaction = mlistDSPTransaction;
    }

    public List<DSPTransaction> getMlistDSPTransactionFilterred() {
        return mlistDSPTransactionFilterred;
    }

    public void setMlistDSPTransactionFilterred(List<DSPTransaction> mlistDSPTransactionFilterred) {
        this.mlistDSPTransactionFilterred = mlistDSPTransactionFilterred;
    }

    public DSPTransaction[] getMselectedDSPTransaction() {
        return mselectedDSPTransaction;
    }

    public void setMselectedDSPTransaction(DSPTransaction[] mselectedDSPTransaction) {
        this.mselectedDSPTransaction = mselectedDSPTransaction;
    }

    public DSPTransaction getMtmpDSPTransaction() {
        return mtmpDSPTransaction;
    }

    public void setMtmpDSPTransaction(DSPTransaction mtmpDSPTransaction) {
        this.mtmpDSPTransaction = mtmpDSPTransaction;
    }

    public List<DSPDcDetail> getMlistDspDcDetail() {
        return mlistDspDcDetail;
    }

    public void setMlistDspDcDetail(List<DSPDcDetail> mlistDspDcDetail) {
        this.mlistDspDcDetail = mlistDspDcDetail;
    }

    public List<DSPDcDetail> getMlistDspDcDetailFilterred() {
        return mlistDspDcDetailFilterred;
    }

    public void setMlistDspDcDetailFilterred(List<DSPDcDetail> mlistDspDcDetailFilterred) {
        this.mlistDspDcDetailFilterred = mlistDspDcDetailFilterred;
    }

    public DSPDcDetail[] getMselectedDSPDcDetail() {
        return mselectedDSPDcDetail;
    }

    public void setMselectedDSPDcDetail(DSPDcDetail[] mselectedDSPDcDetail) {
        this.mselectedDSPDcDetail = mselectedDSPDcDetail;
    }

    public DSPDcDetail getMtmpDcDetail() {
        return mtmpDcDetail;
    }

    public void setMtmpDcDetail(DSPDcDetail mtmpDcDetail) {
        this.mtmpDcDetail = mtmpDcDetail;
    }

    public DSPServicePrice getMtmpDSPServicePrice() {
        return mtmpDSPServicePrice;
    }

    public void setMtmpDSPServicePrice(DSPServicePrice mtmpDSPServicePrice) {
        this.mtmpDSPServicePrice = mtmpDSPServicePrice;
    }

    public List<DSPServicePrice> getMlistCardType() {
        return mlistCardType;
    }

    public void setMlistCardType(List<DSPServicePrice> mlistCardType) {
        this.mlistCardType = mlistCardType;
    }

    public boolean isUserMVAS() {
        return isUserMVAS;
    }

    public void setUserMVAS(boolean userMVAS) {
        isUserMVAS = userMVAS;
    }

    public boolean isAddDetail() {
        return isAddDetail;
    }

    public void setAddDetail(boolean addDetail) {
        isAddDetail = addDetail;
    }

    public boolean isEditDetail() {
        return isEditDetail;
    }

    public void setEditDetail(boolean editDetail) {
        isEditDetail = editDetail;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DSPCompany getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(DSPCompany userCompany) {
        this.userCompany = userCompany;
    }

    public List<DSPTransactionHistory> getMlistDSPTransactionHistory() {
        return mlistDSPTransactionHistory;
    }

    public void setMlistDSPTransactionHistory(List<DSPTransactionHistory> mlistDSPTransactionHistory) {
        this.mlistDSPTransactionHistory = mlistDSPTransactionHistory;
    }

    public List<DSPTransactionHistory> getMlistDSPTransactionHistoryFilterred() {
        return mlistDSPTransactionHistoryFilterred;
    }

    public void setMlistDSPTransactionHistoryFilterred(List<DSPTransactionHistory> mlistDSPTransactionHistoryFilterred) {
        this.mlistDSPTransactionHistoryFilterred = mlistDSPTransactionHistoryFilterred;
    }

    public DSPTransactionHistory[] getMselectedDSPTransactionHistory() {
        return mselectedDSPTransactionHistory;
    }

    public void setMselectedDSPTransactionHistory(DSPTransactionHistory[] mselectedDSPTransactionHistory) {
        this.mselectedDSPTransactionHistory = mselectedDSPTransactionHistory;
    }

    public DSPTransactionHistory getMtmpDSPTransactionHistory() {
        return mtmpDSPTransactionHistory;
    }

    public void setMtmpDSPTransactionHistory(DSPTransactionHistory mtmpDSPTransactionHistory) {
        this.mtmpDSPTransactionHistory = mtmpDSPTransactionHistory;
    }
}
