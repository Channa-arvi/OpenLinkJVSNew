package com.garinternal.common.util;

/*
File Name:                      GarExcelWriteUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains utility methods to write/modify excel files

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarExcelWriteUtil {

    private SXSSFWorkbook workbook;
    private String        path;

    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    private GarExcelWriteUtil() {
        // do nothing
    }

    /**
     * Constructor
     * 
     * @param path Excel file path
     * @throws OException {@link OException}
     */
    public GarExcelWriteUtil(String path) throws OException {

        try {
            this.path = path;
            File file = new File(path);

            if (file.exists()) {
                this.workbook = (SXSSFWorkbook) WorkbookFactory.create(file);
            } else {
                final int rowAccessWindowSize = 100;
                this.workbook = new SXSSFWorkbook(rowAccessWindowSize);
            }

        } catch (Exception e) {
            throw new OException(e);
        }

    }

    /**
     * Print table to excel
     * 
     * @param table            Table to print
     * @param sheetName        Sheet Name to print
     * @param excelStartRow    Excel Row number to start printing
     * @param excelStartCol    Excel Column number to start printing
     * @param printColumnNames Flag to indicate it column names/titles to be printed
     * @throws OException {@link OException}
     */
    public void printTableToExcel(Table table, String sheetName, int excelStartRow, int excelStartCol, boolean printColumnNames)
        throws OException {

        if (GarHasAValue.isTableEmpty(table)) {
            return;
        }

        int numTableRows = table.getNumRows();
        int numTableCols = table.getNumCols();

        Sheet sheet = null;

        if (this.workbook.getSheetIndex(sheetName) >= 0) {
            sheet = this.workbook.getSheet(sheetName);
        } else {
            sheet = this.workbook.createSheet();
            this.workbook.setSheetName(this.workbook.getSheetIndex(sheet), sheetName);
        }

        // add headers
        if (printColumnNames) {
            this.printColumnNamesToExcel(table, excelStartRow, excelStartCol, numTableCols, sheet);
        }

        // get table column type map
        Map<Integer, Integer> colTypeMap = new HashMap<>();

        for (int tableColNum = 1; tableColNum <= numTableCols; tableColNum++) {
            colTypeMap.put(tableColNum, table.getColType(tableColNum));
        }

        // add data
        this.printDataToExcel(table, excelStartRow, excelStartCol, printColumnNames, numTableRows, numTableCols, sheet);
    }

    /**
     * Print data to excel
     * 
     * @param table            Table
     * @param excelStartRow    Excel start row
     * @param excelStartCol    Excel start column
     * @param printColumnNames Flag indicating if column names were printed
     * @param numTableRows     Number of rows in table
     * @param numTableCols     Number of columns in table
     * @param sheet            Sheet instance
     * @throws OException
     */
    private void printDataToExcel(Table table, int excelStartRow, int excelStartCol, boolean printColumnNames, int numTableRows,
        int numTableCols, Sheet sheet) throws OException {

        Map<Integer, Integer> colTypeMap = new HashMap<>();

        // get table column types
        for (int tableColNum = 1; tableColNum <= numTableCols; tableColNum++) {
            colTypeMap.put(tableColNum, table.getColType(tableColNum));
        }

        for (int tableRowNum = 1; tableRowNum <= numTableRows; tableRowNum++) {

            Row row = sheet.createRow(excelStartRow + tableRowNum - 1 + (printColumnNames ? 1 : 0));

            for (int tableColNum = 1; tableColNum <= numTableCols; tableColNum++) {
                int cellNum = excelStartCol + tableColNum - 1;

                Cell cell    = row.createCell(cellNum);
                int  colType = colTypeMap.get(tableColNum);

                if (colType == COL_TYPE_ENUM.COL_STRING.toInt()) {
                    cell.setCellValue(table.getString(tableColNum, tableRowNum));
                } else if (colType == COL_TYPE_ENUM.COL_INT.toInt()) {
                    cell.setCellValue(table.getInt(tableColNum, tableRowNum));
                } else if (colType == COL_TYPE_ENUM.COL_INT64.toInt()) {
                    cell.setCellValue(table.getInt64(tableColNum, tableRowNum));
                } else if (colType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
                    cell.setCellValue(table.getDouble(tableColNum, tableRowNum));
                } else if (colType == COL_TYPE_ENUM.COL_DATE.toInt()) {
                    cell.setCellValue(table.getInt(tableColNum, tableRowNum));
                } else {
                    throw new OException("Unhandled column type: " + COL_TYPE_ENUM.fromInt(colType).toString());
                }

            }

        }

    }

    /**
     * Print Column names
     * 
     * @param table         table
     * @param excelStartRow Excel Start row
     * @param excelStartCol Excel start column
     * @param numTableCols  Num columns in the table
     * @param sheet         Sheet instance
     * @throws OException
     */
    private void printColumnNamesToExcel(Table table, int excelStartRow, int excelStartCol, int numTableCols, Sheet sheet)
        throws OException {
        Row row = sheet.createRow(excelStartRow);

        for (int tableColNum = 1; tableColNum <= numTableCols; tableColNum++) {
            int cellNum = excelStartCol + tableColNum - 1;

            Cell   cell     = row.createCell(cellNum);
            String data     = "";
            String colTitle = table.getColTitle(tableColNum);

            if (GarHasAValue.hasAValue(colTitle, true)) {
                data = colTitle;
            } else {
                data = table.getColName(tableColNum);
            }

            cell.setCellValue(data);
        }

    }

    /**
     * Write Excel to disk
     * 
     * @throws OException {@link OException}
     */
    public void writeFileToDisk() throws OException {

        try (FileOutputStream out = new FileOutputStream(this.path)) {
            this.workbook.write(out);
        } catch (Exception e) {
            throw new OException(e);
        }

    }

    /**
     * Close workbook
     */
    public void closeWorkbook() {

        try {

            if (null != this.workbook) {
                this.workbook.dispose();
                this.workbook.close();
            }

        } catch (Exception e) {
            // do nothing
        }

    }

    @Override
    protected void finalize() {// NOSONAR: We need this just in case the implementer forgets to dispose the object
        this.closeWorkbook();
    }
}
