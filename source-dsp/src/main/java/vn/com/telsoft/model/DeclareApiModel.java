package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DeclareApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeclareApiModel extends AMDataPreprocessor implements Serializable {
    public List<DeclareApi> getlistDeclareApi() throws Exception {
        List<DeclareApi> listdDeclareApis = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT api_id, api_name, api_path, description, status FROM API ORDER BY api_id";

            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DeclareApi tmpDeclareApi = new DeclareApi();
                tmpDeclareApi.setApiId(mRs.getLong("api_id"));
                tmpDeclareApi.setApiName(mRs.getString("api_name"));
                tmpDeclareApi.setApiPath(mRs.getString("api_path"));
                tmpDeclareApi.setDescription(mRs.getString("description"));
                tmpDeclareApi.setStatus(mRs.getLong("status"));
                listdDeclareApis.add(tmpDeclareApi);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listdDeclareApis;
    }

    public void insert(DeclareApi declareApi) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "INSERT INTO api(api_id, api_name, api_path, description, status)"
                    + "VALUES(?, ?, ?, ?, ?)";
            Long id = SQLUtil.getSequenceValue(mConnection, "API_SEQ");
            declareApi.setApiId(id);
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, id);
            mStmt.setString(2, declareApi.getApiName());
            mStmt.setString(3, declareApi.getApiPath());
            mStmt.setString(4, declareApi.getDescription());
            mStmt.setLong(5, declareApi.getStatus());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DeclareApi declareApi) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "UPDATE api SET api_name = ?, api_path = ?, description = ?, status = ? WHERE api_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, declareApi.getApiName());
            mStmt.setString(2, declareApi.getApiPath());
            mStmt.setString(3, declareApi.getDescription());
            mStmt.setLong(4, declareApi.getStatus());
            mStmt.setLong(5, declareApi.getApiId());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void delete(DeclareApi declareApi) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "DELETE FROM api WHERE api_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, declareApi.getApiId());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public List<DeclareApi> getListApiAllByUserId(Long userId) throws Exception {
        List<DeclareApi> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "SELECT api_Id, api_Name, api_Path, DESCRIPTION, STATUS FROM API where api_Id in ( select api_Id from api_user where user_id = ?) ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DeclareApi api = new DeclareApi();
                api.setApiId(mRs.getLong(1));
                api.setApiName(mRs.getString(2));
                api.setApiPath(mRs.getString(3));
                api.setDescription(mRs.getString(4));
                api.setStatus(mRs.getLong(5));
                listReturn.add(api);
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

    public List<DeclareApi> getListApiAllByNotUserId(Long userId) throws Exception {
        List<DeclareApi> listReturn = new ArrayList<>();

        try {
            open();
            String strSQL = "SELECT api_Id, api_Name, api_Path, DESCRIPTION, STATUS FROM API where api_Id not in ( select api_Id from api_user where user_id = ?) and status = 1 ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DeclareApi api = new DeclareApi();
                api.setApiId(mRs.getLong(1));
                api.setApiName(mRs.getString(2));
                api.setApiPath(mRs.getString(3));
                api.setDescription(mRs.getString(4));
                api.setStatus(mRs.getLong(5));
                listReturn.add(api);
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

    public long getTransactionId() throws Exception {
        long id = 0;
        try {
            open();
            mConnection.setAutoCommit(false);
            id = SQLUtil.getSequenceValue(mConnection, "SEQ_SUSPEND_DC");
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
        return id;
    }
}
