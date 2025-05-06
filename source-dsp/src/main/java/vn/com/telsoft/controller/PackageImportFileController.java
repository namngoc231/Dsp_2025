/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.admin.security.AdminUser;
import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.util.FileUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DSPServicePrice;
import vn.com.telsoft.model.DataCodeModel;
import vn.com.telsoft.model.PackageImportFileModel;
import vn.com.telsoft.util.CsvWriter;
import vn.com.telsoft.util.ExcelUtil;
import vn.com.telsoft.util.Utils;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class PackageImportFileController extends TSFuncTemplate implements Serializable {
    static final String MODULE_RESOURCE_BUNDLE = "PP_PACKAGE_IMPORT";

    private File mfile; //file chọn
    private String fileName;  //ten file đc chọn
    private List<String> mlistSelectImport; //list file đc chọn
    private List<String> mlistResultImport; //list file đc chọn
    private List<DSPCompany> mlistCompany; //list company
    private DSPCompany selectedCompany; //list company
    private PackageImportFileModel model; //list company
    private boolean isImportFileError;
    private boolean fileUploaded;
    private DataCodeModel dataCodeModel;

    public PackageImportFileController() throws Exception {
        init();
    }

    private void init() throws Exception {
        Long userId = AdminUser.getUserLogged().getUserId();
        model = new PackageImportFileModel();
        dataCodeModel = new DataCodeModel();
        mlistCompany = model.getListCompany(userId);
        mlistSelectImport = new ArrayList<>();
        mlistResultImport = new ArrayList<>();
        isImportFileError = false;
        selectedCompany = new DSPCompany();
        fileUploaded = false;
    }

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        changeStateImport();
        List<String> mlistWhiteList;

        mfile = new File(FileUtil.uploadFile(event.getFile(), SystemConfig.getConfig("FileUploadPath")));
        fileName = mfile.getName().split("_")[1];

        mlistWhiteList = ExcelUtil.readTxt(mfile);
        mlistSelectImport = validInputFileImport(mlistWhiteList);
        mlistResultImport = new ArrayList<>(mlistSelectImport);
        if (isImportFileError) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "err_file_has_error_record"));
            return;
        }
        ClientMessage.log(ClientMessage.MESSAGE_TYPE.INF, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "uploadSuccessMessage"));
    }

    public void changeStateImport() {
        mfile = null;
        mlistSelectImport = new ArrayList<>();
        isImportFileError = false;
        fileUploaded = true;
        mlistResultImport = new ArrayList<>();
    }

    private List<String> validInputFileImport(List<String> mlistWhiteList) throws Exception {
        List<DSPServicePrice> lstProfile = dataCodeModel.getlistCardType(77L, selectedCompany.getComId());

        String[] params;
        List<String> listReturn = new ArrayList<>();

        for (String str : mlistWhiteList) {
            // check loi
            params = str.split(",");
            if (params.length != 2 || params[0].isEmpty() || params[1].isEmpty()) {
                str += " // Dữ liệu không đúng định dạng";
                listReturn.add(str);
                isImportFileError = true;
                continue;
            }
            if (!Utils.validateIsdnSub(Utils.formatISDN(params[0]))) {
                str += " // Số điện thoại không hợp lệ";
                listReturn.add(str);
                isImportFileError = true;
                continue;
            }
            String[] finalParams = params;
            if (lstProfile.stream().noneMatch(value -> finalParams[1].equals(value.getName()))) {
                str += " // Mã gói không hợp lệ";
                listReturn.add(str);
                isImportFileError = true;
                continue;
            }
            listReturn.add(str);
        }
        return listReturn;
    }

    public DefaultStreamedContent handleDownload() throws Exception {
        try {
            return downloadFile();
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            throw ex;
        }
    }

    public DefaultStreamedContent downloadFile() throws Exception {
        CsvWriter writer = null;
        try {
            String fileName = com.faplib.applet.util.StringUtil.format(new Date(), "yyyyMMddHHmmss") + "ket_qua.txt";
            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String strReportAbsolutePath = context.getRealPath(SystemConfig.getConfig("FileUploadPath"));
            com.faplib.applet.util.FileUtil.forceFolderExist(strReportAbsolutePath);
            writer = new CsvWriter(strReportAbsolutePath + fileName, '\n', Charset.forName("UTF-8"));
            String[] arrayStr = new String[mlistResultImport.size()];
            for (int i = 0; i < mlistResultImport.size(); i++) {
                arrayStr[i] = mlistResultImport.get(i);
            }
            writer.writeRecord(arrayStr, true);
            return FileUtil.downloadFile(fileName, strReportAbsolutePath + fileName);
        } catch (Exception e) {
            SystemLogger.getLogger().error(e);
            throw e;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void insertWhiteList() throws Exception {
        if (selectedCompany.getComId() == null) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "err_com_requried"));
            return;
        }
        if (!fileUploaded) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "err_file_required"));
            return;
        }
        if (isImportFileError) {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString(MODULE_RESOURCE_BUNDLE, "err_file_has_error_record"));
            return;
        }
        mlistResultImport = model.insertWhiteList(selectedCompany.getComId(), mlistSelectImport);
        ClientMessage.logAdd();
    }

    public List<DSPCompany> queryCompany(String query) {
        String queryLowerCase = query.toLowerCase();
        return mlistCompany.stream().filter(t -> t.getComName().toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
    }

    public void search(SelectEvent event) throws Exception {
        DSPCompany newCompany = (DSPCompany) event.getObject();
    }

    public void resetSelectedCompany(SelectEvent event) {
        selectedCompany = (DSPCompany) event.getObject();
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {

    }

    public File getMfile() {
        return mfile;
    }

    public void setMfile(File mfile) {
        this.mfile = mfile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getMlistSelectImport() {
        return mlistSelectImport;
    }

    public void setMlistSelectImport(List<String> mlistSelectImport) {
        this.mlistSelectImport = mlistSelectImport;
    }

    public DSPCompany getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(DSPCompany selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<String> getMlistResultImport() {
        return mlistResultImport;
    }

    public void setMlistResultImport(List<String> mlistResultImport) {
        this.mlistResultImport = mlistResultImport;
    }
}
