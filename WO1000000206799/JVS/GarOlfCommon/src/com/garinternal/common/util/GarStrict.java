package com.garinternal.common.util;

/*
 * File Name:                      GarStrict.java
 * Script Type:                    INCLUDE
 * Parameter Script:               None
 * Display Script:                 None
 *
 * Description:
 * This script contains generic methods to have strict checks on operations performed
 *
 * ---------------------------------------------------------------------------
 * REQ No          | Release Date| Author       | Changes
 * ---------------------------------------------------------------------------
 * WO1000000135255 |             | Khalique     | Initial Version
 *                 |             |              |
 * ---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.GAR_FILE_EXTENSION_ENUM;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.fnd.UtilBase;
import java.io.File;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarStrict {

    /**
     * Constructor
     */
    private GarStrict() {
        // do nothing
    }

    /**
     * Query database
     *
     * @param sql    SQL query
     * @param logger GarLogger instance
     * @return SQL results in table
     * @throws OException {@link OException}
     */
    public static Table query(String sql, GarLogger logger) throws OException {
        return query(sql, "", logger);
    }

    /**
     * Query database
     *
     * @param sql      SQL query
     * @param errorMsg Error Message to append in case of exception
     * @param logger   GarLogger instance
     * @return SQL results in table
     * @throws OException {@link OException}
     */
    public static Table query(String sql, String errorMsg, GarLogger logger) throws OException {
        Table table = Table.tableNew();
        GarStrict.query(table, sql, errorMsg, logger);

        return table;
    }

    /**
     * Query database
     *
     * @param table  Table to return the query data into
     * @param sql    SQL query
     * @param logger GarLogger instance
     * @throws OException {@link OException}
     */
    public static void query(Table table, String sql, GarLogger logger) throws OException {
        GarStrict.query(table, sql, "", logger);
    }

    /**
     * Query database
     *
     * @param table    Table to return the query data into
     * @param sql      SQL query
     * @param errorMsg Error message to append in case of exception
     * @param logger   GaaLogger instance
     * @throws OException {@link OException}
     */
    public static void query(Table table, String sql, String errorMsg, GarLogger logger) throws OException {

        try {
            DBaseTable.execISql(table, sql);
        } catch (OException e) {
            String exceptionMsg = e.getMessage();

            if (null != logger) {
                logger.error("SQL failed for the following reason: " + exceptionMsg);
                logger.error("\n" + sql + "\n");
            }

            throw new OException(errorMsg + "\n" + e.getMessage());
        }

    }

    /**
     * Query database
     *
     * @param sql    SQL query
     * @param logger GarLogger instance
     * @return SQL results in table
     * @throws OException {@link OException}
     */
    public static Table query(StringBuilder sql, GarLogger logger) throws OException {
        return query(sql.toString(), logger);
    }

    /**
     * Query database
     *
     * @param sql      SQL query
     * @param errorMsg Error Message to append in case of exception
     * @param logger   GarLogger instance
     * @return SQL results in table
     * @throws OException {@link OException}
     */
    public static Table query(StringBuilder sql, String errorMsg, GarLogger logger) throws OException {
        return query(sql.toString(), errorMsg, logger);
    }

    /**
     * Query database
     *
     * @param table  Table to return the query data into
     * @param sql    SQL query
     * @param logger GarLogger instance
     * @throws OException {@link OException}
     */
    public static void query(Table table, StringBuilder sql, GarLogger logger) throws OException {
        query(table, sql.toString(), logger);
    }

    /**
     * Query database
     *
     * @param table    Table to return the query data into
     * @param sql      SQL query
     * @param errorMsg Error message to append in case of exception
     * @param logger   GaaLogger instance
     * @throws OException {@link OException}
     */
    public static void query(Table table, StringBuilder sql, String errorMsg, GarLogger logger) throws OException {
        query(table, sql.toString(), errorMsg, logger);
    }

    /**
     * Select data from one table to another
     *
     * @param tblDestination Table to move data into
     * @param tblSource      Table to move data from
     * @param what           Columns to be moved
     * @param where          Conditions for moving data
     * @throws OException {@link OException}
     */
    public static void select(Table tblDestination, Table tblSource, String what, String where) throws OException {

        if (null == tblSource) {
            throw new OException("Source Table is empty");
        }

        if (null == tblDestination) {
            throw new OException("Destination Table is empty");
        }

        try {
            tblDestination.select(tblSource, what, where);
        } catch (OException e) {

            try {
                saveTablesToCSV(tblSource, tblDestination, null);
            } catch (Exception e1) {
                // do nothing
            }

            String message = "GarStrict.select failed for the following reason: \n\n" + e.getMessage();
            throw new OException(message);
        }

    }

    /**
     * Select data from one table to another. Throw exception if number of rows changed after select
     *
     * @param tblDestination Table to move data into
     * @param tblSource      Table to move data from
     * @param what           Columns to be moved
     * @param where          Conditions for moving data
     * @throws OException {@link OException}
     */
    public static void selectConst(Table tblDestination, Table tblSource, String what, String where) throws OException {

        if (null == tblSource) {
            throw new OException("Source Table is empty");
        }

        if (null == tblDestination) {
            throw new OException("Destination Table is empty");
        }

        Table   tblDestinationTemp = Util.NULL_TABLE;
        boolean numRowsChanged     = false;

        try {
            tblDestinationTemp = tblDestination.copyTable();
            int numRowsDestinationStart = tblDestination.getNumRows();
            tblDestination.select(tblSource, what, where);

            int numRowsDestAfterSelect = tblDestination.getNumRows();

            if (numRowsDestAfterSelect != numRowsDestinationStart) {
                numRowsChanged = true;
                throw new OException(
                    "Number of rows in destination table changed from " + numRowsDestinationStart + " to " + numRowsDestAfterSelect);
            }

        } catch (OException e) {

            try {

                if (numRowsChanged) {
                    saveTablesToCSV(tblSource, tblDestination, tblDestinationTemp);
                } else {
                    saveTablesToCSV(tblSource, tblDestination, null);
                }

            } catch (Exception e1) {
                // do nothing
            }

            String message = "GarStrict.selectConst failed for the following reason: " + e.getMessage();
            throw new OException(message);
        } finally {
            GarSafe.destroy(tblDestinationTemp);
        }

    }

    /**
     * Save Tables to CSV. To be used when error occurs in table select operations
     *
     * @param tblSource           Source table
     * @param tblDestBeforeSelect Destination table before select
     * @param tblDestAfterSelect  Destination table before select
     * @throws OException
     */
    private static void saveTablesToCSV(Table tblSource, Table tblDestBeforeSelect, Table tblDestAfterSelect) throws OException {
        String    timeStamp                  = GarDateTimeUtil.getTimeStampNow();
        final int stackTraceCallerClassIndex = 2;
        String    fileName                   = Thread.currentThread().getStackTrace()[stackTraceCallerClassIndex].getClassName();
        String    pathSeparator              = File.separator;

        int    currentDate        = UtilBase.getTradingDate();
        String currentReportPath  = UtilBase.reportGetDirForDate(currentDate) + "\\SELECT_FAIL";
        String sourceFilePath     =
            currentReportPath + pathSeparator + fileName + "_SOURCE_" + timeStamp + "." + GAR_FILE_EXTENSION_ENUM.CSV.toString();
        String destFilePathBefore = "";

        if (GarHasAValue.isTableValid(tblDestAfterSelect)) {
            destFilePathBefore =
                currentReportPath + pathSeparator + fileName + "_DEST_BEFORE_" + timeStamp + "." + GAR_FILE_EXTENSION_ENUM.CSV.toString();
        } else {
            destFilePathBefore =
                currentReportPath + pathSeparator + fileName + "_DEST_" + timeStamp + "." + GAR_FILE_EXTENSION_ENUM.CSV.toString();
        }

        String destFilePathAfter =
            currentReportPath + pathSeparator + fileName + "_DEST_AFTER_" + timeStamp + "." + GAR_FILE_EXTENSION_ENUM.CSV.toString();

        // print CSV
        tblSource.printTableDumpToFile(sourceFilePath);
        tblDestBeforeSelect.printTableDumpToFile(destFilePathBefore);

        if (GarHasAValue.isTableValid(tblDestAfterSelect)) {
            tblDestAfterSelect.printTableDumpToFile(destFilePathAfter);
        }

    }
}
