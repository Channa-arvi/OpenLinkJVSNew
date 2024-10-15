package com.garinternal.common.enums;

/*
File Name:                      GAR_PARTY_ADDRESS_TYPE_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum to represent Party Address Type

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000206799 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;

import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openjvs.fnd.RefBase;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "party_address_type", stringColumn = "address_type_name", dBColumns = { "address_type_id" }, methods = { "toInt" })

public enum GAR_PARTY_ADDRESS_TYPE_ENUM {
  //@formatter:off
    //               Name                          Id
    MAIN             ("Main",                      -1),
    EMAIL            ("Email",                     -1),
    POSTAL           ("Postal",                    -1),

    ;
    //@formatter:on

    private String name;
    private int    id;

    GAR_PARTY_ADDRESS_TYPE_ENUM(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Party Address Type Id
     *
     * @return Party Address Type Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.PARTY_ADDRESS_TYPE_TABLE, this.name);
        }

        return idLocal;
    }
}
