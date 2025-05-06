/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.SystemConfig;
import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.admin.gui.entity.UserDTL;
import com.faplib.lib.util.SQLUtil;
import com.faplib.util.DateUtil;
import com.faplib.util.StringUtil;
import vn.com.telsoft.entity.DSPCompany;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HoangNH
 */
public class DSPCompanyModel extends AMDataPreprocessor implements Serializable {
    DSPMtQueueModel dspMtQueueModel = new DSPMtQueueModel();

    public List<DSPCompany> searchCompany(DSPCompany dspCompany) throws Exception {
        List<DSPCompany> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();

        try {
            open();
            String strSQL = " SELECT COM_ID, COM_NAME, status, BUS_CODE, ADDRESS, PARENT_ID, USER_ID, TYPE, PROVINCE,CITY,DISTRICT,WARD, DECODE(api_user_id, 0, null, api_user_id) api_user_id, " +
                    " rep_position, description, updated_key, email, rep_mobile, rep_phone, tax_code, vas_mobile, public_key, representative, " +
                    " file_path, cps_mobile, SERIAL_PREFIX, API_PUBLIC_KEY, " +
                    "(SELECT user_name from am_user a where b.user_id = a.user_id ) user_name, " +
                    " (SELECT user_name from am_user a where b.api_user_id = a.user_id ) api_user_name, api_updated_key, bhtt_code, cust_type " +
                    " FROM DSP_COMPANY b where 1=1 ";
            if (dspCompany != null) {
                if (dspCompany.getComId() != null) {
                    strSQL += " and COM_ID=? ";
                    parameter.add(dspCompany.getComId());
                }
                if (dspCompany.getComName() != null && !dspCompany.getComName().trim().equals("")) {
                    strSQL += " and lower(FN_CONVERT_TO_VN(COM_NAME)) like ? ";
                    parameter.add("%" + StringUtil.removeSign(dspCompany.getComName().toLowerCase()) + "%");
                }
                if (dspCompany.getShowAddress() != null && !dspCompany.getShowAddress().trim().equals("") && !dspCompany.getShowAddress().trim().equals(",")) {
                    strSQL += " and lower(FN_CONVERT_TO_VN(address || ', ' || WARD || ', ' || DISTRICT || ', ' || CITY || ', ' || PROVINCE)) like ? ";
                    parameter.add("%" + StringUtil.removeSign(dspCompany.getShowAddress().toLowerCase()) + "%");
                }
                if (dspCompany.getBusCode() != null && !dspCompany.getBusCode().trim().equals("")) {
                    strSQL += " and BUS_CODE like ? ";
                    parameter.add("%" + dspCompany.getBusCode() + "%");
                }
                strSQL += " and PARENT_ID = ? ";
                parameter.add(dspCompany.getParentId());
                strSQL += " and status = '1' ";
            }
            strSQL += " order by COM_ID ";
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 0;
            for (Object obj : parameter) {
                mStmt.setObject(++i, obj);
            }
            mRs = mStmt.executeQuery();

            while (this.mRs.next()) {
                DSPCompany tmpCom = new DSPCompany();
                tmpCom.setComId(this.mRs.getLong("COM_ID"));
                tmpCom.setComName(this.mRs.getString("com_name"));
                tmpCom.setStatus(this.mRs.getString("status"));
                tmpCom.setType(this.mRs.getLong("type"));
                tmpCom.setCity(this.mRs.getString("city"));
                tmpCom.setDistrict(this.mRs.getString("district"));
                tmpCom.setRepPosition(this.mRs.getString("rep_position"));
                tmpCom.setUpdatedKey(this.mRs.getDate("updated_key"));
                tmpCom.setDescription(this.mRs.getString("description"));
                tmpCom.setEmail(this.mRs.getString("email"));
                tmpCom.setWard(this.mRs.getString("ward"));
                tmpCom.setRepresentative(this.mRs.getString("representative"));
                tmpCom.setTaxCode(this.mRs.getString("tax_code"));
                tmpCom.setRepMobile(this.mRs.getString("rep_mobile"));
                tmpCom.setAddress(this.mRs.getString("address"));
                tmpCom.setProvince(this.mRs.getString("province"));
                tmpCom.setFilePath(this.mRs.getString("file_path"));
                tmpCom.setPublicKey(this.mRs.getString("public_key"));
                tmpCom.setRepPhone(this.mRs.getString("rep_phone"));
                tmpCom.setUserId(this.mRs.getLong("user_id"));
                tmpCom.setSerialPrefix(this.mRs.getString("SERIAL_PREFIX"));
                tmpCom.setBusCode(this.mRs.getString("bus_code"));
                tmpCom.setApiUserId(this.mRs.getObject("API_USER_ID") == null ? null : this.mRs.getLong("API_USER_ID"));
                tmpCom.setApiPublicKey(this.mRs.getString("API_PUBLIC_KEY"));
                tmpCom.setUserName(this.mRs.getString("user_name"));
                tmpCom.setVasMobile(this.mRs.getString("vas_mobile"));
                tmpCom.setCpsMobile(this.mRs.getString("cps_mobile"));
                tmpCom.setApiUserName(this.mRs.getString("api_user_name"));
                tmpCom.setApiUpdatedKey(this.mRs.getDate("api_updated_key"));
                tmpCom.setBhttCode(this.mRs.getString("bhtt_code"));
                tmpCom.setCustType(this.mRs.getString("cust_type"));
                listReturn.add(tmpCom);
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

    public List<DSPCompany> getDSPCompanyTree(Long parentID) throws Exception {
        ArrayList returnValue = new ArrayList();

        try {
            this.open();
            String strSQL = "SELECT\n" +
                    "    level lv,\n" +
                    "    a.com_id,\n" +
                    "    a.com_name,\n" +
                    "    a.type,\n" +
                    "    a.status,\n" +
                    "    a.user_id,\n" +
                    "    nvl(\n" +
                    "        a.parent_id,\n" +
                    "        0\n" +
                    "    ) parent_id,\n" +
                    "    (\n" +
                    "        SELECT\n" +
                    "            COUNT(1)\n" +
                    "        FROM\n" +
                    "            dsp_company\n" +
                    "        WHERE\n" +
                    "            parent_id = a.com_id\n" +
                    "    ) count_child\n" +
                    "FROM\n" +
                    "    dsp_company a\n" +
                    "WHERE\n" +
                    "   status = '1' and level <= 2\n" +
                    "CONNECT BY\n" +
                    "    PRIOR com_id = parent_id\n" +
                    "START WITH com_id = ?";


            this.mStmt = this.mConnection.prepareStatement(strSQL);
            this.mStmt.setLong(1, parentID);
            this.mRs = this.mStmt.executeQuery();

            long fakeId = -1l;
            while (this.mRs.next()) {
                DSPCompany tmpCompany = new DSPCompany();
                tmpCompany.setComId(this.mRs.getLong("COM_ID"));
                tmpCompany.setComName(this.mRs.getString("com_name"));
                tmpCompany.setStatus(this.mRs.getString("status"));
                tmpCompany.setType(this.mRs.getLong("type"));
                tmpCompany.setParentId(this.mRs.getLong("parent_id"));
                tmpCompany.setLevel(this.mRs.getInt("lv"));
                tmpCompany.setUserId(this.mRs.getLong("user_id"));

                returnValue.add(tmpCompany);

                //Add fake child
                if (mRs.getLong("lv") != 1 && mRs.getInt("count_child") != 0) {
                    tmpCompany = new DSPCompany();
                    tmpCompany.setStatus("1");
                    tmpCompany.setComId(fakeId--);
                    tmpCompany.setParentId(this.mRs.getLong("com_id"));
                    tmpCompany.setComName("fake");
                    returnValue.add(tmpCompany);
                }
            }
        } finally {
            this.close(this.mRs);
            this.close(this.mStmt);
            this.close();
        }

        return returnValue;
    }


    public List<DSPCompany> getNodeList(Long com_id) throws Exception {
        ArrayList returnValue = new ArrayList();

        try {
            this.open();
            String strSQL = "SELECT COM_ID, COM_NAME, STATUS, TYPE, PARENT_ID, USER_ID, REP_POSITION, WARD, DISTRICT, CITY, PROVINCE, DESCRIPTION, " +
                    "UPDATED_KEY, EMAIL, REP_MOBILE, REP_PHONE, TAX_CODE, bus_code, address, vas_mobile, public_key, representative, file_path, " +
                    "cps_mobile, SERIAL_PREFIX, DECODE(api_user_id, 0, null, api_user_id) api_user_id, API_PUBLIC_KEY, " +
                    "(SELECT user_name from am_user a where b.user_id = a.user_id ) user_name, " +
                    "(SELECT user_name from am_user a where b.api_user_id = a.user_id ) api_user_name, API_UPDATED_KEY, bhtt_code, cust_type " +
                    "      FROM dsp_company b  \n" +
                    "      where parent_id = ? and status = '1' " +
                    " order by COM_ID ";
            this.mStmt = this.mConnection.prepareStatement(strSQL);
            this.mStmt.setLong(1, com_id);

            this.mRs = this.mStmt.executeQuery();

            while (this.mRs.next()) {
                DSPCompany tmpCom = new DSPCompany();
                tmpCom.setComId(this.mRs.getLong("COM_ID"));
                tmpCom.setComName(this.mRs.getString("com_name"));
                tmpCom.setStatus(this.mRs.getString("status"));
                tmpCom.setType(this.mRs.getLong("type"));
                tmpCom.setParentId(this.mRs.getLong("parent_id"));
                tmpCom.setUserId(this.mRs.getLong("user_id"));
                tmpCom.setRepPosition(this.mRs.getString("rep_position"));
                tmpCom.setWard(this.mRs.getString("ward"));
                tmpCom.setDistrict(this.mRs.getString("district"));
                tmpCom.setCity(this.mRs.getString("city"));
                tmpCom.setProvince(this.mRs.getString("province"));
                tmpCom.setDescription(this.mRs.getString("description"));
                tmpCom.setUpdatedKey(this.mRs.getDate("updated_key"));
                tmpCom.setEmail(this.mRs.getString("email"));
                tmpCom.setRepMobile(this.mRs.getString("rep_mobile"));
                tmpCom.setRepPhone(this.mRs.getString("rep_phone"));
                tmpCom.setTaxCode(this.mRs.getString("tax_code"));
                tmpCom.setBusCode(this.mRs.getString("bus_code"));
                tmpCom.setAddress(this.mRs.getString("address"));
                tmpCom.setVasMobile(this.mRs.getString("vas_mobile"));
                tmpCom.setPublicKey(this.mRs.getString("public_key"));
                tmpCom.setRepresentative(this.mRs.getString("representative"));
                tmpCom.setFilePath(this.mRs.getString("file_path"));
                tmpCom.setCpsMobile(this.mRs.getString("cps_mobile"));
                tmpCom.setSerialPrefix(this.mRs.getString("SERIAL_PREFIX"));
                tmpCom.setApiUserId(this.mRs.getObject("API_USER_ID") == null ? null : this.mRs.getLong("API_USER_ID"));
                tmpCom.setApiPublicKey(this.mRs.getString("API_PUBLIC_KEY"));
                tmpCom.setUserName(this.mRs.getString("user_name"));
                tmpCom.setApiUserName(this.mRs.getString("api_user_name"));
                tmpCom.setApiUpdatedKey(this.mRs.getDate("api_updated_key"));
                tmpCom.setBhttCode(this.mRs.getString("bhtt_code"));
                tmpCom.setCustType(this.mRs.getString("cust_type"));
                returnValue.add(tmpCom);
            }
        } finally {
            this.close(this.mRs);
            this.close(this.mStmt);
            this.close();
        }

        return returnValue;
    }

    public long add(DSPCompany company, UserDTL user, UserDTL userAPI) throws Exception {
        long idFromSeq;

        try {
            open();

            mConnection.setAutoCommit(false);
            addAmUser(user, mConnection, "RETAILER_GROUP_ID");
            company.setUserId(user.getUserId());
            if (userAPI != null) {
                addAmUser(userAPI, mConnection, "API_GROUP_ID");
                company.setApiUserId(userAPI.getUserId());
            } else {
                company.setApiUserId(null);
            }

            //Update app
            idFromSeq = SQLUtil.getSequenceValue(mConnection, "COMPANY_SEQ");
            company.setComId(idFromSeq);

            String strSQL = "INSERT INTO DSP_COMPANY(COM_ID, COM_NAME, TAX_CODE, BUS_CODE, ADDRESS, VAS_MOBILE, REPRESENTATIVE,REP_PHONE,REP_MOBILE,REP_POSITION,EMAIL," +
                    "PUBLIC_KEY,UPDATED_KEY,USER_ID,PARENT_ID,STATUS,DESCRIPTION,TYPE,PROVINCE,CITY,DISTRICT,WARD,FILE_PATH,CPS_MOBILE,SERIAL_PREFIX,API_PUBLIC_KEY,API_USER_ID,API_UPDATED_KEY,BHTT_CODE,CUST_TYPE) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, idFromSeq);
            mStmt.setString(2, company.getComName().trim());
            mStmt.setString(3, company.getTaxCode().trim());
            mStmt.setString(4, company.getBusCode().trim());
            mStmt.setString(5, company.getAddress().trim());
            mStmt.setString(6, company.getVasMobile().trim());
            mStmt.setString(7, company.getRepresentative().trim());
            mStmt.setString(8, company.getRepPhone().trim());
            mStmt.setString(9, company.getRepMobile().trim());
            mStmt.setString(10, company.getRepPosition().trim());
            mStmt.setString(11, company.getEmail().trim());
            mStmt.setString(12, company.getPublicKey().trim());
            mStmt.setDate(13, (DateUtil.getSqlDate(company.getUpdatedKey())));
            mStmt.setLong(14, company.getUserId());
            mStmt.setLong(15, company.getParentId());
            mStmt.setString(16, company.getStatus().trim());
            mStmt.setString(17, company.getDescription().trim());
            mStmt.setLong(18, company.getType());
            mStmt.setString(19, company.getProvince());
            mStmt.setString(20, company.getCity());
            mStmt.setString(21, company.getDistrict());
            mStmt.setString(22, company.getWard());
            mStmt.setString(23, company.getFilePath());
            mStmt.setString(24, company.getCpsMobile());
            mStmt.setString(25, company.getSerialPrefix());
            mStmt.setString(26, company.getApiPublicKey().trim());
            if (company.getApiUserId() == null)
                mStmt.setNull(27, 0);
            else
                mStmt.setLong(27, company.getApiUserId());
            mStmt.setDate(28, (DateUtil.getSqlDate(company.getApiUpdatedKey())));
            mStmt.setString(29, company.getBhttCode());
            mStmt.setString(30, company.getCustType());
            mStmt.execute();
            //Done
            //send SMS
            if ("1".equals(StringUtil.fix(SystemConfig.getConfig("SendSMS")))
                    && !"".equals(StringUtil.fix(company.getVasMobile()))) {
                if (company.getType() == 2L || company.getType() == 3L) {
                    String[] paramOutput = {company.getComName(), company.getBusCode()};
                    dspMtQueueModel.addSMS("DK_COMPANY", company.getVasMobile(), paramOutput, mConnection);
                }
            }
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;

        } finally {
            close(mStmt);
            close();
        }
        return idFromSeq;
    }

    public void edit(DSPCompany company, UserDTL user, UserDTL userAPI) throws Exception {

        try {
            open();
            mConnection.setAutoCommit(false);
            if (userAPI != null) {
                if (userAPI.getUserId() == 0) {
                    addAmUser(userAPI, mConnection, "API_GROUP_ID");
                    company.setApiUserId(userAPI.getUserId());
                }
                if (userAPI.getPassword() != null)
                    editPasswordUser(userAPI, mConnection);
            }
            if (user.getPassword() != null)
                editPasswordUser(user, mConnection);
            if (company.getStatus().equals("0"))
                editUser(user, mConnection);

            //Update app
            List listCompany = logBeforeUpdate("DSP_COMPANY", "COM_ID=" + company.getComId());
            String strSQL = "UPDATE DSP_COMPANY SET  COM_NAME =?, TAX_CODE =?, BUS_CODE =?, ADDRESS =?, VAS_MOBILE =?, REPRESENTATIVE =?,REP_PHONE =?,REP_MOBILE =?, " +
                    "                    REP_POSITION =?, EMAIL =?, PUBLIC_KEY =?, UPDATED_KEY =?, USER_ID =?, PARENT_ID =?, STATUS =?, DESCRIPTION =?, " +
                    " TYPE =?, PROVINCE =?, CITY =?, DISTRICT =?, WARD =?, FILE_PATH= ?, CPS_MOBILE=?, SERIAL_PREFIX=?, API_USER_ID=?, API_PUBLIC_KEY=?, API_UPDATED_KEY=?, BHTT_CODE=?" +
                    " WHERE COM_ID=? ";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, company.getComName().trim());
            mStmt.setString(2, company.getTaxCode().trim());
            mStmt.setString(3, company.getBusCode().trim());
            mStmt.setString(4, company.getAddress().trim());
            mStmt.setString(5, company.getVasMobile().trim());
            mStmt.setString(6, company.getRepresentative().trim());
            mStmt.setString(7, company.getRepPhone().trim());
            mStmt.setString(8, company.getRepMobile().trim());
            mStmt.setString(9, company.getRepPosition().trim());
            mStmt.setString(10, company.getEmail().trim());
            mStmt.setString(11, company.getPublicKey().trim());
            mStmt.setDate(12, (DateUtil.getSqlDate(company.getUpdatedKey())));
            mStmt.setLong(13, company.getUserId());
            mStmt.setLong(14, company.getParentId());
            mStmt.setString(15, company.getStatus().trim());
            mStmt.setString(16, company.getDescription().trim());
            mStmt.setLong(17, company.getType());
            mStmt.setString(18, company.getProvince());
            mStmt.setString(19, company.getCity());
            mStmt.setString(20, company.getDistrict());
            mStmt.setString(21, company.getWard());
            mStmt.setString(22, company.getFilePath());
            mStmt.setString(23, company.getCpsMobile());
            mStmt.setString(24, company.getSerialPrefix());
            if (company.getApiUserId() == null)
                mStmt.setNull(25, 0);
            else
                mStmt.setLong(25, company.getApiUserId());
            mStmt.setString(26, company.getApiPublicKey().trim());
            mStmt.setDate(27, (DateUtil.getSqlDate(company.getApiUpdatedKey())));
            mStmt.setString(28, company.getBhttCode());
            mStmt.setLong(29, company.getComId());
            mStmt.execute();
            //Commit
            mConnection.commit();

        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;

        } finally {
            close(mStmt);
            close();
        }
    }

    public UserDTL getAmUser(UserDTL userDTL) throws Exception {
        try {
            List<Object> parameter = new ArrayList<>();
            open();
            String strSQL = " SELECT USER_ID, USER_NAME FROM AM_USER where 1=1 and lower(user_name) like ? ";
            parameter.add("%" + userDTL.getUserName().toLowerCase() + "%");
            mStmt = mConnection.prepareStatement(strSQL);
            int i = 0;
            for (Object obj : parameter) {
                mStmt.setObject(++i, obj);
            }

            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                UserDTL amUser = new UserDTL();
                amUser.setUserId(mRs.getLong(1));
                amUser.setUserName(mRs.getString(2));
                return amUser;
            }

        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return null;
    }

    public DSPCompany getCompanyUser(Long userId) throws Exception {
        try {
            open();
            String strSQL = " SELECT com_id, user_id, type, DECODE(api_user_id, 0, null, api_user_id) api_user_id " +
                    " FROM dsp_company where status = 1 " +
                    "   and (user_id = ? " +
                    "       or API_USER_ID = ?" +
                    "       or group_id in (select group_id from AM_GROUP_USER where user_id = ?) " +
                    "       or api_group_id in (select group_id from AM_GROUP_USER where user_id = ?))";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, userId);
            mStmt.setLong(2, userId);
            mStmt.setLong(3, userId);
            mStmt.setLong(4, userId);

            mRs = mStmt.executeQuery();
            if (mRs.next()) {
                DSPCompany company = new DSPCompany();
                company.setComId(mRs.getLong(1));
                company.setUserId(mRs.getLong(2));
                company.setType(mRs.getLong(3));
                company.setApiUserId(mRs.getLong(4));
                return company;
            }
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return null;
    }

    public long addAmUser(UserDTL userDTL, Connection conn, String strKey) throws Exception {
        long idFromSeq;
        String groupId = SystemConfig.getConfig(strKey);
        try {

            idFromSeq = SQLUtil.getSequenceValue(conn, "seq_am_user_id");
            userDTL.setUserId(idFromSeq);
            String strSQL = "INSERT INTO AM_USER(USER_ID, USER_NAME, PASSWORD, EXPIRE_STATUS, STATUS, CONFIG) " +
                    "VALUES(?,?,?,?,?,?)";
            mStmt = conn.prepareStatement(strSQL);
            mStmt.setLong(1, idFromSeq);
            mStmt.setString(2, userDTL.getUserName().trim());
            mStmt.setString(3, userDTL.getPassword().trim());
            mStmt.setLong(4, userDTL.getExpireStatus());
            mStmt.setLong(5, userDTL.getStatus());
            mStmt.setString(6, "language=vi;");
            mStmt.execute();
            logAfterInsert("AM_USER", "USER_ID=" + idFromSeq);
            close(mStmt);

            strSQL = "INSERT INTO am_group_user(GROUP_ID,USER_ID) VALUES(?,?)";
            mStmt = conn.prepareStatement(strSQL);
            mStmt.setLong(1, Long.parseLong(groupId));
            mStmt.setLong(2, idFromSeq);
            mStmt.execute();

        } catch (Exception ex) {
            conn.rollback();
            throw ex;

        } finally {
            close(mStmt);
        }
        return idFromSeq;
    }

    public void editUser(UserDTL user, Connection conn) throws Exception {

        try {
            //Update app
            List listCompany = logBeforeUpdate("DSP_COMPANY", "USER_ID=" + user.getUserId());
            String strSQL = "UPDATE AM_USER SET  EXPIRE_STATUS = ? , STATUS = ? WHERE USER_ID=?";
            mStmt = conn.prepareStatement(strSQL);
            mStmt.setLong(1, user.getExpireStatus());
            mStmt.setLong(2, user.getStatus());
            mStmt.setLong(3, user.getUserId());
            mStmt.execute();

        } catch (Exception ex) {
            conn.rollback();
            throw ex;

        } finally {
            close(mStmt);
        }
    }


    public void editPasswordUser(UserDTL user, Connection conn) throws Exception {
        try {
            //Update app
            List listCompany = logBeforeUpdate("DSP_COMPANY", "USER_ID=" + user.getUserId());
            String strSQL = "UPDATE AM_USER SET  password = ? WHERE USER_ID=?";
            mStmt = conn.prepareStatement(strSQL);
            mStmt.setString(1, StringUtil.encryptPassword(user.getPassword()));
            mStmt.setLong(2, user.getUserId());
            mStmt.execute();

        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            close(mStmt);
        }
    }

    //trieuNV
    public List<DSPCompany> getListCompany() throws Exception {
        List<DSPCompany> listReturn = new ArrayList<>();
        try {
            open();
            final String strSQL = " SELECT COM_ID, COM_NAME, BUS_CODE, ADDRESS, PARENT_ID, USER_ID, TYPE, PROVINCE,CITY,DISTRICT,WARD " +
                    "FROM DSP_COMPANY where status = 1 and type in (2, 3) ";

            mStmt = mConnection.prepareStatement(strSQL);
            mRs = mStmt.executeQuery();

            while (mRs.next()) {
                DSPCompany tmpDSP = new DSPCompany();
                tmpDSP.setComId(mRs.getLong(1));
                tmpDSP.setComName(mRs.getString(2));
                tmpDSP.setBusCode(mRs.getString(3));
                tmpDSP.setAddress(mRs.getString(4));
                tmpDSP.setParentId(mRs.getLong(5));
                tmpDSP.setUserId(mRs.getLong(6));
                tmpDSP.setType(mRs.getLong(7));
                tmpDSP.setProvince(mRs.getString(8));
                tmpDSP.setCity(mRs.getString(9));
                tmpDSP.setDistrict(mRs.getString(10));
                tmpDSP.setWard(mRs.getString(11));
                listReturn.add(tmpDSP);
            }
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
        return listReturn;
    }

    public boolean checkBHTTCode(DSPCompany obj) throws Exception {
        try {
            open();
            String strSQL = "SELECT 1 FROM dsp_company WHERE bhtt_code = ?";
            if (obj.getComId() != null) {
                strSQL += " AND com_id != ?";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, obj.getBhttCode());
            if (obj.getComId() != null) {
                mStmt.setLong(2, obj.getComId());
            }
            mRs = mStmt.executeQuery();
            return !mRs.next();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
    }

    public boolean checkExistVasMobile(DSPCompany obj) throws Exception {
        try {
            open();
            String strSQL = "SELECT 1 FROM dsp_company WHERE vas_mobile = ?";
            if (obj.getComId() != null) {
                strSQL += " AND com_id != ?";
            }
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, obj.getVasMobile());
            if (obj.getComId() != null) {
                mStmt.setLong(2, obj.getComId());
            }
            mRs = mStmt.executeQuery();
            return !mRs.next();
        } catch (Exception ex) {
            throw ex;
        } finally {
            close(mRs);
            close(mStmt);
            close();
        }
    }
}
