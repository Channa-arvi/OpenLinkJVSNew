package com.garinternal.common.constants;

/*
File Name:                      GarConstants.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script will be used to store constant values that may be used across multiple scripts
NOTE: This should not be used to store constant values that have corresponding mapping tables in DB like portfolio, personnel etc.

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

public final class GarConstants {

    /*
     * Error log folder name
     */
    public static final String ERROR_LOG_FOLDER = "error_logs";

    /*
     * Error log folder name
     */
    public static final String REPORTS_FOLDER = "reports";

    /*
     * From email address to be used for sending automated emails
     */
    public static final String FROM_EMAIL_ADDRESS = "noreply@sinarmas-agri.com";

    /*
     * Difference between excel and endur julian dates
     */
    public static final int EXCEL_ENDUR_JULIAN_DATE_DIFF = 2;

    /**
     * Constructor
     */
    private GarConstants() {
        // do nothing
    }
}
