package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.primefaces.shaded.json.JSONException;
import vn.com.telsoft.entity.DipPackage;
import vn.com.telsoft.entity.DipSubService;
import vn.com.telsoft.entity.DspDipSubServiceInfo;
import vn.com.telsoft.model.DipSubServiceModel;
import vn.com.telsoft.util.Utils;
import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.DspApiRequest;
import vn.com.telsoft.ws.DspDipSubApiResponse;

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
public class DipSubServiceController extends TSFuncTemplate implements Serializable {

    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    static final String RESOURCE_BUNDLE = "PP_SUBSERVICE";
    private DipSubService mDipSubService;
    private List<DipSubService> mListDipSubService;
    private List<DipSubService> mListDipSubServiceFiltered;
    private String isdn;
    private Boolean hiddenBtn;
    private DipSubServiceModel mmodel;

    private List<DipPackage> mlistDipPackage;

    public DipSubServiceController() throws Exception {
        mListDipSubService = new ArrayList<>();
        mDipSubService = new DipSubService();
        mmodel = new DipSubServiceModel();
        hiddenBtn = false;
        mlistDipPackage = mmodel.getListPackageService();
    }

    public void handSearch() {
        try {
            this.mListDipSubService = mmodel.getSubService(isdn);
            for (DipSubService dipSubService : mListDipSubService) {
                dipSubService.setInitialAmount(null);
            }
            if (!mListDipSubService.isEmpty())
                hiddenBtn = true;
            this.mListDipSubServiceFiltered = null;
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex, ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

    public void getApiServiceInfo() throws Exception {
        try {
            if (!mListDipSubService.isEmpty()) {
                DspApiRequest request = new DspApiRequest();
                request.setMsisdn(isdn);
                DspApiClient dspApiClient = new DspApiClient();
                DspDipSubApiResponse response = dspApiClient.getDspDipSubServiceInfo(request);
                if (response != null) {
                    //Message to client
                    if ("0".equals(response.getCode())) {
                        List<DspDipSubServiceInfo> mListDipSubServiceApi = response.getListServices();
                        if (mListDipSubServiceApi != null && !mListDipSubServiceApi.isEmpty()) {
                            for (DspDipSubServiceInfo subService : mListDipSubServiceApi) {
                                /*for (DipPackage sub : mlistDipPackage) {
                                    if (subService.getServiceName().equalsIgnoreCase(sub.getProvCode())) {
                                        for (DipSubService dipSub: mListDipSubService){
                                            if (dipSub.getPackageId().equals(sub.getPackageId())){
                                                dipSub.setStartTime(subService.getStartDate());
                                                dipSub.setEndTime(subService.getExpDate());
                                                dipSub.setEndTime(subService.getExpDate());
                                                dipSub.setChannel(subService.getChannel());
                                                dipSub.setUserId(subService.getUserId());
                                                if(subService.getDataAmt() == null){
                                                    dipSub.setInitialAmount(null);
                                                }
                                                else{
                                                    dipSub.setInitialAmount(subService.getDataAmt() / 1024);
                                                }
                                            }
                                        }
                                    }
                                }*/

                                mlistDipPackage.stream().filter(e -> e.getProvCode().equalsIgnoreCase(subService.getServiceName()))
                                        .findFirst().flatMap(dipPackage -> mListDipSubService.stream().filter(v -> v.getPackageId().equals(dipPackage.getPackageId()))
                                                .findFirst()).ifPresent(dipSubService -> {
                                            dipSubService.setStartTime(subService.getStartDate());
                                            dipSubService.setEndTime(subService.getExpDate());
                                            dipSubService.setChannel(subService.getChannel());
                                            dipSubService.setUserId(subService.getUserId());
                                            dipSubService.setInitialAmount(subService.getDataAmt() == null ? null : subService.getDataAmt() / 1024);
                                        });
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

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {
    }

    @Override
    public void handleCancel() throws Exception {
        super.handleCancel();
    }

    public String getDateStr(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (date == null) {
            return null;
        }
        return sd.format(date);
    }

    ///////////// getter and setter //////////
    public DipSubService getmDipSubService() {
        return mDipSubService;
    }

    public void setmDipSubService(DipSubService mDipSubService) {
        this.mDipSubService = mDipSubService;
    }

    public List<DipSubService> getmListDipSubService() {
        return mListDipSubService;
    }

    public void setmListDipSubService(List<DipSubService> mListDipSubService) {
        this.mListDipSubService = mListDipSubService;
    }

    public List<DipSubService> getmListDipSubServiceFiltered() {
        return mListDipSubServiceFiltered;
    }

    public void setmListDipSubServiceFiltered(List<DipSubService> mListDipSubServiceFiltered) {
        this.mListDipSubServiceFiltered = mListDipSubServiceFiltered;
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

    public DipSubServiceModel getMmodel() {
        return mmodel;
    }

    public void setMmodel(DipSubServiceModel mmodel) {
        this.mmodel = mmodel;
    }

    public List<DipPackage> getMlistDipPackage() {
        return mlistDipPackage;
    }

    public void setMlistDipPackage(List<DipPackage> mlistDipPackage) {
        this.mlistDipPackage = mlistDipPackage;
    }
}
