/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.AmUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trieunv
 */
public class AmUserModel extends AMDataPreprocessor implements Serializable {

    public List<AmUser> getListAmUserAll(String userName) throws Exception {
        List<AmUser> listReturn = new ArrayList<>();

        try {
            open();
            String strSQL = "SELECT u.user_id, u.user_name,u.password,u.expire_status,u.modified_password \n" +
                    " FROM am_user u, am_group_user ug, am_group g, dsp_company c \n" +
                    " WHERE     u.user_id = ug.user_id\n" +
                    "      AND ug.GROUP_ID = g.GROUP_ID\n" +
                    "      AND u.user_id = c.api_user_id\n" +
                    "      AND upper(u.user_name) like '%" + userName.toUpperCase() + "%'" +
                    "      AND c.api_public_key IS NOT NULL\n" +
                    "      AND g.GROUP_ID IN (SELECT par_value FROM ap_param\n" +
                    "                         WHERE par_name = 'API_GROUP_ID' AND par_type = 'SYSTEM')\n" +
                    "      AND NVL (g.status, 1) > 0\n" +
                    "      AND NVL (u.status, 1) > 0\n" +
                    " UNION ALL\n" +
                    " SELECT user_id,user_name,password,expire_status,modified_password\n" +
                    " FROM am_user\n" +
                    " WHERE UPPER (user_name) = (SELECT UPPER (par_value) FROM ap_param WHERE par_name = 'INTERNAL_USER' AND par_type = 'SYSTEM')";

            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                AmUser amUser = new AmUser();
                amUser.setUserId(mRs.getLong(1));
                amUser.setUserName(mRs.getString(2));
                listReturn.add(amUser);
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

}
