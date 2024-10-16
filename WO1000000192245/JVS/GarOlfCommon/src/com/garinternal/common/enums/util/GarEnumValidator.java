package com.garinternal.common.enums.util;

/*
File Name:                      GarEnumValidator.java

Script Type:                    MAIN 
Parameter Script:               None                  
Display Script:                 None

Description:
Script to validate enums

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.garinternal.basic.GarBasicScript;
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
@PluginType(SCRIPT_TYPE_ENUM.MAIN_SCRIPT)

public class GarEnumValidator extends GarBasicScript {
	private static final String CONTEXT = "GarEnumValidator";
	private Table tblResults;
	private GarLogger logger;

	/**
	 * Constructor
	 * 
	 * @throws OException
	 */
	public GarEnumValidator() throws OException {
		super();
		this.tblResults = this.getResultsTableStructure();
		this.logger = getLoggerInstance();
	}

	/**
	 * Enum to specify methods in the annotation GarEnumInfo
	 *
	 */
	private enum GAR_ENUM_INFO_METHODS {
		TABLE_NAME("tableName"),
		ID_COLUMN("idColumn"),
		STRING_COLUMN("stringColumn"),
		ID_METHOD("idMethod"),
		STRING_METHOD("stringMethod"),
		EXTRA_DB_COLUMNS("extraDBColumns"),
		EXTRA_METHODS("extraMethods"),
		;

		private String methodName; 

		private GAR_ENUM_INFO_METHODS(String methodName) {
			this.methodName = methodName;
		}

		@Override
		public String toString() {
			return this.methodName;
		}
	}

	@Override
	public void execute(Table argt, Table returnt) throws Exception {
		boolean isError = false;
		GarConstRepoUtil constRepo = null;

		try {
			constRepo = new GarConstRepoUtil(GarEnumValidator.CONTEXT, "packages", "path", this.logger);
			List<String> packages = constRepo.getStringValuesAsList();

			for(String packageName : packages) {
				Map<Class<?>, Annotation> classAnnotationMap = GarAnnotationScanner.getClassListHavingAnnotation(packageName
						, GarEnumInfo.class, logger);

				for(Map.Entry<Class<?>, Annotation> entry : classAnnotationMap.entrySet()) {
					Class<?> className = entry.getKey();

					//we only need to examine enums
					if(!className.isEnum()) {
						continue;
					}

					Annotation annot = entry.getValue();
					Method[] methods = annot.annotationType().getDeclaredMethods();

					Map<String, Object> arguments = new HashMap<>();

					//get annotation parameters
					for(Method method : methods) {
						arguments.put(method.getName(), method.invoke(annot, (Object[]) null));
					}

					//check the values of the enum against database
					this.checkValues(className, arguments);
				}
			}
		} catch(Exception e) {
			isError = true;
			throw e;
		} finally {
			GarSafe.dispose(constRepo);
			if(!isError) {
				this.tblResults.viewTable();
			}
		}
	}

	/**
	 * Check the values of enum against DB values
	 * 
	 * @param enumClassName Enum Class
	 * @param annotArguments Annotation arguments
	 * @throws OException
	 */
	private void checkValues(Class<?> enumClassName, Map<String, Object> annotArguments) throws OException {
		Table tblIds = Table.tableNew();

		try {
			String[] extraDBColNames = (String[]) annotArguments.get(GAR_ENUM_INFO_METHODS.EXTRA_DB_COLUMNS.toString());
			String[] extraMethodNames = (String[]) annotArguments.get(GAR_ENUM_INFO_METHODS.EXTRA_METHODS.toString());

			//number of extra DB columns should match with the number of extra methods to match with
			if(extraDBColNames.length != extraMethodNames.length) {
				throw new OException("Number of extraDBColumns specified does not match with number of extraMethods for the enum "
						+ enumClassName.getName());
			}

			String idMethodName = (String) annotArguments.get(GAR_ENUM_INFO_METHODS.ID_METHOD.toString());
			String stringMethodName = (String) annotArguments.get(GAR_ENUM_INFO_METHODS.STRING_METHOD.toString());

			Method methodToInt = this.getMethod(enumClassName, idMethodName);
			Method methodToString = this.getMethod(enumClassName, stringMethodName);
			Method methodName = this.getMethod(enumClassName, "name");
			Method[] extraMethods = this.getMethods(enumClassName, extraMethodNames);

			//get DB columns to search
			String dbIdColName = (String) annotArguments.get(GAR_ENUM_INFO_METHODS.ID_COLUMN.toString());
			String dbColumns = this.getDBColumnsForQuery(dbIdColName, extraDBColNames);

			String dbTableName = (String) annotArguments.get(GAR_ENUM_INFO_METHODS.TABLE_NAME.toString());
			String dbNameColName = (String) annotArguments.get(GAR_ENUM_INFO_METHODS.STRING_COLUMN.toString());

			for(Object obj : enumClassName.getEnumConstants()) {

				Object idObj = this.invokeMethod(enumClassName, methodToInt, obj);
				Object stringObj = this.invokeMethod(enumClassName, methodToString, obj);
				Object nameObj = this.invokeMethod(enumClassName, methodName, obj);

				int enumId = (Integer) idObj;
				String enumValue = (String) stringObj;
				String enumFieldName = (String) nameObj;

				StringBuilder sql = new StringBuilder() //
						.append("\n SELECT " + dbColumns) //
						.append("\n FROM   " + dbTableName) //
						.append("\n WHERE  " + dbNameColName + " = '" + enumValue + "'"); //

				try {
					GarTableUtil.clearTable(tblIds);
					GarStrict.query(tblIds, sql.toString(), logger);
				} catch(OException e) {
					String errorMsg = "Error while running SQL:\n " + e.getMessage();
					this.setResults(enumClassName, dbTableName, dbIdColName, dbNameColName, enumValue, 0, enumId, enumValue
							, enumFieldName, false, errorMsg);
					throw e;
				}

				if(GarHasAValue.isTableEmpty(tblIds)) {
					String errorMsg = "No rows match with column " + dbNameColName + " = " + enumValue;
					this.setResults(enumClassName, dbTableName, dbIdColName, dbNameColName, "", 0, enumId, enumValue, enumFieldName, false, errorMsg);
				} else if(tblIds.getColType(dbIdColName) != COL_TYPE_ENUM.COL_INT.toInt()) {
					String errorMsg = "Column: " + dbIdColName + " in the table " + dbTableName + " is not of type int";
					this.setResults(enumClassName, dbTableName, dbIdColName, dbNameColName, enumValue, 0, enumId, enumValue
							, enumFieldName, false, errorMsg);
				} else {
					int dbIdValue = tblIds.getInt(dbIdColName, 1);
					String errorMsg = "";

					if(dbIdValue != enumId) {
						errorMsg = "ID in the enum does not match with DB ID value";
						this.setResults(enumClassName, dbTableName, dbIdColName, dbNameColName, enumValue, dbIdValue, enumId, enumValue
								, enumFieldName, false, errorMsg);
					} 

					Object[] extraMethodObjs = this.invokeMethods(enumClassName, extraMethods, obj);

					errorMsg = this.checkValueOfExtraMethodsMatchWithDb(enumClassName, tblIds, dbTableName, dbIdColName,
							dbNameColName, extraDBColNames, extraMethodNames, extraMethods, extraMethodObjs, enumId,
							enumValue, enumFieldName, dbIdValue, errorMsg);

					if(!GarHasAValue.hasAValue(errorMsg, true)) {
						errorMsg = "Match";
						this.setResults(enumClassName, dbTableName, dbIdColName, dbNameColName, enumValue, dbIdValue, enumId, enumValue
								, enumFieldName, true, errorMsg);
					}
				}
			}
		} finally {
			GarSafe.destroy(tblIds);
		}
	}

	/**
	 * Method to check if the value of extra methods match with DB
	 * 
	 * @param enumClassName Enum Class
	 * @param tblIds Table with DB values
	 * @param dbTableName DB Table Name
	 * @param dbIdColName DB Id column name
	 * @param dbNameColName DB String column name
	 * @param extraDBColNames Extra DB columns to match
	 * @param extraMethodNames Extra method names to match
	 * @param extraMethods Extra methods
	 * @param extraMethodObjs Extra Method objects
	 * @param enumId Enum ID
	 * @param enumValue Enum String
	 * @param enumFieldName Enum Field Name
	 * @param dbIdValue DB ID value
	 * @param errorMsg Error Message
	 * @return
	 * @throws OException
	 */
	private String checkValueOfExtraMethodsMatchWithDb(Class<?> enumClassName, Table tblIds, String dbTableName,
			String dbIdColName, String dbNameColName, String[] extraDBColNames, String[] extraMethodNames,
			Method[] extraMethods, Object[] extraMethodObjs, int enumId, String enumValue, String enumFieldName,
			int dbIdValue, String errorMsg) throws OException {
		int numMethods = extraMethods.length;

		for(int index = 0; index < numMethods; index++) {
			String extraDbColumnName = extraDBColNames[index];
			String mthdName = extraMethodNames[index];
			Class<?> methodReturnTypeclass = extraMethods[index].getReturnType();
			int colType = tblIds.getColType(extraDbColumnName);

			Object extraMethodObj = extraMethodObjs[index];

			errorMsg = matchValuesAndSetResults(enumClassName, tblIds, dbTableName, dbIdColName, dbNameColName, extraMethodObj, enumId,
					enumValue, enumFieldName, dbIdValue, extraDbColumnName, mthdName, methodReturnTypeclass, colType);
		}
		return errorMsg;
	}

	/**
	 * Match values from Enum and DB and set the results
	 * 
	 * @param enumClassName Enum Class Name
	 * @param tblIds Table containing DB Data
	 * @param dbTableName DB Table Name
	 * @param dbIdColName DB ID column name
	 * @param dbNameColName DB String column name
	 * @param extraMethodObj Extra Method object to verify
	 * @param enumId Enum ID value
	 * @param enumValue Enum String value
	 * @param enumFieldName Enum Field name
	 * @param dbIdValue DB ID Value
	 * @param dbColumnName DB Column name
	 * @param mthdName Method Name
	 * @param methodReturnTypeclass Class indicating method return type
	 * @param colType Column Type
	 * @return
	 * @throws OException
	 */
	private String matchValuesAndSetResults(Class<?> enumClassName, Table tblIds, String dbTableName, String dbIdColName,
			String dbNameColName, Object extraMethodObj, int enumId, String enumValue, String enumFieldName,
			int dbIdValue, String extraDbColumnName, String mthdName, Class<?> methodReturnTypeclass, int colType) throws OException {

		boolean noMatch = false;

		if(methodReturnTypeclass == String.class && colType == COL_TYPE_ENUM.COL_STRING.toInt()) {
			String dbValue = tblIds.getString(extraDbColumnName, 1);
			String enumMethodVal = (String)extraMethodObj;

			noMatch = (!GarHasAValue.hasAValue(dbValue, false) || !dbValue.equals(enumMethodVal));
		} else if(methodReturnTypeclass == int.class && (colType == COL_TYPE_ENUM.COL_INT.toInt()
				|| colType == COL_TYPE_ENUM.COL_DATE.toInt())) {
			int dbValue = tblIds.getInt(extraDbColumnName, 1);
			int enumMethodVal = (Integer)extraMethodObj;

			noMatch = (dbValue != enumMethodVal);
		} else if(methodReturnTypeclass == long.class && colType == COL_TYPE_ENUM.COL_INT64.toInt()) {
			long dbValue = tblIds.getInt64(extraDbColumnName, 1);
			long enumMethodVal = (Long)extraMethodObj;

			noMatch = (dbValue != enumMethodVal);
		} else if(methodReturnTypeclass == double.class && colType == COL_TYPE_ENUM.COL_DOUBLE.toInt()) {
			double dbValue = tblIds.getDouble(extraDbColumnName, 1);
			double enumMethodVal = (Double)extraMethodObj;
			final double precisionForMatchingDouble = 0.00000001;

			noMatch = (Math.abs(dbValue - enumMethodVal) > precisionForMatchingDouble);
		} else if(methodReturnTypeclass == ODateTime.class && colType == COL_TYPE_ENUM.COL_DATE_TIME.toInt()) {
			ODateTime dbValue = tblIds.getDateTime(extraDbColumnName, 1);
			ODateTime enumMethodVal = (ODateTime)extraMethodObj;

			noMatch = (dbValue != enumMethodVal);
		} else {
			throw new OException("Incompatible types for DB column: " + extraDbColumnName + " and method: " + mthdName
					+ " in the enum " + enumClassName.toString());
		}

		String errorMsg = "";

		if(noMatch) {
			errorMsg = mthdName + " in the enum does not match with DB " + extraDbColumnName;
			this.setResults(enumClassName, dbTableName, dbIdColName, dbNameColName, enumValue, dbIdValue, enumId, enumValue
					, enumFieldName, false, errorMsg);
		}

		return errorMsg;
	}

	/**
	 * Invoke methods and return result as object array
	 * 
	 * @param enumClassName Enum Class Name
	 * @param extraMethods Extra Methods array
	 * @param obj Object
	 * @return
	 * @throws OException
	 */
	private Object[] invokeMethods(Class<?> enumClassName, Method[] extraMethods, Object obj) throws OException {
		int numMethods = extraMethods.length;

		Object[] methodResults = new Object[numMethods];

		for(int index = 0; index < numMethods; index++) {
			Method methodName = extraMethods[index];

			methodResults[index] = this.invokeMethod(enumClassName, methodName, obj);
		}

		return methodResults;
	}

	/**
	 * Get DB columns to be selected in SQL
	 * 
	 * @param dbIdColName ID column name
	 * @param extraDBColNames Extra DB columns
	 * @return
	 */
	private String getDBColumnsForQuery(String dbIdColName, String[] extraDBColNames) {
		StringBuilder dbColumns = new StringBuilder().append(dbIdColName);

		for(String column : extraDBColNames) {
			if(GarHasAValue.hasAValue(column, true)) {
				dbColumns.append(", ").append(column);
			}
		}

		return dbColumns.toString();
	}

	/**
	 * Get methods for given method names
	 * 
	 * @param enumClassName Enum class name to find methods in
	 * @param methodNames Method names array
	 * @return
	 * @throws OException
	 */
	private Method[] getMethods(Class<?> enumClassName, String[] methodNames) throws OException {
		int numMethods = methodNames.length;
		Method[] methods = new Method[numMethods];

		for(int index = 0; index < numMethods; index++) {
			methods[index] = this.getMethod(enumClassName, methodNames[index]);
		}

		return methods;
	}

	/**
	 * Set results to table
	 * 
	 * @param enumClassName Enum Class
	 * @param dbTableName DB Table Name
	 * @param dbIdColName DB Col Name for ID
	 * @param dbNameColName DB Col Name for name
	 * @param dbStringValue DB String value
	 * @param dbIntValue DB ID value
	 * @param enumId Enum Id
	 * @param enumString Enum string
	 * @param enumFieldName Enum Field Name
	 * @param matches Flag to indicate if the data matches
	 * @param error Error Message
	 * @throws OException
	 */
	private void setResults(Class<?> enumClassName, String dbTableName, String dbIdColName, String dbNameColName, String dbStringValue,
			int dbIntValue, int enumId, String enumString, String enumFieldName, boolean matches, String error) throws OException {
		int rowNum = this.tblResults.addRow();

		this.tblResults.setString("enum_name", rowNum, enumClassName.getName());
		this.tblResults.setString("enum_field", rowNum, enumFieldName);
		this.tblResults.setString("enum_string_value", rowNum, enumString);
		this.tblResults.setInt("enum_int_value", rowNum, enumId);
		this.tblResults.setString("db_string_value", rowNum, dbStringValue);
		this.tblResults.setInt("db_int_value", rowNum, dbIntValue);
		this.tblResults.setString("db_table_name", rowNum, dbTableName);
		this.tblResults.setString("db_id_col_name", rowNum, dbIdColName);
		this.tblResults.setString("db_val_col_name", rowNum, dbNameColName);
		this.tblResults.setInt("match", rowNum, matches ? 1 : 0);
		this.tblResults.setString("error_reason", rowNum, error != null ? error : "");
	}

	/**
	 * Invoke method
	 * 
	 * @param enumClassName Enum Class
	 * @param method Metho
	 * @param obj Invoke on Object
	 * @return
	 * @throws OException
	 */
	private Object invokeMethod(Class<?> enumClassName, Method method, Object obj) throws OException {
		Object objRet = null;
		boolean isError = false;
		String errorMsg = "";
		String enumName = enumClassName.getName();
		String methodName = method.getName();

		try {
			objRet = method.invoke(obj);
		} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			isError = true;
			errorMsg = "Method: " + methodName + " Enum: " + enumName + "\n\n" + e.getMessage();
		} finally {
			if(isError) {
				int rowNum = this.tblResults.addRow();
				this.tblResults.setString("enum_name", rowNum, enumName);
				this.tblResults.setString("error_reason", rowNum, errorMsg);
			}
		}

		if(objRet == null) {
			throw new OException("null value returned for Method: " + method.getName() + " in the Enum: " + enumClassName.getName());
		}

		return objRet;
	}

	/**
	 * Get Method
	 * 
	 * @param enumClassName Enum Class
	 * @param methodName Method name
	 * @return
	 * @throws OException
	 */
	private Method getMethod(Class<?> enumClassName, String methodName) throws OException {
		Method method = null;
		boolean isError = false;
		String errorMsg = "";
		String enumName = enumClassName.getName();

		try {
			method = enumClassName.getMethod(methodName);
		} catch (NoSuchMethodException e) {
			isError = true;
			errorMsg = "Method: " + methodName + " not found in Enum: " + enumName + "\n\n" + e.getMessage();
		} catch (SecurityException e) {
			isError = true;
			errorMsg = "Cannot access Method: " + methodName + " in Enum: " + enumName + "\n\n" + e.getMessage();
		} finally {
			if(isError) {
				int rowNum = this.tblResults.addRow();
				this.tblResults.setString("enum_name", rowNum, enumName);
				this.tblResults.setString("error_reason", rowNum, errorMsg);
			}
		}

		if(method == null) {
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
	private Table getResultsTableStructure() throws OException {
		Table results = Table.tableNew();

		results.addCol("enum_name", COL_TYPE_ENUM.COL_STRING);
		results.addCol("enum_field", COL_TYPE_ENUM.COL_STRING);
		results.addCol("enum_string_value", COL_TYPE_ENUM.COL_STRING);
		results.addCol("enum_int_value", COL_TYPE_ENUM.COL_INT);
		results.addCol("db_string_value", COL_TYPE_ENUM.COL_STRING);
		results.addCol("db_int_value", COL_TYPE_ENUM.COL_INT);
		results.addCol("db_table_name", COL_TYPE_ENUM.COL_STRING);
		results.addCol("db_id_col_name", COL_TYPE_ENUM.COL_STRING);
		results.addCol("db_val_col_name", COL_TYPE_ENUM.COL_STRING);
		results.addCol("match", COL_TYPE_ENUM.COL_INT);
		results.addCol("error_reason", COL_TYPE_ENUM.COL_STRING);

		return results;
	}
}
