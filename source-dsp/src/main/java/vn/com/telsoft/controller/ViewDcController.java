package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.TelsoftException;
import com.faplib.lib.util.ResourceBundleUtil;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DSPTransaction;
import vn.com.telsoft.model.ViewDcModel;
import vn.com.telsoft.util.Utils;
import vn.com.telsoft.ws.DcApiRequest;
import vn.com.telsoft.ws.DcApiResponse;
import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.DspApiRequest;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
@ViewScoped
public class ViewDcController extends TSFuncTemplate implements Serializable {
    static final String RESOURCE_BUNDLE = "PP_DATACODE";
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";

    private DcApiResponse mtmpDcApiResponse;
    private String serial;
    private int render = 0;

    private DSPCompany dspCompany;
    private DSPCompany dspCompanyParent;
    private DSPTransaction dspTransaction;

    private String[] arrRef;
    private String isdn;
    private String tranId;

    private Date expDate;
    private Date creDate;
    private Date actDate;
    private Date suspDate;
    private Date useDate;

    public ViewDcController() {
        mtmpDcApiResponse = new DcApiResponse();
        dspCompany = new DSPCompany();
        dspCompanyParent = new DSPCompany();
        dspTransaction = new DSPTransaction();
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String parameter = params.get("serialUsed");
            if (parameter != null && !parameter.isEmpty()) {
                serial = parameter;
            }
        } catch (Exception ex) {
            serial = null;
        }
    }

    public void viewDc() throws Exception {
        if (!Utils.validateSerialDataCode(this.serial)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_DEPOSITDATABYDATACODE", "ERR_NOTVALID_SERIAL"));
            return;
        }
        mtmpDcApiResponse = new DcApiResponse();
        expDate = null;
        creDate = null;
        actDate = null;
        suspDate = null;
        useDate = null;
        isdn = null;
        DspApiRequest request = new DspApiRequest();
        request.setSerial(serial);
        DspApiClient dspApiClient = new DspApiClient();
        DcApiResponse response = dspApiClient.viewdc(request);
        if (response != null) {
            //Message to client
            if ("0".equals(response.getCode())) {
                mtmpDcApiResponse = response;
                mtmpDcApiResponse.setDataAmount(String.valueOf(Integer.parseInt(mtmpDcApiResponse.getDataAmount()) / 1024));

                ViewDcModel viewDcModel = new ViewDcModel();
                dspCompany = viewDcModel.getCompany(mtmpDcApiResponse.getReseller());
                dspCompanyParent = viewDcModel.getParent(mtmpDcApiResponse.getReseller());

                if (mtmpDcApiResponse.getRef() != null && !mtmpDcApiResponse.getRef().trim().isEmpty()) {
                    arrRef = mtmpDcApiResponse.getRef().split("\\|");
                    isdn = arrRef[1];
                }

                if (mtmpDcApiResponse.getOrderCode() != null && !mtmpDcApiResponse.getOrderCode().trim().isEmpty()) {
                    dspTransaction = viewDcModel.getTransaction(mtmpDcApiResponse.getOrderCode());
                }

                if (mtmpDcApiResponse.getExpDate() != null && !mtmpDcApiResponse.getExpDate().trim().isEmpty()) {
                    expDate = convertStringToDate(mtmpDcApiResponse.getExpDate());
                }
                if (mtmpDcApiResponse.getCreDate() != null && !mtmpDcApiResponse.getCreDate().trim().isEmpty()) {
                    creDate = convertStringToDate(mtmpDcApiResponse.getCreDate());
                }
                if (mtmpDcApiResponse.getActDate() != null && !mtmpDcApiResponse.getActDate().trim().isEmpty()) {
                    actDate = convertStringToDate(mtmpDcApiResponse.getActDate());
                }
                if (mtmpDcApiResponse.getSuspDate() != null && !mtmpDcApiResponse.getSuspDate().trim().isEmpty()) {
                    suspDate = convertStringToDate(mtmpDcApiResponse.getSuspDate());
                }
                if (mtmpDcApiResponse.getUseDate() != null && !mtmpDcApiResponse.getUseDate().trim().isEmpty()) {
                    useDate = convertStringToDate(mtmpDcApiResponse.getUseDate());
                }
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
//                ClientMessage.log(ClientMessage.MESSAGE_TYPE.UPDATE, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "msgExtendSucc"));
                render = 1;
            } else {
//                if ("5001".equals(response.getCode())) {
//                }
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
                render = 0;
            }
        }
    }

    public String dateToString(Date input) {
        if (input == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return df.format(input);
    }

    public Date convertStringToDate(String strDate) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            String zoneId = "";
            Pattern pattern = Pattern.compile("(\\[.*?\\])");
            Matcher matcher = pattern.matcher(strDate);
            if (matcher.find()) {
                zoneId = matcher.group(1);
                strDate = strDate.replace(zoneId, "");
                zoneId = zoneId.replace("[", "").replace("]", "");
            }
            formatter.setTimeZone(TimeZone.getTimeZone(zoneId));
            return formatter.parse(strDate.replaceAll("Z$", "+0000"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void validInput() {
        if (!Utils.validateSerialDataCode(this.serial)) {
//            throw new TelsoftException("ERR_NOTVALID_SERIAL");
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_DEPOSITDATABYDATACODE", "ERR_NOTVALID_SERIAL"));
            return;
        }
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {

    }

    public DcApiResponse getMtmpDcApiResponse() {
        return mtmpDcApiResponse;
    }

    public void setMtmpDcApiResponse(DcApiResponse mtmpDcApiResponse) {
        this.mtmpDcApiResponse = mtmpDcApiResponse;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        if (serial != null) {
            this.serial = serial.replaceAll("-", "");
        }
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public DSPCompany getDspCompany() {
        return dspCompany;
    }

    public void setDspCompany(DSPCompany dspCompany) {
        this.dspCompany = dspCompany;
    }

    public DSPCompany getDspCompanyParent() {
        return dspCompanyParent;
    }

    public void setDspCompanyParent(DSPCompany dspCompanyParent) {
        this.dspCompanyParent = dspCompanyParent;
    }

    public DSPTransaction getDspTransaction() {
        return dspTransaction;
    }

    public void setDspTransaction(DSPTransaction dspTransaction) {
        this.dspTransaction = dspTransaction;
    }

    public String[] getArrRef() {
        return arrRef;
    }

    public void setArrRef(String[] arrRef) {
        this.arrRef = arrRef;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Date getCreDate() {
        return creDate;
    }

    public void setCreDate(Date creDate) {
        this.creDate = creDate;
    }

    public Date getActDate() {
        return actDate;
    }

    public void setActDate(Date actDate) {
        this.actDate = actDate;
    }

    public Date getSuspDate() {
        return suspDate;
    }

    public void setSuspDate(Date suspDate) {
        this.suspDate = suspDate;
    }

    public Date getUseDate() {
        return useDate;
    }

    public void setUseDate(Date useDate) {
        this.useDate = useDate;
    }
}
