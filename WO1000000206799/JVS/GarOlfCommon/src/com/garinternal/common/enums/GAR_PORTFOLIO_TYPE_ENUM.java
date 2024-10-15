package com.garinternal.common.enums;

/*
File Name:                      GAR_PORTFOLIO_TYPE.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Portfolio type

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.fnd.RefBase;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "portfolio_type", stringColumn = "name", dBColumns = { "id_number" }, methods = { "toInt" })

public enum GAR_PORTFOLIO_TYPE_ENUM {
    //@formatter:off
	//             name                                ID
	TRADING       ("Trading",                          -1),
	INVESTMENT    ("Investment",                       -1),
	;
    //@formatter:on

    private final String name;
    private int          id;

    GAR_PORTFOLIO_TYPE_ENUM(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns the ID of the enum
     *
     * @return Return Id of the party
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.PORTFOLIO_TYPE_TABLE, this.name);
        }

        return idLocal;
    }
}
