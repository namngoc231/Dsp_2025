/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.ApParam;
import vn.com.telsoft.entity.DSPSubService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DSPSubServiceModel extends AMDataPreprocessor implements Serializable {

    public List<DSPSubService> getSubService(String isdn) throws Exception {
        List<DSPSubService> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT a.isdn, a.service, a.start_time, a.end_time, a.initial_amount, a.last_update," +
                    " a.group_name, a.alert_end_time, (select par_value from ap_param where par_type in ('SRV_NAME', 'DC_VINFAST') and par_name = a.service ) serviceName, " +
                    " a.total_cycle, a.curr_cycle " +
                    " FROM DSP_SUB_SERVICE a WHERE a.end_time> sysdate AND a.isdn = ? ORDER BY a.isdn ASC";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, isdn.trim());
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPSubService tmp = new DSPSubService();
                tmp.setIsdn(mRs.getString(1));
                tmp.setService(mRs.getString(2));
                tmp.setStartTime(mRs.getDate(3));
                tmp.setEndTime(mRs.getDate(4));
                tmp.setInitialAmount(mRs.getLong(5));
                tmp.setLastUpdate(mRs.getDate(6));
                tmp.setGroupName(mRs.getString(7));
                tmp.setAlertEndTime(mRs.getString(8));
                tmp.setServiceName(mRs.getString(9));
                tmp.setTotalCycle(mRs.getLong(10));
                tmp.setCurrCycle(mRs.getLong(11));
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

    //ap_pram service_type
    public List<ApParam> getServiceType() throws Exception {
        List<ApParam> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select par_name, par_value, description from ap_param where par_type in ('SRV_TYPE', 'DC_VINFAST')";
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                ApParam tmp = new ApParam();
                tmp.setParName(mRs.getString(1));
                tmp.setParValue(mRs.getString(2));
                tmp.setDescription(mRs.getString(3));
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
