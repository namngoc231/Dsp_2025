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
import vn.com.telsoft.entity.DSPSubService;
import vn.com.telsoft.model.DSPSubServiceModel;
import vn.com.telsoft.util.Utils;
import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.DspApiRequest;
import vn.com.telsoft.ws.DspApiSerivceInfoResponse;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Named
@ViewScoped
public class SubServiceController extends TSFuncTemplate implements Serializable {
    //bundle
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    static final String RESOURCE_BUNDLE = "PP_SUBSERVICE";
    private DSPSubService mSubService;
    private List<DSPSubService> mListSubService;
    private List<DSPSubService> mListSubServiceFiltered;
    private String isdn;
    private Boolean hiddenBtn;

    private DSPSubServiceModel mmodel;


    public SubServiceController() {
        mListSubService = new ArrayList();
        mSubService = new DSPSubService();
        mmodel = new DSPSubServiceModel();
        hiddenBtn = false;
    }
    //////////////////////////////////////////////////////////////////////////////////

    public void handSearch() {
        try {
            this.mListSubService = mmodel.getSubService(isdn);
            for (DSPSubService subService : mListSubService) {
                subService.setInitialAmount(null);
            }
            if (!mListSubService.isEmpty())
                hiddenBtn = true;
            this.mListSubServiceFiltered = null;

        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public void getApiServiceInfo() throws Exception {
        try {
            if (!mListSubService.isEmpty() && mListSubService != null) {
                DspApiRequest request = new DspApiRequest();
                request.setMsisdn(mListSubService.get(0).getIsdn());
                List<String> listServices = new ArrayList<>();
                for (DSPSubService subService : mListSubService)
                    listServices.add(subService.getService());
                request.setServices(listServices);
                DspApiClient dspApiClient = new DspApiClient();
                DspApiSerivceInfoResponse response = dspApiClient.getServiceInfo(request);
                if (response != null) {
                    //Message to client
                    if ("0".equals(response.getCode())) {
                        List<DSPSubService> mListSubServiceApi = response.getListServices();

                        if (mListSubServiceApi != null && !mListSubServiceApi.isEmpty()) {
                            for (DSPSubService subService : mListSubServiceApi) {
                                for (DSPSubService sub : mListSubService) {
                                    if (subService.getService().equalsIgnoreCase(sub.getService())) {
                                        sub.setStartTime(subService.getStartTime());
                                        sub.setEndTime(subService.getEndTime());
                                        sub.setInitialAmount(subService.getInitialAmount() / 1024);
                                    }
                                }
                            }
                        }
                        hiddenBtn = false;
                    } else {
                        if ("500".equals(response.getCode()))
                            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errorApi"));
                        else
                            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getCode()));
                    }
                }
            } else {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errorNoData"));
            }
        } catch (ConnectException | JSONException e) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errorApi"));
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            throw ex;
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


    public String getDateStr(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sd.format(date);
    }

    public List<DSPSubService> getmListSubService() {
        return mListSubService;
    }

    public void setmListSubService(List<DSPSubService> mListSubService) {
        this.mListSubService = mListSubService;
    }

    public List<DSPSubService> getmListSubServiceFiltered() {
        return mListSubServiceFiltered;
    }

    public void setmListSubServiceFiltered(List<DSPSubService> mListSubServiceFiltered) {
        this.mListSubServiceFiltered = mListSubServiceFiltered;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
        if (isdn != null) {
            this.isdn = Utils.fixIsdnWithout0and84(isdn.replaceAll("[ ()]", ""));
        }
    }

    public Boolean getHiddenBtn() {
        return hiddenBtn;
    }

    public void setHiddenBtn(Boolean hiddenBtn) {
        this.hiddenBtn = hiddenBtn;
    }
}
