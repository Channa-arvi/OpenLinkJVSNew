package com.garinternal.common.enums;

/*
File Name:                      GAR_STLDOC_DEFINITION_ENUM.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Enum for Stldoc Definition

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
@GarEnumInfo(tableName = "stldoc_definitions", stringColumn = "stldoc_def_name", dBColumns = { "stldoc_def_id", "doc_type" },
    methods = { "toInt", "getStlDocTypeId" })

public enum GAR_STLDOC_DEFINITION_ENUM {
    //@formatter:off
	//                         Name                                           Id        Data Type
	PURCHASE_FINAL_INVOICE    ("Purchase Final Invoice",                      -1,       GAR_STLDOC_DOCUMENT_TYPE_ENUM.INVOICE),
	;
    //@formatter:on

    private String                        name;
    private int                           id;
    private GAR_STLDOC_DOCUMENT_TYPE_ENUM stlDocType;

    GAR_STLDOC_DEFINITION_ENUM(String name, int id, GAR_STLDOC_DOCUMENT_TYPE_ENUM stlDocType) {
        this.name       = name;
        this.id         = id;
        this.stlDocType = stlDocType;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Returns Stl Doc Type Id
     *
     * @return Stl Doc Type Id
     * @throws OException {@link OException}
     */
    public int toInt() throws OException {
        int idLocal = this.id;

        if (idLocal == -1) {
            idLocal = RefBase.getValue(SHM_USR_TABLES_ENUM.STLDOC_DEFINITIONS_TABLE, this.name);
        }

        return idLocal;
    }

    /**
     * Returns Stl Doc Type
     *
     * @return Stl Doc Type
     */
    public GAR_STLDOC_DOCUMENT_TYPE_ENUM getStlDocType() {
        return this.stlDocType;
    }

    /**
     * Returns Stl Doc Type Id
     *
     * @return Returns Stl Doc Type Id
     * @throws OException {@link OException}
     */
    public int getStlDocTypeId() throws OException {
        return this.stlDocType.toInt();
    }
}
