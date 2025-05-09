/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DSPPolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DspPolicyModel extends AMDataPreprocessor implements Serializable {

    public List<DSPPolicy> getDSPPolicy(DSPPolicy dto) throws Exception {
        List<DSPPolicy> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT policy_name, description from DSP_Policy where 1=1 ";
            if (dto != null) {
                if (dto.getPolicyName() != null && !dto.getPolicyName().equals("")) {
                    strSQL += " and policy_name =? ";
                    parameter.add(dto.getPolicyName());
                }
                if (dto.getDescription() != null && !dto.getDescription().equals("")) {
                    strSQL += " and description =? ";
                    parameter.add(dto.getDescription());
                }
            }
            strSQL += " order by policy_name ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPPolicy item = new DSPPolicy();
                item.setPolicyName(mRs.getString(1));
                item.setDescription(mRs.getString(2));
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
