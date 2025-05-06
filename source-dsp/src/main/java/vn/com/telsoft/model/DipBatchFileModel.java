package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.ResourceBundleUtil;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.DateUtil;
import vn.com.telsoft.entity.*;
import vn.com.telsoft.util.ExportExcelUtil;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DipBatchFileModel extends AMDataPreprocessor implements Serializable {

    static final String RESOURCE_BUNDLE = "PP_DIP_BATCH_FILE";

    public void insert(DipBatchFile dipBatchFile, List<DipRequest> listDipRequest) throws Exception {
        PreparedStatement stmtDipRequest = null;
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "INSERT INTO dip_batch_file(file_id, file_name, upload_time, real_path, user_id, record_count, total_money, status, com_id)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

            String strSQL1 = "INSERT INTO dip_request(request_id, file_id, channel, isdn, package_code, request_time, status, api_req_id, initial_amount, active_day, price_id, money_amount) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            long id = SQLUtil.getSequenceValue(mConnection, "DIP_BATCH_FILE_SEQ");
            dipBatchFile.setFileId(id);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, id);
            mStmt.setString(2, dipBatchFile.getFileName());
            mStmt.setTimestamp(3, DateUtil.getSqlTimestamp(new Date()));
            mStmt.setString(4, dipBatchFile.getRealPath());
            mStmt.setLong(5, dipBatchFile.getUserId());
            mStmt.setLong(6, dipBatchFile.getRecordCount());
            mStmt.setLong(7, dipBatchFile.getTotalMoney());
            mStmt.setLong(8, 0);
            mStmt.setLong(9, dipBatchFile.getComId());
            mStmt.executeUpdate();

            stmtDipRequest = mConnection.prepareStatement(strSQL1);
            for (DipRequest dipRequest : listDipRequest) {
                long reqId = SQLUtil.getSequenceValue(mConnection, "DIP_REQUEST_SEQ");
                stmtDipRequest.setLong(1, reqId);
                stmtDipRequest.setLong(2, id);
                stmtDipRequest.setString(3, "file");
                stmtDipRequest.setString(4, dipRequest.getIsdn());
                stmtDipRequest.setString(5, dipRequest.getPackageCode());
                stmtDipRequest.setTimestamp(6, DateUtil.getSqlTimestamp(new Date()));
                stmtDipRequest.setLong(7, 1);
                stmtDipRequest.setString(8, dipRequest.getApiReqId());
                stmtDipRequest.setDouble(9, dipRequest.getInitialAmount());
                stmtDipRequest.setLong(10, dipRequest.getActiveDay());
                stmtDipRequest.setLong(11, dipRequest.getPriceId());
                stmtDipRequest.setLong(12, dipRequest.getMoneyAmount());
                stmtDipRequest.addBatch();
            }
            stmtDipRequest.executeBatch();

            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(stmtDipRequest);
            close(mStmt);
            close();
        }
    }

    public void comfirm(DSPTransaction dspTransaction, List<DSPOrderTransaction> listDspOrderTransaction, List<DSPOrder> listDspOrder, DipBatchFile dipBatchFile) throws Exception {
        PreparedStatement stmtDspTransaction = null;
        PreparedStatement stmtDspDcDetail = null;
        PreparedStatement stmtDspOrderTransaction = null;
        PreparedStatement stmtUpdateReserved = null;
        PreparedStatement stmtUpdateTransIdInDipRequest = null;
        PreparedStatement stmtUpdateTransStatus = null;

        String sqlDspTransaction = "INSERT INTO dsp_transaction(transaction_id, request_time, plan_time, amount, status, description, user_id, com_id, tab_id, service_id) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDspOrderTransaction = "INSERT INTO dsp_order_transaction(transaction_id, order_id, issue_time, description, user_id, amount)"
                + "VALUES(?, ?, ?, ?, ?, ?)";
        String sqlUpdateReserved = "UPDATE dsp_order SET reserved_value = ? WHERE order_id = ?";
//        String sqlUpdateReserved = "UPDATE dsp_order SET reserved_value = reserved_value + ? WHERE order_id = ?";
        String sqlUpdateTransIdInDipRequest = "UPDATE dip_request SET transaction_id = ? WHERE file_id = ?";
        String sqlUpdateTransStatus = "UPDATE dsp_transaction SET status = '2' WHERE transaction_id = ?";
        String sqlUpdateFileStatus = "UPDATE dip_batch_file SET status = 2 WHERE file_id = ?";

        try {
            open();
            mConnection.setAutoCommit(false);
            Long id = SQLUtil.getSequenceValue(mConnection, "DSP_TRANSACTION_SEQ");
            dspTransaction.setTransactionId(id);
            stmtDspTransaction = mConnection.prepareStatement(sqlDspTransaction);
            stmtDspTransaction.setLong(1, id);
            stmtDspTransaction.setTimestamp(2, DateUtil.getSqlTimestamp(dspTransaction.getRequestTime()));
            stmtDspTransaction.setTimestamp(3, DateUtil.getSqlTimestamp(dspTransaction.getPlanTime()));
            stmtDspTransaction.setLong(4, dspTransaction.getAmount());
            stmtDspTransaction.setString(5, "0");
            stmtDspTransaction.setString(6, dspTransaction.getDescription());
            stmtDspTransaction.setLong(7, dspTransaction.getUserId());
            stmtDspTransaction.setLong(8, dspTransaction.getComId());
            stmtDspTransaction.setLong(9, dspTransaction.getTabId());
            stmtDspTransaction.setLong(10, dspTransaction.getServiceId());
            stmtDspTransaction.executeUpdate();

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

            stmtUpdateTransIdInDipRequest = mConnection.prepareStatement(sqlUpdateTransIdInDipRequest);
            stmtUpdateTransIdInDipRequest.setLong(1, dspTransaction.getTransactionId());
            stmtUpdateTransIdInDipRequest.setLong(2, dipBatchFile.getFileId());
            stmtUpdateTransIdInDipRequest.executeUpdate();

            insertIntoDipRequestQueue(mConnection, dipBatchFile);

            stmtUpdateTransStatus = mConnection.prepareStatement(sqlUpdateTransStatus);
            stmtUpdateTransStatus.setLong(1, dspTransaction.getTransactionId());
            stmtUpdateTransStatus.executeUpdate();

            mStmt = mConnection.prepareStatement(sqlUpdateFileStatus);
            mStmt.setLong(1, dipBatchFile.getFileId());
            mStmt.executeUpdate();

            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close(stmtUpdateTransStatus);
            close(stmtUpdateTransIdInDipRequest);
            close(stmtUpdateReserved);
            close(stmtDspOrderTransaction);
            close(stmtDspDcDetail);
            close(stmtDspTransaction);
            close();
        }
    }

    public void insertIntoDipRequestQueue(Connection mConnection, DipBatchFile dipBatchFile) throws Exception {
        String sql = " insert into dip_request_queue(request_id, file_id, channel, isdn, package_code, request_time, status, transaction_id, initial_amount, active_day, price_id, money_amount, user_id) "
                + " select a.request_id, a.file_id, a.channel, a.isdn, a.package_code, a.request_time, a.status, a.transaction_id, a.initial_amount, a.active_day, a.price_id, a.money_amount, b.user_id "
                + " FROM dip_request a, dip_batch_file b"
                + " WHERE a.file_id = ? and a.file_id = b.file_id ";
        try {
            mStmt = mConnection.prepareStatement(sql);
            mStmt.setLong(1, dipBatchFile.getFileId());
            mStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    public void delete(DipBatchFile dipBatchFile) throws Exception {
        PreparedStatement stmtDeleteDipRequest = null;
        try {
            open();
            String strSQL = "DELETE FROM dip_batch_file WHERE file_id = ?";
            String strSQL1 = "DELETE FROM dip_request WHERE file_id = ?";
            mConnection.setAutoCommit(false);

            stmtDeleteDipRequest = mConnection.prepareStatement(strSQL1);
            stmtDeleteDipRequest.setLong(1, dipBatchFile.getFileId());
            stmtDeleteDipRequest.executeUpdate();

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dipBatchFile.getFileId());
            mStmt.executeUpdate();

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

    public List<DSPServicePrice> getlistServicePrice() throws Exception {
        List<DSPServicePrice> lsReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select a.price_id, a.tab_id, a.name, a.cap_min, a.cap_max, a.price, a.active_day " +
                    "from DSP_SERVICE_PRICE a ";
            mStmt = mConnection.prepareStatement(strSQL);
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

    public List<DipBatchFile> getlistDipBatchFile(Long userId) throws Exception {
        List<DipBatchFile> listDataCode = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.file_id, "
                    + "               a.file_name, "
                    + "               a.upload_time, "
                    + "               a.real_path, "
                    + "               a.user_id, "
                    + "               a.record_count, "
                    + "               a.total_money, "
                    + "               a.status, "
                    + "               a.com_id, "
                    + "               b.com_name,"
                    + "               (select d.user_name from am_user d where a.user_id = d.user_id) as user_name"
                    + "      FROM   dip_batch_file a, dsp_company b "
                    + "      where a.com_id = b.com_id and b.com_id in"
                    + "      (SELECT com_id FROM dsp_company "
                    + "           WHERE status = '1' "
                    + "           CONNECT BY PRIOR com_id = parent_id START WITH com_id in (select com_id "
                    + "                                         from dsp_company "
                    + "                                         where user_id = ? "
                    + "                                            or group_id in (select group_id from AM_GROUP_USER where user_id = ?) "
                    + "                   ))"
                    + "      ORDER BY a.upload_time desc ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DipBatchFile dipBatchFile = new DipBatchFile();
                dipBatchFile.setFileId(mRs.getLong("file_id"));
                dipBatchFile.setFileName(mRs.getString("file_name"));
                dipBatchFile.setUploadTime(mRs.getTimestamp("upload_time"));
                dipBatchFile.setRealPath(mRs.getString("real_path"));
                dipBatchFile.setUserId(mRs.getLong("user_id"));
                dipBatchFile.setRecordCount(mRs.getLong("record_count"));
                dipBatchFile.setTotalMoney(mRs.getLong("total_money"));
                dipBatchFile.setStatus(mRs.getLong("status"));
                dipBatchFile.setComId(mRs.getLong("com_id"));
                dipBatchFile.setComName(mRs.getString("com_name"));
                dipBatchFile.setUserName(mRs.getString("user_name"));
                listDataCode.add(dipBatchFile);
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

    public List<DipRequest> getlistAllDipRequest(Long userId) throws Exception {
        List<DipRequest> listDipRequest = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.request_id, "
                    + "               a.file_id, "
                    + "               a.channel, "
                    + "               a.isdn, "
                    + "               a.package_code, "
                    + "               a.request_time, "
                    + "               a.status, "
                    + "               a.api_req_id, "
                    + "               a.transaction_id, "
                    + "               a.initial_amount, "
                    + "               a.active_day, "
                    + "               a.price_id, "
                    + "               a.money_amount "
                    + "      FROM   dip_request a, dip_batch_file b"
                    + "      WHERE   a.file_id = b.file_id and b.status = 2 and b.user_id = ?"
                    + "      ORDER BY a.file_id, a.package_code";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DipRequest dipRequest = new DipRequest();
                dipRequest.setRequestId(mRs.getLong("request_id"));
                dipRequest.setFileId(mRs.getLong("file_id"));
                dipRequest.setChannel(mRs.getString("channel"));
                dipRequest.setIsdn(mRs.getString("isdn"));
                dipRequest.setPackageCode(mRs.getString("package_code"));
                dipRequest.setRequestTime(mRs.getTimestamp("request_time"));
                dipRequest.setStatus(mRs.getLong("status"));
                dipRequest.setApiReqId(mRs.getString("api_req_id"));
                dipRequest.setTransactionId(mRs.getLong("transaction_id"));
                dipRequest.setInitialAmount(mRs.getDouble("initial_amount"));
                dipRequest.setActiveDay(mRs.getLong("active_day"));
                dipRequest.setPriceId(mRs.getLong("price_id"));
                dipRequest.setMoneyAmount(mRs.getLong("money_amount"));
                listDipRequest.add(dipRequest);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listDipRequest;
    }

    public List<DipRequest> getlistDipRequest(long fileId) throws Exception {
        List<DipRequest> listDipRequest = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.request_id, "
                    + "               a.file_id, "
                    + "               a.channel, "
                    + "               a.isdn, "
                    + "               a.package_code, "
                    + "               a.request_time, "
                    + "               a.status, "
                    + "               a.api_req_id, "
                    + "               a.transaction_id, "
                    + "               a.initial_amount, "
                    + "               a.active_day, "
                    + "               a.price_id, "
                    + "               a.money_amount "
                    + "      FROM   dip_request a"
                    + "      WHERE  a.file_id = ?"
                    + "      ORDER BY a.package_code";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, fileId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DipRequest dipRequest = new DipRequest();
                dipRequest.setRequestId(mRs.getLong("request_id"));
                dipRequest.setFileId(mRs.getLong("file_id"));
                dipRequest.setChannel(mRs.getString("channel"));
                dipRequest.setIsdn(mRs.getString("isdn"));
                dipRequest.setPackageCode(mRs.getString("package_code"));
                dipRequest.setRequestTime(mRs.getTimestamp("request_time"));
                dipRequest.setStatus(mRs.getLong("status"));
                dipRequest.setApiReqId(mRs.getString("api_req_id"));
                dipRequest.setTransactionId(mRs.getLong("transaction_id"));
                dipRequest.setInitialAmount(mRs.getDouble("initial_amount"));
                dipRequest.setActiveDay(mRs.getLong("active_day"));
                dipRequest.setPriceId(mRs.getLong("price_id"));
                dipRequest.setMoneyAmount(mRs.getLong("money_amount"));
                listDipRequest.add(dipRequest);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listDipRequest;
    }

    public List<DipPackage> getlistDipPackages() throws Exception {
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
                    + "      FROM   dip_package a, dip_service b"
                    + "      WHERE  a.service_id = b.service_id and a.status = 1 and b.status = 1 "
                    + "      ORDER BY a.package_code ";

            mStmt = mConnection.prepareStatement(strSQL);
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
            close(mRs);
            close(mStmt);
            close();
        }
        return listDipPackage;
    }

    public long getComId(Long userId) throws Exception {
        long comId = 0;
        try {
            open();
            String strSQL = "SELECT a.com_id " +
                    "   FROM dsp_company a " +
                    "   WHERE a.status = '1' " +
                    "       AND (a.user_id = ? or a.group_id in (select group_id from AM_GROUP_USER where user_id = ?))";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                comId = mRs.getLong("com_id");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return comId;
    }

    public long getTabId(Long comId, Long serviceId) throws Exception {
        PreparedStatement stmtTabId = null;
        PreparedStatement stmtTabIda = null;
        long tabId = 0;
        boolean bCheck = false;
        try {
            open();
            String strTabId = "SELECT   b.tab_id, b.def"
                    + "      FROM     dsp_service_price_tab b, dsp_com_price c"
                    + "      WHERE    b.tab_id = c.tab_id and b.status = '1' and c.status = '1' and b.def = 0 "
                    + "      and      c.com_id = ? and b.service_id = ? "
                    + "      and (b.start_time is null or b.start_time <= sysdate)"
                    + "      and (b.end_time is null or b.end_time + 1 > sysdate)";

            String strTabIda = "SELECT   b.tab_id, b.def, (sysdate - nvl(b.start_time, TO_DATE('1990-01-01','yyyy-MM-dd'))) as dk1, (nvl(b.end_time, TO_DATE('2100-12-31','yyyy-MM-dd')) - sysdate) as dk2"
                    + "      FROM     dsp_service_price_tab b"
                    + "      WHERE    b.service_id = ? and b.status = '1' and b.def = 1"
                    + "      and (b.start_time is null or b.start_time <= sysdate)"
                    + "      and (b.end_time is null or b.end_time + 1 > sysdate)"
                    + "      order by dk1, dk2";

            stmtTabId = mConnection.prepareStatement(strTabId);
            stmtTabId.setLong(1, comId);
            stmtTabId.setLong(2, serviceId);
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
            close(mRs);
            close(stmtTabIda);
            close(stmtTabId);
            close();
        }
        return tabId;
    }

    public List<DSPServicePrice> getlistDspServicePrice(Long tabId) throws Exception {
        List<DSPServicePrice> listDipPackage = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT   a.price_id, "
                    + "               a.name, "
                    + "               a.price, "
                    + "               a.cap_min, "
                    + "               a.cap_max, "
                    + "               a.active_day "
                    + "      FROM   dsp_service_price a"
                    + "      WHERE  a.tab_id = ? ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, tabId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPServicePrice dspServicePrice = new DSPServicePrice();
                dspServicePrice.setPriceId(mRs.getLong("price_id"));
                dspServicePrice.setName(mRs.getString("name"));
                dspServicePrice.setPrice(mRs.getLong("price"));
                dspServicePrice.setCapMin(mRs.getLong("cap_min"));
                dspServicePrice.setCapMax(mRs.getLong("cap_max"));
                dspServicePrice.setActiveDay(mRs.getLong("active_day"));
                listDipPackage.add(dspServicePrice);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listDipPackage;
    }

    public long getServiceId(String packageCode) throws Exception {
        long service_id = 0;
        try {
            open();
            String strSQL = "SELECT a.service_id " +
                    "   FROM dip_package a " +
                    "   WHERE a.package_code = ? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, packageCode);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                service_id = mRs.getLong("service_id");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return service_id;
    }

    public String getlistDipRequestHistory(long fileId, long status) throws Exception {
        String strReturn = null;
        String strHeader = null;
        String strFileName = "";
        try {
            open();
            String strSQL = "SELECT   a.isdn, "
                    + "               a.package_code, "
                    + "               to_char(a.process_time, 'YYYY-MM-DD HH24:MI:SS') as processTime, "
                    + "               a.initial_amount ";
            if (status == 3) {
                strSQL += ", b.par_value ";
            }
            strSQL += " FROM dip_request_hist a, ap_param b WHERE a.response = b.par_name and a.file_id = ? AND a.status = ? "
                     + " AND b.par_type = 'DIP_ERROR' "
                     + " ORDER BY a.process_time desc ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, fileId);
            mStmt.setLong(2, status);
            mRs = mStmt.executeQuery();
            if (status == 1) {
                strFileName = "DanhSachXuliThanhCong";
                strHeader = ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "file_name_success");
            } else {
                strFileName = "DanhSachXuliThatBai";
                strHeader = ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "file_name_fail");
            }
            HashMap<String, String> hsReturn = new HashMap<>();
            hsReturn = putHeadercolumn(status);
            strReturn = ExportExcelUtil.exportExcel(mRs, strFileName, strHeader, null, hsReturn);
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return strReturn;
    }

    public HashMap<String, String> putHeadercolumn(long status) {
        HashMap<String, String> hsReturn = new HashMap<>();
        hsReturn.put("isdn".toUpperCase(), ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "isdn"));
        hsReturn.put("package_code".toUpperCase(), ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "packageCode"));
        hsReturn.put("initial_amount".toUpperCase(), ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "initialAmount") + " (KB)");
        hsReturn.put("processTime".toUpperCase(), ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "process_time"));
        if (status == 3) {
            hsReturn.put("par_value".toUpperCase(), ResourceBundleUtil.getCTObjectAsString(RESOURCE_BUNDLE, "response"));
        }
        return hsReturn;
    }

    public void updateDipBatchFileStatus(Long fileId) throws Exception {
        try {
            open();
            String strSQL = "UPDATE dip_batch_file SET status = 6 WHERE file_id = ?";
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, fileId);
            mStmt.executeUpdate();
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
                    + "      (select com_id " +
                    "         from dsp_company " +
                    "         where user_id = ? )"
                    + "      and a.service_id = ? and a.service_id = c.service_id and a.status = '6' "
                    + "      ORDER BY a.status, a.plan_time ASC ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, serviceId);
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
}
