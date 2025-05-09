package vn.com.telsoft.controller;

import com.faplib.lib.ClientMessage;
import com.faplib.lib.TSFuncTemplate;
import com.faplib.lib.util.ResourceBundleUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import vn.com.telsoft.entity.AppStoreData;
import vn.com.telsoft.entity.CBStoreAttr;
import vn.com.telsoft.model.AppStoreDataModel;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AppStoreDataController extends TSFuncTemplate implements Serializable {
    private AppStoreDataModel model;
    private AppStoreData appStoreDataEntity;

    private AppStoreData storeEntity;
    private List<AppStoreData> lsStore;
    private List<AppStoreData> lsStoreSelected;
    private List<AppStoreData> lsStoreFiltered;

    private List<AppStoreData> lsAttr;
    private List<AppStoreData> lsAttrSelected;
    private List<AppStoreData> lsAttrFiltered;
    private AppStoreData AttrEntity;

    private int renderAttr = 0;
    private int renderButton = 0;
    private int disableInput = 0;
    private int disableTable = 0;

    private String mflag;
    private String mflagAttr;

    private int index;
    private int indexAttr;
    private int indexAttrFilter;
    private String checkIndex = "";

    private List<AppStoreData> lsInsert;
    private List<AppStoreData> lsUpdate;
    private List<AppStoreData> lsDelete;
    private AppStoreData appStoreDataTemp;
    private AppStoreData appStoreDataInput;


    public AppStoreDataController() throws Exception {
        model = new AppStoreDataModel();
        storeEntity = new AppStoreData();
        AttrEntity = new AppStoreData();
        appStoreDataEntity = new AppStoreData();
        lsStore = new ArrayList<>();
        lsAttr = new ArrayList<>();
        lsStoreSelected = new ArrayList<>();
        lsAttrSelected = new ArrayList<>();
        lsAttrFiltered = new ArrayList<>();
        lsInsert = new ArrayList<>();
        lsUpdate = new ArrayList<>();
        lsDelete = new ArrayList<>();

        lsStore = model.getAllStore();

        storeEntity = lsStore.get(0);
        appStoreDataEntity = lsStore.get(0);
        lsAttr = model.getAllStoreAttrById(appStoreDataEntity.getCbStore().getId());
    }

    @Override
    public void handleOK() throws Exception {

    }

    @Override
    public void handleDelete() throws Exception {

    }

    public void onRowselect(SelectEvent event) throws Exception {
        appStoreDataEntity = (AppStoreData) event.getObject();
        lsAttr = model.getAllStoreAttrById(appStoreDataEntity.getCbStore().getId());
    }

    public void onAdd() throws Exception {
        mflag = "add";
        disableInput = 1;
        renderButton = 1;
        disableTable = 1;
        appStoreDataEntity = new AppStoreData();
        lsAttr = new ArrayList<>();
        lsAttrSelected = new ArrayList<>();

    }

    public void onCopy(AppStoreData ent) throws Exception {
        mflag = "add";
        disableInput = 1;
        renderButton = 1;
        disableTable = 1;
        appStoreDataEntity = (AppStoreData) SerializationUtils.clone(ent);
        lsAttr = model.getAllStoreAttrById(appStoreDataEntity.getCbStore().getId());
        lsAttrSelected = new ArrayList<>();
    }

    public void onEdit(AppStoreData ent) throws Exception {
        mflag = "edit";
        disableInput = 1;
        renderButton = 1;
        disableTable = 1;
        appStoreDataEntity = (AppStoreData) SerializationUtils.clone(ent);
        lsAttr = model.getAllStoreAttrById(appStoreDataEntity.getCbStore().getId());
        index = lsStore.indexOf(ent);
        lsAttrSelected = new ArrayList<>();
    }

    public void onAccept() throws Exception {
        if (mflag.equals("add")) {
            appStoreDataEntity = model.doInsertCopy(appStoreDataEntity, lsAttr);
            lsStore.add(0, appStoreDataEntity);
            appStoreDataEntity = new AppStoreData();
            lsAttr = new ArrayList<>();
            PrimeFaces.current().executeScript("PF('tableStore').clearFilters();");
            PrimeFaces.current().executeScript("PF('tableAttr').clearFilters();");
            //Message to client
            ClientMessage.logAdd();
        } else if (mflag.equals("edit")) {
            appStoreDataEntity = model.doUpdate(appStoreDataEntity, lsInsert, lsUpdate, lsDelete);
            lsStore.set(index, appStoreDataEntity);
            lsInsert = new ArrayList<>();
            lsUpdate = new ArrayList<>();
            lsDelete = new ArrayList<>();
            //Message to client
            ClientMessage.logUpdate();
        }
        disableInput = 0;
        renderButton = 0;
        disableTable = 0;
        lsStoreSelected = new ArrayList<>();
    }

    public void onCancel() {
        appStoreDataEntity = new AppStoreData();
        disableInput = 0;
        renderButton = 0;
        disableTable = 0;
        renderAttr = 0;
        lsAttr = new ArrayList<>();
        lsAttrFiltered = new ArrayList<>();
        lsInsert = new ArrayList<>();
        lsUpdate = new ArrayList<>();
        lsDelete = new ArrayList<>();
        lsStoreSelected = new ArrayList<>();
        PrimeFaces.current().executeScript("PF('tableAttr').clearFilters();");
    }

    public void onAddAttr() {
        renderAttr = 1;
        if (mflag.equals("add")) {
            mflagAttr = "add";
        }
        if (mflag.equals("edit")) {
            mflagAttr = "addAttr";
        }
        appStoreDataEntity.setCbStoreAttr(new CBStoreAttr());
    }

    public void onCopyAttr(CBStoreAttr ent) {
        renderAttr = 1;
        if (mflag.equals("add")) {
            mflagAttr = "add";
        }
        if (mflag.equals("edit")) {
            mflagAttr = "addAttr";
        }
        appStoreDataEntity.setCbStoreAttr(SerializationUtils.clone((CBStoreAttr) ent));

    }

    public void onEditAttr(CBStoreAttr ent) {
        renderAttr = 1;
        if (mflag.equals("edit")) {
            mflagAttr = "editAttr";
        }
        if (mflag.equals("add")) {
            mflagAttr = "edit";
        }
        appStoreDataEntity.setCbStoreAttr(SerializationUtils.clone((CBStoreAttr) ent));
        if (!lsAttr.isEmpty()) {
            for (int i = 0; i < lsAttr.size(); i++) {
                if (lsAttr.get(i).getCbStoreAttr().getName().equals(ent.getName())) {
                    indexAttr = i;
                }
            }
            if (lsAttr.get(indexAttr).getCbStoreAttr().getName().equals(ent.getName())) {
                checkIndex = "samePosition";
            } else {
                checkIndex = "otherPosition";
            }
        }
        if (!lsAttrFiltered.isEmpty()) {
            for (int i = 0; i < lsAttrFiltered.size(); i++) {
                if (lsAttrFiltered.get(i).getCbStoreAttr().getName().equals(ent.getName())) {
                    indexAttrFilter = i;
                }
            }
        }

        for (AppStoreData value : lsInsert) {
            if (value.getCbStoreAttr().getName().equals(ent.getName())) {
                appStoreDataTemp = new AppStoreData(value);
            }
        }

    }


    public void onDeleteAttr(AppStoreData ent) throws Exception {
        if (mflag.equals("edit")) {
            if (!model.checkData(ent.getCbStoreAttr().getName(), ent.getCbStoreAttr().getStoreID())) {
                lsDelete.add(ent);
                lsAttr.remove(ent);
            } else {
                lsAttr.remove(ent);
            }
            for (AppStoreData value : lsInsert) {
                if (value.getCbStoreAttr().getName().equals(ent.getCbStoreAttr().getName())) {
                    lsInsert.remove(ent);
                    break;
                }
            }
            for (AppStoreData value : lsUpdate) {
                if (value.getCbStoreAttr().getName().equals(ent.getCbStoreAttr().getName())) {
                    lsUpdate.remove(ent);
                    break;
                }
            }
        } else {
            lsAttr.remove(ent);
        }
        lsAttrSelected = new ArrayList<>();
        //Message to client
        ClientMessage.logDelete();
    }

    public void onMulDeleteAttr() throws Exception {
        if (mflag.equals("edit")) {
            for (AppStoreData ent : lsAttrSelected) {
                if (!model.checkData(ent.getCbStoreAttr().getName(), ent.getCbStoreAttr().getStoreID())) {
                    lsDelete.add(ent);
                    lsAttr.remove(ent);
                } else {
                    lsAttr.remove(ent);
                }
                for (AppStoreData value : lsInsert) {
                    if (value.getCbStoreAttr().getName().equals(ent.getCbStoreAttr().getName())) {
                        lsInsert.remove(ent);
                        break;
                    }
                }
                for (AppStoreData value : lsUpdate) {
                    if (value.getCbStoreAttr().getName().equals(ent.getCbStoreAttr().getName())) {
                        lsUpdate.remove(ent);
                        break;
                    }
                }
            }
        } else {
            for (AppStoreData ent : lsAttrSelected) {
                lsAttr.remove(ent);
            }
        }
        lsAttrSelected = new ArrayList<>();
        //Message to client
        ClientMessage.logDelete();
    }

    public void onAcceptAttr() throws Exception {
        if (mflagAttr.equals("add")) {
            appStoreDataInput = new AppStoreData(appStoreDataEntity);
            if (!vlaData(appStoreDataInput)) {
                return;
            }
            lsAttr.add(0, appStoreDataInput);
            PrimeFaces.current().executeScript("PF('tableAttr').clearFilters();");

            //Message to client
            ClientMessage.logAdd();
        } else if (mflagAttr.equals("addAttr")) {
            appStoreDataInput = new AppStoreData(appStoreDataEntity);
            if (!vlaData(appStoreDataInput)) {
                return;
            }
            lsInsert.add(appStoreDataInput);
            lsAttr.add(0, appStoreDataInput);
            PrimeFaces.current().executeScript("PF('tableAttr').clearFilters();");
            //Message to client
            ClientMessage.logAdd();
        } else if (mflagAttr.equals("edit")) {
            appStoreDataInput = new AppStoreData(appStoreDataEntity);
            if (checkIndex.equals("otherPosition")) {
                if (!vlaData(appStoreDataInput)) {
                    return;
                }
            }
            lsAttr.set(indexAttr, appStoreDataInput);
            lsAttrFiltered.set(indexAttrFilter, appStoreDataInput);
            //Message to client
            ClientMessage.logUpdate();
        } else if (mflagAttr.equals("editAttr")) {
            appStoreDataInput = new AppStoreData(appStoreDataEntity);
            if (checkIndex.equals("otherPosition")) {
                if (!vlaData(appStoreDataInput)) {
                    return;
                }
            }
            if (!model.checkData(appStoreDataInput.getCbStoreAttr().getName(), appStoreDataInput.getCbStoreAttr().getStoreID())) {
                lsUpdate.add(appStoreDataInput);
            } else {
                lsInsert.add(appStoreDataInput);
            }
            lsAttr.set(indexAttr, appStoreDataInput);
            lsAttrFiltered.set(indexAttrFilter, appStoreDataInput);
            if (appStoreDataTemp != null) {
                for (AppStoreData value : lsInsert) {
                    if (value.getCbStoreAttr().getName().equals(appStoreDataTemp.getCbStoreAttr().getName())) {
                        lsInsert.remove(value);
                        break;
                    }
                }
            }
            //Message to client
            ClientMessage.logUpdate();
        }
        renderAttr = 0;
    }

    public void onCancelAttr() {
        renderAttr = 0;
    }

    public boolean vlaData(AppStoreData ent) {
        for (AppStoreData value : lsAttr) {
            if (value.getCbStoreAttr().getName().equalsIgnoreCase(ent.getCbStoreAttr().getName())) {
                ClientMessage.logErr(ClientMessage.MESSAGE_TYPE.ERR, ResourceBundleUtil.getCTObjectAsString("PP_APPSTOREDATA", "duplicate"));
                return false;
            }
        }
        return true;
    }

    public AppStoreData getAppStoreDataEntity() {
        return appStoreDataEntity;
    }

    public void setAppStoreDataEntity(AppStoreData appStoreDataEntity) {
        this.appStoreDataEntity = appStoreDataEntity;
    }

    public AppStoreData getStoreEntity() {
        return storeEntity;
    }

    public void setStoreEntity(AppStoreData storeEntity) {
        this.storeEntity = storeEntity;
    }

    public List<AppStoreData> getLsStore() {
        return lsStore;
    }

    public void setLsStore(List<AppStoreData> lsStore) {
        this.lsStore = lsStore;
    }

    public List<AppStoreData> getLsStoreSelected() {
        return lsStoreSelected;
    }

    public void setLsStoreSelected(List<AppStoreData> lsStoreSelected) {
        this.lsStoreSelected = lsStoreSelected;
    }

    public List<AppStoreData> getLsStoreFiltered() {
        return lsStoreFiltered;
    }

    public void setLsStoreFiltered(List<AppStoreData> lsStoreFiltered) {
        this.lsStoreFiltered = lsStoreFiltered;
    }

    public List<AppStoreData> getLsAttr() {
        return lsAttr;
    }

    public void setLsAttr(List<AppStoreData> lsAttr) {
        this.lsAttr = lsAttr;
    }

    public List<AppStoreData> getLsAttrSelected() {
        return lsAttrSelected;
    }

    public void setLsAttrSelected(List<AppStoreData> lsAttrSelected) {
        this.lsAttrSelected = lsAttrSelected;
    }

    public List<AppStoreData> getLsAttrFiltered() {
        return lsAttrFiltered;
    }

    public void setLsAttrFiltered(List<AppStoreData> lsAttrFiltered) {
        this.lsAttrFiltered = lsAttrFiltered;
    }

    public AppStoreData getAttrEntity() {
        return AttrEntity;
    }

    public void setAttrEntity(AppStoreData attrEntity) {
        AttrEntity = attrEntity;
    }

    public int getRenderAttr() {
        return renderAttr;
    }

    public void setRenderAttr(int renderAttr) {
        this.renderAttr = renderAttr;
    }

    public int getRenderButton() {
        return renderButton;
    }

    public void setRenderButton(int renderButton) {
        this.renderButton = renderButton;
    }

    public int getDisableInput() {
        return disableInput;
    }

    public void setDisableInput(int disableInput) {
        this.disableInput = disableInput;
    }


    public int getDisableTable() {
        return disableTable;
    }

    public void setDisableTable(int disableTable) {
        this.disableTable = disableTable;
    }
}
