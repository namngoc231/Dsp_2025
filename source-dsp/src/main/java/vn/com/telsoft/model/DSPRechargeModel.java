package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.util.DateUtil;
import vn.com.telsoft.entity.DSPRecharge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DSPRechargeModel extends AMDataPreprocessor implements Serializable {

    public List<DSPRecharge> getlist(String isdn, String fromDate, String toDate, String status) throws Exception {
        List<DSPRecharge> listRecharge = new ArrayList<DSPRecharge>();
        try {
            open();
            String strSQL = "SELECT   a.isdn, "
                    + "               a.issue_date, "
                    + "               a.ref, "
                    + "               a.profile_code, "
                    + "               a.amount, "
                    + "               a.days, "
                    + "               a.serial, "
                    + "               a.status, "
                    + "               a.description, "
                    + "               a.retry_date, "
                    + "               a.addon "
                    + "      FROM   dsp_recharge a "
                    + "      where a.amount is not null ";

            if (!"".equals(isdn)) {
                strSQL += " and a.isdn=" + isdn.replace("(0)","").replaceAll(" ", "").trim() + " ";
            }

            if (!"".equals(status)) {
                strSQL += " and a.status=" + status + " ";
            }

            if (!"".equals(fromDate)) {
                strSQL += " and a.issue_date >=to_date('" + fromDate + "','dd/MM/yyyy') ";
            }

            if (!"".equals(toDate)) {
                strSQL += " and a.issue_date < to_date('" + toDate + "','dd/MM/yyyy')+1";
            }

            strSQL += "order by a.status asc, a.issue_date asc";

            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPRecharge dspRecharge = new DSPRecharge();
                dspRecharge.setIsdn(mRs.getString("isdn"));
                dspRecharge.setIssueDate(mRs.getTimestamp("issue_date"));
                dspRecharge.setRef(mRs.getString("ref"));
                dspRecharge.setProfileCode(mRs.getString("profile_code"));
                dspRecharge.setAmount(mRs.getLong("amount"));
                dspRecharge.setDays(mRs.getInt("days"));
                dspRecharge.setSerial(mRs.getString("serial"));
                dspRecharge.setStatus(mRs.getString("status"));
                dspRecharge.setDescription(mRs.getString("description"));
                dspRecharge.setRetryDate(mRs.getTimestamp("retry_date"));
                dspRecharge.setAddon(mRs.getString("addon"));
                listRecharge.add(dspRecharge);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listRecharge;
    }

    public void updateStatus(DSPRecharge dspRecharge) throws Exception {
        String strSQL = "UPDATE dsp_recharge SET status = 1, retry_date = ? WHERE isdn = ? and serial = ? and status = 2";
        try {
            open();
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setTimestamp(1, DateUtil.getSqlTimestamp(new Date()));
            mStmt.setString(2, dspRecharge.getIsdn());
            mStmt.setString(3, dspRecharge.getSerial());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public int updateStatusTemp(String isdn, String serial) throws Exception {
        String strSQL = "UPDATE dsp_recharge SET status = 2 WHERE isdn = ? and serial = ? and status = 0";
        try {
            open();
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, isdn);
            mStmt.setString(2, serial);
            int i = mStmt.executeUpdate();
            if (i > 0){
                mConnection.commit();
            }
            return i;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close();
        }
    }

    public void updateStatusBack(String isdn, String serial) throws Exception {
        String strSQL = "UPDATE dsp_recharge SET status = 0 WHERE isdn = ? and serial = ? and status = 2";
        try {
            open();
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, isdn);
            mStmt.setString(2, serial);
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }
}
