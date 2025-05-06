/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.controller;

import vn.com.telsoft.entity.CBSubscriber;
import vn.com.telsoft.model.SearchISDNModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author TUNGLM, TELSOFT
 */
@Named
@ViewScoped
public class SearchISDNController implements Serializable {

    private SearchISDNModel mmodel;
    private String isdn;
    private CBSubscriber mtmpSubscriber;

    public SearchISDNController() throws Exception {
        mmodel = new SearchISDNModel();
    }
    //////////////////////////////////////////////////////////////////////////////////


    public void handleSearch() throws Exception {
        mtmpSubscriber =  mmodel.getSubscriberInfo(isdn);
    }
    //////////////////////////////////////////////////////////////////////////////////

    //Getters, Setters
    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public CBSubscriber getMtmpSubscriber() {
        return mtmpSubscriber;
    }

    public void setMtmpSubscriber(CBSubscriber mtmpSubscriber) {
        this.mtmpSubscriber = mtmpSubscriber;
    }
}
