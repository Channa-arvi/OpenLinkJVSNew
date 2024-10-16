package com.garinternal.common.enums;

/*
File Name:                      GAR_ENV_VARIABLE_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Environment variables

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

public enum GAR_ENV_VARIABLE_ENUM {
    //@formatter:off
	AB_OUTDIR    ("AB_OUTDIR"),
	;
    //@formatter:on

    private final String name;

    GAR_ENV_VARIABLE_ENUM(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
