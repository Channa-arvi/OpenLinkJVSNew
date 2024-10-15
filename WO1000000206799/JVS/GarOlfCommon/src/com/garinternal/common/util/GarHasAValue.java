package com.garinternal.common.util;

/*
File Name:                      GarHasAValue.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains methods to check if a given variable/objects is empty or null or invalid

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Transaction;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarHasAValue {

    /**
     * Constructor
     */
    private GarHasAValue() {
        // do nothing
    }

    /**
     * Check if the given string has a value
     *
     * @param string     String to be checked
     * @param trimSpaces Flag to trim spaces before checking
     * @return True/False
     */
    public static boolean hasAValue(String string, boolean trimSpaces) {
        boolean hasAValue = false;

        if (null != string) {

            if (trimSpaces) {
                hasAValue = !string.trim().isEmpty();
            } else {
                hasAValue = !string.isEmpty();
            }

        }

        return hasAValue;
    }

    /**
     * Check if the given StringBuilder has a value
     *
     * @param string     StringBulder to be checked
     * @param trimSpaces Flag to trim spaces before checking
     * @return True/False
     */
    public static boolean hasAValue(StringBuilder string, boolean trimSpaces) {
        boolean hasAValue = false;

        if (null != string) {

            if (trimSpaces) {
                hasAValue = !string.toString().trim().isEmpty();
            } else {
                hasAValue = string.length() != 0;
            }

        }

        return hasAValue;
    }

    /**
     * Method to check if a table is valid
     *
     * @param table Table instance
     * @return True/False
     */
    public static boolean isTableValid(Table table) {
        boolean isValid = false;

        try {

            if (null != table && Util.NULL_TABLE != table && Table.isValidTable(table)) {
                isValid = true;
            }

        } catch (OException e) {
            // do nothing
        }

        return isValid;
    }

    /**
     * Method to check if a table is empty. Invalid/null tables are considered empty
     *
     * @param table Table instance
     * @return True/False
     */
    public static boolean isTableEmpty(Table table) {
        boolean isEmpty = true;

        try {

            if (null != table && Util.NULL_TABLE != table && Table.isValidTable(table) && !Table.isEmptyTable(table)) {
                isEmpty = false;
            }

        } catch (OException e) {
            // do nothing
        }

        return isEmpty;
    }

    /**
     * Method to check if a Transaction is valid
     *
     * @param tran Transaction instance
     * @return True/False
     */
    public static boolean isTranValid(Transaction tran) {
        boolean isValid = false;

        try {

            if (null != tran && Util.NULL_TRAN != tran && Transaction.isNull(tran) == 1) {
                isValid = true;
            }

        } catch (OException e) {
            // do nothing
        }

        return isValid;
    }

    /**
     * Check if an array is Empty
     *
     * @param array Array
     * @return True/False
     */
    public static boolean isEmpty(Object[] array) {
        boolean result = false;

        if (array == null || array.length <= 0) {
            result = true;
        }

        return result;
    }
}
