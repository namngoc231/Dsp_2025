package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import vn.com.telsoft.entity.LockDelEntity;
import vn.com.telsoft.model.LockDelModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class LockDelController extends TSFuncTemplate implements Serializable {

    private LockDelModel model;
    private LockDelEntity lockDelEntitySelected;
    private List<LockDelEntity> lsLockDel;
    private List<LockDelEntity> lsLockDelFiltered;


    public LockDelController() throws Exception {
        model = new LockDelModel();
        lockDelEntitySelected = new LockDelEntity();
        lsLockDel = new ArrayList<>();
        lsLockDelFiltered = new ArrayList<>();
        lsLockDel = model.getLsLock();
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {
        for (LockDelEntity ent : lsLockDel) {
            if (ent.getLockedObject().equals(lockDelEntitySelected.getLockedObject())) {
                lockDelEntitySelected.setCount(ent.getCount());
                break;
            }
        }
        if (lockDelEntitySelected.getCount() >= 5) {
            model.del(lockDelEntitySelected);
            ClientMessage.logDelete();
            lsLockDel = model.getLsLock();
        } else {
            ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR,
                    ResourceBundleUtil.getCTObjectAsString("PP_LOCKDEL", "object_not_block"));
            return;
        }
    }

    public String getType(String object) {
        ResourceBundleUtil resourceBundleUtil = new ResourceBundleUtil("PP_LOCKDEL");
        String strReturn = "";
        if ("0".equals(object)) {
            strReturn = "ISDN";
        } else {
            strReturn = "API_USER";
        }


        return strReturn;
    }

    public LockDelEntity getLockDelEntitySelected() {
        return lockDelEntitySelected;
    }

    public void setLockDelEntitySelected(LockDelEntity lockDelEntitySelected) {
        this.lockDelEntitySelected = lockDelEntitySelected;
    }

    public List<LockDelEntity> getLsLockDel() {
        return lsLockDel;
    }

    public void setLsLockDel(List<LockDelEntity> lsLockDel) {
        this.lsLockDel = lsLockDel;
    }

    public List<LockDelEntity> getLsLockDelFiltered() {
        return lsLockDelFiltered;
    }

    public void setLsLockDelFiltered(List<LockDelEntity> lsLockDelFiltered) {
        this.lsLockDelFiltered = lsLockDelFiltered;
    }
}
