package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DspPcrfSrvConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DspPcrfSrvConfigModel extends AMDataPreprocessor implements Serializable {

    public List<DspPcrfSrvConfig> getListAll() throws Exception {
        List<DspPcrfSrvConfig> listReturn = new ArrayList<>();
        try {
            open();
            //language=Oracle
            String sql = "select ID, SYS_TYPE,SERVICE_TYPE, SERVICE_NAME,SERVICE_NAME_UNIQUE, PIN_PREFIX, ADD_ON,DESCRIPTION, STATUS from DSP_PCRF_SRV_CONFIG order by STATUS desc, ID";
            mStmt = mConnection.prepareStatement(sql);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DspPcrfSrvConfig object = new DspPcrfSrvConfig();
                object.setId(mRs.getLong("ID"));
                object.setSysType(mRs.getString("SYS_TYPE"));
                object.setServiceType(mRs.getString("SERVICE_TYPE"));
                object.setServiceName(mRs.getString("SERVICE_NAME"));
                object.setServiceNameUnique(mRs.getString("SERVICE_NAME_UNIQUE"));
                object.setPinPrefix(mRs.getString("PIN_PREFIX"));
                object.setAddOn(mRs.getString("ADD_ON"));
                object.setDescription(mRs.getString("DESCRIPTION"));
                object.setStatus(mRs.getString("STATUS"));
                listReturn.add(object);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listReturn;
    }

    public void insert(DspPcrfSrvConfig dspNotifConfig) throws Exception {
        try {
            open();
            //language=Oracle
            String sql = "insert into DSP_PCRF_SRV_CONFIG (ID, SYS_TYPE,SERVICE_TYPE, SERVICE_NAME, SERVICE_NAME_UNIQUE, PIN_PREFIX, ADD_ON,DESCRIPTION, STATUS)\n" + "values (?,?,?,?,?,?,?,?,?)";
            long id = SQLUtil.getSequenceValue(mConnection, "DSP_PCRF_SRV_CONFIG_SEQ");
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setLong(1, id);
            mStmt.setString(2, dspNotifConfig.getSysType());
            mStmt.setString(3, dspNotifConfig.getServiceType());
            mStmt.setString(4, dspNotifConfig.getServiceName());
            mStmt.setString(5, dspNotifConfig.getServiceNameUnique());
            mStmt.setString(6, dspNotifConfig.getPinPrefix());
            mStmt.setString(7, dspNotifConfig.getAddOn());
            mStmt.setString(8, dspNotifConfig.getDescription());
            mStmt.setString(9, dspNotifConfig.getStatus());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DspPcrfSrvConfig dspNotifConfig) throws Exception {
        try {
            open();
            //language=Oracle
            String sql = "update DSP_PCRF_SRV_CONFIG set SYS_TYPE = ?,SERVICE_TYPE = ?, SERVICE_NAME = ?,SERVICE_NAME_UNIQUE = ?, PIN_PREFIX = ?, ADD_ON = ?,DESCRIPTION = ?, STATUS = ?" + " where ID = ?";
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setString(1, dspNotifConfig.getSysType());
            mStmt.setString(2, dspNotifConfig.getServiceType());
            mStmt.setString(3, dspNotifConfig.getServiceName());
            mStmt.setString(4, dspNotifConfig.getServiceNameUnique());
            mStmt.setString(5, dspNotifConfig.getPinPrefix());
            mStmt.setString(6, dspNotifConfig.getAddOn());
            mStmt.setString(7, dspNotifConfig.getDescription());
            mStmt.setString(8, dspNotifConfig.getStatus());
            mStmt.setLong(9, dspNotifConfig.getId());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(DspPcrfSrvConfig dspNotifConfig) throws Exception {
        try {
            open();
            //language=Oracle
            String sql = "delete from DSP_PCRF_SRV_CONFIG where ID = ?";
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setLong(1, dspNotifConfig.getId());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }
}
