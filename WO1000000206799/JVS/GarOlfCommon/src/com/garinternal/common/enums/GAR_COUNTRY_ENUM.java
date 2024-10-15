package com.garinternal.common.enums;

/*
File Name:                      GAR_COUNTRY_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum to represent country

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
@GarEnumInfo(tableName = "country", stringColumn = "name", dBColumns = { "id_number" }, methods = { "toInt" })

public enum GAR_COUNTRY_ENUM {
  //@formatter:off
    //               Name                            Id
    INDONESIA       ("INDONESIA",                    -1),
    MALAYSIA        ("MALAYSIA",                     -1),
    SINGAPORE       ("SINGAPORE",                    -1),

    ;
    //@formatter:on

    private String name;
    private int    id;

    GAR_COUNTRY_ENUM(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Country Id
     *
     * @return Country Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.COUNTRY_TABLE, this.name);
        }

        return idLocal;
    }
}
