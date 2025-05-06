package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.AppStoreData;
import vn.com.telsoft.entity.CBStore;
import vn.com.telsoft.entity.CBStoreAttr;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppStoreDataModel extends AMDataPreprocessor implements Serializable {

    public List<AppStoreData> getAllStore() throws Exception {
        List<AppStoreData> listRT = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String strSQL = "select a.id, a.store_code, a.name, a.status, a.description, a.yearly_limit, a.monthly_limit," +
                " a.weekly_limit, a.daily_limit, a.trans_limit from cb_store a order by store_code";
        try {
            open();
            stmt = mConnection.prepareStatement(strSQL);
            rs = stmt.executeQuery();
            while (rs.next()) {
                AppStoreData ent = new AppStoreData();
                CBStore ent1 = new CBStore();
                ent1.setId(rs.getLong(1));
                ent1.setStoreCode(rs.getString(2));
                ent1.setName(rs.getString(3));
                ent1.setStatus(rs.getLong(4));
                ent1.setDescription(rs.getString(5));
                Object yearlyLimit = rs.getObject(6);
                if (yearlyLimit == null) {
                    ent1.setYearlyLimit(null);
                } else {
                    ent1.setYearlyLimit(Long.parseLong(yearlyLimit.toString()));
                }
                Object monthlyLimit = rs.getObject(7);
                if (monthlyLimit == null) {
                    ent1.setMonthlyLimit(null);
                } else {
                    ent1.setMonthlyLimit(Long.parseLong(monthlyLimit.toString()));
                }
                Object weeklyLimit = rs.getObject(8);
                if (weeklyLimit == null) {
                    ent1.setWeeklyLimit(null);
                } else {
                    ent1.setWeeklyLimit(Long.parseLong(weeklyLimit.toString()));
                }
                Object dailyLimit = rs.getObject(9);
                if (dailyLimit == null) {
                    ent1.setDailyLimit(null);
                } else {
                    ent1.setDailyLimit(Long.parseLong(dailyLimit.toString()));
                }
                Object transLimit = rs.getObject(10);
                if (transLimit == null) {
                    ent1.setTransLimit(null);
                } else {
                    ent1.setTransLimit(Long.parseLong(transLimit.toString()));
                }
                ent.setCbStore(ent1);
                listRT.add(ent);
            }
        } catch (Exception ex) {
            Logger.getLogger(AppStoreDataModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            close(rs);
            close(stmt);
            close();
        }
        return listRT;
    }

    public List<AppStoreData> getAllStoreAttrById(long storeId) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<AppStoreData> listRT = new ArrayList<>();
        String strSQL = "select a.attr_id, a.name, a.value from cb_store_attr a where a.store_id = ? order by name";
        try {
            open();
            stmt = mConnection.prepareStatement(strSQL);
            stmt.setLong(1, storeId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                AppStoreData ent = new AppStoreData();
                CBStoreAttr ent1 = new CBStoreAttr();
                ent1.setAttrID(rs.getLong(1));
                ent1.setName(rs.getString(2));
                ent1.setValue((rs.getString(3)));
                ent.setCbStoreAttr(ent1);
                listRT.add(ent);
            }
        } catch (Exception ex) {
            Logger.getLogger(AppStoreDataModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            close(rs);
            close(stmt);
            close();
        }
        return listRT;
    }

    public AppStoreData doInsertCopy(AppStoreData entity, List<AppStoreData> lsAttr) throws Exception {
        PreparedStatement stmtStore = null;
        PreparedStatement stmtAttr = null;
        String strSqlStore = "insert into cb_store(id, store_code, name, description,status, yearly_limit, monthly_limit, weekly_limit, daily_limit,trans_limit)" +
                "values(?, ? ,?, ?,?,?,?,?,?,?)";
        String strSqlAttr = "insert into cb_store_attr(attr_id, store_id, name, value) values(?,?,?,?)";
        try {
            open();
            stmtStore = mConnection.prepareStatement(strSqlStore);
            int i = 1;
            long id = SQLUtil.getSequenceValue(mConnection, "seq_cb_store");
            stmtStore.setLong(i++, id);
            stmtStore.setString(i++, entity.getCbStore().getStoreCode());
            stmtStore.setString(i++, entity.getCbStore().getName());
            stmtStore.setString(i++, entity.getCbStore().getDescription());
            stmtStore.setLong(i++, entity.getCbStore().getStatus());
            if (entity.getCbStore().getYearlyLimit() == null)
                stmtStore.setNull(i++, java.sql.Types.NULL);
            else
                stmtStore.setLong(i++, entity.getCbStore().getYearlyLimit());
            if (entity.getCbStore().getMonthlyLimit() == null)
                stmtStore.setNull(i++, java.sql.Types.NULL);
            else
                stmtStore.setLong(i++, entity.getCbStore().getMonthlyLimit());
            if (entity.getCbStore().getWeeklyLimit() == null)
                stmtStore.setNull(i++, java.sql.Types.NULL);
            else
                stmtStore.setLong(i++, entity.getCbStore().getWeeklyLimit());
            if (entity.getCbStore().getDailyLimit() == null)
                stmtStore.setNull(i++, java.sql.Types.NULL);
            else
                stmtStore.setLong(i++, entity.getCbStore().getDailyLimit());
            if (entity.getCbStore().getTransLimit() == null)
                stmtStore.setNull(i++, java.sql.Types.NULL);
            else
                stmtStore.setLong(i++, entity.getCbStore().getTransLimit());
            stmtStore.executeUpdate();
            entity.getCbStore().setId(id);

            for (AppStoreData values : lsAttr) {
                long attrID = SQLUtil.getSequenceValue(mConnection, "seq_cb_store_attr");
                stmtAttr = mConnection.prepareStatement(strSqlAttr);
                int j = 1;
                stmtAttr.setLong(j++, attrID);
                stmtAttr.setLong(j++, id);
                stmtAttr.setString(j++, values.getCbStoreAttr().getName());
                stmtAttr.setString(j++, values.getCbStoreAttr().getValue());
                stmtAttr.executeUpdate();
            }

        } catch (Exception ex) {
            Logger.getLogger(AppStoreDataModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            close(stmtStore);
            close(stmtAttr);
            close();
        }
        return entity;
    }

    public AppStoreData doUpdate(AppStoreData entity, List<AppStoreData> lsInsert, List<AppStoreData> lsUpdate, List<AppStoreData> lsDelete) throws Exception {
        PreparedStatement stmt = null;
        String strSqlStore = "update cb_store set store_code = ?, name = ?, description = ?, status = ? " +
                ", yearly_limit = ?, monthly_limit = ?, weekly_limit = ?, daily_limit = ?,trans_limit = ?" +
                " where id = ?";
        String strSqlInsertAttr = "insert into cb_store_attr(attr_id, store_id, name, value) values(?,?,?,?)";
        String strSqlUpdateAttr = "update cb_store_attr set name = ? , value = ? where attr_id = ?";
        String strSqlDeleteAttr = "delete cb_store_attr where attr_id = ?";
        try {
            open();
            stmt = mConnection.prepareStatement(strSqlStore);
            int i = 1;
            stmt.setString(i++, entity.getCbStore().getStoreCode());
            stmt.setString(i++, entity.getCbStore().getName());
            stmt.setString(i++, entity.getCbStore().getDescription());
            stmt.setLong(i++, entity.getCbStore().getStatus());
            if (entity.getCbStore().getYearlyLimit() == null)
                stmt.setNull(i++, java.sql.Types.NULL);
            else
                stmt.setLong(i++, entity.getCbStore().getYearlyLimit());
            if (entity.getCbStore().getMonthlyLimit() == null)
                stmt.setNull(i++, java.sql.Types.NULL);
            else
                stmt.setLong(i++, entity.getCbStore().getMonthlyLimit());
            if (entity.getCbStore().getWeeklyLimit() == null)
                stmt.setNull(i++, java.sql.Types.NULL);
            else
                stmt.setLong(i++, entity.getCbStore().getWeeklyLimit());
            if (entity.getCbStore().getDailyLimit() == null)
                stmt.setNull(i++, java.sql.Types.NULL);
            else
                stmt.setLong(i++, entity.getCbStore().getDailyLimit());
            if (entity.getCbStore().getTransLimit() == null)
                stmt.setNull(i++, java.sql.Types.NULL);
            else
                stmt.setLong(i++, entity.getCbStore().getTransLimit());
            stmt.setLong(i++, entity.getCbStore().getId());
            stmt.executeUpdate();

            if (!lsUpdate.isEmpty()) {
                stmt = mConnection.prepareStatement(strSqlUpdateAttr);
                for (AppStoreData values : lsUpdate) {
                    int j = 1;
                    stmt.setString(j++, values.getCbStoreAttr().getName());
                    stmt.setString(j++, values.getCbStoreAttr().getValue());
                    stmt.setLong(j++, values.getCbStoreAttr().getAttrID());
                    stmt.executeUpdate();
                }
            }

            if (!lsDelete.isEmpty()) {
                stmt = mConnection.prepareStatement(strSqlDeleteAttr);
                for (AppStoreData values : lsDelete) {
                    stmt.setLong(1, values.getCbStoreAttr().getAttrID());
                    stmt.executeUpdate();
                }
            }
            if (!lsInsert.isEmpty()) {
                stmt = mConnection.prepareStatement(strSqlInsertAttr);
                for (AppStoreData values : lsInsert) {
                    long attrID = SQLUtil.getSequenceValue(mConnection, "seq_cb_store_attr");
                    int q = 1;
                    stmt.setLong(q++, attrID);
                    stmt.setLong(q++, entity.getCbStore().getId());
                    stmt.setString(q++, values.getCbStoreAttr().getName());
                    stmt.setString(q++, values.getCbStoreAttr().getValue());
                    stmt.executeUpdate();
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(AppStoreDataModel.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            close(stmt);
            close();
        }
        return entity;
    }

    public boolean checkData(String strName, Long storeId) throws Exception {
        try {
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String sql = "select * from cb_store_attr where name = ? and store_id = ?";
            open();
            stmt = mConnection.prepareStatement(sql);
            stmt.setString(1, strName);
            stmt.setLong(2, storeId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }
        return false;
    }
}
