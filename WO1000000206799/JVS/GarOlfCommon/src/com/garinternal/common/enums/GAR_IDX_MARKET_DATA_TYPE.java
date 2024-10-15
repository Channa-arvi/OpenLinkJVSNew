package com.garinternal.common.enums;

/*
File Name:                      GAR_IDX_MARKET_DATA_TYPE.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Index Groups

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
@GarEnumInfo(tableName = "idx_market_data_type", stringColumn = "name", dBColumns = { "id_number" }, methods = { "toInt" })

public enum GAR_IDX_MARKET_DATA_TYPE {

    //@formatter:off
	//                      Name                     Id
	CLOSING                 ("Closing",               1),
	UNIVERSAL               ("Universal",             2),
	PERSONAL                ("Personal",              3),
	ACCOUNTING              ("Accounting",            4),
	SNAPSHOT                ("Snapshot",              5),
	EOD_3PM                 ("3PM EOD",           20001),
	;
	//@formatter:on

    private String name;
    private int    id;

    GAR_IDX_MARKET_DATA_TYPE(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns IDX Group Id
     *
     * @return IDX Group Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.IDX_MARKET_DATA_TYPE_TABLE, this.name);
        }

        return idLocal;
    }
}
