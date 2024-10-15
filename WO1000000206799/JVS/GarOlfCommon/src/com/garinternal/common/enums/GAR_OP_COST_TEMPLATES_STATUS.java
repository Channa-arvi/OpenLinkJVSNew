package com.garinternal.common.enums;

/*
File Name:                      GAR_OP_COST_TEMPLATES_STATUS.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for status in User table USER_operationcost_templates

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000202785 |             | Channa Arvi    | Initial Version
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "USER_operationcost_templates", stringColumn = "status")

public enum GAR_OP_COST_TEMPLATES_STATUS {
    //@formatter:off
    //          Name
    DELETED    ("Deleted"),
    UPDATED    ("Updated"),
    NEW        ("New"),
    ;
    //@formatter:on

    private String name;

    GAR_OP_COST_TEMPLATES_STATUS(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
