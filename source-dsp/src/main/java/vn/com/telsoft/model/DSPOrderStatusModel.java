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
 * @author Trieunv, TELSOFT
 */
public class DSPOrderStatusModel extends AMDataPreprocessor implements Serializable {
    //order status
    static final String ORDER_STATUS_CREATE = "0";
    static final String ORDER_STATUS_WAITING_APPROVAL = "1";
    static final String ORDER_STATUS_APPROVAL = "2";
    static final String ORDER_STATUS_COMPLETE = "3";
    static final String ORDER_STATUS_DELETE = "4";
    static final String ORDER_STATUS_REJECT = "5";
    //SMS
    static final String SMS_APPROVAL_ORDER = "APPROVAL_ORDER";
    static final String SMS_REJECT_ORDER = "REJECT_ORDER";


    public List<DSPOrderStatus> getListOrderStatus(Long orderID) throws Exception {
        List<DSPOrderStatus> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT FILE_PATH,\n" +
                    "NEW_EXPIRE_TIME,\n" +
                    "OLD_EXPIRE_TIME,\n" +
                    "(select b.user_name from am_user b where a.user_id = b.user_id) as USER_NAME,\n" +
                    "ISSUE_TIME,\n" +
                    "ORDER_ID from DSP_ORDER_STATUS a where OLD_EXPIRE_TIME is not null and NEW_EXPIRE_TIME is not null and ORDER_ID = ? ";
            strSQL += " order by ISSUE_TIME desc ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, orderID);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPOrderStatus orderStatus = new DSPOrderStatus();
                orderStatus.setFilePath(mRs.getString(1));
                orderStatus.setNewExpireTime(mRs.getDate(2));
                orderStatus.setOldExpireTime(mRs.getDate(3));
                orderStatus.setUserName(mRs.getString(4));
                orderStatus.setIssueTime(mRs.getTimestamp(5));
                orderStatus.setOrderId(mRs.getLong(6));
                listReturn.add(orderStatus);
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

    public void updateOrderExtension(DSPOrder order, Date newDateExpire, String filePath, Long idUserExt) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQLOrder = "UPDATE dsp_order SET " +
                    "       EXPIRE_TIME=? " +
                    "   WHERE order_id=?";

            mStmt = mConnection.prepareStatement(strSQLOrder);
            java.sql.Date sqlNewDateExpire = new java.sql.Date(newDateExpire.getTime());
            mStmt.setDate(1, sqlNewDateExpire);
            mStmt.setLong(2, order.getOrderId());
            mStmt.execute();
            close(mStmt);

            //add order status
            String strSQLOrderStatus = "INSERT INTO dsp_order_status( " +
                    "       order_id, issue_time, user_id,OLD_EXPIRE_TIME,NEW_EXPIRE_TIME,FILE_PATH, old_status, new_status)" +
                    "   VALUES(?,sysdate,?,?,?,?,?,?)";
            int i = 1;
            mStmt = mConnection.prepareStatement(strSQLOrderStatus);
            mStmt.setLong(i++, order.getOrderId());
            mStmt.setLong(i++, idUserExt);
            java.sql.Date sqlOldDateExpire = new java.sql.Date(order.getExpireTime().getTime());
            mStmt.setDate(i++, sqlOldDateExpire);
            mStmt.setDate(i++, sqlNewDateExpire);
            mStmt.setString(i++, filePath);
            mStmt.setString(i++, order.getStatus());
            mStmt.setString(i, order.getStatus());
            mStmt.execute();
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

}
