/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.TelsoftException;
import com.faplib.lib.util.ResourceBundleUtil;
import org.apache.commons.lang3.SerializationUtils;
import vn.com.telsoft.entity.CBItemContent;
import vn.com.telsoft.entity.CBItemIsdn;
import vn.com.telsoft.entity.CBList;
import vn.com.telsoft.entity.DSPService;
import vn.com.telsoft.model.CBItemIsdnModel;
import vn.com.telsoft.model.CbItemContentModel;
import vn.com.telsoft.model.CbListModel;
import vn.com.telsoft.model.DspServiceModel;

import javax.annotation.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Named
@ViewScoped
public class CreateRequestServiceController extends TSFuncTemplate implements Serializable {

    private List<DSPService> listService;
    private DspServiceModel dspServiceModel;
    private String path;

    public CreateRequestServiceController() throws Exception {
        dspServiceModel = new DspServiceModel();
        listService = dspServiceModel.getDSPServicePath();
    }

    public void redirectPage() throws Exception{
        if(path != null && !"".equals(path.trim())) {
             FacesContext.getCurrentInstance().getExternalContext().redirect(path);
        } else{
            ClientMessage.logErr(ResourceBundleUtil.getCTObjectAsString("PP_SERVICEDECLARE", "pathNotEmpty"));
        }
    }


    @Override
    public void changeStateAdd() throws Exception {

    }
    //////////////////////////////////////////////////////////////////////////////////


    public void handleCancel() throws Exception {

    }
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleOK() throws Exception {

    }
    //////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleDelete() throws Exception {

    }


    public List<DSPService> getListService() {
        return listService;
    }

    public void setListService(List<DSPService> listService) {
        this.listService = listService;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
