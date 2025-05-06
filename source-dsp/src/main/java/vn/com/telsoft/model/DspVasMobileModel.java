package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DspComVasMobile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DspVasMobileModel extends AMDataPreprocessor implements Serializable {

    public long getComIdLogin(Long userId) throws Exception {
        long result = 0;
        String strSql = "SELECT com_id FROM dsp_company WHERE user_id = ?";
        try {
            open();
            mStmt = mConnection.prepareStatement(strSql);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                result = mRs.getLong(1);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return result;
    }

    public DSPCompany getInfoCompany(Long comId) throws Exception {
        DSPCompany result = new DSPCompany();
        String strSql = "SELECT parent_id, cust_type FROM dsp_company WHERE com_id = ?";
        try {
            open();
            mStmt = mConnection.prepareStatement(strSql);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                result.setParentId(mRs.getLong(1));
                result.setCustType(mRs.getString(2));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return result;
    }

    public List<DspComVasMobile> getListById(Long comId, Long comIdLogin) throws Exception {
        List<DspComVasMobile> list = new ArrayList<>();
        String strSql = "SELECT a.com_id, a.vas_mobile " +
                " FROM dsp_comp_vas_mobile a, dsp_company b" +
                " WHERE a.com_id = ? AND a.com_id = b.com_id AND b.parent_id = ? AND b.cust_type = '1' ";

        try {
            open();
            mStmt = mConnection.prepareStatement(strSql);
            mStmt.setLong(1, comId);
            mStmt.setLong(2, comIdLogin);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DspComVasMobile obj = new DspComVasMobile();
                obj.setComId(mRs.getLong(1));
                obj.setVasMobile(mRs.getString(2));
                list.add(obj);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return list;
    }

    public void insert(DspComVasMobile obj) throws Exception {
        try {
            open();
            String strSQL = "INSERT INTO dsp_comp_vas_mobile(com_id, vas_mobile)"
                    + "VALUES(?, ?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, obj.getComId());
            mStmt.setString(2, obj.getVasMobile());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(DspComVasMobile obj) throws Exception {
        try {
            open();
            String strSQL = "DELETE FROM dsp_comp_vas_mobile WHERE vas_mobile = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, obj.getVasMobile());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public boolean checkExistVasMobile(DspComVasMobile obj) throws Exception {
        try {
            open();
            String strSQL = "SELECT 1 FROM dsp_comp_vas_mobile WHERE vas_mobile = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, obj.getVasMobile());
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return false;
    }
}
