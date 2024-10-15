package com.garinternal.mo.bbg.priceimport;

/*
File Name:                      GAR_MO_BBG_USER_SELECTION.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum to store user selection values for Bloomberg price import task

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

public enum GAR_MO_BBG_USER_SELECTION {
    //@formatter:off
    IMPORT_PRICES (  "Import Prices",   1),
    GENERATE_REPORT ("Generate Report", 2),
    ;
    //@formatter:on

    private final String name;
    private final int    id;

    GAR_MO_BBG_USER_SELECTION(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Return Id
     *
     * @return Return Id
     */
    public int toInt() {
        return this.id;
    }
}
