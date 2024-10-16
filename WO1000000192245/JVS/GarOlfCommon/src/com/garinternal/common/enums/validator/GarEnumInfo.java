package com.garinternal.common.enums.validator;

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
@Target({ ElementType.TYPE })

public @interface GarEnumInfo {
    /**
     * Enum to specify methods in the annotation GarEnumInfo. This should always match with the method names in the GarEnumInfo annotation.
     *
     */
    public enum GAR_ENUM_INFO_METHODS {
        //@formatter:off
        TABLE_NAME                    ("tableName"),
        STRING_COLUMN                 ("stringColumn"),
        STRING_METHOD                 ("stringMethod"),
        DB_COLUMNS                    ("dBColumns"),
        METHODS                       ("methods"),
        EXTRA_SQL_FILTER_CONDITION    ("extraSQLFilterCondition"),
        ;
        //@formatter:on

        private String methodName;

        GAR_ENUM_INFO_METHODS(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return this.methodName;
        }
    }

    /**
     * Indicates DB table name
     *
     * @return
     */
    String tableName();

    /**
     * Indicates DB String column name
     *
     * @return
     */
    String stringColumn();

    /**
     * Indicates Method name in the enum to get the String value
     *
     * @return
     */
    String stringMethod() default "toString";

    /**
     * Specify DB columns to check here.
     * This works in conjunction with extraMethods. Both these methods should have equal number of arguments in the same order
     *
     * @return
     */
    String[] dBColumns() default {};

    /**
     * Specify Enum methods to check here.
     * This works in conjunction with extraDBColumns. Both these methods should have equal number of arguments in the same order
     *
     * @return
     */
    String[] methods() default {};

    /**
     * Extra where conditions
     *
     * @return
     */
    String extraSQLFilterCondition() default "";
}
