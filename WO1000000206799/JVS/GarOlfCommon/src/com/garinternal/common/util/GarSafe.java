package com.garinternal.common.util;

/*
File Name:                      GarSafe.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains methods that handle object creation/destruction without throwing exceptions

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.olf.openjvs.EmailMessage;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Query;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarSafe {

    /**
     * Constructor
     */
    private GarSafe() {
        // do nothing
    }

    /**
     * Destroy table pointers
     * 
     * @param tables Tables to be destroyed
     */
    public static void destroy(Table... tables) {
        int numTables = tables.length;

        for (int index = 0; index < numTables; index++) {
            Table table = tables[index];

            try {

                if (null != table && table != Util.NULL_TABLE && Table.isValidTable(table)) {
                    table.destroy();
                } else {
                    tables[index] = null;
                }

            } catch (Exception e) {
                tables[index] = null;
            }

        }

    }

    /**
     * Clear Query Ids from query_result** tables
     * 
     * @param queryIds Query Ids to be cleared
     */
    public static void clear(int... queryIds) {

        for (int queryId : queryIds) {

            if (queryId <= 0) {
                continue;
            }

            try {
                Query.clear(queryId);
            } catch (Exception e) {
                // do nothing
            }

        }

    }

    /**
     * Close Excel Workbook
     * 
     * @param excelUtils GarExcelReadUtil instances
     */
    public static void closeExcelWorkbook(GarExcelReadUtil... excelUtils) {

        if (excelUtils == null) {
            return;
        }

        for (GarExcelReadUtil excelUtil : excelUtils) {

            try {

                if (null != excelUtil) {
                    excelUtil.closeWorkbook();
                }

            } catch (Exception e) {
                // do nothing
            }

        }

    }

    /**
     * Close Excel Workbook
     * 
     * @param excelUtils GarExcelWriteUtil instances
     */
    public static void closeExcelWorkbook(GarExcelWriteUtil... excelUtils) {

        if (excelUtils == null) {
            return;
        }

        for (GarExcelWriteUtil excelUtil : excelUtils) {

            try {

                if (null != excelUtil) {
                    excelUtil.closeWorkbook();
                }

            } catch (Exception e) {
                // do nothing
            }

        }

    }

    /**
     * Dispose the EmailMessage object
     * 
     * @param email EmailMessage instance
     */
    public static void dispose(EmailMessage email) {

        if (null != email) {
            email.dispose();
        }

    }

    /**
     * Destroy Transaction pointers
     * 
     * @param transactions Transactions to be destroyed
     */
    public static void destroy(Transaction... transactions) {
        int numTrans = transactions.length;

        for (int index = 0; index < numTrans; index++) {
            Transaction tran = transactions[index];

            try {

                if (null != tran && tran != Util.NULL_TRAN && Transaction.isNull(tran) == 1) {
                    tran.destroy();
                } else {
                    transactions[index] = null;
                }

            } catch (Exception e) {
                transactions[index] = null;
            }

        }

    }

    /**
     * Dispose GarConstRepoUtil
     * 
     * @param constRepo {@link GarConstRepoUtil} instance
     */
    public static void dispose(GarConstRepoUtil constRepo) {

        try {

            if (null != constRepo) {
                constRepo.dispose();
            }

        } catch (Exception e) {
            // do nothing
        }

    }
}
