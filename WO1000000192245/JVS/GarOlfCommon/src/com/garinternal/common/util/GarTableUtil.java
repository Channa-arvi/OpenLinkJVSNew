package com.garinternal.common.util;

/*
File Name:                      GarTableUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains utility methods for handling Tables

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class GarTableUtil {

    private enum CONVERSION_TYPE {
        //@formatter:off
        DATA_TYPE_CONVERSION,
        UPPER_CASE,
        LOWER_CASE,
        CAMEL_CASE,
        ;
        //@formatter:on
    }

    /**
     * Constructor
     */
    private GarTableUtil() {
        // do nothing
    }

    /**
     * Convert column data to string type. Supported fromcolumns for conversion are string, int, int64, and double
     *
     * @param table       Table
     * @param fromColName Convert from col name
     * @param toColName   Convert to col name
     * @throws OException {@link OException}
     */
    public static void convertColToString(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_STRING.toInt(), CONVERSION_TYPE.DATA_TYPE_CONVERSION);
    }

    /**
     * Convert column data to int type. Supported fromcolumns for conversion are string, int, int64, and double
     *
     * @param table       Table
     * @param fromColName Convert from col name
     * @param toColName   Convert to col name
     * @throws OException {@link OException}
     */
    public static void convertColToInt(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_INT.toInt(), CONVERSION_TYPE.DATA_TYPE_CONVERSION);
    }

    /**
     * Convert column data to int64 type. Supported fromcolumns for conversion are string, int, int64, and double
     *
     * @param table       Table
     * @param fromColName Convert from col name
     * @param toColName   Convert to col name
     * @throws OException {@link OException}
     */
    public static void convertColToInt64(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_INT64.toInt(), CONVERSION_TYPE.DATA_TYPE_CONVERSION);
    }

    /**
     * Convert column data to double type. Supported fromcolumns for conversion are string, int, int64, and double
     *
     * @param table       Table
     * @param fromColName Convert from col name
     * @param toColName   Convert to col name
     * @throws OException {@link OException}
     */
    public static void convertColToDouble(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_DOUBLE.toInt(), CONVERSION_TYPE.DATA_TYPE_CONVERSION);
    }

    /**
     * Convert a column to another column type
     *
     * @param table          Table
     * @param fromColName    From column Name
     * @param toColName      To column Name
     * @param toColType      To column Type
     * @param conversionType Conversion Type
     * @throws OException {@link OException}
     */
    private static void convertColType(Table table, String fromColName, String toColName, int toColType, CONVERSION_TYPE conversionType)
        throws OException {
        COL_TYPE_ENUM toColTypeEnum = COL_TYPE_ENUM.fromInt(toColType);

        if (toColTypeEnum == null) {
            throw new OException("Invalid column type number " + toColType + " passed");
        } else if (!GarHasAValue.isTableValid(table)) {
            throw new OException("Invalid table passed");
        } else if (table.getColNum(fromColName) <= 0) {
            throw new OException("From Column Name \"" + fromColName + "\" does not exist");
        } else if (table.getColNum(toColName) > 0 && table.getColType(toColName) != toColType) {
            throw new OException(
                "To Column Name \"" + toColName + "\" already exists and is not of type " + COL_TYPE_ENUM.fromInt(toColType).toString());
        }

        boolean areBothColNamesSame = false;

        // if both column names are same, add _temp to the name temporarily
        if (fromColName.equalsIgnoreCase(toColName)) {
            areBothColNamesSame = true;
            toColName           = fromColName + "_temp1234";
        }

        // add to column if it doesn't exist
        if (table.getColNum(toColName) <= 0) {
            table.addCol(toColName, toColTypeEnum);
        }

        int totalRows   = table.getNumRows();
        int fromColNum  = table.getColNum(fromColName);
        int toColNum    = table.getColNum(toColName);
        int fromColType = table.getColType(fromColNum);

        for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
            convertAndSetValue(table, rowNum, fromColNum, toColNum, fromColType, toColType, conversionType);
        }

        if (areBothColNamesSame) {
            table.delCol(fromColNum);
            table.setColName(toColName, fromColName);
        }

    }

    /**
     * Convert the column
     *
     * @param table          Table
     * @param rowNum         Row Number
     * @param fromColNum     From column number
     * @param toColNum       To column number
     * @param fromColType    From column type
     * @param toColType      To column Type
     * @param conversionType Conversion Type
     * @throws OException {@link OException}
     */
    private static void convertAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int fromColType, int toColType,
        CONVERSION_TYPE conversionType) throws OException {

        if (fromColType != COL_TYPE_ENUM.COL_STRING.toInt() && fromColType != COL_TYPE_ENUM.COL_INT.toInt()
            && fromColType != COL_TYPE_ENUM.COL_INT64.toInt() && fromColType != COL_TYPE_ENUM.COL_DOUBLE.toInt()
            && toColType != COL_TYPE_ENUM.COL_STRING.toInt() && toColType != COL_TYPE_ENUM.COL_INT.toInt()
            && toColType != COL_TYPE_ENUM.COL_INT64.toInt() && toColType != COL_TYPE_ENUM.COL_DOUBLE.toInt()) {

            throw new OException("Column types unsupported for conversion. From Col Type = " + COL_TYPE_ENUM.fromInt(fromColType)
            + ". To Col Type = " + COL_TYPE_ENUM.fromInt(toColType));
        } else if (conversionType == CONVERSION_TYPE.DATA_TYPE_CONVERSION) {
            convertDataTypeAndSetValue(table, rowNum, fromColNum, toColNum, fromColType, toColType);
        } else if (conversionType == CONVERSION_TYPE.LOWER_CASE) {
            convertStringCaseAndSetValue(table, rowNum, fromColNum, toColNum, fromColType, toColType, CONVERSION_TYPE.LOWER_CASE);
        } else if (conversionType == CONVERSION_TYPE.UPPER_CASE) {
            convertStringCaseAndSetValue(table, rowNum, fromColNum, toColNum, fromColType, toColType, CONVERSION_TYPE.UPPER_CASE);
        } else if (conversionType == CONVERSION_TYPE.CAMEL_CASE) {
            convertStringCaseAndSetValue(table, rowNum, fromColNum, toColNum, fromColType, toColType, CONVERSION_TYPE.UPPER_CASE);

            String value = table.getString(fromColNum, rowNum);

            if (null != value && !value.isEmpty()) {
                value = Stream.of(value.split(" ")).map(v -> v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            } else {
                value = "";
            }

            table.setString(toColNum, rowNum, value);
        }

    }

    /**
     * Convert string to upper/lower/camel cases
     *
     * @param table          Table
     * @param rowNum         Row Number
     * @param fromColNum     From column number
     * @param toColNum       To column number
     * @param fromColType    From column type
     * @param toColType      To column Type
     * @param conversionType Conversion Type
     * @throws OException {@link OException}
     */
    private static void convertStringCaseAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int fromColType, int toColType,
        CONVERSION_TYPE conversionType) throws OException {

        if (fromColType == COL_TYPE_ENUM.COL_STRING.toInt() && toColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
            String value = table.getString(fromColNum, rowNum);
            value = (null != value ? value : "");

            if (!value.isEmpty()) {

                if (conversionType == CONVERSION_TYPE.UPPER_CASE) {
                    value = value.toUpperCase();
                } else if (conversionType == CONVERSION_TYPE.LOWER_CASE) {
                    value = value.toLowerCase();
                } else if (conversionType == CONVERSION_TYPE.CAMEL_CASE) {
                    value = Stream.of(value.split(" ")).map(v -> v.substring(0, 1).toUpperCase() + v.substring(1).toLowerCase())
                        .collect(Collectors.joining(" "));
                }

            }

            table.setString(toColNum, rowNum, value);
        } else {
            throw new OException("Cannot convert to " + conversionType.name() + " as column type is not string. From column type = "
                + COL_TYPE_ENUM.fromInt(fromColType) + ". To Column type = " + COL_TYPE_ENUM.fromInt(toColType));
        }

    }

    /**
     * Convert data type and set to cell
     *
     * @param table       Table
     * @param rowNum      Row Number
     * @param fromColNum  From column number
     * @param toColNum    To column number
     * @param fromColType From column type
     * @param toColType   To column Type
     * @throws OException {@link OException}
     */
    private static void convertDataTypeAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int fromColType, int toColType)
        throws OException {

        if (fromColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
            convertStringColAndSetValue(table, rowNum, fromColNum, toColNum, toColType);
        } else if (fromColType == COL_TYPE_ENUM.COL_INT.toInt()) {
            convertIntColAndSetValue(table, rowNum, fromColNum, toColNum, toColType);
        } else if (fromColType == COL_TYPE_ENUM.COL_INT64.toInt()) {
            convertInt64ColAndSetValue(table, rowNum, fromColNum, toColNum, toColType);
        } else if (fromColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
            convertDoubleColAndSetValue(table, rowNum, fromColNum, toColNum, toColType);
        }

    }

    /**
     * Convert double column to other data type and set to cell
     *
     * @param table      Table
     * @param rowNum     Row Number
     * @param fromColNum From column number
     * @param toColNum   To column number
     * @param toColType  To column Type
     * @throws OException {@link OException}
     */
    private static void convertDoubleColAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int toColType)
        throws OException {

        if (toColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
            double value = table.getDouble(fromColNum, rowNum);
            table.setString(toColNum, rowNum, String.valueOf(value));
        } else if (toColType == COL_TYPE_ENUM.COL_INT.toInt()) {
            double value = table.getDouble(fromColNum, rowNum);
            table.setInt(toColNum, rowNum, (int) value);
        } else if (toColType == COL_TYPE_ENUM.COL_INT64.toInt()) {
            double value = table.getDouble(fromColNum, rowNum);
            table.setInt64(toColNum, rowNum, (long) value);
        } else if (toColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
            double value = table.getDouble(fromColNum, rowNum);
            table.setDouble(toColNum, rowNum, value);
        }

    }

    /**
     * Convert Int64 column to other data type and set to cell
     *
     * @param table      Table
     * @param rowNum     Row Number
     * @param fromColNum From column number
     * @param toColNum   To column number
     * @param toColType  To column Type
     * @throws OException {@link OException}
     */
    private static void convertInt64ColAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int toColType) throws OException {

        if (toColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
            long value = table.getInt64(fromColNum, rowNum);
            table.setString(toColNum, rowNum, String.valueOf(value));
        } else if (toColType == COL_TYPE_ENUM.COL_INT.toInt()) {
            long value = table.getInt64(fromColNum, rowNum);
            table.setInt(toColNum, rowNum, (int) value);
        } else if (toColType == COL_TYPE_ENUM.COL_INT64.toInt()) {
            long value = table.getInt64(fromColNum, rowNum);
            table.setInt64(toColNum, rowNum, value);
        } else if (toColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
            long value = table.getInt64(fromColNum, rowNum);
            table.setDouble(toColNum, rowNum, value);
        }

    }

    /**
     * Convert int column to other data type and set to cell
     *
     * @param table      Table
     * @param rowNum     Row Number
     * @param fromColNum From column number
     * @param toColNum   To column number
     * @param toColType  To column Type
     * @throws OException {@link OException}
     */
    private static void convertIntColAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int toColType) throws OException {

        if (toColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
            int value = table.getInt(fromColNum, rowNum);
            table.setString(toColNum, rowNum, String.valueOf(value));
        } else if (toColType == COL_TYPE_ENUM.COL_INT.toInt()) {
            int value = table.getInt(fromColNum, rowNum);
            table.setInt(toColNum, rowNum, value);
        } else if (toColType == COL_TYPE_ENUM.COL_INT64.toInt()) {
            int value = table.getInt(fromColNum, rowNum);
            table.setInt64(toColNum, rowNum, value);
        } else if (toColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
            int value = table.getInt(fromColNum, rowNum);
            table.setDouble(toColNum, rowNum, value);
        }

    }

    /**
     * Convert string column to other data type and set to cell
     *
     * @param table      Table
     * @param rowNum     Row Number
     * @param fromColNum From column number
     * @param toColNum   To column number
     * @param toColType  To column Type
     * @throws OException {@link OException}
     */
    private static void convertStringColAndSetValue(Table table, int rowNum, int fromColNum, int toColNum, int toColType)
        throws OException {

        if (toColType == COL_TYPE_ENUM.COL_STRING.toInt()) {
            String value = table.getString(fromColNum, rowNum);
            table.setString(toColNum, rowNum, value);
        } else if (toColType == COL_TYPE_ENUM.COL_INT.toInt()) {
            String value = table.getString(fromColNum, rowNum);

            int intValue = (int) Double.parseDouble(value);
            table.setInt(toColNum, rowNum, intValue);
        } else if (toColType == COL_TYPE_ENUM.COL_INT64.toInt()) {
            String value = table.getString(fromColNum, rowNum);

            long longValue = (long) Double.parseDouble(value);
            table.setInt64(toColNum, rowNum, longValue);
        } else if (toColType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
            String value = table.getString(fromColNum, rowNum);
            table.setDouble(toColNum, rowNum, Double.parseDouble(value));
        }

    }

    /**
     * Convert a string column to upper case and populate the values in the To Column Name specified
     *
     * @param table       Table
     * @param fromColName Column Name containing String data
     * @param toColName   Column Name to populate converted upper case String data
     * @throws OException {@link OException}
     */
    public static void convertStringColToUpperCase(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_STRING.toInt(), CONVERSION_TYPE.UPPER_CASE);
    }

    /**
     * Convert a string column to lower case and populate the values in the To Column Name specified
     *
     * @param table       Table
     * @param fromColName Column Name containing String data
     * @param toColName   Column Name to populate converted lower case String data
     * @throws OException {@link OException}
     */
    public static void convertStringColToLowerCase(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_STRING.toInt(), CONVERSION_TYPE.LOWER_CASE);
    }

    /**
     * Convert a string column to camel case and populate the values in the To Column Name specified
     *
     * @param table       Table
     * @param fromColName Column Name containing String data
     * @param toColName   Column Name to populate converted upper case String data
     * @throws OException {@link OException}
     */
    public static void convertStringColToCamelCase(Table table, String fromColName, String toColName) throws OException {
        convertColType(table, fromColName, toColName, COL_TYPE_ENUM.COL_STRING.toInt(), CONVERSION_TYPE.CAMEL_CASE);
    }

    /**
     * Clear all the rows and columns of a table
     *
     * @param table Table to clear
     * @throws OException {@link OException}
     */
    public static void clearTable(Table table) throws OException {

        if (GarHasAValue.isTableValid(table)) {
            table.clearRows();

            int numCols = table.getNumCols();

            for (int colNum = 1; colNum <= numCols; colNum++) {
                table.delCol(1);
            }

        }

    }

    /**
     * Convert table to HTML with formatting
     *
     * @param table table
     * @return Formatted HTML
     * @throws OException {@link OException}
     */
    public static String convertTableToHtml(Table table) throws OException {

        if (GarHasAValue.isTableEmpty(table)) {
            throw new OException("Invalid table passed to convert to HTML");
        }

        //@formatter:off
        String css = "\n <style>"
            + "\n #customers {"
            + "\n   font-family: Arial, Helvetica, sans-serif;"
            + "\n   border-collapse: collapse;"
            + "\n   width: 100%;"
            + "\n }"
            + "\n "
            + "\n #customers td, #customers th {"
            + "\n   border: 1px solid #ddd;"
            + "\n   padding: 8px;" + "\n }"
            + "\n "
            + "\n #customers tr:nth-child(even){background-color: #f2f2f2;}"
            + "\n "
            + "\n #customers tr:hover {background-color: #ddd;}"
            + "\n "
            + "\n #customers th {"
            + "\n   padding-top: 12px;"
            + "\n   padding-bottom: 12px;"
            + "\n   text-align: left;"
            + "\n   background-color: #b3b1b1;"
            + "\n   color: white;"
            + "\n }"
            + "\n </style>";
        //@formatter:on

        StringBuilder html = new StringBuilder() //
            .append("<table id=\"customers\">");

        int totalRows = table.getNumRows();
        int totalCols = table.getNumCols();

        // add columns
        html.append("\n\t<tr>");

        for (int colNum = 1; colNum <= totalCols; colNum++) {
            String header   = "";
            String colTitle = table.getColTitle(colNum);

            if (GarHasAValue.hasAValue(colTitle, true)) {
                header = colTitle;
            } else {
                header = table.getColName(colNum);
            }

            html.append("\n\t\t<th>");
            html.append(header);
            html.append("</th>");
        }

        html.append("\n\t</tr>");

        // add data
        for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
            html.append("\n\t<tr>");

            for (int colNum = 1; colNum <= totalCols; colNum++) {
                html.append("\n\t\t<td>");
                html.append(getDataAsString(table, rowNum, colNum));
                html.append("</td>");
            }

            html.append("\n\t</tr>");
        }

        html.append("\n</table>");
        return css + html.toString();
    }

    /**
     * Get the cell data as string irrespective of column type
     *
     * @param table  Table
     * @param rowNum Row number
     * @param colNum Column number
     * @return Cell data in string format
     * @throws OException {@link OException}
     */
    public static String getDataAsString(Table table, int rowNum, int colNum) throws OException {

        if (GarHasAValue.isTableEmpty(table)) {
            throw new OException("Empty table passed to getDataAsString()");
        }

        String data = "";

        if (table.getNumRows() >= rowNum && table.getNumCols() >= colNum) {
            int colType = table.getColType(colNum);

            if (colType == COL_TYPE_ENUM.COL_STRING.toInt()) {
                data = table.getString(colNum, rowNum);
            } else if (colType == COL_TYPE_ENUM.COL_INT.toInt()) {
                data = Integer.toString(table.getInt(colNum, rowNum));
            } else if (colType == COL_TYPE_ENUM.COL_INT64.toInt()) {
                data = Long.toString(table.getInt64(colNum, rowNum));
            } else if (colType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
                data = Double.toString(table.getDouble(colNum, rowNum));
            } else if (colType == COL_TYPE_ENUM.COL_DATE.toInt()) {
                data = Integer.toString(table.getInt(colNum, rowNum));
            } else {
                throw new OException("Unhandled column type: " + COL_TYPE_ENUM.fromInt(colType).toString());
            }

        }

        return data;
    }
}
