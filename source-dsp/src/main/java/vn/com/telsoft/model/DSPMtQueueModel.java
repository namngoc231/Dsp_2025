/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.DateUtil;
import vn.com.telsoft.entity.DSPMtQueue;
import vn.com.telsoft.entity.DSPSmsCommand;
import vn.com.telsoft.util.Utils;

import java.io.Serializable;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Date;

/**
 * @author TungLM, TELSOFT
 */
public class DSPMtQueueModel extends AMDataPreprocessor implements Serializable {
    //
    static final String MT_QUEUE_SHORTCODE = "9034";
    static final Long MT_QUEUE_RETRIES = 3L;
    static final String MT_QUEUE_STATUS = "0";

    public void addMtQueue(DSPMtQueue mtQueue, Connection conn) throws Exception {
        try {
            String strSQ = "INSERT INTO dsp_mt_queue( " +
                    "       id, request_id, isdn, content, shortcode, retries, process_time, status)" +
                    "   VALUES(?,?,?,?,?,?,?,?)";

            long idFromSequence = SQLUtil.getSequenceValue(conn, "DSP_MT_QUEUE_SEQ");
            int i = 1;
            mStmt = conn.prepareStatement(strSQ);
            mStmt.setLong(i++, idFromSequence);
            mStmt.setLong(i++, idFromSequence);
            mStmt.setString(i++, Utils.fixIsdnWithout0and84(mtQueue.getIsdn()));
            mStmt.setString(i++, mtQueue.getContent());
            mStmt.setString(i++, MT_QUEUE_SHORTCODE);
            mStmt.setLong(i++, MT_QUEUE_RETRIES);
            mStmt.setTimestamp(i++, DateUtil.getSqlTimestamp(new Date()));
            mStmt.setString(i, MT_QUEUE_STATUS);
            mStmt.execute();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    public DSPSmsCommand getSmsCommand(String cmdCode, Connection conn) throws Exception {
        if (cmdCode == null || "".equals(cmdCode.trim())) {
            return null;
        }
        try {
            String strSQL = "SELECT\n" +
                    "       a.cmd_id, a.cmd_code, a.cmd_type, a.cmd_msg_content, a.cmd_param_count, a.description, a.status\n" +
                    "   FROM dsp_sms_command a\n" +
                    "   WHERE a.status = '1'\n" +
                    "       AND lower(a.cmd_code) = ?";
            mStmt = conn.prepareStatement(strSQL);
            mStmt.setString(1, cmdCode.trim().toLowerCase());
            mRs = mStmt.executeQuery();

            if (mRs.next()) {
                DSPSmsCommand tmpSmsCommand = new DSPSmsCommand();
                tmpSmsCommand.setCmdId(mRs.getLong(1));
                tmpSmsCommand.setCmdCode(mRs.getString(2));
                tmpSmsCommand.setCmdType(mRs.getString(3));
                tmpSmsCommand.setCmdMsgContent(mRs.getString(4));
                tmpSmsCommand.setCmdParamCount(mRs.getLong(5));
                tmpSmsCommand.setDescription(mRs.getString(6));
                tmpSmsCommand.setStatus(mRs.getString(7));
                return tmpSmsCommand;
            }

        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
        }
        return null;
    }

    public void addSMS(String cmdCode, String isdn, String[] params, Connection conn) throws Exception {
        try {
            DSPSmsCommand smsCommand = getSmsCommand(cmdCode, conn);
            String msgContent = "";
            if (smsCommand != null) {
                msgContent = smsCommand.getCmdMsgContent();
                if (params != null) {
                    for (int i = 0; i < params.length; i++) {
                        msgContent = MessageFormat.format(msgContent, params);
                    }
                }
            }

            if (msgContent != null && msgContent.length() > 0) {
                DSPMtQueue mtQueue = new DSPMtQueue();
                mtQueue.setIsdn(isdn);
                mtQueue.setContent(msgContent);
                addMtQueue(mtQueue, conn);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
        }
    }

}
