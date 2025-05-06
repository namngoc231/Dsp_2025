/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.DSPSmsCommand;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author TungLM, TELSOFT
 */
public class DSPSmsCommandModel extends AMDataPreprocessor implements Serializable {

    public DSPSmsCommand getSmsCommand(String cmdCode) throws Exception {
        if (cmdCode == null || "".equals(cmdCode.trim())) {
            return null;
        }
        try {
            open();
            String strSQL = "SELECT\n" +
                    "       a.cmd_id, a.cmd_code, a.cmd_type, a.cmd_msg_content, a.cmd_param_count, a.description, a.status\n" +
                    "   FROM dsp_sms_command a\n" +
                    "   WHERE a.status = '1'\n" +
                    "       AND lower(a.cmd_code) = ?";
            mStmt = mConnection.prepareStatement(strSQL);
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
            close();
        }
        return null;
    }

    public List<DSPSmsCommand> getListSmsCommand(DSPSmsCommand command) throws Exception {
        List<DSPSmsCommand> listReturn = new ArrayList<>();
        Vector vtParameter = new Vector();
        try {
            open();
            String strSQL = "SELECT\n" +
                    "       a.cmd_id, a.cmd_code, a.cmd_type, a.cmd_msg_content, a.cmd_param_count, a.description, a.status\n" +
                    "   FROM dsp_sms_command a\n" +
                    "   WHERE a.status = '1'\n";
            if (command != null) {
                if (command.getCmdCode() != null && !"".equals(command.getCmdCode().trim())) {
                    strSQL += "     AND lower(a.cmd_code) like ?\n";
                    vtParameter.add("%" + command.getCmdCode().trim().toLowerCase() + "%");
                }
            }
            strSQL += "   ORDER BY a.cmd_code";
            mStmt = mConnection.prepareStatement(strSQL);
            for (int i = 0; i < vtParameter.size(); ++i) {
                mStmt.setObject(i + 1, vtParameter.elementAt(i));
            }
            mRs = mStmt.executeQuery();

            DSPSmsCommand tmpSmsCommand;
            while (mRs.next()) {
                tmpSmsCommand = new DSPSmsCommand();
                tmpSmsCommand.setCmdId(mRs.getLong(1));
                tmpSmsCommand.setCmdCode(mRs.getString(2));
                tmpSmsCommand.setCmdType(mRs.getString(3));
                tmpSmsCommand.setCmdMsgContent(mRs.getString(4));
                tmpSmsCommand.setCmdParamCount(mRs.getLong(5));
                tmpSmsCommand.setDescription(mRs.getString(6));
                tmpSmsCommand.setStatus(mRs.getString(7));
                listReturn.add(tmpSmsCommand);
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

    public List<DSPSmsCommand> getListSmsCommand(DSPSmsCommand command, Connection conn) throws Exception {
        List<DSPSmsCommand> listReturn = new ArrayList<>();
        Vector vtParameter = new Vector();
        try {
            String strSQL = "SELECT\n" +
                    "       a.cmd_id, a.cmd_code, a.cmd_type, a.cmd_msg_content, a.cmd_param_count, a.description, a.status\n" +
                    "   FROM dsp_sms_command a\n" +
                    "   WHERE a.status = '1'\n";
            if (command != null) {
                if (command.getCmdCode() != null && !"".equals(command.getCmdCode().trim())) {
                    strSQL += "     AND lower(a.cmd_code) like ?\n";
                    vtParameter.add("%" + command.getCmdCode().trim().toLowerCase() + "%");
                }
            }
            strSQL += "   ORDER BY a.cmd_code";
            mStmt = conn.prepareStatement(strSQL);
            for (int i = 0; i < vtParameter.size(); ++i) {
                mStmt.setObject(i + 1, vtParameter.elementAt(i));
            }
            mRs = mStmt.executeQuery();

            DSPSmsCommand tmpSmsCommand;
            while (mRs.next()) {
                tmpSmsCommand = new DSPSmsCommand();
                tmpSmsCommand.setCmdId(mRs.getLong(1));
                tmpSmsCommand.setCmdCode(mRs.getString(2));
                tmpSmsCommand.setCmdType(mRs.getString(3));
                tmpSmsCommand.setCmdMsgContent(mRs.getString(4));
                tmpSmsCommand.setCmdParamCount(mRs.getLong(5));
                tmpSmsCommand.setDescription(mRs.getString(6));
                tmpSmsCommand.setStatus(mRs.getString(7));
                listReturn.add(tmpSmsCommand);
            }

        } catch (Exception ex) {
            throw ex;

        } finally {
            close(mRs);
            close(mStmt);
        }

        return listReturn;
    }

}
