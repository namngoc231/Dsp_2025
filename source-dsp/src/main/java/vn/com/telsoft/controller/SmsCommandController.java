/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import org.apache.commons.lang.SerializationUtils;
import org.primefaces.PrimeFaces;
import vn.com.telsoft.entity.SMSCommand;
import vn.com.telsoft.model.SMSCommandModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author HoangNH
 */
@Named
@ViewScoped
public class SmsCommandController extends TSFuncTemplate implements Serializable {
    private List<SMSCommand> mlistSMS;
    private SMSCommand mtmpSMS;
    private List<SMSCommand> mselectedSMS;
    private SMSCommandModel mmodel = new SMSCommandModel();

    public SmsCommandController() throws Exception {
        this.mlistSMS = this.mmodel.getAll();
    }

    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        this.mtmpSMS = new SMSCommand();
    }

    public void changeStateEdit(SMSCommand app) throws Exception {
        super.changeStateEdit();
        this.selectedIndex = this.mlistSMS.indexOf(app);
        this.mtmpSMS = (SMSCommand) SerializationUtils.clone(app);
    }

    public void changeStateCopy(SMSCommand app) throws Exception {
        super.changeStateCopy();
        this.mtmpSMS = app;
    }

    public void handleOK() throws Exception {
        if (!this.isADD && !this.isCOPY) {
            if (this.isEDIT) {
                if (!this.getPermission("U")) {
                    return;
                }

                this.mmodel.edit(this.mtmpSMS);
                this.mlistSMS.set(this.selectedIndex, this.mtmpSMS);
                ClientMessage.logUpdate();
            }
        } else {
            if (!this.getPermission("I")) {
                return;
            }

            this.mmodel.add(this.mtmpSMS);
            this.mlistSMS.add(0, this.mtmpSMS);
            this.mtmpSMS = new SMSCommand();
            ClientMessage.logAdd();
        }
        handleCancel();
    }

    public void handleDelete() throws Exception {
        this.handleDelete((SMSCommand) null);
    }

    public void handleDelete(SMSCommand ett) throws Exception {
        if (this.getPermission("D")) {
            if (ett == null) {
                this.mmodel.delete(this.mselectedSMS);
                for (SMSCommand s : mselectedSMS) {
                    this.mlistSMS.remove(s);
                }
            } else {
                this.mmodel.delete(Collections.singletonList(ett));
                this.mlistSMS.remove(ett);
            }
            this.mselectedSMS = null;
            ClientMessage.logDelete();
            PrimeFaces.current().executeScript("PF('table_sms').clearFilters();");
        }
    }

    public void handleCancel() throws Exception {
        super.handleCancel();
        PrimeFaces.current().executeScript("PF('table_sms').clearFilters();");
    }

    public boolean isIsSelectedSMS() {
        return this.mselectedSMS != null && !this.mselectedSMS.isEmpty();
    }

    public List<SMSCommand> getMlistSMS() {
        return mlistSMS;
    }

    public void setMlistSMS(List<SMSCommand> mlistSMS) {
        this.mlistSMS = mlistSMS;
    }

    public SMSCommand getMtmpSMS() {
        return mtmpSMS;
    }

    public void setMtmpSMS(SMSCommand mtmpSMS) {
        this.mtmpSMS = mtmpSMS;
    }

    public List<SMSCommand> getMselectedSMS() {
        return mselectedSMS;
    }

    public void setMselectedSMS(List<SMSCommand> mselectedSMS) {
        this.mselectedSMS = mselectedSMS;
    }
}
