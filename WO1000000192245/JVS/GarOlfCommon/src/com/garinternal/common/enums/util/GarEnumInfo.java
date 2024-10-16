package com.garinternal.common.enums.util;

/*
File Name:                      GarEnumInfo.java

Script Type:                    INCLUDE 
Parameter Script:               None                  
Display Script:                 None

Description:
Annotation to be declared by scripts that need to be validated by EnumValidator

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})

public @interface GarEnumInfo {
	/**
	 * Indicates DB table name
	 * @return
	 */
	String tableName();

	/**
	 * Indicates DB ID column name
	 * @return
	 */
	String idColumn();

	/**
	 * Indicates DB String column name
	 * @return
	 */
	String stringColumn();
	
	/**
	 * Indicates Method name in the enum to get the ID
	 * @return
	 */
	String idMethod() default "toInt";

	/**
	 * Indicates Method name in the enum to get the String value
	 * @return
	 */
	String stringMethod() default "toString";

	/**
	 * Specify extra DB columns to check here.
	 * This works in conjuction with extraMethods. Both these methods should have equal number of arguments in the same order
	 * @return
	 */
	String[] extraDBColumns() default {};

	/**
	 * Specify extra Enum methods to check here.
	 * This works in conjuction with extraDBColumns. Both these methods should have equal number of arguments in the same order
	 * @return
	 */
	String[] extraMethods() default {};
}
