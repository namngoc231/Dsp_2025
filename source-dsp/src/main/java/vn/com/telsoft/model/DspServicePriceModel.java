/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.model;

import com.faplib.lib.admin.data.AMDataPreprocessor;
import com.faplib.lib.util.SQLUtil;
import vn.com.telsoft.entity.DSPServicePrice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TrieuNV
 */
public class DspServicePriceModel extends AMDataPreprocessor implements Serializable {

    public List<DSPServicePrice> getDSPServicePrice(DSPServicePrice dto) throws Exception {
        List<DSPServicePrice> listReturn = new ArrayList<>();
        List<Object> parameter = new ArrayList<>();
        try {
            open();
            String strSQL = " SELECT price_id,name,cap_min,cap_max,price,active_day,tab_id from DSP_SERVICE_PRICE where 1=1 ";
            if (dto != null) {
                if (dto.getPriceId() != null) {
                    strSQL += " and price_id =? ";
                    parameter.add(dto.getPriceId());
                }
                if (dto.getName() != null && !dto.getName().equals("")) {
                    strSQL += " and name =? ";
                    parameter.add(dto.getName());
                }
                if (dto.getCapMin() != null) {
                    strSQL += " and cap_min =? ";
                    parameter.add(dto.getCapMin());
                }
                if (dto.getCapMax() != null) {
                    strSQL += " and cap_max =? ";
                    parameter.add(dto.getCapMax());
                }
                if (dto.getPrice() != null) {
                    strSQL += " and price =? ";
                    parameter.add(dto.getPrice());
                }
                if (dto.getActiveDay() != null) {
                    strSQL += " and active_day =? ";
                    parameter.add(dto.getActiveDay());
                }
                if (dto.getTabId() != null) {
                    strSQL += " and tab_id =? ";
                    parameter.add(dto.getTabId());
                }
            }
            strSQL += " order by cap_min ";
            mStmt = mConnection.prepareStatement(strSQL);
            if (dto != null) {
                int i = 0;
                for (Object obj : parameter) {
                    mStmt.setObject(++i, obj);
                }
            }
            mRs = mStmt.executeQuery();
            int index = 0;
            while (mRs.next()) {
                index++;
                DSPServicePrice item = new DSPServicePrice();
                item.setIndex(index);
                item.setPriceId(mRs.getLong(1));
                item.setName(mRs.getString(2));
                long capMinDb = mRs.getLong(3);
                item.setCapMin(capMinDb);
                if (capMinDb % (1024 * 1024) == 0) {
                    item.setStrCapMin(numberPrice(String.valueOf(capMinDb / (1024 * 1024)) + "GB"));
                } else if (capMinDb % 1024 == 0) {
                    item.setStrCapMin(numberPrice(String.valueOf(capMinDb / (1024)) + "MB"));
                } else {
                    item.setStrCapMin(numberPrice(String.valueOf(capMinDb)));
                }

                long capMaxDb = mRs.getLong(4);
                item.setCapMax(capMaxDb);
                if (capMaxDb % (1024 * 1024) == 0) {
                    item.setStrCapMax(numberPrice(String.valueOf(capMaxDb / (1024 * 1024)) + "GB"));
                } else if (capMaxDb % 1024 == 0) {
                    item.setStrCapMax(numberPrice(String.valueOf(capMaxDb / (1024)) + "MB"));
                } else {
                    item.setStrCapMax(numberPrice(String.valueOf(capMaxDb)));
                }

                item.setPrice(mRs.getLong(5));
                item.setActiveDay(mRs.getLong(6));
                item.setTabId(mRs.getLong(7));
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

    public Long insert(DSPServicePrice service) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            Long priceId = SQLUtil.getSequenceValue(mConnection, "DSP_SERVICE_PRICE_SEQ");
            String strSQL = "insert into DSP_SERVICE_PRICE( price_id,name,cap_min,cap_max,price,active_day,tab_id) values (?,?,?,?,?,?,?) ";

            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, priceId);
            mStmt.setString(2, service.getName());
            mStmt.setLong(3, service.getCapMin());
            mStmt.setLong(4, service.getCapMax());
            mStmt.setLong(5, service.getPrice());
            mStmt.setLong(6, service.getActiveDay());
            mStmt.setLong(7, service.getTabId());
            mStmt.execute();
            logAfterInsert("DSP_SERVICE_PRICE", "PRICE_ID=" + priceId);
            mConnection.commit();
            return priceId;
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }

    public void update(DSPServicePrice service) throws Exception {
        try {
            open();
            mConnection.setAutoCommit(false);
            List listChange = logBeforeUpdate("DSP_SERVICE_PRICE", "PRICE_ID=" + service.getPriceId());
            String strSQL = " update DSP_SERVICE_PRICE set name = ?,cap_min = ?,price = ?,active_day = ?,cap_max = ?   ";

            strSQL += "  where tab_id = ? and price_id = ?";
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setString(1, service.getName());
            mStmt.setLong(2, service.getCapMin());
            mStmt.setLong(3, service.getPrice());
            mStmt.setLong(4, service.getActiveDay());
            mStmt.setLong(5, service.getCapMax());
            mStmt.setLong(6, service.getTabId());
            mStmt.setLong(7, service.getPriceId());
            mStmt.execute();
            logAfterUpdate(listChange);
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close();
        }
    }


    public void delete(DSPServicePrice dto) throws Exception {
        try {
            open();
            String strSQL = " DELETE FROM DSP_SERVICE_PRICE WHERE TAB_ID=? and PRICE_ID = ? ";
            mConnection.setAutoCommit(false);
//            logBeforeDelete("DSP_SERVICE_PRICE", "PRICE_ID=" + dto.getPriceId());
            mStmt = mConnection.prepareStatement(strSQL);
            mStmt.setLong(1, dto.getTabId());
            mStmt.setLong(2, dto.getPriceId());
            mStmt.execute();
            mConnection.commit();
        } catch (Exception ex) {
            mConnection.rollback();
            throw ex;
        } finally {
            close(mStmt);
            close(mConnection);
        }
    }

    public String numberPrice(String price) throws Exception {
        int size = price.length();
        String resultAf = "";
        String resultBef = "";
        if (size >= 5) {
            if(price.contains("G")||price.contains("M")){
                resultAf = price.substring(size - 5, size);
                size -= 5;
            } else{
                resultAf = price.substring(size - 4, size);
                size -= 4;
            }
            if (size == 1) {
                price = price.substring(0, 1);
            } else {
                price = price.substring(0, size);
            }

            int thuong = size / 3;
            int du = size % 3;
            if (du == 0) {
                for (int i = 0; i < size; i = i + 3) {
                    resultBef += price.substring(i, i + 3) + ".";
                }
            } else {
                if (du == 1) {
                    resultBef += price.substring(0, 1) + ".";
                    for (int i = 1; i < size; i = i + 3) {
                        resultBef += price.substring(i, i + 3) + ".";
                    }
                }
                if (du == 2) {
                    resultBef += price.substring(0, 2) + ".";
                    for (int i = 2; i < size; i = i + 3) {
                        resultBef += price.substring(i, i + 3) + ".";
                    }
                }
            }
        } else {
            return price;
        }
        String strr = resultBef.substring(0, resultBef.length()) + resultAf;
        return strr;
    }
}
