/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.CBItemContent;
import vn.com.telsoft.entity.CBItemContentExt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author trieunv
 */
public class CbItemContentExtModel extends AMDataPreprocessor implements Serializable {

    public List<CBItemContentExt> getCBItemContentExtAll(Long id, String keyWord) throws Exception {
        List<CBItemContentExt> listReturn = new ArrayList<>();
        List lstParam = new ArrayList<>();
        int i = 0;
        try {
            open();
            String strSQL = "SELECT cbi.content_Id, cbi.reason, cbi.issue_Time, cbi.list_Id, cbi.store_Id, (select name from CB_STORE where id = cbi.store_Id ) as name_store," +
                    " cbc.content_description " +
                    " FROM CB_ITEM_CONTENT cbi , CB_CONTENT cbc where cbi.content_Id = cbc.content_Id ";
            if (id != null) {
                strSQL += " and cbi.list_Id = ? ";
                lstParam.add(id);
            }
            if (keyWord != null && !keyWord.trim().isEmpty()) {
                strSQL += " and LOWER(cbc.content_description) like '%" + keyWord.toLowerCase() + "%' ";
            }
            strSQL += " order by cbi.issue_Time desc";

            mStmt = mConnection.prepareStatement(strSQL);
            if (lstParam != null && lstParam.size() > 0) {
                for (Object obj : lstParam) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                CBItemContentExt tmpCBItemContentExt = new CBItemContentExt();
                CBItemContent cbItemContent = new CBItemContent();
                cbItemContent.setContentId(mRs.getInt(1));
                cbItemContent.setReason(mRs.getString(2));
                cbItemContent.setIssueTime(mRs.getDate(3));
                cbItemContent.setListId(mRs.getLong(4));
                cbItemContent.setStoreId(mRs.getLong(5));
                tmpCBItemContentExt.setCbItemContent(cbItemContent);
                tmpCBItemContentExt.setNameStore(mRs.getString(6));
                tmpCBItemContentExt.setContentDescription(mRs.getString(7));
                listReturn.add(tmpCBItemContentExt);
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

    public void insertCBItemContentExtAll(CBItemContentExt dto) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "INSERT INTO Cb_Item_Content(content_Id, reason, issue_Time, list_Id";
            if(dto.getCbItemContent().getStoreId() != null) {
                strSQL += ", store_Id) VALUES(?,?,sysdate,?,?)";
            } else{
                strSQL += ") VALUES(?,?,sysdate,?)";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getCbItemContent().getContentId());
            mStmt.setString(2, dto.getCbItemContent().getReason());
            mStmt.setLong(3, dto.getCbItemContent().getListId());
            if(dto.getCbItemContent().getStoreId() != null) {
                mStmt.setLong(4, dto.getCbItemContent().getStoreId());
            }
            mStmt.execute();
            logAfterInsert("cb_Item_Content", "content_Id=" + dto.getCbItemContent().getContentId());
            //Done
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
