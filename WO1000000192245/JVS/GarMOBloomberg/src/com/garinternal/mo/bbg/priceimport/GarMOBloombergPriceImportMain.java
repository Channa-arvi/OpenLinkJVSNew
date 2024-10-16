package com.garinternal.mo.bbg.priceimport;

/*
File Name:                      GarMOBloombergPriceImportMain.java

Script Type:                    MAIN
Parameter Script:               GarMOBloombergPriceImportParam
Display Script:                 None

Description:
This is main script to import prices from Bloomberg Price file in excel format

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.HashMap;
import java.util.Map;
import java.io.File;

import org.apache.poi.ss.usermodel.Sheet;

import com.garinternal.basic.GarBasicScript;
import com.garinternal.common.constants.GarConstants;
import com.garinternal.common.enums.GAR_FILE_EXTENSION_ENUM;
import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.garinternal.common.util.GarExcelReadUtil;
import com.garinternal.common.util.GarExcelWriteUtil;
import com.garinternal.common.util.GarDateTimeUtil;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.garinternal.common.util.GarTableUtil;
import com.olf.openjvs.Index;
import com.olf.openjvs.OCalendar;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Sim;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.BMO_ENUMERATION;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.DATE_FORMAT;
import com.olf.openjvs.enums.IDX_DB_STATUS_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.enums.TABLE_SORT_DIR_ENUM;
import com.olf.openjvs.fnd.OCalendarBase;
import com.olf.openjvs.fnd.RefBase;
import com.olf.openjvs.fnd.UtilBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public final class GarMOBloombergPriceImportMain extends GarBasicScript {
    private static final String WARNING         = "WARNING";
    private static final String SUCCESS         = "SUCCESS";
    private static final String FAILURE         = "FAILURE";
    private static final double PRICE_THRESHOLD = 0.00000001;
    private Table               priceImportMessages;
    private boolean             haveImportWarnings;
    private boolean             isExcelWriteObjCreated;
    private GarExcelWriteUtil   excelWriteUtil;
    private GarLogger           logger;

    /**
     * Constructor
     *
     * @throws OException {@link OException}
     */
    public GarMOBloombergPriceImportMain() throws OException {
        super();
        this.priceImportMessages = this.getMessagesTableFormat();
        this.logger              = super.getLoggerInstance();
    }

    @Override
    public void execute(Table argt, Table returnt) throws OException {

        if (GarHasAValue.isTableEmpty(argt)) {
            this.logger.info("Either the param script failed or was not set in the task");
            return;
        }

        Table            priceDataRaw       = Table.tableNew();
        Table            priceDataFormatted = Util.NULL_TABLE;
        GarExcelReadUtil excelReadUtil      = null;
        int              currentDate        = 0;
        String           currentReportDir   = UtilBase.reportGetDirForToday();
        String           pathSeparator      = File.separator;

        try {
            int businessDate = UtilBase.getBusinessDate();
            Util.setCurrentDate(businessDate);
            currentDate = OCalendarBase.today();
            String excelFilePath = argt.getString("file_path", 1);
            int    userSelection = argt.getInt("user_selection", 1);

            excelReadUtil      = new GarExcelReadUtil(excelFilePath);
            priceDataFormatted = this.getPriceDataTableFormat();
            String excelExportPath = currentReportDir + pathSeparator + GarMOBloombergConstants.BLOOMBERG_FORMATTED_PRICE_FILE_NAME + "_"
                + GarDateTimeUtil.getTimeStampNowForFileName() + "." + GAR_FILE_EXTENSION_ENUM.XLSX.toString();

            int sheetNum = 0;

            for (Sheet sheet : excelReadUtil.getWorkbook()) {
                this.logger.info("Running for sheet " + (sheetNum + 1));
                String tickerCode = excelReadUtil.getDataFromExcel(priceDataRaw, sheet, -1, GarMOBloombergConstants.EXCEL_DATA_START_ROW);
                int    indexId    = this.getIndexIdToUpdate(tickerCode, sheetNum);

                // format the data to be updated on curves
                this.getPriceDataFormatted(priceDataFormatted, priceDataRaw, sheetNum);

                // save closing prices
                this.processClosingPrices(indexId, priceDataFormatted, userSelection, sheet.getSheetName(), excelExportPath);

                String successMsg = "";

                if (userSelection == GAR_MO_BBG_USER_SELECTION.IMPORT_PRICES.toInt()) {
                    successMsg = "Price Imported successfully for curve " + RefBase.getName(SHM_USR_TABLES_ENUM.INDEX_TABLE, indexId)
                        + " at Sheet Num: " + (sheetNum + 1);
                } else if (userSelection == GAR_MO_BBG_USER_SELECTION.GENERATE_REPORT.toInt()) {
                    successMsg = "Price printed successfully for curve " + RefBase.getName(SHM_USR_TABLES_ENUM.INDEX_TABLE, indexId)
                        + " at Sheet Num: " + (sheetNum + 1);
                }

                this.addMessageToTable(SUCCESS, successMsg);

                this.logger.info(successMsg);

                priceDataFormatted.clearRows();
                GarTableUtil.clearTable(priceDataRaw);

                sheetNum++;
            }

            if (userSelection == GAR_MO_BBG_USER_SELECTION.GENERATE_REPORT.toInt()) {
                String successMailMsg = "";

                if (this.isExcelWriteObjCreated) {
                    this.excelWriteUtil.writeFileToDisk();
                    successMailMsg = "Excel with formatted prices has been generated at locations: \n" + excelExportPath;
                } else {
                    successMailMsg = "<b><u>No excel with formatted prices generated as nothing to update</u></b>";
                }

                super.setSuccessMessageEmail(successMailMsg);
            }

        } catch (Exception e) {
            this.addMessageToTable(FAILURE, e.getMessage());
            throw e;
        } finally {
            // setting back the current date to what it was before
            Util.setCurrentDate(currentDate);

            if (this.haveImportWarnings) {
                this.logger.warning("Errors/Warnings encountered while processing excel. Please check report for further details");
            }

            GarSafe.destroy(priceDataRaw, priceDataFormatted);
            GarSafe.closeExcelWorkbook(excelReadUtil);
            GarSafe.closeExcelWorkbook(this.excelWriteUtil);

            String errorsExportPath = currentReportDir + pathSeparator + GarMOBloombergConstants.BLOOMBERG_PRICE_IMPORT_ERRORS_FILE_NAME
                + "_" + GarDateTimeUtil.getTimeStampNowForFileName() + "." + GAR_FILE_EXTENSION_ENUM.XLSX.toString();
            this.priceImportMessages.excelSave(errorsExportPath);

            super.setFailureAttachments(errorsExportPath);
            super.setSuccessAttachments(errorsExportPath);

            this.priceImportMessages.viewTable();
            GarSafe.destroy(this.priceImportMessages);
        }

    }

    /**
     * Add messages to table
     *
     * @param type    Message Type
     * @param message Message
     * @throws OException
     */
    private void addMessageToTable(String type, String message) throws OException {
        int rowNum = this.priceImportMessages.addRow();
        this.priceImportMessages.setString("type", rowNum, type);
        this.priceImportMessages.setString("description", rowNum, message);
    }

    /**
     * Get Message table format
     *
     * @return
     * @throws OException
     */
    private Table getMessagesTableFormat() throws OException {
        Table msgTable = Table.tableNew();

        msgTable.addCol("type", COL_TYPE_ENUM.COL_STRING);
        msgTable.addCol("description", COL_TYPE_ENUM.COL_STRING);

        return msgTable;
    }

    /**
     * Get Endur Index Id to update
     *
     * @param tickerCodeBberg Bloomberg ticker code
     * @param sheetNum        Sheet Number
     * @return
     * @throws OException
     */
    private int getIndexIdToUpdate(String tickerCodeBberg, int sheetNum) throws OException {
        Table tblIndexName = Util.NULL_TABLE;
        int   indexId      = -1;

        try {

            StringBuilder sql = new StringBuilder().append("\n SELECT     id.index_id") //
                .append("\n FROM       " + GAR_USER_TABLE_ENUM.USER_BBG_INDEX.toString() + " AS ubi") //
                .append("\n INNER JOIN idx_def id ON id.index_name = ubi.endur_index_name") //
                .append("\n        AND id.db_status = " + IDX_DB_STATUS_ENUM.IDX_DB_STATUS_VALIDATED.toInt()) //
                .append("\n WHERE      ubi.ticker_code = '" + tickerCodeBberg + "'"); //

            String errorMsg =
                "Unable to find a matching index for Bloomberg Index\"" + tickerCodeBberg + "\" on Sheet Number: " + (sheetNum + 1);
            tblIndexName = GarStrict.query(sql.toString(), errorMsg, this.logger);

            if (!GarHasAValue.isTableEmpty(tblIndexName)) {
                indexId = tblIndexName.getInt("index_id", 1);

                if (indexId <= 0) {
                    throw new OException(errorMsg);
                }

            } else {
                throw new OException(errorMsg);
            }

        } finally {
            GarSafe.destroy(tblIndexName);
        }

        return indexId;
    }

    /**
     * Process Closing Prices depending on User selection
     *
     * @param indexId            Index Id
     * @param priceDataFormatted Table containing formatted price data
     * @param userSelection      Action selected by user
     * @param sheetName          Sheet Name
     * @param Export             File export path
     * @throws OException
     */
    private void processClosingPrices(int indexId, Table priceDataFormatted, int userSelection, String sheetName, String excelExportPath)
        throws OException {
        Table tblGptTable = Util.NULL_TABLE;
        Table tblIndexId  = Table.tableNew();
        Table tblSort     = Table.tableNew();

        try {
            // this table will be used for changing id numbers for sorting purposes
            tblSort.addCols("I(new_id) I(old_id)");
            tblSort.addRow();
            tblSort.setInt("new_id", 1, 0);
            tblSort.setInt("old_id", 1, 0);

            int totalRows = priceDataFormatted.getNumRows();
            tblIndexId.addCol("index_id", COL_TYPE_ENUM.COL_INT);
            tblIndexId.addRow();

            tblIndexId.setInt("index_id", 1, indexId);

            priceDataFormatted.sortCol("date", TABLE_SORT_DIR_ENUM.TABLE_SORT_DIR_ASCENDING);
            int     startRowExcel = 1;
            boolean printColumns  = true;

            int colNumDate       = priceDataFormatted.getColNum("date");
            int colNumPriceData  = priceDataFormatted.getColNum("price_data");
            int colNumTblIndexId = tblIndexId.getColNum("index_id");

            for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
                int   date   = priceDataFormatted.getInt(colNumDate, rowNum);
                Table prices = priceDataFormatted.getTable(colNumPriceData, rowNum);

                Util.setCurrentDate(date);

                try {
                    // try to load closing prices if already present. If not load Universal prices in Catch block
                    Sim.loadCloseIndexList(tblIndexId, colNumTblIndexId, date, true);
                } catch (OException e) {
                    // load universal prices if closing prices is not present
                    Index.refreshDbList(tblIndexId, 1);
                }

                tblGptTable = Index.loadAllGpts(indexId);
                this.capitalizeAndFlagNonZeroRows(tblGptTable);

                // if grid point table has any zero values to be updated with prices in the file
                if (tblGptTable.unsortedFindInt("flag", 1) > 0) {
                    GarStrict.select(prices, tblGptTable, "flag", "group_temp EQ $grid_point");
                    prices.deleteWhereString("grid_point", "");

                    GarStrict.select(tblGptTable, prices, "price(input.mid)", "grid_point EQ $group_temp AND flag EQ 1");
                    tblGptTable.addCol("calc_price_flag", COL_TYPE_ENUM.COL_INT);
                    tblGptTable.sortCol("id");
                    this.calculateMissingPrices(tblGptTable);

                    if (userSelection == GAR_MO_BBG_USER_SELECTION.IMPORT_PRICES.toInt()) {
                        tblGptTable.delCol("group_temp");
                        tblGptTable.delCol("flag");
                        tblGptTable.delCol("calc_price_flag");
                        Index.updateGpts(indexId, tblGptTable, BMO_ENUMERATION.BMO_MID, 0, 1, date);
                    } else if (userSelection == GAR_MO_BBG_USER_SELECTION.GENERATE_REPORT.toInt()) {
                        prices.insertCol("grid_point_label", 1, COL_TYPE_ENUM.COL_STRING);
                        prices.addCol("filter_flag", COL_TYPE_ENUM.COL_INT);

                        prices.setColValInt("filter_flag", 1);
                        GarStrict.select(tblGptTable, prices, "filter_flag", "grid_point EQ $group_temp");
                        GarStrict.select(prices, tblGptTable, "input.mid(price), group_temp(grid_point), flag, calc_price_flag",
                            "calc_price_flag EQ 1 AND filter_flag EQ 0");
                        GarStrict.select(prices, tblGptTable, "input.mid(price), group_temp(grid_point), flag, calc_price_flag",
                            "calc_price_flag EQ 1 AND filter_flag EQ 1 AND group_temp EQ $grid_point");
                        GarStrict.select(prices, tblGptTable, "id, name(grid_point_label)", "group_temp EQ $grid_point");
                        prices.delCol("filter_flag");

                        prices.insertCol("index_name", 1, COL_TYPE_ENUM.COL_STRING);
                        prices.insertCol("date", 3, COL_TYPE_ENUM.COL_INT);// NOSONAR - Just inserting a column at certain position

                        // there is a difference of 2 days when we format the same julian date in endur and excel
                        prices.setColValInt("date", date + GarConstants.EXCEL_ENDUR_JULIAN_DATE_DIFF);
                        prices.setColValString("index_name", tblGptTable.getTableName());

                        this.formatAndSortPricesForExport(tblSort, prices);

                        if (!this.isExcelWriteObjCreated) {
                            this.excelWriteUtil         = new GarExcelWriteUtil(excelExportPath);
                            this.isExcelWriteObjCreated = true;
                        }

                        this.excelWriteUtil.printTableToExcel(prices, sheetName, startRowExcel - 1, 0, printColumns);
                        printColumns   = false;
                        startRowExcel += prices.getNumRows() + 1;
                    }

                    // reducing memory footprint
                    prices.clearRows();
                }

                // not the best practice to destroy inside a loop. Still going ahead as no other alternative
                GarSafe.destroy(tblGptTable);
            }

        } finally {
            GarSafe.destroy(tblGptTable, tblIndexId, tblSort);
        }

    }

    /**
     * Format and sort the prices table for exporting
     *
     * @param tblSort Temporary table used to set ID 0 with highest available ID + 1
     * @param prices  Prices table
     * @throws OException
     */
    private void formatAndSortPricesForExport(Table tblSort, Table prices) throws OException {
        final int doublePrecision = 4;
        prices.setColFormatAsDate("date", DATE_FORMAT.DATE_FORMAT_DLMLY_DASH);
        prices.setColFormatAsDouble("date", 0, doublePrecision);

        prices.setColTitle("grid_point_label", "GRID_POINT");
        prices.setColTitle("index_name", "Index");
        prices.setColTitle("date", "Date");
        prices.setColTitle("price", "PX_SETTLE");
        prices.setColTitle("grid_point", "CURRENT_CONTRACT_MONTH_YR");
        prices.setColTitle("flag", "Price Update?\n 1=Yes, 0=No");
        prices.setColTitle("calc_price_flag", "Is calculated price?\n 1=Yes, 0=No");

        prices.sortCol("id");
        int latestId = prices.getInt("id", prices.getNumRows());
        tblSort.setInt("new_id", 1, latestId + 1);

        GarStrict.selectConst(prices, tblSort, "new_id(id)", "old_id EQ $id");
        prices.sortCol("id");
        prices.delCol("id");
    }

    /**
     * Calculate missing prices. If prices are available for any forward dates, set nearest forward price.
     * Else set price available for nearest backward date.
     *
     * @param tblGptTable Table containing Grid points with prices
     * @throws OException
     */
    private void calculateMissingPrices(Table tblGptTable) throws OException {
        int    totalRows           = tblGptTable.getNumRows();
        int    colNumInputMid      = tblGptTable.getColNum("input.mid");
        int    colNumFlag          = tblGptTable.getColNum("flag");
        int    colNumCalcPriceFlag = tblGptTable.getColNum("calc_price_flag");
        double prevAvailablePrice  = 0.0;
        int    rowNum              = 1;

        while (rowNum <= totalRows) {
            double inputMidPrice = tblGptTable.getDouble(colNumInputMid, rowNum);

            if (inputMidPrice < PRICE_THRESHOLD) {
                Map<Integer, Double> calculatedPrice = this.getCalculatedPrice(tblGptTable, colNumInputMid, rowNum + 1, totalRows);

                if (calculatedPrice.isEmpty()) {
                    this.setPriceForRows(tblGptTable, rowNum, totalRows, prevAvailablePrice, colNumInputMid, colNumCalcPriceFlag,
                        colNumFlag);
                    rowNum = totalRows + 1;
                } else {
                    int    priceFoundRowNum = calculatedPrice.keySet().iterator().next();
                    double price            = calculatedPrice.get(priceFoundRowNum);
                    this.setPriceForRows(tblGptTable, rowNum, priceFoundRowNum - 1, price, colNumInputMid, colNumCalcPriceFlag, colNumFlag);
                    prevAvailablePrice = price;
                    rowNum             = priceFoundRowNum;
                }

            }

            if (inputMidPrice >= PRICE_THRESHOLD) {
                prevAvailablePrice = inputMidPrice;
            }

            rowNum++;
        }

    }

    /**
     * Set Price for rows
     *
     * @param tblGptTable         Grid point table with prices
     * @param startRow            Start Row of tblGptTable to set price
     * @param endRow              End Row of tblGptTable to set price
     * @param price               Price
     * @param colNumInputMid      Price column number
     * @param colNumCalcPriceFlag Calc Price Flag column number
     * @param colNumFlag          Flag
     * @throws OException
     */
    private void setPriceForRows(Table tblGptTable, int startRow, int endRow, Double price, int colNumInputMid, int colNumCalcPriceFlag,
        int colNumFlag) throws OException {

        for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
            tblGptTable.setDouble(colNumInputMid, rowNum, price);
            tblGptTable.setInt(colNumCalcPriceFlag, rowNum, 1);
            tblGptTable.setInt(colNumFlag, rowNum, 1);
        }

    }

    /**
     * Get next available forward price
     *
     * @param tblGptTable Grid point table with prices
     * @param priceColNum Price column number
     * @param rowNumStart Row number to start searching for price
     * @param totalRows   Total rows in tblGptTable
     * @return Map of rowNum, price
     * @throws OException
     */
    private Map<Integer, Double> getCalculatedPrice(Table tblGptTable, int priceColNum, int rowNumStart, int totalRows) throws OException {
        Map<Integer, Double> calcPrice = new HashMap<>();

        for (int rowNum = rowNumStart; rowNum <= totalRows; rowNum++) {
            double price = tblGptTable.getDouble(priceColNum, rowNum);

            if (price >= PRICE_THRESHOLD) {
                calcPrice.put(rowNum, price);
                break;
            }

        }

        return calcPrice;
    }

    /**
     * Capitalize group column values into group_temp column and flag rows that already have prices
     *
     * @param tblGptTable Grid point table with prices
     * @throws OException
     */
    private void capitalizeAndFlagNonZeroRows(Table tblGptTable) throws OException {
        int totalRows = tblGptTable.getNumRows();
        tblGptTable.addCol("group_temp", COL_TYPE_ENUM.COL_STRING);
        tblGptTable.addCol("flag", COL_TYPE_ENUM.COL_INT);

        int colNumPrice     = tblGptTable.getColNum("input.mid");
        int colNumGroup     = tblGptTable.getColNum("group");
        int colNumGroupTemp = tblGptTable.getColNum("group_temp");
        int colNumFlag      = tblGptTable.getColNum("flag");

        for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
            double price = tblGptTable.getDouble(colNumPrice, rowNum);
            String group = tblGptTable.getString(colNumGroup, rowNum);

            if (price >= PRICE_THRESHOLD) {
                tblGptTable.setInt(colNumFlag, rowNum, 0);
            } else {
                tblGptTable.setInt(colNumFlag, rowNum, 1);
            }

            tblGptTable.setString(colNumGroupTemp, rowNum, group.toUpperCase());
        }

    }

    /**
     * Get formatted price data
     *
     * @param priceDataFormatted Price table formatted to populate data into
     * @param priceDataRaw       Price table raw from excel
     * @param sheetNum           Sheet Number
     * @return
     * @throws OException
     */
    private Table getPriceDataFormatted(Table priceDataFormatted, Table priceDataRaw, int sheetNum) throws OException {

        if (!GarHasAValue.isTableValid(priceDataRaw)) {
            return null;
        }

        int rawNumCols = priceDataRaw.getNumCols();
        int rawNumRows = priceDataRaw.getNumRows();

        if (rawNumCols <= 0 || rawNumRows <= 0) {
            return priceDataFormatted;
        }

        priceDataFormatted.addNumRows(rawNumRows);

        for (int rowNum = 1; rowNum <= rawNumRows; rowNum++) {

            Table  tblPrices    = this.getPricesTableFormat();
            String priceDateStr = priceDataRaw.getString(GarMOBloombergConstants.EXCEL_DATA_DATE_COLUMN, rowNum);

            int priceDate = 0;

            if (priceDateStr != null && priceDateStr.contains("/")) {
                final int priceFileYearDate  = 0;
                final int priceFileYearMonth = 1;
                final int priceFileYearIndex = 2;
                String[]  dateParts          = priceDateStr.split("/");
                priceDateStr =
                    "20" + dateParts[priceFileYearIndex] + "/" + dateParts[priceFileYearDate] + "/" + dateParts[priceFileYearMonth];

                priceDate = OCalendar.parseString(priceDateStr);
            } else if (GarMOBloombergConstants.EXCEL_DATE_COLUMN_NAME.equalsIgnoreCase(priceDateStr)) {
                // Apache POI API behaves weirdly sometimes and fetches row 7 instead of row 8. Hence this handling is to ignore header row
                continue;
            } else {
                priceDate = Str.strToInt(priceDateStr) - GarConstants.EXCEL_ENDUR_JULIAN_DATE_DIFF;
            }

            priceDataFormatted.setTable("price_data", rowNum, tblPrices);
            priceDataFormatted.setInt("date", rowNum, priceDate);

            int numCols = rawNumCols / GarMOBloombergConstants.EXCEL_DATA_NUM_REPEATED_COLS;
            tblPrices.addNumRows(numCols);

            this.processPriceDataColumns(priceDataRaw, sheetNum, rowNum, tblPrices, numCols);
        }

        priceDataFormatted.deleteWhereValue("date", 0);

        return priceDataFormatted;
    }

    /**
     * Process prices raw table column data
     *
     * @param priceDataRaw Raw price data
     * @param sheetNum     Sheet Number
     * @param rowNum       Row Number
     * @param tblPrices    Prices table
     * @param numCols      Number of columns
     * @throws OException
     */
    private void processPriceDataColumns(Table priceDataRaw, int sheetNum, int rowNum, Table tblPrices, int numCols) throws OException {

        for (int colNum = 1; colNum <= numCols; colNum++) {
            int    priceColNum   =
                (colNum - 1) * GarMOBloombergConstants.EXCEL_DATA_NUM_REPEATED_COLS + GarMOBloombergConstants.EXCEL_DATA_PRICE_COLUMN;
            int    excelSheetNum = sheetNum + 1;
            int    excelRowNum   = rowNum + GarMOBloombergConstants.EXCEL_DATA_START_ROW;
            String priceString   = priceDataRaw.getString(priceColNum, rowNum);

            if (!this.canProcessFurther(priceColNum, excelSheetNum, excelRowNum, priceString)) {
                break;
            }

            String gridPoint = priceDataRaw.getString(
                (colNum - 1) * GarMOBloombergConstants.EXCEL_DATA_NUM_REPEATED_COLS + GarMOBloombergConstants.EXCEL_DATA_GRID_POINT_COLUMN,
                rowNum);

            // WARNING - check if below replacement is applicable for all the curves
            gridPoint = gridPoint.replace(" ", "-");

            if (0 == Str.isDouble(priceString)) {
                throw new OException("Invalid price \"" + priceString + "\" on Sheet" + excelSheetNum + " at Row: " + excelRowNum
                    + " Column: " + priceColNum);
            }

            double price = Str.strToDouble(priceString);

            tblPrices.setDouble("price", colNum, price);
            tblPrices.setString("grid_point", colNum, gridPoint);
        }

    }

    /**
     * Can Process further
     *
     * @param priceColNum   Price Column number
     * @param excelSheetNum Excel Sheet Number
     * @param excelRowNum   Excel Row Number
     * @param priceString   Price String format
     * @return
     * @throws OException
     */
    private boolean canProcessFurther(int priceColNum, int excelSheetNum, int excelRowNum, String priceString) throws OException {
        boolean canProcessFurther = true;

        if (null == priceString || priceString.trim().isEmpty()) {
            String message = "Skipped processing further values for the current row as empty values encountered" + " for Price on Sheet: "
                + excelSheetNum + " at Row: " + excelRowNum + " Column: " + priceColNum;

            this.addMessageToTable(WARNING, message);
            this.haveImportWarnings = true;

            canProcessFurther = false;
        } else if (priceString.startsWith("#N/A")) {
            String message = "Skipped processing further values for the current row as \"#N/A\" encountered" + " on Sheet: " + excelSheetNum
                + " at Row: " + excelRowNum + " Column: " + priceColNum;

            this.addMessageToTable(WARNING, message);
            this.haveImportWarnings = true;

            canProcessFurther = false;
        }

        return canProcessFurther;
    }

    /**
     * Get Price Data table format
     *
     * @return Price data table with columns added
     * @throws OException
     */
    private Table getPriceDataTableFormat() throws OException {
        Table tblPriceData = Table.tableNew();

        tblPriceData.addCol("date", COL_TYPE_ENUM.COL_INT);
        tblPriceData.addCol("price_data", COL_TYPE_ENUM.COL_TABLE);

        return tblPriceData;
    }

    /**
     * Get Prices table format
     *
     * @return Prices table with columns added
     * @throws OException
     */
    private Table getPricesTableFormat() throws OException {
        Table tblPrices = Table.tableNew();

        tblPrices.addCol("price", COL_TYPE_ENUM.COL_DOUBLE);
        tblPrices.addCol("grid_point", COL_TYPE_ENUM.COL_STRING);

        return tblPrices;
    }
}
