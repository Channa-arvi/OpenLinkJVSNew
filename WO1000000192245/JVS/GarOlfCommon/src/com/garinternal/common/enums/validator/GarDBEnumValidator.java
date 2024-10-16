package com.garinternal.common.enums.validator;

/*
 * File Name: GarDBEnumValidator.java
 * Script Type: INCLUDE
 * Parameter Script: None
 * Display Script: None
 * Description:
 * Script to validate DB enums
 * ---------------------------------------------------------------------------
 * REQ No | Release Date| Author | Changes
 * ---------------------------------------------------------------------------
 * WO1000000135255 | | Khalique | Initial Version
 * | | |
 * ---------------------------------------------------------------------------
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.garinternal.common.util.GarAnnotationScannerUtil;
import com.garinternal.common.util.GarConstRepoUtil;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarLogger;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.garinternal.common.util.GarTableUtil;
import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class GarDBEnumValidator implements GarEnumValidator {
    private static final String CONTEXT = "GarDBEnumValidator";
    private Table               tblResults;
    private GarLogger           logger;

    /**
     * Constructor
     * 
     * @throws OException {@link OException}
     */
    public GarDBEnumValidator() throws OException {
        super();
        this.tblResults = this.getResultsTableStructure();
        this.logger     = new GarLogger(this.getClass().getSimpleName());
    }

    @Override
    public Table getResults() throws OException {
        return this.tblResults;
    }

    @Override
    public void validate() throws OException {
        GarConstRepoUtil constRepo = null;

        try {
            constRepo = new GarConstRepoUtil(GarDBEnumValidator.CONTEXT, "packages", "path", this.logger);
            List<String> packages = constRepo.getStringValuesAsList();

            for (String packageName : packages) {
                Map<Class<?>, Annotation> classAnnotationMap =
                    GarAnnotationScannerUtil.getClassListHavingAnnotation(packageName, GarEnumInfo.class, this.logger);

                for (Map.Entry<Class<?>, Annotation> entry : classAnnotationMap.entrySet()) {
                    Class<?> className = entry.getKey();

                    // we only need to examine enums
                    if (!className.isEnum()) {
                        continue;
                    }

                    Annotation annot   = entry.getValue();
                    Method[]   methods = annot.annotationType().getDeclaredMethods();

                    Map<String, Object> arguments = new HashMap<>();

                    // get annotation parameters
                    for (Method method : methods) {
                        arguments.put(method.getName(), method.invoke(annot, (Object[]) null));
                    }

                    // check the values of the enum against database
                    this.checkValues(className, arguments);
                }

            }

        } catch (Exception e) {
            throw new OException(e);
        } finally {
            GarSafe.dispose(constRepo);
        }

    }

    /**
     * Check the values of enum against DB values
     * 
     * @param enumClassName  Enum Class
     * @param annotArguments Annotation arguments
     * @throws OException
     */
    private void checkValues(Class<?> enumClassName, Map<String, Object> annotArguments) throws OException {
        Table tblIds = Table.tableNew();

        try {
            String[] extraDBColNames  = (String[]) annotArguments.get(GarEnumInfo.GAR_ENUM_INFO_METHODS.DB_COLUMNS.toString());
            String[] extraMethodNames = (String[]) annotArguments.get(GarEnumInfo.GAR_ENUM_INFO_METHODS.METHODS.toString());

            // number of extra DB columns should match with the number of extra methods to match with
            if (extraDBColNames.length != extraMethodNames.length) {
                throw new OException("Number of extraDBColumns specified does not match with number of extraMethods for the enum "
                    + enumClassName.getName());
            }

            String stringMethodName = (String) annotArguments.get(GarEnumInfo.GAR_ENUM_INFO_METHODS.STRING_METHOD.toString());

            Method   methodToString = this.getMethod(enumClassName, stringMethodName);
            Method   methodName     = this.getMethod(enumClassName, "name");
            Method[] extraMethods   = this.getMethods(enumClassName, extraMethodNames);

            // get DB columns to search
            String dbColumns = this.getDBColumnsForQuery(extraDBColNames);

            String dbTableName    = (String) annotArguments.get(GarEnumInfo.GAR_ENUM_INFO_METHODS.TABLE_NAME.toString());
            String dbNameColName  = (String) annotArguments.get(GarEnumInfo.GAR_ENUM_INFO_METHODS.STRING_COLUMN.toString());
            String extraSqlFilter = (String) annotArguments.get(GarEnumInfo.GAR_ENUM_INFO_METHODS.EXTRA_SQL_FILTER_CONDITION.toString());

            for (Object obj : enumClassName.getEnumConstants()) {
                Object stringObj = this.invokeMethod(enumClassName, methodToString, obj);
                Object nameObj   = this.invokeMethod(enumClassName, methodName, obj);

                String enumValue     = (String) stringObj;
                String enumFieldName = (String) nameObj;

                StringBuilder sql = new StringBuilder() //
                    .append("\n SELECT " + dbNameColName + (GarHasAValue.hasAValue(dbColumns, true) ? ", " : "") + dbColumns) //
                    .append("\n FROM   " + dbTableName) //
                    .append("\n WHERE  " + dbNameColName + " = '" + enumValue + "'"); //

                if (GarHasAValue.hasAValue(extraSqlFilter, true)) {
                    sql.append("\n   AND  " + extraSqlFilter);
                }

                String errorMsg = "";

                try {
                    GarTableUtil.clearTable(tblIds);
                    GarStrict.query(tblIds, sql.toString(), this.logger);
                } catch (OException e) {
                    errorMsg = "Error while running SQL:\n " + e.getMessage();
                    this.setResults(enumClassName, dbTableName, dbNameColName, enumValue, extraSqlFilter, enumValue, enumFieldName, false,
                        errorMsg);
                    throw e;
                }

                if (GarHasAValue.isTableEmpty(tblIds)) {
                    errorMsg = "No rows match with column \"" + dbNameColName + "\" = \"" + enumValue + "\"";
                    this.setResults(enumClassName, dbTableName, dbNameColName, "", extraSqlFilter, enumValue, enumFieldName, false,
                        errorMsg);
                } else if (!GarHasAValue.isEmpty(extraMethodNames)) {
                    Object[] extraMethodObjs = this.invokeMethods(enumClassName, extraMethods, obj);

                    errorMsg = this.checkValueOfExtraMethodsMatchWithDb(enumClassName, tblIds, dbTableName, dbNameColName, extraSqlFilter,
                        extraDBColNames, extraMethodNames, extraMethods, extraMethodObjs, enumValue, enumFieldName, errorMsg);
                }

                if (!GarHasAValue.hasAValue(errorMsg, true)) {
                    errorMsg = "Match";
                    this.setResults(enumClassName, dbTableName, dbNameColName, enumValue, extraSqlFilter, enumValue, enumFieldName, true,
                        errorMsg);
                }

            }

        } finally {
            GarSafe.destroy(tblIds);
        }

    }

    /**
     * Method to check if the value of extra methods match with DB
     * 
     * @param enumClassName    Enum Class
     * @param tblIds           Table with DB values
     * @param dbTableName      DB Table Name
     * @param dbIdColName      DB Id column name
     * @param dbNameColName    DB String column name
     * @param extraDBColNames  Extra DB columns to match
     * @param extraMethodNames Extra method names to match
     * @param extraMethods     Extra methods
     * @param extraMethodObjs  Extra Method objects
     * @param enumValue        Enum String
     * @param enumFieldName    Enum Field Name
     * @param errorMsg         Error Message
     * @return Error Message
     * @throws OException {@link OException}
     */
    private String checkValueOfExtraMethodsMatchWithDb(Class<?> enumClassName, Table tblIds, String dbTableName, String dbNameColName,
        String extraSqlFilter, String[] extraDBColNames, String[] extraMethodNames, Method[] extraMethods, Object[] extraMethodObjs,
        String enumValue, String enumFieldName, String errorMsg) throws OException {
        int numMethods = extraMethods.length;

        for (int index = 0; index < numMethods; index++) {
            String   extraDbColumnName     = extraDBColNames[index];
            String   mthdName              = extraMethodNames[index];
            Class<?> methodReturnTypeclass = extraMethods[index].getReturnType();
            int      colType               = tblIds.getColType(extraDbColumnName);

            Object extraMethodObj = extraMethodObjs[index];

            errorMsg = this.matchValuesAndSetResults(enumClassName, tblIds, dbTableName, dbNameColName, extraSqlFilter, extraMethodObj,
                enumValue, enumFieldName, extraDbColumnName, mthdName, methodReturnTypeclass, colType);
        }

        return errorMsg;
    }

    /**
     * Match values from Enum and DB and set the results
     * 
     * @param enumClassName         Enum Class Name
     * @param tblIds                Table containing DB Data
     * @param dbTableName           DB Table Name
     * @param dbIdColName           DB ID column name
     * @param dbNameColName         DB String column name
     * @param extraMethodObj        Extra Method object to verify
     * @param enumValue             Enum String value
     * @param enumFieldName         Enum Field name
     * @param extraDbColumnName     DB Column name
     * @param mthdName              Method Name
     * @param methodReturnTypeclass Class indicating method return type
     * @param colType               Column Type
     * @return Error Message
     * @throws OException {@link OException}
     */
    private String matchValuesAndSetResults(Class<?> enumClassName, Table tblIds, String dbTableName, String dbNameColName,
        String extraSqlFilter, Object extraMethodObj, String enumValue, String enumFieldName, String extraDbColumnName, String mthdName,
        Class<?> methodReturnTypeclass, int colType) throws OException {

        String errorMsg = "";

        try {
            boolean noMatch         = false;
            String  dbStringValue   = "";
            String  enumStringValue = "";

            if (methodReturnTypeclass == String.class && colType == COL_TYPE_ENUM.COL_STRING.toInt()) {
                String dbValue       = tblIds.getString(extraDbColumnName, 1);
                String enumMethodVal = (String) extraMethodObj;

                dbStringValue   = dbValue;
                enumStringValue = enumMethodVal;

                noMatch = (!GarHasAValue.hasAValue(dbValue, false) || !dbValue.equals(enumMethodVal));
            } else if (methodReturnTypeclass == int.class
                && (colType == COL_TYPE_ENUM.COL_INT.toInt() || colType == COL_TYPE_ENUM.COL_DATE.toInt())) {
                int dbValue       = tblIds.getInt(extraDbColumnName, 1);
                int enumMethodVal = (Integer) extraMethodObj;

                dbStringValue   = String.valueOf(dbValue);
                enumStringValue = String.valueOf(enumMethodVal);

                noMatch = (dbValue != enumMethodVal);
            } else if (methodReturnTypeclass == long.class && colType == COL_TYPE_ENUM.COL_INT64.toInt()) {
                long dbValue       = tblIds.getInt64(extraDbColumnName, 1);
                long enumMethodVal = (Long) extraMethodObj;

                dbStringValue   = String.valueOf(dbValue);
                enumStringValue = String.valueOf(enumMethodVal);

                noMatch = (dbValue != enumMethodVal);
            } else if (methodReturnTypeclass == double.class && colType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
                double       dbValue                    = tblIds.getDouble(extraDbColumnName, 1);
                double       enumMethodVal              = (Double) extraMethodObj;
                final double precisionForMatchingDouble = 0.00000001;

                dbStringValue   = String.valueOf(dbValue);
                enumStringValue = String.valueOf(enumMethodVal);

                noMatch = (Math.abs(dbValue - enumMethodVal) > precisionForMatchingDouble);
            } else if (methodReturnTypeclass == ODateTime.class && colType == COL_TYPE_ENUM.COL_DATE_TIME.toInt()) {
                ODateTime dbValue       = tblIds.getDateTime(extraDbColumnName, 1);
                ODateTime enumMethodVal = (ODateTime) extraMethodObj;

                dbStringValue   = String.valueOf(dbValue);
                enumStringValue = String.valueOf(enumMethodVal);

                noMatch = (dbValue != enumMethodVal);
            } else {
                throw new OException("Incompatible types for DB column: " + extraDbColumnName + " and method: " + mthdName + " in the enum "
                    + enumClassName.toString());
            }

            if (noMatch) {
                errorMsg = "Mismatch. Value from enum method \"" + mthdName + "\" = \"" + enumStringValue + "\". Value from DB column\""
                    + extraDbColumnName + "\" = \"" + dbStringValue + "\"";
                this.setResults(enumClassName, dbTableName, dbNameColName, enumValue, extraSqlFilter, enumValue, enumFieldName, false,
                    errorMsg);
            }

        } catch (OException e) {
            errorMsg = e.getMessage();
            this.setResults(enumClassName, dbTableName, dbNameColName, enumValue, extraSqlFilter, enumValue, enumFieldName, false,
                errorMsg);
        }

        return errorMsg;
    }

    /**
     * Invoke methods and return result as object array
     * 
     * @param enumClassName Enum Class Name
     * @param extraMethods  Extra Methods array
     * @param obj           Object
     * @return
     * @throws OException
     */
    private Object[] invokeMethods(Class<?> enumClassName, Method[] extraMethods, Object obj) throws OException {
        int numMethods = extraMethods.length;

        Object[] methodResults = new Object[numMethods];

        for (int index = 0; index < numMethods; index++) {
            Method methodName = extraMethods[index];

            methodResults[index] = this.invokeMethod(enumClassName, methodName, obj);
        }

        return methodResults;
    }

    /**
     * Get DB columns to be selected in SQL
     * 
     * @param extraDBColNames Extra DB columns
     * @return
     */
    private String getDBColumnsForQuery(String[] extraDBColNames) {
        return Arrays.asList(extraDBColNames).stream().collect(Collectors.joining(","));
    }

    /**
     * Get methods for given method names
     * 
     * @param enumClassName Enum class name to find methods in
     * @param methodNames   Method names array
     * @return
     * @throws OException
     */
    private Method[] getMethods(Class<?> enumClassName, String[] methodNames) throws OException {
        int      numMethods = methodNames.length;
        Method[] methods    = new Method[numMethods];

        for (int index = 0; index < numMethods; index++) {
            methods[index] = this.getMethod(enumClassName, methodNames[index]);
        }

        return methods;
    }

    /**
     * Set results to table
     * 
     * @param enumClassName        Enum Class
     * @param dbTableName          DB Table Name
     * @param dbNameColName        DB Col Name for name
     * @param dbStringValue        DB String value
     * @param dbExtraQueryCriteria Extra SQL Criteria
     * @param enumString           Enum string
     * @param enumFieldName        Enum Field Name
     * @param matches              Flag to indicate if the data matches
     * @param error                Error Message
     * @throws OException
     */
    private void setResults(Class<?> enumClassName, String dbTableName, String dbNameColName, String dbStringValue,
        String dbExtraQueryCriteria, String enumString, String enumFieldName, boolean matches, String error) throws OException {
        int rowNum = this.tblResults.addRow();

        this.tblResults.setString("enum_name", rowNum, enumClassName.getName());
        this.tblResults.setString("enum_field", rowNum, enumFieldName);
        this.tblResults.setString("enum_string_value", rowNum, enumString);
        this.tblResults.setString("db_string_value", rowNum, dbStringValue);
        this.tblResults.setString("db_table_name", rowNum, dbTableName);
        this.tblResults.setString("db_val_col_name", rowNum, dbNameColName);
        this.tblResults.setString("db_extra_query_criteria", rowNum, dbExtraQueryCriteria);
        this.tblResults.setInt("match", rowNum, matches ? 1 : 0);
        this.tblResults.setString("error_reason", rowNum, error != null ? error : "");
    }

    /**
     * Invoke method
     * 
     * @param enumClassName Enum Class
     * @param method        Metho
     * @param obj           Invoke on Object
     * @return
     * @throws OException
     */
    private Object invokeMethod(Class<?> enumClassName, Method method, Object obj) throws OException {
        Object  objRet     = null;
        boolean isError    = false;
        String  errorMsg   = "";
        String  enumName   = enumClassName.getName();
        String  methodName = method.getName();

        try {
            objRet = method.invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            isError  = true;
            errorMsg = "Method: " + methodName + " Enum: " + enumName + "\n\n" + e.getMessage();
        } finally {

            if (isError) {
                int rowNum = this.tblResults.addRow();
                this.tblResults.setString("enum_name", rowNum, enumName);
                this.tblResults.setString("error_reason", rowNum, errorMsg);
            }

        }

        if (objRet == null) {
            throw new OException("null value returned for Method: " + method.getName() + " in the Enum: " + enumClassName.getName());
        }

        return objRet;
    }

    /**
     * Get Method
     * 
     * @param enumClassName Enum Class
     * @param methodName    Method name
     * @return
     * @throws OException
     */
    private Method getMethod(Class<?> enumClassName, String methodName) throws OException {
        Method  method   = null;
        boolean isError  = false;
        String  errorMsg = "";
        String  enumName = enumClassName.getName();

        try {
            method = enumClassName.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            isError  = true;
            errorMsg = "Method: " + methodName + " not found in Enum: " + enumName + "\n\n" + e.getMessage();
        } catch (SecurityException e) {
            isError  = true;
            errorMsg = "Cannot access Method: " + methodName + " in Enum: " + enumName + "\n\n" + e.getMessage();
        } finally {

            if (isError) {
                int rowNum = this.tblResults.addRow();
                this.tblResults.setString("enum_name", rowNum, enumName);
                this.tblResults.setString("error_reason", rowNum, errorMsg);
            }

        }

        if (method == null) {
            throw new OException("Method: " + methodName + " not found in the Enum: " + enumClassName.getName());
        }

        return method;
    }

    /**
     * Get results table structure
     * 
     * @return
     * @throws OException
     */
    private final Table getResultsTableStructure() throws OException {
        Table results = Table.tableNew();

        results.addCol("enum_name", COL_TYPE_ENUM.COL_STRING);
        results.addCol("enum_field", COL_TYPE_ENUM.COL_STRING);
        results.addCol("enum_string_value", COL_TYPE_ENUM.COL_STRING);
        results.addCol("db_string_value", COL_TYPE_ENUM.COL_STRING);
        results.addCol("db_table_name", COL_TYPE_ENUM.COL_STRING);
        results.addCol("db_val_col_name", COL_TYPE_ENUM.COL_STRING);
        results.addCol("db_extra_query_criteria", COL_TYPE_ENUM.COL_STRING);
        results.addCol("match", COL_TYPE_ENUM.COL_INT);
        results.addCol("error_reason", COL_TYPE_ENUM.COL_STRING);

        return results;
    }

    /**
     * Dispose this object
     */
    @Override
    public void dispose() {
        GarSafe.destroy(this.tblResults);
    }

    @Override
    protected void finalize() {// NOSONAR: This is required just in case the implementer forgets to dispose the object properly
        this.dispose();
    }
}
