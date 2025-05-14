package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DSPCompany;
import vn.com.telsoft.entity.DSPServicePrice;
import vn.com.telsoft.entity.W2gPromMapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class W2gPromMappingModel extends AMDataPreprocessor implements Serializable {

    public List<W2gPromMapping> getListW2gPromMapping() throws Exception {
        List<W2gPromMapping> listReturn = new ArrayList<>();
        try {
            open();
            String strSQL = "select m.id, m.com_id, c.com_name, m.profile_code, m.prom_code " +
                    "from dsp_w2g_prom_mapping m join dsp_company c on m.com_id = c.com_id " +
                    "order by m.prom_code";
            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                W2gPromMapping w2gPromMapping = new W2gPromMapping();
                w2gPromMapping.setId(mRs.getLong("ID"));
                w2gPromMapping.setComId(mRs.getLong("COM_ID"));
                w2gPromMapping.setComName(mRs.getString("COM_NAME"));
                w2gPromMapping.setProfileCode(mRs.getString("PROFILE_CODE"));
                w2gPromMapping.setPromCode(mRs.getString("PROM_CODE"));
                listReturn.add(w2gPromMapping);
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

    public void add(W2gPromMapping w2gPromMapping) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            long id = SQLUtil.getSequenceValue(mConnection, "DSP_W2G_PROM_MAPPING_SEQ");
            String strSQL = "insert into dsp_w2g_prom_mapping (id, com_id, profile_code, prom_code) values (?,?,?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, id);
            mStmt.setLong(2, w2gPromMapping.getComId());
            mStmt.setString(3, w2gPromMapping.getProfileCode());
            mStmt.setString(4, w2gPromMapping.getPromCode());
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

    public void edit(W2gPromMapping w2gPromMapping) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            String strSQL = "update dsp_w2g_prom_mapping set com_id = ?, profile_code=?, prom_code = ? where id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, w2gPromMapping.getComId());
            mStmt.setString(2, w2gPromMapping.getProfileCode());
            mStmt.setString(3, w2gPromMapping.getPromCode());
            mStmt.setLong(4, w2gPromMapping.getId());
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

    public void delete(List<W2gPromMapping> w2gPromMappingList) throws Exception {
        try {
            open();
            String strSQL = "delete from dsp_w2g_prom_mapping where id= ?";
            mConnection.setAutoCommit(false);
            for (W2gPromMapping w2gPromMapping : w2gPromMappingList) {
                mStmt = mConnection.prepareStatement(strSQL);
                mStmt.setLong(1, w2gPromMapping.getId());
                mStmt.execute();
            }
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            mConnection.setAutoCommit(true);
            close(mStmt);
            close(mConnection);
        }
    }

    public List<DSPCompany> getListDSPCompany(String comName) throws Exception {
        List<DSPCompany> listReturn = new ArrayList<>();
        try {
            open();
            boolean hasComName = comName != null && !comName.trim().isEmpty();
            String strSQL = "select com_id, com_name from dsp_company " +
                    "where status = 1 and type in (2,3) " +
                    (hasComName ? "and lower(a.com_name) like ? " : "") +
                    "order by com_name, com_id";
            mStmt = mConnection.prepareStatement(strSQL);
            if (hasComName) {
                mStmt.setString(1, "%" + comName.toLowerCase() + "%");
            }
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPCompany dspCompany = new DSPCompany();
                dspCompany.setComId(mRs.getLong("COM_ID"));
                dspCompany.setComName(mRs.getString("COM_NAME"));
                listReturn.add(dspCompany);
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

    public List<DSPServicePrice> getListProfileCode(Long comId) throws Exception {
        List<DSPServicePrice> listReturn = new ArrayList<>();
        if (comId == null) {
            return listReturn;
        }
        try {
            open();
            String strSQL = "select name " +
                    "from dsp_service_price " +
                    "where tab_id = nvl((select a.tab_id " +
                    "                    from dsp_com_price a, " +
                    "                         dsp_service_price_tab b " +
                    "                    where a.com_id = ? " +
                    "                      and a.tab_id = b.tab_id " +
                    "                      and b.service_id = 77 " +
                    "                      and a.status = 1 " +
                    "                      and start_time <= sysdate " +
                    "                      and (end_time is null or sysdate < end_time + 1) " +
                    "                      and b.status = 1), (select tab_id from (select tab_id " +
                    "                                          from dsp_service_price_tab " +
                    "                                          where def = 1 " +
                    "                                            and status = 1 " +
                    "                                            and service_id = 77 " +
                    "                                            and start_time <= sysdate " +
                    "                                            and (end_time is null or sysdate < end_time + 1) " +
                    "                                            order by (sysdate - nvl(start_time, to_date('1990-01-01','yyyy-MM-dd'))), " +
                    "                                                     (nvl(end_time, to_date('2100-12-31','yyyy-MM-dd')) - sysdate)) " +
                    "                                                        where rownum = 1)) " +
                    "order by name";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, comId);
            mRs = mStmt.executeQuery();
            while (mRs.next()) {
                DSPServicePrice dspServicePrice = new DSPServicePrice();
                dspServicePrice.setName(mRs.getString("NAME"));
                listReturn.add(dspServicePrice);
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
