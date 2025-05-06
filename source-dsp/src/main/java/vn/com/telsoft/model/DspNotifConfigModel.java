package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DspNotifConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DspNotifConfigModel extends AMDataPreprocessor implements Serializable {

    public List<DspNotifConfig> getListAll() throws Exception {
        List<DspNotifConfig> listReturn = new ArrayList<>();
        try {
            open();
            //language=Oracle
            String sql = " select a.id, a.srv_name, a.package_code, a.qtastatus, a.qtavalue, a.dest_api, a.proxy_api, a.status " +
                    " from dsp_notif_config a order by a.status desc, a.id";
            mStmt = mConnection.prepareStatement(sql);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DspNotifConfig object = new DspNotifConfig();
                object.setId(mRs.getLong("id"));
                object.setSrvName(mRs.getString("srv_name"));
                object.setPackageCode(mRs.getString("package_code"));
                object.setQtaStatus(mRs.getString("qtastatus"));
                object.setQtaValue(mRs.getLong("qtavalue"));
                object.setDestApi(mRs.getString("dest_api"));
                object.setProxyApi(mRs.getString("proxy_api"));
                object.setStatus(mRs.getString("status"));
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

    public void insert(DspNotifConfig dspNotifConfig) throws Exception {
        try {
            open();
            //language=Oracle
            String sql = "insert into dsp_notif_config(id, srv_name, package_code, qtastatus, qtavalue, dest_api, status) " +
                    " values(?, ?, ?, ?, ?, ?, ?)";
            long id = SQLUtil.getSequenceValue(mConnection, "DSP_NOTIF_CONFIG_SEQ");
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setLong(1, id);
            mStmt.setString(2, dspNotifConfig.getSrvName());
            mStmt.setString(3, dspNotifConfig.getPackageCode());
            mStmt.setString(4, dspNotifConfig.getQtaStatus());
            mStmt.setLong(5, dspNotifConfig.getQtaValue());
            mStmt.setString(6, dspNotifConfig.getDestApi());
            mStmt.setString(7, dspNotifConfig.getStatus());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DspNotifConfig dspNotifConfig) throws Exception {
        try {
            open();
            //language=Oracle
            String sql = "update dsp_notif_config set srv_name = ?, package_code = ?, qtastatus = ?, qtavalue = ?, dest_api = ?, status = ? " +
                    " where id = ?";
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setString(1, dspNotifConfig.getSrvName());
            mStmt.setString(2, dspNotifConfig.getPackageCode());
            mStmt.setString(3, dspNotifConfig.getQtaStatus());
            mStmt.setLong(4, dspNotifConfig.getQtaValue());
            mStmt.setString(5, dspNotifConfig.getDestApi());
            mStmt.setString(6, dspNotifConfig.getStatus());
            mStmt.setLong(7, dspNotifConfig.getId());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(DspNotifConfig dspNotifConfig) throws Exception {
        try {
            open();
            //language=Oracle
            String sql = "delete from dsp_notif_config where id = ?";
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
