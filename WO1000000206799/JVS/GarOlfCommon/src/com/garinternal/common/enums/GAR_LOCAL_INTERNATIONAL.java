package com.garinternal.common.enums;

/*
File Name:                      GAR_LOCAL_INTERNATIONAL.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for USER_local_international user table

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
---------------------------------------------------------------------------
 */

import com.garinternal.common.enums.validator.GarEnumInfo;
import com.garinternal.common.util.GarHasAValue;
import com.garinternal.common.util.GarSafe;
import com.garinternal.common.util.GarStrict;
import com.olf.openjvs.OException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.Table;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)
@GarEnumInfo(tableName = "USER_local_international", stringColumn = "description", dBColumns = { "id" }, methods = { "toInt" })

public enum GAR_LOCAL_INTERNATIONAL {

    //@formatter:off
    //                      Name                     Id
    LOCAL                   ("Local",                1),
    INTERNATIONAL           ("International",        2),
    ;
    //@formatter:on

    private String name;
    private int    id;

    GAR_LOCAL_INTERNATIONAL(String name, int id) {
        this.name = name;
        this.id   = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Id
     *
     * @return Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = this.getId(this.name);
        }

        return idLocal;
    }

    /**
     * Get enum from it's string value
     *
     * @param nameLocInt String value of the enum
     * @return Enum
     */
    public static GAR_LOCAL_INTERNATIONAL fromString(String nameLocInt) {
        GAR_LOCAL_INTERNATIONAL garLocInt = null;

        for (GAR_LOCAL_INTERNATIONAL value : GAR_LOCAL_INTERNATIONAL.values()) {

            if (GarHasAValue.hasAValue(nameLocInt, true) && value.toString().equalsIgnoreCase(nameLocInt)) {
                garLocInt = value;
            }

        }

        return garLocInt;
    }

    /**
     * Get enum from it's id value
     *
     * @param idLocInt Integer value of the enum
     * @return Enum
     * @throws OException {@link OException}
     */
    public static GAR_LOCAL_INTERNATIONAL fromInt(int idLocInt) throws OException {
        GAR_LOCAL_INTERNATIONAL garLocInt = null;

        for (GAR_LOCAL_INTERNATIONAL value : GAR_LOCAL_INTERNATIONAL.values()) {

            if (value.toInt() == idLocInt) {
                garLocInt = value;
            }

        }

        return garLocInt;
    }

    /**
     * Get Id
     *
     * @param name Name
     * @return Id
     * @throws OException {@link OException}
     */
    private int getId(String name) throws OException {
        Table tblId      = Util.NULL_TABLE;
        int   idLocalInt = 0;

        try {
            String sql = "\n SELECT id" //
                + "\n FROM   " + GAR_USER_TABLE_ENUM.USER_LOCAL_INTERNATIONAL.toString() //
                + "\n WHERE  description = '" + name + "'";

            tblId = GarStrict.query(sql, null);

            if (!GarHasAValue.isTableEmpty(tblId)) {
                idLocalInt = tblId.getInt("id", 1);
            }

        } finally {
            GarSafe.destroy(tblId);
        }

        return idLocalInt;
    }
}
