/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.util;

import com.faplib.lib.SystemLogger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TungLM
 */
public class ExcelUtil {

    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c <= row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    public static List read(File inputWorkbook) throws Exception {
        if (inputWorkbook.getName().lastIndexOf(".xlsx") != -1) {
            return readXlsx(inputWorkbook);
        }

        List listReturn = new ArrayList();
        FileInputStream file = null;

        try {
            file = new FileInputStream(inputWorkbook);

            HSSFWorkbook workbook = new HSSFWorkbook(file);
            HSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet.getRow(0) != null) {
                int colCount = sheet.getRow(0).getLastCellNum();

                for (Row row : sheet) {
                    if (isRowEmpty(row)) {
                        continue;
                    }

                    List<ExcelCellEtt> listCellRow = new ArrayList<ExcelCellEtt>();

                    for (int cn = 0; cn < colCount; cn++) {
                        Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

                        ExcelCellEtt tmpCell = new ExcelCellEtt();

                        if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                                tmpCell.setStringValue(cell.getStringCellValue().trim());

                            } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                tmpCell.setNumberValue(cell.getNumericCellValue());
                            }

                            if (tmpCell.getStringValue() == null) {
                                tmpCell.setStringValue(String.valueOf(new DecimalFormat("#.########################################").format(cell.getNumericCellValue())));
                            }
                        }

                        listCellRow.add(tmpCell);
                    }

                    listReturn.add(listCellRow);
                }
            }

        } catch (Exception e) {
            SystemLogger.getLogger().error(e);
            throw e;

        } finally {
            file.close();
        }

        return listReturn;
    }

    public static List readXlsx(File inputWorkbook) throws Exception {
        List listReturn = new ArrayList();
        FileInputStream file = null;

        try {
            file = new FileInputStream(inputWorkbook);

            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet.getRow(0) != null) {
                int colCount = sheet.getRow(0).getLastCellNum();

                for (Row row : sheet) {
                    if (isRowEmpty(row)) {
                        continue;
                    }

                    List<ExcelCellEtt> listCellRow = new ArrayList<ExcelCellEtt>();

                    for (int cn = 0; cn < colCount; cn++) {
                        Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

                        ExcelCellEtt tmpCell = new ExcelCellEtt();

                        if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                                tmpCell.setStringValue(cell.getStringCellValue());

                            } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                tmpCell.setNumberValue(cell.getNumericCellValue());
                            }

                            if (tmpCell.getStringValue() == null) {
                                tmpCell.setStringValue(String.valueOf(new DecimalFormat("#.########################################").format(cell.getNumericCellValue())));
                            }
                        }

                        listCellRow.add(tmpCell);
                    }

                    listReturn.add(listCellRow);
                }
            }
        } catch (Exception e) {
            SystemLogger.getLogger().error(e);
            throw e;

        } finally {
            file.close();
        }

        return listReturn;
    }

    public static List readTxt(File inputFile) throws IOException {
        List listReturn = new ArrayList();
        FileInputStream file = null;
        String strLine;
        try {
            file = new FileInputStream(inputFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(file));
            while ((strLine = in.readLine()) != null) {
                if (!strLine.trim().isEmpty()) {
                    try {
                        listReturn.add(strLine);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            SystemLogger.getLogger().error(e);
            throw e;
        } finally {
            assert file != null;
            file.close();
        }
        return listReturn;
    }

}
