package com.garinternal.common.util;

/*
File Name:                      GarExcelReadUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains utility methods to read excel files

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.garinternal.common.excel.xlsx.StreamingReader;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarExcelReadUtil {

    private String      excelPath;
    private int         numColumns;
    private int         numSheets;
    private Workbook    workbook;
    private InputStream inputStream;

    /**
     * Constructor - Making this as private as it is not intended to be used for other classes
     */
    @SuppressWarnings("unused")
    private GarExcelReadUtil() {
        // do nothing
    }

    /**
     * Constructor
     * 
     * @param path Path of excel file
     * @throws OException {@link OException}
     */
    public GarExcelReadUtil(String path) throws OException {
        this.excelPath = path;

        File excelFile = new File(this.excelPath);

        if (!excelFile.exists()) {
            throw new OException("File \"" + this.excelPath + "\" does not exist.");
        }

        try {
            this.inputStream = new FileInputStream(excelFile);

            final int rowAccessSize = 100;
            final int maxBufferSize = 50 * 1024 * 1024;
            this.workbook = StreamingReader.builder().rowCacheSize(rowAccessSize)       // number of rows to keep in memory (defaults to 10)
                .bufferSize(maxBufferSize) // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(this.inputStream);

            this.numSheets = this.workbook.getNumberOfSheets();
        } catch (Exception t) {
            throw new OException(t);
        }

    }

    /**
     * Returns the workbook instance associated with this object
     * 
     * @return Workbook instance
     */
    public Workbook getWorkbook() {
        return this.workbook;
    }

    /**
     * Get Number of sheets in Excel File
     * 
     * @return Number of sheets
     */
    public int getNumberOfSheets() {
        return this.numSheets;
    }

    /**
     * Get data from excel file.
     * - When header number is specified, column names will be populated based on headerRowNum. Names will be populated starting
     * from column of given headerRowNum
     * - Data will be fetched starting from dataStartRow. If headerRowNum is specified, data will be fetched for the columns equal
     * to the number of columns encountered at headerRowNum row. Else data will be fetched until it's availabe in the cells.
     * It will continue fetching for all the rows below.
     * - This method returns Ticker Code
     * 
     * @param data         Table to store extracted data
     * @param sheet        Excel Sheet instance
     * @param headerRowNum Row containing column names
     * @param dataStartRow Row where data starts
     * @return Ticker Code
     * @throws OException {@link OException}
     */
    public String getDataFromExcel(Table data, Sheet sheet, int headerRowNum, int dataStartRow) throws OException {
        int    tickerCodeRow = 0;
        int    tickerCodeCol = 1;
        String tikcerCode    = "";
        int    rowNum        = 0;

        for (Row row : sheet) {

            if (rowNum == tickerCodeRow) {
                int colNum = 0;

                for (Cell cell : row) {

                    if (colNum == tickerCodeCol) {
                        tikcerCode = this.getCellDataAsString(cell);
                        break;
                    }

                    colNum++;
                }

            } else if (rowNum == headerRowNum) {
                this.addColumnNamesToTable(row, data);
            } else if (rowNum >= dataStartRow) {
                this.addDataToTable(row, data, rowNum, dataStartRow);
            }

            rowNum++;
        }

        return tikcerCode;
    }

    /**
     * Add column names to table from the specified headerRowNum
     * 
     * @param row          Row instance
     * @param tblExcelData Table to add column names to based on excel values
     * @throws OException
     */
    private void addColumnNamesToTable(Row row, Table tblExcelData) throws OException {
        // For each row, iterate through all the columns
        Iterator<Cell> cellIterator = row.cellIterator();

        while (cellIterator.hasNext()) {
            String columnName = "";
            Cell   cell       = cellIterator.next();

            columnName = this.getCellDataAsString(cell);

            tblExcelData.addCol(columnName, COL_TYPE_ENUM.COL_STRING);
            this.numColumns++;
        }

    }

    /**
     * Get Cell Data
     * 
     * @param cell Cell Instance
     * @return String value of Data
     * @throws OException
     */
    private String getCellDataAsString(Cell cell) throws OException {
        String columnName;

        // Check the cell type and fetch accordingly
        switch (cell.getCellType()) {
            case NUMERIC:
                columnName = Str.doubleToStr(cell.getNumericCellValue());
                break;

            case STRING:
                columnName = cell.getStringCellValue();
                break;

            default:
                columnName = cell.getStringCellValue();
                break;
        }

        return columnName;
    }

    /**
     * Adds the data to table
     * 
     * @param row          Row instance
     * @param tblExcelData Table to add extracted data to
     * @throws OException
     */
    private void addDataToTable(Row row, Table tblExcelData, int rowCount, int dataStartRow) throws OException {

        // For each row, iterate through all the columns
        Iterator<Cell> cellIterator = row.cellIterator();
        int            columnCount  = -1;

        while (cellIterator.hasNext()) {
            columnCount++;

            if (this.numColumns <= 0 && rowCount == dataStartRow) {
                tblExcelData.addCol("A" + columnCount, COL_TYPE_ENUM.COL_STRING);
            }

            if (columnCount == 0) {
                tblExcelData.addRow();
            } else if (this.numColumns > 0 && columnCount >= this.numColumns) {
                // numColumns will be set to >0 if header row number is passed.
                // Use this value only to fetch data from those columns to which
                // header names were set in table
                break;
            }

            String columnName = "";
            Cell   cell       = cellIterator.next();

            columnName = this.getCellDataAsString(cell);

            tblExcelData.setString(columnCount + 1, rowCount - dataStartRow + 1, columnName);
        }

    }

    /**
     * Safely close workbook created
     * 
     * @param workbook
     */
    public void closeWorkbook() {

        try {

            if (null != this.workbook) {
                this.workbook.close();
            }

            if (null != this.inputStream) {
                this.inputStream.close();
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
