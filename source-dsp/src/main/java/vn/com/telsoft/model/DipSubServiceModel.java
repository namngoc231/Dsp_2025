package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DipPackage;
import vn.com.telsoft.entity.DipSubService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DipSubServiceModel extends AMDataPreprocessor implements Serializable {

    public List<DipSubService> getSubService(String isdn) throws Exception {
        List<DipSubService> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT a.isdn, a.package_id, a.service_id, a.start_time, a.end_time, a.last_update_time,  a.initial_amount, a.active_day, b.channel, b.user_id, c.user_name, " +
                    " (select package_code from dip_package where package_id = a.package_id) as packageCode, (select code from dip_service where service_id = a.service_id) as serviceName " +
                    " FROM dip_sub_service a, dip_request_hist b, am_user c" +
                    " WHERE a.request_id = b.request_id and b.user_id = c.user_id and a.isdn = ? AND a.end_time > SYSDATE and b.status = 1 order by a.start_time desc";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, isdn.trim());
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DipSubService tmp = new DipSubService();
                tmp.setIsdn(mRs.getString(1));
                tmp.setPackageId(mRs.getLong(2));
                tmp.setServiceId(mRs.getLong(3));
                tmp.setStartTime(mRs.getTimestamp(4));
                tmp.setEndTime(mRs.getTimestamp(5));
                tmp.setLastUpdateTime(mRs.getTimestamp(6));
                tmp.setInitialAmount(mRs.getLong(7));
                tmp.setActiveDay(mRs.getLong(8));
                tmp.setChannel(mRs.getString(9));
                tmp.setUserId(mRs.getLong(10));
                tmp.setUserName(mRs.getString(11));
                tmp.setPackageName(mRs.getString(12));
                tmp.setServiceName(mRs.getString(13));
                listReturn.add(tmp);
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

    public List<DipPackage> getListPackageService() throws Exception {
        List<DipPackage> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT DISTINCT  a.package_id, a.package_code, a.prov_code" +
                    " FROM DIP_PACKAGE a INNER JOIN DIP_SUB_SERVICE b ON a.package_id = b.package_id";
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DipPackage tmp = new DipPackage();
                tmp.setPackageId(mRs.getLong(1));
                tmp.setPackageCode(mRs.getString(2));
                tmp.setProvCode(mRs.getString(3));
                listReturn.add(tmp);
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
}
