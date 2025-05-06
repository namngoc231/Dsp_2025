package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DipService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DipServiceModel extends AMDataPreprocessor implements Serializable {

    public List<DipService> getlistDipServices() throws Exception {
        List<DipService> listDipService = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.service_id, "
                    + "               a.code, "
                    + "               a.status, "
                    + "               a.description "
                    + "      FROM   dip_service a"
                    + "      ORDER BY a.status desc, a.code ";

            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DipService dipService = new DipService();
                dipService.setServiceId(mRs.getLong("service_id"));
                dipService.setCode(mRs.getString("code"));
                dipService.setStatus(mRs.getLong("status"));
                dipService.setDescription(mRs.getString("description"));
                listDipService.add(dipService);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listDipService;
    }

    public void insert(DipService dipService) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "INSERT INTO dip_service(service_id, code, description, status)"
                    + "VALUES(?, ?, ?, ?)";
            long id = SQLUtil.getSequenceValue(mConnection, "DIP_SERVICE_SEQ");
            dipService.setServiceId(id);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, id);
            mStmt.setString(2, dipService.getCode());
            mStmt.setString(3, dipService.getDescription());
            mStmt.setLong(4, dipService.getStatus());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close();
        }
    }

    public void update(DipService dipService) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "UPDATE dip_service SET code = ?, description = ?, status = ? WHERE service_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, dipService.getCode());
            mStmt.setString(2, dipService.getDescription());
            mStmt.setLong(3, dipService.getStatus());
            mStmt.setLong(4, dipService.getServiceId());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close();
        }
    }

    public void delete(DipService dipService) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "DELETE FROM dip_service WHERE service_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dipService.getServiceId());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close();
        }
    }

    public boolean checkCode(DipService dipService) throws Exception {
        try {
            open();
            String strSQL = "SELECT 1 FROM dip_service a WHERE a.code = ? ";
            if (dipService.getServiceId() != null) {
                strSQL += " and a.service_id != ? ";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, dipService.getCode());
            if (dipService.getServiceId() != null) {
                mStmt.setLong(2, dipService.getServiceId());
            }
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                return true;
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return false;
    }
}
