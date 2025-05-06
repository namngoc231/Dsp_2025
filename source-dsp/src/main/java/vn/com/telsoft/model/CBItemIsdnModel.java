/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import vn.com.telsoft.entity.CBItemIsdn;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hanv, TELSOFT
 */
public class CBItemIsdnModel extends AMDataPreprocessor implements Serializable {

    public List<CBItemIsdn> getCbItemIsdn(CBItemIsdn itemIsdn) throws Exception {
        List<CBItemIsdn> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT ISDN, REASON, ISSUE_TIME, LIST_ID, STORE_ID from CB_ITEM_ISDN where 1=1 ";

            if (itemIsdn.getIsdn() != null && !itemIsdn.getIsdn().equals("")) {
                strSQL += " and ISDN=? ";
                parameter.add(itemIsdn.getIsdn());
            }
//            if (itemIsdn.getReason() != null && !itemIsdn.getReason().equals("")) {
//                strSQL += " and lower(REASON) like ? ";
//                parameter.add("%" + itemIsdn.getReason().toLowerCase() + "%");
//            }
            if (itemIsdn.getListId() != null && itemIsdn.getListId() > 0) {
                strSQL += " and LIST_ID=? ";
                parameter.add(itemIsdn.getListId());
            }
//            if (itemIsdn.getStoreId() != null && itemIsdn.getListId()>0) {
//                strSQL += " and STORE_ID=? ";
//                parameter.add(itemIsdn.getStoreId());
//            }
            strSQL += " order by ISDN, ISSUE_TIME";
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 0;
            for (Object obj : parameter) {
                mStmt.setObject(++i, obj);
            }
            mRs = mStmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            while (mRs.next()) {
                CBItemIsdn item = new CBItemIsdn();
                item.setIsdn(mRs.getString(1));
                item.setReason(mRs.getString(2));
                item.setIssueTime(new Date(mRs.getTimestamp(3).getTime()));
                item.setListId(mRs.getLong(4));
                item.setStoreId(mRs.getLong(5));
                item.setIssueTimeString(sdf.format(item.getIssueTime()));
                listReturn.add(item);
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

    public void insert(CBItemIsdn itemIsdn) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);

            String strSQL = "insert into CB_ITEM_ISDN(ISDN,REASON,ISSUE_TIME,LIST_ID) values (?,?,sysdate,?)";
            if (itemIsdn.getStoreId() != 0)
                strSQL = "insert into CB_ITEM_ISDN(ISDN,REASON,ISSUE_TIME,LIST_ID,STORE_ID) values (?,?,sysdate,?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, itemIsdn.getIsdn());
            mStmt.setString(2, itemIsdn.getReason());
            mStmt.setLong(3, itemIsdn.getListId());
            if (itemIsdn.getStoreId() != 0)
                mStmt.setLong(4, itemIsdn.getStoreId());
            mStmt.execute();
            logAfterInsert("CB_ITEM_ISDN", "ISDN=" + itemIsdn.getIsdn());
            mConnection.commit();

        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(CBItemIsdn itemIsdn) throws Exception {
        try {
            open();

            String strSQL = "DELETE FROM CB_ITEM_ISDN WHERE ISDN=?";
            mConnection.setAutoCommit(false);

            logBeforeDelete("CB_ITEM_ISDN", "ISDN=" + itemIsdn.getIsdn());
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, itemIsdn.getIsdn());
            mStmt.execute();
            mConnection.commit();

        } catch (
                Exception ex) {
            mConnection.rollback();
            throw ex;

        } finally {
            close(mStmt);
            close(mConnection);
        }
    }
}
