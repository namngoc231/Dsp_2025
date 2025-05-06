package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DipPackage;
import vn.com.telsoft.entity.DipService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DipPackageModel extends AMDataPreprocessor implements Serializable {

    public List<DipPackage> getlistDipPackagesById(long serviceId) throws Exception {
        List<DipPackage> listDipPackage = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.package_id, "
                    + "               a.service_id, "
                    + "               a.package_code, "
                    + "               a.status, "
                    + "               a.description, "
                    + "               a.prov_code, "
                    + "               a.initial_amount, "
                    + "               a.active_day, "
                    + "               a.amount_input "
                    + "      FROM   dip_package a"
                    + "      WHERE  a.service_id = ? "
                    + "      ORDER BY a.status desc, a.package_code ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, serviceId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DipPackage dipPackage = new DipPackage();
                dipPackage.setPackageId(mRs.getLong("package_id"));
                dipPackage.setServiceId(mRs.getLong("service_id"));
                dipPackage.setPackageCode(mRs.getString("package_code"));
                dipPackage.setStatus(mRs.getLong("status"));
                dipPackage.setDescription(mRs.getString("description"));
                dipPackage.setProvCode(mRs.getString("prov_code"));
                dipPackage.setInitialAmount(mRs.getDouble("initial_amount"));
                dipPackage.setActiveDay(mRs.getLong("active_day"));
                dipPackage.setAmountInput(mRs.getString("amount_input"));
                listDipPackage.add(dipPackage);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listDipPackage;
    }

    public void insert(DipPackage dipPackage) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "INSERT INTO dip_package(package_id, service_id, package_code, description, status, prov_code, initial_amount, active_day, amount_input)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            long id = SQLUtil.getSequenceValue(mConnection, "DIP_PACKAGE_SEQ");
            dipPackage.setPackageId(id);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, id);
            mStmt.setLong(2, dipPackage.getServiceId());
            mStmt.setString(3, dipPackage.getPackageCode());
            mStmt.setString(4, dipPackage.getDescription());
            mStmt.setLong(5, dipPackage.getStatus());
            mStmt.setString(6, dipPackage.getProvCode());
            mStmt.setDouble(7, dipPackage.getInitialAmount());
            mStmt.setLong(8, dipPackage.getActiveDay());
            mStmt.setString(9, dipPackage.getAmountInput());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.setAutoCommit(true);
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DipPackage dipPackage) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "UPDATE dip_package SET service_id = ?, package_code = ?, description = ?, status = ?, prov_code = ?, initial_amount = ?, active_day = ?, amount_input = ? "
                    + " WHERE package_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dipPackage.getServiceId());
            mStmt.setString(2, dipPackage.getPackageCode());
            mStmt.setString(3, dipPackage.getDescription());
            mStmt.setLong(4, dipPackage.getStatus());
            mStmt.setString(5, dipPackage.getProvCode());
            mStmt.setDouble(6, dipPackage.getInitialAmount());
            mStmt.setLong(7, dipPackage.getActiveDay());
            mStmt.setString(8, dipPackage.getAmountInput());
            mStmt.setLong(9, dipPackage.getPackageId());
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

    public void delete(DipPackage dipPackage) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "DELETE FROM dip_package WHERE package_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dipPackage.getPackageId());
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

    public boolean checkProvCode(DipPackage dipService) throws Exception {
        try {
            open();
            String strSQL = "SELECT 1 FROM dip_package a WHERE a.prov_code = ? ";
            if (dipService.getPackageId() != null) {
                strSQL += " and a.package_id != ? ";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, dipService.getProvCode());
            if (dipService.getPackageId() != null) {
                mStmt.setLong(2, dipService.getPackageId());
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

    public boolean checkPackageCode(DipPackage dipService) throws Exception {
        try {
            open();
            String strSQL = "SELECT 1 FROM dip_package a WHERE a.package_code = ? ";
            if (dipService.getPackageId() != null) {
                strSQL += " and a.package_id != ? ";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, dipService.getPackageCode());
            if (dipService.getPackageId() != null) {
                mStmt.setLong(2, dipService.getPackageId());
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

    public List<DipService> getlistDipServices() throws Exception {
        List<DipService> listDipService = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.service_id, "
                    + "               a.code, "
                    + "               a.status, "
                    + "               a.description "
                    + "      FROM   dip_service a"
                    + "      ORDER BY a.service_id desc ";
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
}
