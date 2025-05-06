/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.admin.gui.entity.UserDTL;
import com.faplib.util.DateUtil;
import com.faplib.util.StringUtil;
import vn.com.telsoft.entity.DSPCompany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HoangNH
 */
public class DSPCompanyUpdatedKeyModel extends AMDataPreprocessor implements Serializable {

    public void edit(DSPCompany company, int check) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);

            //Update app
            List listChange = logBeforeUpdate("DSP_COMPANY", "COM_ID=" + company.getComId());
            String strSQL = "UPDATE DSP_COMPANY SET ";
            if (check == 0) {
                strSQL += " PUBLIC_KEY =?, UPDATED_KEY =? ";
            }
            if (check == 1) {
                strSQL += " API_PUBLIC_KEY=?, API_UPDATED_KEY=? ";
            }
            strSQL += " WHERE COM_ID=? ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (check == 0) {
                mStmt.setString(1, company.getPublicKey().trim());
                mStmt.setDate(2, (DateUtil.getSqlDate(company.getUpdatedKey())));
                mStmt.setLong(3, company.getComId());
            }
            if (check == 1) {
                mStmt.setString(1, company.getApiPublicKey().trim());
                mStmt.setDate(2, (DateUtil.getSqlDate(company.getApiUpdatedKey())));
                mStmt.setLong(3, company.getComId());
            }
            mStmt.execute();
            this.logAfterUpdate(listChange);
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

    public List<DSPCompany> getCompanyUser(long userId) throws Exception {
        List<DSPCompany> returnValue = new ArrayList<DSPCompany>();
        try {
            open();
            String strSQL = " SELECT com_id, user_id, public_key ,updated_key,api_public_key, api_updated_key, api_user_id," +
                    "(SELECT user_name from am_user a where b.user_id = a.user_id ) user_name, " +
                    "(SELECT user_name from am_user a where b.api_user_id = a.user_id ) api_user_name, status" +
                    " FROM dsp_company b where 1=1 and status = 1 and user_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPCompany company = new DSPCompany();
                company.setComId(mRs.getLong(1));
                company.setUserId(mRs.getLong(2));
                company.setPublicKey(mRs.getString(3));
                company.setUpdatedKey(mRs.getDate(4));
                company.setApiPublicKey(mRs.getString(5));
                company.setApiUpdatedKey(mRs.getDate(6));
                company.setApiUserId(mRs.getLong(7));
                company.setUserName(mRs.getString(8));
                company.setApiUserName(mRs.getString(9));
                company.setStatus(mRs.getString(10));
                returnValue.add(company);
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

    // change password from changePassword
    public void editPasswordUser(UserDTL user) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            //Update app
            /*List listCompany = logBeforeUpdate("DSP_COMPANY", "USER_ID=" + user.getUserId());
            List<Object> parameter = new ArrayList<>();*/
            String strSQL = "UPDATE AM_USER SET  password = ?, MODIFIED_PASSWORD = ? WHERE USER_ID=?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, user.getPassword());
            mStmt.setDate(2, (DateUtil.getSqlDate(user.getModifiedPassword())));
            mStmt.setLong(3, user.getUserId());

            mStmt.execute();
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
