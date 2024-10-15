package com.garinternal.common.enums;

/*
File Name:                      GAR_SERVICE_NAME_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Services defined in Services Manager

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "job_cfg", stringColumn = "name",
    extraSQLFilterCondition = "type = 0 AND service_type = 2 AND service_group_type <> 16")

public enum GAR_SERVICE_NAME_ENUM {
    //@formatter:off
	MAIL        ("Mail"),
	;
    //@formatter:on

    private final String name;

    GAR_SERVICE_NAME_ENUM(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
