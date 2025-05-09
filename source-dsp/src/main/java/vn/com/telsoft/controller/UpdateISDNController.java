/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import vn.com.telsoft.entity.CBStore;
import vn.com.telsoft.entity.CBSubStore;
import vn.com.telsoft.entity.CBSubscriber;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * @author TUNGLM, TELSOFT
 */
@Named
@ViewScoped
public class UpdateISDNController implements Serializable {

    @Inject
    private SearchISDNController searchISDNController;
    @Inject
    private InfoISDNController infoISDNController;
    @Inject
    private InfoISDNTransController infoISDNTransController;

    public UpdateISDNController() throws Exception {
    }

    //////////////////////////////////////////////////////////////////////////////////
    public void handleSearchISDN() throws Exception {
        this.searchISDNController.handleSearch();
        setMtmpSubscriber(getMtmpSubscriberSearch());
        if (getMtmpSubscriber() != null && getMtmpSubscriber().getId() != null) {
            setbDISPLAYInfoISDN(true);
            setbDISPLAYInfoISDNTrans(true);
        } else {
            setbDISPLAYInfoISDN(false);
            setbDISPLAYInfoISDNTrans(false);
            setlStoreId(null);
            setMtmpSubStore(null);
//            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_SEARCHISDN", "subNotFound"));
            ClientMessage.logPErr("subNotFound");
            return;
        }
        setMlistStore(getMtmpSubscriberSearch().getLstStore());
        List<CBStore> lstCBStore = getMlistStore();
        if (lstCBStore != null && lstCBStore.size() > 0) {
            setlStoreId(lstCBStore.get(0).getId());
            setMtmpSubStore(getMtmpSubscriber().getMapSubStore().get(getlStoreId()));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    public void handleSaveInfoISDN() throws Exception {
        this.infoISDNController.handleSave();
        ClientMessage.logUpdate();
    }

    //////////////////////////////////////////////////////////////////////////////////
    public void handleSaveInfoISDNTrans() throws Exception {
        this.infoISDNTransController.handleSave();
        ClientMessage.logUpdate();
    }

    //////////////////////////////////////////////////////////////////////////////////
    public void onStoreChange() throws Exception {
        setMtmpSubStore(getMtmpSubscriber().getMapSubStore().get(getlStoreId()));
    }

    //////////////////////////////////////////////////////////////////////////////////
    //Getters, Setters
    public SearchISDNController getSearchISDNController() {
        return searchISDNController;
    }

    public void setSearchISDNController(SearchISDNController searchISDNController) {
        this.searchISDNController = searchISDNController;
    }

    public InfoISDNController getInfoISDNController() {
        return infoISDNController;
    }

    public void setInfoISDNController(InfoISDNController infoISDNController) {
        this.infoISDNController = infoISDNController;
    }

    public InfoISDNTransController getInfoISDNTransController() {
        return infoISDNTransController;
    }

    public void setInfoISDNTransController(InfoISDNTransController infoISDNTransController) {
        this.infoISDNTransController = infoISDNTransController;
    }

    public String getIsdn() {
        return this.searchISDNController.getIsdn();
    }

    public void setIsdn(String isdn) {
        this.searchISDNController.setIsdn(isdn);
    }

    public CBSubscriber getMtmpSubscriberSearch() {
        return this.searchISDNController.getMtmpSubscriber();
    }

    public void setMtmpSubscriberSearch(CBSubscriber mtmpSubscriber) {
        this.searchISDNController.setMtmpSubscriber(mtmpSubscriber);
    }

    public CBSubscriber getMtmpSubscriber() {
        return this.infoISDNController.getMtmpSubscriber();
    }

    public void setMtmpSubscriber(CBSubscriber mtmpSubscriber) {
        this.infoISDNController.setMtmpSubscriber(mtmpSubscriber);
    }

    public boolean isbDISPLAYInfoISDN() {
        return this.infoISDNController.isbDISPLAY();
    }

    public void setbDISPLAYInfoISDN(boolean bDISPLAY) {
        this.infoISDNController.setbDISPLAY(bDISPLAY);
    }

    public CBSubStore getMtmpSubStore() {
        return this.infoISDNTransController.getMtmpSubStore();
    }

    public void setMtmpSubStore(CBSubStore mtmpSubStore) {
        this.infoISDNTransController.setMtmpSubStore(mtmpSubStore);
    }

    public List<CBStore> getMlistStore() {
        return this.infoISDNTransController.getMlistStore();
    }

    public void setMlistStore(List<CBStore> mlistStore) {
        this.infoISDNTransController.setMlistStore(mlistStore);
    }

    public Long getlStoreId() {
        return this.infoISDNTransController.getlStoreId();
    }

    public void setlStoreId(Long lStoreId) {
        this.infoISDNTransController.setlStoreId(lStoreId);
    }

    public boolean isbDISPLAYInfoISDNTrans() {
        return this.infoISDNTransController.isbDISPLAY();
    }

    public void setbDISPLAYInfoISDNTrans(boolean bDISPLAY) {
        this.infoISDNTransController.setbDISPLAY(bDISPLAY);
    }
}
