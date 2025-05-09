/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DSPComPrice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DspComPriceModel extends AMDataPreprocessor implements Serializable {

    public List<DSPComPrice> getDSPComPrice(DSPComPrice dto, String table) throws Exception {
        List<DSPComPrice> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT tab_Id,com_Id,status,description, applied_Date,removed_Date from "+ table + " where 1=1 ";
            if (dto != null) {
                if (dto.getTabId() != null) {
                    strSQL += " and tab_id =? ";
                    parameter.add(dto.getTabId());
                }
                if (dto.getComId() != null) {
                    strSQL += " and com_Id =? ";
                    parameter.add(dto.getComId());
                }
                if (dto.getStatus() != null && !dto.getStatus().equals("")) {
                    strSQL += " and status =? ";
                    parameter.add(dto.getStatus());
                }
                if (dto.getDescription() != null && !dto.getDescription().equals("")) {
                    strSQL += " and description =? ";
                    parameter.add(dto.getDescription());
                }
                if (dto.getAppliedDate() != null) {
                    strSQL += " and applied_Date =? ";
                    parameter.add(dto.getAppliedDate());
                }
                if (dto.getRemovedDate() != null) {
                    strSQL += " and removed_Date =? ";
                    parameter.add(dto.getRemovedDate());
                }
            }
            strSQL += " order by tab_id, com_Id ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPComPrice item = new DSPComPrice();
                item.setTabId(mRs.getLong(1));
                item.setComId(mRs.getLong(2));
                item.setStatus(mRs.getString(3));
                item.setDescription(mRs.getString(4));
                item.setAppliedDate(mRs.getDate(5));
                item.setRemovedDate(mRs.getDate(6));
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

//    public List<DSPCompanyExt> getDSPCompanyInit(Long tabId, String table) throws Exception {
//        List<DSPCompanyExt> listReturn = new ArrayList<>();
//        List<Object> parameter = new ArrayList<>();
//        try {
//            open();
//            String strSQL = " select (select b.COM_ID from DSP_COMPANY b where b.status = 1 and b.type in(2,3) and a.COM_ID = b.com_id ) as com_id, " +
//                    "(select b.com_name from DSP_COMPANY b where b.status = 1 and b.type in(2,3) and a.COM_ID = b.com_id ) as com_name, " +
//                    "a.APPLIED_DATE from "+ table+ " a where a.tab_id = ? and a.STATUS =1 ";
//            parameter.add(tabId);
//            strSQL += " order by com_name";
//            mStmt = mConnection.prepareStatement(strSQL);
//
//                int i = 0;
//                for (Object obj : parameter) {
//                    mStmt.setObject(++i, obj);
//                }
//
//            mRs = mStmt.executeQuery();
//            while (mRs.next()) {
//                DSPCompanyExt itemExt = new DSPCompanyExt();
//                DSPCompany item = new DSPCompany();
//                item.setComId(mRs.getLong(1));
//                item.setComName(mRs.getString(2));
//                itemExt.setDspCompany(item);
//                itemExt.setAppliedDate(mRs.getDate(3));
//                listReturn.add(itemExt);
//            }
//        } catch (Exception ex) {
//            throw ex;
//        } finally {
//            close(mRs);
//            close(mStmt);
//            close();
//        }
//        return listReturn;
//    }

//    public void insert(DSPComPrice dto, String table) throws Exception {
//        try {
//            open();
//            mConnection.setAutoCommit(false);
//            String strSQL = "insert into " + table +" (tab_Id,com_Id,status,description, applied_Date,removed_Date) values (?,?,?,?,sysdate,?)";
//            mStmt = mConnection.prepareStatement(strSQL);
//            mStmt.setLong(1, dto.getTabId());
//            mStmt.setLong(2, dto.getComId());
//            mStmt.setString(3, dto.getStatus());
//            mStmt.setString(4, dto.getDescription());
////            if (dto.getAppliedDate() != null) {
////                java.sql.Date sqlAppliedDate = new java.sql.Date(dto.getAppliedDate().getTime());
////                mStmt.setDate(5, sqlAppliedDate);
////            }
//            mStmt.setDate(5, null);
//            mStmt.execute();
//            logAfterInsert(table, "COM_ID=" + dto.getComId());
//            mConnection.commit();
//        } catch (Exception ex) {
//            mConnection.rollback();
//            throw ex;
//        } finally {
//            close(mStmt);
//            close();
//        }
//    }


//
//    public void delete(DSPComPrice dto, String table) throws Exception {
//        try {
//            open();
//            String strSQL = " UPDATE "+ table + " SET status = ?, removed_Date = sysdate WHERE tab_Id=? and com_Id = ? and status = 1 and APPLIED_DATE = ? ";
//            mConnection.setAutoCommit(false);
////            logBeforeDelete("DSP_Com_Order_Pol", "SERVICE_ID=" + dto.getServiceId());
//            mStmt = mConnection.prepareStatement(strSQL);
//            mStmt.setString(1,dto.getStatus());
//            mStmt.setLong(2, dto.getTabId());
//            mStmt.setLong(3, dto.getComId());
//            java.sql.Date sqlAppliDate = new java.sql.Date(dto.getAppliedDate().getTime());
//            mStmt.setDate(4, sqlAppliDate);
//            mStmt.execute();
//            mConnection.commit();
//        } catch (Exception ex) {
//            mConnection.rollback();
//            throw ex;
//        } finally {
//            close(mStmt);
//            close(mConnection);
//        }
//    }
//
//
}
