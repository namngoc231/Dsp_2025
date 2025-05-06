package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.ApParam;
import vn.com.telsoft.entity.DeclareApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApParamModel extends AMDataPreprocessor implements Serializable {

    public String getParValue(String parType, String parName) throws Exception {
        String result = null;
        try {
            open();
            String strSQL = "SELECT PAR_VALUE FROM AP_PARAM WHERE PAR_TYPE = ? AND PAR_NAME = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, parType);
            mStmt.setString(2, parName);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                result = mRs.getString("PAR_VALUE");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return result;
    }

    public List<ApParam> getListApParamByType(String parType) throws Exception {
        List<ApParam> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT PAR_NAME, PAR_TYPE, PAR_VALUE, DESCRIPTION FROM AP_PARAM WHERE PAR_TYPE = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, parType);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                ApParam apParam = new ApParam();
                apParam.setParName(mRs.getString(1));
                apParam.setParType(mRs.getString(2));
                apParam.setParValue(mRs.getString(3));
                apParam.setDescription(mRs.getString(4));
                listReturn.add(apParam);
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
