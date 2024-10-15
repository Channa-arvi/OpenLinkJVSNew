package com.garinternal.common.util;

/*
File Name:                      GarConstRepoUtil.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Script to access USER_const_repository table

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.garinternal.common.enums.GAR_USER_TABLE_ENUM;
import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarConstRepoUtil {

    private Table tblConstRepo;
    private int   totalRows;

    /**
     * Constructor
     */
    @SuppressWarnings("unused")
    private GarConstRepoUtil() {
        // do nothing
    }

    /**
     * Constructor
     * 
     * @param context    Context
     * @param subContext Sub Context
     * @param logger     GarLogger instance
     * @throws OException {@link OException}
     */
    public GarConstRepoUtil(String context, String subContext, GarLogger logger) throws OException {
        this.tblConstRepo = this.getConstRepoData(context, subContext, logger);

        if (!GarHasAValue.isTableEmpty(this.tblConstRepo)) {
            this.tblConstRepo.sortCol("name");

            this.totalRows = this.tblConstRepo.getNumRows();
        }

    }

    /**
     * Constructor
     * 
     * @param context    Context
     * @param subContext Sub Context
     * @param name       Name
     * @param logger     GarLogger instance
     * @throws OException {@link OException}
     */
    public GarConstRepoUtil(String context, String subContext, String name, GarLogger logger) throws OException {
        this.tblConstRepo = this.getConstRepoData(context, subContext, name, logger);

        if (!GarHasAValue.isTableEmpty(this.tblConstRepo)) {
            this.totalRows = this.tblConstRepo.getNumRows();
        }

    }

    /**
     * Get String Value
     * 
     * @return
     * @throws OException
     */
    public String getStringValue() throws OException {
        String value = "";

        if (this.totalRows > 0) {
            value = this.tblConstRepo.getString("string_value", 1);
        }

        return value;
    }

    /**
     * Get Int value
     *
     * @return
     * @throws OException
     */
    public int getIntValue() throws OException {
        int value = 0;

        if (this.totalRows > 0) {
            value = this.tblConstRepo.getInt("int_value", 1);
        }

        return value;
    }

    /**
     * Get Double Value
     * 
     * @return
     * @throws OException
     */
    public double getDoubleValue() throws OException {
        double value = 0.0;

        if (this.totalRows > 0) {
            value = this.tblConstRepo.getDouble("double_value", 1);
        }

        return value;
    }

    /**
     * Get Date Time value
     * 
     * @return
     * @throws OException
     */
    public ODateTime getDateValue() throws OException {
        ODateTime value = null;

        if (this.totalRows > 0) {
            value = this.tblConstRepo.getDateTime("date_value", 1);
        }

        return value;
    }

    /**
     * Get Const repo data table
     *
     * @return
     */
    public Table getConstRepoTable() {
        return this.tblConstRepo;
    }

    /**
     * Get string values in USER_const_repository table as List
     * 
     * @return
     * @throws OException
     */
    public List<String> getStringValuesAsList() throws OException {
        List<String> valueList = new ArrayList<>();

        if (this.totalRows > 0) {
            int colNumStringVal = this.tblConstRepo.getColNum("string_value");

            for (int rowNum = 1; rowNum <= this.totalRows; rowNum++) {
                String value = this.tblConstRepo.getString(colNumStringVal, 1);
                valueList.add(value);
            }

        }

        return valueList;
    }

    /**
     * Get integer values in USER_const_repository table as List
     * 
     * @return
     * @throws OException
     */
    public List<Integer> getIntValuesAsList() throws OException {
        List<Integer> valueList = new ArrayList<>();

        if (this.totalRows > 0) {
            int colNumIntVal = this.tblConstRepo.getColNum("int_value");

            for (int rowNum = 1; rowNum <= this.totalRows; rowNum++) {
                int value = this.tblConstRepo.getInt(colNumIntVal, 1);
                valueList.add(value);
            }

        }

        return valueList;
    }

    /**
     * Get double values in USER_const_repository table as List
     * 
     * @return
     * @throws OException
     */
    public List<Double> getDoubleValuesAsList() throws OException {
        List<Double> valueList = new ArrayList<>();

        if (this.totalRows > 0) {
            int colNumDoubleVal = this.tblConstRepo.getColNum("double_value");

            for (int rowNum = 1; rowNum <= this.totalRows; rowNum++) {
                double value = this.tblConstRepo.getDouble(colNumDoubleVal, 1);
                valueList.add(value);
            }

        }

        return valueList;
    }

    /**
     * Get ODateTime values in USER_const_repository table as List
     * 
     * @return
     * @throws OException
     */
    public List<ODateTime> getDateTimeValuesAsList() throws OException {
        List<ODateTime> valueList = new ArrayList<>();

        if (this.totalRows > 0) {
            int colNumStringVal = this.tblConstRepo.getColNum("date_value");

            for (int rowNum = 1; rowNum <= this.totalRows; rowNum++) {
                ODateTime value = this.tblConstRepo.getDateTime(colNumStringVal, 1);
                valueList.add(value);
            }

        }

        return valueList;
    }

    /**
     * Get string values in USER_const_repository with delimiter and enclosed within specified text
     * 
     * @param delimiter      Delimiter
     * @param enclosedWithin Enclose within
     * @return String values in USER_const_repository with delimiter and enclosed within specified text
     * @throws OException {@link OException}
     */
    public String getStringValues(String delimiter, String enclosedWithin) throws OException {
        return this.getStringValuesAsList().stream()
            .collect(Collectors.joining(enclosedWithin + delimiter + enclosedWithin, enclosedWithin, enclosedWithin));
    }

    /**
     * Get integer values in USER_const_repository with delimiter and enclosed within specified text
     * 
     * @param delimiter      Delimiter
     * @param enclosedWithin Enclose within
     * @return Integer values in USER_const_repository with delimiter and enclosed within specified text
     * @throws OException {@link OException}
     */
    public String getIntValues(String delimiter, String enclosedWithin) throws OException {
        return this.getIntValuesAsList().stream().map(String::valueOf)
            .collect(Collectors.joining(enclosedWithin + delimiter + enclosedWithin, enclosedWithin, enclosedWithin));
    }

    /**
     * Get double values in USER_const_repository with delimiter and enclosed within specified text
     * 
     * @param delimiter      Delimiter
     * @param enclosedWithin Enclose within
     * @return Double values as comma separated string
     * @throws OException {@link OException}
     */
    public String getDoubleValues(String delimiter, String enclosedWithin) throws OException {
        return this.getDoubleValuesAsList().stream().map(String::valueOf)
            .collect(Collectors.joining(enclosedWithin + delimiter + enclosedWithin, enclosedWithin, enclosedWithin));
    }

    /**
     * Get data from user table
     * 
     * @param context    Context
     * @param subContext Sub context
     * @param logger     GarLogger instance
     * @return Get const repository data
     * @throws OException {@link OException}
     */
    private Table getConstRepoData(String context, String subContext, GarLogger logger) throws OException {
        StringBuilder sql = new StringBuilder().append("\n SELECT *") //
            .append("\n FROM   " + GAR_USER_TABLE_ENUM.USER_CONST_REPOSITORY.toString()) //
            .append("\n WHERE  context = '" + context + "'") //
            .append("\n   AND  sub_context = '" + subContext + "'"); //

        return GarStrict.query(sql.toString(), logger);
    }

    /**
     * Get data from user table
     * 
     * @param context    Context
     * @param subContext Sub context
     * @param name       Name
     * @param logger     GarLogger instance
     * @return
     * @throws OException {@link OException}
     */
    private Table getConstRepoData(String context, String subContext, String name, GarLogger logger) throws OException {
        StringBuilder sql = new StringBuilder().append("\n SELECT *") //
            .append("\n FROM   " + GAR_USER_TABLE_ENUM.USER_CONST_REPOSITORY.toString()) //
            .append("\n WHERE  context = '" + context + "'") //
            .append("\n   AND  sub_context = '" + subContext + "'") //
            .append("\n   AND  name = '" + name + "'"); //

        return GarStrict.query(sql.toString(), logger);
    }

    /**
     * Dispose the object
     */
    public void dispose() {
        GarSafe.destroy(this.tblConstRepo);
    }

    @Override
    protected void finalize() {// NOSONAR: We need this method in case someone forgets to dispose the object after usage
        this.dispose();
    }
}
