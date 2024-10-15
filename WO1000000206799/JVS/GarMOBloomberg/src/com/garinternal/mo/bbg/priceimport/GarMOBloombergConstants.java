package com.garinternal.mo.bbg.priceimport;

/*
File Name:                      GarMOBloombergConstants.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script will be used to store constant values that may be used across bloomberg scripts

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarMOBloombergConstants {
    /*
     * Bloomberg formatted price excel file name
     */
    public static final String BLOOMBERG_FORMATTED_PRICE_FILE_NAME = "BloombergPriceImportFormatted";

    /*
     * Bloomberg price import errors excel file name
     */
    public static final String BLOOMBERG_PRICE_IMPORT_ERRORS_FILE_NAME = "BloombergPriceImportErrors";

    /*
     * Row where data starts in Excel for Bloomberg Price Import files
     */
    public static final int EXCEL_DATA_START_ROW = 6;

    /**
     * Date Column name in Bloomberg Excel file
     */
    public static final String EXCEL_DATE_COLUMN_NAME = "Date";

    /**
     * Indicates how many columns are repeated in bloomberg price import excel
     */
    public static final int EXCEL_DATA_NUM_REPEATED_COLS = 3;

    /**
     * Indicates date column number in bloomberg price import excel
     */
    public static final int EXCEL_DATA_DATE_COLUMN = 1;

    /**
     * Indicates price column number in bloomberg price import excel
     */
    public static final int EXCEL_DATA_PRICE_COLUMN = 2;

    /**
     * Indicates grid point column number in bloomberg price import excel
     */
    public static final int EXCEL_DATA_GRID_POINT_COLUMN = 3;

    /**
     * Constructor
     */
    private GarMOBloombergConstants() {
        // do nothing
    }
}
