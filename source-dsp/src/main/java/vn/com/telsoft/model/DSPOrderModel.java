/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.SystemConfig;
import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.DateUtil;
import com.faplib.util.StringUtil;
import vn.com.telsoft.entity.*;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Types;
import java.util.*;

/**
 * @author TungLM, TELSOFT
 */
public class DSPOrderModel extends AMDataPreprocessor implements Serializable {
    //order status
    static final String ORDER_STATUS_CREATE = "0";
    static final String ORDER_STATUS_WAITING_APPROVAL = "1";
    static final String ORDER_STATUS_APPROVED = "21";
    static final String ORDER_STATUS_COMPLETE = "3";
    static final String ORDER_STATUS_DELETE = "4";
    static final String ORDER_STATUS_REJECT = "5";
    static final String ORDER_STATUS_ACTIVE = "2";
    static final String ORDER_STATUS_WAITING_MONEY = "6";
    static final String ORDER_STATUS_APPROVAL = "8";
    //SMS
    static final String SMS_APPROVAL_ORDER = "APPROVAL_ORDER";
    static final String SMS_REJECT_ORDER = "REJECT_ORDER";
    static final String SMS_ACTIVE_ORDER = "ACTIVE_ORDER";
    static final String SMS_TRANSFER_ORDER = "TRANSFER_ORDER";
    static final String SMS_CONFIRM_ORDER = "CONFIRM_ORDER";
    static final String SMS_APPROVAL_ORDER_FOR_COMPANY = "APPROVAL_ORDER_FOR_COMPANY";


    public void addOrderStatus(DSPOrderStatus orderStatus, Connection conn) throws Exception {
        try {
            //add order status
            String strSQ = "INSERT INTO dsp_order_status( " +
                    "       order_id, old_status, new_status, issue_time, description, user_id)" +
                    "   VALUES(?,?,?,to_date(?,'dd/MM/yyyy hh24:mi:ss'),?,?)";
            int i = 1;
            mStmt = conn.prepareStatement(strSQ);
            mStmt.setLong(i++, orderStatus.getOrderId());
            mStmt.setString(i++, orderStatus.getOldStatus());
            mStmt.setString(i++, orderStatus.getNewStatus());
            mStmt.setString(i++, DateUtil.getDateTimeStr(orderStatus.getIssueTime()));
            mStmt.setString(i++, orderStatus.getDescription());
            mStmt.setLong(i, orderStatus.getUserId());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    public long addOrder(DSPOrder order) throws Exception {
        long idFromSequence;
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());*/

        try {
            open();
            mConnection.setAutoCommit(false);

            //add order
            idFromSequence = SQLUtil.getSequenceValue(mConnection, "DSP_ORDER_SEQ");
            order.setOrderId(idFromSequence);
            order.setOrderCode(order.getComId() + "_" + idFromSequence);

            //set order_time
            //order.setOrderTime(calendar.getTime());

            //Tinh expire_time: truong hop don hang duoc tao voi trang thai tao moi: status = 0
            /*if (ORDER_STATUS_CREATE.equals(order.getStatus())) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }*/

            //Tinh expire_time: truong hop don hang do MVAS tao voi trang thai phe duyet: status = 2
            /*if (ORDER_STATUS_APPROVAL.equals(order.getStatus())) {
                //set approval time
                order.setApprovalTime(calendar.getTime());
                //get dsp_order_policy
                DSPOrderPolicy dspOrderPolicy = getOrderPolicy(order, mConnection);
                if (dspOrderPolicy != null && dspOrderPolicy.getActiveDays() != null) {
                    calendar.add(Calendar.DAY_OF_YEAR, dspOrderPolicy.getActiveDays().intValue() + 1);
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            }*/

            //set expire_time
            //order.setExpireTime(DateUtils.truncate(calendar.getTime(), Calendar.DATE));


            String strSQLOrder = "INSERT INTO dsp_order( " +
                    "       order_id, com_id, tab_id, order_code, order_time, status," +
                    "       contract_value, paid_cost, pay_method, description, contract_code," +
                    "       approval_id, approval_desc, approval_time, effective_time, expire_time," +
                    "       reject_reason, remain_value, reserved_value,file_path,discount_type,discount_amt) " +
                    "   VALUES(" +
                    "       ?,?,?,?,to_date(?,'dd/MM/yyyy hh24:mi:ss'),?," +
                    "       ?,?,?,?,?," +
                    "       ?,?,to_date(?,'dd/MM/yyyy hh24:mi:ss'),to_date(?,'dd/MM/yyyy hh24:mi:ss'),to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       ?,?,?,?,?,?)";

            int i = 1;
            mStmt = mConnection.prepareStatement(strSQLOrder);
            mStmt.setLong(i++, idFromSequence);
            mStmt.setLong(i++, order.getComId());
            if (order.getTabId() != null) {
                mStmt.setLong(i++, order.getTabId());
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            mStmt.setString(i++, order.getOrderCode());
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getOrderTime()));
            mStmt.setString(i++, order.getStatus());
            mStmt.setLong(i++, order.getContractValue());
            mStmt.setLong(i++, order.getPaidCost());
            mStmt.setString(i++, order.getPayMethod());
            mStmt.setString(i++, order.getDescription());
            mStmt.setString(i++, order.getContractCode());
            if (order.getApprovalId() != null) {
                mStmt.setLong(i++, order.getApprovalId());
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            if (order.getApprovalDesc() != null) {
                mStmt.setString(i++, order.getApprovalDesc());
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            if (order.getApprovalTime() != null) {
                mStmt.setString(i++, DateUtil.getDateTimeStr(order.getOrderTime()));
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getEffectiveTime()));
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getExpireTime()));
            mStmt.setString(i++, order.getRejectReason());
            mStmt.setLong(i++, order.getRemainValue());
            mStmt.setNull(i++, Types.NULL);
            mStmt.setString(i++, order.getFilePath());
            mStmt.setString(i++, order.getDiscountType());
            if (order.getDiscountAmt() != null) {
                mStmt.setLong(i, order.getDiscountAmt());
            } else {
                mStmt.setNull(i, Types.NULL);
            }
            mStmt.execute();
            close(mStmt);

            //add order status
            DSPOrderStatus orderStatus = new DSPOrderStatus();
            orderStatus.setOrderId(order.getOrderId());
            orderStatus.setOldStatus(order.getOldStatus());
            orderStatus.setNewStatus(order.getStatus());
            orderStatus.setIssueTime(order.getOrderTime());
            orderStatus.setUserId(order.getUserId());
            addOrderStatus(orderStatus, mConnection);

//            logAfterInsert("dsp_order", "order_id=" + idFromSequence);
            if (ORDER_STATUS_WAITING_MONEY.equals(order.getStatus())) {
                insertIntoDspCpsQueue(mConnection, order);
            }
            //Send SMS
            if (ORDER_STATUS_APPROVAL.equals(order.getStatus())) {
                if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                        && !"".equals(StringUtil.fix(order.getManagerMobile()))) {
                    String[] params = new String[2];
                    params[0] = order.getContractCode();
                    params[1] = order.getDspCompany().getComName();
                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                    dspMtQueueModel.addSMS(SMS_CONFIRM_ORDER, order.getManagerMobile(), params, mConnection);
                }
            }

            //update DSP_COMPANY
            if ("1".equals(order.getDiscountType())) {
                DSPCompany dspCompany = new DSPCompany();
                dspCompany.setCheckDate(order.getDspCompany().getCheckDate());
                dspCompany.setBkCheckDate(order.getDspCompany().getBkCheckDate());
                dspCompany.setComId(order.getComId());
                updCheckDateCompany(mConnection, dspCompany);
            }

            //Done
            mConnection.commit();

        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;

        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close();
        }

        return idFromSequence;
    }

    public void activeOrder(DSPOrder order) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);

            //set order_code
            order.setOrderCode(order.getComId() + "_" + order.getOrderId());

            String strSQL = "UPDATE dsp_order SET " +
                    "       status=?," +
                    "       effective_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       expire_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       activated_date =to_date(?,'dd/MM/yyyy hh24:mi:ss')" +
                    "   WHERE order_id=? ";

            int i = 1;
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(i++, order.getStatus());
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getEffectiveTime()));
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getExpireTime()));
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getActivatedDate()));
            mStmt.setLong(i, order.getOrderId());
            mStmt.execute();
//            logAfterUpdate(listChange);
            close(mStmt);

            //add order status
            if (!order.getStatus().equals(order.getOldStatus())) {
                DSPOrderStatus orderStatus = new DSPOrderStatus();
                orderStatus.setOrderId(order.getOrderId());
                orderStatus.setOldStatus(order.getOldStatus());
                orderStatus.setNewStatus(order.getStatus());
                orderStatus.setIssueTime(new Date());
                orderStatus.setUserId(order.getUserId());
                addOrderStatus(orderStatus, mConnection);
            }

            //Send SMS
            if (ORDER_STATUS_ACTIVE.equals(order.getStatus())) {
                if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                        && !"".equals(StringUtil.fix(order.getDspCompany().getVasMobile()))) {
                    String[] params = new String[1];
                    params[0] = order.getContractCode();
                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                    dspMtQueueModel.addSMS(SMS_ACTIVE_ORDER, order.getDspCompany().getVasMobile(), params, mConnection);
                }
            }
            //Commit
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

    public void editOrder(DSPOrder order) throws Exception {
        /*Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());*/

        try {
            open();
            mConnection.setAutoCommit(false);

            //set order_code
            order.setOrderCode(order.getComId() + "_" + order.getOrderId());

            //Tinh expire_time: truong hop don hang duoc sua voi trang thai tao moi: status = 0
            /*if (ORDER_STATUS_CREATE.equals(order.getStatus())) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }*/

            //Tinh expire_time: truong hop don hang do MVAS sua voi trang thai da xac nhan: status = 2
            /*if (ORDER_STATUS_APPROVAL.equals(order.getStatus())) {
                //set approval time
                order.setApprovalTime(calendar.getTime());
                //get dsp_order_policy
                DSPOrderPolicy dspOrderPolicy = getOrderPolicy(order, mConnection);
                if (dspOrderPolicy != null && dspOrderPolicy.getActiveDays() != null) {
                    calendar.add(Calendar.DAY_OF_YEAR, dspOrderPolicy.getActiveDays().intValue() + 1);
                } else {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            }*/

            //set expire_time
            //order.setExpireTime(DateUtils.truncate(calendar.getTime(), Calendar.DATE));

            //Update app
//            List listChange = logBeforeUpdate("dsp_order", "order_id=" + order.getOrderId());
            String strSQL = "UPDATE dsp_order SET " +
                    "       pay_method=?, " +
                    "       contract_value=?, paid_cost=?, " +
                    "       contract_code=?, com_id=?, " +
                    "       description=?, status=?," +
                    "       order_code=?, " +
                    "       order_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       effective_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       expire_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       remain_value=?, reject_reason=?," +
                    "       approval_id=?, approval_desc=?," +
                    "       approval_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')," +
                    "       tab_id=?," +
                    "       file_path = ? " +
                    "   WHERE order_id=?";

            int i = 1;
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(i++, order.getPayMethod());
            mStmt.setLong(i++, order.getContractValue());
            mStmt.setLong(i++, order.getPaidCost());
            mStmt.setString(i++, order.getContractCode());
            mStmt.setLong(i++, order.getComId());
            mStmt.setString(i++, order.getDescription());
            mStmt.setString(i++, order.getStatus());
            mStmt.setString(i++, order.getOrderCode());
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getOrderTime()));
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getEffectiveTime()));
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getExpireTime()));
            mStmt.setLong(i++, order.getRemainValue());
            mStmt.setString(i++, order.getRejectReason());
            if (order.getApprovalId() != null) {
                mStmt.setLong(i++, order.getApprovalId());
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            if (order.getApprovalDesc() != null) {
                mStmt.setString(i++, order.getApprovalDesc());
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            if (order.getApprovalTime() != null) {
                mStmt.setString(i++, DateUtil.getDateTimeStr(order.getOrderTime()));
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            if (order.getTabId() != null) {
                mStmt.setLong(i++, order.getTabId());
            } else {
                mStmt.setNull(i++, Types.NULL);
            }
            mStmt.setString(i++, order.getFilePath());
            mStmt.setLong(i, order.getOrderId());
            mStmt.execute();
//            logAfterUpdate(listChange);
            close(mStmt);

            //add order status
            if (!order.getStatus().equals(order.getOldStatus())) {
                DSPOrderStatus orderStatus = new DSPOrderStatus();
                orderStatus.setOrderId(order.getOrderId());
                orderStatus.setOldStatus(order.getOldStatus());
                orderStatus.setNewStatus(order.getStatus());
                orderStatus.setIssueTime(order.getOrderTime());
                orderStatus.setUserId(order.getUserId());
                addOrderStatus(orderStatus, mConnection);
            }

            if (ORDER_STATUS_WAITING_MONEY.equals(order.getStatus())) {
                insertIntoDspCpsQueue(mConnection, order);
            }

            //Send SMS
            if (ORDER_STATUS_APPROVAL.equals(order.getStatus())) {
                if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                        && !"".equals(StringUtil.fix(order.getManagerMobile()))) {
                    String[] params = new String[2];
                    params[0] = order.getContractCode();
                    params[1] = order.getDspCompany().getComName();
                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                    dspMtQueueModel.addSMS(SMS_CONFIRM_ORDER, order.getManagerMobile(), params, mConnection);
                }
            }

            if (ORDER_STATUS_WAITING_APPROVAL.equals(order.getStatus())) {
                if (!"".equals(StringUtil.fix(order.getAdminVasMobile())) && order.getDspCompany().getType() != 5
                        && "1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))) {
                    String[] params = new String[6];
                    params[0] = order.getContractCode();
                    params[1] = order.getDspCompany().getComName();
                    params[2] = order.getUserName();
                    params[3] = order.getContractValue().toString();
                    params[4] = DateUtil.getDateStr(order.getEffectiveTime());
                    params[5] = DateUtil.getDateStr(order.getExpireTime());
                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                    dspMtQueueModel.addSMS(SMS_TRANSFER_ORDER, order.getAdminVasMobile(), params, mConnection);
                }
            }

            //update DSP_COMPANY
//            if ("1".equals(order.getDiscountType())) {
//                DSPCompany dspCompany = new DSPCompany();
//                dspCompany.setCheckDate(order.getDspCompany().getCheckDate());
//                dspCompany.setBkCheckDate(order.getDspCompany().getBkCheckDate());
//                dspCompany.setComId(order.getComId());
//                updCheckDateCompany(mConnection, dspCompany);
//            }

            //Commit
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

    public void confirmOrder(DSPOrder order) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        try {
            open();
            mConnection.setAutoCommit(false);
            order.setApprovalTime(calendar.getTime());
            String strSQL = "UPDATE dsp_order SET status = ? WHERE order_id = ?";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, order.getStatus());
            mStmt.setLong(2, order.getOrderId());
            mStmt.execute();
            close(mStmt);

            //add order status
            DSPOrderStatus orderStatus = new DSPOrderStatus();
            orderStatus.setOrderId(order.getOrderId());
            orderStatus.setOldStatus(order.getOldStatus());
            orderStatus.setNewStatus(order.getStatus());
            orderStatus.setIssueTime(order.getApprovalTime());
            orderStatus.setUserId(order.getApprovalId());
            addOrderStatus(orderStatus, mConnection);

            //send SMS
            if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                    && !"".equals(StringUtil.fix(order.getManagerMobile()))) {
                String[] params = new String[2];
                params[0] = order.getContractCode();
                params[1] = order.getDspCompany().getComName();
                DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                dspMtQueueModel.addSMS(SMS_CONFIRM_ORDER, order.getManagerMobile(), params, mConnection);
            }

            //Commit
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


    public void approvalOrder(DSPOrder order) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        try {
            open();
            mConnection.setAutoCommit(false);
            //set approval time
            order.setApprovalTime(calendar.getTime());
            //get dsp_order_policy
            /*
            DSPOrderPolicy dspOrderPolicy = getOrderPolicy(order, mConnection);
            if (dspOrderPolicy != null && dspOrderPolicy.getActiveDays() != null) {
                calendar.add(Calendar.DAY_OF_YEAR, dspOrderPolicy.getActiveDays().intValue() + 1);
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            //set expire_time
            order.setExpireTime(DateUtils.truncate(calendar.getTime(), Calendar.DATE));
            */
            //Update app
//            List listChange = logBeforeUpdate("dsp_order", "order_id=" + order.getOrderId());
            String strSQL = "UPDATE dsp_order SET " +
                    "       approval_id=?, " +
                    "       approval_time=to_date(?,'dd/MM/yyyy hh24:mi:ss'), " +
                    "       approval_desc=?, " +
                    "       status=? " +
//                    "       expire_time=to_date(?,'dd/MM/yyyy hh24:mi:ss')" +
                    "   WHERE order_id=?";

            int i = 1;
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(i++, order.getApprovalId());
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getApprovalTime()));
            mStmt.setString(i++, order.getApprovalDesc());
            mStmt.setString(i++, order.getStatus());
//            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getExpireTime()));
            mStmt.setLong(i, order.getOrderId());
            mStmt.execute();
//            logAfterUpdate(listChange);
            close(mStmt);

            //add order status
            DSPOrderStatus orderStatus = new DSPOrderStatus();
            orderStatus.setOrderId(order.getOrderId());
            orderStatus.setOldStatus(order.getOldStatus());
            orderStatus.setNewStatus(order.getStatus());
            orderStatus.setIssueTime(order.getApprovalTime());
            orderStatus.setUserId(order.getApprovalId());
            addOrderStatus(orderStatus, mConnection);

            //insert cps_queue
            if ("1".equals(order.getPayMethod())) {
                insertIntoDspCpsQueue(mConnection, order);
            }

            //send SMS
            if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                    && !"".equals(StringUtil.fix(order.getAdminVasMobile()))) {
                String[] params = new String[2];
                params[0] = order.getContractCode();
                params[1] = order.getDspCompany().getComName();
                DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                dspMtQueueModel.addSMS(SMS_APPROVAL_ORDER, order.getAdminVasMobile(), params, mConnection);
            }

            if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                    && !"".equals(StringUtil.fix(order.getDspCompany().getVasMobile())) && order.getDspCompany().getType() != 5) {
                String[] params = new String[1];
                params[0] = order.getContractCode();
                DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                dspMtQueueModel.addSMS(SMS_APPROVAL_ORDER_FOR_COMPANY, order.getDspCompany().getVasMobile(), params, mConnection);
            }

            //Commit
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

    public void rejectOrder(DSPOrder order, long userId) throws Exception {

        try {
            open();
            mConnection.setAutoCommit(false);
            //set reject time
            order.setApprovalTime(new Date());

            //Update app
            String strSQL = "UPDATE dsp_order SET " +
                    "       approval_id=?, approval_time=to_date(?,'dd/MM/yyyy hh24:mi:ss'), " +
                    "       reject_reason=?, status=?" +
                    "   WHERE order_id=?";

            int i = 1;
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(i++, order.getApprovalId());
            mStmt.setString(i++, DateUtil.getDateTimeStr(order.getApprovalTime()));
            mStmt.setString(i++, order.getRejectReason());
            mStmt.setString(i++, order.getStatus());
            mStmt.setLong(i, order.getOrderId());
            mStmt.execute();
            close(mStmt);

            //add order status
            DSPOrderStatus orderStatus = new DSPOrderStatus();
            orderStatus.setOrderId(order.getOrderId());
            orderStatus.setOldStatus(order.getOldStatus());
            orderStatus.setNewStatus(order.getStatus());
            orderStatus.setIssueTime(order.getApprovalTime());
            orderStatus.setUserId(order.getApprovalId());
            addOrderStatus(orderStatus, mConnection);

            //send SMS
            if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                    && !"".equals(StringUtil.fix(order.getDspCompany().getVasMobile()))) {
                String[] params = new String[1];
                params[0] = order.getContractCode();
                DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                dspMtQueueModel.addSMS(SMS_REJECT_ORDER, order.getDspCompany().getVasMobile(), params, mConnection);
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

    public void editStatusOrder(DSPOrder order) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);

            //Update app
            String strSQL = "UPDATE dsp_order SET " +
                    "       status=?" +
                    "   WHERE order_id=?";

            int i = 1;
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(i++, order.getStatus());
            mStmt.setLong(i, order.getOrderId());
            mStmt.execute();
            close(mStmt);

            //add order status
            DSPOrderStatus orderStatus = new DSPOrderStatus();
            orderStatus.setOrderId(order.getOrderId());
            orderStatus.setOldStatus(order.getOldStatus());
            orderStatus.setNewStatus(order.getStatus());
            orderStatus.setIssueTime(new Date());
            orderStatus.setUserId(order.getUserId());
            addOrderStatus(orderStatus, mConnection);

            //update DSP_COMPANY
            if (ORDER_STATUS_DELETE.equals(order.getStatus())) {
                if ("1".equals(order.getDiscountType()) && order.getOrderTime().compareTo(order.getDspCompany().getCheckDate()) >= 0) {
                    DSPCompany dspCompany = new DSPCompany();
                    dspCompany.setCheckDate(order.getDspCompany().getCheckDate());
                    dspCompany.setBkCheckDate(order.getDspCompany().getBkCheckDate());
                    dspCompany.setComId(order.getComId());
                    updCheckDateCompany(mConnection, dspCompany);
                }
            }
            if (ORDER_STATUS_WAITING_APPROVAL.equals(order.getStatus())) {
                if (!"".equals(StringUtil.fix(order.getAdminVasMobile())) && order.getDspCompany().getType() != 5
                        && "1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))) {
                    String[] params = new String[6];
                    params[0] = order.getContractCode();
                    params[1] = order.getDspCompany().getComName();
                    params[2] = order.getUserName();
                    params[3] = order.getContractValue().toString();
                    params[4] = DateUtil.getDateStr(order.getEffectiveTime());
                    params[5] = DateUtil.getDateStr(order.getExpireTime());
                    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();
                    dspMtQueueModel.addSMS(SMS_TRANSFER_ORDER, order.getAdminVasMobile(), params, mConnection);
                }
            }

            //Commit
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

    public long getManagerId(Long comId) throws Exception {
        long result = 0;
        try {
            open();
            String strSQL = "select user_id from am_group_user where group_id in (select ag.parent_id from dsp_company dc, am_user au, am_group_user agu, am_group ag " +
                    "where dc.user_id = au.user_id and au.user_id = agu.user_id and agu.group_id = ag.group_id and dc.type = 0 " +
                    "start with dc.com_id = ? connect by prior dc.parent_id = dc.com_id)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                result = mRs.getLong(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return result;
    }

    public String getManagerMobile(Long userId) throws Exception {
        String result = "";
        try {
            open();
            String strSQL = "SELECT mobile FROM am_user where user_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                result = mRs.getString(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return result;
    }

    public List<DSPOrder> getListOrder(DSPOrder order, Date fromOrderDate, Date toOrderDate, Date fromExpDate, Date toExpDate, long userId) throws Exception {
        List<DSPOrder> listReturn = new ArrayList<>();
        Vector vtParameter = new Vector();
        try {
            open();
            String strSQL = "SELECT" +
                    "       a.order_id, a.com_id, a.order_code, a.order_time, a.status," +
                    "       a.contract_value, a.paid_cost, a.pay_method, a.description, a.contract_code," +
                    "       a.approval_id, a.approval_desc, a.approval_time, a.effective_time, a.expire_time, a.reject_reason," +
                    "       a.remain_value, a.reserved_value, (nvl(a.REMAIN_VALUE, 0) - nvl(a.RESERVED_VALUE, 0)) as remain_quota_value," +
                    "       a.serial, a.cancel_time, a.type, a.discount_type, nvl(a.discount_amt, 0), " +
                    "       c.com_id, c.com_name, c.type, c.tax_code, c.bus_code, c.vas_mobile, c.status as com_status,c.cps_mobile, " +
                    "       c.cust_type, c.check_date, c.bk_check_date, " +
                    "       d.user_id, (select e.user_name from am_user e where d.user_id = e.user_id) as orderUser," +
                    "       (select f.user_name from am_user f where a.approval_id = f.user_id) as approvalUser," +
                    "       a.tab_id, a.file_path, (nvl(a.contract_value, 0) - nvl(a.remain_value, 0)) as money_used, " +
                    "       (select vas_mobile from dsp_company where type = 0 start with com_id = a.com_id connect by prior parent_id = com_id) as adminMobile, " +
                    "       (select user_id from am_group_user where group_id in (select ag.parent_id from dsp_company dc, am_user au, am_group_user agu, am_group ag " +
                    "               where dc.user_id = au.user_id and au.user_id = agu.user_id and agu.group_id = ag.group_id and dc.type = 0 " +
                    "               start with dc.com_id = a.com_id connect by prior dc.parent_id = dc.com_id)) as userManager " +
                    "   FROM dsp_order a, dsp_company c," +
                    "       (select order_id, user_id from" +
                    "           (SELECT  order_id, user_id, ROW_NUMBER() OVER (PARTITION BY ORDER_ID ORDER BY ISSUE_TIME) AS rno" +
                    "           FROM dsp_order_status)" +
                    "       where rno = 1) d" +
                    "   WHERE 1=1" +
                    "       AND a.com_id = c.com_id" +
                    "       AND a.order_id = d.order_id" +
                    "       AND ((c.com_id in (SELECT com_id" +
                    "                        FROM dsp_company" +
                    "                        WHERE status = '1'" +
                    "                        CONNECT BY PRIOR com_id = parent_id" +
                    "                        START WITH com_id in (select com_id " +
                    "                                              from dsp_company " +
                    "                                              where user_id = ? " +
                    "                                              or group_id in (select group_id from AM_GROUP_USER where user_id = ?)))) " +
                    "   or (select user_id from am_group_user " +
                    "                       where group_id in (select ag.parent_id from dsp_company dc, am_user au, am_group_user agu, am_group ag " +
                    "                       where dc.user_id = au.user_id and au.user_id = agu.user_id and agu.group_id = ag.group_id and dc.type = 0 " +
                    "                       start with dc.com_id = a.com_id connect by prior dc.parent_id = dc.com_id)) = ?) ";
            vtParameter.add(userId);
            vtParameter.add(userId);
            vtParameter.add(userId);

            if (order != null) {
                if (order.getContractCode() != null && !"".equals(order.getContractCode().trim())) {
                    strSQL += "     AND lower(a.contract_code) like ?";
                    vtParameter.add("%" + order.getContractCode().trim().toLowerCase() + "%");
                }

                if (order.getStatus() != null && !"".equals(order.getStatus())) {
                    strSQL += "     AND a.status=?";
                    vtParameter.add(order.getStatus());
                }
            }

            if (fromOrderDate != null) {
                strSQL = strSQL + "     AND a.order_time>=to_date(?,'dd/MM/yyyy')";
                vtParameter.add(DateUtil.getDateStr(fromOrderDate));
            }

            if (toOrderDate != null) {
                strSQL = strSQL + "     AND a.order_time<to_date(?,'dd/MM/yyyy') + 1";
                vtParameter.add(DateUtil.getDateStr(toOrderDate));
            }

            if (fromExpDate != null) {
                strSQL = strSQL + "     AND a.expire_time>=to_date(?,'dd/MM/yyyy')";
                vtParameter.add(DateUtil.getDateStr(fromExpDate));
            }

            if (toExpDate != null) {
                strSQL = strSQL + "     AND a.expire_time<to_date(?,'dd/MM/yyyy') + 1";
                vtParameter.add(DateUtil.getDateStr(toExpDate));
            }

            strSQL += "   ORDER BY a.order_time DESC";
            mStmt = mConnection.prepareStatement(strSQL);
            for (int k = 0; k < vtParameter.size(); ++k) {
                mStmt.setObject(k + 1, vtParameter.get(k));
            }
            mRs = mStmt.executeQuery();

            DSPOrder tmpDSPOrder;
            DSPCompany tmpDSPCompany;
            int i = 1;
            while (mRs.next()) {
                i = 1;
                tmpDSPOrder = new DSPOrder();
                tmpDSPOrder.setOrderId(mRs.getLong(i++));
                tmpDSPOrder.setComId(mRs.getLong(i++));
                tmpDSPOrder.setOrderCode(mRs.getString(i++));
                tmpDSPOrder.setOrderTime(mRs.getDate(i++));
                tmpDSPOrder.setStatus(mRs.getString(i++));
                tmpDSPOrder.setOldStatus(tmpDSPOrder.getStatus());
                tmpDSPOrder.setContractValue(mRs.getLong(i++));
                tmpDSPOrder.setPaidCost(mRs.getLong(i++));
                tmpDSPOrder.setPayMethod(mRs.getString(i++));
                tmpDSPOrder.setDescription(mRs.getString(i++));
                tmpDSPOrder.setContractCode(mRs.getString(i++));
                tmpDSPOrder.setApprovalId(mRs.getLong(i++));
                tmpDSPOrder.setApprovalDesc(mRs.getString(i++));
                tmpDSPOrder.setApprovalTime(mRs.getDate(i++));
                tmpDSPOrder.setEffectiveTime(mRs.getDate(i++));
                tmpDSPOrder.setExpireTime(mRs.getDate(i++));
                tmpDSPOrder.setRejectReason(mRs.getString(i++));
                tmpDSPOrder.setRemainValue(mRs.getLong(i++));
                tmpDSPOrder.setReservedValue(mRs.getLong(i++));
                tmpDSPOrder.setRemainQuotaValue(mRs.getLong(i++));
                tmpDSPOrder.setSerial(mRs.getString(i++));
                tmpDSPOrder.setCancelTime(mRs.getDate(i++));
                tmpDSPOrder.setType(mRs.getString(i++));
                tmpDSPOrder.setDiscountType(mRs.getString(i++));
                tmpDSPOrder.setDiscountAmt(mRs.getLong(i++));
                tmpDSPOrder.setDiscountPercent(100 - ((double) tmpDSPOrder.getPaidCost() / tmpDSPOrder.getContractValue() * 100));

                tmpDSPCompany = new DSPCompany();
                tmpDSPCompany.setComId(mRs.getLong(i++));
                tmpDSPCompany.setComName(mRs.getString(i++));
                tmpDSPCompany.setType(mRs.getLong(i++));
                tmpDSPCompany.setTaxCode(mRs.getString(i++));
                tmpDSPCompany.setBusCode(mRs.getString(i++));
                tmpDSPCompany.setVasMobile(mRs.getString(i++));
                tmpDSPCompany.setStatus(mRs.getString(i++));
                tmpDSPCompany.setCpsMobile(mRs.getString(i++));
                tmpDSPCompany.setCustType(mRs.getString(i++));
                tmpDSPCompany.setCheckDate(mRs.getDate(i++));
                tmpDSPCompany.setBkCheckDate(mRs.getDate(i++));
                tmpDSPOrder.setDspCompany(tmpDSPCompany);

                tmpDSPOrder.setUserId(mRs.getLong(i++));
                tmpDSPOrder.setUserName(mRs.getString(i++));
                tmpDSPOrder.setApprovalUser(mRs.getString(i++));
                tmpDSPOrder.setTabId(mRs.getLong(i++));
                tmpDSPOrder.setFilePath(mRs.getString(i++));
                tmpDSPOrder.setMoneyUsed(mRs.getLong(i++));
                tmpDSPOrder.setAdminVasMobile(mRs.getString(i++));
                tmpDSPOrder.setManagerUserId(mRs.getLong(i));

                listReturn.add(tmpDSPOrder);
            }
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }

        return listReturn;
    }

    public List<DSPService> getListService() throws Exception {
        List<DSPService> listReturn = new ArrayList<>();

        try {
            open();
            String strSQL = "SELECT a.service_id, a.service_code, a.service_name, a.status" +
                    "   FROM dsp_service a" +
                    "   WHERE a.status = '1'" +
                    "   ORDER BY a.service_code ASC";
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPService tmpDSPService = new DSPService();
                tmpDSPService.setServiceId(mRs.getLong(1));
                tmpDSPService.setServiceCode(mRs.getString(2));
                tmpDSPService.setServiceName(mRs.getString(3));
                tmpDSPService.setStatus(mRs.getString(4));
                listReturn.add(tmpDSPService);
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

    public List<DSPCompany> getListCompany(String query, long userId, int rows) throws Exception {
        List<DSPCompany> listReturn = new ArrayList<>();
        if (query == null || "".equals(query.trim())) {
            return listReturn;
        }
        Vector vtParameter = new Vector();
        try {
            open();
            String strSQL = "SELECT * FROM ( " +
                    "   SELECT a.com_id, a.com_name, a.type, a.tax_code, a.bus_code, a.vas_mobile, a.status,a.cps_mobile, a.cust_type, a.check_date, a.bk_check_date, rownum rown " +
                    "   FROM dsp_company a" +
                    "   WHERE a.status = '1' " +
                    "       AND a.type in (2, 3)" +
                    "       AND (lower(a.com_name) like ? OR lower(a.tax_code) like ? OR lower(a.bus_code) like ?)" +
                    "   CONNECT BY PRIOR a.com_id = a.parent_id" +
                    "   START WITH a.com_id = (select com_id from dsp_company where user_id = ?)" +
                    "   ) b" +
                    "   WHERE b.rown <= ?";
            vtParameter.add("%" + query.trim().toLowerCase() + "%");
            vtParameter.add("%" + query.trim().toLowerCase() + "%");
            vtParameter.add("%" + query.trim().toLowerCase() + "%");
            vtParameter.add(userId);
            vtParameter.add(rows);
            mStmt = mConnection.prepareStatement(strSQL);
            for (int i = 0; i < vtParameter.size(); ++i) {
                mStmt.setObject(i + 1, vtParameter.elementAt(i));
            }
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPCompany tmpDSPCompany = new DSPCompany();
                tmpDSPCompany.setComId(mRs.getLong(1));
                tmpDSPCompany.setComName(mRs.getString(2));
                tmpDSPCompany.setType(mRs.getLong(3));
                tmpDSPCompany.setTaxCode(mRs.getString(4));
                tmpDSPCompany.setBusCode(mRs.getString(5));
                tmpDSPCompany.setVasMobile(mRs.getString(6));
                tmpDSPCompany.setStatus(mRs.getString(7));
                tmpDSPCompany.setCpsMobile(mRs.getString(8));
                tmpDSPCompany.setCustType(mRs.getString(9));
                tmpDSPCompany.setCheckDate(mRs.getDate(10));
                tmpDSPCompany.setBkCheckDate(mRs.getDate(11));
                listReturn.add(tmpDSPCompany);
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

    public DSPCompany getUserCompany(long userId) throws Exception {
        try {
            open();
            String strSQL = "SELECT a.com_id, a.com_name, a.type, a.tax_code, a.bus_code, a.status, a.cust_type, a.check_date, a.bk_check_date\n" +
                    "   FROM dsp_company a\n" +
                    "   WHERE a.status = '1'\n" +
                    "       AND a.user_id = ?\n" +
                    "   ORDER BY a.com_name ASC";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPCompany tmpDSPCompany = new DSPCompany();
                tmpDSPCompany.setComId(mRs.getLong(1));
                tmpDSPCompany.setComName(mRs.getString(2));
                tmpDSPCompany.setType(mRs.getLong(3));
                tmpDSPCompany.setTaxCode(mRs.getString(4));
                tmpDSPCompany.setBusCode(mRs.getString(5));
                tmpDSPCompany.setStatus(mRs.getString(6));
                tmpDSPCompany.setCustType(mRs.getString(7));
                tmpDSPCompany.setCheckDate(mRs.getDate(8));
                tmpDSPCompany.setBkCheckDate(mRs.getDate(9));
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

    //Lay thong tin chinh sach don hang dang ap dung
    public DSPOrderPolicy getOrderPolicy(DSPOrder order, Connection conn) throws Exception {
        if (order.getTabId() == null) {
            return null;
        }
        try {
            String strSQL = "SELECT a.id, b.service_id, a.tab_id, a.min_value, a.max_value, a.active_days, a.prom_pct\n" +
                    "FROM dsp_order_policy a, dsp_order_policy_tab b\n" +
                    "WHERE a.tab_id = b.tab_id\n" +
//                    "    AND b.service_id = ?\n" +
                    "    AND b.tab_id = ?\n" +
                    "    AND (a.min_value is null or a.min_value <= ?)\n" +
                    "    AND (a.max_value is null or a.max_value > ?)";
            mStmt = conn.prepareStatement(strSQL);
            int i = 1;
//            mStmt.setLong(i++, order.getServiceId());
            mStmt.setLong(i++, order.getTabId());
            mStmt.setLong(i++, order.getContractValue());
            mStmt.setLong(i++, order.getContractValue());
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPOrderPolicy tmpDSPOrderPolicy = new DSPOrderPolicy();
                tmpDSPOrderPolicy.setId(mRs.getLong(1));
                tmpDSPOrderPolicy.setServiceId(mRs.getLong(2));
                tmpDSPOrderPolicy.setTabId(mRs.getLong(3));
                tmpDSPOrderPolicy.setMinValue(mRs.getLong(4));
                tmpDSPOrderPolicy.setMaxValue(mRs.getLong(5));
                tmpDSPOrderPolicy.setActiveDays(mRs.getLong(6));
                tmpDSPOrderPolicy.setPromPct(mRs.getLong(7));
                return tmpDSPOrderPolicy;
            }

        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
        }
        return null;
    }

    //Lay danh sach chinh sach don hang ma cty co the dc ap dung
    public List<DSPOrderPolicy> getListOrderPolicy(long comId, String custType) throws Exception {
        List<DSPOrderPolicy> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select pol.id, tab.service_id, pol.tab_id, pol.min_value, pol.max_value, pol.active_days, pol.prom_pct\n" +
                    "from (select service_id, tab_id from \n" +
                    "        (select service_id, tab_id, prio, rown from\n" +
                    "            ((SELECT b.service_id, b.tab_id, 1 prio, rownum rown\n" +
                    "            FROM dsp_order_policy_tab b, dsp_com_order_pol c\n" +
                    "            WHERE b.tab_id = c.tab_id\n" +
//                    "                AND b.service_id = ?\n" +
                    "                AND b.status = '1' AND b.def = 0\n" +//chinh sach khac mac dinh: def = 0: lay chinh sach dc map voi com_id
                    "                AND c.com_id = ? AND c.status = '1'\n" +
                    "                AND (b.start_date is null or b.start_date <= sysdate)\n" +
                    "                AND (b.end_date is null or b.end_date + 1 > sysdate))\n" +
                    "            UNION ALL\n" +
                    "            (select service_id, tab_id, 2 prio, rownum rown from\n" +
                    "                (SELECT b.service_id, b.tab_id\n" +
                    "                FROM dsp_order_policy_tab b\n" +
                    "                WHERE 1 = 1\n" +
//                    "                    AND b.service_id = ?\n" +
                    "                    AND b.status = '1' AND b.def = 1 AND b.cust_type = ?\n" +//chinh sach mac dinh: def = 1 and start_date is not null --> lay chinh sach có ngày start_date gan nhat
                    "                    AND b.start_date is not null\n" +
                    "                    AND b.start_date <= sysdate\n" +
                    "                    AND (b.end_date is null or b.end_date + 1 > sysdate)\n" +
                    "                order by b.start_date desc))\n" +
                    "            UNION ALL\n" +
                    "            (SELECT b.service_id, b.tab_id, 3 prio, rownum rown\n" +
                    "            FROM dsp_order_policy_tab b\n" +
                    "            WHERE 1 = 1\n" +
//                    "                AND b.service_id = ?\n" +
                    "                AND b.status = '1' AND b.def = 1 AND b.cust_type = ?\n" +//chinh sach mac dinh: def = 1 and start_date is null
                    "                AND b.start_date is null\n" +
                    "                AND (b.end_date is null or b.end_date + 1 > sysdate))) a\n" +
                    "        order by prio, rown)\n" +
                    "    where rownum = 1) tab, \n" +
                    "    dsp_order_policy pol\n" +
                    "where tab.tab_id = pol.tab_id";
            mStmt = mConnection.prepareStatement(strSQL);
//            mStmt.setLong(i++, serviceId);
            mStmt.setLong(1, comId);
            mStmt.setString(2, custType);
            mStmt.setString(3, custType);
//            mStmt.setLong(i++, serviceId);
//            mStmt.setLong(i++, serviceId);
            mRs = mStmt.executeQuery();

            DSPOrderPolicy tmpDSPOrderPolicy;
            int i = 1;
            while (mRs.next()) {
                i = 1;
                tmpDSPOrderPolicy = new DSPOrderPolicy();
                tmpDSPOrderPolicy.setId(mRs.getLong(i++));
                tmpDSPOrderPolicy.setServiceId(mRs.getLong(i++));
                tmpDSPOrderPolicy.setTabId(mRs.getLong(i++));
                tmpDSPOrderPolicy.setMinValue(mRs.getLong(i++));
                tmpDSPOrderPolicy.setMaxValue(mRs.getLong(i++));
                tmpDSPOrderPolicy.setActiveDays(mRs.getLong(i++));
                tmpDSPOrderPolicy.setPromPct(mRs.getLong(i));
                listReturn.add(tmpDSPOrderPolicy);
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

    public List<DSPOrder> getListOrderForTrans(long comId) throws Exception {
        List<DSPOrder> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "" +
                    "SELECT\n" +
                    "    a.order_id, a.com_id, a.order_code, a.order_time, a.status,\n" +
                    "    a.contract_value, a.paid_cost, a.pay_method, a.description, a.contract_code,\n" +
                    "    a.approval_id, a.approval_desc, a.approval_time, a.effective_time, a.expire_time, a.reject_reason,\n" +
                    "    a.remain_value, a.reserved_value, (nvl(a.REMAIN_VALUE, 0) - nvl(a.RESERVED_VALUE, 0)) as remain_quota_value\n" +
                    "FROM dsp_order a, dsp_company c\n" +
                    "WHERE 1=1\n" +
                    "    AND a.status = '2'\n" +
                    "    AND a.effective_time <= sysdate\n" +
                    "    AND a.expire_time + 1 > sysdate\n" +
//                    "    AND (nvl(a.REMAIN_VALUE, 0) - nvl(a.RESERVED_VALUE, 0)) > 0\n" +
                    "    AND a.REMAIN_VALUE > 0\n" +
                    "    AND a.com_id = c.com_id\n" +
                    "    AND c.com_id = ?\n" +
                    "ORDER BY a.expire_time, a.order_id";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();

            DSPOrder tmpDSPOrder;
            int i = 1;
            while (mRs.next()) {
                i = 1;
                tmpDSPOrder = new DSPOrder();
                tmpDSPOrder.setOrderId(mRs.getLong(i++));
                tmpDSPOrder.setComId(mRs.getLong(i++));
                tmpDSPOrder.setOrderCode(mRs.getString(i++));
                tmpDSPOrder.setOrderTime(mRs.getDate(i++));
                tmpDSPOrder.setStatus(mRs.getString(i++));
                tmpDSPOrder.setContractValue(mRs.getLong(i++));
                tmpDSPOrder.setPaidCost(mRs.getLong(i++));
                tmpDSPOrder.setPayMethod(mRs.getString(i++));
                tmpDSPOrder.setDescription(mRs.getString(i++));
                tmpDSPOrder.setContractCode(mRs.getString(i++));
                tmpDSPOrder.setApprovalId(mRs.getLong(i++));
                tmpDSPOrder.setApprovalDesc(mRs.getString(i++));
                tmpDSPOrder.setApprovalTime(mRs.getDate(i++));
                tmpDSPOrder.setEffectiveTime(mRs.getDate(i++));
                tmpDSPOrder.setExpireTime(mRs.getDate(i++));
                tmpDSPOrder.setRejectReason(mRs.getString(i++));
                tmpDSPOrder.setRemainValue(mRs.getLong(i++));
                tmpDSPOrder.setReservedValue(mRs.getLong(i++));
                tmpDSPOrder.setRemainQuotaValue(mRs.getLong(i));
                listReturn.add(tmpDSPOrder);
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

    public long getTotalRemainOrder(long comId) throws Exception {
        long lReturn = 0l;
        try {
            open();
            String strSQL = "" +
                    "SELECT\n" +
                    "    sum ((nvl(a.REMAIN_VALUE, 0) - nvl(a.RESERVED_VALUE, 0))) as total_remain_quota_value\n" +
                    "FROM dsp_order a, dsp_company c\n" +
                    "WHERE 1=1\n" +
                    "    AND a.status = '2'\n" +
                    "    AND a.effective_time <= sysdate\n" +
                    "    AND a.expire_time + 1 > sysdate\n" +
                    "    AND (nvl(a.REMAIN_VALUE, 0) - nvl(a.RESERVED_VALUE, 0)) > 0\n" +
                    "    AND a.com_id = c.com_id\n" +
                    "    AND c.com_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                lReturn = mRs.getLong(1);
                return lReturn;
            }

        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
            close();
        }

        return lReturn;
    }

    public void insertIntoDspCpsQueue(Connection mConnection, DSPOrder order) throws Exception {
        String sqlInsert = "insert into dsp_cps_queue(transaction_id,vas_mobile,request_time,status,amount) values(?,?,?,?,?)";
        try {
            mStmt = mConnection.prepareStatement(sqlInsert);
            mStmt.setLong(1, order.getOrderId());
            if ("1".equals(order.getPayMethod())) {
                mStmt.setString(2, order.getDspCompany().getCpsMobile());
            } else {
                mStmt.setString(2, order.getDspCompany().getVasMobile());
            }
            mStmt.setTimestamp(3, DateUtil.getSqlTimestamp(new Date()));
            mStmt.setLong(4, 0L);
            mStmt.setLong(5, order.getPaidCost());
            mStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    //huynq 30/05/2022 //tam dung, mail 24/06/2022
    /*public long checkFirstOrder(long comId) throws Exception {
        long returnValue = 0;
        try {
            open();
            String strSQL = "SELECT count(*)" +
                    "   FROM dsp_order a" +
                    "   WHERE a.com_id = ? AND a.status not in (4, 5) " +
                    "   AND TO_CHAR(a.order_time, 'MM/YYYY') = TO_CHAR(sysdate, 'MM/YYYY')";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                returnValue = mRs.getLong(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return returnValue;
    }

    public Long totalCostOrder(Date date, long comId) throws Exception {
        long returnValue = 0;
        try {
            open();
            String strSQL = "SELECT sum(a.contract_value)" +
                    "   FROM dsp_order a " +
                    "   WHERE a.com_id = ? AND a.status in (2, 3) " +
                    "   AND TO_CHAR(a.activated_date, 'MM/YYYY') in (TO_CHAR(ADD_MONTHS(?, -1), 'MM/YYYY'), TO_CHAR(ADD_MONTHS(?, -2), 'MM/YYYY'), TO_CHAR(ADD_MONTHS(?, -3), 'MM/YYYY'))";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mStmt.setTimestamp(2, DateUtil.getSqlTimestamp(date));
            mStmt.setTimestamp(3, DateUtil.getSqlTimestamp(date));
            mStmt.setTimestamp(4, DateUtil.getSqlTimestamp(date));
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                returnValue = mRs.getLong(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return returnValue;
    }*/
    /////END

    ///
    public Long totalCostOrder(long comId) throws Exception {
        long returnValue = 0;
        try {
            open();
            String strSQL = "SELECT sum(a.contract_value)" +
                    "   FROM dsp_order a, dsp_company b, " +
                    "       (SELECT activated_date " +
                    "           FROM (SELECT * FROM dsp_order " +
                    "                   WHERE com_id = ? AND status in (2, 3) " +
                    "                   AND activated_date is not null " +
                    "                   ORDER BY activated_date desc) " +
                    "           WHERE rownum = 1) c " +
                    "   WHERE a.com_id = ? AND a.com_id = b.com_id AND a.status in (2, 3) " +
                    "   AND a.activated_date is not null " +
                    "   AND a.activated_date >= b.check_date " +
                    "   AND a.activated_date BETWEEN (c.activated_date - 91) AND c.activated_date " +
                    "   AND (TRUNC(SYSDATE) - TRUNC(c.activated_date)) <= 60 ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mStmt.setLong(2, comId);
            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                returnValue = mRs.getLong(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return returnValue;
    }

    public void updCheckDateCompany(Connection conn, DSPCompany company) throws Exception {
        try{
            String strSQL = "UPDATE dsp_company a SET a.check_date = ?, a.bk_check_date = ? WHERE a.com_id = ?";
            mStmt = conn.prepareStatement(strSQL);
            mStmt.setTimestamp(1, DateUtil.getSqlTimestamp(company.getCheckDate()));
            mStmt.setTimestamp(2, DateUtil.getSqlTimestamp(company.getBkCheckDate()));
            mStmt.setLong(3, company.getComId());
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

}
