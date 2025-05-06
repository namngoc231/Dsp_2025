package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.admin.gui.entity.UserDTL;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.DateUtil;
import com.faplib.util.FileUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.model.ApParamModel;
import vn.com.telsoft.model.DSPOrderModel;
import vn.com.telsoft.model.DSPOrderStatusModel;
import vn.com.telsoft.util.Utils;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author TungLM, TELSOFT
 */
@Named
@ViewScoped
public class CreateOrderController extends TSFuncTemplate implements Serializable {
    //bundle
    static final String RESOURCE_BUNDLE = "PP_CREATEORDER";
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    static final String FONT_NAME = "Times New Roman";
    //order status
    static final String ORDER_STATUS_CREATE = "0";
    static final String ORDER_STATUS_WAITING_APPROVAL = "1";
    static final String ORDER_STATUS_APPROVED = "21";
    static final String ORDER_STATUS_COMPLETE = "3";
    static final String ORDER_STATUS_DELETE = "4";
    static final String ORDER_STATUS_REJECT = "5";
    static final String ORDER_STATUS_ACTIVE = "2";
    static final String ORDER_STATUS_WAITING_MONEY = "6";
    static final String ORDER_STATUS_APPROVAL = "8";
    //
    private DSPOrder msearchOrder;
    private Date mdStartDate;
    private Date mdEndDate;
    private Date expStartDate;
    private Date expEndDate;
    private List<DSPOrder> mlistOrder;
    private List<DSPOrder> mlistOrderFiltered;

    private List<DSPCompany> mlistCompany;
    private List<DSPOrderPolicy> mlistOrderPolicy;

    private String mstrCompanySearch;
    private DSPOrder mtmpOrder = new DSPOrder();
    private DSPOrderModel mmodel;

    private boolean userMvas = false;//type = 0
    private boolean userCTKV = false;//type = 1
    private boolean userRetailer = false;//type = 2
    private boolean isUserCompany = false;
    /// /type = 3
    private boolean renderApproval = false;
    private boolean renderReject = false;
    private boolean renderConfirm = false;
    private UserDTL userDTL;
    private DSPCompany userCompany;
    private DSPOrderPolicy orderPolicy;
    private Date currentDate;

    //add_new
    private boolean isOrderExtension = false;
    private DSPOrderStatusModel dspOrderStatusModel;
    private List<DSPOrderStatus> lstDspOrderStatusExtension;
    private List<DSPOrderStatus> lstDspOrderStatusExtensionFiltered;
    private Date oldDateExpire;
    private Long numberDateExpire;
    private Date newDateExpire;
    private String filePath;
    private DSPOrder dspOrderExt;
    private Integer indexOrderExt;
    private Integer indexOrderExtFilter;

    //HuyNQ 30/05/2022
    private ApParamModel apParamModel;
    String discountPercent;
    String issueDate;
    String minCost;

    // HuyNQ 14/08/2023
    String minCostW2G;
    String discountPercentW2G;
    List<ApParam> lstComW2G;

    public CreateOrderController() throws Exception {
        this.mlistOrder = new ArrayList();
        init();
        mmodel = new DSPOrderModel();
        initCombo();
        //Check user in MVAS
        userDTL = AdminUser.getUserLogged();
        if (userDTL != null) {
            userCompany = mmodel.getUserCompany(userDTL.getUserId());
        }
        if (userCompany != null) {
            switch (userCompany.getType().intValue()) {
                case 0:
                    userMvas = true;
                    break;
                case 1:
                    userCTKV = true;
                    break;
                case 2:
                    userRetailer = true;
                    break;
                case 3:
                    isUserCompany = true;
                    break;
                default:
                    // code block
            }
        }
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    private void init() throws Exception {
        msearchOrder = new DSPOrder();
        currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        this.mdEndDate = cal.getTime();
        cal.add(Calendar.DATE, -30);
        this.mdStartDate = cal.getTime();
        dspOrderStatusModel = new DSPOrderStatusModel();
        lstDspOrderStatusExtension = new ArrayList<>();
        oldDateExpire = new Date();
        newDateExpire = new Date();
        filePath = "";
        isOrderExtension = false;
        indexOrderExt = 0;
        indexOrderExtFilter = -1;

        //HuyNQ 30/05/2022 //tam dung, mail 24/06/2022
//        issueDate = apParamModel.getParValue("DISCOUNT", "ISSUE_DATE");
        apParamModel = new ApParamModel();
        discountPercent = apParamModel.getParValue("DISCOUNT", "DISCOUNT_PERCENT");
        minCost = apParamModel.getParValue("DISCOUNT", "MIN_COST");
        discountPercentW2G = apParamModel.getParValue("W2G_DISCOUNT", "DISCOUNT_PERCENT");
        minCostW2G = apParamModel.getParValue("W2G_DISCOUNT", "MIN_COST");
        lstComW2G = apParamModel.getListApParamByType("W2G_DISCOUNT_COM");
    }

    private void initCombo() throws Exception {
//        mlistCompany = mmodel.getListCompany();
    }

    public void handSearch() {
        try {
            this.mlistOrder = mmodel.getListOrder(this.msearchOrder, mdStartDate, mdEndDate, expStartDate, expEndDate, userDTL.getUserId());
            this.mlistOrderFiltered = null;
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public List<DSPCompany> completeCompanySearch(String query) throws Exception {
        List<DSPCompany> lstCom = new ArrayList<>();
        if (query == null || query.length() <= 0)
            return lstCom;
//        String strName = StringUtil.removeSign(query.trim());
        //
        lstCom = mmodel.getListCompany(query, userDTL.getUserId(), 10);
        //
        /*for (DSPCompany tmpCompany : mlistCompany) {
            if ((tmpCompany.getTaxCode() != null && tmpCompany.getTaxCode().contains(query))
                    || (tmpCompany.getBusCode() != null && tmpCompany.getBusCode().contains(query))
                    || (tmpCompany.getComName() != null && tmpCompany.getComName().contains(strName))) {
                lstCom.add(tmpCompany);
            }
        }*/

        return lstCom;
    }

    public void onSelectedCompanyComplete(SelectEvent e) throws Exception {
        this.mtmpOrder.setDspCompany((DSPCompany) e.getObject());
    }

    /// /////////////////////////////////////////////////////////////////////////

    public void getListOrderPolicy() throws Exception {
        //Danh sach chiet khau ap dụng cho com_id
//        if (this.mtmpOrder.getType() == null || !this.mtmpOrder.getType().equals("1")) {
        if (this.mtmpOrder.getDspCompany().getComId() != null) {
            mlistOrderPolicy = mmodel.getListOrderPolicy(this.mtmpOrder.getDspCompany().getComId(), mtmpOrder.getDspCompany().getCustType());
        } else {
            mlistOrderPolicy = null;
        }
        getOrderPolicy();
//        }
    }

    private long activeDays = 0l;

    public Boolean getOrderPolicy() throws Exception {
        this.orderPolicy = null;
        this.activeDays = 0l;
        this.mtmpOrder.setTabId(null);
        if (!"1".equals(this.mtmpOrder.getType())) {
            this.mtmpOrder.setPaidCost(null);
        }
        //huynq 30/05/2022 //tam dung, mail 24/06/2022
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        long discount = 0;
//        long totalOrder = 0;
//        Calendar sysDate = Calendar.getInstance();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(dateFormat.parse(issueDate));
//        if (((sysDate.get(Calendar.MONTH) - calendar.get(Calendar.MONTH)) % 3 == 0) && mmodel.checkFirstOrder(this.mtmpOrder.getDspCompany().getComId()) == 0) {
//            totalOrder = mmodel.totalCostOrder(sysDate.getTime(), this.mtmpOrder.getDspCompany().getComId());
//            if (totalOrder >= Long.parseLong(minCost)) {
//                discount = (totalOrder * Long.parseLong(discountPercent)) / 100;
//            }
//        }
        //////////////////
        //huynq 27/07/2022
        long discount = 0;
        if (!ORDER_STATUS_REJECT.equals(mtmpOrder.getOldStatus())) {
            long minValue, discountPer;
            if (lstComW2G.stream().anyMatch(value -> value.getParValue().equals(this.mtmpOrder.getDspCompany().getComId().toString()))) {
                minValue = Long.parseLong(minCostW2G);
                discountPer = Long.parseLong(discountPercentW2G);
            } else {
                minValue = Long.parseLong(minCost);
                discountPer = Long.parseLong(discountPercent);
            }

            long totalOrder = mmodel.totalCostOrder(this.mtmpOrder.getDspCompany().getComId());
            if (totalOrder >= minValue) {
                this.mtmpOrder.setDiscountType("1");
                discount = (totalOrder * discountPer) / 100;
                this.mtmpOrder.setDiscountAmt(discount);
            }
        } else {
            if ("1".equals(mtmpOrder.getDiscountType())) {
                discount = mtmpOrder.getDiscountAmt();
            }
        }
        //////
        if (this.mtmpOrder.getDspCompany().getComId() == null || this.mtmpOrder.getContractValue() == null) {
            return false;
        }
        long paidCost = this.mtmpOrder.getContractValue().longValue();
        double percent = 0;
        //Tinh chiet khau ap dụng cho com_id
        if (mlistOrderPolicy == null || mlistOrderPolicy.size() == 0) {
            mlistOrderPolicy = mmodel.getListOrderPolicy(this.mtmpOrder.getDspCompany().getComId(), mtmpOrder.getDspCompany().getCustType());
        }
        if (mlistOrderPolicy != null && mlistOrderPolicy.size() >= 0) {
            for (DSPOrderPolicy tmpOrderPolicy : mlistOrderPolicy) {
                if ((tmpOrderPolicy.getMinValue() == null || tmpOrderPolicy.getMinValue().longValue() <= this.mtmpOrder.getContractValue().longValue())
                    && (tmpOrderPolicy.getMaxValue() == null || tmpOrderPolicy.getMaxValue().longValue() > this.mtmpOrder.getContractValue().longValue())) {
                    this.orderPolicy = tmpOrderPolicy;
                    break;
                }
            }
            if (this.orderPolicy != null) {
                paidCost = this.mtmpOrder.getContractValue().longValue() * (100 - this.orderPolicy.getPromPct().longValue()) / 100;
                this.mtmpOrder.setTabId(this.orderPolicy.getTabId());
                this.activeDays = this.orderPolicy.getActiveDays();
                paidCost = paidCost > discount ? paidCost - discount : 0;
                List<ApParam> lstCompanyLoyalty = apParamModel.getListApParamByType("LOYALTY_COMPANY");
                if (lstCompanyLoyalty.stream().anyMatch(value -> Long.parseLong(value.getParValue()) == mtmpOrder.getDspCompany().getComId())) {
                    paidCost = 0;
                }
                if (!"1".equals(mtmpOrder.getType())) {
                    this.mtmpOrder.setPaidCost(paidCost);
                }
                percent = 100 - ((double) mtmpOrder.getPaidCost() / mtmpOrder.getContractValue()) * 100;
                this.mtmpOrder.setDiscountPercent(percent);
            } else {
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "err_contract_value"));
                return false;
            }
        }
        return true;
    }

    /// /////////////////////////////////////////////////////////////////////////

    @Override
    public void changeStateAdd() throws Exception {
        initCombo();
        super.changeStateAdd();
        mtmpOrder = new DSPOrder();
        mtmpOrder.setUserId(userDTL.getUserId());
        mtmpOrder.setUserName(userDTL.getUserName());
        mtmpOrder.setDspCompany(userCompany);
        mtmpOrder.setReservedValue(0L);
        mtmpOrder.setDiscountType("0");
        mlistOrderPolicy = null;
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void changeStateEdit(DSPOrder order) throws Exception {
        initCombo();
        super.changeStateEdit();
        selectedIndex = mlistOrder.indexOf(order);
        mtmpOrder = SerializationUtils.clone(order);
        mtmpOrder.setOldStatus(mtmpOrder.getStatus());
        /*if (mlistCompany != null) {
            if (!"1".equals(mtmpOrder.getDspCompany().getStatus())) {
                mlistCompany.add(0, mtmpOrder.getDspCompany());
            }
            mtmpOrder.setDspCompany(mlistCompany.stream().filter(line -> line.getComId().equals(mtmpOrder.getComId())).findFirst().orElse(mtmpOrder.getDspCompany()));
        }*/
        mlistOrderPolicy = null;
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void changeStateView(DSPOrder order) throws Exception {
        this.changeStateEdit(order);
        super.changeStateView();
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    private boolean validInput() throws Exception {
        if (mtmpOrder.getEffectiveTime() != null && mtmpOrder.getExpireTime() != null) {
            long diffDays = TimeUnit.DAYS.convert(mtmpOrder.getExpireTime().getTime() - mtmpOrder.getEffectiveTime().getTime(), TimeUnit.MILLISECONDS);
            if (diffDays < 0l) {
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "eff_exp_date_error"));
                return false;
            }
            if (diffDays + 1 > activeDays) {
                ClientMessage.logErr(MessageFormat.format(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "exp_date_error"), activeDays));
                return false;
            }
            if (ORDER_STATUS_APPROVAL.equals(mtmpOrder.getStatus()) && "1".equals(mtmpOrder.getPayMethod())) {
                return !validCspMobile();
            }
        }
        return true;
    }

    private boolean validConfirm() throws Exception {
        if (mtmpOrder.getEffectiveTime() != null) {
            SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
            Date newDate = sd.parse(DateUtil.getDateStr(new Date()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            calendar.add(Calendar.DATE, -3);
            Date dateConfirm = calendar.getTime();
            if (dateConfirm.after(mtmpOrder.getEffectiveTime())) {
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "eff_appr_date_error"));
                return false;
            }
        }
        return true;
    }

    private boolean validApproval() throws Exception {
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
        Date newDate = sd.parse(DateUtil.getDateStr(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        calendar.add(Calendar.DATE, -3);
        Date dateApprove = calendar.getTime();

        if (mtmpOrder.getEffectiveTime() != null) {
            if (dateApprove.after(mtmpOrder.getEffectiveTime())) {
                ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "eff_appr_date_error"));
                return false;
            }
        }
        if ("1".equals(mtmpOrder.getPayMethod())) {
            return !validCspMobile();
        }
        return true;
    }

    private boolean validCspMobile() throws Exception {
        if (mtmpOrder.getDspCompany().getCpsMobile() == null || mtmpOrder.getDspCompany().getCpsMobile().isEmpty()) {
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "eff_cps_mobile_error"));
            return true;
        }
        if (!Utils.checkVasMobile(mtmpOrder.getDspCompany().getCpsMobile())) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errCspMobile"));
            return true;
        }
        return false;
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleOK() throws Exception {
        //Tinh lai so tien thanh toan & so ngay hieu luc don hang
        if (getOrderPolicy()) {
            if (validInput()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                mtmpOrder.setComId(null);
                if (mtmpOrder.getDspCompany() != null) {
                    mtmpOrder.setComId(mtmpOrder.getDspCompany().getComId());
                }
                //Cap nhat remain theo gia tri hop dong
                mtmpOrder.setRemainValue(mtmpOrder.getContractValue());
                mtmpOrder.setRemainQuotaValue(mtmpOrder.getContractValue());
                mtmpOrder.setReservedValue(0L);

                //set order_time
                if (isADD) {
                    mtmpOrder.setOrderTime(calendar.getTime());
                }

                //Tinh expire_time: truong hop don hang duoc tao voi trang thai tao moi: status = 0
                if (ORDER_STATUS_CREATE.equals(mtmpOrder.getStatus())) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                //Tinh expire_time: truong hop don hang do MVAS tao/sua voi trang thai da xac nhan: status = 21
                if (ORDER_STATUS_APPROVAL.equals(mtmpOrder.getStatus())) {
                    mtmpOrder.setApprovalId(userDTL.getUserId());
                    mtmpOrder.setApprovalUser(userDTL.getUserName());
                    mtmpOrder.setApprovalTime(calendar.getTime());
                    if (this.orderPolicy != null && this.orderPolicy.getActiveDays() != null) {
                        calendar.add(Calendar.DAY_OF_YEAR, this.orderPolicy.getActiveDays().intValue() + 1);
                    } else {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    if ("1".equals(mtmpOrder.getPayMethod())) {
                        mtmpOrder.setStatus(ORDER_STATUS_WAITING_MONEY);
                    }
                }
                //set expire_time:
//            mtmpOrder.setExpireTime(DateUtils.truncate(calendar.getTime(), Calendar.DATE));

                if (isADD) {
                    mtmpOrder.setManagerUserId(mmodel.getManagerId(mtmpOrder.getDspCompany().getComId()));
                    mtmpOrder.setManagerMobile(mmodel.getManagerMobile(mtmpOrder.getManagerUserId()));
                    if ("1".equals(mtmpOrder.getDiscountType())) {
                        mtmpOrder.getDspCompany().setBkCheckDate(mtmpOrder.getDspCompany().getCheckDate());
                        mtmpOrder.getDspCompany().setCheckDate(calendar.getTime());
                    }
                    mtmpOrder.setOldStatus(mtmpOrder.getStatus());
                    mmodel.addOrder(mtmpOrder);
                    mlistOrder.add(0, mtmpOrder);

                    //Reset form
                    mtmpOrder = new DSPOrder();

                    //Message to client
                    ClientMessage.logAdd();

                } else if (isEDIT) {
                    mtmpOrder.setManagerMobile(mmodel.getManagerMobile(mtmpOrder.getManagerUserId()));
                    if (ORDER_STATUS_REJECT.equals(mtmpOrder.getOldStatus())) {
                        mtmpOrder.setStatus(ORDER_STATUS_WAITING_APPROVAL);
                        mtmpOrder.setRejectReason(null);
                    }
                    mmodel.editOrder(mtmpOrder);
                    mlistOrder.set(selectedIndex, mtmpOrder);

                    //Message to client
                    ClientMessage.logUpdate();
                }
                this.mlistOrder = mmodel.getListOrder(this.msearchOrder, mdStartDate, mdEndDate, expStartDate, expEndDate, userDTL.getUserId());
                handleCancel();
                PrimeFaces.current().executeScript("clearFilters('tableOrder');");
            }
        }
        //

    }

    /// ///////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleCancel() throws Exception {
        super.handleCancel();
        this.renderApproval = false;
        this.renderReject = false;
        this.renderConfirm = false;
    }

    /// ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleDelete() throws Exception {

    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void handleDelete(DSPOrder order) throws Exception {
        selectedIndex = mlistOrder.indexOf(order);
        mtmpOrder = SerializationUtils.clone(order);
        mtmpOrder.setOldStatus(mtmpOrder.getStatus());
        mtmpOrder.setStatus(ORDER_STATUS_DELETE);
        mtmpOrder.getDspCompany().setCheckDate(mtmpOrder.getDspCompany().getBkCheckDate());
        mmodel.editStatusOrder(mtmpOrder);
        mlistOrder.set(selectedIndex, mtmpOrder);

        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgDeleteSucc"));
//        ClientMessage.logPDelete(RESOURCE_BUNDLE, "msgDeleteSucc");
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void preRejectOrder() throws Exception {
        return;
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void handRejectOrder() throws Exception {
        mtmpOrder.setApprovalId(userDTL.getUserId());
        mtmpOrder.setApprovalUser(userDTL.getUserName());
        mtmpOrder.setOldStatus(mtmpOrder.getStatus());
        mtmpOrder.setStatus(ORDER_STATUS_REJECT);
        mmodel.rejectOrder(mtmpOrder, userDTL.getUserId());
        mlistOrder.set(selectedIndex, mtmpOrder);
        this.handleCancel();

        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgRejectSucc"));
//        ClientMessage.logPUpdate(RESOURCE_BUNDLE, "msgRejectSucc");
        PrimeFaces.current().executeScript("PF('dlgRejectOrder').hide();");
        PrimeFaces.current().executeScript("clearFilters('tableOrder');");
    }

    /// ///////////////////////////////////////////////////////////////////////////////
    public void preConfirmOrder(DSPOrder order) throws Exception {
        this.changeStateView(order);
        this.renderConfirm = true;
    }

    public void handConfirmOrder() throws Exception {
        if (validConfirm()) {
            mtmpOrder.setManagerMobile(mmodel.getManagerMobile(mtmpOrder.getManagerUserId()));
            mtmpOrder.setApprovalId(userDTL.getUserId());
            mtmpOrder.setApprovalUser(userDTL.getUserName());
            mtmpOrder.setOldStatus(mtmpOrder.getStatus());
            mtmpOrder.setStatus(ORDER_STATUS_APPROVAL);
            mmodel.confirmOrder(mtmpOrder);
            mlistOrder.set(selectedIndex, mtmpOrder);
            handleCancel();
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgApprovalSucc"));
            PrimeFaces.current().executeScript("clearFilters('tableOrder');");
        }
    }

    public void preApprovalOrder(DSPOrder order) throws Exception {
        this.changeStateView(order);
        this.renderApproval = true;
        this.renderReject = true;
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void handApprovalOrder() throws Exception {
        if (validApproval()) {
            mtmpOrder.setApprovalId(userDTL.getUserId());
            mtmpOrder.setApprovalUser(userDTL.getUserName());
            mtmpOrder.setOldStatus(mtmpOrder.getStatus());
            if ("1".equals(mtmpOrder.getPayMethod())) {
                mtmpOrder.setStatus(ORDER_STATUS_WAITING_MONEY);
            } else {
                mtmpOrder.setStatus(ORDER_STATUS_APPROVED);
            }
            mmodel.approvalOrder(mtmpOrder);
            mlistOrder.set(selectedIndex, mtmpOrder);
            this.handleCancel();

            //Message to client
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgApprovalSucc"));
//        ClientMessage.logPUpdate(RESOURCE_BUNDLE, "msgApprovalSucc");
            PrimeFaces.current().executeScript("clearFilters('tableOrder');");
        }
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void handTransferApprovalOrder(DSPOrder order) throws Exception {
        selectedIndex = mlistOrder.indexOf(order);
        mtmpOrder = SerializationUtils.clone(order);
        mtmpOrder.setOldStatus(mtmpOrder.getStatus());
        mtmpOrder.setStatus(ORDER_STATUS_WAITING_APPROVAL);
//        if("1".equals(mtmpOrder.getPayMethod())) {
//            mtmpOrder.setStatus(ORDER_STATUS_WAITING_MONEY);
//        }
        mmodel.editStatusOrder(mtmpOrder);
        mlistOrder.set(selectedIndex, mtmpOrder);

        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgTransferApprovalSucc"));
//        ClientMessage.logPUpdate(RESOURCE_BUNDLE, "msgTransferApprovalSucc");
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void handCompleteOrder(DSPOrder order) throws Exception {
        selectedIndex = mlistOrder.indexOf(order);
        mtmpOrder = SerializationUtils.clone(order);
        mtmpOrder.setOldStatus(mtmpOrder.getStatus());
        mtmpOrder.setStatus(ORDER_STATUS_COMPLETE);
        mmodel.editStatusOrder(mtmpOrder);
        mlistOrder.set(selectedIndex, mtmpOrder);

        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgCompleteSucc"));
//        ClientMessage.logPUpdate(RESOURCE_BUNDLE, "msgCompleteSucc");
    }

    /// ///////////////////////////////////////////////////////////////////////////////
    public void handActiveOrder(DSPOrder order) throws Exception {
        selectedIndex = mlistOrder.indexOf(order);
        mtmpOrder = SerializationUtils.clone(order);
        mtmpOrder.setOldStatus(mtmpOrder.getStatus());
        mtmpOrder.setStatus(ORDER_STATUS_ACTIVE);

        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
        Date dateActive = sd.parse(DateUtil.getDateStr(new Date()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateActive);
        calendar.add(Calendar.DATE, -3);
        Date newDate = calendar.getTime();

        if (newDate.after(mtmpOrder.getEffectiveTime())) {
            mtmpOrder.setExpireTime(new Date(mtmpOrder.getExpireTime().getTime() + newDate.getTime() - mtmpOrder.getEffectiveTime().getTime()));
            mtmpOrder.setEffectiveTime(newDate);
        }
        mtmpOrder.setActivatedDate(dateActive);
        mmodel.activeOrder(mtmpOrder);
        mlistOrder.set(selectedIndex, mtmpOrder);

        //Message to client
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgActiveSucc"));
//        ClientMessage.logPUpdate(RESOURCE_BUNDLE, "msgCompleteSucc");
    }

    /// ///////////////////////////////////////////////////////////////////////////////


    public void handleFileUploadOrder(FileUploadEvent event) throws Exception {
        try {
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
            String pathFile = file.getName();
            mtmpOrder.setFilePath(pathFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public DefaultStreamedContent downloadFileOrder(DSPOrder dto) throws Exception {
        try {
            String path = SystemConfig.getConfig("FileUploadPath") + dto.getFilePath();
            return FileUtil.downloadFile(dto.getFilePath(), path);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }


    public String getDateStr(Date date) {
        return DateUtil.getDateStr(date);
    }

    public String getDateTimeStr(Date date) {
        return DateUtil.getDateTimeStr(date);
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public boolean isDisplayBtnAdd() {
        return this.userMvas || this.userCTKV;
    }

    public boolean isDisplayBtnReject() {
        return isDisplayBtnReject(mtmpOrder);
    }

    public boolean isDisplayBtnReject(DSPOrder order) {
        return this.userMvas && order != null && ORDER_STATUS_WAITING_APPROVAL.equals(order.getStatus());
    }

    public boolean isDisplayBtnApproval(DSPOrder order) {
        return this.userMvas && order != null && ORDER_STATUS_WAITING_APPROVAL.equals(order.getStatus());
    }

    public boolean isDisplayBtnActive(DSPOrder order) {
        return this.userMvas && order != null && ORDER_STATUS_APPROVED.equals(order.getStatus());
    }

    public boolean isDisplayBtnTransferApproval(DSPOrder order) {
        return isDisplayBtnAdd() && order != null && ORDER_STATUS_CREATE.equals(order.getStatus())
               && order.getUserId() != null && order.getUserId().longValue() == userDTL.getUserId()
               && (order.getExpireTime() != null && order.getEffectiveTime() != null);
    }

    //trieunv add
    public boolean isDisplayBtnExtension(DSPOrder order) {
        return this.userMvas && order != null && ORDER_STATUS_ACTIVE.equals(order.getStatus())
               && order.getUserId() != null && order.getRemainQuotaValue() > 0;
    }

    public void changeStateOrderExtension(DSPOrder order) throws Exception {
        super.handleCancel();
        indexOrderExt = mlistOrder.indexOf(order);
        if (mlistOrderFiltered != null && mlistOrderFiltered.size() > 0) {
            indexOrderExtFilter = mlistOrderFiltered.indexOf(order);
        }
        dspOrderExt = (DSPOrder) SerializationUtils.clone(order);
        lstDspOrderStatusExtension = dspOrderStatusModel.getListOrderStatus(order.getOrderId());
        isOrderExtension = true;
        oldDateExpire = new Date(order.getExpireTime().getTime() + 1000 * 60 * 60 * 24);
        newDateExpire = oldDateExpire;
        numberDateExpire = 1L;
        filePath = "";
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form_main:table_history_extension");
        dataTable.setValueExpression("sortBy", null);
    }

    public void handDestroyExtension() throws Exception {
        isOrderExtension = false;
        dspOrderExt = null;
    }

    public void handOrderExtension() throws Exception {
        if (numberDateExpire.equals(0L)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "numberDateExpireNotValid"));
        } else {
            dspOrderStatusModel.updateOrderExtension(dspOrderExt, newDateExpire, filePath, userDTL.getUserId());
            lstDspOrderStatusExtension = dspOrderStatusModel.getListOrderStatus(dspOrderExt.getOrderId());
            dspOrderExt.setExpireTime(newDateExpire);
            mlistOrder.set(indexOrderExt, dspOrderExt);
            if (indexOrderExtFilter >= 0) {
                mlistOrderFiltered.set(indexOrderExtFilter, dspOrderExt);
            }
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgExtensionSucc"));
            handDestroyExtension();
        }
    }

    public void onDateSelect(SelectEvent event) throws Exception {
        Date dateChoose = (Date) event.getObject();
        numberDateExpire = (dateChoose.getTime() - dspOrderExt.getExpireTime().getTime()) / (1000 * 60 * 60 * 24);
    }

    public void onNumberDate() {
        newDateExpire = new Date(dspOrderExt.getExpireTime().getTime() + numberDateExpire * 1000 * 60 * 60 * 24);
    }

    public void handleFileUploadFile(FileUploadEvent event) {
        try {
            File file = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
            filePath = file.getName();
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public DefaultStreamedContent downloadFileServicePriceTab(String namefile) throws Exception {
        try {
            String path = SystemConfig.getConfig("FileUploadPath") + namefile;
            return FileUtil.downloadFile(namefile, path);
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    public boolean isDisplayBtnEdit(DSPOrder order) {
        return isDisplayBtnAdd() && order != null && (ORDER_STATUS_CREATE.equals(order.getStatus()) || ORDER_STATUS_REJECT.equals(order.getStatus()))
               && order.getUserId() != null && order.getUserId().longValue() == userDTL.getUserId();
    }

    public boolean isDisplayBtnDelete(DSPOrder order) {
        return isDisplayBtnAdd() && order != null && order.getUserId().longValue() == userDTL.getUserId()
               && (ORDER_STATUS_CREATE.equals(order.getStatus()) || ORDER_STATUS_REJECT.equals(order.getStatus()))
               && (order.getExpireTime() != null && order.getEffectiveTime() != null);
    }

    public boolean isDisplayBtnComplete(DSPOrder order) {
        return this.userMvas && order != null && ORDER_STATUS_ACTIVE.equals(order.getStatus())
               && ((order.getRemainValue() != null ? order.getRemainValue().longValue() : 0l) <= 0l || order.getExpireTime().compareTo(currentDate) < 0);
    }

    /// ///////////////////////////////////////////////////////////////////////////////

    public void exportToExcel() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        /*String fileName = MessageFormat.format("MTMO{0}-{1}",
                dateFormat.format(mtmpRequest.getFromDate()), dateFormat.format(mtmpRequest.getToDate()));*/
        String fileName = "Danh sach don hang";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 11));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 11));

        // Style cho tiêu đề
        CellStyle centerStyle = workbook.createCellStyle();
        Font fontTitle = workbook.createFont();
        fontTitle.setFontHeightInPoints((short) 16);
        fontTitle.setFontName(FONT_NAME);
        fontTitle.setBoldweight(Font.BOLDWEIGHT_BOLD);
        centerStyle.setFont(fontTitle);
        centerStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // Style ghi chu
        CellStyle centerStyleNote = workbook.createCellStyle();
        Font fontTitleNote = workbook.createFont();
        fontTitleNote.setFontHeightInPoints((short) 14);
        fontTitleNote.setFontName(FONT_NAME);
        centerStyleNote.setFont(fontTitleNote);
        centerStyleNote.setAlignment(CellStyle.ALIGN_CENTER);

        CellStyle headerStyle = workbook.createCellStyle();
        CellStyle cellStyle = workbook.createCellStyle();

        Font font = workbook.createFont();
        Font fontCell = workbook.createFont();
        //font
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT_NAME);

        fontCell.setFontHeightInPoints((short) 12);
        fontCell.setFontName(FONT_NAME);

        //border
        cellStyle.setWrapText(true);
        System.out.println(cellStyle.getWrapText());
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //text-align
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        cellStyle.setFont(fontCell);
        //style header
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFont(font);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);

        CellStyle numberFormatStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        numberFormatStyle.setDataFormat(dataFormat.getFormat("#,###"));
        //border
        numberFormatStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        numberFormatStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        numberFormatStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        numberFormatStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        //text-align
        numberFormatStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        numberFormatStyle.setFont(fontCell);
        numberFormatStyle.setWrapText(true);

        // title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "list_order"));
        titleCell.getCellStyle().setAlignment(CellStyle.ALIGN_GENERAL);
        titleCell.setCellStyle(centerStyle);

        //Tạo chú thích cho bảng
        Row detailRow = sheet.createRow(1);
        Cell detailCell = detailRow.createCell(0);
        detailCell.setCellValue(MessageFormat.format("{0} {1} {2} {3}",
                ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "createTimeFrom") + ": ",
                mdStartDate != null ? dateFormat.format(mdStartDate) : "",
                ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "createTimeTo") + ": ",
                mdEndDate != null ? dateFormat.format(mdEndDate) : ""));
        detailCell.getCellStyle().setAlignment(CellStyle.ALIGN_GENERAL);
        detailCell.setCellStyle(centerStyleNote);

        Row detailRow1 = sheet.createRow(2);
        Cell detailCell1 = detailRow1.createCell(0);
        detailCell1.setCellValue(MessageFormat.format("{0} {1} {2} {3}",
                ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "expTimeFrom") + ": ",
                expStartDate != null ? dateFormat.format(expStartDate) : "",
                ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "expTimeTo") + ": ",
                expEndDate != null ? dateFormat.format(expEndDate) : ""));
        detailCell1.getCellStyle().setAlignment(CellStyle.ALIGN_GENERAL);
        detailCell1.setCellStyle(centerStyleNote);

        List<String> lstHeader = getAllHeader("form_main:table_order");

        Row headerRow = sheet.createRow(3);
        int indexHeader = 0;
        for (String data : lstHeader) {
            Cell cell = headerRow.createCell(indexHeader++);
            cell.setCellValue(data);
        }

        int indexTable = 4;
        for (DSPOrder object : mlistOrder) {
            Row dataRow = sheet.createRow(indexTable++);
            int i = 0;
            Cell cell1 = dataRow.createCell(i++);
            cell1.setCellValue(object.getOrderId());

            Cell cell2 = dataRow.createCell(i++);
            cell2.setCellValue(object.getContractCode());

            Cell cell3 = dataRow.createCell(i++);
            cell3.setCellValue(object.getContractValue());

            Cell cell4 = dataRow.createCell(i++);
            cell4.setCellValue(object.getRemainQuotaValue() != null ? object.getRemainQuotaValue() : 0);

            Cell cell5 = dataRow.createCell(i++);
            cell5.setCellValue(object.getReservedValue() != null ? object.getReservedValue() : 0);

            Cell cell6 = dataRow.createCell(i++);
            cell6.setCellValue(object.getMoneyUsed() != null ? object.getMoneyUsed() : 0);

            Cell cell7 = dataRow.createCell(i++);
            cell7.setCellValue(object.getDiscountPercent() != null ? object.getDiscountPercent() : 0);

            Cell cell8 = dataRow.createCell(i++);
            cell8.setCellValue(object.getUserName());

            Cell cell9 = dataRow.createCell(i++);
            if (object.getOrderTime() != null)
                cell9.setCellValue(sdf.format(object.getOrderTime()));

            Cell cell10 = dataRow.createCell(i++);
            if (object.getEffectiveTime() != null)
                cell10.setCellValue(dateFormat.format(object.getEffectiveTime()));

            Cell cell11 = dataRow.createCell(i++);
            if (object.getExpireTime() != null)
                cell11.setCellValue(dateFormat.format(object.getExpireTime()));

            Cell cell12 = dataRow.createCell(i);
            cell12.setCellValue(getStatusLabel(object.getStatus()));
        }

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (row.getRowNum() == 3) {
                    cell.setCellStyle(headerStyle);
                } else if (row.getRowNum() > 3) {
                    if(cell.getColumnIndex() == 0) {
                        cell.setCellStyle(cellStyle);
                        continue;
                    }
                    if (!(cell.getCellType() == 0)) {
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell.setCellStyle(numberFormatStyle);
                    }
                }
            }
        }

        for (int i = 0; i < lstHeader.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        downloadExcel(workbook, fileName);
    }

    public List<String> getAllHeader(String tableId) {
        FacesContext context = FacesContext.getCurrentInstance();
        DataTable dataTable = (DataTable) context.getViewRoot().findComponent(tableId);
        List<String> headers = new ArrayList<>();
        if (dataTable != null) {
            List<UIColumn> columns = dataTable.getColumns();
            for (UIColumn column : columns) {
                String headerText = column.getHeaderText();
                headers.add(headerText);
            }
        }
        if (!headers.isEmpty()) {
            headers.remove(headers.size() - 1);
        }
        return headers;
    }

    private String getStatusLabel(String status) {
        if (status == null || status.isEmpty()) {
            return "";
        }
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("0", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_0"));
        statusMap.put("6", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_6"));
        statusMap.put("1", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_1"));
        statusMap.put("2", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_2"));
        statusMap.put("3", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_3"));
        statusMap.put("4", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_4"));
        statusMap.put("5", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_5"));
        statusMap.put("8", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_8"));
        statusMap.put("21", ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "status_21"));
        return statusMap.getOrDefault(status, status);
    }

    public void downloadExcel(Workbook excel, String fileName) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            excel.write(outputStream);
            // Thiết lập các thông tin phản hồi cho tệp Excel
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", MessageFormat.format("attachment; filename={0}.xlsx", fileName));

            // Gửi dữ liệu Excel đến phản hồi HTTP
            try (OutputStream output = response.getOutputStream()) {
                output.write(outputStream.toByteArray());
            }
            response.setStatus(HttpServletResponse.SC_OK);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            throw e;
        }
    }
    /// //////////////////////////////////////////////////////////////////////////////

    //Getters, Setters
    public Date getCurrentDate() {
        return DateUtils.truncate(new Date(), Calendar.DATE);
    }

    public DSPOrder getMsearchOrder() {
        return msearchOrder;
    }

    public void setMsearchOrder(DSPOrder msearchOrder) {
        this.msearchOrder = msearchOrder;
    }

    public Date getMdStartDate() {
        return mdStartDate;
    }

    public void setMdStartDate(Date mdStartDate) {
        this.mdStartDate = mdStartDate;
    }

    public Date getMdEndDate() {
        return mdEndDate;
    }

    public void setMdEndDate(Date mdEndDate) {
        this.mdEndDate = mdEndDate;
    }

    public List<DSPOrder> getMlistOrder() {
        return mlistOrder;
    }

    public void setMlistOrder(List<DSPOrder> mlistOrder) {
        this.mlistOrder = mlistOrder;
    }

    public List<DSPOrder> getMlistOrderFiltered() {
        return mlistOrderFiltered;
    }

    public void setMlistOrderFiltered(List<DSPOrder> mlistOrderFiltered) {
        this.mlistOrderFiltered = mlistOrderFiltered;
    }

    public List<DSPCompany> getMlistCompany() {
        return mlistCompany;
    }

    public void setMlistCompany(List<DSPCompany> mlistCompany) {
        this.mlistCompany = mlistCompany;
    }

    public DSPOrder getMtmpOrder() {
        return mtmpOrder;
    }

    public void setMtmpOrder(DSPOrder mtmpOrder) {
        this.mtmpOrder = mtmpOrder;
    }

    public boolean isUserMvas() {
        return userMvas;
    }

    public void setUserMvas(boolean userMvas) {
        this.userMvas = userMvas;
    }

    public boolean isUserCTKV() {
        return userCTKV;
    }

    public void setUserCTKV(boolean userCTKV) {
        this.userCTKV = userCTKV;
    }

    public boolean isUserRetailer() {
        return userRetailer;
    }

    public void setUserRetailer(boolean userRetailer) {
        this.userRetailer = userRetailer;
    }

    public boolean isIsUserCompany() {
        return isUserCompany;
    }

    public String getMstrCompanySearch() {
        return mstrCompanySearch;
    }

    public void setMstrCompanySearch(String mstrCompanySearch) {
        this.mstrCompanySearch = mstrCompanySearch;
    }

    public boolean isOrderExtension() {
        return isOrderExtension;
    }

    public void setOrderExtension(boolean orderExtension) {
        isOrderExtension = orderExtension;
    }

    public List<DSPOrderStatus> getLstDspOrderStatusExtension() {
        return lstDspOrderStatusExtension;
    }

    public void setLstDspOrderStatusExtension(List<DSPOrderStatus> lstDspOrderStatusExtension) {
        this.lstDspOrderStatusExtension = lstDspOrderStatusExtension;
    }

    public Date getOldDateExpire() {
        return oldDateExpire;
    }

    public void setOldDateExpire(Date oldDateExpire) {
        this.oldDateExpire = oldDateExpire;
    }

    public Long getNumberDateExpire() {
        return numberDateExpire;
    }

    public void setNumberDateExpire(Long numberDateExpire) {
        this.numberDateExpire = numberDateExpire;
    }

    public Date getNewDateExpire() {
        return newDateExpire;
    }

    public void setNewDateExpire(Date newDateExpire) {
        this.newDateExpire = newDateExpire;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<DSPOrderStatus> getLstDspOrderStatusExtensionFiltered() {
        return lstDspOrderStatusExtensionFiltered;
    }

    public void setLstDspOrderStatusExtensionFiltered(List<DSPOrderStatus> lstDspOrderStatusExtensionFiltered) {
        this.lstDspOrderStatusExtensionFiltered = lstDspOrderStatusExtensionFiltered;
    }

    public boolean isRenderApproval() {
        return renderApproval;
    }

    public void setRenderApproval(boolean renderApproval) {
        this.renderApproval = renderApproval;
    }

    public boolean isRenderReject() {
        return renderReject;
    }

    public void setRenderReject(boolean renderReject) {
        this.renderReject = renderReject;
    }

    public boolean isRenderConfirm() {
        return renderConfirm;
    }

    public void setRenderConfirm(boolean renderConfirm) {
        this.renderConfirm = renderConfirm;
    }

    public UserDTL getUserDTL() {
        return userDTL;
    }

    public void setUserDTL(UserDTL userDTL) {
        this.userDTL = userDTL;
    }

    public Date getExpStartDate() {
        return expStartDate;
    }

    public void setExpStartDate(Date expStartDate) {
        this.expStartDate = expStartDate;
    }

    public Date getExpEndDate() {
        return expEndDate;
    }

    public void setExpEndDate(Date expEndDate) {
        this.expEndDate = expEndDate;
    }
}
