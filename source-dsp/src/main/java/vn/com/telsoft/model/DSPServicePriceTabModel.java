/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DSPServicePriceTab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DSPServicePriceTabModel extends AMDataPreprocessor implements Serializable {

    public List<DSPServicePriceTab> getDSPServicePriceTab(DSPServicePriceTab dto) throws Exception {
        List<DSPServicePriceTab> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT a.tab_Id,a.service_id,a.start_Time,a.end_Time,a.status,a.description,a.def,a.file_Path,a.CURRENCY," +
                    "(select count(*) from DSP_COM_PRICE b where b.tab_id = a.tab_id and b.status = 1 ) as numberCom, block from DSP_SERVICE_PRICE_TAB a where 1=1 ";
            if (dto != null) {
                if (dto.getServiceId() != null) {
                    strSQL += " and a.service_id =? ";
                    parameter.add(dto.getServiceId());
                }
                if (dto.getTabId() != null) {
                    strSQL += " and a.tab_Id =? ";
                    parameter.add(dto.getTabId());
                }
                if (dto.getStartTime() != null) {
                    strSQL += " and a.start_Time =? ";
                    parameter.add(dto.getStartTime());
                }
                if (dto.getEndTime() != null) {
                    strSQL += " and a.end_Time =? ";
                    parameter.add(dto.getEndTime());
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
                if (dto.getCurrency() != null && !dto.getCurrency().equals("")) {
                    strSQL += " and a.CURRENCY =? ";
                    parameter.add(dto.getCurrency());
                }
                if (dto.getBlock() != null) {
                    strSQL += " and a.BLOCK =? ";
                    parameter.add(dto.getBlock());
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
                DSPServicePriceTab item = new DSPServicePriceTab();
                item.setTabId(mRs.getLong(1));
                item.setServiceId(mRs.getLong(2));
                item.setStartTime(mRs.getDate(3));
                item.setEndTime(mRs.getDate(4));
                item.setStatus(mRs.getString(5));
                item.setDescription(mRs.getString(6));
                item.setDefaultValue((mRs.getInt(7)));
                item.setFilePath(mRs.getString(8));
                item.setCurrency(mRs.getString(9));
                item.setComApply(mRs.getInt(10));
                Long blockDb = mRs.getLong(11);
                item.setBlock(blockDb);
                if (blockDb % (1024 * 1024) == 0) {
                    item.setStrBlock(numberPrice(String.valueOf(blockDb / (1024 * 1024)) + "GB"));
                } else if (blockDb % 1024 == 0) {
                    item.setStrBlock(numberPrice(String.valueOf(blockDb / (1024)) + "MB"));
                } else {
                    item.setStrBlock(numberPrice(String.valueOf(blockDb)));
                }
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

    public Long insert(DSPServicePriceTab dto) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            Long id = SQLUtil.getSequenceValue(mConnection, "DSP_SERVICE_PRICE_TAB_SEQ");
            String strSQL = "insert into DSP_SERVICE_PRICE_TAB(tab_Id,service_id,status,description,def,file_Path,start_Time,end_Time,currency,block)";
//            if (dto.getStartTime() != null) {
//                strSQL += ",start_Time ";
//            }
//            if (dto.getEndTime() != null) {
//                strSQL += ",end_Time ";
//            }
            strSQL += " values (?,?,?,?,?,?,?,?,?,?)";
//            if (dto.getStartTime() != null) {
//                strSQL += ",?";
//            }
//            if (dto.getEndTime() != null) {
//                strSQL += ",?";
//            }
//            strSQL += ")";
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 1;
            mStmt.setLong(i++, id);
            mStmt.setLong(i++, dto.getServiceId());
            mStmt.setString(i++, dto.getStatus());
            mStmt.setString(i++, dto.getDescription());
            mStmt.setInt(i++, dto.getDefaultValue());
            mStmt.setString(i++, dto.getFilePath());
            if (dto.getStartTime() != null) {
                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
                mStmt.setDate(i++, sqlDateStart);
            } else {
                mStmt.setDate(i++, null);
            }
            if (dto.getEndTime() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(dto.getEndTime().getTime());
                mStmt.setDate(i++, sqlDateEnd);
            } else {
                mStmt.setDate(i++, null);
            }
            mStmt.setString(i++, dto.getCurrency());
            mStmt.setLong(i++, dto.getBlock());
            mStmt.execute();
            logAfterInsert("DSP_SERVICE_PRICE_TAB", "TAB_ID=" + id);
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

    public void update(DSPServicePriceTab servicePrice) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            List listChange = logBeforeUpdate("DSP_SERVICE_PRICE_TAB", "TAB_ID=" + servicePrice.getTabId());
            String strSQL = " update DSP_SERVICE_PRICE_TAB set status = ?,description=?,def = ?,file_Path=?,end_Time = ?,start_Time=?, currency=?, block = ? where tab_Id = ? and service_id = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, servicePrice.getStatus());
            mStmt.setString(2, servicePrice.getDescription());
            mStmt.setInt(3, servicePrice.getDefaultValue());
            mStmt.setString(4, servicePrice.getFilePath());
            if (servicePrice.getEndTime() != null && servicePrice.getStartTime() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(servicePrice.getEndTime().getTime());
                mStmt.setDate(5, sqlDateEnd);
                java.sql.Date sqlDateStart = new java.sql.Date(servicePrice.getStartTime().getTime());
                mStmt.setDate(6, sqlDateStart);
                mStmt.setString(7, servicePrice.getCurrency());
                mStmt.setLong(8, servicePrice.getBlock());
                mStmt.setLong(9, servicePrice.getTabId());
                mStmt.setLong(10, servicePrice.getServiceId());

            }
            if (servicePrice.getStartTime() == null && servicePrice.getEndTime() == null) {
                mStmt.setDate(5, null);
                mStmt.setDate(6, null);
                mStmt.setString(7, servicePrice.getCurrency());
                mStmt.setLong(8, servicePrice.getBlock());
                mStmt.setLong(9, servicePrice.getTabId());
                mStmt.setLong(10, servicePrice.getServiceId());
            }
            if (servicePrice.getStartTime() != null && servicePrice.getEndTime() == null) {

                mStmt.setDate(5, null);
                java.sql.Date sqlDateStart = new java.sql.Date(servicePrice.getStartTime().getTime());
                mStmt.setDate(6, sqlDateStart);
                mStmt.setString(7, servicePrice.getCurrency());
                mStmt.setLong(8, servicePrice.getBlock());
                mStmt.setLong(9, servicePrice.getTabId());
                mStmt.setLong(10, servicePrice.getServiceId());
            }
            if (servicePrice.getStartTime() == null && servicePrice.getEndTime() != null) {
                java.sql.Date sqlDateEnd = new java.sql.Date(servicePrice.getEndTime().getTime());
                mStmt.setDate(5, sqlDateEnd);
                mStmt.setDate(6, null);
                mStmt.setString(7, servicePrice.getCurrency());
                mStmt.setLong(8, servicePrice.getBlock());
                mStmt.setLong(9, servicePrice.getTabId());
                mStmt.setLong(10, servicePrice.getServiceId());
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

    public void delete(DSPServicePriceTab dto) throws Exception {
        try {
            open();
            String strSQL = "UPDATE DSP_SERVICE_PRICE_TAB set status = 2 WHERE TAB_ID=? and SERVICE_ID = ? ";
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getTabId());
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

//    public Long insertGroup(DSPServicePriceTab dto, List<DSPOrderPolicy> lstDspOr, List<DSPComOrderPol> lstDspPol) throws Exception {
//        PreparedStatement stmtInsOrderPolicyTab = null;
//        PreparedStatement stmtInsOrderPolicy = null;
//        PreparedStatement stmtInsComOrderPol = null;
//        Long idTab = null;
//        String strSqlOrderPolicyTab = "insert into DSP_SERVICE_PRICE_TAB(tab_Id,service_id,status,description,def,file_Path, start_Time" +
//                " ,end_Time) values (?,?,?,?,?,?,?,?) ";
//        String strSqlOrderPolicy = "insert into DSP_ORDER_POLICY(id,tab_id,min_Value,max_Value,active_Days,prom_pct) values (?,?,?,?,?,?)";
//        String strSqlComOrderPol = "insert into DSP_COM_ORDER_POL (tab_Id,com_Id,status,description, applied_Date,removed_Date) values (?,?,?,?,sysdate,?)";
//        try {
//            open();
//            mConnection.setAutoCommit(false);
//            idTab = SQLUtil.getSequenceValue(mConnection, "DSP_SERVICE_PRICE_TAB_SEQ");
//            stmtInsOrderPolicyTab = mConnection.prepareStatement(strSqlOrderPolicyTab);
//            int i = 1;
//            stmtInsOrderPolicyTab.setLong(i++, idTab);
//            stmtInsOrderPolicyTab.setLong(i++, dto.getServiceId());
//            stmtInsOrderPolicyTab.setString(i++, "0");
//            stmtInsOrderPolicyTab.setString(i++, dto.getDescription());
//            stmtInsOrderPolicyTab.setInt(i++, dto.getDefaultValue());
//            stmtInsOrderPolicyTab.setString(i++, dto.getFilePath());
//            if (dto.getStartTime() != null) {
//                java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                stmtInsOrderPolicyTab.setDate(i++, sqlDateStart);
//            } else {
//                stmtInsOrderPolicyTab.setDate(i++, null);
//            }
//            if (dto.getEndTime() != null) {
//                java.sql.Date sqlDateEnd = new java.sql.Date(dto.getEndTime().getTime());
//                stmtInsOrderPolicyTab.setDate(i++, sqlDateEnd);
//            } else {
//                stmtInsOrderPolicyTab.setDate(i++, null);
//            }
//            stmtInsOrderPolicyTab.executeUpdate();
//
//            stmtInsOrderPolicy = mConnection.prepareStatement(strSqlOrderPolicy);
//            for (DSPOrderPolicy dspOr : lstDspOr) {
//                Long idOr = SQLUtil.getSequenceValue(mConnection, "DSP_ORDER_POLICY_SEQ");
//                int j = 1;
//                stmtInsOrderPolicy.setLong(j++, idOr);
//                stmtInsOrderPolicy.setLong(j++, idTab);
//                stmtInsOrderPolicy.setLong(j++, dspOr.getMinValue());
//                stmtInsOrderPolicy.setLong(j++, dspOr.getMaxValue());
//                stmtInsOrderPolicy.setLong(j++, dspOr.getActiveDays());
//                stmtInsOrderPolicy.setLong(j++, dspOr.getPromPct());
//                stmtInsOrderPolicy.addBatch();
//            }
//            stmtInsOrderPolicy.executeBatch();
//
//            stmtInsComOrderPol = mConnection.prepareStatement(strSqlComOrderPol);
//            for (DSPComOrderPol dspPol : lstDspPol) {
//                int k = 1;
//                stmtInsComOrderPol.setLong(k++, idTab);
//                stmtInsComOrderPol.setLong(k++, dspPol.getComId());
//                stmtInsComOrderPol.setString(k++, "1");
//                stmtInsComOrderPol.setString(k++, dspPol.getDescription());
//                stmtInsComOrderPol.setDate(k++, null);
//                stmtInsComOrderPol.addBatch();
//            }
//            stmtInsComOrderPol.executeBatch();
//
//            mConnection.commit();
//        } catch (Exception ex) {
//            mConnection.rollback();
//            Logger.getLogger(DSPServicePriceTabModel.class.getName()).log(Level.SEVERE, null, ex);
//            throw ex;
//        } finally {
//            mConnection.setAutoCommit(true);
//            close(stmtInsOrderPolicyTab);
//            close(stmtInsOrderPolicy);
//            close(stmtInsComOrderPol);
//            close();
//        }
//        return idTab;
//    }

//    public Boolean validateDefault(Long serviceId) throws Exception {
//        List<DSPServicePriceTab> listReturn = new ArrayList<>();
//        List<Object> parameter = new ArrayList<>();
//        try {
//            open();
//            String strSQL = " select start_Time,COUNT(*) from DSP_SERVICE_PRICE_TAB where  DEF = 1 and STATUS = 1 and " +
//                    " SERVICE_ID = ? GROUP BY start_Time HAVING COUNT(*) > 0 ";
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

    public Boolean validateDefaultNull(DSPServicePriceTab dto, Boolean dateIsNull) throws Exception {
        List<DSPServicePriceTab> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " select * from DSP_SERVICE_PRICE_TAB where  DEF = 1 and STATUS = 1 and " +
                    "   SERVICE_ID = ? ";
            parameter.add(dto.getServiceId());
            if (dateIsNull) {
                strSQL += " and start_Time is null and end_Time is null ";
            } else {
                if (dto.getStartTime() == null) {
                    strSQL += " and start_Time  is null ";
                } else {
                    strSQL += " and start_Time  = ? ";
                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
                    parameter.add(sqlDateStart);
                }
                if (dto.getEndTime() == null) {
                    strSQL += " and end_Time  is null ";
                } else {
                    strSQL += " and end_Time  = ? ";
                    java.sql.Date sqlDateEnd = new java.sql.Date(dto.getEndTime().getTime());
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
            if(dateIsNull){
                if (index > 1) {
                    return false;
                }
            } else{
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

    public List<DSPServicePriceTab> validateNotDefault(Long comId, DSPServicePriceTab dto) throws Exception {
        List<DSPServicePriceTab> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = "";
//            if(dto.getDefaultValue().equals(1)){
//                 strSQL = "  select a.tab_Id,a.service_id,a.start_Time,a.end_Time,a.status,a.description,a.def,a.file_Path  " +
//                        "from DSP_SERVICE_PRICE_TAB a, dsp_com_price b " +
//                        "where 1 = 1 " +
//                        "    and a.DEF = 0 " +
//                        "    and a.SERVICE_ID = ? " +
//                        "    and a.TAB_ID = b.TAB_ID " +
//                        "    and a.STATUS = 1 " +
//                        "    and b.com_id = ?   and b.status = 1  " +
//                        "    and ((a.start_Time is null and a.end_Time is null)  " +
//                        "        or (? is null and ? is null) " +
//                        // p_start_Time   p_end_Time
//                        "        or (? is null and ? is not null and (a.start_Time is null or (a.start_Time is not null and a.start_Time < ?))) " +
////                    p_start_Time p_end_Time p_end_Time
//                        "        or (? is not null and ? is null and (a.end_Time is null or (a.end_Time is not null and a.end_Time > ?))) " +
////                    p_start_Time p_end_Time p_start_Time
//                        "        or (a.start_Time > ? and a.start_Time < ? ) " +
////                     p_end_Time
//                        "        or (a.end_Time > ? and a.end_Time < ?) " +
//                        "        or (a.start_Time < ? and ? < a.end_Time  ) " +
//
//                        "        or (a.start_Time = ? and  a.end_Time = ?  ) " +
////                     p_end_Time
//                        "        or (a.start_Time < ? and ? < a.end_Time  )) ";
//
//
//                parameter.add(dto.getServiceId());
//                parameter.add(comId);
////            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
////            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
////            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
////            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
////            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
////            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
////            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
////            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
////            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
////            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
////            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
////            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//                //            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//                //            parameter.add(dto.getStartTime());
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
//                if (dto.getStartTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
//                    parameter.add(sqlDateStart);
//                }
//
//                //            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
//                //            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//
//                //            parameter.add(dto.getEndTime());
//                if (dto.getEndTime() == null) {
//                    parameter.add(null);
//                } else {
//                    java.sql.Date sqlEndStart = new java.sql.Date(dto.getEndTime().getTime());
//                    parameter.add(sqlEndStart);
//                }
//            } else{

            strSQL = "select a.tab_Id,a.service_id,a.start_Time,a.end_Time,a.status,a.description,a.def,a.file_Path " +
                    "FROM DSP_SERVICE_PRICE_TAB a, DSP_COM_PRICE b " +
                    "     WHERE 1 = 1 AND a.DEF = 0 AND a.SERVICE_ID = ? AND a.TAB_ID = b.TAB_ID " +
                    "     AND a.STATUS = 1 AND b.COM_ID = ? AND b.STATUS = 1 " +
                    "     AND ((a.END_TIME IS NULL OR (? <= a.END_TIME)) AND (? IS NULL OR (? >= a.START_TIME)))";
            parameter.add(dto.getServiceId());
            parameter.add(comId);
            java.sql.Date sqlDateStart = new java.sql.Date(dto.getStartTime().getTime());
            parameter.add(sqlDateStart);
            java.sql.Date sqlDateEnd = new java.sql.Date(dto.getStartTime().getTime());
            parameter.add(sqlDateEnd);
            parameter.add(sqlDateEnd);

            mStmt = mConnection.prepareStatement(strSQL);
            int i = 0;
            for (Object obj : parameter) {
                mStmt.setObject(++i, obj);
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPServicePriceTab item = new DSPServicePriceTab();
                item.setTabId(mRs.getLong(1));
                item.setServiceId(mRs.getLong(2));
                item.setStartTime(mRs.getDate(3));
                item.setEndTime(mRs.getDate(4));
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

    public String numberPrice(String price) throws Exception {
        int size = price.length();
        String resultAf = "";
        String resultBef = "";
        if (size >= 5) {
            if(price.contains("G")||price.contains("M")){
                resultAf = price.substring(size - 5, size);
                size -= 5;
            } else{
                resultAf = price.substring(size - 4, size);
                size -= 4;
            }
            if (size == 1) {
                price = price.substring(0, 1);
            } else {
                price = price.substring(0, size);
            }

            int thuong = size / 3;
            int du = size % 3;
            if (du == 0) {
                for (int i = 0; i < size; i = i + 3) {
                    resultBef += price.substring(i, i + 3) + ".";
                }
            } else {
                if (du == 1) {
                    resultBef += price.substring(0, 1) + ".";
                    for (int i = 1; i < size; i = i + 3) {
                        resultBef += price.substring(i, i + 3) + ".";
                    }
                }
                if (du == 2) {
                    resultBef += price.substring(0, 2) + ".";
                    for (int i = 2; i < size; i = i + 3) {
                        resultBef += price.substring(i, i + 3) + ".";
                    }
                }
            }
        } else {
            return price;
        }
        String strr = resultBef.substring(0, resultBef.length()) + resultAf;
        return strr;
    }
}
