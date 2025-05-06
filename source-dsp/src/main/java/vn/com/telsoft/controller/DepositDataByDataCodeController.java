/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.*;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.FileUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import vn.com.telsoft.entity.ApParam;
import vn.com.telsoft.model.ApParamModel;
import vn.com.telsoft.util.CsvReader;
import vn.com.telsoft.util.CsvWriter;
import vn.com.telsoft.util.Utils;
import vn.com.telsoft.ws.DspApiClient;
import vn.com.telsoft.ws.DspApiRequest;
import vn.com.telsoft.ws.DspApiResponse;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.*;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author TungLM, TELSOFT
 */
@Named
@ViewScoped
public class DepositDataByDataCodeController extends TSFuncTemplate implements Serializable {
    static final String MODULE_RESOURCE_BUNDLE = "PP_DEPOSITDATABYDATACODE";
    static final String API_DATACODE_RESOURCE_BUNDLE = "PP_API_DATACODE";
    private static final AtomicLong seq = new AtomicLong(System.currentTimeMillis());
    //
    private String type;
    private String isdn;
    private String serial;
    private String datacode;
    private String content;

    private long mlUserId;
    private CsvReader reader = null;
    List<String> listDspDepositData;
    String strFileName;
    private boolean checkSeriUsed;
    String webUserName;
    List<ApParam> listType;
    ApParamModel apParamModel = new ApParamModel();

    public DepositDataByDataCodeController() throws Exception {
        init();
        //Check user in MVAS
        mlUserId = AdminUser.getUserLogged().getUserId();
        webUserName = AdminUser.getUserLogged().getUserName();
        listType = apParamModel.getListApParamByType("USEDC_TYPE");
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {

    }
    //////////////////////////////////////////////////////////////////////////////////

    private void init() {
        this.isdn = "";
        this.serial = "";
        this.datacode = "";
        this.content = "";
        listDspDepositData = new ArrayList<>();
        strFileName = "";
        checkSeriUsed = false;
    }

    public void handleDeposit() throws Exception {
        checkSeriUsed = false;
        validInput();
        //call api
        DspApiRequest request = new DspApiRequest();
        request.setDatacode(this.datacode);
        request.setMsisdn(this.isdn);
        request.setSerial(this.serial);
        request.setComment(this.content);
        request.setWebUsername(webUserName);
        DspApiClient dspApiClient = new DspApiClient();
        DspApiResponse response;
        if ("DATACODE".equals(type)) {
            response = dspApiClient.usedc(request);
        } else if ("C90N".equals(type)){ // C90N
            response = dspApiClient.wusedc_khcn(request);
        } else { //ClipTV
            request.setTransactionId(webUserName + "_" + seq.getAndIncrement());
            response = dspApiClient.registerClipTV(request);
        }
        if (response != null) {
            //Message to client
            if ("0".equals(response.getResultCode())) {
                ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getResultCode()));
            } else {
                if ("5004".equals(response.getResultCode())) {
                    checkSeriUsed = true;
                }
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getResultCode()));
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    private String regex(String type) {
        for (ApParam object : listType) {
            if (type.equals(object.getParName())) {
                return object.getParValue();
            }
        }
        return "";
    }

    private void validInput() {
        if (!Utils.validateIsdn(this.isdn)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_ISDN"));
        }
        if (!Utils.validateSerialDataCode(this.serial)) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_SERIAL"));
        }
        if (Utils.validateDataCode(this.datacode, regex(this.type))) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_DATACODE"));
        }
    }

    private String validInputFileImport(String strIsdn, String strSerial, String strDataCode, String regex) {
        String error = "";
        if (!Utils.validateIsdn(strIsdn)) {
            error += ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_ISDN") + "|";
        }
        if (!Utils.validateSerialDataCode(strSerial)) {
            error += ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_SERIAL") + "|";
        }
        if ("".equals(regex)) {
            error += ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_TYPE") + "|";
        } else {
            if (Utils.validateDataCode(strDataCode, regex)) {
                error += ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "ERR_NOTVALID_DATACODE") + "|";
            }
        }
        if (!error.isEmpty()) {
            error = error.substring(0, error.length() - 1);
        }
        return error;
    }

    ///////////////////////////////////////////////////////

    public DefaultStreamedContent handleDownload() throws Exception {
        CsvWriter writer = null;
        try {
            String fileName = com.faplib.applet.util.StringUtil.format(new Date(), "yyyyMMddHHmmss") + "ket_qua_nap_the.csv";
            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String strReportAbsolutePath = context.getRealPath(SystemConfig.getConfig("FileUploadPath"));
            com.faplib.applet.util.FileUtil.forceFolderExist(strReportAbsolutePath);
            writer = new CsvWriter(strReportAbsolutePath + fileName, '\n', Charset.forName("UTF-8"));
            String[] arrayStr = new String[listDspDepositData.size()];
            for (int i = 0; i < listDspDepositData.size(); i++) {
                arrayStr[i] = listDspDepositData.get(i);
            }
            writer.writeRecord(arrayStr, true);
            return FileUtil.downloadFile(fileName, strReportAbsolutePath + fileName);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            throw ex;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        CsvReader reader = null;
        String strFilePath = "";
        listDspDepositData = new ArrayList<>();
        try {
            File file = new File(FileUtil.uploadTempFile(event.getFile()));
            strFilePath = file.getPath();
            strFileName = file.getName();
            reader = new CsvReader(file.getPath(), ',');
            int numberRow = 0;
            while (reader.readRecord()) {
                if (numberRow == 0) {
                    numberRow = 1;
                    continue;
                }
                String[] strRecords = reader.getValues();
                if (strRecords.length != 4) {
                    String strInput = "";
                    for (String strRecord : strRecords) {
                        strInput += strRecord + ", ";
                    }
                    listDspDepositData.add(strInput + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + ", " + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                } else {
                    if ("".equals(strRecords[0].trim()) || "".equals(strRecords[1].trim()) || "".equals(strRecords[2].trim()) || "".equals(strRecords[3].trim())) {
                        listDspDepositData.add(strRecords[0] + ", " + strRecords[1] + ", " + strRecords[2] + ", " + strRecords[3] + ", " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + ", " + ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "data_invalid"));
                    } else {
                        setIsdn(strRecords[0].trim());
                        setSerial(strRecords[1].trim());
                        setDatacode(strRecords[2].trim());
                        setType(strRecords[3].trim());
                        String strIsdn = getIsdn();
                        String strSerial = getSerial();
                        String strPin = getDatacode();
                        String strType = getType();
                        String regex = regex(strType);
                        String err = validInputFileImport(strIsdn, strSerial, strPin, regex);
                        String result = "";
                        if ("".equals(err)) {
                            DspApiRequest request = new DspApiRequest();
                            request.setDatacode(strPin);
                            request.setMsisdn(strIsdn);
                            request.setSerial(strSerial);
                            request.setWebUsername(webUserName);
                            DspApiClient dspApiClient = new DspApiClient();
                            DspApiResponse response;
                            if ("DATACODE".equals(strType)) {
                                response = dspApiClient.usedc(request);
                            } else { // C90N
                                response = dspApiClient.wusedc_khcn(request);
                            }
                            if (response != null) {
                                result = ResourceBundleUtil.getCTObjectAsString(API_DATACODE_RESOURCE_BUNDLE, response.getResultCode());
                            }
                        } else {
                            result = err;
                        }
                        listDspDepositData.add(strRecords[0].trim() + ", " + strRecords[1].trim() + ", " + strRecords[2].trim() + ", " + strRecords[3] + ", " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + ", " + result);
                    }
                }
            }
            if (listDspDepositData.size() == 0) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "FileEmpty"));
            }
        } catch (ConnectException e) {
            SystemLogger.getLogger().error(e);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "err_connect_process"));
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "err_no_specific"));
        } finally {
            if (reader != null) {
                reader.close();
            }
            FileUtil.deleteFile(strFilePath + strFileName);
        }
    }

    //////////////////////////////////////////////////////////////
    public void redirectPage() throws Exception {
        if (serial != null && !"".equals(serial.trim())) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("viewDc?serialUsed=" + serial);
        }
    }

    public DefaultStreamedContent downloadFileDataCode() {
        try {
            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String resFileDataPath = servletContext.getRealPath("/resources/template/file_nap_the_data_cho_thue_bao.csv");
            return FileUtil.downloadFile("file_nap_the_data_cho_thue_bao.csv", resFileDataPath);
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////

    //Getters, Setters
    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        if (isdn != null) {
            this.isdn = Utils.fixIsdnWithout0and84(isdn.replaceAll("[ ()]", ""));
        }
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        if (serial != null) {
            this.serial = serial.replaceAll("-", "");
        }
    }

    public String getDatacode() {
        return datacode;
    }

    public void setDatacode(String datacode) {
        if (datacode != null) {
            this.datacode = datacode.replaceAll("-", "");
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getListDspDepositData() {
        return listDspDepositData;
    }

    public void setListDspDepositData(List<String> listDspDepositData) {
        this.listDspDepositData = listDspDepositData;
    }

    public String getStrFileName() {
        return strFileName;
    }

    public void setStrFileName(String strFileName) {
        this.strFileName = strFileName;
    }

    public boolean isCheckSeriUsed() {
        return checkSeriUsed;
    }

    public void setCheckSeriUsed(boolean checkSeriUsed) {
        this.checkSeriUsed = checkSeriUsed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ApParam> getListType() {
        return listType;
    }

    public void setListType(List<ApParam> listType) {
        this.listType = listType;
    }
}
