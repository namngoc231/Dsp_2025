/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.TelsoftException;
import org.apache.commons.lang3.SerializationUtils;
import vn.com.telsoft.entity.CBItemContent;
import vn.com.telsoft.entity.CBItemIsdn;
import vn.com.telsoft.entity.CBList;
import vn.com.telsoft.model.CBItemIsdnModel;
import vn.com.telsoft.model.CbItemContentModel;
import vn.com.telsoft.model.CbListModel;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HoangNH
 */
@Named
@ViewScoped
public class CbListController extends TSFuncTemplate implements Serializable {

    private List<CBList> mList;
    private CBList mtmpCbList;
    private CBList mselectedList;
    private List<CBList> mFilter;
    private CbListModel mmodel;

    //cbItemISDN
    private CBItemIsdnModel isdnModel;
    private List<CBItemIsdn> lstIsdn;
    private CBItemIsdn mIsdn;

    //cbContent
    private List<CBItemContent> lstContent;
    private CbItemContentModel contentModel;

    public CbListController() throws Exception {
        mtmpCbList = new CBList();
        mmodel = new CbListModel();
        mList = mmodel.getListAll();
        if (mList.size() > 0) {
            mselectedList = new CBList(mList.get(0));
        }

        mtmpCbList.setStatus(1);

    }
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void changeStateAdd() throws Exception {
        super.changeStateAdd();
        mtmpCbList = new CBList();

    }
    //////////////////////////////////////////////////////////////////////////////////

    public void changeStateEdit(CBList app) throws Exception {
        super.changeStateEdit();
        selectedIndex = mList.indexOf(app);
        mtmpCbList = (CBList) SerializationUtils.clone(app);
    }
    //////////////////////////////////////////////////////////////////////////////////

    public void changeStateCopy(CBList app) throws Exception {
        super.changeStateCopy();
        mtmpCbList = app;
    }
    //////////////////////////////////////////////////////////////////////////////////

    public void handleCancel() throws Exception {
        super.handleCancel();
        //Reset form
        mtmpCbList = new CBList();
    }
    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleOK() throws Exception {
        if (isADD || isCOPY) {
            //Check permission
            if (!getPermission("I")) {
                return;
            }
            if (checkDuplicate()) {
                mmodel.add(mtmpCbList);
                mList.add(0, mtmpCbList);
            }


            //Reset form
            mtmpCbList = new CBList();

            //Message to client
            ClientMessage.logAdd();

        } else if (isEDIT) {
            //Check permission
            if (!getPermission("U")) {
                return;
            }
            if (checkDuplicate()) {
                mtmpCbList.setCached(1);
                mmodel.edit(mtmpCbList);
                mList.set(selectedIndex, mtmpCbList);
            }
            mtmpCbList = new CBList();
            //Message to client
            ClientMessage.logUpdate();
        }
        handleCancel();

    }
    //////////////////////////////////////////////////////////////////////////////////

    public boolean checkDuplicate() throws Exception {
        if (mtmpCbList != null && !mtmpCbList.equals("")) {
            for (CBList l : mList) {
                if (isADD) {
                    if (l.getListName().toLowerCase().equals(mtmpCbList.getListName().toLowerCase())) {
                        throw new TelsoftException("ERR-0111");
                    }
                } else if (isEDIT) {
                    if (l.getListName().toLowerCase().equals(mtmpCbList.getListName().toLowerCase())) {
                        if (!l.getListId().equals(mtmpCbList.getListId())) {
                            throw new TelsoftException("ERR-0111");
                        }
                    }
                }
            }
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleDelete() throws Exception {
        handleDelete(null);
    }
    //////////////////////////////////////////////////////////////////////////////////

    public void handleDelete(CBList ett) throws Exception {
        //Check permission

        if (checkListIdKey(ett.getListId(), ett.getListType())) {
            if (!getPermission("D")) {
                return;
            }
            if (ett == null) {
                mmodel.delete(Collections.singletonList(mselectedList));

            } else {
                mmodel.delete(Collections.singletonList(ett));
            }

            mList = mmodel.getListAll();
            mselectedList = null;

            //Message to client
            ClientMessage.logDelete();
            handleCancel();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////

    public boolean getIsDisplayBtnConfirm() throws Exception {
        return this.isADD || this.isEDIT || this.isCOPY;
    }

    public void onRowSelect() throws Exception {
        if (mselectedList != null) {
            if (mselectedList == null) {
                mList = new ArrayList<>();
            } else {
                mtmpCbList = (CBList) SerializationUtils.clone(mselectedList);
            }

        } else {
            mList = new ArrayList<>();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public void getItem(CBList app) throws Exception {
        if (app != null && app.getListId() != null) {
            if (app.getListType().equals(0)) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("cbItemIsdn?cbListId=" + app.getListId());
            } else if (app.getListType().equals(1)) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("adjustcontent?cbListId=" + app.getListId());
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////

    public boolean checkListIdKey(Long id, Integer listType) throws Exception {
        isdnModel = new CBItemIsdnModel();
        mIsdn = new CBItemIsdn();

        contentModel = new CbItemContentModel();
        if (listType.equals(0)) {
            lstIsdn = isdnModel.getCbItemIsdn(mIsdn);

            if (lstIsdn != null && !lstIsdn.isEmpty()) {
                for (CBItemIsdn i : lstIsdn) {
                    if (i.getListId().equals(id)) {
                        isdnModel.delete(i);
                    }
                }
                return true;
            }
        } else if (listType.equals(1)) {
            lstContent = mmodel.getListItemContent();

            if (lstContent != null || !lstContent.isEmpty()) {
                for (CBItemContent i : lstContent) {
                    if (i.getListId().equals(id)) {
                        contentModel.deleteCBItemContent(i);
                    }
                }
                return true;
            }
        }
        return false;
    }


    //Getters
    public List<CBList> getmList() {
        return mList;
    }

    public void setmList(List<CBList> mList) {
        this.mList = mList;
    }

    public CBList getMtmpCbList() {
        return mtmpCbList;
    }

    public void setMtmpCbList(CBList mtmpCbList) {
        this.mtmpCbList = mtmpCbList;
    }

    public CBList getMselectedList() {
        return mselectedList;
    }

    public void setMselectedList(CBList mselectedList) {
        this.mselectedList = mselectedList;
    }

    public List<CBList> getmFilter() {
        return mFilter;
    }

    public void setmFilter(List<CBList> mFilter) {
        this.mFilter = mFilter;
    }

}
