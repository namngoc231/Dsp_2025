/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trieunv
 */
public class CbContentModel extends AMDataPreprocessor implements Serializable {

    public String getSql(String strDescription) throws Exception {

        String strSQL = "SELECT content_Id, content_Description, status " +
                " FROM CB_CONTENT where status = 1 ";
        if (strDescription != null && !strDescription.trim().isEmpty()) {
            strSQL += " and LOWER(content_Description) like '%" + strDescription.trim().toLowerCase() + "%'";
        }
        strSQL += " order by CONTENT_ID";
        return strSQL;
    }

    public Boolean checkCbContentDup(Integer id) throws Exception {
        List lstParam = new ArrayList<>();
        int i = 0;
        try {
            open();
            String strSQL = "SELECT * " +
                    " FROM CB_Content where 1=1 ";
            if (id != null) {
                strSQL += " and content_id = ?";
                lstParam.add(id);
            }
            mStmt = mConnection.prepareStatement(strSQL);
            if (lstParam != null && lstParam.size() > 0) {
                for (Object obj : lstParam) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return false;
    }
}
