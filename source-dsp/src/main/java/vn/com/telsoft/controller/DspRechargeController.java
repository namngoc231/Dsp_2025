package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.DateUtil;
import org.primefaces.PrimeFaces;
import org.primefaces.shaded.json.JSONException;
import vn.com.telsoft.entity.DSPRecharge;
import vn.com.telsoft.model.DSPRechargeModel;
import vn.com.telsoft.util.Utils;
import vn.com.telsoft.ws.DcApiResponse;
import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.DspApiRechargeRequest;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class DspRechargeController extends TSFuncTemplate implements Serializable {
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    static final String RESOURCE_BUNDLE = "PP_NON_VOUCHER_RECHARGE";

    private List<DSPRecharge> mlistDSPRecharge;
    private List<DSPRecharge> mlistDSPRechargeFilterred;
    private DSPRecharge[] mselectedDSPRecharge;
    private DSPRecharge mtmpDSPRecharge;

    private String isdn;
    private Date fromDate;
    private Date toDate;
    private String status;

    private DSPRechargeModel dspRechargeModel;

    private DcApiResponse mtmpDcApiResponse;
    private String resultMes;

    private int render = 0;

    public DspRechargeController() throws Exception {
        dspRechargeModel = new DSPRechargeModel();
        mlistDSPRecharge = new ArrayList<>();
        mlistDSPRechargeFilterred = new ArrayList<>();
        mtmpDSPRecharge = new DSPRecharge();
        mtmpDcApiResponse = new DcApiResponse();
        isdn = null;
        fromDate = null;
        toDate = null;
        status = null;
    }

    public void handSearch() {
        try {
            String strFromDate = dateStr(fromDate);
            String strToDate = dateStr(toDate);
            mlistDSPRecharge = dspRechargeModel.getlist(Utils.fixIsdnWithout0and84(isdn), strFromDate, strToDate, status);
            mlistDSPRechargeFilterred = null;
            render = 1;
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public void changeStateRecharge(DSPRecharge obj) throws Exception {
//        render = 2;
        mtmpDSPRecharge = obj;
    }

    public void onConfirmRecharge(DSPRecharge obj) throws Exception {
        try {
            mtmpDcApiResponse = new DcApiResponse();
            resultMes = null;
            mtmpDSPRecharge = obj;

            String profile = mtmpDSPRecharge.getProfileCode() != null && !mtmpDSPRecharge.getProfileCode().isEmpty() ? mtmpDSPRecharge.getProfileCode() : "";

            DspApiRechargeRequest request = new DspApiRechargeRequest();
            request.setTransactionId("test");
            request.setIsdn(mtmpDSPRecharge.getIsdn());
            request.setSerial(mtmpDSPRecharge.getSerial());
            request.setRef(mtmpDSPRecharge.getRef());
            request.setAmount(mtmpDSPRecharge.getAmount());
            request.setDays(mtmpDSPRecharge.getDays());
//        request.setWebUsername(AdminUser.getUserLogged().getUserName());
            request.setProfileCode(profile);
            request.setAddon(mtmpDSPRecharge.getAddon());

            int stt = dspRechargeModel.updateStatusTemp(mtmpDSPRecharge.getIsdn(), mtmpDSPRecharge.getSerial());

            if (stt > 0) {
                try {
                    DspApiClient dspApiClient = new DspApiClient();
                    DcApiResponse response = dspApiClient.nonVoucherRecharge(request);

                    if (response != null) {
                        //Message to client
                        if ("0".equals(response.getCode())) {
                            mtmpDcApiResponse = response;
                            mtmpDSPRecharge.setStatus("1");
                            dspRechargeModel.updateStatus(mtmpDSPRecharge);

                            resultMes = ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode());
                            PrimeFaces.current().executeScript("PF('dialogResult').show();");
                        } else {
                            if(response.getCode().equals("6003") || response.getCode().equals("-1")) {
                                dspRechargeModel.updateStatusBack(mtmpDSPRecharge.getIsdn(), mtmpDSPRecharge.getSerial());
                                resultMes = ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, response.getCode());
                                PrimeFaces.current().executeScript("PF('dialogResult').show();");
                            } else if (response.getCode().equals("500")) {
                                dspRechargeModel.updateStatusBack(mtmpDSPRecharge.getIsdn(), mtmpDSPRecharge.getSerial());
                                resultMes = ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, response.getCode());
                                PrimeFaces.current().executeScript("PF('dialogResult').show();");
                            } else {
                                dspRechargeModel.updateStatusBack(mtmpDSPRecharge.getIsdn(), mtmpDSPRecharge.getSerial());
                                resultMes = ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode());
                                PrimeFaces.current().executeScript("PF('dialogResult').show();");
                            }
                        }
                    }
                    PrimeFaces.current().executeScript("PF('dialogRecharge').hide();");
                } catch (Exception e) {
                    dspRechargeModel.updateStatusBack(mtmpDSPRecharge.getIsdn(), mtmpDSPRecharge.getSerial());
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "500"));
                    PrimeFaces.current().executeScript("PF('dialogRecharge').hide();");
                }
            } else {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "7020"));
                PrimeFaces.current().executeScript("PF('dialogRecharge').hide();");
            }
        } catch (ConnectException | JSONException e) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "500"));
            PrimeFaces.current().executeScript("PF('dialogRecharge').hide();");
        }
    }

    public String dateToString(Date date) {
        return DateUtil.getDateTimeStr(date);
    }

    public static String dateStr(Date date) {
        if (date == null) {
            return "";
        } else {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            return formatter.format(date);
        }
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {

    }

    public static String getApiDatacodeResourceBundle() {
        return API_DATACODE_RESOURCE_BUNDLE;
    }

    public List<DSPRecharge> getMlistDSPRecharge() {
        return mlistDSPRecharge;
    }

    public void setMlistDSPRecharge(List<DSPRecharge> mlistDSPRecharge) {
        this.mlistDSPRecharge = mlistDSPRecharge;
    }

    public List<DSPRecharge> getMlistDSPRechargeFilterred() {
        return mlistDSPRechargeFilterred;
    }

    public void setMlistDSPRechargeFilterred(List<DSPRecharge> mlistDSPRechargeFilterred) {
        this.mlistDSPRechargeFilterred = mlistDSPRechargeFilterred;
    }

    public DSPRecharge[] getMselectedDSPRecharge() {
        return mselectedDSPRecharge;
    }

    public void setMselectedDSPRecharge(DSPRecharge[] mselectedDSPRecharge) {
        this.mselectedDSPRecharge = mselectedDSPRecharge;
    }

    public DSPRecharge getMtmpDSPRecharge() {
        return mtmpDSPRecharge;
    }

    public void setMtmpDSPRecharge(DSPRecharge mtmpDSPRecharge) {
        this.mtmpDSPRecharge = mtmpDSPRecharge;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DSPRechargeModel getDspRechargeModel() {
        return dspRechargeModel;
    }

    public void setDspRechargeModel(DSPRechargeModel dspRechargeModel) {
        this.dspRechargeModel = dspRechargeModel;
    }

    public DcApiResponse getMtmpDcApiResponse() {
        return mtmpDcApiResponse;
    }

    public void setMtmpDcApiResponse(DcApiResponse mtmpDcApiResponse) {
        this.mtmpDcApiResponse = mtmpDcApiResponse;
    }

//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getTransId() {
//        return transId;
//    }
//
//    public void setTransId(String transId) {
//        this.transId = transId;
//    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public String getResultMes() {
        return resultMes;
    }

    public void setResultMes(String resultMes) {
        this.resultMes = resultMes;
    }
}
