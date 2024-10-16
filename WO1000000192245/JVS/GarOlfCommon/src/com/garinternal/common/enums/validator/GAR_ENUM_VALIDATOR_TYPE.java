package com.garinternal.common.enums.validator;

/*
File Name:                      GAR_ENUM_VALIDATOR_TYPE.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum to denote Enum Validator Type

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

public enum GAR_ENUM_VALIDATOR_TYPE {
    //@formatter:off
	DB_ENUM_VALIDATOR       ("db_enum_validator_results"),
	ENV_VARIABLE_VALIDATOR  ("env_var_validator_results"),
	;
    //@formatter:on

    private final String columnName;

    GAR_ENUM_VALIDATOR_TYPE(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return this.columnName;
    }
}
