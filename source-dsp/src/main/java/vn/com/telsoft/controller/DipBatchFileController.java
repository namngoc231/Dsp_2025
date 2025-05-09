package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.FileUtil;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.model.DSPOrderModel;
import vn.com.telsoft.model.DipBatchFileModel;
import vn.com.telsoft.model.DspServiceModel;
import vn.com.telsoft.util.ExcelCellEtt;
import vn.com.telsoft.util.ExcelUtil;
import vn.com.telsoft.util.Utils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Named
@ViewScoped
public class DipBatchFileController extends TSFuncTemplate implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_DIP_BATCH_FILE";
    static final String BUNDLE = "PP_DATACODE";

    File mfile;
    String fileName;
    private DipBatchFile mtmpDipBatchFile;
    private DipRequest mtmpDipRequest;
    private List<DipRequest> mlistSelectImport;
    private List<DipRequest> mlistErrImport;
    private List<DipRequest> mlistSuccesImport;
    private List<DipRequest> mlistDipRequest;
    private List<DipBatchFile> mlistDipBatchFile;
    private List<DipBatchFile> mlistDipBatchFileFilter;
    private List<DipPackage> mlistDipPackages;
    private List<DSPServicePrice> mlistDspServicePrice;
    private List<DSPService> mlistDspService;

    private List<DSPOrder> mlistDSPOrder;
    private DSPTransaction mtmpDSPTransaction;
    private List<DSPOrderTransaction> mlistDspOrderTransaction;
    private DSPOrderTransaction mtmpDspOrderTransaction;

    private DipBatchFileModel dipBatchFileModel;
    private DSPOrderModel dspOrderModel;
    private DspServiceModel dspServiceModel;

    private boolean checkIsdn;
    private boolean checkPackageCode;
    private boolean checkPackageCodeTemp;
    private long totalMoney;

    private boolean fileIndexErr;
    private Long userId;
    private Long comId;
    private Long tabId;
    private Long serviceId;
    private int render = 0;

    HashMap<String, DipPackage> hmDipPackage;
    HashMap<String, DSPServicePrice> hmDspservicePrice;
    String duplicateIndex = "";
    private DefaultStreamedContent fileDownloadTemplate;

    private List<DSPTransaction> mlistTransactions;
    Map<Long, List<DipRequest>> hmDipRequest;

    public DipBatchFileController() throws Exception {
        mtmpDipBatchFile = new DipBatchFile();
        mtmpDipRequest = new DipRequest();
        mlistSelectImport = new ArrayList<>();
        mlistErrImport = new ArrayList<>();
        mlistSuccesImport = new ArrayList<>();
        mlistDipRequest = new ArrayList<>();
        mlistDipPackages = new ArrayList<>();
        mlistDspServicePrice = new ArrayList<>();
        mlistDSPOrder = new ArrayList<>();
        mtmpDSPTransaction = new DSPTransaction();
        mlistDspOrderTransaction = new ArrayList<>();
        mtmpDspOrderTransaction = new DSPOrderTransaction();
        mlistTransactions = new ArrayList<>();

        dspOrderModel = new DSPOrderModel();
        dipBatchFileModel = new DipBatchFileModel();
        dspServiceModel = new DspServiceModel();

        hmDipPackage = new HashMap<>();
        hmDspservicePrice = new HashMap<>();
        hmDipRequest = new HashMap<>();

        userId = AdminUser.getUserLogged().getUserId();
        getLink();
        comId = dipBatchFileModel.getComId(userId);
        tabId = dipBatchFileModel.getTabId(comId, serviceId);
        mlistTransactions = dipBatchFileModel.getlistDataCode(serviceId, userId);
        mlistDipPackages = dipBatchFileModel.getlistDipPackages();
        mlistDspServicePrice = dipBatchFileModel.getlistDspServicePrice(tabId);
        mlistDipRequest = dipBatchFileModel.getlistAllDipRequest(userId);
        mlistDipPackages.forEach(value -> hmDipPackage.put(value.getPackageCode(), value));
        mlistDspServicePrice.forEach(value -> hmDspservicePrice.put(value.getName(), value));
        if (mlistDipRequest.size() > 0) {
            hmDipRequest = mlistDipRequest.stream().collect(groupingBy(DipRequest::getTransactionId, Collectors.toCollection(ArrayList::new)));
            mlistTransactions.forEach(value -> {
                try {
                    dipBatchFileModel.updateDipBatchFileStatus(hmDipRequest.get(value.getTransactionId()).get(0).getFileId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        mlistDipBatchFile = dipBatchFileModel.getlistDipBatchFile(userId);
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
            serviceId = mlistDspService.get(0).getServiceId();
        }
    }

    public void prepareUpload() {
        render = 1;
    }

    @Override
    public void handleCancel() throws Exception {
        super.handleCancel();
        PrimeFaces.current().executeScript("PF('dtPackageList').clearFilters();");
        render = 0;
    }

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        mfile = null;
        mfile = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
        if (mfile.getName().endsWith(".xlsx")) {
            fileName = mfile.getName().replace(".xlsx", "");
        }
        if (mfile.getName().endsWith(".xls")) {
            fileName = mfile.getName().replace(".xls", "");
        }
        fileName = fileName.split("\\_")[1];
        doImport();
        if (fileIndexErr) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "fileIndexErr"));
        } else {
            if (mlistErrImport.size() > 0) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "uploadErrMessage"));
            }
            if (mlistErrImport.size() == 0 && mlistSuccesImport.size() > 0) {
                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "uploadSuccessMessage"));
            }
        }
    }

    public void doImport() throws Exception {
        fileIndexErr = false;
        mlistErrImport = new ArrayList<>();
        mlistSuccesImport = new ArrayList<>();
        mlistSelectImport = ExcelUtil.read(mfile);
        totalMoney = 0;
        if (!mlistSelectImport.isEmpty()) {
            List<ExcelCellEtt> checkRow = (List<ExcelCellEtt>) mlistSelectImport.get(0);
            if (checkRow.size() < 3) {
                fileIndexErr = true;
            } else {
                mlistSelectImport.remove(0);
                if (mlistSelectImport.size() > 0) {
                    for (int i = 0; i < mlistSelectImport.size(); i++) {
                        List<ExcelCellEtt> row = (List<ExcelCellEtt>) mlistSelectImport.get(i);
                        mtmpDipRequest = new DipRequest();
                        checkPackageCodeTemp = false;
                        checkIsdn = false;
                        checkPackageCode = false;
                        if (row.get(0).getStringValue() != null) {
                            mtmpDipRequest.setIndex(row.get(0).getStringValue());
                        }
                        if (row.get(1).getStringValue() != null) {
                            if (isNumeric(Utils.fixIsdnWithout0and84(row.get(1).getStringValue().trim().replaceAll(" ", "")))
                                    && Utils.fixIsdnWithout0and84(row.get(1).getStringValue().trim().replaceAll(" ", "")).length() >= 9
                                    && Utils.fixIsdnWithout0and84(row.get(1).getStringValue().trim().replaceAll(" ", "")).length() <= 14) {
                                mtmpDipRequest.setIsdn(Utils.fixIsdnWithout0and84(row.get(1).getStringValue().trim().replaceAll(" ", "")));
                                mtmpDipRequest.setDisplayIsdn(Utils.fixIsdnWithout0and84(row.get(1).getStringValue().trim().replaceAll(" ", "")));
                                checkIsdn = true;
                            } else {
                                mtmpDipRequest.setIsdn(row.get(1).getStringValue());
                                mtmpDipRequest.setDisplayIsdn(row.get(1).getStringValue());
                            }
                        }
                        if (row.get(2).getStringValue() != null) {
                            mtmpDipRequest.setPackageCode(row.get(2).getStringValue().trim());
                            DipPackage dipPackage = null;
                            if (!hmDipPackage.isEmpty()) {
                                dipPackage = hmDipPackage.get(mtmpDipRequest.getPackageCode());
                            }
                            DSPServicePrice dspServicePrice = null;
                            if (!hmDspservicePrice.isEmpty()) {
                                dspServicePrice = hmDspservicePrice.get(mtmpDipRequest.getPackageCode());
                            }
                            if (dipPackage != null) {
                                mtmpDipRequest.setServiceId(dipPackage.getServiceId());
                                checkPackageCode = true;
                            }
                            if (dspServicePrice != null) {
                                mtmpDipRequest.setPriceId(dspServicePrice.getPriceId());
                                mtmpDipRequest.setMoneyAmount(dspServicePrice.getPrice());
                                mtmpDipRequest.setInitialAmount((double) dspServicePrice.getCapMin());
                                mtmpDipRequest.setActiveDay(dspServicePrice.getActiveDay());
                                totalMoney += mtmpDipRequest.getMoneyAmount();
                                checkPackageCodeTemp = true;
                            }
                        }
                        if (mtmpDipRequest.getIndex() == null || mtmpDipRequest.getIsdn() == null || mtmpDipRequest.getPackageCode() == null) {
                            mtmpDipRequest.setLogErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "notEnoughInfo"));
                            mlistErrImport.add(mtmpDipRequest);
                            continue;
                        }
                        if (!checkIsdn) {
                            if (mtmpDipRequest.getLogErr() == null || mtmpDipRequest.getLogErr().isEmpty()) {
                                mtmpDipRequest.setLogErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errIsdn"));
                            } else {
                                mtmpDipRequest.setLogErr(mtmpDipRequest.getLogErr() + ", " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errIsdn"));
                            }
                        }
                        if (!checkPackageCodeTemp) {
                            if (mtmpDipRequest.getLogErr() == null || mtmpDipRequest.getLogErr().isEmpty()) {
                                mtmpDipRequest.setLogErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errPackageCode1"));
                            } else {
                                mtmpDipRequest.setLogErr(mtmpDipRequest.getLogErr() + ", " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errPackageCode1"));
                            }
                        }
                        if (!checkPackageCode) {
                            if (mtmpDipRequest.getLogErr() == null || mtmpDipRequest.getLogErr().isEmpty()) {
                                mtmpDipRequest.setLogErr(ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errPackageCode2"));
                            } else {
                                mtmpDipRequest.setLogErr(mtmpDipRequest.getLogErr() + ", " + ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "errPackageCode2"));
                            }
                        }
                        if (checkIsdn && checkPackageCode && checkPackageCodeTemp) {
                            mlistSuccesImport.add(mtmpDipRequest);
                        } else {
                            mlistErrImport.add(mtmpDipRequest);
                        }
                    }
                    if (mlistErrImport.isEmpty()) {
                        Map<String, List<DipRequest>> lstDuplicates = mlistSuccesImport.stream().collect(groupingBy(DipRequest::uniqueAttributes, Collectors.toCollection(ArrayList::new)));
                        for (Map.Entry<String, List<DipRequest>> map : lstDuplicates.entrySet()) {
                            if (map.getValue().size() > 1) {
                                duplicateIndex = ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "duplicateIsdn");
                                for (int i = 0; i < map.getValue().size(); i++) {
                                    duplicateIndex += map.getValue().get(i).getIndex() + ", ";
                                }
                                map.getValue().forEach(value -> {
                                    value.setLogErr(duplicateIndex);
                                    if (value.getLogErr().contains(String.valueOf(value.getIndex()))) {
                                        value.setLogErr(value.getLogErr().replace(value.getIndex() + ", ", ""));
                                        value.setLogErr(value.getLogErr().substring(0, value.getLogErr().length() - 2));
                                    }
                                });
                                mlistSuccesImport.removeAll(map.getValue());
                                mlistErrImport.addAll(map.getValue());
                            }
                        }
                    }
                    mtmpDipBatchFile.setFileName(fileName);
                    mtmpDipBatchFile.setRealPath(SystemConfig.getConfig("FileUploadPath")); //TODO
                    mtmpDipBatchFile.setUserId(userId);
                    mtmpDipBatchFile.setComId(comId);
                    mtmpDipBatchFile.setRecordCount((long) mlistSuccesImport.size());
                    mtmpDipBatchFile.setTotalMoney(totalMoney);
                } else {
                    ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "fileHasNoData"));
                }
            }
        } else {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "fileHasNoData"));
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void handleOK() throws Exception {
        dipBatchFileModel.insert(mtmpDipBatchFile, mlistSuccesImport);
        mfile = null;
        mlistSuccesImport = new ArrayList<>();
        mlistErrImport = new ArrayList<>();
        render = 0;
        mlistDipBatchFile = dipBatchFileModel.getlistDipBatchFile(userId);
        PrimeFaces.current().executeScript("PF('dtPackageList').clearFilters();");
        ClientMessage.logAdd();
    }

    public void onConfirm(DipBatchFile obj) throws Exception {
        mtmpDipBatchFile = new DipBatchFile();
        mtmpDSPTransaction = new DSPTransaction();
        mlistDSPOrder = new ArrayList<>();
        mlistDspOrderTransaction = new ArrayList<>();

        mtmpDipBatchFile = obj;
        mtmpDSPTransaction.setRequestTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mtmpDSPTransaction.getRequestTime());
        calendar.add(Calendar.HOUR, 3);
        mtmpDSPTransaction.setPlanTime(calendar.getTime());
        mtmpDSPTransaction.setAmount(mtmpDipBatchFile.getTotalMoney());
        mtmpDSPTransaction.setUserId(userId);
        mtmpDSPTransaction.setComId(comId);
        mtmpDSPTransaction.setTabId(tabId);
        mtmpDSPTransaction.setServiceId(serviceId);

        mlistDSPOrder = dspOrderModel.getListOrderForTrans(comId);
        long totalRemain = dspOrderModel.getTotalRemainOrder(comId);
//        if (checkPublicKey()) {
        if (mtmpDipBatchFile.getTotalMoney() > totalRemain) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(BUNDLE, "Total_money_is_greater_than_the_contract_value") + customFormat(totalRemain) + " " + ResourceBundleUtil.getCTObjectAsString(BUNDLE, "VND"));
        } else {
            long amount = mtmpDipBatchFile.getTotalMoney();
            for (DSPOrder dspOrder : mlistDSPOrder) {
                if (dspOrder.getRemainQuotaValue() < amount && dspOrder.getRemainQuotaValue() != 0) {
                    mtmpDspOrderTransaction = new DSPOrderTransaction();
                    amount = amount - dspOrder.getRemainQuotaValue();
                    dspOrder.setReservedValue(dspOrder.getRemainQuotaValue() + dspOrder.getReservedValue());
                    mtmpDspOrderTransaction.setOrderId(dspOrder.getOrderId());
                    mtmpDspOrderTransaction.setIssueTime(mtmpDSPTransaction.getRequestTime());
                    mtmpDspOrderTransaction.setUserId(userId);
                    mtmpDspOrderTransaction.setAmount(dspOrder.getRemainQuotaValue());
                    mlistDspOrderTransaction.add(mtmpDspOrderTransaction);
                } else if (dspOrder.getRemainQuotaValue() >= amount) {
                    mtmpDspOrderTransaction = new DSPOrderTransaction();
                    dspOrder.setReservedValue(amount + dspOrder.getReservedValue());
                    dspOrder.setRemainQuotaValue(dspOrder.getRemainQuotaValue() - amount);
                    mtmpDspOrderTransaction.setOrderId(dspOrder.getOrderId());
                    mtmpDspOrderTransaction.setIssueTime(mtmpDSPTransaction.getRequestTime());
                    mtmpDspOrderTransaction.setUserId(userId);
                    mtmpDspOrderTransaction.setAmount(amount);
                    mlistDspOrderTransaction.add(mtmpDspOrderTransaction);
                    amount = 0;
                    break;
                }
            }

            dipBatchFileModel.comfirm(mtmpDSPTransaction, mlistDspOrderTransaction, mlistDSPOrder, mtmpDipBatchFile);
            mlistDipBatchFile = dipBatchFileModel.getlistDipBatchFile(userId);
            ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "confirm_success"));
        }
//        }
    }

    public String dateToString(Date input) {
        if (input == null) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return df.format(input);
    }

    public String customFormat(long value) {
        String pattern = "###,###.###";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    public void changeStateView(DipBatchFile obj) throws Exception {
        render = 3;
        mlistDipRequest = dipBatchFileModel.getlistDipRequest(obj.getFileId());
    }

    public void handleCancelDetail() {
        PrimeFaces.current().executeScript("PF('dtPackageList').clearFilters();");
        PrimeFaces.current().executeScript("PF('dtDipRequest').clearFilters();");
        render = 0;
    }

    public void handleDelete(DipBatchFile obj) throws Exception {
        dipBatchFileModel.delete(obj);
        mlistDipBatchFile = dipBatchFileModel.getlistDipBatchFile(userId);
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.DELETE);
    }

    public DefaultStreamedContent downloadFile(DipBatchFile obj, long status) {
        try {
            String strFileName = dipBatchFileModel.getlistDipRequestHistory(obj.getFileId(), status);
            return FileUtil.downloadFile(new File(strFileName));
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    public void downloadTemplate(ActionEvent evt) throws Exception {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            File file = new File(context.getRealPath("/resources/template/FileMauDangKyDichVuDataIpTheoLo.xlsx"));
            InputStream input = new FileInputStream(file);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            setFileDownloadTemplate(new DefaultStreamedContent(input, externalContext.getMimeType(file.getName()), file.getName()));
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
    }

//    private boolean checkPublicKey() throws Exception{
//        Date sysDate = new Date();
//        Calendar updateDate = Calendar.getInstance();
////        updateDate.setTime(userCompany.getUpdatedKey());
//        updateDate.add(Calendar.MONTH, 3);
//        if (updateDate.getTime().before(sysDate)) {
//            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(BUNDLE, "public_key_expired"));
//            return false;
//        }
//        return true;
//    }

    @Override
    public void handleDelete() throws Exception {
    }

    public File getMfile() {
        return mfile;
    }

    public void setMfile(File mfile) {
        this.mfile = mfile;
    }

    public DipRequest getMtmpDipRequest() {
        return mtmpDipRequest;
    }

    public void setMtmpDipRequest(DipRequest mtmpDipRequest) {
        this.mtmpDipRequest = mtmpDipRequest;
    }

    public List<DipRequest> getMlistSelectImport() {
        return mlistSelectImport;
    }

    public void setMlistSelectImport(List<DipRequest> mlistSelectImport) {
        this.mlistSelectImport = mlistSelectImport;
    }

    public List<DipRequest> getMlistErrImport() {
        return mlistErrImport;
    }

    public void setMlistErrImport(List<DipRequest> mlistErrImport) {
        this.mlistErrImport = mlistErrImport;
    }

    public List<DipRequest> getMlistSuccesImport() {
        return mlistSuccesImport;
    }

    public void setMlistSuccesImport(List<DipRequest> mlistSuccesImport) {
        this.mlistSuccesImport = mlistSuccesImport;
    }

    public boolean isFileIndexErr() {
        return fileIndexErr;
    }

    public void setFileIndexErr(boolean fileIndexErr) {
        this.fileIndexErr = fileIndexErr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DipBatchFile getMtmpDipBatchFile() {
        return mtmpDipBatchFile;
    }

    public void setMtmpDipBatchFile(DipBatchFile mtmpDipBatchFile) {
        this.mtmpDipBatchFile = mtmpDipBatchFile;
    }

    public List<DipPackage> getMlistDipPackages() {
        return mlistDipPackages;
    }

    public void setMlistDipPackages(List<DipPackage> mlistDipPackages) {
        this.mlistDipPackages = mlistDipPackages;
    }

    public int getRender() {
        return render;
    }

    public void setRender(int render) {
        this.render = render;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<DipBatchFile> getMlistDipBatchFile() {
        return mlistDipBatchFile;
    }

    public void setMlistDipBatchFile(List<DipBatchFile> mlistDipBatchFile) {
        this.mlistDipBatchFile = mlistDipBatchFile;
    }

    public List<DipRequest> getMlistDipRequest() {
        return mlistDipRequest;
    }

    public void setMlistDipRequest(List<DipRequest> mlistDipRequest) {
        this.mlistDipRequest = mlistDipRequest;
    }

    public List<DipBatchFile> getMlistDipBatchFileFilter() {
        return mlistDipBatchFileFilter;
    }

    public void setMlistDipBatchFileFilter(List<DipBatchFile> mlistDipBatchFileFilter) {
        this.mlistDipBatchFileFilter = mlistDipBatchFileFilter;
    }

    public DefaultStreamedContent getFileDownloadTemplate() {
        return fileDownloadTemplate;
    }

    public void setFileDownloadTemplate(DefaultStreamedContent fileDownloadTemplate) {
        this.fileDownloadTemplate = fileDownloadTemplate;
    }
}
