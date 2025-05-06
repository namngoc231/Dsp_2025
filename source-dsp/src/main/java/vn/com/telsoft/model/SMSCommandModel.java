/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.SystemLogger;
import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.admin.gui.entity.AppGUI;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.SMSCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author HoangNH
 */
public class SMSCommandModel extends AMDataPreprocessor {

    public List<SMSCommand> getAll() throws Exception {
        List<SMSCommand> returnValue = new ArrayList<SMSCommand>();

        String strSQL = "SELECT CMD_ID,CMD_CODE,CMD_TYPE,CMD_MSG_CONTENT,CMD_PARAM_COUNT,DESCRIPTION,CMD_REGEX,STATUS,SYS_TYPE FROM DSP_SMS_COMMAND ";
        try {
            open();
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                SMSCommand tmp = new SMSCommand();
                tmp.setCmdId(mRs.getLong(1));
                tmp.setCmdCode(mRs.getString(2));
                tmp.setCmdType(mRs.getString(3));
                tmp.setCmdMsgContent(mRs.getString(4));
                tmp.setCmdParamCount(mRs.getLong(5));
                tmp.setDescription(mRs.getString(6));
                tmp.setCmdRegex(mRs.getString(7));
                tmp.setStatus(mRs.getString(8));
                tmp.setSysType(mRs.getString(9));
                returnValue.add(tmp);
            }
        } catch (Exception ex) {
            SystemLogger.getLogger().error(ex);
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return returnValue;
    }

    public long add(SMSCommand ett) throws Exception {
        long idFromSequence;
        try {
            open();
            idFromSequence = SQLUtil.getSequenceValue(mConnection, "DSP_SMS_COMMAND_SEQ");
            ett.setCmdId(idFromSequence);
            String strSQL = "INSERT INTO DSP_SMS_COMMAND (CMD_ID, CMD_CODE, CMD_TYPE, CMD_MSG_CONTENT, CMD_PARAM_COUNT,CMD_REGEX, DESCRIPTION,STATUS,SYS_TYPE) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, idFromSequence);
            mStmt.setString(2, ett.getCmdCode());
            mStmt.setString(3, ett.getCmdType());
            mStmt.setString(4, ett.getCmdMsgContent());
            mStmt.setLong(5, ett.getCmdParamCount());
            mStmt.setString(6, ett.getCmdRegex());
            mStmt.setString(7, ett.getDescription());
            mStmt.setString(8, ett.getStatus());
            mStmt.setString(9, ett.getSysType());
            mStmt.execute();
        } catch (Exception var8) {
            throw var8;
        } finally {
            close(mStmt);
            close();
        }

        return idFromSequence;
    }

    public void delete(List<SMSCommand> listSMS) throws Exception {
        try {
            open();
            String strSQL = "DELETE FROM DSP_SMS_COMMAND WHERE CMD_ID=?";
            mConnection.setAutoCommit(false);
            Iterator var3 = listSMS.iterator();

            while (var3.hasNext()) {
                SMSCommand sms = (SMSCommand) var3.next();
                logBeforeDelete("DSP_SMS_COMMAND", "CMD_ID=" + sms.getCmdId());
                mStmt = mConnection.prepareStatement(strSQL);
                mStmt.setLong(1, sms.getCmdId());
                mStmt.execute();
            }

            mConnection.commit();
        } catch (Exception var8) {
            mConnection.rollback();
            throw var8;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close(mConnection);
        }
    }


    public void update(SMSCommand ett) throws Exception {
        open();
        mConnection.setAutoCommit(false);
        String strSQL = "UPDATE DSP_SMS_COMMAND " +
                "SET CMD_CODE=?, CMD_TYPE=?, CMD_MSG_CONTENT=?, CMD_PARAM_COUNT=?, DESCRIPTION=?,CMD_REGEX=?,STATUS=?" +
                "  WHERE CMD_ID = ?";
        try {
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, ett.getCmdCode());
            mStmt.setString(2, ett.getCmdType());
            mStmt.setString(3, ett.getCmdMsgContent());
            mStmt.setLong(4, ett.getCmdParamCount());
            mStmt.setString(5, ett.getDescription());
            mStmt.setString(6, ett.getCmdRegex());
            mStmt.setString(7, ett.getStatus());
            mStmt.setLong(8, ett.getCmdId());

            mStmt.executeUpdate();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            SystemLogger.getLogger().error(ex);
            throw new Exception(ex);

        } finally {
            close(mStmt);
            close(mRs);
            close();
        }
    }

    public void edit(SMSCommand ett) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "UPDATE DSP_SMS_COMMAND " +
                    "SET CMD_CODE=?, CMD_TYPE=?, CMD_MSG_CONTENT=?, CMD_PARAM_COUNT=?, DESCRIPTION=?,CMD_REGEX=?,STATUS=?,SYS_TYPE=?" +
                    "  WHERE CMD_ID = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, ett.getCmdCode());
            mStmt.setString(2, ett.getCmdType());
            mStmt.setString(3, ett.getCmdMsgContent());
            mStmt.setLong(4, ett.getCmdParamCount());
            mStmt.setString(5, ett.getDescription());
            mStmt.setString(6, ett.getCmdRegex());
            mStmt.setString(7, ett.getStatus());
            mStmt.setString(8, ett.getSysType());
            mStmt.setLong(9, ett.getCmdId());
            mStmt.execute();
            close(mStmt);
            mConnection.commit();
        } catch (Exception var7) {
            mConnection.rollback();
            throw var7;
        } finally {
            close(mStmt);
            close();
        }

    }


}
