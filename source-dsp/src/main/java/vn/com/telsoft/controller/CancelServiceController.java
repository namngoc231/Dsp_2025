/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.primefaces.shaded.json.JSONException;
import vn.com.telsoft.entity.ApParam;
import vn.com.telsoft.model.DSPSubServiceModel;
import vn.com.telsoft.util.Utils;
import vn.com.telsoft.ws.DspApiCancelServiceRequest;
import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.DspApiSerivceInfoResponse;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Named
@ViewScoped
public class CancelServiceController extends TSFuncTemplate implements Serializable {
    //bundle
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    static final String RESOURCE_BUNDLE = "PP_SUBSERVICE";
    private String isdn, serviceType, reason;
//    private List<DSPSubService> lstDspSubService;
    private List<ApParam> lstDspSubService;

    private DSPSubServiceModel mmodel;

    public CancelServiceController() throws Exception{
        lstDspSubService = new ArrayList<>();
        mmodel = new DSPSubServiceModel();
        lstDspSubService = mmodel.getServiceType();
    }
    //////////////////////////////////////////////////////////////////////////////////

    public void handCheck() {
        try {
            String url = "";
            for (ApParam object : lstDspSubService) {
                if (Objects.equals(object.getParValue(), serviceType) && object.getDescription() != null)
                    url = object.getDescription();
            }

            DspApiCancelServiceRequest request = new DspApiCancelServiceRequest();
            request.setIsdn(isdn);
            request.setServiceType(serviceType);
            request.setReason(reason);
            DspApiClient dspApiClient = new DspApiClient();
            DspApiSerivceInfoResponse response = dspApiClient.checkCancelService(request, url);
            if (response != null) {
                //Message to client
                if ("0".equals(response.getCode())) {
                    ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "cancelServiceSuccess"));
                } else {
                    if ("7005".equals(response.getCode()))
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_DATA_AMOUNT_EXISTS"));
                    else if ("7006".equals(response.getCode()))
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "ERR_SUB_DO_NOT_USE_SRV"));
                    else if ("500".equals(response.getCode()))
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errorApi"));
                    else
                        ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
                }
            } else {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errorNoData"));
            }
        } catch (ConnectException | JSONException e) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errorApi"));

        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }


    ////////////////////////////////////////////////////////////////////

    @Override
    public void changeStateAdd() {
    }
    //////////////////////////////////////////////////////////////////////////////////

    public void changeStateEdit() {
    }
    //////////////////////////////////////////////////////////////////////////////////


    @Override
    public void handleOK() {
    }
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleCancel() throws Exception {
        super.handleCancel();
    }

    //////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleDelete() throws Exception {

    }
    //////////////////////////////////////////////////////////////////////////////////

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
        if (isdn != null) {
            this.isdn = Utils.fixIsdnWithout0and84(isdn.replaceAll("[ ()]", ""));
        }
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ApParam> getLstDspSubService() {
        return lstDspSubService;
    }

    public void setLstDspSubService(List<ApParam> lstDspSubService) {
        this.lstDspSubService = lstDspSubService;
    }
}
