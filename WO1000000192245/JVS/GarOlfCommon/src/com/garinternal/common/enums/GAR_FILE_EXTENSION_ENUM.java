package com.garinternal.common.enums;

/*
File Name:                      GAR_FILE_EXTENSION_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for File Name extensions

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

public enum GAR_FILE_EXTENSION_ENUM {
    //@formatter:off
    XLSX   ("xlsx"),
    XLS    ("xls"),
    CSV    ("csv"),
    TXT    ("txt"),
    LOG    ("log"),
    ;
    //@formatter:on

    private final String name;

    GAR_FILE_EXTENSION_ENUM(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
