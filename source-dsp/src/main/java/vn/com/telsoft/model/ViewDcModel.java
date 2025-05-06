package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DSPTransaction;

import java.io.Serializable;
import java.sql.PreparedStatement;

public class ViewDcModel extends AMDataPreprocessor implements Serializable {

    public DSPCompany getCompany(String reseller) throws Exception {
        try {
            open();
            String strSQL = "SELECT a.com_id, a.com_name, a.type, a.tax_code, a.bus_code, a.status, a.vas_mobile" +
                    "   FROM dsp_company a" +
                    "   WHERE status = 1 AND a.bus_code = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, reseller);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPCompany tmpDSPCompany = new DSPCompany();
                tmpDSPCompany.setComId(mRs.getLong(1));
                tmpDSPCompany.setComName(mRs.getString(2));
                tmpDSPCompany.setType(mRs.getLong(3));
                tmpDSPCompany.setTaxCode(mRs.getString(4));
                tmpDSPCompany.setBusCode(mRs.getString(5));
                tmpDSPCompany.setStatus(mRs.getString(6));
                tmpDSPCompany.setVasMobile(mRs.getString(7));
                return tmpDSPCompany;
            }

        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return null;
    }

    public DSPCompany getParent(String reseller) throws Exception {
        try {
            open();
            String strSQL = "SELECT a.com_id, a.com_name, a.type, a.tax_code, a.bus_code, a.status, a.vas_mobile, a.parent_id" +
                    "   FROM dsp_company a" +
                    "   WHERE a.com_id in (SELECT parent_id FROM dsp_company WHERE bus_code = ?)" +
                    "   AND a.status = '1'";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, reseller);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPCompany tmpDSPCompany = new DSPCompany();
                tmpDSPCompany.setComId(mRs.getLong(1));
                tmpDSPCompany.setComName(mRs.getString(2));
                tmpDSPCompany.setType(mRs.getLong(3));
                tmpDSPCompany.setTaxCode(mRs.getString(4));
                tmpDSPCompany.setBusCode(mRs.getString(5));
                tmpDSPCompany.setStatus(mRs.getString(6));
                tmpDSPCompany.setVasMobile(mRs.getString(7));
                tmpDSPCompany.setParentId(mRs.getLong(8));

                if (tmpDSPCompany.getType() == 2) {
                    mStmt = mConnection.prepareStatement(strSQL);
                    mStmt.setString(1, tmpDSPCompany.getBusCode());
                    mRs = mStmt.executeQuery();
                    if (mRs.next()) {
                        tmpDSPCompany.setComId(mRs.getLong(1));
                        tmpDSPCompany.setComName(mRs.getString(2));
                        tmpDSPCompany.setType(mRs.getLong(3));
                        tmpDSPCompany.setTaxCode(mRs.getString(4));
                        tmpDSPCompany.setBusCode(mRs.getString(5));
                        tmpDSPCompany.setStatus(mRs.getString(6));
                        tmpDSPCompany.setVasMobile(mRs.getString(7));
                        tmpDSPCompany.setParentId(mRs.getLong(8));
                    }
                }
                return tmpDSPCompany;
            }
        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return null;
    }

    public DSPTransaction getTransaction(String orderCode) throws Exception {
        try {
            open();
            String strSQL = "Select a.description, a.delivery_time, a.transaction_id from dsp_transaction a where a.res_order_id = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, orderCode);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPTransaction dspTransaction = new DSPTransaction();
                dspTransaction.setDescription(mRs.getString(1));
                dspTransaction.setDeliveryTime(mRs.getTimestamp(2));
                dspTransaction.setTransactionId(mRs.getLong(3));
                return dspTransaction;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return null;
    }
}
