/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DSPComOrderPol;
import vn.com.telsoft.entity.DSPOrderPolicy;
import vn.com.telsoft.entity.DSPOrderPolicyTab;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author TrieuNV
 */
public class DspOrderPolicyTabModel extends AMDataPreprocessor implements Serializable {

    public List<DSPOrderPolicyTab> getDSPOrderPolicyTab(DSPOrderPolicyTab dto) throws Exception {
        List<DSPOrderPolicyTab> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
//            DSPComOrderPol dspCom = new DSPComOrderPol();
//            dspCom.setTabId(dto.getTabId());
//            dspCom.setStatus("1");
//            listComOrderTable = dspComOrderPolModel.getDSPComOrderPol(dspCom, DSP_COM_ORDER_POL);
            String strSQL = " SELECT a.tab_Id,a.service_id,a.start_Date,a.end_Date,a.status,a.description,a.def,a.file_Path,a.cust_type, " +
                    "(select count(*) from DSP_COM_ORDER_POL b where b.tab_id = a.tab_id and b.status = 1 ) as numberCom from DSP_Order_Policy_tab a where 1=1 ";
            if (dto != null) {
                if (dto.getServiceId() != null) {
                    strSQL += " and a.service_id =? ";
                    parameter.add(dto.getServiceId());
                }
                if (dto.getTabId() != null) {
                    strSQL += " and a.tab_Id =? ";
                    parameter.add(dto.getTabId());
                }
                if (dto.getStartDate() != null) {
                    strSQL += " and a.start_Date =? ";
                    parameter.add(dto.getStartDate());
                }
                if (dto.getEndDate() != null) {
                    strSQL += " and a.end_Date =? ";
                    parameter.add(dto.getEndDate());
                }

                if (dto.getDescription() != null && !dto.getDescription().equals("")) {
                    strSQL += " and a.description =? ";
                    parameter.add(dto.getDescription());
                }
                if (dto.getDefaultValue() != null) {
                    strSQL += " and a.def =? ";
                    parameter.add(dto.getDefaultValue());
                }
                if (dto.getFilePath() != null && !dto.getFilePath().equals("")) {
                    strSQL += " and a.file_Path =? ";
                    parameter.add(dto.getFilePath());
                }
            }
            strSQL += " and a.status in (1,0,2) ";
            strSQL += " order by a.description,a.tab_Id ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPOrderPolicyTab item = new DSPOrderPolicyTab();
                item.setTabId(mRs.getLong(1));
                item.setServiceId(mRs.getLong(2));
                item.setStartDate(mRs.getDate(3));
                item.setEndDate(mRs.getDate(4));
                item.setStatus(mRs.getString(5));
                item.setDescription(mRs.getString(6));
                item.setDefaultValue(mRs.getInt(7));
                item.setFilePath(mRs.getString(8));
                item.setCustType(mRs.getString(9));
                item.setComApply(mRs.getInt(10));
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

    public Long insert(DSPOrderPolicyTab dto) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            Long id = SQLUtil.getSequenceValue(mConnection, "DSP_ORDER_POLICY_TAB_SEQ");
            String strSQL = "insert into DSP_ORDER_POLICY_TAB(tab_Id,status,description,def,file_Path,start_Date,end_Date,cust_type)";
//            if (dto.getStartDate() != null) {
//                strSQL += ",start_Date ";
//            }
//            if (dto.getEndDate() != null) {
//                strSQL += ",end_Date ";
//            }
            strSQL += " values (?,?,?,?,?,?,?,?)";
//            if (dto.getStartDate() != null) {
//                strSQL += ",?";
//            }
//            if (dto.getEndDate() != null) {
//                strSQL += ",?";
//            }
//            strSQL += ")";
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 1;
            mStmt.setLong(i++, id);
            mStmt.setString(i++, dto.getStatus());
            mStmt.setString(i++, dto.getDescription());
            mStmt.setInt(i++, dto.getDefaultValue());
            mStmt.setString(i++, dto.getFilePath());
            if (dto.getStartDate() != null) {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                mStmt.setDate(i++, sqlDateStart);
            } else {
                mStmt.setDate(i++, null);
            }
            if (dto.getEndDate() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(dto.getEndDate().getTime());
                mStmt.setDate(i++, sqlDateEnd);
            } else {
                mStmt.setDate(i++, null);
            }
            if (dto.getCustType() != null) {
                mStmt.setString(i, dto.getCustType());
            } else {
                mStmt.setString(i, null);
            }
            mStmt.execute();
            logAfterInsert("DSP_ORDER_POLICY_TAB", "TAB_ID=" + id);
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

    public void update(DSPOrderPolicyTab orderPolicy) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            List listChange = logBeforeUpdate("DSP_ORDER_POLICY_TAB", "TAB_ID=" + orderPolicy.getTabId());
            String strSQL = " update DSP_ORDER_POLICY_TAB set status = ?,description=?,def = ?,file_Path=?,end_Date = ?,start_Date=?,cust_type=? where tab_Id = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, orderPolicy.getStatus());
            mStmt.setString(2, orderPolicy.getDescription());
            mStmt.setInt(3, orderPolicy.getDefaultValue());
            mStmt.setString(4, orderPolicy.getFilePath());
            if (orderPolicy.getEndDate() != null && orderPolicy.getStartDate() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(orderPolicy.getEndDate().getTime());
                mStmt.setDate(5, sqlDateEnd);
                java.sql.Date sqlDateStart = new java.sql.Date(orderPolicy.getStartDate().getTime());
                mStmt.setDate(6, sqlDateStart);
                mStmt.setLong(8, orderPolicy.getTabId());
            }
            if (orderPolicy.getStartDate() == null && orderPolicy.getEndDate() == null) {
                mStmt.setDate(5, null);
                mStmt.setDate(6, null);
                mStmt.setLong(8, orderPolicy.getTabId());
            }
            if (orderPolicy.getStartDate() != null && orderPolicy.getEndDate() == null) {
                mStmt.setDate(5, null);
                java.sql.Date sqlDateStart = new java.sql.Date(orderPolicy.getStartDate().getTime());
                mStmt.setDate(6, sqlDateStart);
                mStmt.setLong(8, orderPolicy.getTabId());
            }
            if (orderPolicy.getStartDate() == null && orderPolicy.getEndDate() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(orderPolicy.getEndDate().getTime());
                mStmt.setDate(5, sqlDateEnd);
                mStmt.setDate(6, null);
                mStmt.setLong(8, orderPolicy.getTabId());
            }
            if (orderPolicy.getDefaultValue() == 1) {
                mStmt.setString(7, orderPolicy.getCustType());
            } else {
                mStmt.setString(7, null);
            }
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

    public void delete(DSPOrderPolicyTab dto) throws Exception {
        try {
            open();
            String strSQL = "UPDATE DSP_ORDER_POLICY_TAB set status = 2 WHERE TAB_ID=? ";
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getTabId());
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

    public Long insertGroup(DSPOrderPolicyTab dto, List<DSPOrderPolicy> lstDspOr, List<DSPComOrderPol> lstDspPol) throws Exception {
        PreparedStatement stmtInsOrderPolicyTab = null;
        PreparedStatement stmtInsOrderPolicy = null;
        PreparedStatement stmtInsComOrderPol = null;
        Long idTab = null;
        String strSqlOrderPolicyTab = "insert into DSP_ORDER_POLICY_TAB(tab_Id,service_id,status,description,def,file_Path, start_Date" +
                " ,end_Date) values (?,?,?,?,?,?,?,?) ";
        String strSqlOrderPolicy = "insert into DSP_ORDER_POLICY(id,tab_id,min_Value,max_Value,active_Days,prom_pct) values (?,?,?,?,?,?)";
        String strSqlComOrderPol = "insert into DSP_COM_ORDER_POL (tab_Id,com_Id,status,description, applied_Date,removed_Date) values (?,?,?,?,sysdate,?)";
        try {
            open();
            mConnection.setAutoCommit(false);
            idTab = SQLUtil.getSequenceValue(mConnection, "DSP_ORDER_POLICY_TAB_SEQ");
            stmtInsOrderPolicyTab = mConnection.prepareStatement(strSqlOrderPolicyTab);
            int i = 1;
            stmtInsOrderPolicyTab.setLong(i++, idTab);
            stmtInsOrderPolicyTab.setLong(i++, dto.getServiceId());
            stmtInsOrderPolicyTab.setString(i++, "0");
            stmtInsOrderPolicyTab.setString(i++, dto.getDescription());
            stmtInsOrderPolicyTab.setInt(i++, dto.getDefaultValue());
            stmtInsOrderPolicyTab.setString(i++, dto.getFilePath());
            if (dto.getStartDate() != null) {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                stmtInsOrderPolicyTab.setDate(i++, sqlDateStart);
            } else {
                stmtInsOrderPolicyTab.setDate(i++, null);
            }
            if (dto.getEndDate() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(dto.getEndDate().getTime());
                stmtInsOrderPolicyTab.setDate(i++, sqlDateEnd);
            } else {
                stmtInsOrderPolicyTab.setDate(i++, null);
            }
            stmtInsOrderPolicyTab.executeUpdate();

            stmtInsOrderPolicy = mConnection.prepareStatement(strSqlOrderPolicy);
            for (DSPOrderPolicy dspOr : lstDspOr) {
                Long idOr = SQLUtil.getSequenceValue(mConnection, "DSP_ORDER_POLICY_SEQ");
                int j = 1;
                stmtInsOrderPolicy.setLong(j++, idOr);
                stmtInsOrderPolicy.setLong(j++, idTab);
                stmtInsOrderPolicy.setLong(j++, dspOr.getMinValue());
                stmtInsOrderPolicy.setLong(j++, dspOr.getMaxValue());
                stmtInsOrderPolicy.setLong(j++, dspOr.getActiveDays());
                stmtInsOrderPolicy.setLong(j++, dspOr.getPromPct());
                stmtInsOrderPolicy.addBatch();
            }
            stmtInsOrderPolicy.executeBatch();

            stmtInsComOrderPol = mConnection.prepareStatement(strSqlComOrderPol);
            for (DSPComOrderPol dspPol : lstDspPol) {
                int k = 1;
                stmtInsComOrderPol.setLong(k++, idTab);
                stmtInsComOrderPol.setLong(k++, dspPol.getComId());
                stmtInsComOrderPol.setString(k++, "1");
                stmtInsComOrderPol.setString(k++, dspPol.getDescription());
                stmtInsComOrderPol.setDate(k++, null);
                stmtInsComOrderPol.addBatch();
            }
            stmtInsComOrderPol.executeBatch();

            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            Logger.getLogger(DspOrderPolicyTabModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmtInsOrderPolicyTab);
            close(stmtInsOrderPolicy);
            close(stmtInsComOrderPol);
            close();
        }
        return idTab;
    }

//    public Boolean validateDefault(Long serviceId) throws Exception {
//        List<DSPOrderPolicyTab> listReturn = new ArrayList<>();
//        List<Object> parameter = new ArrayList<>();
//        try {
//            open();
//            String strSQL = " select start_Date,COUNT(*) from DSP_ORDER_POLICY_TAB where  DEF = 1 and STATUS = 1 and " +
//                    " SERVICE_ID = ? GROUP BY start_Date HAVING COUNT(*) > 0 ";
//            parameter.add(serviceId);
//            mStmt = mConnection.prepareStatement(strSQL);
//            int i = 0;
//            for (Object obj : parameter) {
//                mStmt.setObject(++i, obj);
//            }
//            mRs = mStmt.executeQuery();
//            while (mRs.next()) {
//              return false;
//            }
//        } catch (Exception ex) {
//            throw ex;
//        } finally {
//            close(mRs);
//            close(mStmt);
//            close();
//        }
//        return true;
//    }

    public Boolean validateDefaultNull(DSPOrderPolicyTab dto, Boolean dateIsNull) throws Exception {
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " select * from DSP_ORDER_POLICY_TAB where DEF = 1 and STATUS = 1 " +
                    "   ";
            if (dto.getCustType() != null) {
                strSQL += " and cust_type = ? ";
                parameter.add(dto.getCustType());
            }

            if (dateIsNull) {
                strSQL += " and start_date is null and end_date is null ";
            } else {
                if (dto.getStartDate() == null) {
                    strSQL += " and start_date  is null ";
                } else {
                    strSQL += " and start_date  = ? ";
                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                    parameter.add(sqlDateStart);
                }
                if (dto.getEndDate() == null) {
                    strSQL += " and end_date  is null ";
                } else {
                    strSQL += " and end_date  = ? ";
                    java.sql.Date sqlDateEnd = new java.sql.Date(dto.getEndDate().getTime());
                    parameter.add(sqlDateEnd);
                }
            }

            mStmt = mConnection.prepareStatement(strSQL);
            int i = 0;
            for (Object obj : parameter) {
                mStmt.setObject(++i, obj);
            }
            mRs = mStmt.executeQuery();
            int index = 0;
            while (mRs.next()) {
                index++;
            }
            if (dateIsNull) {
                if (index > 1) {
                    return false;
                }
            } else {
                if (index > 0) {
                    return false;
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return true;
    }

    public List<DSPOrderPolicyTab> validateNotDefault(Long comId, DSPOrderPolicyTab dto) throws Exception {
        List<DSPOrderPolicyTab> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = "  select a.tab_Id,a.service_id,a.start_Date,a.end_Date,a.status,a.description,a.def,a.file_Path  " +
                    "from DSP_ORDER_POLICY_TAB a, dsp_com_order_pol b " +
                    "where 1 = 1 " +
                    "    and a.DEF = 0 " +
                    "    and a.TAB_ID = b.TAB_ID " +
                    "    and a.STATUS = 1 " +
                    "    and b.com_id = ?   and b.status = 1  " +
                    "    and ((a.start_date is null and a.end_date is null)  " +
                    "        or (? is null and ? is null) " +
                    // p_start_date   p_end_date
                    "        or (? is null and ? is not null and (a.start_date is null or (a.start_date is not null and a.start_date <= ?))) " +
//                    p_start_date p_end_date p_end_date
                    "        or (? is not null and ? is null and (a.end_date is null or (a.end_date is not null and a.end_date >= ?))) " +
//                    p_start_date p_end_date p_start_date
                    "        or ((a.start_date BETWEEN ? and ?) " +
//                    p_start_date p_end_date
                    "        or (a.end_date BETWEEN ? and ?))) ";

            parameter.add(comId);
//        1    parameter.add(dto.getStartDate());
            if (dto.getStartDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                parameter.add(sqlDateStart);
            }

//          2  parameter.add(dto.getEndDate());
            if (dto.getEndDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
                parameter.add(sqlEndStart);
            }
//3
            if (dto.getStartDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                parameter.add(sqlDateStart);
            }
//4
            if (dto.getEndDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
                parameter.add(sqlEndStart);
            }
//5
            if (dto.getEndDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
                parameter.add(sqlEndStart);
            }
//6
            if (dto.getStartDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                parameter.add(sqlDateStart);
            }
            //7
            if (dto.getEndDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
                parameter.add(sqlEndStart);
            }
            //8
            if (dto.getStartDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                parameter.add(sqlDateStart);
            }
            //9
            if (dto.getStartDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                parameter.add(sqlDateStart);
            }
            //10
            if (dto.getEndDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
                parameter.add(sqlEndStart);
            }
            //11
            if (dto.getStartDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
                parameter.add(sqlDateStart);
            }
            //12
            if (dto.getEndDate() == null) {
                parameter.add(null);
            } else {
                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
                parameter.add(sqlEndStart);
            }
//           String strSQL = "  select a.tab_Id,a.service_id,a.start_Date,a.end_Date,a.status,a.description,a.def,a.file_Path  " +
//                    "from DSP_ORDER_POLICY_TAB a, dsp_com_order_pol b " +
//                    "where 1 = 1 " +
//                    "    and a.DEF = 0 " +
//                    "    and a.TAB_ID = b.TAB_ID " +
//                    "    and a.STATUS = 1 " +
//                    "    and b.com_id = ?   and b.status = 1  " +
//                    "    and ((a.start_date is null and a.end_date is null)  " +
//                    "        or (? is null and ? is null) " +
//                    // p_start_date   p_end_date
//                    "        or (? is null and ? is not null and (a.start_date is null or (a.start_date is not null and a.start_date < ?))) " +
////                    p_start_date p_end_date p_end_date
//                    "        or (? is not null and ? is null and (a.end_date is null or (a.end_date is not null and a.end_date > ?))) " +
////                    p_start_date p_end_date p_start_date
//                    "        or (a.start_date > ? and a.start_date < ? ) " +
////               p_start_date      p_end_date
//                    "        or (a.end_date > ? and a.end_date < ?) " +
////                    p_start_date  p_start_date
//                    "        or (a.start_date < ? and ? < a.end_date  ) " +
////                    p_start_date  p_end_date
//                    "        or (a.start_date = ? and  a.end_date = ?  ) " +
////                    p_end_date p_end_date
//                    "        or (a.start_date < ? and ? < a.end_date  )) ";
////            p_start_date
//            parameter.add(comId);
//            //            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
////            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
////            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
////            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
////            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
////            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
////            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
////            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
////            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
////            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
////            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
////            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//            //            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//            //            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
//            //            parameter.add(dto.getStartDate());
//            if (dto.getStartDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartDate().getTime());
//                parameter.add(sqlDateStart);
//            }
//
//            //            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
//            //            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }
//
//            //            parameter.add(dto.getEndDate());
//            if (dto.getEndDate() == null) {
//                parameter.add(null);
//            } else {
//                java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndDate().getTime());
//                parameter.add(sqlEndStart);
//            }

            mStmt = mConnection.prepareStatement(strSQL);
            int i = 0;
            for (Object obj : parameter) {
                mStmt.setObject(++i, obj);
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPOrderPolicyTab item = new DSPOrderPolicyTab();
                item.setTabId(mRs.getLong(1));
                item.setServiceId(mRs.getLong(2));
                item.setStartDate(mRs.getDate(3));
                item.setEndDate(mRs.getDate(4));
                item.setStatus(mRs.getString(5));
                item.setDescription(mRs.getString(6));
                item.setDefaultValue((mRs.getInt(7)));
                item.setFilePath(mRs.getString(8));
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
