/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.SystemLogger;
import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.admin.gui.entity.AccessRight;
import com.faplib.lib.admin.gui.entity.AppGUI;
import com.faplib.lib.admin.gui.entity.ModuleGUI;
import com.faplib.lib.config.Config;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.StringUtil;
import com.faplib.ws.client.ClientRequestProcessor;
import vn.com.telsoft.entity.CBSubscriber;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TUNGLM, TELSOFT
 */
public class InfoISDNModel extends AMDataPreprocessor implements Serializable {

    public void updateInfoISDN(CBSubscriber sub) throws Exception {

        try {
            open();
            mConnection.setAutoCommit(false);

            //Update cb_subscriber
            List listChange = logBeforeUpdate("cb_subscriber", "id=" + sub.getId());
            String strSQL = "UPDATE cb_subscriber SET status=? WHERE id=?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, sub.getStatus());
            mStmt.setLong(2, sub.getId());
            mStmt.execute();
            logAfterUpdate(listChange);
            close(mStmt);

            //Commit
            mConnection.commit();

        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;

        } finally {
            close(mStmt);
            close();
        }
    }
}
