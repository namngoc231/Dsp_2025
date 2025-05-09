package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.SystemConfig;
import com.faplib.lib.SystemLogger;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.util.FileUtil;
import org.primefaces.model.DefaultStreamedContent;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.File;
import java.io.Serializable;

@Named
@ViewScoped
public class DownloadPuclicKeyController extends TSFuncTemplate implements Serializable {

    public DefaultStreamedContent downloadFilePublickey() throws Exception {
        try {
            String filePath = SystemConfig.getConfig("FilePublicKeyPath");
            return FileUtil.downloadFile(new File(filePath));
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLogger.getLogger().error(ex);
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ex.toString());
        }
        return null;
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {

    }
}
