/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DSPOrderPolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DspOrderPolicyModel extends AMDataPreprocessor implements Serializable {

    public List<DSPOrderPolicy> getDSPOrderPolicy(DSPOrderPolicy dto) throws Exception {
        List<DSPOrderPolicy> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT id,tab_id,min_Value,max_Value,active_Days,prom_Pct from DSP_Order_Policy where 1=1 ";
            if (dto != null) {
                if (dto.getTabId() != null && !dto.getTabId().equals("")) {
                    strSQL += " and tab_id =? ";
                    parameter.add(dto.getTabId());
                }
                if (dto.getId() != null) {
                    strSQL += " and id =? ";
                    parameter.add(dto.getId());
                }
                if (dto.getMinValue() != null) {
                    strSQL += " and min_Value =? ";
                    parameter.add(dto.getMinValue());
                }
                if (dto.getMaxValue() != null) {
                    strSQL += " and max_Value =? ";
                    parameter.add(dto.getMaxValue());
                }
                if (dto.getActiveDays() != null) {
                    strSQL += " and active_Days =? ";
                    parameter.add(dto.getActiveDays());
                }
                if (dto.getPromPct() != null) {
                    strSQL += " and prom_pct =? ";
                    parameter.add(dto.getPromPct());
                }
            }
            strSQL += " order by min_Value ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPOrderPolicy item = new DSPOrderPolicy();
                item.setId(mRs.getLong(1));
                item.setTabId(mRs.getLong(2));
                item.setMinValue(mRs.getLong(3));
                item.setMaxValue(mRs.getLong(4));
                item.setActiveDays(mRs.getLong(5));
                item.setPromPct(mRs.getLong(6));
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

    public Long insert(DSPOrderPolicy dto) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            Long id = SQLUtil.getSequenceValue(mConnection, "DSP_ORDER_POLICY_SEQ");
            String strSQL = "insert into DSP_ORDER_POLICY(id,tab_id,min_Value,max_Value,active_Days,prom_pct) values (?,?,?,?,?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, id);
            mStmt.setLong(2, dto.getTabId());
            mStmt.setLong(3, dto.getMinValue());
            mStmt.setLong(4, dto.getMaxValue());
            mStmt.setLong(5, dto.getActiveDays());
            mStmt.setLong(6, dto.getPromPct());
            mStmt.execute();
            logAfterInsert("DSP_ORDER_POLICY", "ID=" + id);
            mConnection.commit();
            return id;
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DSPOrderPolicy orderPolicy) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            List listChange = logBeforeUpdate("DSP_ORDER_POLICY", "ID=" + orderPolicy.getId());
            String strSQL = " update DSP_ORDER_POLICY set min_Value = ?,max_Value = ?,active_Days = ?, prom_pct = ? where tab_id = ? and id = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, orderPolicy.getMinValue());
            mStmt.setLong(2, orderPolicy.getMaxValue());
            mStmt.setLong(3, orderPolicy.getActiveDays());
            mStmt.setLong(4, orderPolicy.getPromPct());
            mStmt.setLong(5, orderPolicy.getTabId());
            mStmt.setLong(6, orderPolicy.getId());
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

    public void delete(DSPOrderPolicy dto) throws Exception {
        try {
            open();
            String strSQL = " DELETE FROM DSP_ORDER_POLICY WHERE ID=? and TAB_ID = ? ";
            mConnection.setAutoCommit(false);
//            logBeforeDelete("DSP_ORDER_POLICY", "ID=" + dto.getId());
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getId());
            mStmt.setLong(2, dto.getTabId());
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
