/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.ApiUser;

import java.io.Serializable;

/**
 * @author Hanv, TELSOFT
 */
public class ApiUserModel extends AMDataPreprocessor implements Serializable {

    public void insert(ApiUser apiUser) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "insert into Api_User(api_id, user_id) values (?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, apiUser.getApiId());
            mStmt.setLong(2, apiUser.getUserId());
            mStmt.execute();
            logAfterInsert("API_USER", "API_ID=" + apiUser.getApiId());
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(ApiUser apiUser) throws Exception {
        try {
            open();
            String strSQL = "DELETE FROM Api_User WHERE api_id= ? and user_id=? ";
            mConnection.setAutoCommit(false);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, apiUser.getApiId());
            mStmt.setLong(2, apiUser.getUserId());
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
}
