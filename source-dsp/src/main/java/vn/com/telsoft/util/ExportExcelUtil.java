/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.com.telsoft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.faplib.lib.SystemConfig;
import com.faplib.util.FileUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * @author AnhTQ
 */
public class ExportExcelUtil implements Serializable {

    /**
     * Hàm dùng để xuất 1 file excel đơn giản
     *
     * @param list         List dữ liệu
     * @param strFileName  tên File xuất ra
     * @param strHeader    Header của File
     * @param strSheetName Tên sheet
     * @return
     * @author ANHTQ
     */
    public static String exportExcel(List list, String strFileName,
                                     String strHeader, String strSheetName) throws Exception {
        List<String> listHeader = (List<String>) list.get(0);
        List<String[]> listData = (List<String[]>) list.get(1);
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        if (strSheetName == null || strSheetName.equals("")) {
            strSheetName = "Sheet ";
        }
        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(strSheetName);
        Row r = null;
        Cell cell = null;
        int rowNum = 2;
        int sheetNum = 2;
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setFontName("Arial");
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setItalic(false);

        CellStyle headerStyle = wb.createCellStyle();
        ;
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);

        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);

        DataFormat format = wb.createDataFormat();

        CellStyle numberCellStyle = wb.createCellStyle();
        numberCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setDataFormat(format.getFormat("###,##0.##"));

        String filePath = "";
        String dateName = "";

        r = sheet.createRow(0);
        cell = r.createCell(0);
        cell.setCellValue(strHeader);

        try {
            r = sheet.createRow(2);

            int columnCount = listHeader.size();
            for (int i = 0; i < columnCount; i++) {
                cell = r.createCell(i);
                cell.setCellValue(listHeader.get(i));
                cell.setCellStyle(headerStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

            for (String[] data : listData) {
                rowNum++;
                if (rowNum >= 1000000) {
                    sheet = (SXSSFSheet) wb.createSheet(strSheetName + sheetNum);
                    rowNum = 3;
                    sheetNum++;
                    r = sheet.createRow(2);
                    for (int i = 0; i < columnCount; i++) {
                        cell = r.createCell(i);
                        cell.setCellValue(listHeader.get(i));
                        cell.setCellStyle(headerStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

                }
                r = sheet.createRow(rowNum);
                for (int i = 0; i < columnCount; i++) {
                    Cell cellValue = r.createCell(i);
                    String strValue = com.faplib.applet.util.StringUtil.nvl(data[i], "");
                    try {
                        Double dbValue = Double.parseDouble(strValue);
                        cellValue.setCellValue(dbValue);
                        if ((dbValue == Math.floor(dbValue)) && !Double.isInfinite(dbValue)) {
                            cellValue.setCellStyle(style);
                        } else {
                            cellValue.setCellStyle(numberCellStyle);
                        }
                    } catch (NumberFormatException ex) {
                        cellValue.setCellValue(strValue);
                        cellValue.setCellStyle(style);
                    }
                }

            }
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                for (int i = 0; i < columnCount + 3; i++) {
                    try {
                        wb.getSheetAt(j).autoSizeColumn(i);
                    } catch (Exception ex) {
                    }
                }
            }
            String relativeWebPath = "/resources/tmp/";
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            String path = servletContext.getRealPath(relativeWebPath);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            dateName = dateFormat.format(date);
            filePath = path + "/" + strFileName + dateName + ".xlsx";
            try (FileOutputStream output = new FileOutputStream(new File(filePath))) {
                wb.write(output);
            }

            System.out.println("Excel written successfully..");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
        return "/" + strFileName + dateName + ".xlsx";
    }

    /**
     * Hàm dùng để xuất 1 file excel đơn giản
     *
     * @param list          List dữ liệu
     * @param strFileName   tên File xuất ra
     * @param strHeader     Header của File
     * @param strSheetName  Tên sheet
     * @param hmColumnsName HashMap tên cột
     * @return
     * @author ANHTQ
     */
    public static String exportExcel(List list, String strFileName,
                                     String strHeader, String strSheetName, HashMap<String, String> hmColumnsName) throws Exception {
        List<String> listHeader = (List<String>) list.get(0);
        List<String[]> listData = (List<String[]>) list.get(1);
        if (strSheetName == null || strSheetName.equals("")) {
            strSheetName = "Sheet ";
        }
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(strSheetName);
        Row r = null;
        Cell cell = null;
        int rowNum = 2;
        int sheetNum = 2;
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setFontName("Arial");
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setItalic(false);

        CellStyle headerStyle = wb.createCellStyle();
        ;
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);

        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);

        DataFormat format = wb.createDataFormat();

        CellStyle numberCellStyle = wb.createCellStyle();
        numberCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setDataFormat(format.getFormat("###,##0.##"));

        String filePath = "";
        String dateName = "";

        r = sheet.createRow(0);
        cell = r.createCell(0);
        cell.setCellValue(strHeader);

        try {
            r = sheet.createRow(2);

            int columnCount = listHeader.size();
            for (int i = 0; i < columnCount; i++) {
                cell = r.createCell(i);
                if (hmColumnsName.get(listHeader.get(i)) != null) {
                    cell.setCellValue(hmColumnsName.get(listHeader.get(i)));
                } else {
                    cell.setCellValue(listHeader.get(i));
                }
                cell.setCellStyle(headerStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

            for (String[] data : listData) {
                rowNum++;
                if (rowNum >= 1000000) {
                    sheet = (SXSSFSheet) wb.createSheet(strSheetName + sheetNum);
                    rowNum = 3;
                    sheetNum++;
                    r = sheet.createRow(2);
                    for (int i = 0; i < columnCount; i++) {
                        cell = r.createCell(i);
                        if (hmColumnsName.get(listHeader.get(i)) != null) {
                            cell.setCellValue(hmColumnsName.get(listHeader.get(i)));
                        } else {
                            cell.setCellValue(listHeader.get(i));
                        }
                        cell.setCellStyle(headerStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

                }
                r = sheet.createRow(rowNum);
                for (int i = 0; i < columnCount; i++) {
                    Cell cellValue = r.createCell(i);
                    String strValue = com.faplib.applet.util.StringUtil.nvl(data[i], "");
                    try {
                        Double dbValue = Double.parseDouble(strValue);
                        cellValue.setCellValue(dbValue);
                        if ((dbValue == Math.floor(dbValue)) && !Double.isInfinite(dbValue)) {
                            cellValue.setCellStyle(style);
                        } else {
                            cellValue.setCellStyle(numberCellStyle);
                        }
                    } catch (NumberFormatException ex) {
                        cellValue.setCellValue(strValue);
                        cellValue.setCellStyle(style);
                    }
                }

            }
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                for (int i = 0; i < columnCount + 3; i++) {
                    try {
                        wb.getSheetAt(j).autoSizeColumn(i);
                    } catch (Exception ex) {
                    }
                }
            }
            String relativeWebPath = "/resources/tmp/";
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            String path = servletContext.getRealPath(relativeWebPath);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            dateName = dateFormat.format(date);
            filePath = path + "/" + strFileName + dateName + ".xlsx";
            try (FileOutputStream output = new FileOutputStream(new File(filePath))) {
                wb.write(output);
            }

            System.out.println("Excel written successfully..");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
        return "/" + strFileName + dateName + ".xlsx";
    }

    public static String exportExcel(ResultSet rs, String strFileName,
                                     String strHeader, String strSheetName) throws Exception {
        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        if (strSheetName == null || strSheetName.equals("")) {
            strSheetName = "Sheet ";
        }
        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(strSheetName);
        Row r = null;
        Cell cell = null;
        int rowNum = 2;
        int sheetNum = 2;
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setFontName("Arial");
//        headerFont.setBold(true);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setItalic(false);

        CellStyle headerStyle = wb.createCellStyle();
        ;
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);

        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);

        DataFormat format = wb.createDataFormat();

        CellStyle numberCellStyle = wb.createCellStyle();
        numberCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setDataFormat(format.getFormat("###,##0.##"));

        String filePath = "";
        String dateName = "";

        r = sheet.createRow(0);
        cell = r.createCell(0);
        cell.setCellValue(strHeader);

        try {
            r = sheet.createRow(2);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                cell = r.createCell(i - 1);
                cell.setCellValue(metaData.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

            while (rs.next()) {
                rowNum++;
                if (rowNum >= 1000000) {
                    sheet = (SXSSFSheet) wb.createSheet(strSheetName + sheetNum);
                    rowNum = 3;
                    sheetNum++;
                    r = sheet.createRow(2);
                    for (int i = 1; i < columnCount + 1; i++) {
                        cell = r.createCell(i - 1);
                        cell.setCellValue(metaData.getColumnName(i));
                        cell.setCellStyle(headerStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

                }
                r = sheet.createRow(rowNum);
                for (int i = 0; i < columnCount; i++) {
                    Cell cellValue = r.createCell(i);
                    String strValue = com.faplib.applet.util.StringUtil.nvl(rs.getString(i + 1), "");
                    try {
                        Double dbValue = Double.parseDouble(strValue);
                        cellValue.setCellValue(dbValue);
                        if ((dbValue == Math.floor(dbValue)) && !Double.isInfinite(dbValue)) {
                            cellValue.setCellStyle(style);
                        } else {
                            cellValue.setCellStyle(numberCellStyle);
                        }
                    } catch (NumberFormatException ex) {
                        cellValue.setCellValue(strValue);
                        cellValue.setCellStyle(style);
                    }
                }

            }
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                for (int i = 0; i < columnCount + 3; i++) {
                    try {
                        wb.getSheetAt(j).autoSizeColumn(i);
                    } catch (Exception ex) {
                    }
                }
            }

            String relativeWebPath = "/resources/tmp/";
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            String path = servletContext.getRealPath(relativeWebPath);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            dateName = dateFormat.format(date);
            filePath = path + "/" + strFileName + dateName + ".xlsx";
            try (FileOutputStream output = new FileOutputStream(new File(filePath))) {
                wb.write(output);
            }

            System.out.println("Excel written successfully..");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "/" + strFileName + dateName + ".xlsx";
    }

    public static String exportExcel(ResultSet rs, String strFileName,
                                     String strHeader, String strSheetName, HashMap<String, String> hmColumnsName) throws Exception {

        SXSSFWorkbook wb = new SXSSFWorkbook(5000);
        if (strSheetName == null || strSheetName.equals("")) {
            strSheetName = "Sheet ";
        }
        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet(strSheetName);
        Row r = null;
        Cell cell = null;
        int rowNum = 2;
        int sheetNum = 2;
        Font headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setFontName("Arial");
//        headerFont.setBold(true);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setItalic(false);

        CellStyle headerStyle = wb.createCellStyle();
        ;
        headerStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        headerStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerStyle.setWrapText(true);

        DataFormat format = wb.createDataFormat();

        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setDataFormat(format.getFormat("0"));

        CellStyle numberCellStyle = wb.createCellStyle();
        numberCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        numberCellStyle.setDataFormat(format.getFormat("###,##0.##"));

        String filePath = "";
        String dateName = "";

        r = sheet.createRow(0);
        cell = r.createCell(0);
        cell.setCellValue(strHeader);

        try {

            r = sheet.createRow(2);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i < columnCount + 1; i++) {
                cell = r.createCell(i - 1);
                if (hmColumnsName.get(metaData.getColumnLabel(i)) != null) {
                    cell.setCellValue(hmColumnsName.get(metaData.getColumnLabel(i)));
                } else {
                    cell.setCellValue(metaData.getColumnLabel(i));
                }
                cell.setCellStyle(headerStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

            while (rs.next()) {
                rowNum++;
                if (rowNum >= 1000000) {
                    sheet = (SXSSFSheet) wb.createSheet(strSheetName + sheetNum);
                    rowNum = 3;
                    sheetNum++;
                    r = sheet.createRow(2);
                    for (int i = 1; i < columnCount + 1; i++) {
                        cell = r.createCell(i - 1);
                        if (hmColumnsName.get(metaData.getColumnName(i)) != null) {
                            cell.setCellValue(hmColumnsName.get(metaData.getColumnName(i)));
                        } else {
                            cell.setCellValue(metaData.getColumnName(i));
                        }
                        cell.setCellStyle(headerStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

                }
                r = sheet.createRow(rowNum);
                for (int i = 0; i < columnCount; i++) {
                    Cell cellValue = r.createCell(i);
                    String strValue = com.faplib.applet.util.StringUtil.nvl(rs.getString(i + 1), "");
                    try {
                        Double dbValue = Double.parseDouble(strValue);
                        cellValue.setCellValue(dbValue);
                        if ((dbValue == Math.floor(dbValue)) && !Double.isInfinite(dbValue)) {
                            cellValue.setCellStyle(style);
                        } else {
                            cellValue.setCellStyle(numberCellStyle);
                        }
                    } catch (NumberFormatException ex) {
                        cellValue.setCellValue(strValue);
                        cellValue.setCellStyle(style);
                    }
                }

            }
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                for (int i = 0; i < columnCount + 3; i++) {
                    try {
                        wb.getSheetAt(j).autoSizeColumn(i);
                    } catch (Exception ex) {
                    }
                }
            }

//            String relativeWebPath = "/resources/tmp/";
//            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
//            ServletContext servletContext = (ServletContext) externalContext.getContext();
//            String path = servletContext.getRealPath(relativeWebPath);

            String path = SystemConfig.getConfig("FileUploadPath");
            File directory = new File(path);
            //check tạo thư mục
            if (! directory.exists()){
                directory.mkdir();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            dateName = dateFormat.format(date);
//            filePath = path + "/" + strFileName + dateName + ".xlsx";
            filePath = path + strFileName + ".xlsx";

            try (FileOutputStream output = new FileOutputStream(new File(filePath))) {
                wb.write(output);
            }

            System.out.println("Excel written successfully..");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        return "/" + strFileName + dateName + ".xlsx";
        return filePath;
    }

}
