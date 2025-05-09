/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DSPService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DspServiceModel extends AMDataPreprocessor implements Serializable {

    public List<DSPService> getDSPService(DSPService dto) throws Exception {
        List<DSPService> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT service_id,service_name,service_code,status,description, path from DSP_Service where 1=1 ";
            if (dto != null) {
                if (dto.getServiceId() != null && !dto.getServiceId().equals("")) {
                    strSQL += " and service_id =? ";
                    parameter.add(dto.getServiceId());
                }
                if (dto.getServiceCode() != null && !dto.getServiceCode().equals("")) {
                    strSQL += " and service_code =? ";
                    parameter.add(dto.getServiceCode());
                }
                if (dto.getServiceName() != null && !dto.getServiceName().equals("")) {
                    strSQL += " and service_name =? ";
                    parameter.add(dto.getServiceName());
                }
                if (dto.getStatus() != null && !dto.getStatus().equals("")) {
                    strSQL += " and status =? ";
                    parameter.add(dto.getStatus());
                }
                if (dto.getDescription() != null && !dto.getDescription().equals("")) {
                    strSQL += " and description =? ";
                    parameter.add(dto.getDescription());
                }
                if (dto.getPath() != null && !dto.getPath().equals("")) {
                    strSQL += " and path =? ";
                    parameter.add(dto.getPath());
                }
            }
            strSQL += " order by service_name, service_id desc ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPService item = new DSPService();
                item.setServiceId(mRs.getLong(1));
                item.setServiceName(mRs.getString(2));
                item.setServiceCode(mRs.getString(3));
                item.setStatus(mRs.getString(4));
                item.setDescription(mRs.getString(5));
                item.setPath(mRs.getString(6));
                listReturn.add(item);
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

    public Long insert(DSPService service) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            Long serviceId = SQLUtil.getSequenceValue(mConnection, "DSP_SERVICE_SEQ");
            String strSQL = "insert into DSP_SERVICE(service_id,service_name,service_code,status,description,path) values (?,?,?,?,?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, serviceId);
            mStmt.setString(2, service.getServiceName());
            mStmt.setString(3, service.getServiceCode());
            mStmt.setString(4, "1");
            mStmt.setString(5, service.getDescription());
            mStmt.setString(6, service.getPath());
            mStmt.execute();
            logAfterInsert("DSP_SERVICE", "SERVICE_ID=" + serviceId);
            mConnection.commit();
            return serviceId;
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DSPService service) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            List listChange = logBeforeUpdate("DSP_SERVICE", "SERVICE_ID=" + service.getServiceId());
            String strSQL = " update DSP_SERVICE set service_name = ?,service_code = ?,status = ?,description = ?, path = ? where service_id = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, service.getServiceName());
            mStmt.setString(2, service.getServiceCode());
            mStmt.setString(3, service.getStatus());
            mStmt.setString(4, service.getDescription());
            mStmt.setString(5, service.getPath());
            mStmt.setLong(6, service.getServiceId());
            mStmt.execute();
            logAfterUpdate(listChange);
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(DSPService dto) throws Exception {
        try {
            open();
            String strSQL = " DELETE FROM DSP_SERVICE WHERE SERVICE_ID=? ";
            mConnection.setAutoCommit(false);
//            logBeforeDelete("DSP_SERVICE", "SERVICE_ID=" + dto.getServiceId());
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getServiceId());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close(mConnection);
        }
    }

    public Boolean checkServiceId(Long serviceId) throws Exception {
        try {
            open();
            String strSQL = " select service_id from DSP_SERVICE_POLICY where service_id = ? ";
            strSQL += " UNION ALL";
            strSQL += " select service_id from DSP_ORDER_POLICY_TAB where service_id = ? ";
            strSQL += " UNION ALL";
            strSQL += " select service_id from DSP_SERVICE_PRICE_TAB where service_id = ? ";
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, serviceId);
            mStmt.setLong(2, serviceId);
            mStmt.setLong(3, serviceId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                return true;
            }
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close(mConnection);
        }
        return false;
    }

    public List<DSPService> getDSPServicePath() throws Exception {
        List<DSPService> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT service_id,service_name,service_code,status,description, path from DSP_Service where status = 1 and path is not null ";

            strSQL += " order by service_id desc ";
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPService item = new DSPService();
                item.setServiceId(mRs.getLong(1));
                item.setServiceName(mRs.getString(2));
                item.setServiceCode(mRs.getString(3));
                item.setStatus(mRs.getString(4));
                item.setDescription(mRs.getString(5));
                item.setPath(mRs.getString(6));
                listReturn.add(item);
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
