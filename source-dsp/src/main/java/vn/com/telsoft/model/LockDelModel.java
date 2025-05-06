package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.LockDelEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LockDelModel extends AMDataPreprocessor implements Serializable {

    public List<LockDelEntity> getLsLock() throws Exception {
        List<LockDelEntity> returnVal = new ArrayList<>();
        try {
            open();
            String strSQL = "select a.count,a.issue_date,a.locked_object,a.type \n" +
                    "from dsp_lock a \n" +
                    "where a.issue_date >= TRUNC(SYSDATE) and a.issue_date < TRUNC(SYSDATE+1) AND a.count>=5";
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                LockDelEntity ett = new LockDelEntity();
                ett.setCount(mRs.getLong(1));
                ett.setIssueDate(mRs.getTimestamp(2));
                ett.setLockedObject(mRs.getString(3));
                ett.setType(mRs.getString(4));
                returnVal.add(ett);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return returnVal;
    }

    public void del(LockDelEntity ett) throws Exception {
        try {
            open();
            String strSQL = "DELETE FROM dsp_lock where locked_object = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, ett.getLockedObject());
            mStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
    }
}
