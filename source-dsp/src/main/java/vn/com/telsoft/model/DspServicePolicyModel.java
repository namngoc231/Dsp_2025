/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DSPServicePolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DspServicePolicyModel extends AMDataPreprocessor implements Serializable {

    public List<DSPServicePolicy> getDSPServicePolicy(DSPServicePolicy dto) throws Exception {
        List<DSPServicePolicy> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT service_id,policy_Name,policy_Value,status,description from DSP_Service_Policy where 1=1 ";
            if (dto != null) {
                if (dto.getServiceId() != null && !dto.getServiceId().equals("")) {
                    strSQL += " and service_id =? ";
                    parameter.add(dto.getServiceId());
                }
                if (dto.getPolicyName() != null && !dto.getPolicyName().equals("")) {
                    strSQL += " and policy_Name =? ";
                    parameter.add(dto.getPolicyName());
                }
                if (dto.getPolicyValue() != null && !dto.getPolicyValue().equals("")) {
                    strSQL += " and policy_value =? ";
                    parameter.add(dto.getPolicyValue());
                }
                if (dto.getStatus() != null && !dto.getStatus().equals("")) {
                    strSQL += " and status =? ";
                    parameter.add(dto.getStatus());
                }
                if (dto.getDescription() != null && !dto.getDescription().equals("")) {
                    strSQL += " and description =? ";
                    parameter.add(dto.getDescription());
                }
            }
            strSQL += " order by policy_Name ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPServicePolicy item = new DSPServicePolicy();
                item.setServiceId(mRs.getLong(1));
                item.setPolicyName(mRs.getString(2));
                item.setPolicyValue(mRs.getString(3));
                item.setStatus(mRs.getString(4));
                item.setDescription(mRs.getString(5));
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

    public Long insert(DSPServicePolicy dto) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "insert into DSP_SERVICE_POLICY(service_id,policy_Name,policy_Value,status,description) values (?,?,?,?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getServiceId());
            mStmt.setString(2, dto.getPolicyName());
            mStmt.setString(3, dto.getPolicyValue());
            mStmt.setString(4, dto.getStatus());
            mStmt.setString(5, dto.getDescription());
            mStmt.execute();
            logAfterInsert("DSP_SERVICE_POLICY", "SERVICE_ID=" + dto.getServiceId());
            mConnection.commit();
            return dto.getServiceId();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DSPServicePolicy servicePolicy) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            List listChange = logBeforeUpdate("DSP_SERVICE_POLICY", "SERVICE_ID=" + servicePolicy.getServiceId());
            String strSQL = " update DSP_SERVICE_POLICY set status = ?,policy_Value = ? where service_id = ? and policy_Name = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, servicePolicy.getStatus());
            mStmt.setString(2, servicePolicy.getPolicyValue());
            mStmt.setLong(3, servicePolicy.getServiceId());
            mStmt.setString(4, servicePolicy.getPolicyName());
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

    public void delete(DSPServicePolicy dto) throws Exception {
        try {
            open();
            String strSQL = " DELETE FROM DSP_SERVICE_POLICY WHERE POLICY_NAME=? and SERVICE_ID = ? ";
            mConnection.setAutoCommit(false);
//            logBeforeDelete("DSP_SERVICE_POLICY", "SERVICE_ID=" + dto.getServiceId());
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, dto.getPolicyName());
            mStmt.setLong(2, dto.getServiceId());
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
}
