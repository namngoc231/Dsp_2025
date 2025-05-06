package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.DateUtil;
import vn.com.telsoft.entity.*;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataCodeModel extends AMDataPreprocessor implements Serializable {

    static final String SMS_APPROVAL_TRANSACTION = "APPROVAL_TRANSACTION";
    static final String SMS_REJECT_TRANSACTION = "REJECT_TRANSACTION";

    public DSPCompany getUserCompany(long userId) throws Exception {
        try {
            open();
            String strSQL = "SELECT a.com_id, a.com_name, a.type, a.tax_code, a.bus_code, a.status, a.vas_mobile, a.updated_key" +
                    "   FROM dsp_company a " +
                    "   WHERE a.status = '1' " +
                    "       AND (a.user_id = ? or a.group_id in (select group_id from AM_GROUP_USER where user_id = ?))" +
                    "   ORDER BY a.com_name ASC";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPCompany tmpDSPCompany = new DSPCompany();
                tmpDSPCompany.setComId(mRs.getLong(1));
                tmpDSPCompany.setComName(mRs.getString(2));
                tmpDSPCompany.setType(mRs.getLong(3));
                tmpDSPCompany.setTaxCode(mRs.getString(4));
                tmpDSPCompany.setBusCode(mRs.getString(5));
                tmpDSPCompany.setStatus(mRs.getString(6));
                tmpDSPCompany.setVasMobile(mRs.getString(7));
                tmpDSPCompany.setUpdatedKey(mRs.getTimestamp(8));
                return tmpDSPCompany;
            }

        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return null;
    }

    public List<DSPTransaction> getlistDataCode(Long serviceId, Long userId) throws Exception {
        List<DSPTransaction> listDataCode = new ArrayList<DSPTransaction>();
        try {
            open();
            String strSQL = "SELECT   a.transaction_id, "
                    + "               a.request_time, "
                    + "               a.plan_time, "
                    + "               a.amount, "
                    + "               a.status, "
                    + "               a.description, "
                    + "               a.service_id, "
                    + "               a.com_id, "
                    + "               a.tab_id, "
                    + "               a.res_order_id, "
                    + "               b.user_id, "
                    + "               b.com_name,"
                    + "               b.vas_mobile,"
                    + "               (select d.user_name from am_user d where a.user_id = d.user_id) as user_name"
                    + "      FROM   dsp_transaction a, dsp_company b, dsp_service c, dsp_service_price_tab d "
                    + "      where a.com_id = b.com_id and a.tab_id = d.tab_id and b.com_id in"
                    + "      (SELECT com_id FROM dsp_company " +
                    "           WHERE status = '1' " +
                    "           CONNECT BY PRIOR com_id = parent_id START WITH com_id in (select com_id " +
                    "                                         from dsp_company " +
                    "                                         where user_id = ? " +
                    "                                            or group_id in (select group_id from AM_GROUP_USER where user_id = ?) " +
                    "                   ))"
                    + "      and a.service_id = ? and a.service_id = c.service_id"
                    + "      ORDER BY a.status, a.plan_time ASC ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mStmt.setLong(3, serviceId);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPTransaction tmpDSPTransaction = new DSPTransaction();
                tmpDSPTransaction.setTransactionId(mRs.getLong("transaction_id"));
                tmpDSPTransaction.setRequestTime(mRs.getTimestamp("request_time"));
                tmpDSPTransaction.setPlanTime(mRs.getTimestamp("plan_time"));
                tmpDSPTransaction.setAmount(mRs.getLong("amount"));
                tmpDSPTransaction.setStatus(mRs.getString("status"));
                tmpDSPTransaction.setDescription(mRs.getString("description"));
                tmpDSPTransaction.setServiceId(mRs.getLong("service_id"));
                tmpDSPTransaction.setComId(mRs.getLong("com_id"));
                tmpDSPTransaction.setTabId(mRs.getLong("tab_id"));
                tmpDSPTransaction.setResOrderId(mRs.getString("res_order_id"));
                tmpDSPTransaction.setUserId(mRs.getLong("user_id"));
                tmpDSPTransaction.setComName(mRs.getString("com_name"));
                tmpDSPTransaction.setComNumber(mRs.getString("vas_mobile"));
                tmpDSPTransaction.setUserName(mRs.getString("user_name"));
                listDataCode.add(tmpDSPTransaction);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listDataCode;
    }

    public List<DSPDcDetail> getlistDcDetail(Long transactionId, Long tabId) throws Exception {
        List<DSPDcDetail> listDcDetail = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.amount, a.card_name, a.transaction_id, c.name, a.request_time, a.status, a.total_cost, a.price_id, c.price"
                    + "      FROM     dsp_dc_detail a, dsp_transaction b, dsp_service_price c, dsp_service_price_tab d"
                    + "      WHERE    a.transaction_id = b.transaction_id and b.service_id = d.service_id and a.price_id = c.price_id and b.tab_id = d.tab_id and b.transaction_id = ? and d.tab_id = ?"
                    + "      ORDER BY c.name";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, transactionId);
            mStmt.setLong(2, tabId);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPDcDetail tmpDSPDcDetail = new DSPDcDetail();
                tmpDSPDcDetail.setAmount(mRs.getLong("amount"));
                tmpDSPDcDetail.setCardName(mRs.getString("name"));
//                tmpDSPDcRequest.setCardTypeName(mRs.getString("name"));
                tmpDSPDcDetail.setRequestTime(mRs.getTimestamp("request_time"));
                tmpDSPDcDetail.setStatus(mRs.getString("status"));
                tmpDSPDcDetail.setTotalCost(mRs.getLong("total_cost"));
                tmpDSPDcDetail.setPriceId(mRs.getLong("price_id"));
                tmpDSPDcDetail.setCardTypePrice(mRs.getLong("price"));
                tmpDSPDcDetail.setTransactionId(mRs.getLong("transaction_id"));
                listDcDetail.add(tmpDSPDcDetail);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listDcDetail;
    }

    public List<DSPServicePrice> getlistCardType(Long serviceId, Long comId) throws Exception {
        PreparedStatement stmtTabId = null;
        PreparedStatement stmtTabIda = null;
        List<DSPServicePrice> listCardType = new ArrayList<>();
        long tabId = 0l;
        boolean bCheck = false;
        try {
            open();
            String strTabId = "SELECT   b.tab_id, b.def"
                    + "      FROM     dsp_service_price_tab b, dsp_com_price c"
                    + "      WHERE    b.service_id = ? and b.tab_id = c.tab_id and b.status = '1' and c.status = '1' and b.def = 0 "
                    + "      and      c.com_id = ?"
                    + "      and (b.start_time is null or b.start_time <= sysdate)"
                    + "      and (b.end_time is null or b.end_time + 1 > sysdate)";

            String strTabIda = "SELECT   b.tab_id, b.def, (sysdate - nvl(b.start_time, TO_DATE('1990-01-01','yyyy-MM-dd'))) as dk1, (nvl(b.end_time, TO_DATE('2100-12-31','yyyy-MM-dd')) - sysdate) as dk2"
                    + "      FROM     dsp_service_price_tab b"
                    + "      WHERE    b.service_id = ? and b.status = '1' and b.def = 1"
                    + "      and (b.start_time is null or b.start_time <= sysdate)"
                    + "      and (b.end_time is null or b.end_time + 1 > sysdate)"
                    + "      order by dk1 asc, dk2 asc";

            stmtTabId = mConnection.prepareStatement(strTabId);
            stmtTabId.setLong(1, serviceId);
            stmtTabId.setLong(2, comId);
            mRs = stmtTabId.executeQuery();

            if (mRs.next()) {
                bCheck = true;
                tabId = mRs.getLong("tab_id");
            }

            if (!bCheck) {
                stmtTabIda = mConnection.prepareStatement(strTabIda);
                stmtTabIda.setLong(1, serviceId);
                mRs = stmtTabIda.executeQuery();
                if (mRs.next()) {
                    tabId = mRs.getLong("tab_id");
                }
            }

            String strSQL = "SELECT   a.price_id, a.name, a.price"
                    + "      FROM     dsp_service_price a"
                    + "      WHERE    a.tab_id = ?"
                    + "      order by a.name";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, tabId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPServicePrice tmpDSPServicePrice = new DSPServicePrice();
                tmpDSPServicePrice.setPriceId(mRs.getLong("price_id"));
                tmpDSPServicePrice.setName(mRs.getString("name"));
                tmpDSPServicePrice.setPrice(mRs.getLong("price"));
                listCardType.add(tmpDSPServicePrice);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(stmtTabIda);
            close(stmtTabId);
            close(mRs);
            close();
        }
        return listCardType;
    }

    public long getTabId(Long serviceId, Long comId) throws Exception {
        PreparedStatement stmtTabId = null;
        PreparedStatement stmtTabIda = null;
        long tabId = 0l;
        boolean bCheck = false;
        try {
            open();
            String strTabId = "SELECT   b.tab_id, b.def"
                    + "      FROM     dsp_service_price_tab b, dsp_com_price c"
                    + "      WHERE    b.service_id = ? and b.tab_id = c.tab_id and b.status = '1' and c.status = '1' and b.def = 0 "
                    + "      and      c.com_id = ?"
                    + "      and (b.start_time is null or b.start_time <= sysdate)"
                    + "      and (b.end_time is null or b.end_time + 1 > sysdate)";

            String strTabIda = "SELECT   b.tab_id, b.def, (sysdate - nvl(b.start_time, TO_DATE('1990-01-01','yyyy-MM-dd'))) as dk1, (nvl(b.end_time, TO_DATE('2100-12-31','yyyy-MM-dd')) - sysdate) as dk2"
                    + "      FROM     dsp_service_price_tab b"
                    + "      WHERE    b.service_id = ? and b.status = '1' and b.def = 1"
                    + "      and (b.start_time is null or b.start_time <= sysdate)"
                    + "      and (b.end_time is null or b.end_time + 1 > sysdate)"
                    + "      order by dk1 asc, dk2 asc";

            stmtTabId = mConnection.prepareStatement(strTabId);
            stmtTabId.setLong(1, serviceId);
            stmtTabId.setLong(2, comId);
            mRs = stmtTabId.executeQuery();

            if (mRs.next()) {
                bCheck = true;
                tabId = mRs.getLong("tab_id");
            }

            if (!bCheck) {
                stmtTabIda = mConnection.prepareStatement(strTabIda);
                stmtTabIda.setLong(1, serviceId);
                mRs = stmtTabIda.executeQuery();
                if (mRs.next()) {
                    tabId = mRs.getLong("tab_id");
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(stmtTabIda);
            close(stmtTabId);
            close(mRs);
            close();
        }
        return tabId;
    }

    public String getVasMoblie(Long comId) throws Exception {
        String mobile = null;
        try {
            open();
            String strSQL = "Select vas_mobile from dsp_company where com_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                mobile = mRs.getString("vas_mobile");
                return mobile;
            }

        } catch (Exception ex) {
            close(mRs);
            close(mStmt);
            close();
        }
        return mobile;
    }

    public void insert(DSPTransaction dspTransaction, List<DSPDcDetail> listDspDcDetail, List<DSPOrderTransaction> listDspOrderTransaction, List<DSPOrder> listDspOrder) throws Exception {
        PreparedStatement stmtDspTransaction = null;
        PreparedStatement stmtDspDcDetail = null;
        PreparedStatement stmtDspOrderTransaction = null;
        PreparedStatement stmtUpdateReserved = null;

        String sqlDspTransaction = "INSERT INTO dsp_transaction(transaction_id, request_time, plan_time, amount, status, description, user_id, service_id, com_id, tab_id) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDspDcDetail = "INSERT INTO dsp_dc_detail(card_name, amount, total_cost, status, request_time, transaction_id, price_id) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        String sqlDspOrderTransaction = "INSERT INTO dsp_order_transaction(transaction_id, order_id, issue_time, description, user_id, amount)"
                + "VALUES(?, ?, ?, ?, ?, ?)";
        String sqlUpdateReserved = "UPDATE dsp_order SET reserved_value = ? WHERE order_id = ?";

        try {
            open();
            mConnection.setAutoCommit(false);
            Long id = SQLUtil.getSequenceValue(mConnection, "DSP_TRANSACTION_SEQ");
            dspTransaction.setTransactionId(id);
            dspTransaction.setComId(checkComId(mConnection, dspTransaction.getUserId()));
            stmtDspTransaction = mConnection.prepareStatement(sqlDspTransaction);
            stmtDspTransaction.setLong(1, id);
            stmtDspTransaction.setTimestamp(2, DateUtil.getSqlTimestamp(dspTransaction.getRequestTime()));
            stmtDspTransaction.setTimestamp(3, DateUtil.getSqlTimestamp(dspTransaction.getPlanTime()));
            stmtDspTransaction.setLong(4, dspTransaction.getAmount());
            stmtDspTransaction.setString(5, dspTransaction.getStatus());
            stmtDspTransaction.setString(6, dspTransaction.getDescription());
            stmtDspTransaction.setLong(7, dspTransaction.getUserId());
            stmtDspTransaction.setLong(8, dspTransaction.getServiceId());
            stmtDspTransaction.setLong(9, dspTransaction.getComId());
            stmtDspTransaction.setLong(10, dspTransaction.getTabId());
            stmtDspTransaction.executeUpdate();

            stmtDspDcDetail = mConnection.prepareStatement(sqlDspDcDetail);
            for (DSPDcDetail dspDcDetail : listDspDcDetail) {
                dspDcDetail.setTransactionId(dspTransaction.getTransactionId());
                stmtDspDcDetail.setString(1, dspDcDetail.getCardName());
                stmtDspDcDetail.setLong(2, dspDcDetail.getAmount());
                stmtDspDcDetail.setLong(3, dspDcDetail.getTotalCost());
                stmtDspDcDetail.setString(4, dspDcDetail.getStatus());
                stmtDspDcDetail.setTimestamp(5, DateUtil.getSqlTimestamp(dspDcDetail.getRequestTime()));
                stmtDspDcDetail.setLong(6, dspDcDetail.getTransactionId());
                stmtDspDcDetail.setLong(7, dspDcDetail.getPriceId());
                stmtDspDcDetail.addBatch();
            }
            stmtDspDcDetail.executeBatch();

            stmtDspOrderTransaction = mConnection.prepareStatement(sqlDspOrderTransaction);
            for (DSPOrderTransaction dspOrderTransaction : listDspOrderTransaction) {
                dspOrderTransaction.setTransactionId(dspTransaction.getTransactionId());
                dspOrderTransaction.setUserId(dspTransaction.getUserId());
                dspOrderTransaction.setIssueTime(dspTransaction.getRequestTime());
                stmtDspOrderTransaction.setLong(1, dspOrderTransaction.getTransactionId());
                stmtDspOrderTransaction.setLong(2, dspOrderTransaction.getOrderId());
                stmtDspOrderTransaction.setTimestamp(3, DateUtil.getSqlTimestamp(dspOrderTransaction.getIssueTime()));
                stmtDspOrderTransaction.setString(4, dspOrderTransaction.getDescription());
                stmtDspOrderTransaction.setLong(5, dspOrderTransaction.getUserId());
                stmtDspOrderTransaction.setLong(6, dspOrderTransaction.getAmount());
                stmtDspOrderTransaction.addBatch();
            }
            stmtDspOrderTransaction.executeBatch();

            stmtUpdateReserved = mConnection.prepareStatement(sqlUpdateReserved);
            for (DSPOrder dspOrder : listDspOrder) {
                stmtUpdateReserved.setLong(1, dspOrder.getReservedValue());
                stmtUpdateReserved.setLong(2, dspOrder.getOrderId());
                stmtUpdateReserved.addBatch();
            }
            stmtUpdateReserved.executeBatch();

            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmtUpdateReserved);
            close(stmtDspOrderTransaction);
            close(stmtDspDcDetail);
            close(stmtDspTransaction);
            close();
        }
    }

    private void addDcDetail(Connection mConnection, DSPTransaction dspTransaction, List<DSPDcDetail> listDspDcDetail) throws Exception {
        PreparedStatement stmtDspDcDetail = null;

        String strSQL = "DELETE FROM dsp_dc_detail WHERE transaction_id = ?";
        String sqlDspDcDetail = "INSERT INTO dsp_dc_detail(card_name, amount, total_cost, status, request_time, transaction_id, price_id)"
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try {
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dspTransaction.getTransactionId());
            mStmt.executeUpdate();

            stmtDspDcDetail = mConnection.prepareStatement(sqlDspDcDetail);
            for (DSPDcDetail dspDcDetail : listDspDcDetail) {
                dspDcDetail.setTransactionId(dspTransaction.getTransactionId());
                stmtDspDcDetail.setString(1, dspDcDetail.getCardName());
                stmtDspDcDetail.setLong(2, dspDcDetail.getAmount());
                stmtDspDcDetail.setLong(3, dspDcDetail.getTotalCost());
                stmtDspDcDetail.setString(4, dspDcDetail.getStatus());
                stmtDspDcDetail.setTimestamp(5, DateUtil.getSqlTimestamp(dspDcDetail.getRequestTime()));
                stmtDspDcDetail.setLong(6, dspDcDetail.getTransactionId());
                stmtDspDcDetail.setLong(7, dspDcDetail.getPriceId());
                stmtDspDcDetail.addBatch();

            }
            stmtDspDcDetail.executeBatch();

        } catch (Exception ex) {
            throw ex;
        } finally {
            close(stmtDspDcDetail);
            close(mStmt);
        }
    }

    public void update(DSPTransaction dspTransaction, List<DSPDcDetail> listDspDcDetail, List<DSPOrderTransaction> listDspOrderTransaction, List<DSPOrder> listDspOrder) throws Exception {
        PreparedStatement stmtDeleteOrderTransaction = null;
        PreparedStatement stmtInsertOrderTransaction = null;
        PreparedStatement stmtUpdateOrder = null;

        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "UPDATE dsp_transaction SET description = ?, amount = ?, request_time = ?, plan_time = ?, status = ?  WHERE transaction_id = ?";
            String strDeleteOrderTransaction = "DELETE from dsp_order_transaction where transaction_id = ?";
            String strInsertOrderTransaction = "INSERT INTO dsp_order_transaction(transaction_id, order_id, issue_time, description, user_id, amount)"
                    + "VALUES(?, ?, ?, ?, ?, ?)";
            String sqlUpdateOrder = "UPDATE dsp_order SET reserved_value = reserved_value + ? WHERE order_id = ?";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, dspTransaction.getDescription());
            mStmt.setLong(2, dspTransaction.getAmount());
            mStmt.setTimestamp(3, DateUtil.getSqlTimestamp(dspTransaction.getRequestTime()));
            mStmt.setTimestamp(4, DateUtil.getSqlTimestamp(dspTransaction.getPlanTime()));
            mStmt.setString(5, dspTransaction.getStatus());
            mStmt.setLong(6, dspTransaction.getTransactionId());
            mStmt.execute();
            addDcDetail(mConnection, dspTransaction, listDspDcDetail);

            stmtDeleteOrderTransaction = mConnection.prepareStatement(strDeleteOrderTransaction);
            for (DSPOrderTransaction dspOrderTransaction : listDspOrderTransaction) {
                stmtDeleteOrderTransaction.setLong(1, dspOrderTransaction.getTransactionId());
                stmtDeleteOrderTransaction.addBatch();
            }
            stmtDeleteOrderTransaction.executeBatch();

            stmtInsertOrderTransaction = mConnection.prepareStatement(strInsertOrderTransaction);
            for (DSPOrderTransaction dspOrderTransaction : listDspOrderTransaction) {
                dspOrderTransaction.setTransactionId(dspTransaction.getTransactionId());
                dspOrderTransaction.setUserId(dspTransaction.getUserId());
                dspOrderTransaction.setIssueTime(dspTransaction.getRequestTime());
                stmtInsertOrderTransaction.setLong(1, dspOrderTransaction.getTransactionId());
                stmtInsertOrderTransaction.setLong(2, dspOrderTransaction.getOrderId());
                stmtInsertOrderTransaction.setTimestamp(3, DateUtil.getSqlTimestamp(dspOrderTransaction.getIssueTime()));
                stmtInsertOrderTransaction.setString(4, dspOrderTransaction.getDescription());
                stmtInsertOrderTransaction.setLong(5, dspOrderTransaction.getUserId());
                stmtInsertOrderTransaction.setLong(6, dspOrderTransaction.getAmount());
                stmtInsertOrderTransaction.addBatch();
            }
            stmtInsertOrderTransaction.executeBatch();

            stmtUpdateOrder = mConnection.prepareStatement(sqlUpdateOrder);
            for (DSPOrder dspOrder : listDspOrder) {
                stmtUpdateOrder.setLong(1, dspOrder.getReservedValue());
                stmtUpdateOrder.setLong(2, dspOrder.getOrderId());
                stmtUpdateOrder.addBatch();
            }
            stmtUpdateOrder.executeBatch();

            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmtUpdateOrder);
            close(stmtInsertOrderTransaction);
            close(stmtDeleteOrderTransaction);
            close(mStmt);
            close();
        }
    }

    public String customFormat(long value) throws Exception {
        String pattern = "###,###.###";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return myFormatter.format(value);
    }

    public void updateStatus(DSPTransaction dspTransaction, List<DSPOrderTransaction> listDspOrderTransaction) throws Exception {
        String sqlUpdate = "update dsp_transaction set status = ? where transaction_id = ?";
        try {
            open();
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(sqlUpdate);
            mStmt.setString(1, dspTransaction.getStatus());
            mStmt.setLong(2, dspTransaction.getTransactionId());
            mStmt.executeUpdate();

            switch (dspTransaction.getStatus()) {
                case "2":
                    copyDcDetailToDcRequest(mConnection, dspTransaction);
                    //send SMS
//                    if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
//                            && !"".equals(StringUtil.fix(dspTransaction.getComNumber()))) {
//                        String[] params = {String.valueOf(dspTransaction.getTransactionId()), String.valueOf(DateUtil.getSqlTimestamp(dspTransaction.getRequestTime())), customFormat(dspTransaction.getAmount())};
//                        DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
//                        dspMtQueueModel.addSMS(SMS_APPROVAL_TRANSACTION, dspTransaction.getComNumber(), params, mConnection);
//                    }
                    break;
                case "4":
                    updateReservedValue(mConnection, listDspOrderTransaction);
                    break;
//                case "5":
//                    //send SMS
//                    if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
//                            && !"".equals(StringUtil.fix(dspTransaction.getComNumber()))) {
//                        String[] params = {String.valueOf(dspTransaction.getTransactionId()), String.valueOf(DateUtil.getSqlTimestamp(dspTransaction.getRequestTime())), customFormat(dspTransaction.getAmount())};
//                        DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
//                        dspMtQueueModel.addSMS(SMS_REJECT_TRANSACTION, dspTransaction.getComNumber(), params, mConnection);
//                    }
//                    break;
                case "6":
                    updateOrder(mConnection, listDspOrderTransaction);
//                    insertIntoDspCpsQueue(mConnection, dspTransaction);
                    break;
                default:
                    break;
            }

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

    public void updateReservedValue(Connection mConnection, List<DSPOrderTransaction> listDspOrderTransaction) throws Exception {
        String sqlUpdateReserved = "UPDATE dsp_order SET reserved_value = reserved_value - ? WHERE order_id = ?";
        try {
            mStmt = mConnection.prepareStatement(sqlUpdateReserved);
            for (DSPOrderTransaction dspOrderTransaction : listDspOrderTransaction) {
                mStmt.setLong(1, dspOrderTransaction.getAmount());
                mStmt.setLong(2, dspOrderTransaction.getOrderId());
                mStmt.addBatch();
            }
            mStmt.executeBatch();
        } catch (Exception e) {
            throw e;
        } finally {
            close(mStmt);
        }
    }

    public Long checkComId(Connection mConnection, Long userId) throws Exception {
        Long comId = null;
        try {
            String strSQL = "select b.com_id from am_user a ,dsp_company b where a.user_id = b.user_id and a.user_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                comId = mRs.getLong(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
        }
        return comId;
    }

    public List<DSPOrderTransaction> getlistOrderTransaction(Long userId, Long transactionId) throws Exception {
        List<DSPOrderTransaction> listOrderTransaction = new ArrayList<>();
        try {
            open();
            /*String strSQL = "SELECT   a.transaction_id, "
                    + "               a.order_id, "
                    + "               a.issue_time, "
                    + "               a.amount, "
                    + "               a.description, "
                    + "               a.user_id "
                    + "      FROM   dsp_order_transaction a, dsp_company b"
                    + "      where  transaction_id = ? and a.user_id = b.user_id and b.user_id in"
                    + "      (SELECT user_id FROM dsp_company WHERE status = '1' CONNECT BY PRIOR com_id = parent_id START WITH user_id = ?)";*/

            String strSQL = "SELECT   a.transaction_id, "
                    + "               a.order_id, "
                    + "               a.issue_time, "
                    + "               a.amount, "
                    + "               a.description, "
                    + "               a.user_id "
                    + "      FROM   dsp_order_transaction a"
                    + "      where  a.transaction_id = ? ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, transactionId);
//            mStmt.setLong(2, userId);

            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPOrderTransaction tmpDSPOrderTransaction = new DSPOrderTransaction();
                tmpDSPOrderTransaction.setTransactionId(mRs.getLong("transaction_id"));
                tmpDSPOrderTransaction.setOrderId(mRs.getLong("order_id"));
                tmpDSPOrderTransaction.setIssueTime(mRs.getTimestamp("issue_time"));
                tmpDSPOrderTransaction.setAmount(mRs.getLong("amount"));
                tmpDSPOrderTransaction.setDescription(mRs.getString("description"));
                tmpDSPOrderTransaction.setUserId(mRs.getLong("user_id"));
                listOrderTransaction.add(tmpDSPOrderTransaction);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listOrderTransaction;
    }

    public void updateOrder(Connection mConnection, List<DSPOrderTransaction> listDspOrderTransaction) throws Exception {
        String strSQL = "UPDATE dsp_order SET reserved_value = reserved_value - ?, remain_value = remain_value - ? WHERE order_id = ?";
        try {
            mStmt = mConnection.prepareStatement(strSQL);
            for (DSPOrderTransaction dspOrderTransaction : listDspOrderTransaction) {
                mStmt.setLong(1, dspOrderTransaction.getAmount());
                mStmt.setLong(2, dspOrderTransaction.getAmount());
                mStmt.setLong(3, dspOrderTransaction.getOrderId());
                mStmt.addBatch();
            }
            mStmt.executeBatch();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    public void copyDcDetailToDcRequest(Connection mConnection, DSPTransaction dspTransaction) throws Exception {
        String sql = " insert into dsp_dc_request(card_name, amount, total_cost, status, request_time, transaction_id, price_id) "
                + " select card_name, amount, total_cost, status, request_time, transaction_id, price_id"
                + " FROM dsp_dc_detail "
                + " WHERE transaction_id = ? ";
        try {
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setLong(1, dspTransaction.getTransactionId());
            mStmt.executeUpdate();

        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    public void insertIntoDspCpsQueue(Connection mConnection, DSPTransaction dspTransaction) throws Exception {
        String sqlInsert = "insert into dsp_cps_queue(transaction_id,vas_mobile,request_time,status,amount) values(?,?,?,?,?)";
        try {
            mStmt = mConnection.prepareStatement(sqlInsert);
            mStmt.setLong(1, dspTransaction.getTransactionId());
            mStmt.setString(2, dspTransaction.getComNumber());
            mStmt.setTimestamp(3, DateUtil.getSqlTimestamp(new Date()));
            mStmt.setLong(4, 0L);
            mStmt.setLong(5, dspTransaction.getAmount());
            mStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    public List<DSPTransactionHistory> getListDSPTransactionHistory(Long userId, Long transactionId) throws Exception {
        List<DSPTransactionHistory> listDspTransactionHistory = new ArrayList<DSPTransactionHistory>();
        try {
            open();
            String strSQL = "SELECT   a.transaction_id, "
                    + "               a.action_type, "
                    + "               a.description, "
                    + "               a.file_path, "
                    + "               a.issue_time, "
                    + "               a.user_id, "
                    + "               a.extended_days, "
                    + "               (select d.user_name from am_user d where a.user_id = d.user_id) as user_name,"
                    + "               b.com_name "
                    + "      FROM   dsp_transaction_history a, dsp_company b, dsp_transaction c "
                    + "      WHERE a.user_id = b.user_id and a.user_id = ? "
                    + "      AND a.transaction_id = c.transaction_id and a.transaction_id = ? "
                    + "      ORDER BY a.transaction_id ASC ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, transactionId);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPTransactionHistory dspTransactionHistory = new DSPTransactionHistory();
                dspTransactionHistory.setTransactionId(mRs.getLong("transaction_id"));
                dspTransactionHistory.setActionType(mRs.getLong("action_type"));
                dspTransactionHistory.setDescription(mRs.getString("description"));
                dspTransactionHistory.setFilePath(mRs.getString("file_path"));
                dspTransactionHistory.setIssueTime(mRs.getTimestamp("issue_time"));
                dspTransactionHistory.setUserId(mRs.getLong("user_id"));
                dspTransactionHistory.setExtendedDays(mRs.getLong("extended_days"));
                dspTransactionHistory.setComName(mRs.getString("user_name"));
                listDspTransactionHistory.add(dspTransactionHistory);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
        return listDspTransactionHistory;
    }

    public void insertDSPTransactionHistory(DSPTransactionHistory dspTransactionHistory) throws Exception {
        String strSQL = "INSERT INTO dsp_transaction_history(transaction_id, action_type, description, file_path, issue_time, user_id, extended_days) values(?, ?, ?, ?, ?, ?, ?)";
        try {
            open();
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dspTransactionHistory.getTransactionId());
            mStmt.setLong(2, dspTransactionHistory.getActionType());
            mStmt.setString(3, dspTransactionHistory.getDescription());
            mStmt.setString(4, dspTransactionHistory.getFilePath());
            mStmt.setTimestamp(5, DateUtil.getSqlTimestamp(dspTransactionHistory.getIssueTime()));
            mStmt.setLong(6, dspTransactionHistory.getUserId());
            mStmt.setLong(7, dspTransactionHistory.getExtendedDays());
            mStmt.executeUpdate();
            mConnection.commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close();
        }
    }
}
