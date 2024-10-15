package com.garinternal.common.enums;

/*
File Name:                      GAR_EMAIL_SUB_CONTEXT_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for email sub context configured in user table USER_email_address

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

public enum GAR_EMAIL_SUB_CONTEXT_ENUM {
    //@formatter:off
	SUCCESS  ("Success"),
	FAILURE  ("Failure"),
	;
    //@formatter:on

    private final String name;

    GAR_EMAIL_SUB_CONTEXT_ENUM(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
