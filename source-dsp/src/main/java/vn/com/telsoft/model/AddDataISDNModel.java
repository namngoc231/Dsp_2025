package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.DateUtil;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.util.ExportExcelUtil;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddDataISDNModel extends AMDataPreprocessor implements Serializable {
    private static final String DATA_SERVICE = "ADD";
    private static final String DATA_GROUP_SERVICE = "ADD_GROUP";
    //order status
    private static final String STATUS_APPROVAL = "2";
    private static final String STATUS_REJECT = "5";
    //SMS
    private static final String SMS_APPROVAL = "APPROVE_ADD_DATA_ISDN";
    private static final String SMS_REJECT = "REJECT_ADD_DATA_ISDN";
    private static String pattern = "###,###.###";
    private DSPOrderModel model = new DSPOrderModel();
    private DSPMtQueueModel modelSmS = new DSPMtQueueModel();
    private DecimalFormat decimalFormat = new DecimalFormat(pattern);

    public static String getQueryIn(List object) {
        StringBuilder inQuery = new StringBuilder("in (");
        for (int i = 0; i < object.size(); i++) {
            inQuery.append("?,");
        }
        inQuery.deleteCharAt(inQuery.length() - 1);
        inQuery.append(")");
        return inQuery.toString();
    }

    public Long checkUserType(Long userId) throws Exception {
        Long type = null;
        try {
            open();
            String strSQL = "select type " +
                    " from dsp_company " +
                    " where user_id = ? or group_id in (select GROUP_ID from AM_GROUP_USER where user_id = ?)" +
                    " order by TYPE ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                type = mRs.getLong(1);
            }
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return type;
    }

    public Long checkComID(Long userId) throws Exception {
        Long comID = null;
        try {
            open();
            String strSQL = "select com_id " +
                    "  from dsp_company " +
                    "  where user_id = ? or group_id in (select GROUP_ID from AM_GROUP_USER where user_id = ?)" +
                    " order by TYPE";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                comID = mRs.getLong(1);
            }
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return comID;
    }

    public List<AddDataISDNEntity> getListTrans(Long userID, Long service_id, Long service_idGroup) throws Exception {
        List<AddDataISDNEntity> listReturn = new ArrayList<>();
        String strSQL = "";
        try {
            open();

            if (service_id != null && service_idGroup != null) {
                strSQL = "select a.transaction_id,a.request_time,a.plan_time,a.amount,a.status,b.com_name," +
                        " a.description,b.user_id,a.service_id,d.service_name,a.data_amount,d.service_code,a.com_id,b.vas_mobile\n" +
                        "                    from dsp_transaction a ,dsp_company b,dsp_service d where a.com_id = b.com_id\n" +
                        "                    and b.com_id in " +
                        " (SELECT com_id FROM dsp_company WHERE status = '1' CONNECT BY PRIOR com_id = parent_id" +
                        " START WITH com_id in (select com_id\n" +
                        "                                         from dsp_company\n" +
                        "                                         where user_id = ?\n" +
                        "                                            or group_id in (select group_id from AM_GROUP_USER where user_id = ?)\n" +
                        "                   ))" +
                        " and a.service_id in(?,?) and d.service_id = a.service_id ";
                strSQL += "ORDER BY a.plan_time DESC";
            } else {
                strSQL = "select a.transaction_id,a.request_time,a.plan_time,a.amount,a.status,b.com_name," +
                        " a.description,b.user_id,a.service_id,d.service_name,a.data_amount,d.service_code,a.com_id,b.vas_mobile\n" +
                        "                    from dsp_transaction a ,dsp_company b,dsp_service d where a.com_id = b.com_id\n" +
                        "                    and b.com_id in " +
                        " (SELECT com_id FROM dsp_company WHERE status = '1' CONNECT BY PRIOR com_id = parent_id" +
                        " START WITH com_id in (select com_id\n" +
                        "                                         from dsp_company\n" +
                        "                                         where user_id = ?\n" +
                        "                                            or group_id in (select group_id from AM_GROUP_USER where user_id = ?)\n" +
                        "                   ))" +
                        " and a.service_id in(?) and d.service_id = a.service_id ";
                strSQL += "ORDER BY a.plan_time DESC";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userID);
            mStmt.setLong(2, userID);
            if (service_id != null && service_idGroup != null) {
                mStmt.setLong(3, service_id);
                mStmt.setLong(4, service_idGroup);
            } else if (service_id == null && service_idGroup != null) {
                mStmt.setLong(3, service_idGroup);
            } else if (service_id != null && service_idGroup == null) {
                mStmt.setLong(3, service_id);
            }

            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                AddDataISDNEntity transEntity = new AddDataISDNEntity();
                transEntity.setTransactionID(mRs.getLong(1));
                transEntity.setRequestTime(mRs.getTimestamp(2));
                transEntity.setPlanTime(mRs.getTimestamp(3));
                transEntity.setAmount(mRs.getLong(4));
                transEntity.setStatus(mRs.getString(5));
                transEntity.setComName(mRs.getString(6));
                transEntity.setDescription(mRs.getString(7));
                transEntity.setUserID(mRs.getLong(8));
                transEntity.setServiceID(mRs.getLong(9));
                transEntity.setServiceName(mRs.getString(10));
                transEntity.setDataAmount(mRs.getLong(11));
                transEntity.setServiceCode(mRs.getString(12));
                transEntity.setComID(mRs.getLong(13));
                transEntity.setVasMobile(mRs.getString(14));
                listReturn.add(transEntity);
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

    public List<AddDataDDRequestEntity> getLsDDrequest(Long transactionID) throws Exception {
        List<AddDataDDRequestEntity> listReturn = new ArrayList<>();

        try {
            open();
            String strSQL = "select a.isdn,a.amount/1024,a.active_days,a.request_time,a.req_cost from dsp_dd_detail a where a.transaction_id = ?";
            strSQL += "ORDER BY a.isdn";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, transactionID);

            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                AddDataDDRequestEntity Entity = new AddDataDDRequestEntity();
                Entity.setISDN(mRs.getLong(1));
                Entity.setAmount(mRs.getLong(2));
                Entity.setActiveDay(mRs.getLong(3));
                Entity.setRequestTime(mRs.getTimestamp(4));
                Entity.setReqCost(mRs.getLong(5));

                listReturn.add(Entity);
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

    public List<AddDataDDRequestEntity> getListPackageData(Long lTransactionID) throws Exception {
        List<AddDataDDRequestEntity> listReturn = new ArrayList<>();

        try {
            open();
            String strSQL = "SELECT DISTINCT a.active_days,a.amount\n" +
                    "from dsp_dd_detail a where a.transaction_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, lTransactionID);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                AddDataDDRequestEntity addDataDDRequestEntity = new AddDataDDRequestEntity();
                addDataDDRequestEntity.setActiveDay(mRs.getLong(1));
                addDataDDRequestEntity.setAmount(mRs.getLong(2));
                listReturn.add(addDataDDRequestEntity);
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

    public List<AddDataISDNEntity> getListDdDetailBAK(Long lTransactionID) throws Exception {
        List<AddDataISDNEntity> returnVal = new ArrayList<>();
        try {
            open();
            String strSQL = "select transaction_id, isdn, amount, active_days,request_time," +
                    "group_name,req_cost from dsp_dd_detail where transaction_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, lTransactionID);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                AddDataISDNEntity transEntity = new AddDataISDNEntity();
                transEntity.setTransactionID(mRs.getLong(1));
                transEntity.setISDN(mRs.getString(2));
                transEntity.setAmountRequest(mRs.getLong(3));
                transEntity.setActiveDay(mRs.getLong(4));
                transEntity.setRequestTime(mRs.getTimestamp(5));
                transEntity.setGroup(mRs.getString(6));
                transEntity.setReqCost(mRs.getLong(7));
                returnVal.add(transEntity);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return returnVal;
    }

    public HashMap<String, String> putHeadercolumn() {
        HashMap<String, String> hsReturn = new HashMap<>();
        hsReturn.put("ISDN", ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "ISDN"));
        hsReturn.put("ACTIVE_DAYS", ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "active_day"));
        hsReturn.put("A.AMOUNT/1024", ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "amount_package") + " (MB)");
        hsReturn.put("processTime".toUpperCase(), ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "process_time"));
        return hsReturn;
    }

    public String getListSuccess(Long lTransactionID) throws Exception {
        String strReturn = null;
        String strHeader = ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_name_success");
        String strFileName = "DanhSachXuliThanhCong";
        try {
            open();
            String strSQL = "SELECT a.isdn,a.active_days,a.amount/1024,to_char(a.process_time, 'YYYY-MM-DD HH24:MI:SS') as processTime \n" +
                    "                        from dsp_dd_history a \n" +
                    "                        where a.status = '1' and a.transaction_id = ?" +
                    "                        order by a.process_time desc";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, String.valueOf(lTransactionID));
            mRs = mStmt.executeQuery();

            HashMap<String, String> hsReturn = new HashMap<>();
            hsReturn = putHeadercolumn();
            strReturn = ExportExcelUtil.exportExcel(mRs, strFileName, strHeader, null, hsReturn);

        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }

        return strReturn;
    }

    public String getListFail(Long lTransactionID) throws Exception {
        String strReturn = null;
        String strHeader = ResourceBundleUtil.getCTObjectAsString("PP_ADDDATAISDN", "file_name_fail");
        String strFileName = "DanhSachXuliThatBai";
        try {
            open();
            String strSQL = "SELECT a.isdn,a.active_days,a.amount/1024,to_char(a.process_time, 'YYYY-MM-DD HH24:MI:SS') as processTime \n" +
                    "                        from dsp_dd_history a \n" +
                    "                        where a.status = '3' and a.transaction_id = ?" +
                    "                        order by a.process_time desc";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, String.valueOf(lTransactionID));
            mRs = mStmt.executeQuery();

            HashMap<String, String> hsReturn = new HashMap<>();
            hsReturn = putHeadercolumn();
            strReturn = ExportExcelUtil.exportExcel(mRs, strFileName, strHeader, null, hsReturn);

        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }

        return strReturn;
    }

    public Long insertGroup(Long lTab_id, Long lService_id, Long lDataAmount, String description,
                            List<AddDataISDNEntity> lsData, Long amountData, List<DSPOrder> lsDspOrder,
                            List<DspOrderTransactionEntity> lsDspOrderTransaction) throws Exception {
        PreparedStatement stmtTran = null;
        PreparedStatement stmtRequest = null;
        PreparedStatement stmtUpdateOrder = null;
        PreparedStatement stmtInsertOrderTransaction = null;
        Long id = null;
        String strSqlTran = "insert into dsp_transaction(transaction_id, request_time, plan_time,amount,status,user_id,description,service_id,com_id,tab_id) " +
                "values(?, ? ,?, ?,?,?,?,?,?,?)";
        String strSqlTranGroup = "insert into dsp_transaction(transaction_id, request_time, plan_time,amount,status,user_id,description,service_id,com_id,data_amount) " +
                "values(?, ? ,?, ?,?,?,?,?,?,?)";
        String strSqlRequest = "insert into dsp_dd_detail(transaction_id,isdn,amount,status,active_days,request_time,req_cost) " +
                "values(?,?,?,?,?,?,?)";
        String strSqlRequestGroup = "insert into dsp_dd_detail(transaction_id,isdn,amount,status,active_days,request_time,group_name) " +
                "values(?,?,?,?,?,?,?)";
        String strSqlUpdateOrder = "update dsp_order set remain_value = ?, reserved_value = ? where order_id = ?";
        String strSqlInsertOrderTransaction = "insert into dsp_order_transaction(transaction_id, order_id, issue_time, description,user_id,amount) " +
                "values(?, ? ,?, ?,?,?)";
        try {
            Long com_id = checkComID(lsData.get(0).getUserID());
            open();
            mConnection.setAutoCommit(false);
            id = SQLUtil.getSequenceValue(mConnection, "dsp_transaction_seq");
            if (amountData != null && amountData != 0) {
                stmtTran = mConnection.prepareStatement(strSqlTranGroup);
                int i = 1;
                stmtTran.setLong(i++, id);
                stmtTran.setTimestamp(i++, DateUtil.getSqlTimestamp(lsData.get(0).getRequestTime()));
                stmtTran.setTimestamp(i++, DateUtil.getSqlTimestamp(lsData.get(0).getPlanTime()));
                stmtTran.setDouble(i++, lDataAmount);
                stmtTran.setString(i++, "0");
                stmtTran.setLong(i++, lsData.get(0).getUserID());
                stmtTran.setString(i++, description);
                stmtTran.setLong(i++, lService_id);
                stmtTran.setLong(i++, com_id);
                if (amountData != null && amountData != 0) {
                    stmtTran.setLong(i, amountData * 1024 * 1024);
                }
                stmtTran.executeUpdate();

                stmtRequest = mConnection.prepareStatement(strSqlRequestGroup);
                for (AddDataISDNEntity entRequest : lsData) {
                    int j = 1;
                    stmtRequest.setLong(j++, id);
                    stmtRequest.setString(j++, entRequest.getISDN());
                    stmtRequest.setDouble(j++, entRequest.getAmountRequest());
                    stmtRequest.setString(j++, "0");
                    stmtRequest.setLong(j++, entRequest.getActiveDay());
                    stmtRequest.setTimestamp(j++, DateUtil.getSqlTimestamp(entRequest.getRequestTime()));
                    stmtRequest.setLong(j, id);
                    stmtRequest.addBatch();
                }
                stmtRequest.executeBatch();
            } else {
                stmtTran = mConnection.prepareStatement(strSqlTran);
                int i = 1;
                stmtTran.setLong(i++, id);
                stmtTran.setTimestamp(i++, DateUtil.getSqlTimestamp(lsData.get(0).getRequestTime()));
                stmtTran.setTimestamp(i++, DateUtil.getSqlTimestamp(lsData.get(0).getPlanTime()));
                stmtTran.setDouble(i++, lDataAmount);
                stmtTran.setString(i++, "0");
                stmtTran.setLong(i++, lsData.get(0).getUserID());
                stmtTran.setString(i++, description);
                stmtTran.setLong(i++, lService_id);
                stmtTran.setLong(i++, com_id);
                stmtTran.setLong(i, lTab_id);
                stmtTran.executeUpdate();

                stmtRequest = mConnection.prepareStatement(strSqlRequest);
                for (AddDataISDNEntity entRequest : lsData) {
                    int j = 1;
                    stmtRequest.setLong(j++, id);
                    stmtRequest.setString(j++, entRequest.getISDN());
                    stmtRequest.setDouble(j++, entRequest.getAmountRequest());
                    stmtRequest.setString(j++, "0");
                    stmtRequest.setLong(j++, entRequest.getActiveDay());
                    stmtRequest.setTimestamp(j++, DateUtil.getSqlTimestamp(entRequest.getRequestTime()));
                    stmtRequest.setLong(j, entRequest.getReqCost());
                    stmtRequest.addBatch();
                }
                stmtRequest.executeBatch();
            }


            stmtUpdateOrder = mConnection.prepareStatement(strSqlUpdateOrder);
            for (DSPOrder dspOrder : lsDspOrder) {
                int k = 1;
                stmtUpdateOrder.setLong(k++, dspOrder.getRemainValue());
                stmtUpdateOrder.setLong(k++, dspOrder.getReservedValue());
                stmtUpdateOrder.setLong(k, dspOrder.getOrderId());
                stmtUpdateOrder.addBatch();
            }
            stmtUpdateOrder.executeBatch();

            stmtInsertOrderTransaction = mConnection.prepareStatement(strSqlInsertOrderTransaction);
            for (DspOrderTransactionEntity dspOrderTransactionEntity : lsDspOrderTransaction) {
                int e = 1;
                stmtInsertOrderTransaction.setLong(e++, id);
                stmtInsertOrderTransaction.setLong(e++, dspOrderTransactionEntity.getOderID());
                stmtInsertOrderTransaction.setTimestamp(e++, DateUtil.getSqlTimestamp(dspOrderTransactionEntity.getIssueTime()));
                stmtInsertOrderTransaction.setString(e++, dspOrderTransactionEntity.getDescription());
                stmtInsertOrderTransaction.setLong(e++, dspOrderTransactionEntity.getUserId());
                stmtInsertOrderTransaction.setLong(e, dspOrderTransactionEntity.getAmount());
                stmtInsertOrderTransaction.addBatch();
            }
            stmtInsertOrderTransaction.executeBatch();

            mConnection.commit();

        } catch (Exception ex) {
            mConnection.rollback();
            Logger.getLogger(AddDataISDNModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmtTran);
            close(stmtRequest);
            close(stmtUpdateOrder);
            close(stmtInsertOrderTransaction);
            close();
        }
        return id;
    }

    public void updateStatus(AddDataISDNEntity addDataISDNEntity, List<DSPOrder> lsOrderUpdate) throws Exception {
        PreparedStatement stmt = null;
        String sqlUpdate = "update dsp_transaction set status = ? where transaction_id = ?";
        try {
            open();
            mConnection.setAutoCommit(false);
            stmt = mConnection.prepareStatement(sqlUpdate);
            stmt.setString(1, addDataISDNEntity.getStatus());
            stmt.setLong(2, addDataISDNEntity.getTransactionID());
            stmt.executeUpdate();
            if (lsOrderUpdate != null) {
                String sqlUpdate1 = "update dsp_order set remain_value = ?, reserved_value = ? where order_id = ?";
                stmt = mConnection.prepareStatement(sqlUpdate1);
                for (DSPOrder dspOrder : lsOrderUpdate) {
                    int i = 1;
                    stmt.setLong(i++, dspOrder.getRemainValue());
                    stmt.setLong(i++, dspOrder.getReservedValue());
                    stmt.setLong(i, dspOrder.getOrderId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            if (addDataISDNEntity.getStatus().equals("6")) {
                String sqlInsert = "insert into dsp_cps_queue(transaction_id,vas_mobile,request_time,status,amount) values(?,?,?,?,?)";
                stmt = mConnection.prepareStatement(sqlInsert);
                stmt.setLong(1, addDataISDNEntity.getTransactionID());
                stmt.setString(2, addDataISDNEntity.getVasMobile());
                stmt.setTimestamp(3, DateUtil.getSqlTimestamp(new Date()));
                stmt.setLong(4, 6L);
                stmt.setLong(5, addDataISDNEntity.getAmount());
                stmt.executeUpdate();
            }
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            Logger.getLogger(AddDataISDNModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmt);
            close();
        }
    }

    public void updateStatusApprove(AddDataISDNEntity addDataISDNEntity, List<AddDataDDRequestEntity> lsDetail) throws Exception {
        PreparedStatement stmt = null;
        String sqlUpdate = "update dsp_transaction set status = ? where transaction_id = ?";
        String sqlInsert = "insert into dsp_dd_request(transaction_id,isdn,amount,status,active_days,request_time,process_time,request_id,req_cost) " +
                "values(?,?,?,?,?,?,?,?,?)";
        String sqlInsertGroup = "insert into dsp_dd_request(transaction_id,isdn,amount,status,active_days,request_time,process_time,group_name) " +
                "values(?,?,?,?,?,?,?,?)";
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(addDataISDNEntity.getRequestTime());
//        Date fromDAte = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, +3);
        Date toDate = calendar.getTime();
        try {
            open();
            mConnection.setAutoCommit(false);
            stmt = mConnection.prepareStatement(sqlUpdate);
            stmt.setString(1, addDataISDNEntity.getStatus());
            stmt.setLong(2, addDataISDNEntity.getTransactionID());
            stmt.executeUpdate();

            if (addDataISDNEntity.getServiceCode().equals(DATA_GROUP_SERVICE)) {
                stmt = mConnection.prepareStatement(sqlInsertGroup);
                for (AddDataDDRequestEntity entRequest : lsDetail) {
                    int j = 1;
                    stmt.setLong(j++, addDataISDNEntity.getTransactionID());
                    stmt.setLong(j++, entRequest.getISDN());
                    stmt.setDouble(j++, entRequest.getAmount());
                    stmt.setString(j++, "0");
                    stmt.setLong(j++, entRequest.getActiveDay());
                    stmt.setTimestamp(j++, DateUtil.getSqlTimestamp(entRequest.getRequestTime()));
                    stmt.setTimestamp(j++, DateUtil.getSqlTimestamp(toDate));
                    stmt.setLong(j, addDataISDNEntity.getTransactionID());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            } else {
                stmt = mConnection.prepareStatement(sqlInsert);
                for (AddDataDDRequestEntity entRequest : lsDetail) {
                    int j = 1;
                    stmt.setLong(j++, addDataISDNEntity.getTransactionID());
                    stmt.setLong(j++, entRequest.getISDN());
                    stmt.setDouble(j++, entRequest.getAmount() * 1024);
                    stmt.setString(j++, "0");
                    stmt.setLong(j++, entRequest.getActiveDay());
                    stmt.setTimestamp(j++, DateUtil.getSqlTimestamp(entRequest.getRequestTime()));
                    stmt.setTimestamp(j++, DateUtil.getSqlTimestamp(toDate));
                    stmt.setLong(j++, SQLUtil.getSequenceValue(mConnection, "seq_dd_request"));
                    stmt.setLong(j, entRequest.getReqCost());

                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
//            if (("5").equals(addDataISDNEntity.getStatus())) {
//                //send SMS
//                if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
//                        && !"".equals(StringUtil.fix(addDataISDNEntity.getVasMobile()))) {
//                    String[] params = new String[1];
//                    params[0] = addDataISDNEntity.getTransactionID().toString();
//                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
//                    dspMtQueueModel.addSMS(SMS_REJECT, addDataISDNEntity.getVasMobile(), params, mConnection);
//                }
//            } else if (("2").equals(addDataISDNEntity.getStatus())) {
//                if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
//                        && !"".equals(StringUtil.fix(addDataISDNEntity.getVasMobile()))) {
//                    String[] params = new String[1];
//                    params[0] = addDataISDNEntity.getTransactionID().toString();
//                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
//                    dspMtQueueModel.addSMS(SMS_APPROVAL, addDataISDNEntity.getVasMobile(), params, mConnection);
//                }
//            }

            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            Logger.getLogger(AddDataISDNModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmt);
            close();
        }
    }

    public List<DSPComPrice> getListComPrice(Long comID) throws Exception {
        List<DSPComPrice> lsReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select distinct (a.tab_id)" +
                    " from dsp_com_price a where a.status = 1 and a.com_id = ?" +
                    " and ((applied_date is not null and removed_date is not null and applied_date <= sysdate and removed_date >= sysdate)" +
                    " or (applied_date is not null and removed_date is null and applied_date <= sysdate)" +
                    " or (applied_date is null and removed_date is not null and removed_date >= sysdate)" +
                    " or (applied_date is null and removed_date is null))";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comID);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPComPrice dspComPrice = new DSPComPrice();
                dspComPrice.setTabId(mRs.getLong(1));
                lsReturn.add(dspComPrice);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return lsReturn;
    }

    public List<DSPServicePriceTab> getListServicePriceTab(List<Long> lsTabID, Long serviceID, Long Default) throws Exception {
        List<DSPServicePriceTab> lsReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select a.service_id,a.tab_id,a.status," +
                    " a.description,a.currency,a.def,a.start_time," +
                    " a.end_time,a.file_path,a.block" +
                    " from dsp_service_price_tab a" +
                    " where a.status = 1 and a.service_id = ? and a.def = ? ";
            if (Default == 0) {
                strSQL += " and a.tab_id ";
                strSQL += getQueryIn(lsTabID);
            }
            strSQL += " and ((start_time is not null and end_time is not null and start_time <= sysdate and end_time + 1 > SYSDATE)" +
                    " or (start_time is not null and end_time is null and start_time <= sysdate)" +
                    " or (start_time is null and end_time is not null and end_time + 1 > SYSDATE)" +
                    " or (start_time is null and end_time is null)) ORDER BY a.start_time DESC";
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 1;
            mStmt.setLong(i++, serviceID);
            mStmt.setLong(i++, Default);
            if (Default == 0) {
                for (Long values : lsTabID) {
                    mStmt.setLong(i++, values);
                }
            }
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPServicePriceTab dspServicePriceTab = new DSPServicePriceTab();
                dspServicePriceTab.setServiceId(mRs.getLong(1));
                dspServicePriceTab.setTabId(mRs.getLong(2));
                dspServicePriceTab.setStatus(mRs.getString(3));
                dspServicePriceTab.setDescription(mRs.getString(4));
                dspServicePriceTab.setCurrency(mRs.getString(5));
                dspServicePriceTab.setDefaultValue(mRs.getInt(6));
                dspServicePriceTab.setStartTime(mRs.getDate(7));
                dspServicePriceTab.setEndTime(mRs.getDate(8));
                dspServicePriceTab.setFilePath(mRs.getString(9));
                dspServicePriceTab.setBlock(mRs.getLong(10));
                lsReturn.add(dspServicePriceTab);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return lsReturn;
    }

    public List<DSPServicePrice> getPrice(List<Long> tabID) throws Exception {
        List<DSPServicePrice> lsReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select a.price_id,a.tab_id,a.name,a.cap_min,a.cap_max,a.price,a.active_day " +
                    "from DSP_SERVICE_PRICE a where a.tab_id ";
            strSQL += getQueryIn(tabID);
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 1;
            for (Long values : tabID) {
                mStmt.setLong(i++, values);
            }
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPServicePrice dspServicePrice = new DSPServicePrice();
                dspServicePrice.setPriceId(mRs.getLong(1));
                dspServicePrice.setTabId(mRs.getLong(2));
                dspServicePrice.setName(mRs.getString(3));
                dspServicePrice.setCapMin(mRs.getLong(4));
                dspServicePrice.setCapMax(mRs.getLong(5));
                dspServicePrice.setPrice(mRs.getLong(6));
                dspServicePrice.setActiveDay(mRs.getLong(7));
                lsReturn.add(dspServicePrice);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return lsReturn;
    }

    public void updateInfoGroup(List<DSPOrder> listDspOrderChange, List<DspOrderTransactionEntity> lsDataOrderTransaction, Long comID, Long serviceId, Long transaction_id, String status, Long lDataAmount, String description,
                                List<AddDataISDNEntity> lsData, Long data_amount) throws Exception {
        PreparedStatement stmt = null;

        String sqlUpdatePlus = "update dsp_transaction set request_time = ?, plan_time = ?, amount = ?,status = ?,description = ?,data_amount = ? where transaction_id = ?";
        String sqlDelete = "delete from dsp_dd_detail where transaction_id = ?";
        String sqlInsert = "insert into dsp_dd_detail(transaction_id,isdn,amount,status,active_days,request_time,group_name) " +
                "values(?,?,?,?,?,?,?)";
        String strSqlUpdateOrder = "update dsp_order set remain_value = ?, reserved_value = ? where order_id = ?";
        String strSqlDeleteOrderTransaction = "delete dsp_order_transaction where order_id = ? and transaction_id = ?";
        String strSqlInsertOrderTransaction = "insert into dsp_order_transaction(transaction_id, order_id, issue_time, description,user_id,amount) " +
                "values(?, ? ,?, ?,?,?)";

        try {
            open();
            mConnection.setAutoCommit(false);

            int i = 1;
            if (lsData.size() > 0) {
                stmt = mConnection.prepareStatement(sqlUpdatePlus);
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(lsData.get(0).getRequestTime()));
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(lsData.get(0).getPlanTime()));
                stmt.setDouble(i++, lDataAmount);
                if (status.equals("0")) {
                    stmt.setString(i++, "0");
                } else {
                    stmt.setString(i++, "1");
                }
                stmt.setString(i++, description);
                stmt.setLong(i++, data_amount * 1024 * 1024);
                stmt.setLong(i, transaction_id);
                stmt.executeUpdate();

                stmt = mConnection.prepareStatement(sqlDelete);
                stmt.setLong(1, transaction_id);
                stmt.executeUpdate();

                stmt = mConnection.prepareStatement(sqlInsert);
                for (AddDataISDNEntity entRequest : lsData) {
                    int j = 1;
                    stmt.setLong(j++, transaction_id);
                    stmt.setString(j++, entRequest.getISDN());
                    stmt.setDouble(j++, entRequest.getAmountRequest());
                    stmt.setString(j++, "0");
                    stmt.setLong(j++, entRequest.getActiveDay());
                    stmt.setTimestamp(j++, DateUtil.getSqlTimestamp(entRequest.getRequestTime()));
                    stmt.setLong(j, transaction_id);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            } else {
                stmt = mConnection.prepareStatement(sqlUpdatePlus);
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(new Date()));
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(new Date()));
                stmt.setLong(i++, lDataAmount);
                if (status.equals("0")) {
                    stmt.setString(i++, "0");
                } else {
                    stmt.setString(i++, "1");
                }
                stmt.setString(i++, description);
                stmt.setLong(i++, data_amount * 1024 * 1024);
                stmt.setLong(i, transaction_id);
                stmt.executeUpdate();
            }

            stmt = mConnection.prepareStatement(strSqlUpdateOrder);
            for (DSPOrder dspOrder : listDspOrderChange) {
                stmt.setLong(1, dspOrder.getReservedValue());
                stmt.setLong(2, dspOrder.getOrderId());
                stmt.addBatch();
            }
            stmt.executeBatch();

            stmt = mConnection.prepareStatement(strSqlDeleteOrderTransaction);
            for (DspOrderTransactionEntity dspOrderTransaction : lsDataOrderTransaction) {
                stmt.setLong(1, dspOrderTransaction.getOderID());
                stmt.setLong(2, transaction_id);
                stmt.addBatch();
            }
            stmt.executeBatch();

            stmt = mConnection.prepareStatement(strSqlInsertOrderTransaction);
            for (DspOrderTransactionEntity dspOrderTransactionEntity : lsDataOrderTransaction) {
                int e = 1;
                stmt.setLong(e++, transaction_id);
                stmt.setLong(e++, dspOrderTransactionEntity.getOderID());
                stmt.setTimestamp(e++, DateUtil.getSqlTimestamp(dspOrderTransactionEntity.getIssueTime()));
                stmt.setString(e++, dspOrderTransactionEntity.getDescription());
                stmt.setLong(e++, dspOrderTransactionEntity.getUserId());
                stmt.setLong(e, dspOrderTransactionEntity.getAmount());
                stmt.addBatch();
            }
            stmt.executeBatch();
            mConnection.commit();

        } catch (Exception ex) {
            mConnection.rollback();
            Logger.getLogger(AddDataISDNModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmt);
            close();
        }
    }

    public void updateInfo(List<DSPOrder> listDspOrderChange, List<DspOrderTransactionEntity> lsDataOrderTransaction,
                           AddDataISDNEntity addDataISDNEntity, Long lDataAmount, String description,
                           List<AddDataISDNEntity> lsData) throws Exception {
        PreparedStatement stmt = null;

        String sqlUpdate = "update dsp_transaction set request_time = ?, plan_time = ?, amount = ?,status = ?,description = ? where transaction_id = ?";
        String sqlUpdate1 = "update dsp_transaction set request_time = ?, plan_time = ?, status = ?,description = ? where transaction_id = ?";
        String sqlDelete = "delete from dsp_dd_detail where transaction_id = ?";
        String sqlInsert = "insert into dsp_dd_detail(transaction_id,isdn,amount,status,active_days,request_time,req_cost) " +
                "values(?,?,?,?,?,?,?)";
        String strSqlUpdateOrder = "UPDATE dsp_order SET reserved_value = reserved_value + ? WHERE order_id = ?";
        String strSqlDeleteOrderTransaction = "delete dsp_order_transaction where order_id = ? and transaction_id = ?";
        String strSqlInsertOrderTransaction = "insert into dsp_order_transaction(transaction_id, order_id, issue_time, description,user_id,amount) " +
                "values(?, ? ,?, ?,?,?)";


        try {
            open();
            mConnection.setAutoCommit(false);

            int i = 1;
            if (lsData.size() > 0) {
                stmt = mConnection.prepareStatement(sqlUpdate);
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(addDataISDNEntity.getRequestTime()));
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(addDataISDNEntity.getPlanTime()));
                stmt.setLong(i++, lDataAmount);
                if (addDataISDNEntity.getStatus().equals("0")) {
                    stmt.setString(i++, "0");
                } else {
                    stmt.setString(i++, "1");
                }
                stmt.setString(i++, description);
                stmt.setLong(i, addDataISDNEntity.getTransactionID());
                stmt.executeUpdate();

                stmt = mConnection.prepareStatement(sqlDelete);
                stmt.setLong(1, addDataISDNEntity.getTransactionID());
                stmt.executeUpdate();

                stmt = mConnection.prepareStatement(sqlInsert);
                for (AddDataISDNEntity entRequest : lsData) {
                    int j = 1;
                    stmt.setLong(j++, addDataISDNEntity.getTransactionID());
                    stmt.setString(j++, entRequest.getISDN());
                    stmt.setDouble(j++, entRequest.getAmountRequest());
                    stmt.setString(j++, "0");
                    stmt.setLong(j++, entRequest.getActiveDay());
                    stmt.setTimestamp(j++, DateUtil.getSqlTimestamp(addDataISDNEntity.getRequestTime()));
                    stmt.setLong(j, entRequest.getReqCost());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            } else {
                stmt = mConnection.prepareStatement(sqlUpdate1);
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(addDataISDNEntity.getRequestTime()));
                stmt.setTimestamp(i++, DateUtil.getSqlTimestamp(addDataISDNEntity.getPlanTime()));
                if (addDataISDNEntity.getStatus().equals("0")) {
                    stmt.setString(i++, "0");
                } else {
                    stmt.setString(i++, "1");
                }
                stmt.setString(i++, description);
                stmt.setLong(i, addDataISDNEntity.getTransactionID());
                stmt.executeUpdate();
            }
            stmt = mConnection.prepareStatement(strSqlUpdateOrder);
            for (DSPOrder dspOrder : listDspOrderChange) {
                stmt.setLong(1, dspOrder.getReservedValue());
                stmt.setLong(2, dspOrder.getOrderId());
                stmt.addBatch();
            }
            stmt.executeBatch();

            stmt = mConnection.prepareStatement(strSqlDeleteOrderTransaction);
            for (DspOrderTransactionEntity dspOrderTransaction : lsDataOrderTransaction) {
                stmt.setLong(1, dspOrderTransaction.getOderID());
                stmt.setLong(2, addDataISDNEntity.getTransactionID());
                stmt.addBatch();
            }
            stmt.executeBatch();

            stmt = mConnection.prepareStatement(strSqlInsertOrderTransaction);
            for (DspOrderTransactionEntity dspOrderTransactionEntity : lsDataOrderTransaction) {
                int e = 1;
                stmt.setLong(e++, addDataISDNEntity.getTransactionID());
                stmt.setLong(e++, dspOrderTransactionEntity.getOderID());
                stmt.setTimestamp(e++, DateUtil.getSqlTimestamp(dspOrderTransactionEntity.getIssueTime()));
                stmt.setString(e++, dspOrderTransactionEntity.getDescription());
                stmt.setLong(e++, dspOrderTransactionEntity.getUserId());
                stmt.setLong(e, dspOrderTransactionEntity.getAmount());
                stmt.addBatch();
            }
            stmt.executeBatch();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            Logger.getLogger(AddDataISDNModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmt);
            close();
        }
    }

    public Long getAmountOrderTrans(Long orderID, Long transactionID) throws Exception {
        Long AmountOrderTrans = null;
        try {
            open();
            String strSQL = "select a.amount from dsp_order_transaction a where a.order_id = ? and a.transaction_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, orderID);
            mStmt.setLong(2, transactionID);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                AmountOrderTrans = mRs.getLong(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return AmountOrderTrans;
    }
}
