/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.CBItemContent;
import vn.com.telsoft.entity.CBItemContentExt;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author trieunv
 */
public class CbItemContentModel extends AMDataPreprocessor implements Serializable {

    public Boolean checkCbItemContentDup(CBItemContent dto) throws Exception {
        List lstParam = new ArrayList<>();
        int i = 0;
        try {
            open();
            String strSQL = "SELECT * " +
                    " FROM CB_ITEM_CONTENT where 1=1 ";
            if (dto != null) {
                if (dto.getListId() != null) {
                    strSQL += " and list_Id = ?";
                    lstParam.add(dto.getListId());
                }
                if (dto.getContentId() != null) {
                    strSQL += " and content_Id = ?";
                    lstParam.add(dto.getContentId());
                }
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

    public void deleteCBItemContent(CBItemContent dto) throws Exception {

        try {
            open();
            String strSQL = "DELETE FROM CB_ITEM_CONTENT WHERE list_Id = ? and content_Id = ? ";
            mConnection.setAutoCommit(false);
            logBeforeDelete("CB_ITEM_CONTENT", "list_Id=" + dto.getListId());
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getListId());
            mStmt.setLong(2, dto.getContentId());
            mStmt.execute();

            //Commit
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close(mConnection);
        }
    }



    private Timestamp convertTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

}
