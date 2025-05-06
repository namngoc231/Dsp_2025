package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import smartlib.database.Database;
import smartlib.database.batch.BatchError;
import smartlib.database.batch.BatchField;
import smartlib.database.batch.BatchStatement;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PackageImportFileModel extends AMDataPreprocessor implements Serializable {

    public List<DSPCompany> getListCompany(Long userId) throws Exception {
        List<DSPCompany> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = " select com_id, com_name, type from dsp_company where status = 1 " +
                            " connect by prior com_id = parent_id " +
                            " start with com_id in (select com_id from dsp_company where user_id = ?) " +
                            " order by com_name ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPCompany tmpDSP = new DSPCompany();
                tmpDSP.setComId(mRs.getLong(1));
                tmpDSP.setComName(mRs.getString(2));
                tmpDSP.setType(mRs.getLong(3));
                listReturn.add(tmpDSP);
            }
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listReturn;
    }

    public List<String> insertWhiteList(Long comId, List<String> mlistReq) throws Exception {
        long count = 0, success = 0, error = 0, batchSize = 1000;
        List<String> failedRecords = new ArrayList<>();
        String[] params;
        BatchStatement bstmt = null;

        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "insert into dsp_white_list(com_id, isdn, package_code) values (?, ?, ?)";
            bstmt = new BatchStatement(mConnection);
            bstmt.prepareStatement(strSQL);
            for (String str : mlistReq) {

                count++;
                // check loi
                params = str.split(",");
                if (params.length != 2 || params[0].isEmpty() || params[1].isEmpty()) {
                    error++;
                    continue;
                }
                if (!Utils.validateIsdnSub(Utils.formatISDN(params[0]))) {
                    error++;
                    continue;
                }

                bstmt.setLong(1, comId);
                bstmt.setString(2, Utils.formatISDN(params[0]));
                bstmt.setString(3, params[1]);
                bstmt.addBatch();
                success++;
                if (count % batchSize == 0 && count > 0) {
                    bstmt.resetCounter();
                    Vector vtError = bstmt.executeBatch();
                    if (!vtError.isEmpty()) {
                        error += vtError.size();
                        success = success - vtError.size();
                        processError(vtError, failedRecords);
                    }
                    bstmt.clearError();
                }
            }
            bstmt.resetCounter();
            Vector vtError = bstmt.executeBatch();
            if (!vtError.isEmpty()) {
                error += vtError.size();
                success = success - vtError.size();
                processError(vtError, failedRecords);
            }
            bstmt.clearError();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            Database.closeObject(bstmt);
            close();
        }
        failedRecords.add(0,"Thành công: " + success + " thất bại: " + error);
        return failedRecords;
    }

    public void processError(Vector vtError, List<String> failedRecords) {
        StringBuilder strLog = new StringBuilder("Có lỗi xảy ra\nChi tiết:");
        for (Object o : vtError) {
            BatchError batErrorElement = (BatchError) o;
            Vector vtDataError = (Vector) batErrorElement.getErrorData();
            BatchField btISDNValue = (BatchField) vtDataError.get(1);
            strLog.append("\n\tISDN: ")
                    .append(btISDNValue.getFieldValue())
                    .append("\n\tMô tả: ")
                    .append(batErrorElement.getFullMessage().indexOf("ORA-00001") > 0 ? "Dữ liệu đã tồn tại" : batErrorElement.getFullMessage());
        }
        failedRecords.add(strLog.toString());
    }
}
