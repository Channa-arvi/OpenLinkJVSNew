package com.garinternal.common.enums;

/*
File Name:                      GAR_SAP_SYSTEM_TYPE.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Sap System types

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
@GarEnumInfo(tableName = "USER_sap_md_material_mapping", stringColumn = "type")

public enum GAR_SAP_SYSTEM_TYPE {

    //@formatter:off
	//                      Name
    MD    ("MD"),
    FI    ("FI"),
	;
    //@formatter:on

    private String name;

    GAR_SAP_SYSTEM_TYPE(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
